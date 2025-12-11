package com.example.savebite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "SaveBite.db";
    private static final int DATABASE_VERSION = 3;

    private static final String TABLE_PANTRY = "pantry_items";
    private static final String TABLE_USERS = "users";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createPantry = "CREATE TABLE " + TABLE_PANTRY + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT, " +
                "category TEXT, " +
                "quantity TEXT, " +
                "expiry_date TEXT, " +
                "is_consumed INTEGER DEFAULT 0)";
        db.execSQL(createPantry);

        String createUsers = "CREATE TABLE " + TABLE_USERS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "username TEXT, " +
                "email TEXT UNIQUE, " +
                "password TEXT)";
        db.execSQL(createUsers);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 3) {
            try {
                db.execSQL("ALTER TABLE " + TABLE_PANTRY + " ADD COLUMN is_consumed INTEGER DEFAULT 0");
            } catch (Exception e) {}
        }
    }

    // ✅ 修复点：添加了 getAvailableItems 方法
    // 这个方法只查询 is_consumed = 0 (未消耗) 的食材
    public List<PantryItem> getAvailableItems() {
        List<PantryItem> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_PANTRY + " WHERE is_consumed = 0 ORDER BY expiry_date ASC", null);

        if (cursor.moveToFirst()) {
            do {
                int idIndex = cursor.getColumnIndex("id");
                int nameIndex = cursor.getColumnIndex("name");
                int catIndex = cursor.getColumnIndex("category");
                int qtyIndex = cursor.getColumnIndex("quantity");
                int expIndex = cursor.getColumnIndex("expiry_date");
                int consIndex = cursor.getColumnIndex("is_consumed");

                if (idIndex != -1) {
                    int consumed = (consIndex != -1) ? cursor.getInt(consIndex) : 0;
                    list.add(new PantryItem(
                            cursor.getInt(idIndex),
                            cursor.getString(nameIndex),
                            cursor.getString(catIndex),
                            cursor.getString(qtyIndex),
                            cursor.getString(expIndex),
                            consumed
                    ));
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public List<PantryItem> getAllItems() {
        List<PantryItem> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_PANTRY + " ORDER BY is_consumed ASC, expiry_date ASC", null);

        if (cursor.moveToFirst()) {
            do {
                int idIndex = cursor.getColumnIndex("id");
                int nameIndex = cursor.getColumnIndex("name");
                int catIndex = cursor.getColumnIndex("category");
                int qtyIndex = cursor.getColumnIndex("quantity");
                int expIndex = cursor.getColumnIndex("expiry_date");
                int consIndex = cursor.getColumnIndex("is_consumed");

                if (idIndex != -1) {
                    int consumed = (consIndex != -1) ? cursor.getInt(consIndex) : 0;
                    list.add(new PantryItem(
                            cursor.getInt(idIndex),
                            cursor.getString(nameIndex),
                            cursor.getString(catIndex),
                            cursor.getString(qtyIndex),
                            cursor.getString(expIndex),
                            consumed
                    ));
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public void updateItemStatus(int id, int isConsumed) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("is_consumed", isConsumed);
        db.update(TABLE_PANTRY, cv, "id=?", new String[]{String.valueOf(id)});
    }

    // 以下是其他必要方法，确保保留
    public boolean registerUser(String username, String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("username", username);
        contentValues.put("email", email);
        contentValues.put("password", password);
        long result = db.insert(TABLE_USERS, null, contentValues);
        return result != -1;
    }

    public boolean checkUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE email = ? AND password = ?", new String[]{email, password});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public String getUsername(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT username FROM " + TABLE_USERS + " WHERE email = ?", new String[]{email});
        if (cursor.moveToFirst()) {
            String name = cursor.getString(0);
            cursor.close();
            return name;
        }
        cursor.close();
        return "User";
    }

    public void addItem(String name, String category, String quantity, String expiry) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("name", name);
        cv.put("category", category);
        cv.put("quantity", quantity);
        cv.put("expiry_date", expiry);
        cv.put("is_consumed", 0);
        db.insert(TABLE_PANTRY, null, cv);
    }

    public void deleteItem(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PANTRY, "id=?", new String[]{String.valueOf(id)});
    }
}