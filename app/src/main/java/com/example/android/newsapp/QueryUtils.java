package com.example.android.newsapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

public final class QueryUtils {

    private static String TAG_NAME = QueryUtils.class.getSimpleName();

    /**
     * Prevent any instanciation of the class. This class is meant to contains helper methods only.
     * It does not make sense to create instance of it.
     */
    private QueryUtils() {
    }

    /**
     * Create a URL instance out of the URL String
     *
     * @param urlString url used to make the request
     */
    public static URL createURL(String urlString) {
        URL url = null;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            Log.e(TAG_NAME, "URL instance cannot be created, here is the URL string: " + urlString, e);
        }
        return url;
    }

    /**
     * Make the HTTP request given the URL,
     * return the response as a String formatted in JSON format
     *
     * @param url url used to make the request
     * @return Http request response
     */
    public static String makeHttpRequestNews(URL url) throws IOException {
        // Initialize the output
        String jsonResponse = "";

        if (url == null) {
            Log.e(TAG_NAME, "makeHttpRequestNews: The URL passed is null");
            return jsonResponse;
        }

        // Make the HTTP request and return the response as a JSON String.
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;
        try {
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setConnectTimeout(10000);
            httpURLConnection.setReadTimeout(15000);
            httpURLConnection.connect();
            if (httpURLConnection.getResponseCode() == 200) {
                inputStream = httpURLConnection.getInputStream();
                // Create a Json String from an inputStream
                jsonResponse = getJSONStringFromInputStream(inputStream);
            } else {
                Log.e(TAG_NAME, "makeHttpRequestNews: Response code: " + httpURLConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(TAG_NAME, "makeHttpRequestNews: Problem with the HTTP request. " + url.toString(), e);
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }

        return jsonResponse;
    }

    /**
     * Return the bitmap based on the url passed without storing it
     *
     * @param url url used to make the request
     * @return Http request response
     */
    public static Bitmap makeHttpRequestImage(URL url) throws IOException {
        // Initialize the output
        Bitmap image = null;

        if (url == null) {
            Log.e(TAG_NAME, "makeHttpRequestImage: The URL passed is null");
            return image;
        }

        // Make the HTTP request and return the response as a Bitmap.
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;
        try {
            // Get an input stream from the URL request
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setReadTimeout(10000 /* milliseconds */);
            httpURLConnection.setConnectTimeout(15000 /* milliseconds */);
            httpURLConnection.connect();
            if (httpURLConnection.getResponseCode() == 200) {
                inputStream = httpURLConnection.getInputStream();
                // Create a Bitmap from an inputStream
                image = BitmapFactory.decodeStream(inputStream);
            } else {
                Log.e(TAG_NAME, "makeHttpRequestImage: Response code: " + httpURLConnection.getResponseCode());
            }
        } catch (Exception e) {
            Log.e(TAG_NAME, "makeHttpRequestImage: Problem when trying to do the HTTP request. " + url.toString(), e);
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }

        return image;
    }

    /**
     * Convert an input stream into a String
     *
     * @param inputStream inputStream to read from
     * @return String representation of the inputStream
     */
    public static String getJSONStringFromInputStream(InputStream inputStream) {
        // Initialize output
        StringBuilder jsonStringBuilder = new StringBuilder();

        if (inputStream == null) {
            Log.e(TAG_NAME, "getJSONStringFromInputStream: The inputStream is null");
            return jsonStringBuilder.toString();
        }

        // The input stream reader will take a byte and convert it into a character
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        try {
            String line = bufferedReader.readLine();
            while (line != null) {
                jsonStringBuilder.append(line);
                line = bufferedReader.readLine();
            }
        } catch (IOException e) {
            Log.e(TAG_NAME, "Error while trying to read lines from the bufferedReader.", e);
        }

        return jsonStringBuilder.toString();
    }

    /**
     * Given the query response as a JSON String, extract all the information needed to
     * create an ArrayList of News.
     *
     * @param jsonString Response of the request as a String formatted as a JSON
     * @return ArrayList of the News from the JSON passed in
     */
    public static ArrayList<News> getNewsFromJson(String jsonString) {
        ArrayList<News> newsList = new ArrayList<>();
        if (jsonString.isEmpty()) {
            return newsList;
        }

        // Retrieve all the fields needed to construct News instance
        try {
            JSONObject root = new JSONObject(jsonString);
            JSONObject response = root.getJSONObject("response");
            JSONArray results = response.getJSONArray("results");

            for (int i = 0; i < results.length(); i++) {
                JSONObject news = results.getJSONObject(i);
                // For each news, extract the fields we need to create a News object
                String date = news.getString(("webPublicationDate"));
                String articleUrl = news.getString("webUrl");
                String picture = news.getJSONObject("fields").getString("thumbnail");
                String title = news.getJSONObject("fields").getString("headline");
                // Get the author first and last name if mentioned
                String author_name = "";
                JSONArray tags = news.optJSONArray("tags");
                if (tags != null && tags.length() > 0) {
                    String first_name = tags.getJSONObject(0).optString("firstName");
                    String last_name = tags.getJSONObject(0).optString("lastName");
                    first_name = capitalizeWord(first_name);
                    last_name = capitalizeWord(last_name);
                    if (first_name.isEmpty() && last_name.isEmpty()){
                        author_name = "";
                    } else if (last_name.isEmpty()) {
                        author_name = first_name;
                    }
                    else {
                        author_name = last_name + " " + first_name;
                    }
                }
                // Add the news to the News list
                newsList.add(new News(title, date, picture, articleUrl, author_name));
            }

        } catch (JSONException e) {
            Log.e(TAG_NAME, "Error when trying to parse the JSON String.", e);
        }

        return newsList;

    }

    public static String capitalizeWord(String word){

        if (word != null && !word.isEmpty()) {
            if (word.length() > 1) {
                word = word.substring(0,1).toUpperCase() + word.substring(1);
            }
            else {
                word = word.substring(0,1).toUpperCase();
            }
        }
        return word;

    }


}
