package com.example.emlar.colorpictures;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.VideoView;

public class VideoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        VideoView videoView = (VideoView) findViewById(R.id.videoView);
        Intent intent = getIntent();

        Uri videoUri = intent.getData();

        videoView.setVideoURI(videoUri);
        videoView.start();
    }
}
