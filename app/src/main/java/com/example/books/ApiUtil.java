package com.example.books;

import android.graphics.Color;
import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Logger;

public class ApiUtil {


    //we never instantiate this class so we remove the constructor with empty constructor
    private ApiUtil(){}

    private static final String QUERY_PARAMETER_KEY = "q";
    public static final String BASE_API_URL = "https://www.googleapis.com/books/v1/volumes";
    public static final String KEY = "key";
    public static final String API_KEY = "AIzaSyDA7088WAp7PZcFbkijeXvnkQh2F1uq8gA";

    public static URL buildUrl(String title){
//        String fullUrl = BASE_API_URL + "/?q=" + title;

        //the uri.Builder class is generally help us to avoid errors building the uri
        //Uri.parse convert the BASE_API_URL(string) into a uri
        //buildUpon() construct a new builder
        //appendQueryParameter() encodes the key and value and appends the parameter to the query string
        //build() will actually create the URI
        Uri uri = Uri.parse(BASE_API_URL)
                .buildUpon()
                .appendQueryParameter(QUERY_PARAMETER_KEY,title)
                .appendQueryParameter(KEY,API_KEY)
                .build();
        URL url = null;
        try {
//            url = new URL(fullUrl);
            //converting uri into url
            url = new URL(uri.toString());
        }catch (Exception e){
            e.printStackTrace();
        }
        return url;
    }

    public static String getJson(URL url) throws IOException{
        //we connect to the url that's passed
        //NOTE:the urlInstance does not establish the actual network connection on creation.it will happen only when we try to read
        //data from it(url)
        //a connection can throw an IO exception
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            try {
                //once we establish the connection we then read the data
                InputStream stream = connection.getInputStream();
                //as we r reading a JSON,once we have the stream,we need to convert our stream to a string by
                //using Scanner which have the advantage of buffering the data and encoding the character to UTF 16,which is the Android format
                Scanner scanner = new Scanner(stream);
                //the scanner can be used to delimit large pieces of stream into smaller ones. in this case we will set the delimiter to backslash backslash A. Meaning that we want to read everything
                //the backslash A is a pattern which in turns is a regular expression
                scanner.useDelimiter("//A");
                //hasNext return true if there is data and false if not.
                boolean hasData = scanner.hasNext();
                if (hasData){
                    return scanner.next();
                }else {
                    return null;
                }
            }catch (Exception e){
                Log.d("Error",e.toString());
                return null;
            }
            finally {
                connection.disconnect();
            }

    }

    public static ArrayList<Book> getBookFromJson(String json){
        final String ID = "id";
        final String TITLE = "title";
        final String SUBTITLE = "subtitle";
        final String AUTHORS = "authors";
        final String PUBLISHER = "publisher";
        final String PUBLISHED_DATE = "publishedDate";
        final String ITEMS = "items";
        final String VOLUME_INFO = "volumeInfo";


        ArrayList<Book> books = new ArrayList<>();
        try {
            //creating JSON object from the JSON string
            //the json parameter is the string containing the JSON retrieved from the API
            JSONObject jsonBooks = new JSONObject(json);
            //get the array that contain all the books
            JSONArray arrayBooks = jsonBooks.getJSONArray(ITEMS);
            //number of books retrieved
            int numberOfBooks = arrayBooks.length();
            for (int i=0;i<numberOfBooks;i++){
                JSONObject bookJSON = arrayBooks.getJSONObject(i);
                JSONObject volumeInfoJSON = bookJSON.getJSONObject(VOLUME_INFO);
                int authorNum = volumeInfoJSON.getJSONArray(AUTHORS).length();
                String[] authors = new String[authorNum];
                for (int j=0;j<authorNum;j++){
                    authors[j] = volumeInfoJSON.getJSONArray(AUTHORS).get(j).toString();
                }
                Book book = new Book(
                        bookJSON.getString(ID),
                        volumeInfoJSON.getString(TITLE),
                        (volumeInfoJSON.isNull(SUBTITLE)?"":volumeInfoJSON.getString(SUBTITLE)),
                        authors,
                        volumeInfoJSON.getString(PUBLISHER),
                        volumeInfoJSON.getString(PUBLISHED_DATE));
                books.add(book);
            }


        }catch (JSONException e){
            e.printStackTrace();
        }
        return books;
    }
}
