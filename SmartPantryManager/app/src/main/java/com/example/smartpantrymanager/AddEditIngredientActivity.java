package com.example.smartpantrymanager;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import android.widget.Toast;

import com.example.smartpantrymanager.model.PantryItem;
import com.google.android.material.textfield.TextInputLayout;


import android.app.DatePickerDialog;

import java.util.Calendar;
import java.util.Locale;



import com.example.smartpantrymanager.database.DatabaseHelper;

public class AddEditIngredientActivity extends AppCompatActivity {

    private TextInputEditText editIngredientName;
    private TextInputEditText editQuantity;
    private TextInputEditText editExpiryDate;

    private AutoCompleteTextView dropdownUnit;

    private MaterialButton buttonSaveIngredient;
    private MaterialButton buttonCancel;

    private TextView textBack;
    private TextView textScreenTitle;

    private DatabaseHelper databaseHelper;

    private TextInputLayout layoutIngredientName;
    private TextInputLayout layoutQuantity;
    private TextInputLayout layoutUnit;
    private int itemId = -1;
    private boolean isEditMode = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_edit_ingredient);

        editIngredientName = findViewById(R.id.editIngredientName);
        editQuantity = findViewById(R.id.editQuantity);
        editExpiryDate = findViewById(R.id.editExpiryDate);

        dropdownUnit = findViewById(R.id.dropdownUnit);

        buttonSaveIngredient = findViewById(R.id.buttonSaveIngredient);
        buttonCancel = findViewById(R.id.buttonCancel);

        textBack = findViewById(R.id.textBack);
        textScreenTitle = findViewById(R.id.textScreenTitle);

        layoutIngredientName = findViewById(R.id.layoutIngredientName);
        layoutQuantity = findViewById(R.id.layoutQuantity);
        layoutUnit = findViewById(R.id.layoutUnit);



        databaseHelper = new DatabaseHelper(this);

        itemId = getIntent().getIntExtra( //This line reads the value sent by MainActivity
                "item_id",
                -1
        );

        if (itemId != -1) {
            isEditMode = true;
        }

        String[] units = {
                "Items",
                "Grams",
                "Kilograms",
                "Milliliters",
                "Liters",
                "Cups",
                "Tablespoons",
                "Teaspoons"
        };

        ArrayAdapter<String> unitAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                units
        );

        dropdownUnit.setAdapter(unitAdapter);

        textBack.setOnClickListener(view -> finish());

        buttonCancel.setOnClickListener(view -> finish());

        editExpiryDate.setOnClickListener(view -> showDatePicker());

        buttonSaveIngredient.setOnClickListener(view -> saveIngredient());// when user presses save ingredient, Android runs the saveIngriedient() method

        if (isEditMode) {
            loadIngredientForEditing();
        }


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();

        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH);
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (datePicker, selectedYear, selectedMonth, selectedDay) -> {

                    String formmatedDate = String.format(
                            Locale.getDefault(),
                            "%04d-%02d-%02d",
                            selectedYear,
                            selectedMonth + 1,
                            selectedDay
                    );

                    editExpiryDate.setText(formmatedDate);


                },
                currentYear,
                currentMonth,
                currentDay

        );

        datePickerDialog.show();

    }

    private void saveIngredient(){
        String name = editIngredientName.getText().toString().trim();
        String quantityText = editQuantity.getText().toString().trim();
        String unit = dropdownUnit.getText().toString().trim();
        String expiryDate = editExpiryDate.getText().toString().trim();

        //Removing any previous error messages
        layoutIngredientName.setError(null);
        layoutQuantity.setError(null);
        layoutUnit.setError(null);

        boolean isValid = true;
        double quantity = 0;

        //Checking the ingredient name
        if (name.isEmpty()){
            layoutIngredientName.setError("Ingredient name is required for this field");
            isValid = false;
        }else {
            try{
                quantity = Double.parseDouble(quantityText);

                if (quantity <= 0) {
                    layoutQuantity.setError("Quantity must be greater than zero");
                    isValid = false;
                }
            }catch (NumberFormatException exception) {
                layoutQuantity.setError("Enter a valid quantity");
                isValid = false;
            }
        }

        //Checking the unit
        if (unit.isEmpty()) {
            layoutUnit.setError("Please enter a unit");
            isValid = false;
        }

        //Stop here is anything is invalid
        if (!isValid) {
            return;
        }

        PantryItem pantryItem = new PantryItem(
                name,
                quantity,
                unit,
                expiryDate
        );

        boolean operationSuccessful;

        if (isEditMode) {
            pantryItem.setId(itemId); //Important because the update query uses this ID to find the correct row

            int updatedRows =
                    databaseHelper.updatePantryItem(pantryItem);

            operationSuccessful = updatedRows > 0;
        }else {
            long newItemId =
                    databaseHelper.addPantryItem(pantryItem);

            operationSuccessful = newItemId != -1;
        }

        //Choosing on whether you are editing or saving
        if (operationSuccessful) {
            String message = isEditMode
                    ? "Ingredient updated successfully"
                    : "Ingredient added successfully";

            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            setResult(RESULT_OK);
            finish();
        }else {
            String message = isEditMode
                    ? "Ingredient could not be updated "
                    : "Ingredient could not be saved";

            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        }
    }

    private void loadIngredientForEditing() {
        PantryItem pantryItem =
                databaseHelper.getPantryItemById(itemId);

        if (pantryItem == null) {
            Toast.makeText(
                    this,
                    "Ingredient could not be found",
                    Toast.LENGTH_SHORT
            ).show();

            finish();
            return;
        }

        editIngredientName.setText(
                pantryItem.getName()
        );

        editQuantity.setText(
                String.valueOf(pantryItem.getQuantity())
        );

        dropdownUnit.setText(
                pantryItem.getUnit(),
                false
        );

        editExpiryDate.setText(
                pantryItem.getExpiryDate()
        );

        textScreenTitle.setText("Edit Ingredient");
        buttonSaveIngredient.setText("Update Ingredient");
    }
}