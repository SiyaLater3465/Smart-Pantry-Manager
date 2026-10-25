package com.example.smartpantrymanager;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.widget.TextView;

import com.example.smartpantrymanager.database.DatabaseHelper;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import android.widget.Toast;
import com.example.smartpantrymanager.model.Recipe;
import com.example.smartpantrymanager.model.RecipeIngredient;

import java.util.List;
public class RecipeDetailActivity extends AppCompatActivity {

    private ShapeableImageView imageRecipeDetail;
    private TextView textRecipeDetailName;
    private TextView textRecipeDetailIngredients;
    private TextView textRecipeDetailSteps;
    private DatabaseHelper databaseHelper;
    private int recipeId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_recipe_detail);

        imageRecipeDetail = findViewById(R.id.imageRecipeDetail);

        textRecipeDetailName = findViewById(R.id.textRecipeDetailName);

        textRecipeDetailIngredients = findViewById(R.id.textRecipeDetailIngredients);

        textRecipeDetailSteps = findViewById(R.id.textRecipeDetailSteps);

        MaterialButton buttonBackFromRecipe = findViewById(R.id.buttonBackFromRecipe);

        buttonBackFromRecipe.setOnClickListener(view -> finish());

        databaseHelper = new DatabaseHelper(this);

        recipeId = getIntent().getIntExtra(
                "recipe_id",
                -1
        );

        if (recipeId == -1){
            Toast.makeText(
                    this,
                    "Recipe could not be opened",
                    Toast.LENGTH_LONG
            ).show();

            finish();
            return;
        }

        loadRecipeDetails();


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    //Load the selected recipes main information
    private void loadRecipeDetails(){
        Recipe recipe = databaseHelper.getRecipeById(
                recipeId
        );

        if (recipe == null){
            Toast.makeText(
                    this,
                    "Recipe was not found",
                    Toast.LENGTH_LONG
            ).show();

            finish();
            return;
        }

        textRecipeDetailName.setText(
                recipe.getName()
        );

        textRecipeDetailSteps.setText(
                recipe.getPreparationSteps()
        );

        List<RecipeIngredient> ingredients =
                databaseHelper.getIngredientsForRecipe(recipe.getId());

        textRecipeDetailIngredients.setText(
                formatIngredientList(ingredients)
        );

        int imageResourceId = getResources().getIdentifier(
                recipe.getImageName(),
                "drawable",
                getPackageName()
        );

        if (imageResourceId != 0) {
            imageRecipeDetail.setImageResource(
                    imageResourceId
            );
        } else {
            imageRecipeDetail.setImageResource(
                    R.drawable.ic_launcher_foreground
            );
        }

        imageRecipeDetail.setContentDescription(
                recipe.getName() + " finished meal"
        );
    }

    private String formatIngredientList(
            List<RecipeIngredient> ingredients
    ) {
        StringBuilder formattedList = new StringBuilder();

        for (RecipeIngredient ingredient : ingredients) {
            double quantity = ingredient.getRequiredQuantity();

            formattedList.append("• ");

            if (quantity == Math.floor(quantity)) {
                formattedList.append((int) quantity);
            } else {
                formattedList.append(quantity);
            }

            formattedList
                    .append(" ")
                    .append(ingredient.getUnit())
                    .append(" ")
                    .append(ingredient.getIngredientName())
                    .append("\n");
        }

        return formattedList.toString().trim();
    }
}