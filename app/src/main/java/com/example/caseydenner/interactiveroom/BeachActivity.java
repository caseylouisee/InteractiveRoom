package com.example.caseydenner.interactiveroom;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.TextView;
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
     * String holding the connected device's mac address
     */
    String m_address = null;

    /**
     * ProgressDialog
     */
    private ProgressDialog m_progress;

    /**
     * Bluetooth adapter
     */
    BluetoothAdapter m_Bluetooth = null;

    /**
     * Bluetooth socket
     */
    BluetoothSocket m_btSocket = null;

    /**
     * Boolean to check whether bluetooth is connected to a device
     */
    private boolean m_isBtConnected = false;

    /**
     * SPP UUID. Look for it
     */
    static final UUID MYUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    /**
     * String to hold the path of the original beach video
     */
    //String path = "storage/extSdCard/DCIM/Camera/";

    private final String BEACH_ORIG_PATH = "storage/extSdCard/DCIM/Camera/beach.mp4";

    /**
     * String to hold the path of the beach with birds video
     */
    private final String BEACH_BIRDS_PATH = "storage/extSdCard/InteractiveRoom/birds.mp4";

    /**
     * Buttons on, off and disconnect
     * on and off control the Arduino LED, disconnect disconnects the connection to the Arduino
     */
    Button btnOn, btnOff, btnDis;

    /**
     * TextView to display text
     */
    TextView text;

    /**
     * VideoView to view the video
     */
    VideoView videoView;

    /**
     * MediaController for the video view
     */
    MediaController m_mediaController;

    /**
     * InputStream used to retrieve information sent from Arduino to Android via bluetooth
     */
    InputStream m_inputStream;

    /**
     * Thread to manage the bluetooth connection
     */
    ThreadConnected m_ThreadConnected;

    /**
     * Called when the BeachActivity is created
     * @param savedInstanceState the state in which the application was ended is
     *                           saved to be used in the future if the application is reopened
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent newInt = getIntent();
        //receive the address of the bluetooth device
        m_address = newInt.getStringExtra(MainActivity.EXTRA_ADDRESS);

        setContentView(R.layout.activity_beach);

        new ConnectBT().execute(); //Call the class to connect

        //btnOn = (Button)findViewById(R.id.btn_on);
        //btnOff = (Button)findViewById(R.id.btn_off);
        //btnDis = (Button)findViewById(R.id.btn_disconnect);
        //text = (TextView)findViewById(R.id.text_edit);

        m_mediaController = new MediaController(this);
        m_mediaController.setVisibility(View.GONE);
        m_mediaController.setAnchorView(videoView);

        //Log.i("onCreate Beach", getExternalStorageDirectory().toString());
        videoView = (VideoView) findViewById(R.id.videoView);
        videoView.setMediaController(m_mediaController);
        changeVideo(BEACH_ORIG_PATH);

//        btnOn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                turnOnLed();
//            }
//        });

//        btnOff.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                turnOffLed();
//            }
//        });

    }

    /**
     * Disconnects/Closes Arduino bluetooth connection
     */
    @Override
    public void onBackPressed(){
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
        private boolean connectSuccess = true;

        @Override
        protected void onPreExecute(){
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
     * @param socket
     */
    private void startThreadConnected(BluetoothSocket socket){
        m_ThreadConnected = new ThreadConnected(socket);
        m_ThreadConnected.start();
    }

    private class ThreadConnected extends Thread {
        /**
         * Checks if the input stream isn't null
         * @param socket the bluetooth socket which the input stream comes from
         */
        public ThreadConnected(BluetoothSocket socket) {
            m_inputStream = null;

            try {
                m_inputStream = socket.getInputStream();
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
                            if(strReceived.contains("butterflyButton")){
                                changeVideo(BEACH_BIRDS_PATH);
                                playOrigVid();
                            } else if(strReceived.contains("birdsButton")){
                                changeVideo(BEACH_BIRDS_PATH);
                                playOrigVid();
                            }
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
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }

    /**
     * Changes the video back to the original video which just shows the beach
     */
    private void playOrigVid(){
        // video oncompletion listener - change back to original video
        videoView.setOnCompletionListener(
                new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        changeVideo(BEACH_ORIG_PATH);
                    }
                });
    }

//    /**
//     * Turns off the LED on the Arduino device connected via bluetooth
//     */
//    private void turnOffLed() {
//        if (m_btSocket!=null) {
//            try {
//                m_btSocket.getOutputStream().write("TF".getBytes());
//                Log.i("turnOffLED", "TF Sent");
//            } catch (IOException e) {
//                msg("Error");
//            }
//        }
//    }
//
//    /**
//     * Turns on the LED on the Arduino device connected via bluetooth
//     */
//    private void turnOnLed() {
//        if (m_btSocket!=null) {
//            try {
//                m_btSocket.getOutputStream().write("TO".getBytes());
//                Log.i("turnOnLED", "TO Sent");
//            } catch (IOException e) {
//                msg("Error");
//            }
//        }
//    }

    /**
     * Changes the video path to the path specified in the string
     * @param string path to be changed to
     */
    public void changeVideo(String string){
        videoView.setVideoPath(string);
        videoView.start();
    }
}
