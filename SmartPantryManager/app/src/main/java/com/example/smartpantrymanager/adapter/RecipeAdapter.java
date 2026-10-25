package com.example.smartpantrymanager.adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantrymanager.database.DatabaseHelper;
import com.example.smartpantrymanager.model.Recipe;

import android.provider.ContactsContract;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;

import com.example.smartpantrymanager.R;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.smartpantrymanager.model.RecipeIngredient;

import java.util.List;

import java.util.List;
public class RecipeAdapter
        extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {

    private final Context context;
    private List<Recipe> recipes;
    private final DatabaseHelper databaseHelper;

    public RecipeAdapter(
            Context context,
            List<Recipe> recipes
    ){
        this.context = context;
        this.recipes = recipes;
        this.databaseHelper = new DatabaseHelper(context);
    }

    public static class RecipeViewHolder
            extends RecyclerView.ViewHolder {

        private final ShapeableImageView imageRecipe;
        private final TextView textRecipeName;
        private final TextView textRecipeIngredients;
        private final MaterialButton buttonViewRecipe;


       //The ViewHolder represents one visible recipe card
        public RecipeViewHolder(@NonNull View itemView) {
            super(itemView);

            imageRecipe = itemView.findViewById(
                    R.id.imageRecipe
            );

            textRecipeName = itemView.findViewById(
                    R.id.textRecipeName
            );

            textRecipeIngredients = itemView.findViewById(
                    R.id.textRecipeIngredients
            );

            buttonViewRecipe = itemView.findViewById(
                    R.id.buttonViewRecipe
            );
        }
    }

    //Format the ingredient list for each card
    private String formatIngredients(
            List<RecipeIngredient> ingredients
    ) {
        StringBuilder ingredientText = new StringBuilder();

        for (int index = 0; index < ingredients.size(); index++){
            RecipeIngredient ingredient = ingredients.get(index);

            double quantity = ingredient.getRequiredQuantity();

            if (quantity == Math.floor(quantity)) {
                ingredientText.append((int) quantity);
            } else {
                ingredientText.append(quantity);
            }

            ingredientText
                    .append(" ")
                    .append(ingredient.getUnit())
                    .append(" ")
                    .append(ingredient.getIngredientName());

            if (index < ingredients.size() - 1) {
                ingredientText.append(" • "); //50 grams cheese • 2 items egg • 10 millilitres oil
            }
        }

        return ingredientText.toString();
    }


    //RecyclerView calls onCreateViewHolder() whenever it needs to create a new recipe card
    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {
        View recipeView = LayoutInflater
                .from(parent.getContext())
                .inflate(
                        R.layout.item_recipe,
                        parent,
                        false
                );

        return new RecipeViewHolder(recipeView);
    }




    @Override
    public void onBindViewHolder(
            @NonNull RecipeViewHolder holder,
            int position
    ) {
        Recipe recipe = recipes.get(position); //Retrieves the correct recipe for the cards position

        holder.textRecipeName.setText(
                recipe.getName()
        );

        List<RecipeIngredient> ingredients =
                databaseHelper.getIngredientsForRecipe(
                        recipe.getId()
                );

        holder.textRecipeIngredients.setText(
                formatIngredients(ingredients)
        );

        int imageResourceId = context
                .getResources()
                .getIdentifier(
                        recipe.getImageName(),
                        "drawable",
                        context.getPackageName()
                );

        if (imageResourceId != 0) {
            holder.imageRecipe.setImageResource(
                    imageResourceId
            );
        } else {
            holder.imageRecipe.setImageResource(
                    R.drawable.ic_launcher_foreground
            );
        }

        holder.imageRecipe.setContentDescription(
                recipe.getName() + " meal image"
        );
    }

    @Override
    public int getItemCount() { //tells RecyclerView how many recipe cards it needs to display
        return recipes.size();
    }

    public void updateRecipes(List<Recipe> updatedRecipes) {
        this.recipes = updatedRecipes; //replaces the old suggestion list
        notifyDataSetChanged();  //tells RecyclerView to redraw its cards using the new data.
        //For example, if the user deletes Milk, recipes requiring milk should be able to disappear from the suggestion screen
    }



}
