package com.buihuuduy.btl_android.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.buihuuduy.btl_android.DBSQLite.TestDatabaseHelper;
import com.buihuuduy.btl_android.R;
import com.buihuuduy.btl_android.adapter.BookAdapter;
import com.buihuuduy.btl_android.common.ShowDialog;
import com.buihuuduy.btl_android.entity.Book;
import com.google.android.material.navigation.NavigationView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TestMainActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    ImageButton btnToggle;
    NavigationView navigationView;

    private TestDatabaseHelper databaseHelper;
    private RecyclerView recyclerView;
    private BookAdapter adapter;
    private List<Book> bookList;
    private static final int PICK_IMAGE_REQUEST = 1;
    private String selectedImagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addbook_sidebar);

        drawerLayout = findViewById(R.id.sidebar_layout);
        btnToggle = findViewById(R.id.btnToggle);
        navigationView = findViewById(R.id.nav_view);

        databaseHelper = new TestDatabaseHelper(this);
        recyclerView = findViewById(R.id.recyclerViewBooks);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        bookList = new ArrayList<>();
        adapter = new BookAdapter(bookList, databaseHelper, this);
        recyclerView.setAdapter(adapter);

        Button btnAddBook = findViewById(R.id.btnAddBook);
        btnAddBook.setOnClickListener(v -> showAddBookDialog());

        loadBooksFromDatabase();

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
                    ShowDialog.showToast(TestMainActivity.this, "Home menu clicked");
                } else if (itemId == R.id.nav_document) {
                    ShowDialog.showToast(TestMainActivity.this, "Document menu clicked");
                } else if (itemId == R.id.nav_share) {
                    ShowDialog.showToast(TestMainActivity.this, "share menu clicked");
                } else if (itemId == R.id.nav_sale) {

                }
                drawerLayout.close();
                return false;
            }
        });
    }

    private void loadBooksFromDatabase() {
        bookList.clear();
        Cursor cursor = databaseHelper.getAllBooks();
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
                String author = cursor.getString(cursor.getColumnIndexOrThrow("author"));
                String coverPath = cursor.getString(cursor.getColumnIndexOrThrow("cover_image_path"));

                bookList.add(new Book(id, title, author, coverPath));
            } while (cursor.moveToNext());
        }
        cursor.close();
        adapter.notifyDataSetChanged();
    }

    private void showAddBookDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.add_book, null);
        builder.setView(dialogView);

        EditText editTextTitle = dialogView.findViewById(R.id.editTextTitle);
        EditText editTextAuthor = dialogView.findViewById(R.id.editTextAuthor);
        ImageView imageViewCover = dialogView.findViewById(R.id.imageViewCover);
        Button btnChooseImage = dialogView.findViewById(R.id.btnChooseImage);

        btnChooseImage.setOnClickListener(v -> pickImageFromGallery());

        builder.setPositiveButton("Lưu", (dialog, which) -> {
            String title = editTextTitle.getText().toString().trim();
            String author = editTextAuthor.getText().toString().trim();

            if (title.isEmpty() || author.isEmpty() || selectedImagePath == null) {
                Toast.makeText(TestMainActivity.this, "Vui lòng nhập đầy đủ thông tin và chọn ảnh!", Toast.LENGTH_SHORT).show();
                return;
            }

            String imagePath = saveImageLocally(selectedImagePath);

            long result = databaseHelper.insertBook(title, author, imagePath);
            if (result != -1) {
                Toast.makeText(TestMainActivity.this, "Thêm sách thành công!", Toast.LENGTH_SHORT).show();
                loadBooksFromDatabase();
            } else {
                Toast.makeText(TestMainActivity.this, "Có lỗi xảy ra!", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Hủy", null);
        builder.create().show();
    }

    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
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