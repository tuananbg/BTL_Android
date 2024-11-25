package com.buihuuduy.btl_android.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.buihuuduy.btl_android.DBSQLite.TestDatabaseHelper;
import com.buihuuduy.btl_android.R;
import com.buihuuduy.btl_android.entity.Book;

import java.io.File;
import java.util.List;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {

    private final List<Book> bookList;
    private final TestDatabaseHelper databaseHelper;
    private final Context context;

    public BookAdapter(List<Book> bookList, TestDatabaseHelper databaseHelper, Context context) {
        this.bookList = bookList;
        this.databaseHelper = databaseHelper;
        this.context = context;
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book, parent, false);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        Book book = bookList.get(position);

        holder.textViewTitle.setText(book.getTitle());
        holder.textViewAuthor.setText(book.getAuthor());

        File imgFile = new File(book.getCoverPath());
        if (imgFile.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            holder.imageViewBookCover.setImageBitmap(bitmap);
        }

        holder.btnDeleteBook.setOnClickListener(v -> {
            int result = databaseHelper.deleteBook(book.getId());
            if (result > 0) {
                Toast.makeText(context, "Xóa sách thành công!", Toast.LENGTH_SHORT).show();
                bookList.remove(position);
                notifyDataSetChanged();
            } else {
                Toast.makeText(context, "Có lỗi xảy ra!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }

    public static class BookViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTitle, textViewAuthor;
        ImageView imageViewBookCover;
        Button btnDeleteBook;

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewAuthor = itemView.findViewById(R.id.textViewAuthor);
            imageViewBookCover = itemView.findViewById(R.id.imageViewBookCover);
            btnDeleteBook = itemView.findViewById(R.id.btnDeleteBook);
        }
    }
}
