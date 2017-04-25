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

    /**
     * Method to retrieve the file
     * @return string m_file
     */
    public String getFile(){
        return m_file;
    }

    /**
     * Method to set the file m_file to the parameter
     * @param string to set the file to
     */
    public void setFile(String string){
        m_file = string;
    }

    /**
     * Method to retrieve the rotation
     * @return m_rotate
     */
    public int getRotate(){
        return m_rotate;
    }

    /**
     * Method to set m_rotate to the int parameter
     * @param r to set m_rotate to
     */
    public void setRotate(int r){
        m_rotate = r;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zoom);
        Log.d("OnCreate", "Zoom Activity");

        Intent newInt = getIntent();
        setFile(newInt.getStringExtra(ReminiscenceActivity.FILE));

        videoView = (VideoView) findViewById(R.id.videoViewZoomed);
        File file = new File(getFile());

        ReminiscenceActivity reminiscenceActivity = new ReminiscenceActivity();
        reminiscenceActivity.checkOrientation(file);

        m_mediaController = new MediaController(this);
        m_mediaController.setVisibility(View.GONE);
        m_mediaController.setAnchorView(videoView);
        videoView.setMediaController(m_mediaController);

        videoView.setVideoPath(getFile());
        videoView.setRotation(getRotate());
        videoView.start();

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                finish();
            }
        });
    }

}
