package com.example.smartlibrary.Book;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.smartlibrary.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class ArticleDetailActivity extends AppCompatActivity {

    private TextView blogTitle, author;
    private ImageView blogImage;
    private TextView contentDetail;
    private DatabaseReference myRef;
    String post_key = null;
    public static final String TAG = "BlogDetailActivity";
    FirebaseAuth mAuth;
    String currentUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_detail);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();

        blogImage = findViewById(R.id.blogsImageDetail);
        contentDetail = findViewById(R.id.contentDetail);
        blogTitle = findViewById(R.id.blogTitle);
        author = findViewById(R.id.authorNew);

        post_key = getIntent().getStringExtra("BlogID");

        myRef = FirebaseDatabase.getInstance().getReference().child("Articles").child(post_key);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
                    String blog_title = (String) snapshot.child("mTitle").getValue().toString();
                    String blog_author = (String) snapshot.child("mAuthor").getValue().toString();
                    String blog_content = (String) snapshot.child("mInfo").getValue().toString();

                    collapsingToolbar.setTitle(" ");
                    blogTitle.setText(blog_title);
                    author.setText("By " + blog_author);
                    contentDetail.setText(blog_content);

                    String blog_imageUrl = (String) snapshot.child("mPhotoUrl").getValue().toString();
                    Picasso.get().load(blog_imageUrl).into(blogImage);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
