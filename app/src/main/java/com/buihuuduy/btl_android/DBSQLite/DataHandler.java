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
            "('user1', 'First User', '1234', 0), " +
            "('user2', 'Second User', '1234', 0), " +
            "('admin1', 'Admin', '1234', 1);";

    private static final String INIT_BOOK_LIST =
            "INSERT INTO book (name, description, price, status, content, image_path, user_id, created_at, category_id) VALUES " +
            "('Nơi chốn bình yên', 'Harriet và Wyn là cặp đôi hoàn hảo kể từ khi họ gặp nhau ở trường đại học, họ hợp nhau như muối và tiêu, mật ong và trà.', 210000, 1, 'Tình yêu và sự hối tiếc: Cuốn sách khám phá những gì xảy ra khi tình yêu đối diện với những thử thách thực sự, và cách mà hối tiếc và những quyết định sai lầm có thể ảnh hưởng đến mối quan hệ. Tình bạn và sự trưởng thành: “Happy Place” cũng tập trung vào tình bạn lâu năm, cách mà thời gian và những biến đổi trong cuộc sống ảnh hưởng đến mối quan hệ bạn bè, và cách mà những người bạn thân thiết có thể trở thành gia đình.', '/data/data/com.buihuuduy.btl_android/files/1734493798480_cover.jpg', 1, '2024-12-03', 1), " +
            "('An Insignificant Case', 'Charlie Webb là một luật sư tốt nghiệp trường luật vì không được bất kỳ công ty luật nào tuyển dụng nên đã mở công ty riêng.', 275000, 1, 'Charlie đã có một cuộc sống tầm thường, cả về mặt cá nhân lẫn nghề nghiệp. Cho đến khi anh được bổ nhiệm làm luật sư cho một nghệ sĩ lập dị tự xưng là Guido Sabatini. Sabatini đã bị bắt một lần nữa vì đột nhập vào một nhà hàng và lấy trộm một bức tranh mà anh đã bán cho họ vì anh cảm thấy bị xúc phạm bởi nơi trưng bày bức tranh', '/data/data/com.buihuuduy.btl_android/files/1734493789462_cover.jpg', 1, '2024-12-03', 2), " +
            "('Thời gian của trái tim', 'Một biên niên sử sâu sắc và đầy cảm động, từ một trong những nhà trị liệu tâm lý nổi tiếng nhất thời đại chúng ta, cùng những thách thức và đột phá mà ông đã đạt được khi tiếp nhận bệnh nhân trong một giờ, chỉ một lần duy nhất.', 160000, 1, 'Đối mặt với tình trạng mất trí nhớ ở tuổi 93, cũng như hậu quả của một đại dịch toàn cầu đã chuyển phần lớn cuộc sống hàng ngày lên mạng, nhà trị liệu tâm lý huyền thoại và tác giả sách bán chạy nhất Irvin Yalom đã buộc phải xem xét lại rất nhiều về hình thức các buổi trị liệu của mình với bệnh nhân. Nhưng thay vì đầu hàng trước sự thay đổi, Tiến sĩ Yalom đã xem xét trực tiếp những hạn chế do những thực tế mới này áp đặt và cách mạng hóa hoạt động thực hành của mình.', '/data/data/com.buihuuduy.btl_android/files/1734493784829_cover.jpg', 2, '2024-12-06', 6), " +
            "('Hilda and Twig: Hide from the Rain', 'Một cuộc phiêu lưu hoàn toàn mới có sự góp mặt của nữ anh hùng tóc xanh được chúng ta yêu thích Hilda và người bạn đồng hành đáng tin cậy chú cáo nai Twig.', 150000, 1, 'Hilda và Twig sẽ không bao giờ để một chút mưa cản trở cuộc phiêu lưu, nhưng sẽ khác khi cuộc thám hiểm khu rừng của bạn bị gián đoạn bởi một cơn bão lớn. Núp trong một gò đất bí ẩn dưới lòng đất, Twig nhanh chóng nhận ra rằng rắc rối đang đến gần, và người bạn tóc xanh thân nhất của anh đang gặp nguy hiểm. Thật không may, anh chưa bao giờ thực sự nghĩ mình là người dũng cảm, nhưng có vẻ như anh sẽ phải bước lên và cứu Hilda khỏi một loạt rắc rối lớn, đầy vảy!', '/data/data/com.buihuuduy.btl_android/files/1734493779801_cover.jpg', 2, '2024-12-06', 8), " +
            "('The Wedding People', 'Một cuốn tiểu thuyết đầy sức thuyết phục và khôn ngoan về một vị khách dự đám cưới bất ngờ và những người bất ngờ đã giúp cô bắt đầu lại cuộc sống.', 250000, 1, 'Một ngày đẹp trời ở Newport, Rhode Island, khi Phoebe Stone đến Cornwall Inn lớn mặc một chiếc váy xanh lá cây và giày cao gót màu vàng, không có một chiếc túi nào trong tầm mắt, một mình. Cô ấy ngay lập tức bị mọi người trong sảnh nhầm là một trong những người dự tiệc cưới, nhưng thực ra cô ấy là vị khách duy nhất tại Cornwall không đến đây để tham dự sự kiện lớn này. Phoebe ở đây vì cô ấy đã mơ ước được đến đây trong nhiều năm—cô ấy hy vọng được bóc vỏ hàu và đi thuyền ngắm hoàng hôn cùng chồng, chỉ có điều bây giờ cô ấy ở đây mà không có anh ấy, ở tận cùng vực thẳm, và quyết tâm có một lần phung phí xa hoa cuối cùng cho bản thân.', '/data/data/com.buihuuduy.btl_android/files/1734493793826_cover.jpg', 1, '2024-12-06', 1);";

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
            db.execSQL(INIT_CATEGORY_LIST);
            db.execSQL(INIT_BOOK_LIST);
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
    //k
    public Cursor getAllBookOnMyBook(Integer userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT b.name, b.description, b.image_path, u.full_name, b.price, b.status, b.id " +
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
    //k
    public Cursor getFilteredBooks(String categoryName, List<String> filterType, Integer userId) {
        String query = "SELECT b.*, u.full_name FROM book b " +
                "JOIN category c ON b.category_id = c.id " +
                "JOIN user u ON b.user_id = u.id " + "WHERE u.id = ?";

        if(!categoryName.contains("Tất cả")){
            if (filterType.size() == 1) {
                if(filterType.get(0).contains("Sale")){
                    query += "AND b.price > 0 "; //
                }else{
                    query += "AND b.price = 0 ";
                }
            }
            query += "AND c.name = ?";
            return getReadableDatabase().rawQuery(query, new String[]{userId.toString(), categoryName});
        }else{
            if (filterType.size() == 1) {
                if(filterType.get(0).contains("Sale")){
                    query += "AND b.price > 0 "; //
                }else{
                    query += "AND b.price = 0 ";
                }
            }
            return getReadableDatabase().rawQuery(query, new String[]{userId.toString()});
        }
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