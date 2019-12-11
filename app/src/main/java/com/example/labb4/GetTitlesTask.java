package com.example.labb4;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;

public class GetTitlesTask extends AsyncTask<Void, Void, Void> {
    static ArrayList<String> titlelist = new ArrayList<String>();
    static ArrayList<String> idlist = new ArrayList<String>();
    public SearchesActivity mainActivity;

    public GetTitlesTask(SearchesActivity mainActivity) {

        this.mainActivity = mainActivity;

    }

    public GetTitlesTask() {

    }


    @Override

    // Denna körs asyncront med andra tråden. Hämtar ut api:t, Söker igenom strängen och lägger in id och titel i två arrayer.
    protected Void doInBackground(Void... params) {


        // Hämtar ut JSON från apit.
        StringBuilder builder = new StringBuilder();
        URL url;
        String place = mainActivity.getName();
        HttpURLConnection urlConnection = null;
        try {
            idlist.clear();
            titlelist.clear();
            url= new URL("https://www.myapifilms.com/tmdb/searchMovie?movieName=" + place + "&token=4ce0d6f7-047c-4741-8a11-e97bf495a129&format=json&language=en&includeAdult=1");
            Log.i("Eservice-json", "Request: "+url.toURI().toString());
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream content = null;
            content = urlConnection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(content));
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        } catch (URISyntaxException e1) {
            e1.printStackTrace();
        }
            String jsonStr = builder.toString();

        // Söker ut från jsonobjektet, sedan från jsonarrayen och i den lägger jag till id och titel i två olika arraylists.

            try {
            JSONObject object = new JSONObject(jsonStr);
            JSONObject jsonObjectData = object.getJSONObject("data");
            JSONArray arr = jsonObjectData.getJSONArray("results");

            for (int j = 0; j < arr.length(); j++) {
                idlist.add(arr.getJSONObject(j).getString("id"));
                titlelist.add(arr.getJSONObject(j).getString("title"));


            }
                

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override

    // Kallar på setTitles-metoden när doinbackground är klar.
    protected void onPostExecute(Void result) {
        mainActivity.setTitles();
        super.onPostExecute(result);
    }

    // För att skicka arrayen till main aktiviteten.
    public ArrayList<String> getTitleList() {
        return titlelist;

    }

    // För att skicka arrayen till mainaktiviteten.
    public ArrayList<String> getIdList() {
        return idlist;
    }

}
