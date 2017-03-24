package com.example.caseydenner.interactiveroom;

import android.content.Intent;
import android.media.ExifInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.MediaController;
import android.widget.VideoView;

import java.io.File;
import java.io.IOException;

public class ZoomActivity extends AppCompatActivity {

    private String m_file = "";

    VideoView videoView;

    int rotate;

    /**
     * MediaController for the video view
     */
    MediaController m_mediaController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zoom);

        Intent newInt = getIntent();
        m_file = newInt.getStringExtra(ReminiscenceActivity.FILE);

        videoView = (VideoView) findViewById(R.id.videoViewZoomed);
        File file = new File(m_file);

        checkOrientation(file);

        m_mediaController = new MediaController(this);
        m_mediaController.setVisibility(View.GONE);
        m_mediaController.setAnchorView(videoView);
        videoView.setMediaController(m_mediaController);

        videoView.setVideoPath(m_file);
        videoView.setRotation(rotate);
        videoView.start();
    }

    private void checkOrientation(File imageFile){
        rotate=0;
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(imageFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_270:
                rotate = 270;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                rotate = 180;
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                rotate = 90;
                break;
        }
    }
}
