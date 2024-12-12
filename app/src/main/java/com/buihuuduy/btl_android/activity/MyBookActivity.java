package com.buihuuduy.btl_android.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.buihuuduy.btl_android.DBSQLite.DataHandler;
import com.buihuuduy.btl_android.R;
import com.buihuuduy.btl_android.adapter.BookAdapter;
import com.buihuuduy.btl_android.adapter.MyBookAdapter;
import com.buihuuduy.btl_android.common.ShowDialog;
import com.buihuuduy.btl_android.entity.BookEntity;
import com.buihuuduy.btl_android.entity.CategoryEntity;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

public class MyBookActivity extends AppCompatActivity {
    // Declare menu component
    private DrawerLayout drawerLayout;
    private ImageButton btnToggle;
    private NavigationView navigationView;

    // Declare list book component
    private Spinner spinnerCategory;
    private DataHandler dataHandler;
    private ListView listView;
    private MyBookAdapter adapter;
    private List<BookEntity> bookList;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences = this.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        email = sharedPreferences.getString("email", "null");
        Log.e("email: ", email);

        setContentView(R.layout.mybook_sidebar);

        initializeViews();

        getAllBooksOnMyBook();


        btnToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.open();
            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_home) {
                    Intent intent = new Intent(MyBookActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                } else if (itemId == R.id.nav_document) {
                    Intent intent = new Intent(MyBookActivity.this, MyBookActivity.class);
                    startActivity(intent);
                    finish();
                } else if (itemId == R.id.nav_share) {
                    Intent intent = new Intent(MyBookActivity.this, ShareBookActivity.class);
                    startActivity(intent);
                    finish();
                } else if (itemId == R.id.nav_sale) {
                    ShowDialog.showToast(MyBookActivity.this, "Sale menu clicked");
                }
                drawerLayout.close();
                return false;
            }
        });
        ImageButton btnFilter = findViewById(R.id.btnFilter);
        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performFilter();
            }
        });
    }
    private void performFilter() {
        String selectedCategory = spinnerCategory.getSelectedItem().toString(); // Lấy tên category
        String filterType = "";

        // Kiểm tra nếu các CheckBox được chọn
        CheckBox checkBoxShare = findViewById(R.id.checkBoxShare);
        CheckBox checkBoxSale = findViewById(R.id.checkBoxSale);

        List<String> selectedFilters = new ArrayList<>();
        if (checkBoxShare.isChecked()) {
            selectedFilters.add("Share");
        }
        if (checkBoxSale.isChecked()) {
            selectedFilters.add("Sale");
        }

        // Lọc sách theo category và loại tài liệu
        filterBooks(selectedCategory, selectedFilters);
    }

    private void filterBooks(String categoryName, List<String> filterTypes) {
        bookList.clear();
        Cursor cursor = dataHandler.getFilteredBooks(categoryName, filterTypes); // Hàm xử lý lấy sách lọc
        if (cursor.moveToFirst()) {
            do {
                String bookName = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                String bookDescription = cursor.getString(cursor.getColumnIndexOrThrow("description"));
                String imagePath = cursor.getString(cursor.getColumnIndexOrThrow("image_path"));
                String username = cursor.getString(cursor.getColumnIndexOrThrow("full_name"));
                Integer price = cursor.getInt(cursor.getColumnIndexOrThrow("price"));
                Integer status = cursor.getInt(cursor.getColumnIndexOrThrow("status"));

                BookEntity bookEntity = new BookEntity();
                bookEntity.setName(bookName);
                bookEntity.setDescription(bookDescription);
                bookEntity.setImagePath(imagePath);
                bookEntity.setUserName(username);
                bookEntity.setPrice(price);
                bookEntity.setStatus(status);

                bookList.add(bookEntity);
            } while (cursor.moveToNext());
        }
        cursor.close();

        adapter.notifyDataSetChanged();
    }
    private void initializeViews()
    {
        drawerLayout = findViewById(R.id.sidebar_layout);
        btnToggle = findViewById(R.id.btnToggle);
        navigationView = findViewById(R.id.nav_view);
        dataHandler = new DataHandler(this);
        listView = findViewById(R.id.listView);
        spinnerCategory = findViewById(R.id.spinner2);

        bookList = new ArrayList<>();

        adapter = new MyBookAdapter(bookList, dataHandler, this);
        listView.setAdapter(adapter);

        // Kiểm tra nếu các CheckBox được chọn
        CheckBox checkBoxShare = findViewById(R.id.checkBoxShare);
        CheckBox checkBoxSale = findViewById(R.id.checkBoxSale);

        checkBoxShare.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                checkBoxSale.setChecked(false);
            }
        });

        checkBoxSale.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                checkBoxShare.setChecked(false);
            }
        });

        List<CategoryEntity> categories = getAllCategories();
        ArrayList<String> cateNames =new ArrayList<>();
        cateNames.add("Tất cả");
        for (CategoryEntity category : categories) {
            cateNames.add(category.getName());
            Log.d("CategoryName", "Category: " + category.getName());
        }

        if (cateNames.isEmpty()) {
            Log.d("SpinnerError", "Category names list is empty");
        } else {
            Log.d("SpinnerSuccess", "Category names loaded successfully");
        }
        // Tạo ArrayAdapter
        ArrayAdapter<String> adapterCate = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                cateNames
        );
        adapterCate.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Gắn adapter vào Spinner
        spinnerCategory.setAdapter(adapterCate);

    }


    private void getAllBooksOnMyBook() {
        bookList.clear();
        Integer userId = dataHandler.getUserByEmail(email).getId();
        Cursor cursor = dataHandler.getAllBookOnMyBook(userId);
        if (cursor.moveToFirst()) {
            do {
                String bookName = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                String bookDescription = cursor.getString(cursor.getColumnIndexOrThrow("description"));
                String imagePath = cursor.getString(cursor.getColumnIndexOrThrow("image_path"));
                String username = cursor.getString(cursor.getColumnIndexOrThrow("full_name"));
                Integer price = cursor.getInt(cursor.getColumnIndexOrThrow("price"));
                Integer status = cursor.getInt(cursor.getColumnIndexOrThrow("status"));

                BookEntity bookEntity = new BookEntity();
                bookEntity.setName(bookName);
                bookEntity.setDescription(bookDescription);
                bookEntity.setImagePath(imagePath);
                bookEntity.setUserName(username);
                bookEntity.setPrice(price);
                bookEntity.setStatus(status);

                bookList.add(bookEntity);
            } while (cursor.moveToNext());
        }
        cursor.close();
        adapter.notifyDataSetChanged();
    }

    public List<CategoryEntity> getAllCategories() {
        List<CategoryEntity> categories = new ArrayList<>();
        Cursor cursor = dataHandler.getAllCategories();

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                categories.add(new CategoryEntity(id, name));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return categories;
    }

}