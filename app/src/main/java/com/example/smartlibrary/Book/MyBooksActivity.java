package com.example.smartlibrary.Book;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smartlibrary.Model.Book;
import com.example.smartlibrary.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MyBooksActivity extends AppCompatActivity {

    @BindView(R.id.search_view)
    MaterialSearchView searchView;
    @BindView(R.id.toolbar_container)
    FrameLayout toolbarContainer;
    @BindView(R.id.myBooksRv)
    RecyclerView booksRv;

    ArrayList<Book> list = new ArrayList<>();
    DatabaseReference userRef,bookRef;
    @BindView(R.id.searchPb)
    ProgressBar searchPb;
    String branch,currentUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_books);

        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Books");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ButterKnife.bind(this);

        branch = getIntent().getStringExtra(branch);
        branch = "Religion";

        searchPb.setVisibility(View.VISIBLE);
        booksRv.setVisibility(View.GONE);

        booksRv.setLayoutManager(new LinearLayoutManager(this));

        userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID).child("Issued Books");
        bookRef = FirebaseDatabase.getInstance().getReference().child("All Books");

        searches();

    }

    private void searches() {

        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                search(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                search(newText);
                return true;
            }
        });
    }

    private void search(String newText) {
        ArrayList<Book> myList = new ArrayList<>();

        for (Book object : list) {
            if (object.getName().toLowerCase().contains(newText.toLowerCase())) {
                myList.add(object);
            }

            SearchAdapter adapter = new SearchAdapter(myList);
            booksRv.setAdapter(adapter);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);

        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);

        return true;
    }

    @Override
    public void onStart() {
        super.onStart();

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    list = new ArrayList<>();

                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        Log.d("Books",ds.getKey() + " category");
                        list.add(ds.getValue(Book.class));
                    }

                    searchPb.setVisibility(View.GONE);
                    booksRv.setVisibility(View.VISIBLE);

                    SearchAdapter adapter = new SearchAdapter(list);
                    booksRv.setAdapter(adapter);

                } else{
                    searchPb.setVisibility(View.GONE);
                    Toast.makeText(MyBooksActivity.this, "No Data Available", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchHolder> {
        ArrayList<Book> list;

        public SearchAdapter(ArrayList<Book> list) {
            this.list = list;
        }

        @NonNull
        @Override
        public SearchAdapter.SearchHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.zunit_mybook, parent, false);
            return new SearchAdapter.SearchHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull SearchAdapter.SearchHolder holder, int position) {
            Book book = list.get(position);
            holder.bookNameTv.setText(book.getName());

            holder.issueDateTv.setText("Issued On: " + book.getIssuedOn());
            holder.statusTv.setText(book.getStatus());

            if (book.getStatus().equals("return")){
                holder.returnDateTv.setText("Returned On: " + book.getReturnDate());
                holder.statusTv.setVisibility(View.GONE);
            } else{
                holder.returnDateTv.setText("Return Date: " + book.getReturnDate());
            }
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public class SearchHolder extends RecyclerView.ViewHolder {

            @BindView(R.id.bookNameTv)
            TextView bookNameTv;
            @BindView(R.id.statusTv)
            TextView statusTv;
            @BindView(R.id.issueDateTv)
            TextView issueDateTv;
            @BindView(R.id.returnDateTv)
            TextView returnDateTv;

            public SearchHolder(@NonNull View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }
    }
}
