package com.buihuuduy.btl_android.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.buihuuduy.btl_android.DBSQLite.DataHandler;
import com.buihuuduy.btl_android.R;
import com.buihuuduy.btl_android.entity.BookEntity;
import java.io.File;
import java.util.List;
import android.view.LayoutInflater;

public class BookAdapter extends BaseAdapter
{
    TextView textViewBookName, textViewDescription, textViewAuthor;
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

        BookEntity book = bookList.get(position);

        textViewBookName.setText(book.getName());
        textViewDescription.setText(book.getDescription());
        textViewAuthor.setText(book.getUserName());

        File imgFile = new File(book.getImagePath());
        if (imgFile.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            imageViewBook.setImageBitmap(bitmap);
        } else {
            imageViewBook.setImageResource(R.drawable.logo); // Thay bằng ảnh mặc định
        }

        // xu ly button

        return convertView;
    }
}
