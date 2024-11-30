package com.buihuuduy.btl_android.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.buihuuduy.btl_android.DBSQLite.DataHandler;
import com.buihuuduy.btl_android.R;
import com.buihuuduy.btl_android.entity.BookEntity;
import com.google.android.material.navigation.NavigationView;

import java.io.File;

public class AdminDetailBook extends AppCompatActivity {
    private ImageView adminBookImage, adminDetailBookBackButton;
    private TextView adminAuthorName, adminAuthorEmail, adminDetailBookContent, adminDetailBookDescription;
    private Button adminApproveButton, adminRejectButton;
    private DataHandler dataHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_detail_book);

        dataHandler = new DataHandler(this);

        adminDetailBookBackButton = findViewById(R.id.adminDetailBookBackButton);
        adminBookImage = findViewById(R.id.adminBookImage);
        adminAuthorName = findViewById(R.id.adminAuthorName);
        adminAuthorEmail = findViewById(R.id.adminAuthorEmail);
        adminDetailBookContent = findViewById(R.id.adminDetailBookContent);
        adminDetailBookDescription = findViewById(R.id.adminDetailBookDescription);
        adminApproveButton = findViewById(R.id.adminApproveButton);
        adminRejectButton = findViewById(R.id.adminRejectButton);

        int bookId = getIntent().getIntExtra("BOOK_ID", -1);
        BookEntity book = dataHandler.getBookById(bookId);

        adminAuthorName.setText(book.getUserName());
        adminAuthorEmail.setText(book.getUserEmail());
        adminDetailBookContent.setText(book.getContent());
        adminDetailBookDescription.setText(book.getDescription());

        File imgFile = new File(book.getImagePath());
        if (imgFile.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            adminBookImage.setImageBitmap(bitmap);
        } else {
            adminBookImage.setImageResource(R.drawable.logo);
        }

        adminDetailBookBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminDetailBook.this, AdminActivity.class);
                AdminDetailBook.this.startActivity(intent);
            }
        });

        adminApproveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isUpdated = dataHandler.updateBookStatus(bookId, 1);
                Intent intent = new Intent(AdminDetailBook.this, AdminActivity.class);
                AdminDetailBook.this.startActivity(intent);
                if (isUpdated) {
                    Toast.makeText(AdminDetailBook.this, "Phê duyệt thành công", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(AdminDetailBook.this, "Phê duyệt thất bại", Toast.LENGTH_SHORT).show();
                }
            }
        });

        adminRejectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isUpdated = dataHandler.updateBookStatus(bookId, 2);
                Intent intent = new Intent(AdminDetailBook.this, AdminActivity.class);
                AdminDetailBook.this.startActivity(intent);
                if (isUpdated) {
                    Toast.makeText(AdminDetailBook.this, "Từ chối thành công", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(AdminDetailBook.this, "Từ chối thất bại", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
