package com.buihuuduy.btl_android.activity;

import android.content.Intent;
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
import com.buihuuduy.btl_android.adapter.AdminBookAdapter;
import com.buihuuduy.btl_android.entity.BookEntity;
import com.google.android.material.navigation.NavigationView;
import java.util.ArrayList;

public class AdminActivity extends AppCompatActivity {
    DrawerLayout drawerLayout;
    ImageButton btnToggle;
    NavigationView navigationView;

    private ListView adminLvAwaitingApproval;
    private ArrayList<BookEntity> bookList;
    private AdminBookAdapter adapter;
    private DataHandler dataHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_sidebar);

        dataHandler = new DataHandler(this);

        drawerLayout = findViewById(R.id.sidebar_layout);
        btnToggle = findViewById(R.id.btnToggle);
        navigationView = findViewById(R.id.nav_view);
        adminLvAwaitingApproval = findViewById(R.id.adminLvAwaitingApproval);

        bookList = dataHandler.getAllBooksAwaitingApproval();
        adapter = new AdminBookAdapter(bookList, dataHandler, this);
        adminLvAwaitingApproval.setAdapter(adapter);

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
                if (itemId == R.id.nav_logout) {
                    Intent intent = new Intent(AdminActivity.this, LoginActivity.class);
                    startActivity(intent); finish();
                } else if (itemId == R.id.nav_statistical) {
                    Intent intent = new Intent(AdminActivity.this, ChartExportFile.class);
                    startActivity(intent); finish();
                }
                drawerLayout.close();
                return false;
            }
        });
    }
}
