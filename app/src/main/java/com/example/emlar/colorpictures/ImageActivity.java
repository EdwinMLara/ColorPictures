package com.example.emlar.colorpictures;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class ImageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        Intent intent = getIntent();

        Picasso.with(this).load(intent.getData()).into(imageView);

    }
}
