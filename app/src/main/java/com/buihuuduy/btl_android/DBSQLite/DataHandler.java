package com.buihuuduy.btl_android.DBSQLite;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.buihuuduy.btl_android.entity.UserEntity;

public class DataHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "book_management_db.db";

    private static final String TABLE_USER = "user";

    // SQL query to create the user table
    private static final String CREATE_TABLE_USER =
            "CREATE TABLE " + TABLE_USER + " (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "email VARCHAR(255) UNIQUE, " +
                    "full_name VARCHAR(255), " +
                    "password VARCHAR(255), " +
                    "isAdmin INTEGER" +
                    ");";

    // Constructor
    public DataHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        // Create default accounts when database is created
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(CREATE_TABLE_USER);  // Tạo bảng user
            createDefaultAccounts(db); // Chèn tài khoản mặc định
        } catch (Exception e) {
            Log.e("DataHandler", "Error creating table: " + e.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        onCreate(db);
    }

    // Check if email exists in the database
    public boolean checkEmailExist(SQLiteDatabase db, String email) {
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USER + " WHERE email = ?", new String[]{email});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    // Register a new user in the database
    public boolean registerUser(UserEntity userEntity) {
        SQLiteDatabase db = this.getWritableDatabase();
        // Ensure email does not already exist
        if (checkEmailExist(db, userEntity.getEmail())) {
            return false;  // Email already exists
        }

        ContentValues values = new ContentValues();
        values.put("email", userEntity.getEmail());
        values.put("full_name", userEntity.getFullName());
        values.put("password", userEntity.getPassword());
        values.put("isAdmin", userEntity.getIsAdmin());

        long result = db.insert(TABLE_USER, null, values);
        db.close();
        return result != -1;  // Return true if insert is successful
    }

    // Authenticate user with email and password, return user role (admin/user)
    public int authenticateUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USER + " WHERE email = ? AND password = ?", new String[]{email, password});

        if (cursor.moveToFirst()) {
            @SuppressLint("Range") int isAdmin = cursor.getInt(cursor.getColumnIndex("isAdmin"));
            cursor.close();
            return isAdmin;  // Return 1 if admin, 0 if user
        }

        cursor.close();
        return -1;  // Return -1 if not found
    }

    // Retrieve user details by email
    public UserEntity getUserByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USER + " WHERE email = ?", new String[]{email});

        UserEntity user = null;
        if (cursor.moveToFirst()) {
            @SuppressLint("Range") String fullName = cursor.getString(cursor.getColumnIndex("full_name"));
            @SuppressLint("Range") String password = cursor.getString(cursor.getColumnIndex("password"));
            @SuppressLint("Range") int isAdmin = cursor.getInt(cursor.getColumnIndex("isAdmin"));
            user = new UserEntity(email, fullName, password, isAdmin);
        }

        cursor.close();
        return user;
    }

    // Update user details (e.g., password)
    public boolean updateUser(UserEntity userEntity) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("full_name", userEntity.getFullName());
        values.put("password", userEntity.getPassword());

        int rowsUpdated = db.update(TABLE_USER, values, "email = ?", new String[]{userEntity.getEmail()});
        db.close();

        return rowsUpdated > 0;
    }

    // Insert admin account into the database
    private boolean insertAdmin(SQLiteDatabase db) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("email", "admin@example.com");
        contentValues.put("full_name", "Admin User");
        contentValues.put("password", "admin123");
        contentValues.put("isAdmin", 1); // 1 = admin
        long result = db.insert(TABLE_USER, null, contentValues);
        return result != -1;
    }

    // Insert user account into the database
    private boolean insertUser(SQLiteDatabase db) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("email", "user@example.com");
        contentValues.put("full_name", "Regular User");
        contentValues.put("password", "user123");
        contentValues.put("isAdmin", 0); // 0 = user
        long result = db.insert(TABLE_USER, null, contentValues);
        return result != -1;
    }

    // Create default user and admin account if they don't exist
    private void createDefaultAccounts(SQLiteDatabase db) {
        if (!checkEmailExist(db, "admin@example.com")) {
            insertAdmin(db); // Thêm tài khoản admin nếu chưa tồn tại
        }

        if (!checkEmailExist(db, "user@example.com")) {
            insertUser(db); // Thêm tài khoản user nếu chưa tồn tại
        }
    }
}
