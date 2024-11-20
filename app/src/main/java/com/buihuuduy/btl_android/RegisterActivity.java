package com.buihuuduy.btl_android;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.buihuuduy.btl_android.DBSQLite.DataHandler;
import com.buihuuduy.btl_android.common.ShowDialog;
import com.buihuuduy.btl_android.entity.UserEntity;

public class RegisterActivity extends AppCompatActivity
{
    DataHandler databaseHelper;
    EditText editTextEmail, editTextFullName, editTextPassword;
    Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        databaseHelper = new DataHandler(this);
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextFullName = (EditText) findViewById(R.id.editTextUsername);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        btnRegister = (Button) findViewById(R.id.btnRegister); btnRegister.setOnClickListener(new ButtonEvent());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private class ButtonEvent implements View.OnClickListener
    {
        @Override
        public void onClick(View view) {
            if(view.getId() == R.id.btnRegister) {
                solveBtnRegisterEvent();
            }
        }
    }

    private void solveBtnRegisterEvent()
    {
        String email = editTextEmail.getText().toString().trim();
        String fullName = editTextFullName.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if(TextUtils.isEmpty(email) || TextUtils.isEmpty(fullName) || TextUtils.isEmpty(password)) {
            ShowDialog.showAlertDialog(this, "Thông báo", "Vui lòng nhập đủ thông tin");
            return;
        }
        if(databaseHelper.checkEmailExist(email)) {
            ShowDialog.showAlertDialog(this, "Thông báo", "Email đã tồn tại");
            return;
        }

        UserEntity userEntity = new UserEntity(email, fullName, password);

        boolean isInserted = databaseHelper.registerUser(userEntity);
        if (isInserted) {
            ShowDialog.showToast(RegisterActivity.this, "Đăng ký thành công!");
        } else {
            ShowDialog.showToast(RegisterActivity.this, "Đăng ký thất bại");
        }
    }

}