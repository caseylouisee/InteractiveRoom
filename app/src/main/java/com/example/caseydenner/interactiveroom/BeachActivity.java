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
import android.view.Window;
import android.view.WindowManager;
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
     * ProgressDialog used to show progress of the connection to the Arduino
     */
    private ProgressDialog m_progress;

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
     * Method to retrieve the bluetooth adapter
     * @return m_bluetooth
     */
    public BluetoothAdapter getBluetooth(){
        return m_Bluetooth;
    }

    /**
     * Method to set m_Bluetooth to bt in parameter
     * @param bt to set m_bluetooth to
     */
    public void setBluetooth(BluetoothAdapter bt){
        m_Bluetooth = bt;
    }

    /**
     * Method to return the bluetooth socket
     * @return m_btSocket
     */
    public BluetoothSocket getBluetoothSocket(){
        return m_btSocket;
    }

    /**
     * Method to set the bluetooth socket to the parameter
     * @param bs to set the bluetooth socket to
     */
    public void setBluetoothSocket(BluetoothSocket bs){
        m_btSocket = bs;
    }

    /**
     * Method to retrieve the inputStream
     * @return m_inputStream
     */
    public InputStream getInputStream(){
        return m_inputStream;
    }

    /**
     * Method to set the input stream to the parameter
     * @param is to set the m_inputStream to
     */
    public void setInputStream(InputStream is){
        m_inputStream = is;
    }

    /**
     * Method to retrieve m_address
     * @return m_address string
     */
    public String getAddress(){
        return m_address;
    }

    /**
     * Method to set m_address to the string parameter
     * @param string to set m_address to
     */
    public void setAddress(String string){
        m_address = string;
    }

    /**
     * Method to retrieve m_isBTConnected boolean
     * @return m_isBTConnected
     */
    public boolean getIsBTConnected(){
        return m_isBtConnected;
    }

    /**
     * Method to set m_isBTConnected to boolean parameter
     * @param bool to set m_isBTConnected to
     */
    public void setIsBTConnected(Boolean bool){
        m_isBtConnected = bool;
    }

    /**
     * Method to retrieve the connected thread m_ThreadConnected
     * @return m_ThreadConnected
     */
    public ThreadConnected getThreadConnected(){
        return m_ThreadConnected;
    }

    /**
     * Method to set the thread connected to the parameter
     * @param tc to set m_threadConnected to
     */
    public void setThreadConnected(ThreadConnected tc){
        m_ThreadConnected = tc;
    }

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
        setAddress(newInt.getStringExtra(MainActivity.EXTRA_ADDRESS));
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
        if (getBluetoothSocket()!=null) {
            try {
                getBluetoothSocket().close(); //close connection
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
                if (getBluetoothSocket() == null || !getIsBTConnected()) {
                    //get the mobile bluetooth device
                    setBluetooth(BluetoothAdapter.getDefaultAdapter());
                    //connects to the device's address and checks if it's available
                    BluetoothDevice bluetoothDevice = getBluetooth().getRemoteDevice(getAddress());
                    //create a RFCOMM (SPP) connection
                    setBluetoothSocket(bluetoothDevice.createInsecureRfcommSocketToServiceRecord(MYUUID));
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    getBluetoothSocket().connect();//start connection

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
                setIsBTConnected(true);
                startThreadConnected(getBluetoothSocket());
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
        setThreadConnected(new ThreadConnected(socket));
        getThreadConnected().start();
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
            setInputStream(null);

            try {
                setInputStream(socket.getInputStream());
                Log.d(CLASS, "Getting input stream");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;

            while (getBluetoothSocket().isConnected()) {
                try {
                    bytes = getInputStream().read(buffer);
                    final String strReceived = new String(buffer, 0, bytes);

                    runOnUiThread(new Runnable(){
                        @Override
                        public void run() {
                            if(strReceived.contains("butterflyButton")){
                                Log.d(CLASS, "butterflyButton");
                                Uri video = Uri.parse("android.resource://" + getPackageName() + "/" +
                                        R.raw.butterfliescompressed); //do not add any extension
                                m_videoView.setVideoURI(video);
                                m_videoView.start();
                                playOrigVid();
                            } else if(strReceived.contains("birdsButton")){
                                Log.d(CLASS, "birdsButton");
                                Uri video = Uri.parse("android.resource://" + getPackageName() + "/" +
                                        R.raw.birdscompressed); //do not add any extension
                                m_videoView.setVideoURI(video);
                                m_videoView.start();
                                playOrigVid();
                            } else if(strReceived.contains("three")){
                                Log.d(CLASS, "pin 11");
                                Uri video = Uri.parse("android.resource://" + getPackageName() + "/" +
                                        R.raw.firecompressed); //do not add any extension
                                m_videoView.setVideoURI(video);
                                m_videoView.start();
                                playOrigVid();
                            } else if(strReceived.contains("four")) {
                                Log.d(CLASS, "pin 10");
                                Uri video = Uri.parse("android.resource://" + getPackageName() + "/" +
                                        R.raw.planecompressed); //do not add any extension
                                m_videoView.setVideoURI(video);
                                m_videoView.start();
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
