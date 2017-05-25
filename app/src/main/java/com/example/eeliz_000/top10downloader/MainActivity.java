package com.example.eeliz_000.top10downloader;

/*
    MAIN CLASS INTERFACE
    WHAT THIS APPLICATION DOES:
        USES AN ASYNCTASK TO DOWNLOAD DATA WITHOUT BLOCKING THE MAIN UI THREAD
        USES THE BUILT IN HTTP CLASSES TO DOWNLOAD DATA FROM THE INTERNET
        USES SUPPLIED XML PARSER CLASSES TO PARSE THE XML FROM THE DOWNLOADED DATA TO STORE THE DATA WE WANTED INTO OBJECTS
        USES LISTVIEW WIDGET TO VIEW TEH DATA AND CONNECTS TO  OUR CUSTOM ADAPTER TO CONTROL HOW OUR DATA IS BEING DISPLAYED
        USES MENU AND SUB MENUS AND CODE THAT RESPONDS TO THE DIFFERENT ITEMS SELECTED
 */

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.StringBuilderPrinter;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyStore;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private ListView listApps;
    // limit for sub menu
    // using %d as the variable for the limit categaory
    private String feedUrl = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=%d/xml";
    private int feedLimit = 10;
    // for refresh
    private String feedCachedUrl = "INVALDIATED";
    public static final String STATE_URL = "feedUrl";
    public static final String STATE_LIMIT = "feedLimit";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "OS read - onCreate: onCreate Start");
        listApps = (ListView) findViewById(R.id.xmlListView);

        // create an instance of the DownloadData class and execute

        // for phone rotation on refresh
        if(savedInstanceState != null) {
            feedUrl = savedInstanceState.getString(STATE_URL);
            feedLimit = savedInstanceState.getInt(STATE_LIMIT);
        }
//        DownloadData downloadData = new DownloadData();
        /*
            note: once execute(), the asynchronous task runs through onPostExecute() and doInBackground()
         */
        Log.d(TAG, "OS read - onCreate: Starting downloadURL");
        // input url link with feeLimit of value 10
        downloadUrl(String.format(feedUrl, feedLimit));
        Log.d(TAG, "OS read - onCreate: done");
    }

    /*
        DISPLAY MENU
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
       getMenuInflater().inflate(R.menu.feeds_menu, menu);
        if(feedLimit == 10) {
            menu.findItem(R.id.mnu10).setChecked(true);
        } else {
            menu.findItem(R.id.mnu25).setChecked(true);
        }
        return true;
    }

    // selects menu item
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id) {
            case R.id.mnuFree:
                    feedUrl = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=%d/xml";
                break;
            case R.id.mnuPaid:
                feedUrl = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/toppaidapplications/limit=%d/xml";
                break;
            case R.id.mnuSongs:
                feedUrl = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topsongs/limit=%d/xml";
                break;
            // if sub menu is clicked - toggle between the top 10 and top 25
            case R.id.mnu10:
            case R.id.mnu25:
                    if(!item.isChecked()) {
                        item.setChecked(true);
                        // to determine the limit
                        feedLimit = 35 - feedLimit;
                        Log.d(TAG, "OS read - onOptionsItemSelected: " + item.getTitle() + " setting feedLimit to " + feedLimit);
                    } else {
                        Log.d(TAG, "OS read - onOptionsItemSelected: " + item.getTitle() + " feedLimit unchanged");
                    }
                    break;
            case R.id.mnuRefresh:
            feedCachedUrl = "INVALIDATED";
            break;
            default:
                // for sub menu
                return super.onOptionsItemSelected(item);
        }
        downloadUrl(String.format(feedUrl, feedLimit));
        return true;
    }

    // for phone rotation - saves feed url and feed limit
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(STATE_URL, feedUrl);
        outState.putInt(STATE_LIMIT, feedLimit);
        super.onSaveInstanceState(outState);
    }

    private void downloadUrl(String feedUrl) {
        if(!feedUrl.equalsIgnoreCase(feedCachedUrl)) {
            // create an instance of the DownloadData class and execute
            Log.d(TAG, "OS read - DownloadUrl: AsyncTask Started");
            DownloadData downloadData = new DownloadData();
        /*
            note: once execute(), the asynchronous task runs through onPostExecute() and doInBackground()
         */
            // execute will call the doInBackground method
            downloadData.execute(feedUrl);
            feedCachedUrl = feedUrl;
            Log.d(TAG, "OS read - DownloadUrl: downloadURL done");
        } else {
            Log.d(TAG, "OS read - downloadUrl: URL not changed");
        }

    }
    // the Async Class to perform the asynchronous processing
    // takes three parameters
    // String - RSS feed
    // Void - not using the progress bar (downloading indication)
    // String - type of the data we want to get back
    private class DownloadData extends AsyncTask<String, Void, String> {
        private static final String TAG = "DownloadData";

        // runs on the main UI thread after the background execution finished
        // will receive returned value from doInBackground
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            // logs the parameter that is being passed in onPostexecute
            Log.d(TAG, "OS read - onPostExecute: parameter has executed the XML ");
            ParseApplication parseApplication = new ParseApplication();
            // s is the XML
            parseApplication.parse(s);

            // links listview to our XML data
//            ArrayAdapter<FeedEntry> arrayAdapter = new ArrayAdapter<FeedEntry>(
//                    MainActivity.this, R.layout.list_item, parseApplication.getApplications());
//            listApps.setAdapter(arrayAdapter);

            FeedAdapter feedAdapter = new FeedAdapter(MainActivity.this, R.layout.list_record, parseApplication.getApplications());
            listApps.setAdapter(feedAdapter);
        }

        // the main method to run the background task when downloadData.execute()
        // it will return data to the onPostExectute
        @Override
        protected String doInBackground(String... strings) {
            // logs the parameter that is being passed in background
            Log.d(TAG, "OS read - doInBackground: Running background with: " + strings[0]);
            // downloads the rss and returns the feed containing the XML
            String rssFeed = downloadXML(strings[0]);
            if (rssFeed == null) {
                Log.e(TAG, "OS read - doInBackground: Error downloading");
            }
            Log.d(TAG, "OS read - doInBackground: Background Done / AsyncTask Done");
            return rssFeed;
        }

        // downloadXML method to read from XML file and return a string data
        private String downloadXML(String urlPath) {
            // appending to a string as reading chars from an input stream 
            StringBuilder xmlResult = new StringBuilder();
            /*
                Set up connection to internet and the reader to grab XML data
             */
            try {
                // try url 
                URL url = new URL(urlPath);
                // try internet connection
                // note: declare internet permission in manifest
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                int response = connection.getResponseCode();
                Log.d(TAG, "OS read - downloadXML: The response code was " + response);

                // after url and connection checks,  create IO streams
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                // read data and store in array
                int charsRead;
                char[] inputBuffer = new char[500];
                while (true) {
                    charsRead = reader.read(inputBuffer);
                    // if at end of data streak break out of hte loop
                    if (charsRead < 0) {
                        break;
                    }
                    if (charsRead > 0) {
                        xmlResult.append(String.copyValueOf(inputBuffer, 0, charsRead));
                    }
                }
                reader.close();
                // return XML data
                return xmlResult.toString();
            } catch (MalformedURLException e) {
                // catch invalid URL's 
                Log.e(TAG, "downloadXML: downloadXML: Invalid URL" + e.getMessage());
            } catch (IOException e) {
                // catch bad connection
                Log.e(TAG, "downloadXML: IO Exception reading data: " + e.getMessage());
            } catch (SecurityException e) {
                // catch security exception
                Log.e(TAG, "downloadXML: Security Exception. Needs permission?  " + e.getMessage());
            }
            // if errors return null
            // will be used in doInBackground
            return null;
        }
    }
}

