package com.buihuuduy.btl_android;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.buihuuduy.btl_android.DBSQLite.DataHandler;
import com.buihuuduy.btl_android.common.ShowDialog;
import com.buihuuduy.btl_android.entity.BookEntity;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HomePageActivity extends AppCompatActivity
{
    DrawerLayout drawerLayout;
    ImageButton btnToggle;
    NavigationView navigationView;
    ListView listViewBook;
    DataHandler dataHandler;
    Adapter bookAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_sidebar);

        drawerLayout = findViewById(R.id.sidebar_layout);
        btnToggle = findViewById(R.id.btnToggle);
        navigationView = findViewById(R.id.nav_view);
        dataHandler = new DataHandler(this);

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
                    ShowDialog.showToast(HomePageActivity.this, "Home menu clicked");
                } else if (itemId == R.id.nav_document) {
                    ShowDialog.showToast(HomePageActivity.this, "Document menu clicked");
                } else if (itemId == R.id.nav_share) {
                    ShowDialog.showToast(HomePageActivity.this, "share menu clicked");
                } else if (itemId == R.id.nav_sale) {
                    ShowDialog.showToast(HomePageActivity.this, "Sale menu clicked");
                }
                drawerLayout.close();
                return false;
            }
        });
    }
}
