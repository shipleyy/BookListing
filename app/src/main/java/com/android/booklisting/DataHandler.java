package com.android.booklisting;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;


class DataHandler {

    private static final String LOG_TAG = DataHandler.class.getName();

    private DataHandler() {
    }

    public static ArrayList<Book> getBookData(Context context, String requestUrl) {

        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem with connecting", e);
        }

        // Extract relevant fields from the JSON response and create an {@link Event} object

        return parseBooks(context, jsonResponse);
    }

    private static ArrayList<Book> parseBooks(Context context, String jsonResponse) {

        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(jsonResponse)) {
            return null;
        }
        // Create an empty ArrayList that we can start adding earthquakes to
        ArrayList<Book> books = new ArrayList<>();

        // Try to parse the jsonResponse. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.

        try {

            // Create a JSONObject from the JSON response string
            JSONObject root = new JSONObject(jsonResponse);

            // items array represents a list of books.
            JSONArray items = root.getJSONArray("items");

            // For each books in the array, create an {@link Book} object
            for (int i = 0; i < items.length(); i++) {

                JSONObject f = items.getJSONObject(i);

                JSONObject volumeInfo = f.getJSONObject("volumeInfo");

                String bookTitle = volumeInfo.getString("title");

                // Since there might be more than one author, save the names to a new ArrayList
                List<String> allAuthors = new ArrayList<>();

                // Checks if the book has any authors
                String bookAuthorsComplete;
                if (volumeInfo.has("authors")) {

                    JSONArray bookAuthors = volumeInfo.getJSONArray("authors");
                    for (int j = 0; j < bookAuthors.length(); j++) {
                        String bookAuthorName = bookAuthors.getString(j);
                        allAuthors.add(bookAuthorName);
                    }

                    if (bookAuthors.length() > 1) {
                        // Take all the authors names and put into one long String
                        StringBuilder builder = new StringBuilder();
                        for (String bookAuthorName : allAuthors) {
                            builder.append(bookAuthorName + ", ");
                        }
                        // removing the last comma and space from end of the last name in the list
                        builder.setLength(builder.length() - 2);
                        bookAuthorsComplete = builder.toString();
                    } else bookAuthorsComplete = allAuthors.get(0);
                } else bookAuthorsComplete = context.getString(R.string.no_author);

                JSONObject imageLinks = volumeInfo.getJSONObject("imageLinks");

                String bookCoverUrl = imageLinks.getString("thumbnail");

                //Download the book covers and save them as Bitmap
                Bitmap coverBmp = BitmapFactory.decodeStream((InputStream) new URL(bookCoverUrl).getContent());

                // Save the data in the Book class
                Book newBook = new Book(bookTitle, coverBmp, bookAuthorsComplete);

                // Add a Book to the ArrayList
                books.add(newBook);
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e(LOG_TAG, "Problem parsing the Books JSON results", e);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Return the list of earthquakes
        return books;
    }

    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            Log.v(LOG_TAG, "URL response code: " + urlConnection.getResponseCode());

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;

    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error with creating URL ", e);
        }
        return url;
    }
}
