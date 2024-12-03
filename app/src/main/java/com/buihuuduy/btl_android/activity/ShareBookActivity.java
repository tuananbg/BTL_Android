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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import com.buihuuduy.btl_android.DBSQLite.DataHandler;
import com.buihuuduy.btl_android.R;
import com.buihuuduy.btl_android.common.ShowDialog;
import com.buihuuduy.btl_android.entity.BookEntity;
import com.buihuuduy.btl_android.entity.CategoryEntity;
import com.google.android.material.navigation.NavigationView;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ShareBookActivity extends AppCompatActivity
{
    // Declare menu component
    private DrawerLayout drawerLayout;
    private ImageButton btnToggle;
    private NavigationView navigationView;

    // Declare page component
    private EditText editTextBookName, editTextBookDescription, editTextBookContent;
    private Button btnShareBook, btnFile;
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
        setContentView(R.layout.sharebook_sidebar);

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
            public void onClick(View view) {handleShareBtn();}
        });
        btnFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { handleFileBtn(); }
        });
    }

    private void initializeViews()
    {
        dataHandler = new DataHandler(this);
        categoryList = dataHandler.getAllCategory();
        drawerLayout = findViewById(R.id.sidebar_layout);
        btnToggle = findViewById(R.id.btnToggle);
        navigationView = findViewById(R.id.nav_view);
        editTextBookName = findViewById(R.id.editTextBookNameSharePage);
        editTextBookDescription = findViewById(R.id.editTextBookDescriptionSharePage);
        editTextBookContent = findViewById(R.id.editTextBookContent);
        imageButtonBookImage = findViewById(R.id.shareBookImageButton);
        btnShareBook = findViewById(R.id.btnShareBook);
        btnFile = findViewById(R.id.btnFileSharePage);
        spinnerBookCategory = findViewById(R.id.spinnerBookCategorySharePage);
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

    private void handleFileBtn()
    {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intent, "Chọn file"), 2);
    }

    private void handleShareBtn()
    {
        String bookName = editTextBookName.getText().toString().trim();
        String bookDescription = editTextBookDescription.getText().toString().trim();
        String bookContent = editTextBookContent.getText().toString().trim();

        bookEntity.setName(bookName);
        bookEntity.setDescription(bookDescription);
        bookEntity.setContent(bookContent);
        bookEntity.setImagePath(selectedImagePath);
        bookEntity.setUserId(dataHandler.getUserByEmail(email).getId());

        long result = dataHandler.shareBook(bookEntity);
        if (result != -1) {
            ShowDialog.showToast(ShareBookActivity.this, "Chia sẻ sách thành công!");
        } else {
            ShowDialog.showToast(ShareBookActivity.this, "Có lỗi xảy ra!");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            if(requestCode == 1) {
                // Save img into local
                selectedImagePath = saveImageLocally(getRealPathFromURI(imageUri));
                Bitmap bitmap = BitmapFactory.decodeFile(selectedImagePath);
                imageButtonBookImage.setImageBitmap(bitmap);
                imageButtonBookImage.setAlpha(1.0f);
            }
            else if(requestCode == 2) {
                Uri fileUri = data.getData();
                if (fileUri != null) {
                    readTextFile(fileUri);
                }
            }
        }
    }

    /* - - - - <Solve file> - - - - - */
    private void readTextFile(Uri fileUri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(fileUri);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            reader.close();
            editTextBookContent.setText(stringBuilder.toString()); // Hiển thị nội dung vào EditText
        } catch (IOException e) {
            System.out.println(e.getMessage());
            ShowDialog.showToast(ShareBookActivity.this, "Không thể đọc file TXT!");
        }
    }
    /* - - - - </Solve file> - - - - - */

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

