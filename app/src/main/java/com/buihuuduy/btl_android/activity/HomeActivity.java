package com.buihuuduy.btl_android.activity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import com.buihuuduy.btl_android.DBSQLite.DataHandler;
import com.buihuuduy.btl_android.R;
import com.buihuuduy.btl_android.adapter.BookAdapter;
import com.buihuuduy.btl_android.common.ShowDialog;
import com.buihuuduy.btl_android.entity.BookEntity;
import com.google.android.material.navigation.NavigationView;
import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity
{
    // Declare menu component
    private DrawerLayout drawerLayout;
    private ImageButton btnToggle;
    private NavigationView navigationView;

    // Declare list book component
    private DataHandler dataHandler;
    private ListView listView;
    private BookAdapter adapter;
    private List<BookEntity> bookList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_sidebar);

        initializeViews();

        getAllBooksOnHomePage();

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
                if (itemId == R.id.nav_document) {
                    ShowDialog.showToast(HomeActivity.this, "Document menu clicked");
                } else if (itemId == R.id.nav_share) {
                    Intent intent = new Intent(HomeActivity.this, ShareBookActivity.class);
                    startActivity(intent); finish();
                } else if (itemId == R.id.nav_sale) {
                    Intent intent = new Intent(HomeActivity.this, SellBookActivity.class);
                    startActivity(intent); finish();
                } else if (itemId == R.id.nav_logout) {
                    Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                    startActivity(intent); finish();
                }
                drawerLayout.close();
                return false;
            }
        });
    }

    private void initializeViews()
    {
        drawerLayout = findViewById(R.id.sidebar_layout);
        btnToggle = findViewById(R.id.btnToggle);
        navigationView = findViewById(R.id.nav_view);
        dataHandler = new DataHandler(this);
        listView = findViewById(R.id.homePageListViewBooks);
        bookList = new ArrayList<>();
        adapter = new BookAdapter(bookList, dataHandler, this);
        listView.setAdapter(adapter);
    }

    private void getAllBooksOnHomePage() {
        bookList.clear();
        Cursor cursor = dataHandler.getAllBooksOnHomePage();
        if (cursor.moveToFirst()) {
            do {
                Integer bookId = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String bookName = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                String bookDescription = cursor.getString(cursor.getColumnIndexOrThrow("description"));
                String imagePath = cursor.getString(cursor.getColumnIndexOrThrow("image_path"));
                String username = cursor.getString(cursor.getColumnIndexOrThrow("full_name"));
                Integer price = cursor.getInt(cursor.getColumnIndexOrThrow("price"));

                BookEntity bookEntity = new BookEntity();
                bookEntity.setId(bookId);
                bookEntity.setName(bookName);
                bookEntity.setDescription(bookDescription);
                bookEntity.setImagePath(imagePath);
                bookEntity.setUserName(username);
                bookEntity.setPrice(price);

                bookList.add(bookEntity);
            } while (cursor.moveToNext());
        }
        cursor.close();
        adapter.notifyDataSetChanged();
    }
}
