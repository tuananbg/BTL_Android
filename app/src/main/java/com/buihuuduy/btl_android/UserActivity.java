package com.buihuuduy.btl_android;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class UserActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        TextView textView = findViewById(R.id.textViewUser);
        textView.setText("Welcome to User Dashboard");
    }
}
