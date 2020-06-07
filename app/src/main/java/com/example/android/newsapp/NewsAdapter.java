package com.example.android.newsapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;


public class NewsAdapter extends ArrayAdapter<News> {

    // We need a Context to inflate the View hierarchy
    private Context mContext;
    // Source of data to use to customize the layout
    private ArrayList<News> mSource;

    /**
     * Create a NewsAdapter instance
     *
     * @param context Context to inflate the item layout
     * @param source  Data to display
     */
    public NewsAdapter(Context context, ArrayList<News> source) {
        // Pass 0 as the ressouce since we are going to inflate the View ourself.
        super(context, 0, source);
        mContext = context;
        mSource = source;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        // Inflate the layout ONLY if no recycled view is passed
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item, parent, false);
        }

        // Get the News at the position given
        News news = mSource.get(position);

        // Customize the layout views based on the current news
        // PICTURE
        ImageView imageView = convertView.findViewById(R.id.list_item_image_view);
        imageView.setImageBitmap(news.getPictureBitmap());
        // TITLE
        TextView titleView = convertView.findViewById(R.id.list_item_title_view);
        titleView.setText(news.getTitle());
        // AUTHOR
        TextView authorView = convertView.findViewById(R.id.list_item_author_view);
        String author = news.getAuthor();
        // Hide the author view if no author is mentionned
        if (author.isEmpty()) {
            authorView.setVisibility(View.GONE);
        } else {
            authorView.setText(mContext.getResources().getString(R.string.author) + " " + news.getAuthor());
            authorView.setVisibility(View.VISIBLE);
        }

        // DATE
        TextView dateView = convertView.findViewById(R.id.list_item_date_view);
        dateView.setText(news.getDate());
        // HOUR
        TextView hourView = convertView.findViewById(R.id.list_item_hour_view);
        hourView.setText(news.getHour());

        return convertView;
    }
}
