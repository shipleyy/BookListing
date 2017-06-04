package com.android.booklisting;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;


class BookAdapter extends ArrayAdapter<Book> {

    public BookAdapter(@NonNull Context context, @NonNull ArrayList<Book> objects) {
        super(context, 0, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;

        // Uses the ViewHolder pattern to optimize amounts of Views reused
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);
            holder = new ViewHolder();
            convertView.setTag(holder);
            holder.bookAuthor = (TextView) convertView.findViewById(R.id.tv_author);
            holder.bookTitle = (TextView) convertView.findViewById(R.id.tv_title);
            holder.bookCover = (ImageView) convertView.findViewById(R.id.iv_cover);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // Finds the current book in the ArrayList
        Book currentBook = getItem(position);

        // Display the information from the current book in the list
        assert currentBook != null;
        holder.bookAuthor.setText(currentBook.getAuthorName());
        holder.bookTitle.setText(currentBook.getTitle());
        holder.bookCover.setImageBitmap(currentBook.getCoverBmp());

        // returns the populated view
        return convertView;
    }

    static class ViewHolder {
        private TextView bookAuthor;
        private TextView bookTitle;
        private ImageView bookCover;
    }
}
