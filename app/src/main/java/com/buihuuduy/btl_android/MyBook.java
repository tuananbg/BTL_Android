package com.buihuuduy.btl_android;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.buihuuduy.btl_android.DBSQLite.BookSQLHelper;
import com.buihuuduy.btl_android.adapter.BookAdapter;
import com.buihuuduy.btl_android.entity.BookEntity;

import java.util.ArrayList;
import java.util.List;

public class MyBook extends AppCompatActivity {
    private RecyclerView recyclerView;
    private BookSQLHelper databaseHelper;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_book);
        deleteDatabase("books.db");
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        databaseHelper = new BookSQLHelper(this);

        loadBooksFromDatabase();
    }
    private void loadBooksFromDatabase() {
        List<BookEntity> allBooks = databaseHelper.getAllBooks();
        BookAdapter bookAdapter = new BookAdapter(this, allBooks);
        recyclerView.setAdapter(bookAdapter);
    }
}
