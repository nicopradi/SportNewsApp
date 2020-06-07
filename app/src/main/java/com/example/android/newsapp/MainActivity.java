package com.example.android.newsapp;

import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<News>> {

    public static String GUARDIAN_URL = "https://content.guardianapis.com/search";
    private static NewsAdapter newsAdapter;
    private TextView mEmptyTextView;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get the progress bar and empty screen
        mProgressBar = findViewById(R.id.progress_bar_view);
        mEmptyTextView = findViewById(R.id.empty_text_view);

        // Hook up the ListView with its Adapter
        ListView listView = findViewById(R.id.list_view);
        newsAdapter = new NewsAdapter(this, new ArrayList<News>());
        listView.setAdapter(newsAdapter);

        // Hook up the ListView with its empty screen
        listView.setEmptyView(mEmptyTextView);

        // Check for network connectivity before running the HTTP queries
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
            // Launch the Loader
            // Get the data from the GUARDIAN_URL (news + their thumbnails)
            LoaderManager.getInstance(this).initLoader(0, null, MainActivity.this);
        } else {
            mEmptyTextView.setText(getString(R.string.no_internet));
            mProgressBar.setVisibility(View.INVISIBLE);
        }

        // Add Intent to each item in the ListView
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the news clicked on
                String item_url = newsAdapter.getItem(position).getArticleUrl();
                // Set up the Intent and activate it
                Intent toArticle = new Intent(Intent.ACTION_VIEW);
                toArticle.setData(Uri.parse(item_url));
                if (toArticle.resolveActivity(getPackageManager()) != null) {
                    startActivity(toArticle);
                }
            }
        });
    }

    /**
     * Instantiate and return a new Loader for the given ID.
     *
     * <p>This will always be called from the process's main thread.
     *
     * @param id   The ID whose loader is to be created.
     * @param args Any arguments supplied by the caller.
     * @return Return a new Loader instance that is ready to start loading.
     */
    @NonNull
    @Override
    public Loader<ArrayList<News>> onCreateLoader(int id, @Nullable Bundle args) {

        // Get the user preferences to custom the query
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String favorite_sport_user = sharedPreferences.getString(getString(R.string.preference_favorite_sport_key), "");
        Toast.makeText(this, getString(R.string.toast_favorite_sport) + " " + favorite_sport_user, Toast.LENGTH_SHORT).show();

        // Build the url text query using Uri object
        Uri baseUri = Uri.parse(GUARDIAN_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();
        uriBuilder.appendQueryParameter("api-key", getString(R.string.query_api_key));
        uriBuilder.appendQueryParameter("section", getString(R.string.query_section));
        uriBuilder.appendQueryParameter("page-size", getString(R.string.query_page_size));
        uriBuilder.appendQueryParameter("show-fields", getString(R.string.query_show_fields));
        uriBuilder.appendQueryParameter("order-by", getString(R.string.query_order_by));
        uriBuilder.appendQueryParameter("show-tags", getString(R.string.query_show_tags));
        uriBuilder.appendQueryParameter("q", favorite_sport_user.toLowerCase());

        // Return the loader with the custom query
        return new NewsLoader(this, uriBuilder.toString());
    }

    /**
     * Called when a previously created loader has finished its load.
     * <p>This will always be called from the process's main thread.
     *  @param loader The Loader that has finished.
     *
     * @param data The data generated by the Loader.
     */
    @Override
    public void onLoadFinished(@NonNull Loader<ArrayList<News>> loader, ArrayList<News> data) {
        // Hide the progress bar
        mProgressBar.setVisibility(View.INVISIBLE);

        if (data != null && !data.isEmpty()) {
            newsAdapter.addAll(data);
            newsAdapter.notifyDataSetChanged();
        } else {
            // Nothing to show, what if there is a connection drop during the loading ?
            // it might print "no news" whereas it is actually a connection issue
            mEmptyTextView.setText(getString(R.string.no_content));
        }
    }

    /**
     * Called when a previously created loader is being reset, and thus
     * making its data unavailable.  The application should at this point
     * remove any references it has to the Loader's data.
     *
     * <p>This will always be called from the process's main thread.
     *
     * @param loader The Loader that is being reset.
     */
    @Override
    public void onLoaderReset(@NonNull Loader<ArrayList<News>> loader) {
        // The data is no longer relevant, clear it
        newsAdapter.clear();
        newsAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Go to the activity linked to the menu item
        int itemId = item.getItemId();
        if (itemId == R.id.menu_item_settings) {
            Intent toSettings = new Intent(this, SettingsActivity.class);
            startActivity(toSettings);
        }
        return super.onOptionsItemSelected(item);
    }
}
