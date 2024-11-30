package com.buihuuduy.btl_android.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;
import android.view.View;
import androidx.recyclerview.widget.RecyclerView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.buihuuduy.btl_android.R;
import com.buihuuduy.btl_android.entity.BookEntity;

import java.util.List;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {
    private Context context;
    private List<BookEntity> bookList;

    public BookAdapter(Context context, List<BookEntity> bookList) {
        this.context = context;
        this.bookList = bookList;
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_book, parent, false);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        BookEntity book = bookList.get(position);
        int status = book.getStatus();
        switch (status){
            case 0:
                holder.colorIndicator.setBackgroundColor(Color.GREEN);
                break;
            case 1:
                holder.colorIndicator.setBackgroundColor(Color.YELLOW);
                break;
            case 2:
                holder.colorIndicator.setBackgroundColor(Color.RED);
                break;
            default:
                holder.colorIndicator.setBackgroundColor(Color.GREEN);
                break;
        }
        holder.bookName.setText(book.getName());
        holder.bookAuthor.setText(book.getAuthor());
        holder.bookPrice.setText(String.format("%.0f VND", book.getPrice()));
        holder.showDetailButton.setOnClickListener(v -> {
            // Xử lý sự kiện khi nhấn "Show Detail"
        });
        // Load ảnh (nếu dùng thư viện như Glide hoặc Picasso)
    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }

    public static class BookViewHolder extends RecyclerView.ViewHolder {
        TextView bookName, bookAuthor, bookPrice;
        ImageView bookImage;
        Button showDetailButton;
        View colorIndicator;

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            bookName = itemView.findViewById(R.id.bookName);
            bookAuthor = itemView.findViewById(R.id.bookAuthor);
            bookPrice = itemView.findViewById(R.id.bookPrice);
            bookImage = itemView.findViewById(R.id.bookImage);
            showDetailButton = itemView.findViewById(R.id.showDetailButton);
            colorIndicator = itemView.findViewById(R.id.colorIndicator);
        }
    }
}

