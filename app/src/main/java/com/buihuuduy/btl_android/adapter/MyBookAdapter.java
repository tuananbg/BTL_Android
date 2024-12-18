package com.buihuuduy.btl_android.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.LayoutInflater;
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

public class MyBookAdapter extends BaseAdapter
{
    TextView textViewBookName, textViewDescription, textViewAuthor, textViewPrice;
    ImageView imageViewBook;
    View colorIndicator;
    Button btnShowDetail;

    private final List<BookEntity> bookList;
    private final DataHandler dataHandler;
    private final Context context;

    public MyBookAdapter(List<BookEntity> bookList, DataHandler dataHandler, Context context) {
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


    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.book_item, parent, false);
        }

        textViewBookName = convertView.findViewById(R.id.bookItemName);
        textViewDescription = convertView.findViewById(R.id.bookItemDescription);
        imageViewBook = convertView.findViewById(R.id.bookItemImage);
        textViewAuthor = convertView.findViewById(R.id.bookItemAuthor);
        textViewPrice = convertView.findViewById((R.id.bookItemPrice));
        colorIndicator =  convertView.findViewById(R.id.colorIndicator);
        btnShowDetail = convertView.findViewById(R.id.bookItemBtnShowDetail);
        BookEntity book = bookList.get(position);

        Log.e("Book: ", book.toString());

        textViewBookName.setText(book.getName());
        textViewDescription.setText(book.getDescription());
        textViewAuthor.setText(book.getUserName());
        textViewPrice.setText("Price: " + book.getPrice());

        GradientDrawable background = (GradientDrawable) colorIndicator.getBackground();
        if (book.getStatus() == 0) {
            background.setColor(Color.YELLOW); // Thay đổi màu
        } else if (book.getStatus() == 1) {
            background.setColor(Color.GREEN);
        } else {
            background.setColor(Color.RED);
        }

        File imgFile = new File(book.getImagePath());
        if (imgFile.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            imageViewBook.setImageBitmap(bitmap);
        } else {
            imageViewBook.setImageResource(R.drawable.logo); // Thay bằng ảnh mặc định
        }

        // xu ly button
        btnShowDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("BookAdapter", "Book ID: " + book.getId());
                Intent intent = new Intent(context, UserDetailBook.class);
                intent.putExtra("BOOK_ID_USER", book.getId());
                intent.putExtra("MY_BOOK_ID_USER", 2);
                context.startActivity(intent);
            }
        });
        return convertView;
    }
}
