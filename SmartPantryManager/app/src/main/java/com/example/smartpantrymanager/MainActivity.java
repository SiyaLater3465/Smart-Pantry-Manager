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

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerPantryItems;
    private PantryItemAdapter pantryItemAdapter;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        recyclerPantryItems = findViewById(R.id.recyclerPantryItems);

        MaterialButton buttonAddIngredient = findViewById(R.id.buttonAddIngredient);

        databaseHelper = new DatabaseHelper(this);

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
    }
    //onResume runs whenever MainActivity becomes active and visible

    private void loadPantryItems() {
        List<PantryItem> pantryItems =
                databaseHelper.getAllPantryItems(); //The database returns all saved records as a list

        pantryItemAdapter.updateItems(pantryItems);
    }
}