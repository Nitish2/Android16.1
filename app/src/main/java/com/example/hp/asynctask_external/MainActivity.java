package com.example.hp.asynctask_external;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    // Assigning values to the variables
    private static final int WRITE_REQUEST_CODE = 50;
    static String FILENAME = "ReadWriteFile.txt";
    // Declaring variables
    EditText editText;
    TextView textView;
    Button add, delete;
    File file;

    //@RequiresApi shows that the annotated element should only be called on the given API level or higher.
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Creating and Initializing objects by ID.
        editText = (EditText) findViewById(R.id.enter_text);
        textView = (TextView) findViewById(R.id.display_data);
        add = (Button) findViewById(R.id.add_data);
        delete = (Button) findViewById(R.id.delete);

        // Permission for External storage
        String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
        requestPermissions(permissions, WRITE_REQUEST_CODE);

          /*
         * Environment provides access to environment variables.
         * getExternalStorageDirectory() will return the primary external storage directory.
         */
        //Creating new file in the external storage .
        file = new File(Environment.getExternalStorageDirectory(), FILENAME);
        try {
            if (file.createNewFile()){
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        //Adding data into the file
        add.setOnClickListener(new View.OnClickListener() { //SetOnClickListener
            @Override
            public void onClick(View view) {
                String string=editText.getText().toString();
                editText.setText(""); // It will set the string data entered on the editText
                // ReadFile will read data from the file stored in external storage
                ReadFile readFile = new ReadFile(file); // Creating object
                readFile.execute(string); // File Execution
                Toast.makeText(getApplicationContext(), "File Created", //Toast Message
                        Toast.LENGTH_LONG).show();
            }
        });
        //Deleting the whole file and data stored in it .
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                file.delete(); //Deleting file
                Toast.makeText(getApplicationContext(), "File is Deleted", //Toast Message
                        Toast.LENGTH_LONG).show();
            }
        });

    }

    /*
      * Extends ReadFile class with AsyncTask.
      * AsyncTask enables proper and easy use of the UI thread.
      * AsyncTask<Params, Progress, Result>
       Params, the type of the parameters sent to the task upon execution.
       Progress, the type of the progress units published during the background computation.
       Result, the type of the result of the background computation.
     */
    private class ReadFile extends AsyncTask<String, Integer, String> {

        File fRead;

        public ReadFile(File fileRead) {
            super();
            this.fRead=fileRead;

        }
        // doInBackground is used to perform background computation .
        @Override
        protected String doInBackground(String... strings) {
            String enter="\n";
            FileWriter filewriter=null;
            try {
                filewriter=new FileWriter(fRead,true);
                filewriter.append(strings[0].toString()); // Write into the file
                filewriter.append(enter); // On press enter it will append
                /*
                flush()Flushes this output stream and forces any buffered output bytes
                to be written out.
                 */
                filewriter.flush();
            }
            catch (IOException e) {
                e.printStackTrace();
            }finally { // finally will execute wheather any function executes or not .
                try {
                    filewriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }
        /*
        onPostExecute(), invoked on the UI thread after the background computation finishes.
        The result of the background computation is passed to this step as a parameter.
         */
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            String name = "";
            StringBuilder stringBuilder = new StringBuilder();
            FileReader fileReader = null;
            try {
                fileReader = new FileReader(fRead);
                // StringBuilder is used to create mutable string.
                BufferedReader  bufferedReader = new BufferedReader(fileReader);//Creating object
                while ((name = bufferedReader.readLine()) != null) { // While loop
                    stringBuilder.append(name);
                    stringBuilder.append("\n");

                }
                bufferedReader.close(); // Closing buffer
                fileReader.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            textView.setText(stringBuilder.toString()); // Set the stored data on the text view

        }
    }

    /*
      * onRequestPermissionsResult is callback for the result from requesting permissions.
      * This method is invoked for every call on requestPermissions().
      * The requested permissions is never null.
      * grantResults for the corresponding permissions which is either
         PERMISSION_GRANTED or PERMISSION_DENIED.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        //Passing request code  to request for the permissions
        switch (requestCode) {
            case WRITE_REQUEST_CODE:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(getApplicationContext(), "Permission Granted ", //Toast Message
                            Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getApplicationContext(), "Permission Denied ", //Toast Message
                            Toast.LENGTH_SHORT).show();

                }
                break;
        }
    }

}