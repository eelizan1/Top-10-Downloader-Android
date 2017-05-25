package com.example.eeliz_000.top10downloader;

/*
 * Created by eeliz_000 on 4/17/2017.
 *
 * CLASS PARSES AND FORMATS THE DATA FROM THE FEEDENTRY CLASS
 */

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.util.ArrayList;

public class ParseApplication {
    private static final String TAG = "ParseApplications";
    // entry list
    private ArrayList<FeedEntry> applications;

    // constructor
    public ParseApplication() {

        this.applications = new ArrayList<>();
    }

    // fed into the arrayAdapter
    public ArrayList<FeedEntry> getApplications() {

        return applications;
    }

    // builds up our entries
    public boolean parse(String xmlData) {
        // are we at the stage of parsing an entry?
        boolean status = true;
        FeedEntry currentRecord = null;
        boolean inEntry = false;
        String textValue = "";

        // parsing
        try {
            // setting up the XML classes that are apart of java
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new StringReader(xmlData));
            int eventType = xpp.getEventType();
            // while we are not at the end of document  parse tags
            while (eventType != XmlPullParser.END_DOCUMENT) {
                // tell what to parse
                String tagName = xpp.getName();
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        Log.d(TAG, "parse: Starting tag for " + tagName);
                        if ("entry".equalsIgnoreCase(tagName)) {
                            inEntry = true;
                            currentRecord = new FeedEntry();
                        }
                        break;
                    case XmlPullParser.TEXT:
                        textValue = xpp.getText();
                        break;
                    case XmlPullParser.END_TAG:
                        Log.d(TAG, "parse: Ending tag for " + tagName);
                        if (inEntry) {
                            if ("entry".equalsIgnoreCase(tagName)) {
                                applications.add(currentRecord);
                                inEntry = false;
                                // note: make sure values are exactly spelled XML values
                            } else if ("name".equalsIgnoreCase(tagName)) {
                                currentRecord.setName(textValue);
                            } else if ("artist".equalsIgnoreCase(tagName)) {
                                currentRecord.setArtist(textValue);
                            } else if ("releaseDate".equalsIgnoreCase(tagName)) {
                                currentRecord.setReleaseDate(textValue);
                            } else if ("summary".equalsIgnoreCase(tagName)) {
                                currentRecord.setSummary(textValue);
                            } else if ("image".equalsIgnoreCase(tagName)) {
                                currentRecord.setImageURL(textValue);
                            }
                        }
                        break;
                    default:
                        // nothing to do
                }
                eventType = xpp.next();
            }
            // logs the data
//            for (FeedEntry app : applications) {
//                Log.d(TAG, "*******************");
//                Log.d(TAG, app.toString());
//            }
        } catch (Exception e) {
            status = false;
            e.printStackTrace();
        }

        return status;
    }
}
