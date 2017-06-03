package com.android.booklisting;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;

import static android.view.View.GONE;

public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<ArrayList<Book>> {

    // The loader ID
    private static final int BOOK_LOADER_ID = 1;
    // The first part of the API URL
    private static final String GOOGLE_API_QUERY_START = "https://www.googleapis.com/books/v1/volumes?q=";
    // The part of the URL after the query text
    private static final String GOOGLE_API_QUERY_END = "&maxResults=10&langRestrict=en&key=AIzaSyDnGMJHBQ32dYQ4pIhtdS_xf1-swM5-zeg";
    // The complete API URL unique for each query
    String apiUrl;
    // Has internet connectivity
    boolean isConnected;
    // Declares the adapter
    private BookAdapter mAdapter;
    // Declares the progressBar
    ProgressBar progressBar;
    // Declares the TextViews
    TextView searchResultsHeader;
    TextView noItems;
    // Declare the ListView
    ListView listView;
    // Declaring a boolean to check if this is a first run
    boolean firstRun;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firstRun = true;
        // Finding and declaring all the Views here
        // Declares the search text field
        final EditText searchBox = (EditText) findViewById(R.id.et_search);
        // Declares the search button
        Button searchBtn = (Button) findViewById(R.id.btn_search);
        // Declares the ListView
        listView = (ListView) findViewById(R.id.list);
        // Finds the TextViews
        searchResultsHeader = (TextView) findViewById(R.id.tv_search_result);
        noItems = (TextView) findViewById(R.id.tv_no_items);
        // Finds the progressBar in the layout
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        // Create a new {@link ArrayAdapter} of books
        mAdapter = new BookAdapter(this, new ArrayList<Book>());

        listView.setAdapter(mAdapter);

        // Use LoaderManager to make sure AsyncTask is not recreated if activity is stopped
        final LoaderManager loaderManager = getLoaderManager();

        // Start loading the information in a background task
        loaderManager.initLoader(BOOK_LOADER_ID, null, this);

        // If the activity is just created, show TextView with description
        if (firstRun == true) {
            noItems.setText(R.string.first_run);
            noItems.setVisibility(View.VISIBLE);
        } else {
            noItems.setVisibility(View.GONE);
        }

        // Setting what happens when the Search button is clicked
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // After this point if the boolean is called, it is no longer a first run
                firstRun = false;
                // Start by getting the query from the EditText and save it as a String
                String searchQuery = searchBox.getText().toString();

                // Hide the virtual keyboard since it is no longer being used
                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);

                // Checks for network connectivity
                ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

                //Check if there is internet connection. If not, no need to do the background task
                if (isConnected) {

                    // Use the search searchQuery String to complete the API url
                    apiUrl = GOOGLE_API_QUERY_START + searchQuery + GOOGLE_API_QUERY_END;

                    noItems.setVisibility(View.GONE);
                    // While the background task is loading, display the progressBar
                    progressBar.setVisibility(View.VISIBLE);
                    // Display the header above the ListView showing what the search query was
                    searchResultsHeader.setText(R.string.tv_results);
                    searchResultsHeader.append(" " + searchQuery);

                    loaderManager.restartLoader(BOOK_LOADER_ID, null, MainActivity.this);
                } else {
                    Toast.makeText(MainActivity.this, R.string.no_internet,
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    @Override
    public Loader<ArrayList<Book>> onCreateLoader(int id, Bundle args) {
        return new BookLoader(this, apiUrl);
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Book>> loader, ArrayList<Book> data) {
        progressBar.setVisibility(GONE);
        mAdapter.clear();

        if (data != null && !data.isEmpty()) {
            mAdapter.addAll(data);
        } else if (firstRun == false) {
            noItems.setText(R.string.tv_no_items);
            noItems.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Book>> loader) {
        mAdapter.clear();
    }
}
