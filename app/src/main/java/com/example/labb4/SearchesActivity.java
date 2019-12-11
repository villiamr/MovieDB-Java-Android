package com.example.labb4;

import android.content.Intent;
import java.util.ArrayList;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;



import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SearchesActivity extends Activity {
    ArrayAdapter aa;
    ArrayList<String> titlelist = new ArrayList<String>();
    ArrayList<String> idlist = new ArrayList<String>();
    String finalid;
    String data;
    private final String FILENAME = "databas.txt";


    // Metod för att lägga in titlarna i listan.
    public void setTitles() {
        GetTitlesTask ma = new GetTitlesTask();
        titlelist = ma.getTitleList();

        aa = new ArrayAdapter(SearchesActivity.this,
                android.R.layout.simple_list_item_1, SearchesActivity.this.titlelist);
        ListView lw = findViewById(R.id.listView1);
        lw.setAdapter(aa);
    }

    // För att uppdatera listan med similar movies.
    public void setSimilar() {
        GetSimilarsTask ma2 = new GetSimilarsTask();
        titlelist = ma2.getTitleList();
        aa = new ArrayAdapter(SearchesActivity.this,
                android.R.layout.simple_list_item_1, SearchesActivity.this.titlelist);
        ListView lw = findViewById(R.id.listView1);
        lw.setAdapter(aa);
    }

    // För att uppdatera listvyn med actors.
    public void setActors() {
        GetActorsTask ma3 = new GetActorsTask();
        titlelist = ma3.getTitleList();
        aa = new ArrayAdapter(SearchesActivity.this,
                android.R.layout.simple_list_item_1, SearchesActivity.this.titlelist);
        ListView lw = findViewById(R.id.listView1);
        lw.setAdapter(aa);
    }

    public String returnId(){
        return finalid;
    }


    // Hämtar vad som skrivs in i textfältet och returnar så asynctasken kan ta hand om det.
    public String getName(){
        EditText titlestring = (EditText) findViewById(R.id.track);
        String formatedTitle = titlestring.getText().toString().replace(' ', '_');
        return formatedTitle;
    }




    private void writeToFile(String data) throws FileNotFoundException {

        // Skriver till textfil som jag använder för att lagra sparade filmer. lägger med radbrytning för att sedan
        // kunna skriva ut till listvyn eller söka reda på en specifik att radera.

        try {
            FileOutputStream fos = openFileOutput(FILENAME, getApplicationContext().MODE_APPEND);

            fos.write(data.getBytes());
            fos.write("\n".getBytes());
            fos.close();

            Toast.makeText(SearchesActivity.this,
                    "You have saved " + data + " to the database.", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.toString();


        }


    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        setContentView(R.layout.activity_searches);
        Button button = findViewById(R.id.button1);
        ListView lw = findViewById(R.id.listView1);
        Button similarBtn = findViewById(R.id.button6);
        Button saveBtn = findViewById(R.id.button4);
        Button savedBtn = findViewById(R.id.button2);
        Button getActors = findViewById(R.id.button7);
        saveBtn.setVisibility(Button.GONE);
        similarBtn.setVisibility(Button.GONE);
        getActors.setVisibility(Button.GONE);

        lw.setOnItemClickListener(new OnItemClickListener() {

            @Override

            // Skickas till ny aktivitet om jag trycker på nån av listitemsen.
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                data =(String)parent.getItemAtPosition(position);
                GetTitlesTask ma = new GetTitlesTask();
                idlist = ma.getIdList();
                finalid = idlist.get(position);

            }
        });


        button.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                EditText titlestring = findViewById(R.id.track);
                String checker = titlestring.getText().toString();
                // Kollar så att textfältet inte är tomt eller endast innehåller ett mellanslag, ifall det gör det så skrivs en toast.
                if(checker.equals("") || checker.equals(" ")) {
                    Toast.makeText(SearchesActivity.this,
                            "You have to input a part of a movie title.", Toast.LENGTH_LONG).show();
                }

                // Visar knapparna efter att man sökt på en sträng. Startar även första asynctasken för att hämta från api:t.
                else {
                    Button similarBtn = findViewById(R.id.button6);
                    Button saveBtn = findViewById(R.id.button4);
                    Button getActors = findViewById(R.id.button7);
                    saveBtn.setVisibility(Button.VISIBLE);
                    similarBtn.setVisibility(Button.VISIBLE);
                    getActors.setVisibility(Button.VISIBLE);
                    GetTitlesTask backgroundTask = new GetTitlesTask(
                            SearchesActivity.this);
                    backgroundTask.execute();
                }
            }

        });

        similarBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if(data == null){
                    Toast.makeText(SearchesActivity.this,
                            "You have to choose one of the listed movies.", Toast.LENGTH_LONG).show();
                }
                else {
                    // Startar asynctask 2 för att söka på liknande filmer. hämtar från api:t.

                    GetSimilarsTask backgroundTask = new GetSimilarsTask(
                            SearchesActivity.this);
                    backgroundTask.execute();
                }

            }

        });

        saveBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if(data == null){
                    Toast.makeText(SearchesActivity.this,
                            "You have to choose one of the listed movies.", Toast.LENGTH_LONG).show();
                }
                else {
                    try {
                        writeToFile(data);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }

        });

        savedBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(SearchesActivity.this, SavedTitlesActivity.class);
                startActivity(intent);
            }

        });

        getActors.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if(data == null){
                    Toast.makeText(SearchesActivity.this,
                            "You have to choose one of the listed movies.", Toast.LENGTH_LONG).show();
                }

                else {
                    // Gömmer oväsentliga knappar när man trycker på "GET ACTORS".
                    // Startar även asynctask 3 för att hämta actors från api:t.
                    Button similarBtn = findViewById(R.id.button6);
                    Button saveBtn = findViewById(R.id.button4);
                    Button getActors = findViewById(R.id.button7);
                    saveBtn.setVisibility(Button.GONE);
                    similarBtn.setVisibility(Button.GONE);
                    getActors.setVisibility(Button.GONE);

                    GetActorsTask backgroundTask = new GetActorsTask(
                            SearchesActivity.this);
                    backgroundTask.execute();
                }
            }

        });

    }
}