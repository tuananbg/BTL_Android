package com.buihuuduy.btl_android.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.buihuuduy.btl_android.DBSQLite.DataHandler;
import com.buihuuduy.btl_android.R;
import com.buihuuduy.btl_android.entity.BookEntity;
import java.io.File;

public class UserDetailBook extends AppCompatActivity 
{
    private ImageView userBookImage, userDetailBookBackButton;
    private TextView userAuthorName, userBookName, userBookPrice, userBookCategory, userDetailBookContent;
    private DataHandler dataHandler;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_detail_book);

        dataHandler = new DataHandler(this);

        userDetailBookBackButton = findViewById(R.id.userDetailBookBackButton);
        userBookImage = findViewById(R.id.userBookImage);
        userAuthorName = findViewById(R.id.userAuthorName);
        userBookName = findViewById(R.id.userBookName);
        userBookPrice = findViewById(R.id.userBookPrice);
        userDetailBookContent = findViewById(R.id.userDetailBookContent);
        userBookCategory = findViewById(R.id.userDetailBookCategory);

        int bookId = getIntent().getIntExtra("BOOK_ID_USER", -1);
        int index = getIntent().getIntExtra("MY_BOOK_ID_USER", -1);
        BookEntity book = dataHandler.getBookById(bookId);

        userAuthorName.setText(book.getUserName());
        userBookName.setText(book.getName());
        userBookPrice.setText(book.getPrice() + " VND");
        userBookCategory.setText("Thể loại : " + book.getCategoryName());
        if(book.getContent() != null) userDetailBookContent.setText(book.getContent());
        else {
            userDetailBookContent.setText("Không truy cập được nội dung");
            userDetailBookContent.setTypeface(null, Typeface.ITALIC);
        }
        File imgFile = new File(book.getImagePath());
        if (imgFile.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            userBookImage.setImageBitmap(bitmap);
        } else {
            userBookImage.setImageResource(R.drawable.logo);
        }

        userDetailBookBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(index == 1){
                    Intent intent = new Intent(UserDetailBook.this, HomeActivity.class);
                    UserDetailBook.this.startActivity(intent);
                }else{
                    Intent intent = new Intent(UserDetailBook.this, MyBookActivity.class);
                    UserDetailBook.this.startActivity(intent);
                }
            }
        });
    }
}
