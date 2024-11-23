package com.buihuuduy.btl_android;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.buihuuduy.btl_android.DBSQLite.DataHandler;
import com.buihuuduy.btl_android.adapter.BookAdapter;
import com.buihuuduy.btl_android.entity.BookEntity;

import java.util.ArrayList;
import java.util.List;

public class UserActivity extends AppCompatActivity {
    private ListView listViewBooks;
    private BookAdapter bookAdapter;
    private DataHandler dataHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        listViewBooks = findViewById(R.id.homePageListViewBooks);

        dataHandler = new DataHandler(this);

//        List<BookEntity> bookList = dataHandler.getAllBooks();

        List<BookEntity> bookList = new ArrayList<>();
        bookList.add(new BookEntity(1, "Toán", "Sách toán và những công thức bổ ích", 65.7, "Hoàng"));
        bookList.add(new BookEntity(7, "Văn", "Văn và những câu chuyện cổ tích", 834.8, "Quốc"));
        bookList.add(new BookEntity(5, "Anh", "Hello World", 134.54, "Việt"));

        bookAdapter = new BookAdapter(this, bookList);
        listViewBooks.setAdapter(bookAdapter);
    }
}
