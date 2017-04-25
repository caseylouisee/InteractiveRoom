package com.example.caseydenner.interactiveroom;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

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
     * int holding the dialog_load_file number
     */
    private static final int DIALOG_LOAD_FILE = 1000;

    /**
     * Buttons that are used in the activity to select files
     */
    private Button m_btnGo, m_btnSelectFile, m_btnSelectFile2, m_btnSelectFile3, m_btnSelectFile4;

    /**
     * ListAdapter used to display the directory and file names
     */
    private ListAdapter m_adapter;

    /**
     * String holding the connected device's mac address
     */
    private String m_address = null;

    /**
     * Stores names of traversed directories
     */
    private ArrayList<String> m_traversed = new ArrayList<String>();

    /**
     * Stores files that have been selected
     */
    private ArrayList<String> m_files = new ArrayList<String>();

    /**
     * Boolean to check if the first level of the directory structure is the one showing
     */
    private Boolean m_firstLvl = true;

    /**
     * Array of files
     */
    private Item[] m_fileList;

    /**
     * Default path that is loaded for users to select a file from
     */
    private File m_path = new File(Environment.getExternalStorageDirectory() + "/DCIM/100MEDIA/");
    //"storage/extSdCard/DCIM/Camera/");

    /**
     * String holding the name of the last selected file
     */
    private String m_chosenFile;

    /**
     * Method to get the bluetooth device's address
     * @return m_address the bluetooth device's address
     */
    public String getAddress() {
        final String METHOD = "getAddress";
        Log.d(METHOD, "getting address");
        return m_address;
    }

    /**
     * Method to set the bluetooth device's address
     * @return m_address the bluetooth device's address
     */
    public void setAddress(String string) {
        final String METHOD = "setAddress";
        Log.d(METHOD, "Address set to " + string);
        m_address = string;
    }

    /**
     * Method to return the m_traversed arrayList
     * @return m_traversed
     */
    public ArrayList<String> getTraversed(){
        return m_traversed;
    }

    /**
     * Method to set the m_traversed arrayList to the parameter
     * @param arrayList to set m_traversed to
     */
    public void setTraversed(ArrayList<String> arrayList){
        m_traversed = arrayList;
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
     * returns the value of m_firstLvl
     * @return boolean of m_firstLvl
     */
    public boolean getFirstLvl(){
        return m_firstLvl;
    }

    /**
     * Method to set m_firstLvl to the bool parameter
     * @param bool to set m_firstLvl to
     */
    public void setFirstLvl(Boolean bool){
        m_firstLvl = bool;
    }

    /**
     * Returns m_fileList
     * @return m_fileList
     */
    public Item[] getFileList(){
        return m_fileList;
    }

    /**
     * Sets the m_fileList to the item [] in the parameter
     * @param item list of files
     */
    public void setFileList(Item[] item){
        m_fileList = item;
    }

    /**
     * Method to return the file path needed to get user's videos
     * @return m_path which is the file of the path where the user's videos are stored
     */
    public File getPath(){
        return m_path;
    }

    /**
     * Sets the m_path to the path in the method parameter
     * @param file to set the path to
     */
    public void setPath(File file){
        m_path = file;
    }

    /**
     * Method to return the m_chosenFile string
     * @return m_chosenFile
     */
    public String getChosenFile(){
        return m_chosenFile;
    }

    /**
     * Method to set the m_chosenFile to the string parameter
     * @param string to set m_chosenFile to
     */
    public void setChosenFile(String string){
        m_chosenFile = string;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final String METHOD = "onCreate";
        Log.d(METHOD, "Activity started");
        super.onCreate(savedInstanceState);
        Intent newInt = getIntent();
        setAddress(newInt.getStringExtra(MainActivity.EXTRA_ADDRESS));
        setContentView(R.layout.activity_intermediate_reminiscence);
        setView();
    }

    /**
     * Sets up the view and initializes the xml components
     */
    private void setView(){
        final String METHOD = "setView";
        Log.d(METHOD, "Setting View");

        m_btnGo = (Button)findViewById(R.id.btn_go);
        m_btnSelectFile = (Button) findViewById(R.id.btn_select);
        m_btnSelectFile2 = (Button) findViewById(R.id.btn_select2);
        m_btnSelectFile3 = (Button) findViewById(R.id.btn_select3);
        m_btnSelectFile4 = (Button) findViewById(R.id.btn_select4);

        m_btnSelectFile2.setVisibility(View.INVISIBLE);
        m_btnSelectFile3.setVisibility(View.INVISIBLE);
        m_btnSelectFile4.setVisibility(View.INVISIBLE);

        m_btnGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(IntermediateReminiscence.this, ReminiscenceActivity.class);
                intent.putExtra(EXTRA_ADDRESS, getAddress());
                intent.putExtra(FILES, getFiles());
                startActivity(intent);
                finish();
            }
        });

        m_btnSelectFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadFileList();
                showDialog(DIALOG_LOAD_FILE);
                m_btnSelectFile2.setVisibility(View.VISIBLE);
                m_btnSelectFile.setText("File Selected");
            }
        });

        m_btnSelectFile2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadFileList();
                showDialog(DIALOG_LOAD_FILE);
                m_btnSelectFile3.setVisibility(View.VISIBLE);
                m_btnSelectFile2.setText("File Selected");
            }
        });

        m_btnSelectFile3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadFileList();
                showDialog(DIALOG_LOAD_FILE);
                m_btnSelectFile4.setVisibility(View.VISIBLE);
                m_btnSelectFile3.setText("File Selected");
            }
        });

        m_btnSelectFile4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadFileList();
                showDialog(DIALOG_LOAD_FILE);
                m_btnSelectFile4.setText("File Selected");
            }
        });

    }

    /**
     * Loads file list from path
     */
    private void loadFileList() {
        final String METHOD = "loadFileList";
        Log.d(METHOD, "Loading file list");
        try {
            getPath().mkdirs();
        } catch (SecurityException e) {
            Log.e("Error", e.toString());
        }

        // Checks whether path exists
        if (getPath().exists()) {
            FilenameFilter filter = new FilenameFilter() {
                @Override
                public boolean accept(File dir, String filename) {
                    File sel = new File(dir, filename);
                    // Filters based on whether the file is hidden or not
                    return (sel.isFile() || sel.isDirectory()) && !sel.isHidden();
                }
            };

            String[] fList = getPath().list(filter);
            setFileList(new Item[fList.length]);
            for (int i = 0; i < fList.length; i++) {
                getFileList()[i] = new Item(fList[i], R.mipmap.file_icon);
                // Convert into file path
                File sel = new File(getPath(), fList[i]);

                // Set drawables
                if (sel.isDirectory()) {
                    getFileList()[i].icon = R.mipmap.directory_icon;
                    Log.d("DIRECTORY", getFileList()[i].fileName);
                } else {
                    Log.d("FILE", getFileList()[i].fileName);
                }
            }

            if (!getFirstLvl()) {
                Item temp[] = new Item[getFileList().length + 1];
                for (int i = 0; i < getFileList().length; i++) {
                    temp[i + 1] = getFileList()[i];
                    }
                temp[0] = new Item("Up", R.mipmap.directory_up);
                    setFileList(temp);
            }
        } else {
            Log.e("loadFileList", "path does not exist");
        }

        m_adapter = new ArrayAdapter<Item>(this,
                android.R.layout.select_dialog_item, android.R.id.text1, m_fileList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                // creates view
                View view = super.getView(position, convertView, parent);
                TextView textView = (TextView) view.findViewById(android.R.id.text1);
                // put the image on the text view
                textView.setCompoundDrawablesWithIntrinsicBounds(getFileList()[position].icon,0,0,0);
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
        final String CLASS = "Item";
        private String fileName;
        private int icon;

        private Item(String fileName, Integer icon) {
            Log.d(CLASS, "New item constructed " + fileName);
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

        if (getFileList() == null) {
            Log.e("Dialog", "No files loaded");
            dialog = builder.create();
            return dialog;
        }

        switch (id) {
            case DIALOG_LOAD_FILE:
                builder.setTitle("Choose your file");
                builder.setAdapter(m_adapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                         setChosenFile(getFileList()[which].fileName);
                        File sel = new File(getPath() + "/" + getChosenFile());
                        if (sel.isDirectory()) {
                            setFirstLvl(false);
                            // Adds chosen directory to list
                            getTraversed().add(getChosenFile());
                            setFileList(null);
                            setPath(new File(sel + ""));
                            loadFileList();
                            removeDialog(DIALOG_LOAD_FILE);
                            showDialog(DIALOG_LOAD_FILE);
                        }
                        // Checks if 'up' was clicked
                        else if (getChosenFile().equalsIgnoreCase("up") && !sel.exists()) {
                            // present directory removed from list
                            String s = getTraversed().remove(getTraversed().size() - 1);
                            // path modified to exclude present directory
                            setPath(new File(getPath().toString().substring(0, getPath().toString().lastIndexOf(s))));
                            setFileList(null);

                            // if there are no more directories in the list, then
                            // its the first level
                            if (getTraversed().isEmpty()) {
                                setFirstLvl(true);
                            }
                            loadFileList();
                            removeDialog(DIALOG_LOAD_FILE);
                            showDialog(DIALOG_LOAD_FILE);
                        }
                        // File picked
                        else {
                            getFiles().add(getChosenFile());
                        }
                    }
                });
                break;
            }
            dialog = builder.show();
        return dialog;
    }
}
