package com.example.books;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class BooksAdapter extends RecyclerView.Adapter<BooksAdapter.BookViewHolder> {
    ArrayList<Book> books;
    private final Context mContext;
    private final LayoutInflater mLayoutInflater;

    public BooksAdapter(Context context,ArrayList<Book> books){
        this.books = books;
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
    }


    public class BookViewHolder extends RecyclerView.ViewHolder{
        TextView tvTitle;
        TextView tvAuthors;
        TextView tvDate;
        TextView tvPublisher;


        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvAuthors = itemView.findViewById(R.id.tvAuthors);
            tvDate = itemView.findViewById(R.id.tvPublishedDate);
            tvPublisher = itemView.findViewById(R.id.tvPublisher);
        }
    }
    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mLayoutInflater
                .inflate(R.layout.book_list_item,parent,false);

        return new BookViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        Book book = books.get(position);
//        holder.bind(book);
       holder.tvTitle.setText(book.title);
        String authors = "";
        int i = 0;
        for (String author:book.authors){
            authors+=author;
            i++;
            if (i<book.authors.length){
                authors += ", ";
            }
        }
        holder.tvAuthors.setText(authors);
        holder.tvDate.setText(book.publishedDate);
        holder.tvPublisher.setText(book.publisher);
    }

    @Override
    public int getItemCount() {
        return books.size();
    }
}
