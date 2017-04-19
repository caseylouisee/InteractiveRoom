package com.example.caseydenner.interactiveroom;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.MediaController;
import android.widget.VideoView;

import java.io.File;

public class ZoomActivity extends AppCompatActivity {

    /**
     * MediaController for the video view
     */
    private MediaController m_mediaController;

    /**
     * VideoView used to display file
     */
    private VideoView videoView;

    /**
     * String representing file received via intent
     */
    private String m_file = "";

    /**
     * integer representing how much to rotate the file used in the activity
     */
    private int m_rotate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zoom);
        Log.d("OnCreate", "Zoom Activity");

        Intent newInt = getIntent();
        m_file = newInt.getStringExtra(ReminiscenceActivity.FILE);

        videoView = (VideoView) findViewById(R.id.videoViewZoomed);
        File file = new File(m_file);

        ReminiscenceActivity reminiscenceActivity = new ReminiscenceActivity();
        reminiscenceActivity.checkOrientation(file);

        m_mediaController = new MediaController(this);
        m_mediaController.setVisibility(View.GONE);
        m_mediaController.setAnchorView(videoView);
        videoView.setMediaController(m_mediaController);

        videoView.setVideoPath(m_file);
        videoView.setRotation(m_rotate);
        videoView.start();

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                finish();
            }
        });
    }

}
