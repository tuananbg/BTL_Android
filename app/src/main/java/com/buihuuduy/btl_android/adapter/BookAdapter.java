package com.buihuuduy.btl_android.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.buihuuduy.btl_android.DBSQLite.DataHandler;
import com.buihuuduy.btl_android.R;
import com.buihuuduy.btl_android.activity.UserDetailBook;
import com.buihuuduy.btl_android.entity.BookEntity;
import java.io.File;
import java.util.List;
import android.view.LayoutInflater;

public class BookAdapter extends BaseAdapter
{
    TextView textViewBookName, textViewDescription, textViewAuthor, textViewBookPrice;
    ImageView imageViewBook;
    Button btnShowDetail;

    private final List<BookEntity> bookList;
    private final DataHandler dataHandler;
    protected final Context context;

    public BookAdapter(List<BookEntity> bookList, DataHandler dataHandler, Context context) {
        this.bookList = bookList;
        this.dataHandler = dataHandler;
        this.context = context;
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


    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.book_item, parent, false);
        }

        textViewBookName = convertView.findViewById(R.id.bookItemName);
        textViewDescription = convertView.findViewById(R.id.bookItemDescription);
        textViewDescription.setMovementMethod(new ScrollingMovementMethod());
        imageViewBook = convertView.findViewById(R.id.bookItemImage);
        textViewAuthor = convertView.findViewById(R.id.bookItemAuthor);
        textViewBookPrice = convertView.findViewById(R.id.bookItemPrice);
        btnShowDetail = convertView.findViewById(R.id.bookItemBtnShowDetail);

        BookEntity book = bookList.get(position);

        textViewBookName.setText(book.getName());
        textViewDescription.setText(book.getDescription());
        textViewAuthor.setText(book.getUserName());
        textViewBookPrice.setText("Giá : " + book.getPrice() + " VND");

        File imgFile = new File(book.getImagePath());
        if (imgFile.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            imageViewBook.setImageBitmap(bitmap);
        } else {
            imageViewBook.setImageResource(R.drawable.logo);
        }

        btnShowDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("BookAdapter", "Book ID: " + book.getId());
                Intent intent = new Intent(context, UserDetailBook.class);
                intent.putExtra("BOOK_ID_USER", book.getId());
                intent.putExtra("MY_BOOK_ID_USER", 1);
                context.startActivity(intent);
            }
        });

        return convertView;
    }
}