package com.example.smartlibrary.Book;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.smartlibrary.Model.Blog;
import com.example.smartlibrary.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

public class ArticleListActivity extends AppCompatActivity {

    private static final String TAG = "BlogActivity";
    private RecyclerView recyclerView;
    String currentUserID;
    LinearLayoutManager linearLayoutManager;
    DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_list);

        recyclerView = findViewById(R.id.blog_recycler_list);
        recyclerView.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        myRef = FirebaseDatabase.getInstance().getReference().child("Articles");

    }

    @Override
    protected void onStart() {
        super.onStart();

        Query query = FirebaseDatabase.getInstance().getReference().child("Articles");

        FirebaseRecyclerOptions<Blog> options = new FirebaseRecyclerOptions.Builder<Blog>()
                .setQuery(query, Blog.class)
                .build();

        FirebaseRecyclerAdapter<Blog, BlogHolder> adapter = new FirebaseRecyclerAdapter<Blog, BlogHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull BlogHolder holder, int position, @NonNull Blog model) {

                String key = getRef(position).getKey();

                if(model.getmPhotoUrl()!=null){
                    Picasso.get()
                            .load(model.getmPhotoUrl())
                            .into(holder.photoImageView);

                    holder.authorTextView.setText("By " + model.getmAuthor());

                    String updateText = model.getmInfo();

                    int maxLength2 = (updateText.length() < 85) ? updateText.length() : 85;
                    updateText = updateText.substring(0, maxLength2) + "...";

                    holder.subTitleTextView.setText(updateText);

                } else{
                    holder.photoImageView.setVisibility(View.GONE);
                    holder.authorTextView.setVisibility(View.GONE);

                    String updateText = model.getmInfo();

                    int maxLength2 = (updateText.length() < 75) ? updateText.length() : 75;
                    updateText = updateText.substring(0, maxLength2) + "... By " + model.getmAuthor();

                    holder.subTitleTextView.setText(updateText);

                }

                holder.titleTextView.setText(model.getmTitle());

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent detailActivity = new Intent(ArticleListActivity.this, ArticleDetailActivity.class);
                        detailActivity.putExtra("BlogID", key);
                        holder.itemView.getContext().startActivity(detailActivity);
                    }
                });
            }

            @Override
            public int getItemCount() {
                return super.getItemCount();
            }

            @NonNull
            @Override
            public BlogHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.zunit_blog, parent, false);
                return new BlogHolder(v);
            }
        };

        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);

        linearLayoutManager.smoothScrollToPosition(recyclerView,null,0);
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                recyclerView.smoothScrollToPosition(positionStart+1);
                linearLayoutManager.setReverseLayout(true);
                linearLayoutManager.setStackFromEnd(true);
            }
        });
        adapter.startListening();

    }

    class BlogHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView authorTextView;
        ImageView photoImageView;
        TextView subTitleTextView;

        public BlogHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.title);
            authorTextView = itemView.findViewById(R.id.author);
            photoImageView = (ImageView) itemView.findViewById(R.id.blogsImage);
            subTitleTextView = (TextView) itemView.findViewById(R.id.subTitle);

        }
    }
}
