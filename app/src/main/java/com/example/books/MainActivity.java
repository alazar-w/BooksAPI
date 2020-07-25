package com.example.books;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuCompat;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements androidx.appcompat.widget.SearchView.OnQueryTextListener {
    private ProgressBar mLoadingProgress;
    private RecyclerView rvBooks;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLoadingProgress = findViewById(R.id.pb_loading);
        rvBooks = findViewById(R.id.rv_books);
        try {
            URL bookUrl = ApiUtil.buildUrl("french");
            new BooksQueryTask().execute(bookUrl);

        } catch (Exception e) {
            Log.d("error",e.getMessage());
        }

        LinearLayoutManager booksLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false
        );
        rvBooks.setLayoutManager(booksLayoutManager);
    }


    public class BooksQueryTask extends AsyncTask<URL,Void,String>{
        @Override
        protected String doInBackground(URL... urls) {
            URL searchUrl = urls[0];
            String result = null;
            try {
                result = ApiUtil.getJson(searchUrl);
            }catch (IOException e){
                Log.e("Error",e.getMessage());
            }
            return result;
        }

        @Override
        protected void onPostExecute(String jsonResult) {
            TextView tvError = findViewById(R.id.tv_error);
            mLoadingProgress.setVisibility(View.INVISIBLE);
            if (jsonResult == null){
                rvBooks.setVisibility(View.INVISIBLE);
                tvError.setVisibility(View.VISIBLE);
            }else {
                rvBooks.setVisibility(View.VISIBLE);
                tvError.setVisibility(View.INVISIBLE);
            }
            ArrayList<Book> books = ApiUtil.getBookFromJson(jsonResult);
//            String resultString = "";
//            for (Book book:books){
//                resultString = resultString + book.title + "\n" +
//                        book.publishedDate + "\n\n";
//            }
            BooksAdapter adapter = new BooksAdapter(MainActivity.this,books);
            rvBooks.setAdapter(adapter);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingProgress.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.book_list_menu,menu);
        final MenuItem searchItem = menu.findItem(R.id.action_search);
//        final SearchView searchView = (SearchView)MenuItemCompat.getActionView(searchItem);
        final androidx.appcompat.widget.SearchView searchView = (androidx.appcompat.widget.SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(this);
        return true;
    }
    @Override
    public boolean onQueryTextSubmit(String query) {
        try {
            URL bookUrl = ApiUtil.buildUrl(query);
            new BooksQueryTask().execute(bookUrl);
        }catch (Exception e){
            Log.d("error",e.getMessage());
        }
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

}
