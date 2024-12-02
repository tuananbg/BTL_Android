package com.buihuuduy.btl_android.DBSQLite;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;
import com.buihuuduy.btl_android.entity.BookEntity;
import com.buihuuduy.btl_android.entity.UserEntity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DataHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "book_management_db.db";

    private static final String TABLE_USER = "user";
    private static final String TABLE_BOOK = "book";
    private static final String TABLE_CATEGORY = "category";

    // SQL query to create tables
    private static final String CREATE_TABLE_USER =
            "CREATE TABLE " + TABLE_USER + " (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "email VARCHAR(255) UNIQUE, " +
                    "full_name NVARCHAR(255), " +
                    "password VARCHAR(255), " +
                    "isAdmin INTEGER" +
                    ");";

    private static final String CREATE_TABLE_BOOK =
            "create table " + TABLE_BOOK + " (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "name NVARCHAR(255), " +
                    "description TEXT, " +
                    "content TEXT, " +
                    "price INTEGER, " +
                    "status TINYINT(1), " +
                    "user_id INTEGER, " +
                    "category_id INTEGER, " +
                    "created_at DATE, " +
                    "image_path TEXT" +
                    ");";

    private static final String CREATE_TABLE_CATEGORY =
            "create table " + TABLE_CATEGORY + " (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "name NVARCHAR(255)" +
                    ");";

    private static final String INIT_USER =
            "INSERT INTO user (email, full_name, password, isAdmin) VALUES " +
            "('user1', 'User 1', '1234', 0), " +
            "('admin1', 'Admin 1', '1234', 1);";

    private static final String INIT_BOOK_LIST =
            "INSERT INTO book (name, description, price, content, image_path, user_id, category_id) VALUES " +
            "('Toán', 'Sách toán và những công thức bổ ích', 20000, 'Hằng đẳng thức', '/data/data/com.buihuuduy.btl_android/files/1732587321614_cover.jpg', 1, 1), " +
            "('Văn', 'Văn và những câu chuyện cổ tích', 25000, 'Mò kim đáy bể', '/data/user/0/com.buihuuduy.btl_android/files/1732587321614_cover.jpg', 1, 2), " +
            "('Anh', 'Hello World', 0, 'Android Studio', '/data/user/0/com.buihuuduy.btl_android/files/1732587321614_cover.jpg', 1, 3);";

    private static  final  String INIT_CATE =
            "INSERT INTO category (id, name) VALUES " +
                    "(1, 'Tư duy'), " +
                    "(2, 'Sáng tạo'), " +
                    "(3, 'Học thuật');";

    // Constructor
    public DataHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(CREATE_TABLE_USER);
            db.execSQL(CREATE_TABLE_BOOK);
            db.execSQL(CREATE_TABLE_CATEGORY);
            // db.execSQL(INIT_USER);
             db.execSQL(INIT_BOOK_LIST);
             db.execSQL(INIT_CATE);
        } catch (Exception e) {
            Log.e("DataHandler", "Error creating table: " + e.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

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
    public UserEntity getUserByEmail(String email)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USER + " WHERE email = ?", new String[]{email});
        UserEntity user = null;
        if (cursor.moveToFirst()) {
            @SuppressLint("Range") Integer id = Integer.parseInt(cursor.getString(cursor.getColumnIndex("id")));
            @SuppressLint("Range") String fullName = cursor.getString(cursor.getColumnIndex("full_name"));
            @SuppressLint("Range") String password = cursor.getString(cursor.getColumnIndex("password"));
            @SuppressLint("Range") int isAdmin = cursor.getInt(cursor.getColumnIndex("isAdmin"));
            user = new UserEntity(email, fullName, password, isAdmin); user.setId(id);
        }
        cursor.close();
        return user;
    }

    public long shareBook(BookEntity bookEntity) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", bookEntity.getName());
        values.put("description", bookEntity.getDescription());
        values.put("content", bookEntity.getContent());
        values.put("image_path", bookEntity.getImagePath());
        values.put("user_id", bookEntity.getUserId());
        values.put("status", 0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            values.put("created_at", String.valueOf(LocalDate.now()));
        }
        return db.insert(TABLE_BOOK, null, values);
    }

    public Cursor getAllBooksOnHomePage() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT b.name, b.description, b.image_path, u.full_name, b.price " +
                "FROM " + TABLE_BOOK + " b " +
                "JOIN " + TABLE_USER + " u " +
                "ON b.user_id = u.id";
        return db.rawQuery(query, null);
    }
    public Cursor getAllBookOnMyBook() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT b.name, b.description, b.image_path, u.full_name, b.price  " +
                "FROM " + TABLE_BOOK + " b " +
                "JOIN " + TABLE_USER + " u " +
                "ON b.user_id = u.id";
        return db.rawQuery(query, null);
    }
    public Cursor getAllCategories(){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT c.name, c.id " +
                "FROM " + TABLE_CATEGORY + " c ";
        return db.rawQuery(query, null);
    }
    public Cursor getFilteredBooks(String categoryName, List<String> filterType) {
        String query = "SELECT b.*, u.full_name FROM book b " +
                "JOIN category c ON b.category_id = c.id " +
                "JOIN user u ON b.user_id = u.id ";
        // Thêm điều kiện lọc loại tài liệu nếu cần
        if (!filterType.isEmpty() && filterType.size() != 2) {
            if(filterType.get(0).contains("Sale")){
                query += "AND b.price > 0 "; // "type" là cột lưu giá trị "Share" hoặc "Sale"
            }else{
                query += "AND b.price = 0 ";
            }

            if(!categoryName.contains("Tất cả")){
                query += "WHERE c.name = ?";
                return getReadableDatabase().rawQuery(query, new String[]{categoryName});
            }
            return getReadableDatabase().rawQuery(query, null);
        }else{
                if(!categoryName.contains("Tất cả")){
                    query += "WHERE c.name = ?";
                    return getReadableDatabase().rawQuery(query, new String[]{categoryName});
                }
        }

        return getReadableDatabase().rawQuery(query, null);
    }

}