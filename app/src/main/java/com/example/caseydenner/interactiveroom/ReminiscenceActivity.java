package com.example.caseydenner.interactiveroom;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
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
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;

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
     * Buttons on, off and disconnect
     * on and off control the Arduino LED, disconnect disconnects the connection to the Arduino
     */
    Button btnOn, btnOff;

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

    ImageView imageView1, imageView2, imageView3, imageView4, imageView5;

    static final String FILE = "FILE";

    private Animator mCurrentAnimator;
    private int mShortAnimationDuration;


    /**
     * Int used for the rotation of images on the view
     */
    int rotate=0;

    String path = "storage/extSdCard/DCIM/Camera/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent newInt = getIntent();
        m_address = newInt.getStringExtra(IntermediateReminiscence.EXTRA_ADDRESS);
        files = newInt.getStringArrayListExtra(IntermediateReminiscence.FILES);

        setContentView(R.layout.activity_reminiscence);

        new ConnectBT().execute(); //Call the class to connect

        btnOn = (Button)findViewById(R.id.btn_on);
        btnOff = (Button)findViewById(R.id.btn_off);
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
                if(files.get(i).endsWith("mp4")){
                    Bitmap bMap = ThumbnailUtils.createVideoThumbnail(imageFile.getAbsolutePath(),
                            MediaStore.Video.Thumbnails.MINI_KIND);
                    imageView1.setImageBitmap(bMap);
                } else {
                    checkOrientation(imageFile);
                    imageView1.setImageURI(null);
                    imageView1.setImageURI(Uri.parse(path + files.get(i)));
                    imageView1.setRotation(rotate);
                }
                imageView1.invalidate();
                Log.d("onCreate", "imageView1 set: " + path+files.get(i) + " rotation: " + rotate);
            } if(i==1){
                File imageFile = new File(path+files.get(i));
                if(files.get(i).endsWith("mp4")) {
                    Bitmap bMap = ThumbnailUtils.createVideoThumbnail(imageFile.getAbsolutePath(),
                            MediaStore.Video.Thumbnails.MINI_KIND);
                    imageView2.setImageBitmap(bMap);
                } else {
                    checkOrientation(imageFile);
                    imageView2.setImageURI(null);
                    imageView2.setImageURI(Uri.parse(path + files.get(i)));
                    imageView2.setRotation(rotate);
                }
                imageView2.invalidate();
                Log.d("onCreate", "imageView2 set: " + path+files.get(i) + " rotation: " + rotate);
            } if(i==2){
                File imageFile = new File(path+files.get(i));
                if(files.get(i).endsWith("mp4")){
                    Bitmap bMap = ThumbnailUtils.createVideoThumbnail(imageFile.getAbsolutePath(),
                            MediaStore.Video.Thumbnails.MINI_KIND);
                    imageView3.setImageBitmap(bMap);
                } else {
                    checkOrientation(imageFile);
                    imageView3.setImageURI(null);
                    imageView3.setImageURI(Uri.parse(path + files.get(i)));
                    imageView3.setRotation(rotate);
                }
                imageView3.invalidate();
                Log.d("onCreate", "imageView3 set: " + path+files.get(i) + " rotation: " + rotate);
            } if(i==3){
                File imageFile = new File(path+files.get(i));
                if(files.get(i).endsWith("mp4")){
                    Bitmap bMap = ThumbnailUtils.createVideoThumbnail(imageFile.getAbsolutePath(),
                            MediaStore.Video.Thumbnails.MINI_KIND);
                    imageView4.setImageBitmap(bMap);
                } else {
                    checkOrientation(imageFile);
                    imageView4.setImageURI(null);
                    imageView4.setImageURI(Uri.parse(path + files.get(i)));
                    imageView4.setRotation(rotate);
                }
                imageView4.invalidate();
                Log.d("onCreate", "imageView4 set: " + path+files.get(i) + " rotation: " + rotate);
            } if(i==4){
                File imageFile = new File(path+files.get(i));
                if(files.get(i).endsWith("mp4")){
                    Bitmap bMap = ThumbnailUtils.createVideoThumbnail(imageFile.getAbsolutePath(),
                            MediaStore.Video.Thumbnails.MINI_KIND);
                    imageView5.setImageBitmap(bMap);
                } else {
                    checkOrientation(imageFile);
                    imageView5.setImageURI(null);
                    imageView5.setImageURI(Uri.parse(path + files.get(i)));
                    imageView5.setRotation(rotate);
                }
                imageView5.invalidate();
                Log.d("onCreate", "imageView5 set: " + path+files.get(i) + " rotation: " + rotate);
            }
        }

        btnOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                turnOnLed();
            }
        });

        btnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                turnOffLed();
            }
        });

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
                            if(strReceived.contains("buttonOne")){
                                Intent intent = new Intent(ReminiscenceActivity.this, ZoomActivity.class);
                                intent.putExtra(FILE, path+files.get(1));
                            } else if(strReceived.contains("buttonTwo")){

                            }
                        }});

                } catch (IOException e) {
                    e.printStackTrace();
                    onBackPressed();
                }
            }
        }

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




    /**
     * Provides a quicker way to create Toasts on the screen
     * @param s the message to be included in the toast
     */
    private void msg(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }

    /**
     * Turns off the LED on the Arduino device connected via bluetooth
     */
    private void turnOffLed() {
        if (m_btSocket!=null) {
            try {
                m_btSocket.getOutputStream().write("TF".getBytes());
                Log.i("turnOffLED", "TF Sent");
            } catch (IOException e) {
                msg("Error");
            }
        }
    }

    /**
     * Turns on the LED on the Arduino device connected via bluetooth
     */
    private void turnOnLed() {
        if (m_btSocket!=null) {
            try {
                m_btSocket.getOutputStream().write("TO".getBytes());
                Log.i("turnOnLED", "TO Sent");
            } catch (IOException e) {
                msg("Error");
            }
        }
    }
}
