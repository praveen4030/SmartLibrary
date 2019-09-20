package com.example.smartlibrary.Home;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.smartlibrary.R;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

public class VideoActivity extends YouTubeBaseActivity {

    YouTubePlayerView youTubePv;
    YouTubePlayer ytp;
    String videoID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        youTubePv = findViewById(R.id.youTubePlayerView);

        videoID = getIntent().getStringExtra("videoID");

        playVideo(videoID);

    }

    private void playVideo(String videoId) {
        youTubePv.initialize("AIzaSyAW5wD0GFiF1USESnDRAS9RN2G0LDUGD4M",
                new YouTubePlayer.OnInitializedListener() {
                    @Override
                    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                        ytp = youTubePlayer;
                        youTubePlayer.cueVideo(videoId);
                    }

                    @Override
                    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

                    }
                });
    }
}
