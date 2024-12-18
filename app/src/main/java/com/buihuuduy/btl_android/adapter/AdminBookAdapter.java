package com.buihuuduy.btl_android.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.buihuuduy.btl_android.DBSQLite.DataHandler;
import com.buihuuduy.btl_android.R;
import com.buihuuduy.btl_android.activity.AdminDetailBook;
import com.buihuuduy.btl_android.entity.BookEntity;
import java.util.List;

public class AdminBookAdapter extends BookAdapter {
    public AdminBookAdapter(List<BookEntity> bookList, DataHandler dataHandler, Context context) {
        super(bookList, dataHandler, context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        BookEntity book = (BookEntity) getItem(position);

        Button btnShowDetail = view.findViewById(R.id.bookItemBtnShowDetail);
        btnShowDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, AdminDetailBook.class);
                intent.putExtra("BOOK_ID", book.getId());
                context.startActivity(intent);
            }
        });

        return view;
    }
}