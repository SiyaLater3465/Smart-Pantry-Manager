package com.example.smartpantrymanager;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.Intent;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantrymanager.adapter.PantryItemAdapter;
import com.example.smartpantrymanager.database.DatabaseHelper;
import com.example.smartpantrymanager.model.PantryItem;


import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

import android.widget.TextView;

import android.view.View;

import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import android.content.SharedPreferences;

import androidx.appcompat.app.AlertDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerPantryItems;
    private PantryItemAdapter pantryItemAdapter;
    private DatabaseHelper databaseHelper;
    private TextView textEmptyPantry;
    private SharedPreferences preferences;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        recyclerPantryItems = findViewById(R.id.recyclerPantryItems);

        textEmptyPantry = findViewById(R.id.textEmptyPantry);

        MaterialButton buttonAddIngredient = findViewById(R.id.buttonAddIngredient);

        MaterialButton buttonSuggestedRecipes = findViewById(R.id.buttonSuggestedRecipes);

        buttonSuggestedRecipes.setOnClickListener(view -> {
            Intent intent = new Intent(
                    MainActivity.this,
                    SuggestedRecipesActivity.class
            );

            startActivity(intent);
        });

        databaseHelper = new DatabaseHelper(this);

        preferences = getSharedPreferences(
                SettingsActivity.PREFERENCES_NAME,
                MODE_PRIVATE
        );

        //A RecyclerView does not automatically, know how to position its items.
        //The LayoutManager knows how to control these cards (by default vertical)
        recyclerPantryItems.setLayoutManager(
                new LinearLayoutManager(this)
        );

        pantryItemAdapter = new PantryItemAdapter(
                new ArrayList<>()
        );

        recyclerPantryItems.setAdapter(pantryItemAdapter); //connects RecyclerView to the adapter

        //RecyclerView provides the scrollable area on the screen
        //PantryItemAdapter receives ingredient objects and creates the appropriate cards
        //item_pantry.xml determines the visual appearance of each card

        pantryItemAdapter.setOnPantryItemActionListener(
                new PantryItemAdapter.OnPantryItemActionListener() {
                    @Override
                    public void onEditClick(PantryItem pantryItem) {
                        //Creating navigation to the same screen currently used to add an ingredient
                        Intent intent = new Intent(
                                MainActivity.this,
                                AddEditIngredientActivity.class
                        );
                        //An Intent can carry additional information between activities
                        intent.putExtra(
                                "item_id",
                                pantryItem.getId()
                        );

                        startActivity(intent);
                    }

                    @Override
                    public void onDeleteClick(PantryItem pantryItem) {
                       showDeleteConfirmation(pantryItem);

                    }
                }
        );


        buttonAddIngredient.setOnClickListener( view -> {
            Intent intent = new Intent(
                    MainActivity.this,
                    AddEditIngredientActivity.class
            );

            startActivity(intent);
        });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPantryItems();
        checkExpiryAlerts(); //When expiry Alerts are enabled : retrieves all pantry ingredients, reads each, checks whether it expires today or next three days
        // displays the matching ingredients in an alert window
    }
    //onResume runs whenever MainActivity becomes active and visible

    private void loadPantryItems() {
        List<PantryItem> pantryItems =
                databaseHelper.getAllPantryItems();//The database returns all saved records as a list

        if(pantryItems.isEmpty()) {
            //When it is empty, the message appear, and the empty RecyclerView is hidden
            textEmptyPantry.setVisibility(View.VISIBLE);
            recyclerPantryItems.setVisibility(View.GONE);
        } else {
            //When ingredients exist, the message disappears, and the ingredient cards are shown
            textEmptyPantry.setVisibility(View.GONE);
            recyclerPantryItems.setVisibility(View.VISIBLE);
        }

        pantryItemAdapter.updateItems(pantryItems);
    }

    private void checkExpiryAlerts() {

        boolean alertsEnabled = preferences.getBoolean(
                SettingsActivity.KEY_EXPIRY_ALERTS,
                false
        );

        //We stop here when the user has disabled expiry alerts.
        if (!alertsEnabled) {
            return;
        }

        List<PantryItem> pantryItems = databaseHelper.getAllPantryItems();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        dateFormat.setLenient(false);

        try {
            //Remove the current time so only the dates are compared.
            Date today = dateFormat.parse(dateFormat.format(new Date()));

            Calendar expiryLimit = Calendar.getInstance();
            expiryLimit.setTime(today);
            expiryLimit.add(Calendar.DAY_OF_YEAR, 3); //Get todays date and then add three days

            StringBuilder expiringItems = new StringBuilder();

            for (PantryItem item : pantryItems) {

                String expiryDateText = item.getExpiryDate();

                if (expiryDateText == null || expiryDateText.trim().isEmpty()) { //Skips items without an expiry date
                    continue;
                }

                Date expiryDate = dateFormat.parse(expiryDateText);

                boolean expiresTodayOrLater = !expiryDate.before(today);
                boolean expiresWithinThreeDays =
                        !expiryDate.after(expiryLimit.getTime());

                if (expiresTodayOrLater && expiresWithinThreeDays) {
                    expiringItems
                            .append("•")
                            .append(item.getName())
                            .append(" - ")
                            .append(expiryDateText)
                            .append("\n");
                }
            }

            if (expiringItems.length() > 0) {
                new AlertDialog.Builder(this)
                        .setTitle("ingredients expiring soon")
                        .setMessage(expiringItems.toString().trim())
                        .setPositiveButton("OK", null)
                        .show();
            }

        }catch (ParseException exception) {
            exception.printStackTrace();
        }
    }

    private void showDeleteConfirmation(PantryItem pantryItem) {
        new AlertDialog.Builder(this)
                .setTitle("Delete ingredient")
                .setMessage(
                        "Are you sure you want to detele " + pantryItem.getName() + "?"
                )
                .setPositiveButton("Delete", (dialog, which) -> {
                    databaseHelper.deletePantryItem(
                            pantryItem.getId()
                    );

                    loadPantryItems();

                    Toast.makeText(
                            MainActivity.this,
                            pantryItem.getName() + " deleted",
                            Toast.LENGTH_SHORT
                    ).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
        }
    }
