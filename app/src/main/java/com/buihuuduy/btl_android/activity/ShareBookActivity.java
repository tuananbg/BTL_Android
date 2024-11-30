package com.buihuuduy.btl_android.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import com.buihuuduy.btl_android.DBSQLite.DataHandler;
import com.buihuuduy.btl_android.R;
import com.buihuuduy.btl_android.common.ShowDialog;
import com.buihuuduy.btl_android.entity.BookEntity;
import com.google.android.material.navigation.NavigationView;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ShareBookActivity extends AppCompatActivity
{
    // Declare menu component
    private DrawerLayout drawerLayout;
    private ImageButton btnToggle;
    private NavigationView navigationView;

    // Declare page component
    private EditText editTextBookName, editTextBookDescription, editTextBookContent;
    private Button btnShareBook;
    private ImageButton imageButtonBookImage;
    private DataHandler dataHandler;

    // Global variable
    private String selectedImagePath;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sharebook_sidebar);

        SharedPreferences sharedPreferences = this.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        email = sharedPreferences.getString("email", "null");

        initialViews();

        imageButtonBookImage.setOnClickListener(v -> pickImageFromGallery());

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
                    Intent intent = new Intent(ShareBookActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                } else if (itemId == R.id.nav_document) {
                    ShowDialog.showToast(ShareBookActivity.this, "Document menu clicked");
                } else if (itemId == R.id.nav_sale) {
                    ShowDialog.showToast(ShareBookActivity.this, "Sale menu clicked");
                }
                drawerLayout.close();
                return false;
            }
        });

        btnShareBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                handleShareBtn();
            }
        });
    }

    private void initialViews()
    {
        drawerLayout = findViewById(R.id.sidebar_layout);
        btnToggle = findViewById(R.id.btnToggle);
        navigationView = findViewById(R.id.nav_view);
        dataHandler = new DataHandler(this);
        editTextBookName = findViewById(R.id.editTextBookName);
        editTextBookDescription = findViewById(R.id.editTextBookDescription);
        editTextBookContent = findViewById(R.id.editTextBookContent);
        imageButtonBookImage = findViewById(R.id.shareBookImageBookButton);
        btnShareBook = findViewById(R.id.btnShareBook);
    }

    private void handleShareBtn()
    {
        String bookName = editTextBookName.getText().toString().trim();
        String bookDescription = editTextBookDescription.getText().toString().trim();
        String bookContent = editTextBookContent.getText().toString().trim();
        String imagePath = saveImageLocally(selectedImagePath);

        BookEntity bookEntity = new BookEntity();
        bookEntity.setName(bookName);
        bookEntity.setDescription(bookDescription);
        bookEntity.setContent(bookContent);
        bookEntity.setImagePath(imagePath);
        bookEntity.setUserId(dataHandler.getUserByEmail(email).getId());

        long result = dataHandler.shareBook(bookEntity);
        if (result != -1) {
            ShowDialog.showToast(ShareBookActivity.this, "Chia sẻ sách thành công!");
        } else {
            ShowDialog.showToast(ShareBookActivity.this, "Có lỗi xảy ra!");
        }
    }

    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            selectedImagePath = getRealPathFromURI(imageUri);
        }
    }

    private String getRealPathFromURI(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String path = cursor.getString(columnIndex);
            cursor.close();
            return path;
        }
        return null;
    }

    private String saveImageLocally(String sourcePath) {
        File directory = getFilesDir();
        File destFile = new File(directory, System.currentTimeMillis() + "_cover.jpg");

        try (FileOutputStream out = new FileOutputStream(destFile)) {
            Bitmap bitmap = BitmapFactory.decodeFile(sourcePath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return destFile.getAbsolutePath();
    }
}
