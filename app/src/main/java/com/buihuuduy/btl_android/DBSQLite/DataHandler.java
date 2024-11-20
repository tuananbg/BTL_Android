package com.buihuuduy.btl_android.DBSQLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.buihuuduy.btl_android.entity.UserEntity;

public class DataHandler extends SQLiteOpenHelper
{
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "book_management_db.db";

    public DataHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    private static final String CREATE_TABLE_USER =
            "CREATE TABLE user (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "email VARCHAR(255), " +
                    "full_name VARCHAR(255), " +
                    "password VARCHAR(255), " +
                    "isAdmin INTEGER" +
                    ");";

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        try {
            sqLiteDatabase.execSQL(CREATE_TABLE_USER);
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Lỗi tạo bảng: " + e.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public boolean checkEmailExist(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM user WHERE email = ?", new String[]{email});
        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        return exists;
    }

    public boolean registerUser(UserEntity userEntity)
    {
        ContentValues values = new ContentValues();
        values.put("email", userEntity.getEmail());
        values.put("full_name", userEntity.getFullName());
        values.put("password", userEntity.getPassword());
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        long result = sqLiteDatabase.insert("user", null, values);
        sqLiteDatabase.close();
        return result != -1;
    }

}
