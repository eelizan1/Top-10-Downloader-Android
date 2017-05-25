package com.example.eeliz_000.top10downloader;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/*
 * Created by eeliz_000 on 4/17/2017.
 */
public class FeedAdapter extends ArrayAdapter {
    private static final String TAG = "FeedAdapter";
    private final int layoutResource;
    private final LayoutInflater layoutInflater;
    private List<FeedEntry> applications;

    // constructor
    public FeedAdapter(@NonNull Context context, @LayoutRes int resource, List<FeedEntry> applications) {
        super(context, resource);
        this.layoutResource = resource;
        // Instantiates a layout XML file into its corresponding View objects
        this.layoutInflater = LayoutInflater.from(context);
        this.applications = applications;
    }

    /*
        overrides will be called by our ListView
     */

    @Override
    public int getCount() {
        // returns the number of entries in the applications list
        return applications.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        // reuse views if they've scrolled off the screen to save memory
        if (convertView == null) {
            Log.d(TAG, "OS run - getView: called with numm convertView");
            convertView = layoutInflater.inflate(layoutResource, parent, false);
            // retrieve stored id's from viewHolder class
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            Log.d(TAG, "OS run - getView: provided a convertView");
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // get the id's that are apart of the view
//        TextView tvName = (TextView) convertView.findViewById(R.id.tvName);
//        TextView tvArtist = (TextView) convertView.findViewById(R.id.tvArtist);
//        TextView tvSummary = (TextView) convertView.findViewById(R.id.tvSummary);

        FeedEntry currentApp = applications.get(position);

        viewHolder.tvName.setText(currentApp.getName());
        viewHolder.tvArtist.setText(currentApp.getArtist());
        viewHolder.tvSummary.setText(currentApp.getSummary());

        return convertView;
    }

    // class to hold the views so we don't need to search every time
    private class ViewHolder {
        final TextView tvName;
        final TextView tvArtist;
        final TextView tvSummary;

        // constructor finds the id
        ViewHolder(View v) {
            this.tvName = (TextView) v.findViewById(R.id.tvName);
            this.tvArtist = (TextView) v.findViewById(R.id.tvArtist);
            this.tvSummary = (TextView) v.findViewById(R.id.tvSummary);
        }
    }
}
