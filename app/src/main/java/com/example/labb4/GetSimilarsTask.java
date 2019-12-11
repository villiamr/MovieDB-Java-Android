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

public class GetSimilarsTask extends AsyncTask<Void, Void, Void> {
    static ArrayList<String> titlelist = new ArrayList<String>();
    static ArrayList<String> idlist = new ArrayList<String>();
    public SearchesActivity mainActivity;

    public GetSimilarsTask(SearchesActivity mainActivity) {

        this.mainActivity = mainActivity;

    }

    public GetSimilarsTask() {

    }

    @Override

    // Denna körs asyncront med andra tråden. Hämtar ut JSON från apit, söker igenom strängen och lägger in similarmovies i arrayen.
    protected Void doInBackground(Void... params) {

        StringBuilder builder = new StringBuilder();
        URL url;
        String place = mainActivity.returnId();
        HttpURLConnection urlConnection = null;
        try {
            idlist.clear();
            titlelist.clear();
            url = new URL("https://www.myapifilms.com/tmdb/movieInfoImdb?idIMDB="+ place +"&token=4ce0d6f7-047c-4741-8a11-e97bf495a129&format=json&language=en&alternativeTitles=0&casts=0&images=0&keywords=0&releases=0&videos=0&translations=0&similar=1&reviews=0&lists=0");
            Log.i("Eservice-json", "Request: " + url.toURI().toString());
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

        try {
            JSONObject object = new JSONObject(jsonStr);
            JSONObject jsonObjectData = object.getJSONObject("data");
            JSONArray arr = jsonObjectData.getJSONArray("similar");

            for (int j = 0; j < arr.length(); j++) {
                titlelist.add(arr.getJSONObject(j).getString("title"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override

    // Kör setSimilar-metoden när doinbackground är klar.
    protected void onPostExecute(Void result) {
        mainActivity.setSimilar();
        super.onPostExecute(result);
    }

    // För att skicka arrayen till main aktiviteten.
    public ArrayList<String> getTitleList() {
        return titlelist;

    }
}

