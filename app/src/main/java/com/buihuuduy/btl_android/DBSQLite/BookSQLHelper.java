package com.buihuuduy.btl_android.DBSQLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.buihuuduy.btl_android.entity.BookEntity;

import java.util.ArrayList;
import java.util.List;

public class BookSQLHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "books.db";
    private static final int DATABASE_VERSION = 1;

    // Tên bảng và các cột
    private static final String TABLE_BOOKS = "books";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_AUTHOR = "author";
    private static final String COLUMN_PRICE = "price";
    private static final String COLUMN_IMAGE_URL = "imageUrl";
    private static final String COLUMN_STATUS = "status";

    public BookSQLHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Tạo bảng books
        String createTable = "CREATE TABLE " + TABLE_BOOKS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME + " TEXT, " +
                COLUMN_AUTHOR + " TEXT, " +
                COLUMN_PRICE + " REAL, " +
                COLUMN_STATUS + " INT, " +
                COLUMN_IMAGE_URL + " TEXT)";
        db.execSQL(createTable);
        // Chèn dữ liệu mẫu
        String insertData = "INSERT INTO books (name, author, price, imageUrl, status) VALUES " +
                "('Little Women', 'Louisa May Alcott', 20000, 'https://example.com/image1.jpg', 0)," +
                "('Pride and Prejudice', 'Jane Austen', 25000, 'https://example.com/image2.jpg', 1)," +
                "('Moby Dick', 'Herman Melville', 30000, 'https://example.com/image3.jpg', 2);";
        db.execSQL(insertData);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOKS);
        onCreate(db);
    }

    // Thêm sách mới
    public void addBook(String name, String author, double price, String imageUrl, int status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_AUTHOR, author);
        values.put(COLUMN_PRICE, price);
        values.put(COLUMN_IMAGE_URL, imageUrl);
        values.put(COLUMN_STATUS, status);

        db.insert(TABLE_BOOKS, null, values);
        db.close();
    }

    // Lấy tất cả sách
    public List<BookEntity> getAllBooks() {
        List<BookEntity> bookList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_BOOKS, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                try {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                    String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                    String author = cursor.getString(cursor.getColumnIndexOrThrow("author"));
                    double price = cursor.getDouble(cursor.getColumnIndexOrThrow("price"));
                    String imageUrl = cursor.getString(cursor.getColumnIndexOrThrow("imageUrl"));
                    int status = cursor.getInt(cursor.getColumnIndexOrThrow("status"));
                    bookList.add(new BookEntity(id, name, author, price, imageUrl, status));
                } catch (IllegalArgumentException e) {
                    // Xử lý ngoại lệ nếu cột không tồn tại
                    e.printStackTrace();
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        return bookList;
    }
}
