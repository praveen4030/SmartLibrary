package com.example.smartlibrary.Home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smartlibrary.Model.Video;
import com.example.smartlibrary.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class VideoListActivity extends AppCompatActivity {

    ArrayList<Video> list = new ArrayList<>();
    @BindView(R.id.videoListRv)
    RecyclerView videoListRv;
    @BindView(R.id.search_view)
    MaterialSearchView searchView;

    DatabaseReference videoRef;
    @BindView(R.id.searchPb)
    ProgressBar searchPb;
    @BindView(R.id.toolbar_container)
    FrameLayout toolbarContainer;

    VideoListAdapter adapter;
    String branch;

    BottomSheetDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_list);
        ButterKnife.bind(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Video Lectures");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        branch = getIntent().getStringExtra("branch");
        searchView.showSearch(false);

        searchPb.setVisibility(View.VISIBLE);
        videoListRv.setVisibility(View.GONE);

        searches();

        videoRef = FirebaseDatabase.getInstance().getReference().child("Videos").child(branch);
        videoRef.keepSynced(true);

        videoListRv.setLayoutManager(new GridLayoutManager(this, 2));
        fetchData();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);

        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);

        return true;

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
        ArrayList<Video> myList = new ArrayList<>();

        for (Video object : list) {
            if (object.getTopic().toLowerCase().contains(newText.toLowerCase())) {
                myList.add(object);
            }

            VideoListAdapter adapter = new VideoListAdapter(myList);
            videoListRv.setAdapter(adapter);

        }
    }

    private void fetchData() {

        videoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                     list = new ArrayList<>();

                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        list.add(ds.getValue(Video.class));
                        Log.d("Video",ds.getKey());
                    }

                    searchPb.setVisibility(View.GONE);
                    videoListRv.setVisibility(View.VISIBLE);

                    adapter = new VideoListAdapter(list);
                    videoListRv.setAdapter(adapter);

                } else{
                    searchPb.setVisibility(View.GONE);
                    Toast.makeText(VideoListActivity.this, "No Data available", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    class VideoListAdapter extends RecyclerView.Adapter<VideoListAdapter.ListHolder> {
        ArrayList<Video> list;

        public VideoListAdapter(ArrayList<Video> list) {
            this.list = list;
        }

        @NonNull
        @Override
        public ListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.zunit_video, parent, false);
            return new ListHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ListHolder holder, int position) {
            Video video = list.get(position);

            Picasso.get().load("http://img.youtube.com/vi/" + video.getVideoID() + "/0.jpg").into(holder.videoImage);
            holder.topicTv.setText(video.getTopic());
            holder.subjectTv.setText(video.getSubject());
            holder.teacherTv.setText("By : " + video.getTeacher());

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(VideoListActivity.this, VideoActivity.class);
                    intent.putExtra("videoID", video.getVideoID());
                    startActivity(intent);
                }
            });

        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public class ListHolder extends RecyclerView.ViewHolder {

            @BindView(R.id.videoImage)
            ImageView videoImage;
            @BindView(R.id.topicTv)
            TextView topicTv;
            @BindView(R.id.subjectTv)
            TextView subjectTv;
            @BindView(R.id.teacherTv)
            TextView teacherTv;

            public ListHolder(@NonNull View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }
    }
}
