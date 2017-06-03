package com.android.booklisting;

import android.graphics.Bitmap;

/**
 * Created by simon on 02/06/2017.
 */

public class Book {

    private String mTitle;
    private Bitmap mCoverBmp;
    private String mAuthorName;

    public Book (String title, Bitmap coverBmp, String authorName) {
        mTitle = title;
        mCoverBmp = coverBmp;
        mAuthorName = authorName;
    }

    public String getTitle() {
        return mTitle;
    }


    public Bitmap getCoverBmp() {
        return mCoverBmp;
    }


    public String getAuthorName() {
        return mAuthorName;
    }

}
