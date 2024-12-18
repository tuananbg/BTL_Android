package com.buihuuduy.btl_android.activity;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import com.buihuuduy.btl_android.DBSQLite.DataHandler;
import com.buihuuduy.btl_android.R;
import com.buihuuduy.btl_android.common.ShowDialog;
import com.buihuuduy.btl_android.entity.BookEntity;
import com.buihuuduy.btl_android.entity.CategoryEntity;
import com.google.android.material.navigation.NavigationView;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SellBookActivity extends AppCompatActivity
{
    // Declare menu component
    private DrawerLayout drawerLayout;
    private ImageButton btnToggle;
    private NavigationView navigationView;

    // Declare page component
    private EditText editTextBookName, editTextBookDescription, editTextBookPrice;
    private Button btnSellBook;
    private ImageButton imageButtonBookImage;
    private Spinner spinnerBookCategory;

    // Global variable
    private DataHandler dataHandler;
    private String selectedImagePath;
    private String email;
    private List<CategoryEntity> categoryList;
    private final BookEntity bookEntity = new BookEntity();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sellbook_sidebar);

        SharedPreferences sharedPreferences = this.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        email = sharedPreferences.getString("email", "null");

        initializeViews();
        initializeSpinnerBookCategory();

        imageButtonBookImage.setOnClickListener(v -> pickImageFromGallery());
        spinnerBookCategory.setOnItemSelectedListener(new SpinnerEvent());
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
                    Intent intent = new Intent(SellBookActivity.this, HomeActivity.class);
                    startActivity(intent); finish();
                } else if (itemId == R.id.nav_document) {
                    ShowDialog.showToast(SellBookActivity.this, "Document menu clicked");
                } else if (itemId == R.id.nav_share) {
                    Intent intent = new Intent(SellBookActivity.this, ShareBookActivity.class);
                    startActivity(intent); finish();
                } else if (itemId == R.id.nav_logout) {
                    Intent intent = new Intent(SellBookActivity.this, LoginActivity.class);
                    startActivity(intent); finish();
                }
                drawerLayout.close();
                return false;
            }
        });
        btnSellBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {handleSellBtn();}
        });
    }

    private void initializeViews()
    {
        dataHandler = new DataHandler(this);
        categoryList = dataHandler.getAllCategory();
        drawerLayout = findViewById(R.id.sidebar_layout);
        btnToggle = findViewById(R.id.btnToggle);
        navigationView = findViewById(R.id.nav_view);
        editTextBookName = findViewById(R.id.editTextBookNameSellPage);
        editTextBookDescription = findViewById(R.id.editTextBookDescriptionSellPage);
        editTextBookPrice = findViewById(R.id.editTextBookPriceSellPage);
        imageButtonBookImage = findViewById(R.id.sellBookImageButton);
        btnSellBook = findViewById(R.id.btnSellBook);
        spinnerBookCategory = findViewById(R.id.spinnerBookCategorySellPage);
    }

    private void initializeSpinnerBookCategory()
    {
        ArrayList<String> categoryNameList = new ArrayList<>();
        ArrayAdapter<String> categoryNameAdapter = null;
        for(CategoryEntity category : categoryList) {
            categoryNameList.add(category.getName());
        }
        categoryNameAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categoryNameList);
        spinnerBookCategory.setAdapter(categoryNameAdapter);
    }

    private class SpinnerEvent implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            CategoryEntity selectedCategory = categoryList.get(position);
            bookEntity.setCategoryId(selectedCategory.getId());
        }
        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {}
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            // Save img into local
            selectedImagePath = saveImageLocally(getRealPathFromURI(imageUri));
            Bitmap bitmap = BitmapFactory.decodeFile(selectedImagePath);
            imageButtonBookImage.setImageBitmap(bitmap);
            imageButtonBookImage.setAlpha(1.0f);
        }
    }

    private void handleSellBtn()
    {
        String bookName = editTextBookName.getText().toString().trim();
        String bookDescription = editTextBookDescription.getText().toString().trim();
        String bookPrice = editTextBookPrice.getText().toString().trim();

        bookEntity.setName(bookName);
        bookEntity.setDescription(bookDescription);
        bookEntity.setPrice(Integer.parseInt(bookPrice));
        bookEntity.setImagePath(selectedImagePath);
        bookEntity.setUserId(dataHandler.getUserByEmail(email).getId());

        long result = dataHandler.sellBook(bookEntity);
        if (result != -1) {
            ShowDialog.showToast(SellBookActivity.this, "Đăng bán sách thành công, vui lòng đợi duyệt");
            Intent intent = new Intent(SellBookActivity.this, HomeActivity.class);
            startActivity(intent); finish();
        } else {
            ShowDialog.showToast(SellBookActivity.this, "Có lỗi xảy ra!");
        }
    }

    /* - - - - <Solve image> - - - - - */
    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 1);
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
            System.out.println(e.getMessage());
        }

        return destFile.getAbsolutePath();
    }
    /* - - - - </Solve image> - - - - - */
}