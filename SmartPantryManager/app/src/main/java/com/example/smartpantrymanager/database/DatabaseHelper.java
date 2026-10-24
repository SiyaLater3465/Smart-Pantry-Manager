package com.example.smartpantrymanager.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import android.content.ContentValues;
import android.database.Cursor;

import com.example.smartpantrymanager.model.PantryItem;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "smart_pantry.db";
    private static final int DATABASE_VERSION = 1;

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

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL(CREATE_PANTRY_ITEMS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PANTRY_ITEMS);
        onCreate(db);
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

    public PantryItem getPantryItem(int id) {
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
