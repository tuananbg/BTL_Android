package com.buihuuduy.btl_android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.buihuuduy.btl_android.DBSQLite.DataHandler;
import com.buihuuduy.btl_android.R;
import com.buihuuduy.btl_android.common.ShowDialog;
import com.google.android.material.navigation.NavigationView;

public class HomeSidebarActivity extends AppCompatActivity
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
                    ShowDialog.showToast(HomeSidebarActivity.this, "Home menu clicked");
                } else if (itemId == R.id.nav_document) {
                    ShowDialog.showToast(HomeSidebarActivity.this, "Document menu clicked");
                } else if (itemId == R.id.nav_share) {
                    ShowDialog.showToast(HomeSidebarActivity.this, "share menu clicked");
                } else if (itemId == R.id.nav_sale) {
                    Intent intent = new Intent(HomeSidebarActivity.this, TestMainActivity.class);
                    startActivity(intent);
                    finish();
                }
                drawerLayout.close();
                return false;
            }
        });
    }
}
