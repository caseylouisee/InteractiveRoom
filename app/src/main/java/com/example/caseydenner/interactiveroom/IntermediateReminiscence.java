package com.example.caseydenner.interactiveroom;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;

import java.util.UUID;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

/**
 * Activity that allows the user to select files to upload to the reminiscence room
 */
public class IntermediateReminiscence extends AppCompatActivity {

    /**
     * Name of an extra in an intent that is sent through an intent.
     */
    public static String EXTRA_ADDRESS = "device_address";

    /**
     * Name of an extra in an intent that is sent through an intent.
     */
    public static String FILES = "Files to be used in activity";

    /**
     * String holding the connected device's mac address
     */
    String m_address = null;

    /**
     * SPP UUID. Look for it
     */
    static final UUID MYUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    /**
     * Buttons that are used in the activity
     */
    Button btnGo, btnSelectFile, btnSelectFile2, btnSelectFile3, btnSelectFile4, btnSelectFile5;

    /**
     * Stores names of traversed directories
     */
    ArrayList<String> str = new ArrayList<String>();

    /**
     * Stores files that have been selected
     */
    ArrayList<String> files = new ArrayList<String>();

    /**
     * Boolean to check if the first level of the directory structure is the one showing
     */
    private Boolean firstLvl = true;

    /**
     * Array of files
     */
    private Item[] fileList;

    /**
     * Default path that is loaded for users to select a file from
     */
    //private File path = new File(Environment.getRootDirectory() + "");
    private File path = new File(Environment.getExternalStorageDirectory() + "/DCIM/100MEDIA/");
    //"storage/extSdCard/DCIM/Camera/");

    /**
     * String holding the name of the last selected file
     */
    private String chosenFile;

    /**
     * int holding the dialog_load_file number
     */
    private static final int DIALOG_LOAD_FILE = 1000;

    /**
     * ListAdapter used to display the directory and file names
     */
    ListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent newInt = getIntent();
        setAddress(newInt.getStringExtra(MainActivity.EXTRA_ADDRESS));

        setContentView(R.layout.activity_intermediate_reminiscence);

        btnGo = (Button)findViewById(R.id.btn_go);
        btnSelectFile = (Button) findViewById(R.id.btn_select);
        btnSelectFile2 = (Button) findViewById(R.id.btn_select2);
        btnSelectFile3 = (Button) findViewById(R.id.btn_select3);
        btnSelectFile4 = (Button) findViewById(R.id.btn_select4);
        btnSelectFile5 = (Button) findViewById(R.id.btn_select5);

        btnSelectFile2.setVisibility(View.INVISIBLE);
        btnSelectFile3.setVisibility(View.INVISIBLE);
        btnSelectFile4.setVisibility(View.INVISIBLE);
        btnSelectFile5.setVisibility(View.INVISIBLE);

        btnGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(IntermediateReminiscence.this, ReminiscenceActivity.class);
                intent.putExtra(EXTRA_ADDRESS, getAddress());
                intent.putExtra(FILES, files);
                startActivity(intent);
                setContentView(R.layout.activity_reminiscence);
                finish();
            }
        });

        btnSelectFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadFileList();
                showDialog(DIALOG_LOAD_FILE);
                btnSelectFile2.setVisibility(View.VISIBLE);
                btnSelectFile.setText("File Selected");
                //btnSelectFile.setText(files.get(0));
            }
        });

        btnSelectFile2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadFileList();
                showDialog(DIALOG_LOAD_FILE);
                btnSelectFile3.setVisibility(View.VISIBLE);
                //btnSelectFile2.setText(files.get(1));
            }
        });

        btnSelectFile3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadFileList();
                showDialog(DIALOG_LOAD_FILE);
                btnSelectFile4.setVisibility(View.VISIBLE);
                //btnSelectFile3.setText(files.get(2));
            }
        });

        btnSelectFile4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadFileList();
                showDialog(DIALOG_LOAD_FILE);
                btnSelectFile5.setVisibility(View.VISIBLE);
                //btnSelectFile4.setText(files.get(3));
            }
        });

        btnSelectFile5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadFileList();
                showDialog(DIALOG_LOAD_FILE);
                //btnSelectFile5.setText(files.get(4));
            }
        });

    }

    /**
     * Provides a quicker way to create Toasts on the screen
     * @param s the message to be included in the toast
     */
    private void msg(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }

    /**
     * Method to get the bluetooth device's address
     * @return m_address the bluetooth device's address
     */
    public String getAddress() {
        return m_address;
    }

    /**
     * Method to set the bluetooth device's address
     * @return m_address the bluetooth device's address
     */
    public void setAddress(String string) {
        m_address = string;
    }

    /**
     * Loads file list from path
     */
    private void loadFileList() {
        try {
            path.mkdirs();
        } catch (SecurityException e) {
            Log.e("Error", e.toString());
        }

        // Checks whether path exists
        if (path.exists()) {
            FilenameFilter filter = new FilenameFilter() {
                @Override
                public boolean accept(File dir, String filename) {
                    File sel = new File(dir, filename);
                    // Filters based on whether the file is hidden or not
                    return (sel.isFile() || sel.isDirectory()) && !sel.isHidden();
                }
            };

            String[] fList = path.list(filter);
            fileList = new Item[fList.length];
            for (int i = 0; i < fList.length; i++) {
                fileList[i] = new Item(fList[i], R.mipmap.file_icon);
                // Convert into file path
                File sel = new File(path, fList[i]);

                // Set drawables
                if (sel.isDirectory()) {
                    fileList[i].icon = R.mipmap.directory_icon;
                    Log.d("DIRECTORY", fileList[i].fileName);
                } else {
                    Log.d("FILE", fileList[i].fileName);
                }
            }

            if (!firstLvl) {
                Item temp[] = new Item[fileList.length + 1];
                for (int i = 0; i < fileList.length; i++) {
                    temp[i + 1] = fileList[i];
                    }
                temp[0] = new Item("Up", R.mipmap.directory_up);
                    fileList = temp;
            }
        } else {
            Log.e("loadFileList", "path does not exist");
        }

        adapter = new ArrayAdapter<Item>(this,
                android.R.layout.select_dialog_item, android.R.id.text1, fileList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                // creates view
                View view = super.getView(position, convertView, parent);
                TextView textView = (TextView) view.findViewById(android.R.id.text1);
                // put the image on the text view
                textView.setCompoundDrawablesWithIntrinsicBounds(fileList[position].icon, 0, 0, 0);
                 // add margin between image and text (support various screen densities)
                int dp5 = (int) (5 * getResources().getDisplayMetrics().density + 0.5f);
                textView.setCompoundDrawablePadding(dp5);
                return view;
            }
        };

    }

    /**
     * Class that represents an item in a directory. Each item has a file name and an icon.
     */
    private class Item {
        public String fileName;
        public int icon;

        public Item(String fileName, Integer icon) {
            this.fileName = fileName;
            this.icon = icon;
        }

        @Override
        public String toString() {
            return fileName;
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        Dialog dialog = null;
        AlertDialog.Builder builder = new Builder(this);

        if (fileList == null) {
            Log.e("Dialog", "No files loaded");
            dialog = builder.create();
            return dialog;
        }

        switch (id) {
            case DIALOG_LOAD_FILE:
                builder.setTitle("Choose your file");
                builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        chosenFile = fileList[which].fileName;
                        File sel = new File(path + "/" + chosenFile);
                        if (sel.isDirectory()) {
                            firstLvl = false;
                            // Adds chosen directory to list
                            str.add(chosenFile);
                            fileList = null;
                            path = new File(sel + "");
                            loadFileList();
                            removeDialog(DIALOG_LOAD_FILE);
                            showDialog(DIALOG_LOAD_FILE);
                        }
                        // Checks if 'up' was clicked
                        else if (chosenFile.equalsIgnoreCase("up") && !sel.exists()) {
                            // present directory removed from list
                            String s = str.remove(str.size() - 1);
                            // path modified to exclude present directory
                            path = new File(path.toString().substring(0, path.toString().lastIndexOf(s)));
                            fileList = null;

                            // if there are no more directories in the list, then
                            // its the first level
                            if (str.isEmpty()) {
                                firstLvl = true;
                            }
                            loadFileList();
                            removeDialog(DIALOG_LOAD_FILE);
                            showDialog(DIALOG_LOAD_FILE);
                        }
                        // File picked
                        else {
                            files.add(chosenFile);
                        }
                    }
                });
                break;
            }
            dialog = builder.show();
        return dialog;
    }
}
