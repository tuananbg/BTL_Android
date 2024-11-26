package com.buihuuduy.btl_android.activity;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.buihuuduy.btl_android.DBSQLite.DataHandler;
import com.buihuuduy.btl_android.R;
import com.buihuuduy.btl_android.entity.UserEntity;
import com.buihuuduy.btl_android.common.ShowDialog;

public class RegisterActivity extends AppCompatActivity {

    private EditText editTextEmail, editTextFullName, editTextPassword;
    private Button btnRegister, btnExit;
    private DataHandler databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initializeViews();

        // Create an instance of DataHandler for database operations
        databaseHelper = new DataHandler(this);

        // Set button click listeners
        setButtonListeners();
    }

    private void initializeViews() {
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextFullName = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        btnRegister = findViewById(R.id.btnRegister);
        btnExit = findViewById(R.id.btnExit);
    }

    private void setButtonListeners() {
        btnRegister.setOnClickListener(new ButtonEvent());
        btnExit.setOnClickListener(new ButtonEvent());
    }

    private class ButtonEvent implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.btnRegister) {
                handleRegister();
            } else if (view.getId() == R.id.btnExit) {
                finish(); // Close register activity
            }
        }
    }

    private void handleRegister() {
        String email = editTextEmail.getText().toString().trim();
        String fullName = editTextFullName.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        // Validate inputs
        if (!validateInputs(email, fullName, password)) {
            return;  // Validation failed, exit early
        }

        // Check if email already exists using the updated method
        if (isEmailExists(email)) {
            return;  // Email exists, exit early
        }

        // Proceed with registration
        UserEntity newUser = new UserEntity(email, fullName, password, 0); // 0 means normal user
        if (registerNewUser(newUser)) {
            onRegisterSuccess();
        } else {
            onRegisterFailure();
        }
    }

    private boolean validateInputs(String email, String fullName, String password) {
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(fullName) || TextUtils.isEmpty(password)) {
            ShowDialog.showAlertDialog(this, "Thông báo", "Vui lòng nhập đủ thông tin");
            return false;  // Validation failed
        }
        return true;  // Validation passed
    }

    private boolean isEmailExists(String email) {
        // Get writable database to pass it to the checkEmailExist method
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        boolean emailExists = databaseHelper.checkEmailExist(db, email);
        if (emailExists) {
            ShowDialog.showAlertDialog(this, "Thông báo", "Email đã tồn tại");
        }
        return emailExists;  // Return true if email exists, false otherwise
    }

    private boolean registerNewUser(UserEntity newUser) {
        return databaseHelper.registerUser(newUser); // Attempt to register the user
    }

    private void onRegisterSuccess() {
        ShowDialog.showToast(RegisterActivity.this, "Đăng ký thành công!");
        finish();  // Close register activity on success
    }

    private void onRegisterFailure() {
        ShowDialog.showAlertDialog(this, "Thông báo", "Đăng ký không thành công");
    }
}
