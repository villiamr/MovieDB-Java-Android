package com.example.labb4;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Button;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;


public class SavedTitlesActivity extends Activity {
    private final String FILENAME = "databas.txt";
    ArrayAdapter aa;
    String data;
    ArrayList<String> deleteRecords = new ArrayList<String>();
    ArrayList<String> titlelist = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_savedtitles);
        Button deleteBtn = findViewById(R.id.button5);
        readFromFile();
        ListView lw = findViewById(R.id.listView1);

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setDeleteRecords();
            }

        });

        lw.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override

            // Skickas till ny aktivitet om jag trycker på nån av listitemsen.
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                data =(String)parent.getItemAtPosition(position);

            }
        });
    }


    // Metod för att kunna ta bort en film. 
    private void setDeleteRecords(){
        String line = null;
        String res = null;
        // rensar arraylistan med undansparade titlar.
        deleteRecords.clear();
        try {
            InputStream in = openFileInput(FILENAME);
            if (in != null) {
                InputStreamReader input = new InputStreamReader(in);
                BufferedReader buffreader = new BufferedReader(input);
                res = "";
                while ((line = buffreader.readLine()) != null) {
                    if (line.contains(data)) {
                        Toast.makeText(SavedTitlesActivity.this,
                                "You have deleted " + data + " from the database.", Toast.LENGTH_LONG).show();
                    } else {
                        deleteRecords.add(line);
                    }
                }
                in.close();

                // tömmer textfilen.
                FileOutputStream fox = openFileOutput(FILENAME, getApplicationContext().MODE_PRIVATE);
                fox.write(32);
                fox.close();


                // Loopar igenom nya arraylisten med de records jag vill ha kvar och skriver ut i filen igen.
                for(int i = 0; i < deleteRecords.size(); i++){
                    try {
                        FileOutputStream fos = openFileOutput(FILENAME, getApplicationContext().MODE_APPEND);
                        fos.write(deleteRecords.get(i).getBytes());
                        fos.write("\n".getBytes());
                        fos.close();
                    } catch (IOException e) {
                        e.toString();
                    }
                }
            }

            // Till sist så lägger jag in nya records i listvyn.
            aa = new ArrayAdapter(SavedTitlesActivity.this,
                    android.R.layout.simple_list_item_1, SavedTitlesActivity.this.deleteRecords);
            ListView lw = findViewById(R.id.listView1);
            lw.setAdapter(aa);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(),
                    e.toString() + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    // Läser från fil och skriver ut i listvyn.
    private void readFromFile() {
        String line = null;
        String res = null;
        try {
            InputStream in = openFileInput(FILENAME);
            if (in != null) {
                InputStreamReader input = new InputStreamReader(in);
                BufferedReader buffreader = new BufferedReader(input);
                res = "";
                while ((line = buffreader.readLine()) != null) {
                    res += line;
                    titlelist.add(line);
                }
                in.close();
                aa = new ArrayAdapter(SavedTitlesActivity.this,
                        android.R.layout.simple_list_item_1, SavedTitlesActivity.this.titlelist);
                ListView lw = findViewById(R.id.listView1);
                lw.setAdapter(aa);
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(),
                    e.toString() + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
