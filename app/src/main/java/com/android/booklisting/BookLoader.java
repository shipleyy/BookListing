package com.android.booklisting;

import android.content.Context;
import java.util.ArrayList;

public class BookLoader extends android.content.AsyncTaskLoader<ArrayList<Book>> {

    private String apiUrl;

    public BookLoader(Context context, String url) {
        super(context);
        apiUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public ArrayList<Book> loadInBackground() {
        if (apiUrl == null) {
            return null;
        }
        ArrayList<Book> books = DataHandler.getBookData(apiUrl);
        return books;
    }
}
