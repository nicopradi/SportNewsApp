package com.example.android.newsapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class NewsLoader extends AsyncTaskLoader<ArrayList<News>> {

    private static String TAG_NAME = NewsLoader.class.getSimpleName();
    private String mUrl;
    // Keep track of whether the Loader has already loaded data or not.
    private boolean mLoaded = false;

    /**
     * Create a NewsLoader instance
     *
     * @param context used to retrieve the application context.
     * @param url     url for the request
     */
    public NewsLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    /**
     * Subclasses must implement this to take care of loading their data,
     * as per {@link #startLoading()}.  This is not called by clients directly,
     * but as a result of a call to {@link #startLoading()}.
     * This will always be called from the process's main thread.
     */
    @Override
    protected void onStartLoading() {
        // Make the HTTP request only if you have not done it before
        //TODO: Is there a better way to check when a forceLoad() is needed ?
        if (mLoaded == false) {
            Log.e(TAG_NAME, "mLoaded is false");
            forceLoad();
            mLoaded = true;
        }
    }

    /**
     * Called on a worker thread to perform the actual load and to return
     * the result of the load operation.
     */
    @Nullable
    @Override
    public ArrayList<News> loadInBackground() {

        // 1 - Retrieve the news from the URL
        // Convert the URL String as URL object
        URL url = QueryUtils.createURL(mUrl);
        // Make the HTTP request to get the news
        String jsonResponse = null;
        try {
            // URL -> JSON String
            jsonResponse = QueryUtils.makeHttpRequestNews(url);
        } catch (IOException e) {
            Log.e(TAG_NAME, "QueryUtils.makeHttpRequestNews failed", e);
        }
        // JSON String -> ArrayList<News>
        ArrayList<News> news = QueryUtils.getNewsFromJson(jsonResponse);

        // 2 - For each news, make a new HTTP request to get their thumbnail image
        try {
            for (int i = 0; i < news.size(); i++) {
                // Convert the URL String as URL object
                URL url_thumbnail = QueryUtils.createURL(news.get(i).getPictureUrl());
                // Make the HTTP request to get the thumbnails images
                Bitmap bitmap = QueryUtils.makeHttpRequestImage(url_thumbnail);
                // Update each news with their corresponding thumbnail image
                news.get(i).setPictureBitmap(bitmap);
            }
        } catch (IOException e) {
            Log.e(TAG_NAME, "QueryUtils.makeHttpRequestImage failed", e);
        }
        return news;
    }
}
