package com.buihuuduy.btl_android.DBSQLite;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;
import com.buihuuduy.btl_android.entity.BookEntity;
import com.buihuuduy.btl_android.entity.CategoryEntity;
import com.buihuuduy.btl_android.entity.UserEntity;
import com.github.mikephil.charting.data.BarEntry;
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

    // status { 0: waiting,  1: approved, 2: reject }
    private static final String CREATE_TABLE_BOOK =
            "create table " + TABLE_BOOK + " (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "name NVARCHAR(255), " +
                    "description TEXT, " +
                    "content TEXT, " +
                    "price INTEGER default 0, " +
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
            "('user1', 'First User ', '1234', 0), " +
            "('admin1', 'Admin', '1234', 1);";

    private static final String INIT_BOOK_LIST =
            "INSERT INTO book (name, description, price, status, content, image_path, user_id, created_at) VALUES " +
            "('Sách Toán', 'Sách toán và những công thức bổ ích', 20000, 1, 'Hằng đẳng thức', '/data/data/com.buihuuduy.btl_android/files/1733331439563_cover.jpg', 1, '2024-12-03'), " +
                    "('Sách Toán', 'Sách toán và những công thức bổ ích', 20000, 1, 'Hằng đẳng thức', '/data/data/com.buihuuduy.btl_android/files/1733331439563_cover.jpg', 1, '2024-12-03'), " +
                    "('Sách Toán', 'Sách toán và những công thức bổ ích', 20000, 1, 'Hằng đẳng thức', '/data/data/com.buihuuduy.btl_android/files/1733331439563_cover.jpg', 1, '2024-12-03'), " +
                    "('Sách Toán', 'Sách toán và những công thức bổ ích', 20000, 1, 'Hằng đẳng thức', '/data/data/com.buihuuduy.btl_android/files/1733331439563_cover.jpg', 1, '2024-12-04'), " +
                    "('Sách Toán', 'Sách toán và những công thức bổ ích', 20000, 1, 'Hằng đẳng thức', '/data/data/com.buihuuduy.btl_android/files/1733331439563_cover.jpg', 1, '2024-12-04'), " +
                    "('Sách Toán', 'Sách toán và những công thức bổ ích', 20000, 1, 'Hằng đẳng thức', '/data/data/com.buihuuduy.btl_android/files/1733331439563_cover.jpg', 1, '2024-12-05'), " +
                    "('Sách Toán', 'Sách toán và những công thức bổ ích', 20000, 1, 'Hằng đẳng thức', '/data/data/com.buihuuduy.btl_android/files/1733331439563_cover.jpg', 1, '2024-12-05'), " +
                    "('Sách Toán', 'Sách toán và những công thức bổ ích', 20000, 1, 'Hằng đẳng thức', '/data/data/com.buihuuduy.btl_android/files/1733331439563_cover.jpg', 1, '2024-12-05'), " +
            "('Sách Văn', 'Văn và những câu chuyện cổ tích', 25000, 1, 'Mò kim đáy bể', '/data/data/com.buihuuduy.btl_android/files/1733331439563_cover.jpg', 1, '2024-12-06');";

    private static final String INIT_CATEGORY_LIST =
            "INSERT INTO category (name) VALUES " +
            "('Lãng mạn'), ('Kinh dị'), ('Khoa học viễn tưởng'), ('Tôn giáo - Tâm linh'), ('Kinh tế - Chính trị'), " +
            "('Tâm lý - Triết học'), ('Văn học - Lịch sử'), ('Thiếu nhi'), ('Tiểu thuyết'), ('Phát triển bản thân');";

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
            db.execSQL(INIT_USER);
            //db.execSQL(INIT_BOOK_LIST);
            db.execSQL(INIT_CATEGORY_LIST);
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
        values.put("category_id", bookEntity.getCategoryId());
        values.put("status", 0);
        values.put("created_at", String.valueOf(LocalDate.now()));
        return db.insert(TABLE_BOOK, null, values);
    }

    public long sellBook(BookEntity bookEntity) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", bookEntity.getName());
        values.put("description", bookEntity.getDescription());
        values.put("price", bookEntity.getPrice());
        values.put("image_path", bookEntity.getImagePath());
        values.put("user_id", bookEntity.getUserId());
        values.put("category_id", bookEntity.getCategoryId());
        values.put("status", 0);
        values.put("created_at", String.valueOf(LocalDate.now()));
        return db.insert(TABLE_BOOK, null, values);
    }

    public Cursor getAllBooksOnHomePage() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT b.id, b.name, b.description, b.price, b.image_path, u.full_name " +
                "FROM " + TABLE_BOOK + " b " +
                "JOIN " + TABLE_USER + " u ON b.user_id = u.id " +
                "WHERE b.status = 1 " +
                "ORDER BY b.id DESC ";
        return db.rawQuery(query, null);
    }
    public Cursor getAllBookOnMyBook(Integer userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT b.name, b.description, b.image_path, u.full_name, b.price, b.status " +
                "FROM " + TABLE_BOOK + " b " +
                "JOIN " + TABLE_USER + " u " +
                "ON b.user_id = u.id" + " WHERE u.id = ?";
        return db.rawQuery(query, new String[]{userId.toString()});
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

    public ArrayList<BarEntry> getWeeklySalesFromDatabase() {
        ArrayList<BarEntry> weeklyEntries = new ArrayList<>();

        try{
            SQLiteDatabase db = this.getReadableDatabase();
            String query = "SELECT strftime('%Y-%m-%d', created_at) AS week, " +
                    "Count(id) AS total_sales " +
                    "FROM book " +
                    "WHERE status = 1 and price != 0 " + // Chỉ tính những sách bán
                    "GROUP BY week " +
                    "ORDER BY week ASC";

            Cursor cursor = db.rawQuery(query, null);

            if (cursor.moveToFirst()) {
                int weekIndex = 1;
                do {
                    int temp = cursor.getColumnIndex("total_sales");
                    if(temp < 0) temp = 0;
                    float weeklySales = cursor.getFloat(temp);
                    weeklyEntries.add(new BarEntry(weekIndex, weeklySales));
                    weekIndex++;
                } while (cursor.moveToNext());
            }

            cursor.close();
            db.close();
        }catch(Exception e){
            String x = e.getMessage();
        }

        return weeklyEntries;
    }

    public ArrayList<String> getDaysFromDatabase()
    {
        ArrayList<String> days = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT strftime('%Y-%m-%d', created_at) AS week, " +
                "Count(id) AS total_sales " +
                "FROM book " +
                "WHERE status = 1 and price != 0 " + // Chỉ tính những sách bán
                "GROUP BY week " +
                "ORDER BY week ASC";

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") String day = cursor.getString(cursor.getColumnIndex("week"));
                days.add(day);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return days;
    }
    public ArrayList<String> getSellBookListFromDatabase()
    {
        ArrayList<String> books = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT strftime('%Y-%m-%d', created_at) AS week, " +
                "Count(id) AS total_sales, " +
                "GROUP_CONCAT(DISTINCT(name)) AS book " +
                "FROM book " +
                "WHERE status = 1 and price != 0 " + // Chỉ tính những sách bán
                "GROUP BY week " +
                "ORDER BY week ASC";

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") String book = cursor.getString(cursor.getColumnIndex("book"));
                books.add(book);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return books;
    }


    public ArrayList<BookEntity> getAllBooksAwaitingApproval() {
        ArrayList<BookEntity> bookList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query =
                "SELECT b.*, u.full_name FROM " + TABLE_BOOK + " b " +
                        "JOIN " + TABLE_USER + " u ON b.user_id = u.id " +
                        "WHERE b.status = 0 " +
                        "ORDER BY b.id DESC ";

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
                int price = cursor.getInt(cursor.getColumnIndexOrThrow("price"));
                String imagePath = cursor.getString(cursor.getColumnIndexOrThrow("image_path"));
                String username = cursor.getString(cursor.getColumnIndexOrThrow("full_name"));

                BookEntity book = new BookEntity();
                book.setId(id);
                book.setName(name);
                book.setDescription(description);
                book.setPrice(price);
                book.setImagePath(imagePath);
                book.setUserName(username);

                bookList.add(book);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return bookList;
    }

    public BookEntity getBookById(int id) {
        BookEntity book = null;
        SQLiteDatabase db = this.getReadableDatabase();

        String query =
                "SELECT b.*, u.full_name, u.email, c.name AS category_name FROM " + TABLE_BOOK + " b " +
                        "JOIN " + TABLE_USER + " u ON b.user_id = u.id " +
                        "JOIN " + TABLE_CATEGORY + " c ON b.category_id = c.id " +
                        "WHERE b.id = " + id;

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            book = new BookEntity();
            String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            String content = cursor.getString(cursor.getColumnIndexOrThrow("content"));
            String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
            int price = cursor.getInt(cursor.getColumnIndexOrThrow("price"));
            String imagePath = cursor.getString(cursor.getColumnIndexOrThrow("image_path"));
            String username = cursor.getString(cursor.getColumnIndexOrThrow("full_name"));
            String email = cursor.getString(cursor.getColumnIndexOrThrow("email"));
            String categoryName = cursor.getString(cursor.getColumnIndexOrThrow("category_name"));

            book.setName(name);
            book.setContent(content);
            book.setDescription(description);
            book.setPrice(price);
            book.setImagePath(imagePath);
            book.setUserName(username);
            book.setUserEmail(email);
            book.setCategoryName(categoryName);
        }

        cursor.close();
        db.close();

        return book;
    }

    public boolean updateBookStatus(int id, int status) {
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "UPDATE book SET status = ? WHERE id = ?";

        // Thực thi câu lệnh SQL
        SQLiteStatement statement = db.compileStatement(query);
        statement.bindLong(1, status);
        statement.bindLong(2, id);

        // Thực hiện cập nhật và trả về true nếu ít nhất một dòng bị ảnh hưởng
        return statement.executeUpdateDelete() > 0;
    }

    public List<CategoryEntity> getAllCategory()
    {
        List<CategoryEntity> categoryList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_CATEGORY;
        Cursor cursor = db.rawQuery(query, null);

        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    do {
                        Integer id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                        String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));

                        CategoryEntity category = new CategoryEntity(id, name);

                        categoryList.add(category);
                    } while (cursor.moveToNext());
                }
            } finally {
                cursor.close();
            }
        }

        db.close();
        return categoryList;
    }
}