package com.buihuuduy.btl_android;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class AdminActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        TextView textView = findViewById(R.id.textViewAdmin);
        textView.setText("Welcome to Admin Dashboard");
    }
}
