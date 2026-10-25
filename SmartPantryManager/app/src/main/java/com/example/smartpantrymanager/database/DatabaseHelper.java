package com.example.smartpantrymanager.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import android.content.ContentValues;
import android.database.Cursor;

import com.example.smartpantrymanager.model.PantryItem;

import java.util.ArrayList;
import java.util.List;

import com.example.smartpantrymanager.model.Recipe;

import com.example.smartpantrymanager.model.RecipeIngredient;

import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {
//PANTRY TABLE
    private static final String DATABASE_NAME = "smart_pantry.db";
    private static final int DATABASE_VERSION = 2; //Increased database version 1 to 2

    public static final String TABLE_PANTRY_ITEMS = "pantry_items";

    public static final String COLUMN_PANTRY_ID = "id";
    public static final String COLUMN_PANTRY_NAME = "name";
    public static final String COLUMN_PANTRY_QUANTITY = "quantity";
    public static final String COLUMN_PANTRY_UNIT = "unit";
    public static final String COLUMN_PANTRY_EXPIRY_DATE = "expiry_date";

    private static final String CREATE_PANTRY_ITEMS_TABLE =
            "CREATE TABLE " + TABLE_PANTRY_ITEMS + " (" +
                    COLUMN_PANTRY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_PANTRY_NAME + " TEXT NOT NULL," +
                    COLUMN_PANTRY_QUANTITY + " REAL NOT NULL, " +
                    COLUMN_PANTRY_UNIT + " TEXT NOT NULL, " +
                    COLUMN_PANTRY_EXPIRY_DATE + " TEXT" +
                    ")";

    //RECIPES TABLE
    private static final String TABLE_RECIPES = "recipes";

    private static final String RECIPE_COLUMN_ID = "id";
    private static final String RECIPE_COLUMN_NAME = "name";
    private static final String RECIPE_COLUMN_STEPS = "preparation_steps";
    private static final String RECIPE_COLUMN_IMAGE = "image_name";

    private static final String CREATE_RECIPES_TABLE =
            "CREATE TABLE " + TABLE_RECIPES + " (" +
                    RECIPE_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    RECIPE_COLUMN_NAME + " TEXT NOT NULL, " +
                    RECIPE_COLUMN_STEPS + " TEXT NOT NULL, " +
                    RECIPE_COLUMN_IMAGE + " TEXT" +
                    ")";

    //INGREDIENTS RECIPE TABLE
    private static final String TABLE_RECIPE_INGREDIENTS = "recipe_ingredients";

    private final static String RECIPE_INGREDIENT_COLUMN_ID = "id";
    private final static String RECIPE_INGREDIENT_COLUMN_RECIPE_ID = "recipe_id";
    private final static String RECIPE_INGREDIENT_COLUMN_NAME = "ingredient_name";
    private final static String RECIPE_INGREDIENT_COLUMN_QUANTITY = "required_quantity";
    private final static String RECIPE_INGREDIENT_COLUMN_UNIT = "unit";

    //CREATE TABLE
    private static final String CREATE_RECIPE_INGREDIENTS_TABLE =
            "CREATE TABLE " + TABLE_RECIPE_INGREDIENTS + " (" +
                    RECIPE_INGREDIENT_COLUMN_ID +
                    " INTEGER PRIMARY KEY AUTOINCREMENT, " +

                    RECIPE_INGREDIENT_COLUMN_RECIPE_ID  +
                    " INTEGER NOT NULL, " +

                    RECIPE_INGREDIENT_COLUMN_NAME +
                    " TEXT NOT NULL, " +

                    RECIPE_INGREDIENT_COLUMN_QUANTITY +
                    " REAL NOT NULL, " +

                    RECIPE_INGREDIENT_COLUMN_UNIT +
                    " TEXT NOT NULL, " +

                    "FOREIGN KEY (" +
                    RECIPE_INGREDIENT_COLUMN_RECIPE_ID +
                    ") REFERENCES " +
                    TABLE_RECIPES +
                    "(" + RECIPE_COLUMN_ID + ") ON DELETE CASCADE" +
                    ")";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }
    //SQLite does not always enforce foreign-key rules automatically.
    //This method activates them whenever the database is opened.
    //It also activates the ON DELETE CASCADE rule. Therefore, if a recipe is deleted, its required
    //ingredients are deteled automatically

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);

        if (!db.isReadOnly()) {
            seedRecipesIfNeeded(db);
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL(CREATE_PANTRY_ITEMS_TABLE);
        db.execSQL(CREATE_RECIPES_TABLE);
        db.execSQL(CREATE_RECIPE_INGREDIENTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2){
            db.execSQL(CREATE_RECIPES_TABLE);
            db.execSQL(CREATE_RECIPE_INGREDIENTS_TABLE);
        }
    }


    private long insertRecipe(
            SQLiteDatabase db,
            String name,
            String preparationSteps,
            String imageName
    ) {
        ContentValues values = new ContentValues();

        values.put(RECIPE_COLUMN_NAME, name);
        values.put(RECIPE_COLUMN_STEPS, preparationSteps);
        values.put(RECIPE_COLUMN_IMAGE, imageName);

        return db.insert(TABLE_RECIPES, null, values);
    }

    private long insertRecipeIngredient(
            SQLiteDatabase db,
            long recipeId, //long lets us pass the newly generated recipe ID directly into this method
            String ingredientName,
            double requiredQuantity,
            String unit
    ){
        ContentValues values = new ContentValues();

        values.put(
                RECIPE_INGREDIENT_COLUMN_RECIPE_ID,
                recipeId
        );

        values.put(
                RECIPE_INGREDIENT_COLUMN_NAME,
                ingredientName
        );

        values.put(
                RECIPE_INGREDIENT_COLUMN_QUANTITY,
                requiredQuantity
        );

        values.put(
                RECIPE_INGREDIENT_COLUMN_UNIT,
                unit
        );

        return db.insert(
                TABLE_RECIPE_INGREDIENTS,
                null,
                values
        );


    }

    private void seedRecipes(SQLiteDatabase db) {

        //Egg omelette
        long omeletteId = insertRecipe(
                db,
                "Cheese Omelette",
                "1. Crack the eggs into a bowl and beat them well.\n" +
                        "2. Heat the oil in a frying pan.\n" +
                        "3. Pour the eggs into the pan.\n" +
                        "4. Add the cheese and cook unitl the eggs are firm.\n" +
                        "5. Fold the omelette and serve.",
                "cheese_omelette"
        );

        if (omeletteId != -1){
            insertRecipeIngredient(
                    db,
                    omeletteId,
                    "egg",
                    2,
                    "items"
            );

            insertRecipeIngredient(
                    db,
                    omeletteId,
                    "cheese",
                    50,
                    "grams"
            );

            insertRecipeIngredient(
                    db,
                    omeletteId,
                    "oil",
                    10,
                    "milliliters"
            );
        }

        //Scrambled Eggs
        long scrambledEggsId = insertRecipe(
                db,
                "Scrambled Eggs",
                "1. Crack the eggs into a bowl.\n" +
                        "2. Add the milk and beat the mixture.\n" +
                        "3. Melt the butter in a frying pan.\n" +
                        "4. Pour in the egg mixture and stir gently.\n" +
                        "5. Cook until the eggs are soft and firm.",
                "scrambled_eggs"
        );

        if (scrambledEggsId != -1) {
            insertRecipeIngredient(
                    db,
                    scrambledEggsId,
                    "egg",
                    2,
                    "items"
            );

            insertRecipeIngredient(
                    db,
                    scrambledEggsId,
                    "milk",
                    30,
                    "millilitres"
            );

            insertRecipeIngredient(
                    db,
                    scrambledEggsId,
                    "butter",
                    10,
                    "grams"
            );
        }

        //Tomato and Cheese Sandwich
        long tomatoSandwichId = insertRecipe(
                db,
                "Tomato and Cheese Sandwich",
                "1. Wash and slice the tomato.\n" +
                        "2. Place the cheese onto one slice of bread.\n" +
                        "3. Add the tomato slices.\n" +
                        "4. Cover with the second slice of bread.\n" +
                        "5. Cut the sandwich and serve.",
                "tomato_cheese_sandwich"
        );

        if (tomatoSandwichId != -1) {
            insertRecipeIngredient(
                    db,
                    tomatoSandwichId,
                    "bread",
                    2,
                    "slices"
            );

            insertRecipeIngredient(
                    db,
                    tomatoSandwichId,
                    "tomato",
                    1,
                    "items"
            );

            insertRecipeIngredient(
                    db,
                    tomatoSandwichId,
                    "cheese",
                    30,
                    "grams"
            );
        }

        //Grilled Cheese Sandwich
        long grilledCheeseId = insertRecipe(
                db,
                "Grilled Cheese Sandwich",
                "1. Spread butter on one side of each bread slice.\n" +
                        "2. Place the cheese between the unbuttered sides.\n" +
                        "3. Heat a frying pan over medium heat.\n" +
                        "4. Cook the sandwich until golden underneath.\n" +
                        "5. Turn it over and cook until the cheese melts.",
                "grilled_cheese"
        );

        if (grilledCheeseId != -1) {
            insertRecipeIngredient(
                    db,
                    grilledCheeseId,
                    "bread",
                    2,
                    "slices"
            );

            insertRecipeIngredient(
                    db,
                    grilledCheeseId,
                    "cheese",
                    50,
                    "grams"
            );

            insertRecipeIngredient(
                    db,
                    grilledCheeseId,
                    "butter",
                    10,
                    "grams"
            );
        }

        //Banana Smoothie
        long bananaSmoothieId = insertRecipe(
                db,
                "Banana Smoothie",
                "1. Peel and slice the banana.\n" +
                        "2. Place the banana in a blender.\n" +
                        "3. Add the milk.\n" +
                        "4. Blend until smooth.\n" +
                        "5. Pour into a glass and serve.",
                "banana_smoothie"
        );

        if (bananaSmoothieId != -1) {
            insertRecipeIngredient(
                    db,
                    bananaSmoothieId,
                    "banana",
                    1,
                    "items"
            );

            insertRecipeIngredient(
                    db,
                    bananaSmoothieId,
                    "milk",
                    250,
                    "millilitres"
            );
        }

        //Tomato Pasta
        long tomatoPastaId = insertRecipe(
                db,
                "Tomato Pasta",
                "1. Cook the pasta in boiling water until tender.\n" +
                        "2. Wash and chop the tomatoes.\n" +
                        "3. Heat the oil in a pan.\n" +
                        "4. Cook the tomatoes until they form a soft sauce.\n" +
                        "5. Add the cooked pasta, mix well and serve.",
                "tomato_pasta"
        );

        if (tomatoPastaId != -1) {
            insertRecipeIngredient(
                    db,
                    tomatoPastaId,
                    "pasta",
                    200,
                    "grams"
            );

            insertRecipeIngredient(
                    db,
                    tomatoPastaId,
                    "tomato",
                    2,
                    "items"
            );

            insertRecipeIngredient(
                    db,
                    tomatoPastaId,
                    "oil",
                    15,
                    "millilitres"
            );
        }

        //Tuna Sandwich
        long tunaSandwichId = insertRecipe(
                db,
                "Tuna Sandwich",
                "1. Place the tuna in a bowl.\n" +
                        "2. Add the mayonnaise and mix well.\n" +
                        "3. Spread the tuna mixture over one bread slice.\n" +
                        "4. Cover it with the second bread slice.\n" +
                        "5. Cut the sandwich and serve.",
                "tuna_sandwich"
        );

        if (tunaSandwichId != -1) {
            insertRecipeIngredient(
                    db,
                    tunaSandwichId,
                    "bread",
                    2,
                    "slices"
            );

            insertRecipeIngredient(
                    db,
                    tunaSandwichId,
                    "tuna",
                    100,
                    "grams"
            );

            insertRecipeIngredient(
                    db,
                    tunaSandwichId,
                    "mayonnaise",
                    20,
                    "grams"
            );
        }

        //Egg Fried Rice
        long eggFriedRiceId = insertRecipe(
                db,
                "Egg Fried Rice",
                "1. Cook the rice and allow it to cool slightly.\n" +
                        "2. Heat the oil in a frying pan.\n" +
                        "3. Crack the egg into the pan and stir it while cooking.\n" +
                        "4. Add the cooked rice.\n" +
                        "5. Stir everything together until heated through.",
                "egg_fried_rice"
        );

        if (eggFriedRiceId != -1) {
            insertRecipeIngredient(
                    db,
                    eggFriedRiceId,
                    "rice",
                    200,
                    "grams"
            );

            insertRecipeIngredient(
                    db,
                    eggFriedRiceId,
                    "egg",
                    1,
                    "items"
            );

            insertRecipeIngredient(
                    db,
                    eggFriedRiceId,
                    "oil",
                    15,
                    "millilitres"
            );
        }

        //Mashed Potato
        long mashedPotatoesId = insertRecipe(
                db,
                "Mashed Potatoes",
                "1. Peel and cut the potatoes into pieces.\n" +
                        "2. Boil the potatoes until soft.\n" +
                        "3. Drain the water.\n" +
                        "4. Add the butter and milk.\n" +
                        "5. Mash until smooth and serve.",
                "mashed_potatoes"
        );

        if (mashedPotatoesId != -1) {
            insertRecipeIngredient(
                    db,
                    mashedPotatoesId,
                    "potato",
                    3,
                    "items"
            );

            insertRecipeIngredient(
                    db,
                    mashedPotatoesId,
                    "butter",
                    20,
                    "grams"
            );

            insertRecipeIngredient(
                    db,
                    mashedPotatoesId,
                    "milk",
                    50,
                    "millilitres"
            );
        }

        //Chicken and Rice
        long chickenRiceId = insertRecipe(
                db,
                "Chicken and Rice",
                "1. Cut the chicken into small pieces.\n" +
                        "2. Heat the oil in a pan.\n" +
                        "3. Cook the chicken until completely cooked through.\n" +
                        "4. Cook the rice separately until tender.\n" +
                        "5. Combine the chicken and rice, then serve.",
                "chicken_and_rice"
        );

        if (chickenRiceId != -1) {
            insertRecipeIngredient(
                    db,
                    chickenRiceId,
                    "chicken",
                    200,
                    "grams"
            );

            insertRecipeIngredient(
                    db,
                    chickenRiceId,
                    "rice",
                    150,
                    "grams"
            );

            insertRecipeIngredient(
                    db,
                    chickenRiceId,
                    "oil",
                    15,
                    "millilitres"
            );
        }

        //Fruit Salad
        long fruitSaladId = insertRecipe(
                db,
                "Fruit Salad",
                "1. Wash all the fruit.\n" +
                        "2. Peel the banana and orange.\n" +
                        "3. Cut the apple, banana and orange into pieces.\n" +
                        "4. Place all the fruit in a bowl.\n" +
                        "5. Mix gently and serve.",
                "fruit_salad"
        );

        if (fruitSaladId != -1) {
            insertRecipeIngredient(
                    db,
                    fruitSaladId,
                    "apple",
                    1,
                    "items"
            );

            insertRecipeIngredient(
                    db,
                    fruitSaladId,
                    "banana",
                    1,
                    "items"
            );

            insertRecipeIngredient(
                    db,
                    fruitSaladId,
                    "orange",
                    1,
                    "items"
            );
        }

        //Pancakes
        long pancakesId = insertRecipe(
                db,
                "Pancakes",
                "1. Place the flour and sugar in a bowl.\n" +
                        "2. Add the egg and milk.\n" +
                        "3. Mix until the batter is smooth.\n" +
                        "4. Pour small portions into a lightly heated pan.\n" +
                        "5. Cook each pancake on both sides until golden.",
                "pancakes"
        );

        if (pancakesId != -1) {
            insertRecipeIngredient(
                    db,
                    pancakesId,
                    "flour",
                    150,
                    "grams"
            );

            insertRecipeIngredient(
                    db,
                    pancakesId,
                    "milk",
                    200,
                    "millilitres"
            );

            insertRecipeIngredient(
                    db,
                    pancakesId,
                    "egg",
                    1,
                    "items"
            );

            insertRecipeIngredient(
                    db,
                    pancakesId,
                    "sugar",
                    20,
                    "grams"
            );
        }

        //French Toast
        long frenchToastId = insertRecipe(
                db,
                "French Toast",
                "1. Crack the egg into a bowl.\n" +
                        "2. Add the milk and beat the mixture.\n" +
                        "3. Dip each bread slice into the egg mixture.\n" +
                        "4. Melt the butter in a frying pan.\n" +
                        "5. Cook the bread on both sides until golden.",
                "french_toast"
        );

        if (frenchToastId != -1) {
            insertRecipeIngredient(
                    db,
                    frenchToastId,
                    "bread",
                    2,
                    "slices"
            );

            insertRecipeIngredient(
                    db,
                    frenchToastId,
                    "egg",
                    1,
                    "items"
            );

            insertRecipeIngredient(
                    db,
                    frenchToastId,
                    "milk",
                    50,
                    "millilitres"
            );

            insertRecipeIngredient(
                    db,
                    frenchToastId,
                    "butter",
                    10,
                    "grams"
            );
        }

        //Cheesy Baked Potatoes
        long cheesyPotatoesId = insertRecipe(
                db,
                "Cheesy Baked Potatoes",
                "1. Wash the potatoes thoroughly.\n" +
                        "2. Lightly coat them with oil.\n" +
                        "3. Bake until the potatoes are soft.\n" +
                        "4. Cut each potato open.\n" +
                        "5. Add the cheese and return them to the oven until melted.",
                "cheesy_baked_potatoes"
        );

        if (cheesyPotatoesId != -1) {
            insertRecipeIngredient(
                    db,
                    cheesyPotatoesId,
                    "potato",
                    2,
                    "items"
            );

            insertRecipeIngredient(
                    db,
                    cheesyPotatoesId,
                    "cheese",
                    50,
                    "grams"
            );

            insertRecipeIngredient(
                    db,
                    cheesyPotatoesId,
                    "oil",
                    10,
                    "millilitres"
            );
        }

        //Vegetable Soup
        long vegetableSoupId = insertRecipe(
                db,
                "Vegetable Soup",
                "1. Wash, peel and chop the vegetables.\n" +
                        "2. Place the vegetables in a pot.\n" +
                        "3. Add the water and bring it to a boil.\n" +
                        "4. Reduce the heat and simmer until the vegetables are soft.\n" +
                        "5. Stir well and serve warm.",
                "vegetable_soup"
        );

        if (vegetableSoupId != -1) {
            insertRecipeIngredient(
                    db,
                    vegetableSoupId,
                    "potato",
                    2,
                    "items"
            );

            insertRecipeIngredient(
                    db,
                    vegetableSoupId,
                    "carrot",
                    2,
                    "items"
            );

            insertRecipeIngredient(
                    db,
                    vegetableSoupId,
                    "tomato",
                    2,
                    "items"
            );

            insertRecipeIngredient(
                    db,
                    vegetableSoupId,
                    "water",
                    500,
                    "millilitres"
            );
        }

        //Chicken Salad
        long chickenSaladId = insertRecipe(
                db,
                "Chicken Salad",
                "1. Cook the chicken completely and allow it to cool.\n" +
                        "2. Wash and chop the lettuce.\n" +
                        "3. Wash and slice the tomato.\n" +
                        "4. Cut the chicken into small pieces.\n" +
                        "5. Combine everything in a bowl and serve.",
                "chicken_salad"
        );

        if (chickenSaladId != -1) {
            insertRecipeIngredient(
                    db,
                    chickenSaladId,
                    "chicken",
                    200,
                    "grams"
            );

            insertRecipeIngredient(
                    db,
                    chickenSaladId,
                    "lettuce",
                    100,
                    "grams"
            );

            insertRecipeIngredient(
                    db,
                    chickenSaladId,
                    "tomato",
                    1,
                    "items"
            );
        }

        //
    }

    //Check whether recipes already exist
    //We need to prevent the same 15-20 recipes from being inserted every time the app opens.
    private void seedRecipesIfNeeded(SQLiteDatabase db){
        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM " + TABLE_RECIPES,
                null
        );

        int recipeCount = 0;

        if (cursor.moveToFirst()) {
            recipeCount = cursor.getInt(0);
        }

        cursor.close();

        if (recipeCount == 0) {
            seedRecipes(db);
        }
    }

    public List<Recipe> getAllRecipes() {
        List<Recipe> recipes = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_RECIPES,
                null,
                null,
                null,
                null,
                null,
                RECIPE_COLUMN_NAME + " ASC"
        );

        while (cursor.moveToNext()) {
            int id = cursor.getInt(
                    cursor.getColumnIndexOrThrow(RECIPE_COLUMN_ID)
            );

            String name = cursor.getString(
                    cursor.getColumnIndexOrThrow(RECIPE_COLUMN_NAME)
            );

            String preparationSteps = cursor.getString(
                    cursor.getColumnIndexOrThrow(RECIPE_COLUMN_STEPS)
            );

            String imageName = cursor.getString(
                    cursor.getColumnIndexOrThrow(RECIPE_COLUMN_IMAGE)
            );

            Recipe recipe = new Recipe(
                    id,
                    name,
                    preparationSteps,
                    imageName
            );

            recipes.add(recipe);
        }

        cursor.close();

        return recipes;
    }

    //Retrieving on recipe by its ID
    public Recipe getRecipeById(int recipeId) {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_RECIPES,
                null,
                RECIPE_COLUMN_ID + " = ?",
                new String[]{String.valueOf(recipeId)},
                null,
                null,
                null
        );

        Recipe recipe = null; //prevent from the app crashing

        if (cursor.moveToFirst()) {
            int id = cursor.getInt(
                    cursor.getColumnIndexOrThrow(
                            RECIPE_COLUMN_ID
                    )
            );

            String name = cursor.getString(
                    cursor.getColumnIndexOrThrow(
                            RECIPE_COLUMN_NAME
                    )
            );

            String preparationSteps = cursor.getString(
                    cursor.getColumnIndexOrThrow(
                            RECIPE_COLUMN_STEPS
                    )
            );

             String imageName = cursor.getString(
                     cursor.getColumnIndexOrThrow(
                             RECIPE_COLUMN_IMAGE
                     )
             );

             recipe = new Recipe(
                     id,
                     name,
                     preparationSteps,
                     imageName
             );
        }

        cursor.close();

        return recipe;

    }

    public List<RecipeIngredient> getIngredientsForRecipe(int recipeId) {
        List<RecipeIngredient> ingredients = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_RECIPE_INGREDIENTS,
                null,
                RECIPE_INGREDIENT_COLUMN_RECIPE_ID + " = ?",
                new String[]{String.valueOf(recipeId)},
                null,
                null,
                RECIPE_INGREDIENT_COLUMN_NAME + " ASC"
        );

        while (cursor.moveToNext()) {
            int id = cursor.getInt(
                    cursor.getColumnIndexOrThrow(
                            RECIPE_INGREDIENT_COLUMN_ID
                    )
            );

            int storedRecipeId = cursor.getInt(
                    cursor.getColumnIndexOrThrow(
                            RECIPE_INGREDIENT_COLUMN_RECIPE_ID
                    )
            );

            String ingredientName = cursor.getString(
                    cursor.getColumnIndexOrThrow(
                            RECIPE_INGREDIENT_COLUMN_NAME
                    )
            );

            double requiredQuantity = cursor.getDouble(
                    cursor.getColumnIndexOrThrow(
                            RECIPE_INGREDIENT_COLUMN_QUANTITY
                    )
            );

            String unit = cursor.getString(
                    cursor.getColumnIndexOrThrow(
                            RECIPE_INGREDIENT_COLUMN_UNIT
                    )
            );

            RecipeIngredient ingredient = new RecipeIngredient(
                    id,
                    storedRecipeId,
                    ingredientName,
                    requiredQuantity,
                    unit
            );

            ingredients.add(ingredient);
        }

        cursor.close();

        return ingredients;
    }

    //Normalizing ingredient names
    private String normalizeIngredientName(String ingredientName) {
        if (ingredientName == null) {
            return "";
        }

        String normalizedName = ingredientName
                .toLowerCase(Locale.ROOT)
                .trim()
                .replaceAll("\\s+", " "); //replaces several spaces with one "chicken   breast" -> "chicken breast"

        switch (normalizedName){
            case "eggs":
                return "egg";

            case "tomatoes":
                return "tomato";

            case "potatoes":
                return "potato";

            case "bananas":
                return "banana";

            case "apples":
                return "apple";


            case "oranges":
                return "orange";

            case "carrots":
                return "carrot";

            case "chicken breast":
            case "chicken breasts":
                return "chicken";

            case "lettuce leaves":
                return "lettuce";

            case "bread slices":
                return "bread";

            default:
                return normalizedName;

        }
    }

    //Normalizing Units
    private String normalizeUnit(String unit) {
        if (unit == null) {
            return "";
        }

        String normalizedUnit = unit
                .toLowerCase(Locale.ROOT)
                .trim();

        switch (normalizedUnit) {
            case "g":
            case "gram":
            case "grams":
                return "grams";

            case "kg":
            case "kilogram":
            case "kilograms":
                return "kilograms";

            case "ml":
            case "milliliter":
            case "milliliters":
            case "millilitre":
            case "millilitres":
                return "millilitres";

            case "l":
            case "liter":
            case "liters":
            case "litre":
            case "litres":
                return "litres";

            case "item":
            case "items":
            case "piece":
            case "pieces":
                return "items";

            default:
                return normalizedUnit;
        }
    }

    private double convertToBaseQuantity(
            double quantity,
            String unit
    ){
        String normalizedUnit = normalizeUnit(unit);

        switch (normalizedUnit){
            case "kilograms":
                return quantity * 1000;

            case "litres":
                return quantity * 1000;

            default:
                return quantity;
        }

    }

    private String getBaseUnit(String unit){
        String normalizedUnit = normalizeUnit(unit);

        switch (normalizedUnit) {
            case "kilograms":
            case "grams":
                return "grams";

            case "litres":
            case "millilitres":
                return "millilitres";

            default:
                return normalizedUnit;

        }
    }

    //Checking whether the pantry has enough of one ingredient
    private boolean hasEnoughIngredient(
            List<PantryItem> pantryItems,
            RecipeIngredient requiredIngredient
    ) {
        String requiredName = normalizeIngredientName(
                requiredIngredient.getIngredientName()
        );

        String requiredBaseUnit = getBaseUnit(
                requiredIngredient.getUnit()
        );

        double requiredQuantity = convertToBaseQuantity(
                requiredIngredient.getRequiredQuantity(),
                requiredIngredient.getUnit()
        );

        double totalAvailabilityQuantity = 0;

        for (PantryItem pantryItem : pantryItems) {
            String pantryName = normalizeIngredientName(
                    pantryItem.getName()
            );

            String pantryBaseUnit = getBaseUnit(
                    pantryItem.getUnit()
            );

            if (pantryName.equals(requiredName)
                      && pantryBaseUnit.equals(requiredBaseUnit)) {

                totalAvailabilityQuantity += convertToBaseQuantity(
                        pantryItem.getQuantity(),
                        pantryItem.getUnit()
                );
            }
        }

        return totalAvailabilityQuantity >= requiredQuantity;
    }

    //Strict matching, check whether an entire recipe can be made
    private boolean canMakeRecipe(
            Recipe recipe,
            List<PantryItem> pantryItems
    ){
        List<RecipeIngredient> requiredIngredients =
                getIngredientsForRecipe(recipe.getId()); //Retrieves every ingredient required by the selected recipe

        if (requiredIngredients.isEmpty()) {
            return false;
        }

        for (RecipeIngredient requiredIngredient : requiredIngredients) {
            boolean ingredientAvailable = hasEnoughIngredient(
                    pantryItems,
                    requiredIngredient
            );

            if (!ingredientAvailable){
                return false;
            }
        }

        return true;
    }

    //Return only the recipes the user can make
    public List<Recipe> getSuggestedRecipes() {
        List<PantryItem> pantryItems = getAllPantryItems(); //everything stored in the users pantry
        List<Recipe> allRecipes = getAllRecipes(); //Retrieves everything that has been stored in seeded recipes

        List<Recipe> suggestedRecipes = new ArrayList<>();

        for (Recipe recipe : allRecipes) { //the loop tests each recipe individually
            if (canMakeRecipe(recipe, pantryItems)) { //Only recipes that return true are added to the suggested list
                suggestedRecipes.add(recipe);
            }
        }

        return suggestedRecipes;
    }
    public long addPantryItem(PantryItem pantryItem) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_PANTRY_NAME, pantryItem.getName());
        values.put(COLUMN_PANTRY_QUANTITY, pantryItem.getQuantity());
        values.put(COLUMN_PANTRY_UNIT, pantryItem.getUnit());

        if (pantryItem.getExpiryDate() == null ||
              pantryItem.getExpiryDate().trim().isEmpty()) {
            values.putNull(COLUMN_PANTRY_EXPIRY_DATE);
        }else {
            values.put(COLUMN_PANTRY_EXPIRY_DATE,
                    pantryItem.getExpiryDate());
        }

        return db.insert(TABLE_PANTRY_ITEMS, null, values);
    }

    public List<PantryItem> getAllPantryItems() {
        List<PantryItem> pantryItems = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        String orderBy = COLUMN_PANTRY_NAME + " ASC";

        try (Cursor cursor = db.query(
                TABLE_PANTRY_ITEMS,
                null,
                null,
                null,
                null,
                null,
                orderBy
        )) {
            int idIndex = cursor.getColumnIndexOrThrow(COLUMN_PANTRY_ID);
            int nameIndex = cursor.getColumnIndexOrThrow(COLUMN_PANTRY_NAME);
            int quantityIndex =
                    cursor.getColumnIndexOrThrow(COLUMN_PANTRY_QUANTITY);
            int unitIndex = cursor.getColumnIndexOrThrow(COLUMN_PANTRY_UNIT);
            int expiryIndex =
                    cursor.getColumnIndexOrThrow(COLUMN_PANTRY_EXPIRY_DATE);

            while (cursor.moveToNext()) {
                int id = cursor.getInt(idIndex);
                String name = cursor.getString(nameIndex);
                double quantity = cursor.getDouble(quantityIndex);
                String unit = cursor.getString(unitIndex);

                String expiryDate = null;

                if (!cursor.isNull(expiryIndex)) {
                    expiryDate = cursor.getString(expiryIndex);
                }

                PantryItem pantryItem = new PantryItem(
                        id,
                        name,
                        quantity,
                        unit,
                        expiryDate
                );

                pantryItems.add(pantryItem);
            }
        }

        return pantryItems;

    }

    public PantryItem getPantryItemById(int id) {
        SQLiteDatabase db = getReadableDatabase();

        String selection = COLUMN_PANTRY_ID + " = ?";
        String[] selectionArguments = {
                String.valueOf(id)
        };

        try (Cursor cursor = db.query(
                TABLE_PANTRY_ITEMS,
                null,
                selection,
                selectionArguments,
                null,
                null,
                null
        )) {
            if (cursor.moveToFirst()){
                int nameIndex =
                        cursor.getColumnIndexOrThrow(COLUMN_PANTRY_NAME);
                int quantityIndex =
                        cursor.getColumnIndexOrThrow(COLUMN_PANTRY_QUANTITY);
                int unitIndex =
                        cursor.getColumnIndexOrThrow(COLUMN_PANTRY_UNIT);
                int expiryIndex =
                        cursor.getColumnIndexOrThrow(COLUMN_PANTRY_EXPIRY_DATE);

                String expiryDate = null;

                if (!cursor.isNull(expiryIndex)){
                    expiryDate= cursor.getString(expiryIndex);
                }

                return new PantryItem(
                        id,
                        cursor.getString(nameIndex),
                        cursor.getDouble(quantityIndex),
                        cursor.getString(unitIndex),
                        expiryDate
                );

            }
        }

        return null;
    }

    public int updatePantryItem(PantryItem pantryItem) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_PANTRY_NAME, pantryItem.getName());
        values.put(COLUMN_PANTRY_QUANTITY, pantryItem.getQuantity());
        values.put(COLUMN_PANTRY_UNIT, pantryItem.getUnit());

        if (pantryItem.getExpiryDate() == null ||
               pantryItem.getExpiryDate().trim().isEmpty()) {
            values.putNull(COLUMN_PANTRY_EXPIRY_DATE);
        }else {
            values.put(COLUMN_PANTRY_EXPIRY_DATE,
                    pantryItem.getExpiryDate());
        }

        String whereClause =  COLUMN_PANTRY_ID + " = ?";
        String[] whereArguments = {
                String.valueOf(pantryItem.getId())
        };

        return db.update(
                TABLE_PANTRY_ITEMS,
                values,
                whereClause,
                whereArguments);
    }

    public int deletePantryItem(int id) {
        SQLiteDatabase db = getWritableDatabase();

        String whereClause = COLUMN_PANTRY_ID + " = ?";
        String[] whereArguments = {
                String.valueOf(id)
        };

        return db.delete(
                TABLE_PANTRY_ITEMS,
                whereClause,
                whereArguments
        );
    }




}
