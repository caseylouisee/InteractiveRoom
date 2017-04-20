package com.example.caseydenner.interactiveroom;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

/**
 * First class that is called within the application
 */
public class MainActivity extends AppCompatActivity {

    /**
     * Static address that is sent through an intent.
     */
    public static String EXTRA_ADDRESS = "device_address";

    /**
     * Button go used to proceed to the next activity
     */
    private Button m_btnGo;

    /**
     * Radio buttons used to select the scene to project
     */
    private RadioButton m_btnBeach, m_btnReminisce;

    /**
     * Text view to output information to the user
     */
    private TextView m_textView;

    /**
     * Bluetooth adapter used to connect to bluetooth socket
     */
    private BluetoothAdapter m_Bluetooth = null;

    /**
     * String that holds the device address
     */
    private String m_address;

    /**
     * Method to get the bluetooth device's address
     * @return m_address the bluetooth device's address
     */
    public String getAddress() {
        return m_address;
    }

    /**
     * Method to set the bluetooth device's address
     * @param string set as the bluetooth device's address
     */
    public void setAddress(String string){
        m_address = string;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final String METHOD = "onCreate";
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(METHOD, "main activity created");
        setUpBluetooth();
        setView();
    }

    /**
     * Checks that bluetooth is enabled on the Android device and if it isn't asks the user to enable it
     */
    private void setUpBluetooth(){
        final String METHOD = "setUpBluetooth";

        //if the device has bluetooth
        m_Bluetooth = BluetoothAdapter.getDefaultAdapter();

        if (m_Bluetooth == null) {
            //Show a message. that the device has no bluetooth adapter
            Log.d(METHOD, "Bluetooth Device Not Available");
            Toast.makeText(getApplicationContext(), "Bluetooth Device Not Available",
                    Toast.LENGTH_LONG).show();

            //finish apk
            finish();
        } else if (!m_Bluetooth.isEnabled()) {
            //Ask to the user turn the bluetooth on
            Log.d(METHOD, "Ask user to turn bluetooth on");
            Intent turnBTon = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnBTon, 1);
        }
    }

    /**
     * Sets up the view initializing the xml components
     */
    private void setView(){
        final String METHOD = "setView";
        m_btnBeach = (RadioButton) findViewById(R.id.radioBeach);
        m_btnReminisce = (RadioButton) findViewById(R.id.radioReminisce);
        m_textView = (TextView) findViewById(R.id.textViewOutput);
        m_textView.setVisibility(View.INVISIBLE);

        m_btnGo= (Button) findViewById(R.id.buttonGo);

        m_btnGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (m_btnBeach.isChecked()) {
                    connectToArduino();
                    Log.d(METHOD, "beach is checked");
                    Intent intent = new Intent(MainActivity.this, BeachActivity.class);
                    intent.putExtra(EXTRA_ADDRESS, getAddress());
                    startActivity(intent);

                } else if (m_btnReminisce.isChecked()) {
                    connectToArduino();
                    Log.d(METHOD, "reminiscence is checked");

                    Intent intent = new Intent(MainActivity.this, IntermediateReminiscence.class);
                    intent.putExtra(EXTRA_ADDRESS, getAddress());
                    startActivity(intent);

                } else {
                    Log.d(METHOD, "nothing is checked");
                    m_textView.setText("Please select a scene to project");
                    m_textView.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    /**
     * Connects to the Arduino via bluetooth using the device name to auto connect
     */
    private void connectToArduino() {
        final String METHOD = "connectToArduino";
        Set<BluetoothDevice> pairedDevices = m_Bluetooth.getBondedDevices();
        ArrayList list = new ArrayList();

        // automatically connects to arduino device set up
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice bt : pairedDevices) {
                //Get the device's name and the address
                list.add(bt.getName() + "\n" + bt.getAddress());
                if ((bt.getName().equals("Arduino")) || (bt.getName().equals("HC-06"))) {
                    // Get the device MAC address, the last 17 chars in the View
                    String info = (bt.getName() + "\n" + bt.getAddress());
                    setAddress(info.substring(info.length() - 17));
                    Log.d(METHOD,"attempting to connect to " + bt.getName());
                }
            }
        }
    }

}
