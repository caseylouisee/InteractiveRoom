package com.example.caseydenner.interactiveroom;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.UUID;

public class ReminiscenceActivity extends AppCompatActivity {

    /**
     * SPP UUID. Look for it
     */
    private static final UUID MYUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    /**
     * final String used for transferring extra data in an intent
     */
    public static final String FILE = "FILE";

    /**
     * The imageViews visible on the activity
     */
    private ImageView m_imageView1, m_imageView2, m_imageView3, m_imageView4;

    /**
     * ProgressDialog to show the progress of the bluetooth connection
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
     * Boolean to check whether bluetooth is connected to a device
     */
    private boolean m_isBtConnected = false;

    /**
     * String holding the connected device's mac address
     */
    private String m_address = null;

    /**
     * ArrayList holding strings
     */
    private ArrayList<String> m_files = new ArrayList<>();

    /**
     * Int used for the rotation of images on the view
     */
    private int m_rotate=0;

    /**
     * Path on the phone where the media from the Camera is stored.
     */
    private String m_path = Environment.getExternalStorageDirectory() + "/DCIM/100MEDIA/";

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
     * Method to return array of files selected by user
     */
    public ArrayList<String> getFiles(){
        return m_files;
    }

    /**
     * Method to set the files in the array of files
     * @param files files to add to the array
     */
    public void setFiles(ArrayList<String> files){
        m_files = files;
    }

    /**
     * Method to retrieve m_rotate
     * @return m_rotate
     */
    public int getRotate(){
        return m_rotate;
    }

    /**
     * Method to setRotate to int parameter
     * @param r to set m_rotate to
     */
    public void setRotate(int r){
        m_rotate = r;
    }

    /**
     * Method to return the file path needed to get user's videos
     * @return m_path which is the file of the path where the user's videos are stored
     */
    public String getPath(){
        return m_path;
    }

    /**
     * Sets the m_path to the path in the method parameter
     * @param string to set the path to
     */
    public void setPath(String string){
        m_path = string;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final String METHOD = "onCreate";

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminiscence);

        Intent newInt = getIntent();
        setAddress(newInt.getStringExtra(IntermediateReminiscence.EXTRA_ADDRESS));
        setFiles(newInt.getStringArrayListExtra(IntermediateReminiscence.FILES));

        new ConnectBT().execute(); //Call the class to connect

        setView();

    }

    /**
     * Sets up the view and initializes the xml components
     */
    private void setView(){
        m_imageView1 = (ImageView) findViewById(R.id.imageView);
        m_imageView2 = (ImageView) findViewById(R.id.imageView2);
        m_imageView3 = (ImageView) findViewById(R.id.imageView3);
        m_imageView4 = (ImageView) findViewById(R.id.imageView4);

        int fileSize = getFiles().size();
        for(int i = 0; i<fileSize; i++){
            if(i==0){
                File imageFile = new File(getPath()+getFiles().get(i));
                checkOrientation(imageFile);
                setImageView(m_imageView1, i, imageFile);
            } if(i==1) {
                File imageFile = new File(getPath()+getFiles().get(i));
                checkOrientation(imageFile);
                setImageView(m_imageView2, i, imageFile);
            } if(i==2){
                File imageFile = new File(getPath()+getFiles().get(i));
                checkOrientation(imageFile);
                setImageView(m_imageView3, i, imageFile);
            } if(i==3){
                File imageFile = new File(getPath()+getFiles().get(i));
                checkOrientation(imageFile);
                setImageView(m_imageView4, i, imageFile);
            }
        }
    }

    /**
     * Sets the imageView image based on the file the user selects to upload. A thumbnail of the video is output in
     * the imageView.
     * @param imageView The imageView where the image is shown
     * @param i the index number of the file path in the array m_files
     * @param imageFile The image to be displayed in the view
     */
    private void setImageView(ImageView imageView, int i, File imageFile) {
        if(getFiles().get(i).endsWith("mp4")){
            Bitmap bMap = ThumbnailUtils.createVideoThumbnail(imageFile.getAbsolutePath(),
                    MediaStore.Video.Thumbnails.MINI_KIND);
            imageView.setImageBitmap(bMap);
        } else {
            imageView.setImageURI(null);
            imageView.setImageURI(Uri.parse(getPath() + getFiles().get(i)));
            imageView.setRotation(getRotate());
        }
        imageView.invalidate();
        Log.d("onCreate", "imageView set: " + getPath()+getFiles().get(i) + " rotation: " + getRotate());
    }

    /**
     * Disconnects/Closes Arduino bluetooth connection
     */
    @Override
    public void onBackPressed(){
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
        private boolean connectSuccess = true;

        @Override
        protected void onPreExecute(){
            //show a progress dialog
            m_progress = ProgressDialog.show(ReminiscenceActivity.this, "Connecting...", "Please wait!!!");
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
        setThreadConnected(new ThreadConnected(socket));
        getThreadConnected().start();
    }

    private class ThreadConnected extends Thread {
        /**
         * Checks if the input stream isn't null
         * @param socket the bluetooth socket which the input stream comes from
         */
        private ThreadConnected(BluetoothSocket socket) {
            setInputStream(null);

            try {
                setInputStream(socket.getInputStream());
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
                                Log.d("butterflyButton", getPath()+getFiles().get(0) + " to play in zoomActivity");
                                Intent intent = new Intent(ReminiscenceActivity.this, ZoomActivity.class);
                                intent.putExtra(FILE, getPath()+getFiles().get(0));
                                startActivity(intent);
                            } else if(strReceived.contains("birdsButton")){
                                Log.d("butterflyButton", getPath()+getFiles().get(1) + " to play in zoomActivity");
                                Intent intent = new Intent(ReminiscenceActivity.this, ZoomActivity.class);
                                intent.putExtra(FILE, getPath()+getFiles().get(1));
                                startActivity(intent);
                            } else if(strReceived.contains("three")){
                                Log.d("three", getPath()+getFiles().get(2) + " to play in zoomActivity");
                                Intent intent = new Intent(ReminiscenceActivity.this, ZoomActivity.class);
                                intent.putExtra(FILE, getPath()+getFiles().get(2));
                                startActivity(intent);
                            } else if(strReceived.contains("four")) {
                                Log.d("four", getPath()+getFiles().get(3) + " to play in zoomActivity");
                                Intent intent = new Intent(ReminiscenceActivity.this, ZoomActivity.class);
                                intent.putExtra(FILE, getPath()+getFiles().get(3));
                                startActivity(intent);
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
     * Checks orientation of file (both video and images) and if they are not horizontal, then the file is rotated in
     * order for it to be displayed correctly in landscape on the activity.
     * @param imageFile file to check orientation of
     */
    public void checkOrientation(File imageFile){
        setRotate(0);
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(imageFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_270:
                setRotate(270);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                setRotate(180);
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                setRotate(90);
                break;
        }
    }

    /**
     * Provides a quicker way to create Toasts on the screen
     * @param s the message to be included in the toast
     */
    private void msg(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }

}
