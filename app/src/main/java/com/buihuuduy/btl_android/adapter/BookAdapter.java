package com.buihuuduy.btl_android.adapter;

import com.buihuuduy.btl_android.R;
import com.buihuuduy.btl_android.entity.BookEntity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import java.util.List;

public class BookAdapter extends BaseAdapter {
    private Context context;
    private List<BookEntity> bookList;

    public BookAdapter(Context context, List<BookEntity> bookList) {
        this.context = context;
        this.bookList = bookList;
    }

    @Override
    public int getCount() {
        return bookList.size();
    }

    @Override
    public Object getItem(int position) {
        return bookList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.book_item, parent, false);
        }

        BookEntity book = bookList.get(position);

        TextView name = convertView.findViewById(R.id.bookItemName);
        TextView description = convertView.findViewById(R.id.bookItemDescription);
        TextView price = convertView.findViewById(R.id.bookItemPrice);
        TextView author = convertView.findViewById(R.id.bookItemAuthor);
        Button btnDetail = convertView.findViewById(R.id.bookItemBtnShowDetail);

        name.setText(book.getName());
        description.setText(book.getDescription());
        price.setText(String.format("%.2f VND", book.getPrice()));
        author.setText(book.getAuthor());

        btnDetail.setOnClickListener(v -> {
        });

        return convertView;
    }
}
