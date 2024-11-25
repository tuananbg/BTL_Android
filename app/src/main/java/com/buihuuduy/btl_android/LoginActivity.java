package com.buihuuduy.btl_android;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.buihuuduy.btl_android.DBSQLite.DataHandler;
import com.buihuuduy.btl_android.common.ShowDialog;

public class LoginActivity extends AppCompatActivity {

    EditText editTextEmail, editTextPassword;
    Button btnLogin, btnRegister;
    DataHandler databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize components
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);

        // Create an instance of DataHandler for database operations
        databaseHelper = new DataHandler(this);

        // Set button click listeners
        btnLogin.setOnClickListener(new ButtonEvent());
        btnRegister.setOnClickListener(new ButtonEvent());
    }

    private class ButtonEvent implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.btnLogin) {
                handleLogin();
            } else if (view.getId() == R.id.btnRegister) {
                // Navigate to Register Activity if the user wants to register
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        }
    }

    private void handleLogin() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        // Validate the fields
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            ShowDialog.showAlertDialog(this, "Thông báo", "Vui lòng nhập đủ thông tin");
            return;
        }

        // Check if user exists and credentials match
        int role = databaseHelper.authenticateUser(email, password);
        if (role == 1) {
            // Admin login
            ShowDialog.showToast(LoginActivity.this, "Đăng nhập thành công với quyền Admin!");

            Intent intent = new Intent(LoginActivity.this, AdminActivity.class);
            startActivity(intent);
            finish();  // Close the login activity
        } else if (role == 0) {
            // User login
            ShowDialog.showToast(LoginActivity.this, "Đăng nhập thành công!");

            Intent intent = new Intent(LoginActivity.this, HomePageActivity.class);
            startActivity(intent);
            finish();  // Close the login activity
        } else {
            // Invalid credentials
            ShowDialog.showAlertDialog(this, "Thông báo", "Thông tin đăng nhập không chính xác");
        }
    }
}
