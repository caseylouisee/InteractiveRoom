package com.example.caseydenner.interactiveroom;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

/**
 * Class which manages the Beach Scene
 */
public class BeachActivity extends AppCompatActivity {

    /**
     * SPP UUID. Look for it
     */
    private static final UUID MYUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    /**
     * String used in logs with the class name
     */
    private final String CLASS = "BeachActivity";

    /**
     * VideoView to view the video
     */
    private VideoView m_videoView;

    /**
     * MediaController for the video view
     */
    private MediaController m_mediaController;

    /**
     * Bluetooth adapter
     */
    private BluetoothAdapter m_Bluetooth = null;

    /**
     * Bluetooth socket
     */
    private BluetoothSocket m_btSocket = null;

    /**
     * InputStream used to retrieve information sent from Arduino to Android via bluetooth
     */
    private InputStream m_inputStream;

    /**
     * ProgressDialog used to show progress of the connection to the Arduino
     */
    private ProgressDialog m_progress;

    /**
     * Thread to manage the bluetooth connection
     */
    private ThreadConnected m_ThreadConnected;

    /**
     * String holding the connected device's mac address
     */
    private String m_address = null;

    /**
     * Boolean to check whether bluetooth is connected to a device
     */
    private boolean m_isBtConnected = false;

    /**
     * Called when the BeachActivity is created
     * @param savedInstanceState the state in which the application was ended is
     *                           saved to be used in the future if the application is reopened
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(CLASS, "Activity called");
        final String METHOD = "onCreate";
        Log.d(METHOD, "called");
        super.onCreate(savedInstanceState);
        Intent newInt = getIntent();
        //receive the address of the bluetooth device
        m_address = newInt.getStringExtra(MainActivity.EXTRA_ADDRESS);
        setContentView(R.layout.activity_beach);
        new ConnectBT().execute(); //Call the class to connect
        setView();
    }

    /**
     * Sets view including the media controller and video view
     */
    private void setView(){
        m_mediaController = new MediaController(this);
        m_mediaController.setVisibility(View.GONE);
        m_mediaController.setAnchorView(m_videoView);

        //Log.i("onCreate Beach", getExternalStorageDirectory().toString());
        m_videoView = (VideoView) findViewById(R.id.videoView);
        m_videoView.setMediaController(m_mediaController);
        Uri video = Uri.parse("android.resource://" + getPackageName() + "/" +
                R.raw.beachcompressed); //do not add any extension
        m_videoView.setVideoURI(video);
        m_videoView.start();
    }

    /**
     * Disconnects/Closes Arduino bluetooth connection and returned to the Main activity
     */
    @Override
    public void onBackPressed(){
        final String METHOD = "onBackPressed";
        Log.d(METHOD, "back buton pressed");
        if (m_btSocket!=null) {
            try {
                m_btSocket.close(); //close connection
            }
            catch (IOException e) {
                msg("Error");
            }
        }
        finish(); //return to the first layout
    }


    /**
     * Connects to the bluetooth device (Arduino)
     */
    private class ConnectBT extends AsyncTask<Void, Void, Void> {
        final String CLASS = "connectBT";
        private boolean connectSuccess = true;

        @Override
        protected void onPreExecute(){
            Log.d(CLASS, "onPreExecute");
            //show a progress dialog
            m_progress = ProgressDialog.show(BeachActivity.this, "Connecting...", "Please wait!!!");
        }

        /**
         * Completed in the background to check connection to the bluetooth device
         * @param devices the devices it is connected to
         */
        @Override
        protected Void doInBackground(Void... devices){
            try {
                if (m_btSocket == null || !m_isBtConnected) {
                    //get the mobile bluetooth device
                    m_Bluetooth = BluetoothAdapter.getDefaultAdapter();
                    //connects to the device's address and checks if it's available
                    BluetoothDevice bluetoothDevice = m_Bluetooth.getRemoteDevice(m_address);
                    //create a RFCOMM (SPP) connection
                    m_btSocket = bluetoothDevice.createInsecureRfcommSocketToServiceRecord(MYUUID);
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    m_btSocket.connect();//start connection

                }
            }
            catch (IOException e) {
                connectSuccess = false;//if the try failed, you can check the exception here
            }
            return null;
        }

        /**
         * Completed after the doInBackground method to ensure everything is ok
         * @param result result of doInBackground
         */
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            if (connectSuccess){
                msg("Connected.");
                m_isBtConnected = true;
                startThreadConnected(m_btSocket);
            }
            m_progress.dismiss();
        }
    }

    /**
     * Starts a thread which checks for any incoming messages from the input stream
     * @param socket bluetooth socket to connect to
     */
    private void startThreadConnected(BluetoothSocket socket){
        final String METHOD = "startThreadConnected";
        m_ThreadConnected = new ThreadConnected(socket);
        m_ThreadConnected.start();
        Log.d(METHOD, "Thread Connected started");
    }

    /**
     * Thread class that checks the input stream from the bluetooth connection
     */
    private class ThreadConnected extends Thread {
        final String CLASS = "ThreadConnected";

        /**
         * Checks if the input stream isn't null
         * @param socket the bluetooth socket which the input stream comes from
         */
        private ThreadConnected(BluetoothSocket socket) {
            m_inputStream = null;

            try {
                m_inputStream = socket.getInputStream();
                Log.d(CLASS, "Getting input stream");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;

            while (m_btSocket.isConnected()) {
                try {
                    bytes = m_inputStream.read(buffer);
                    final String strReceived = new String(buffer, 0, bytes);

                    runOnUiThread(new Runnable(){
                        @Override
                        public void run() {
                            Uri video = Uri.parse("android.resource://" + getPackageName() + "/" +
                                    R.raw.beachcompressed);
                            if(strReceived.contains("butterflyButton")){
                                Log.d(CLASS, "butterflyButton");
                                video = Uri.parse("android.resource://" + getPackageName() + "/" +
                                        R.raw.butterfliescompressed); //do not add any extension
                            } else if(strReceived.contains("birdsButton")){
                                Log.d(CLASS, "birdsButton");
                                video = Uri.parse("android.resource://" + getPackageName() + "/" +
                                        R.raw.birdscompressed); //do not add any extension
                            } else if(strReceived.contains("three")){
                                Log.d(CLASS, "pin 11");
                                video = Uri.parse("android.resource://" + getPackageName() + "/" +
                                        R.raw.birdscompressed); //do not add any extension
                            } else if(strReceived.contains("four")) {
                                Log.d(CLASS, "pin 10");
                                video = Uri.parse("android.resource://" + getPackageName() + "/" +
                                        R.raw.birdscompressed); //do not add any extension
                            }
                            m_videoView.setVideoURI(video);
                            m_videoView.start();
                            playOrigVid();
                        }});

                } catch (IOException e) {
                    e.printStackTrace();
                    onBackPressed();
                }
            }
        }

    }

    /**
     * Provides a quicker way to create Toasts on the screen
     * @param s the message to be included in the toast
     */
    private void msg(String s) {
        final String METHOD = "msg";
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
        Log.d(METHOD, "Making toast " + s);
    }

    /**
     * Changes the video back to the original video which just shows the beach
     */
    private void playOrigVid(){
        final String METHOD = "playOrigVid";
        // video oncompletion listener - change back to original video
        m_videoView.setOnCompletionListener(
                new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        Uri video = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.beachcompressed); //do not add any extension
                        m_videoView.setVideoURI(video);
                        m_videoView.start();
                        Log.d(METHOD, "Starting video" + video);
                    }
                });
    }
}
