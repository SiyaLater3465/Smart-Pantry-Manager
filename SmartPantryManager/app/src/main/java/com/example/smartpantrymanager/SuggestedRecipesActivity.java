package com.example.smartpantrymanager;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantrymanager.adapter.RecipeAdapter;
import com.example.smartpantrymanager.database.DatabaseHelper;
import com.example.smartpantrymanager.model.Recipe;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;
import android.view.View;

public class SuggestedRecipesActivity extends AppCompatActivity {

    private RecyclerView recyclerSuggestedRecipes;
    private MaterialCardView cardNoRecipes;
    private RecipeAdapter recipeAdapter;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_suggested_recipes);

        MaterialButton buttonBackToPantry = findViewById(R.id.buttonBackToPantry);

        buttonBackToPantry.setOnClickListener(view -> finish());

        recyclerSuggestedRecipes = findViewById(
                R.id.recyclerSuggestedRecipes
        );

        cardNoRecipes = findViewById(
                R.id.cardNoRecipes
        );

        databaseHelper = new DatabaseHelper(this);

        recyclerSuggestedRecipes.setLayoutManager(
                new LinearLayoutManager(this)
        );

        recipeAdapter = new RecipeAdapter(
                this,
                new ArrayList<>()
        );

        recyclerSuggestedRecipes.setAdapter(
                recipeAdapter
        );

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void loadSuggestedRecipes() {
        List<Recipe> suggestedRecipes =
                databaseHelper.getSuggestedRecipes();

        recipeAdapter.updateRecipes(
                suggestedRecipes
        );

        if (suggestedRecipes.isEmpty()) {
            recyclerSuggestedRecipes.setVisibility(View.GONE);
            cardNoRecipes.setVisibility(View.VISIBLE);
        } else {
            recyclerSuggestedRecipes.setVisibility(View.VISIBLE);
            cardNoRecipes.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadSuggestedRecipes();
    }
}