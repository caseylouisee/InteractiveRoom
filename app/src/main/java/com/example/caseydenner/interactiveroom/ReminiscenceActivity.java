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
     * String holding the connected device's mac address
     */
    String m_address = null;

    /**
     * ArrayList holding strings
     */
    ArrayList<String> files = new ArrayList<>();

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
     * TextView to display text
     */
    TextView text;

    /**
     * InputStream used to retrieve information sent from Arduino to Android via bluetooth
     */
    InputStream m_inputStream;

    /**
     * Thread to manage the bluetooth connection
     */
    ThreadConnected m_ThreadConnected;

    /**
     * The imageViews visible on the activity
     */
    ImageView imageView1, imageView2, imageView3, imageView4, imageView5;

    /**
     * final String used for transferring extra data in an intent
     */
    static final String FILE = "FILE";

    /**
     * Int used for the rotation of images on the view
     */
    int rotate=0;

    /**
     * Path on the phone where the media from the Camera is stored.
     */
    String path = Environment.getExternalStorageDirectory() + "/DCIM/100MEDIA/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminiscence);

        Intent newInt = getIntent();
        m_address = newInt.getStringExtra(IntermediateReminiscence.EXTRA_ADDRESS);
        files = newInt.getStringArrayListExtra(IntermediateReminiscence.FILES);

        new ConnectBT().execute(); //Call the class to connect

        text = (TextView)findViewById(R.id.text_edit);
        imageView1 = (ImageView) findViewById(R.id.imageView);
        imageView2 = (ImageView) findViewById(R.id.imageView2);
        imageView3 = (ImageView) findViewById(R.id.imageView3);
        imageView4 = (ImageView) findViewById(R.id.imageView4);
        imageView5 = (ImageView) findViewById(R.id.imageView5);

        int fileSize = files.size();
        for(int i = 0; i<fileSize; i++){
            if(i==0){
                File imageFile = new File(path+files.get(i));
                checkOrientation(imageFile);
                setImageView(imageView1, i, imageFile);
            } if(i==1) {
                File imageFile = new File(path+files.get(i));
                checkOrientation(imageFile);
                setImageView(imageView2, i, imageFile);
            } if(i==2){
                File imageFile = new File(path+files.get(i));
                checkOrientation(imageFile);
                setImageView(imageView3, i, imageFile);
            } if(i==3){
                File imageFile = new File(path+files.get(i));
                checkOrientation(imageFile);
                setImageView(imageView4, i, imageFile);
            } if(i==4) {
                File imageFile = new File(path + files.get(i));
                checkOrientation(imageFile);
                setImageView(imageView5, i, imageFile);
            }
        }

    }

    private void setImageView(ImageView imageView, int i, File imageFile) {
        if(files.get(i).endsWith("mp4")){
            Bitmap bMap = ThumbnailUtils.createVideoThumbnail(imageFile.getAbsolutePath(),
                    MediaStore.Video.Thumbnails.MINI_KIND);
            imageView.setImageBitmap(bMap);
        } else {
            imageView.setImageURI(null);
            imageView.setImageURI(Uri.parse(path + files.get(i)));
            imageView.setRotation(rotate);
        }
        imageView.invalidate();
        Log.d("onCreate", "imageView set: " + path+files.get(i) + " rotation: " + rotate);
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
            m_progress = ProgressDialog.show(ReminiscenceActivity.this, "Connecting...", "Please wait!!!");
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
                                Log.d("butterflyButton", path+files.get(0) + " to play in zoomActivity");
                                Intent intent = new Intent(ReminiscenceActivity.this, ZoomActivity.class);
                                intent.putExtra(FILE, path+files.get(0));
                                startActivity(intent);

                            } else if(strReceived.contains("birdsButton")){
                                Log.d("butterflyButton", path+files.get(1) + " to play in zoomActivity");
                                Intent intent = new Intent(ReminiscenceActivity.this, ZoomActivity.class);
                                intent.putExtra(FILE, path+files.get(1));
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


    /**
     * Provides a quicker way to create Toasts on the screen
     * @param s the message to be included in the toast
     */
    private void msg(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }

}
