package com.example.android.newsapp;

import android.graphics.Bitmap;

import java.text.SimpleDateFormat;
import java.util.Date;

public class News {

    private static String TAG_NAME = News.class.getSimpleName();

    private String mTitle;
    // mDate will be as "2020-06-04T07:00:00Z"
    private String mDate;
    // mPictureUrl will contain the URL to the image
    private String mPictureUrl;
    // mPictureBitmap will contain the image as a Bitmap object
    private Bitmap mPictureBitmap;
    private String mArticleUrl;
    private String mAuthor;

    /**
     * Create a News instance
     *
     * @param title      Title of the instance
     * @param date       Date of publication
     * @param pictureUrl Url of the picture as a String
     * @param articleUrl Url to the article
     */
    public News(String title, String date, String pictureUrl, String articleUrl, String author) {
        mTitle = title;
        mDate = date;
        mPictureUrl = pictureUrl;
        mArticleUrl = articleUrl;
        mAuthor = author;
        mPictureBitmap = null;
    }

    public String getTitle() {
        return mTitle;
    }

    /**
     * Get the date of the News without the time.
     *
     * @return Date only as a String
     */
    public String getDate() {

        // Parse the date to get a Date object
        Date date = parseDate(mDate);
        // Define the date text pattern you want as output
        SimpleDateFormat dateFormatter = new SimpleDateFormat("LLL dd, yyyy");

        return dateFormatter.format(date);
    }

    /**
     * Get the time of publication of the News.
     *
     * @return Time of publication of the News
     */
    public String getHour() {
        // Parse the date to get a Date object
        Date hour = parseDate(mDate);
        // Define the date text pattern you want as output
        SimpleDateFormat dateFormatter = new SimpleDateFormat("h:mm a");

        return dateFormatter.format(hour);
    }

    public String getPictureUrl() {
        return mPictureUrl;
    }

    public Bitmap getPictureBitmap() {
        return mPictureBitmap;
    }

    public String getArticleUrl() {
        return mArticleUrl;
    }

    /**
     * If the author is not mentionned return an empty String
     * @return author of the News
     */
    public String getAuthor() {
        return mAuthor;
    }

    public void setPictureBitmap(Bitmap bitmap) {
        mPictureBitmap = bitmap;
    }

    public static Date parseDate(String dateString) {
        // dateString will be like "2020-06-04T07:00:00Z"
        //TODO: Is there a cleaner/better way to parse the dateString here ? regex ?
        int year = Integer.parseInt(dateString.substring(0, 4)) - 1900;
        int month = Integer.parseInt(dateString.substring(5, 7)) - 1;
        int day = Integer.parseInt(dateString.substring(8, 10));
        int hours = Integer.parseInt(dateString.substring(11, 13));
        int minutes = Integer.parseInt(dateString.substring(14, 16));
        int seconds = Integer.parseInt(dateString.substring(17, 19));

        Date date = new Date(year, month, day, hours, minutes, seconds);

        return date;
    }

}
