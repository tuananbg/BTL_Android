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
    DrawerLayout drawerLayout;
    ImageButton btnToggle;
    NavigationView navigationView;

    private DataHandler dataHandler;
    private ListView listView;
    private BookAdapter adapter;
    private List<BookEntity> bookList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_sidebar);

        drawerLayout = findViewById(R.id.sidebar_layout);
        btnToggle = findViewById(R.id.btnToggle);
        navigationView = findViewById(R.id.nav_view);
        dataHandler = new DataHandler(this);

        listView = findViewById(R.id.homePageListViewBooks);
        bookList = new ArrayList<>();
        adapter = new BookAdapter(bookList, dataHandler, this);
        listView.setAdapter(adapter);

        btnToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.open();
            }
        });

        loadBooksFromDatabase();

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_home) {
                    ShowDialog.showToast(HomeActivity.this, "Home menu clicked");
                } else if (itemId == R.id.nav_document) {
                    ShowDialog.showToast(HomeActivity.this, "Document menu clicked");
                } else if (itemId == R.id.nav_share) {
                    Intent intent = new Intent(HomeActivity.this, ShareBookActivity.class);
                    startActivity(intent);
                    finish();
                } else if (itemId == R.id.nav_sale) {
                    ShowDialog.showToast(HomeActivity.this, "Sale menu clicked");
                }
                drawerLayout.close();
                return false;
            }
        });
    }

    private void loadBooksFromDatabase() {
        bookList.clear();
        Cursor cursor = dataHandler.getAllBooksOnUser();
        if (cursor.moveToFirst()) {
            do {
                String bookName = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                String bookDescription = cursor.getString(cursor.getColumnIndexOrThrow("description"));
                String imagePath = cursor.getString(cursor.getColumnIndexOrThrow("image_path"));
                String username = cursor.getString(cursor.getColumnIndexOrThrow("full_name"));

                BookEntity bookEntity = new BookEntity();
                bookEntity.setName(bookName);
                bookEntity.setDescription(bookDescription);
                bookEntity.setImagePath(imagePath);
                bookEntity.setUserName(username);

                bookList.add(bookEntity);
            } while (cursor.moveToNext());
        }
        cursor.close();
        adapter.notifyDataSetChanged();
    }
}
