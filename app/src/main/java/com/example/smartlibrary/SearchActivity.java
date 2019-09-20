package com.example.smartlibrary;

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

import com.example.smartlibrary.Book.BooksActivity;
import com.example.smartlibrary.Book.IssueActivity;
import com.example.smartlibrary.Book.ScannedBarCodeActivity;
import com.example.smartlibrary.Model.Book;
import com.example.smartlibrary.Model.Search;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchActivity extends AppCompatActivity {

    @BindView(R.id.search_view)
    MaterialSearchView searchView;
    @BindView(R.id.toolbar_container)
    FrameLayout toolbarContainer;
    @BindView(R.id.searchRv)
    RecyclerView searchRv;

    ArrayList<Book> list = new ArrayList<>();
    DatabaseReference bookRef;
    @BindView(R.id.searchPb)
    ProgressBar searchPb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("All Books");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        searchView.showSearch(false);

        searchPb.setVisibility(View.VISIBLE);
        searchRv.setVisibility(View.GONE);

        searchRv.setLayoutManager(new LinearLayoutManager(this));

        bookRef = FirebaseDatabase.getInstance().getReference().child("AllBooks");

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);

        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);

        return true;
    }

    private void search(String newText) {
        ArrayList<Book> myList = new ArrayList<>();

        for (Book object : list) {
            String search = object.getName() + object.getAuthor() + object.getPublication();
            if (search.toLowerCase().contains(newText.toLowerCase())) {
                myList.add(object);
            }

            SearchAdapter adapter = new SearchAdapter(myList);
            searchRv.setAdapter(adapter);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        bookRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    list = new ArrayList<>();

                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        Log.d("Books",ds.getKey() + " category");
                        list.add(ds.getValue(Book.class));

                    }

                    searchPb.setVisibility(View.GONE);
                    searchRv.setVisibility(View.VISIBLE);

                    SearchAdapter adapter = new SearchAdapter(list);
                    searchRv.setAdapter(adapter);

                } else{
                    searchPb.setVisibility(View.GONE);
                    Toast.makeText(SearchActivity.this, "No Data Available", Toast.LENGTH_SHORT).show();
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
        public SearchHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.zlayout_book, parent, false);
            return new SearchHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull SearchHolder holder, int position) {

            Book book = list.get(position);
            holder.authorTv.setText("Author : " + book.getAuthor());
            holder.bookNameTv.setText(book.getName());
            holder.publicationTv.setText("Pub : " + book.getPublication());

            String presence = book.getPresence();
            holder.statusTv.setText(book.getStatus());

            if (presence!=null){
                if (presence.equals("absent")){
                    holder.locationTv.setText("Location : Currently Misplaced");

                } else if (presence.equals("present")){
                    holder.locationTv.setText("Location : " + book.getLocation());
                }
            }

            if (book.getStatus().equals("issued")){
                holder.issueBt.setVisibility(View.GONE);
                holder.locationTv.setVisibility(View.GONE);
            }

            if (book.getUrl()!=null){
                holder.downloadBt.setVisibility(View.VISIBLE);
            } else{
                holder.downloadBt.setVisibility(View.GONE);
            }

            holder.issueBt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(SearchActivity.this, IssueActivity.class);
                    intent.putExtra("key",book.getKey());
                    startActivity(intent);
                }
            });

            holder.downloadBt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int SDK_INT = Build.VERSION.SDK_INT;
                    if (SDK_INT > 8) {
                        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                                .permitAll().build();
                        StrictMode.setThreadPolicy(policy);

                        String url = book.getUrl();
                        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                        request.setDescription("Downloading...");
                        request.setTitle("Downloads");
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                            request.allowScanningByMediaScanner();
                            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                        }
                        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "Documents");

                        DownloadManager manager = (DownloadManager) holder.itemView.getContext().getSystemService(Context.DOWNLOAD_SERVICE);
                        manager.enqueue(request);
                        Toast.makeText(holder.itemView.getContext(), "Downloading...", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public class SearchHolder extends RecyclerView.ViewHolder {

            @BindView(R.id.bookNameTv)
            TextView bookNameTv;
            @BindView(R.id.publicationTv)
            TextView publicationTv;
            @BindView(R.id.authorTv)
            TextView authorTv;
            @BindView(R.id.downloadBt)
            ImageView downloadBt;
            @BindView(R.id.issueBt)
            TextView issueBt;
            @BindView(R.id.locationTv)
            TextView locationTv;
            @BindView(R.id.statusTv)
            TextView statusTv;

            public SearchHolder(@NonNull View itemView) {
                super(itemView);

                ButterKnife.bind(this, itemView);
            }
        }
    }
}
