package com.burmistrov.denis.listfriends;

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;

import static com.burmistrov.denis.listfriends.FriendsAdapter.cache;

public class FullscreenActivity extends Activity {

    ImageView imageView;
    String urlOfPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);

        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        urlOfPhoto = getIntent().getStringExtra(FriendsAdapter.URL_OF_PHOTO);
        imageView = findViewById(R.id.iv_full_screen);
        imageView.setImageBitmap((Bitmap) cache.get(urlOfPhoto));

    }
}
