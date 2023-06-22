package com.OmanSubadri_10120194_IF5;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FetchData extends AsyncTask<String, Void, List<HashMap<String, String>>> {
    private FetchDataListener listener;

    public FetchData(FetchDataListener listener) {
        this.listener = listener;
    }

    @Override
    protected List<HashMap<String, String>> doInBackground(String... params) {
        String data = "";
        try {
            data = downloadUrl(params[0]);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return parseJson(data);
    }

    @Override
    protected void onPostExecute(List<HashMap<String, String>> list) {
        if (list != null && list.size() > 0) {
            listener.onFetchDataSuccess(list);
            listener.onFetchDataComplete(list);
        } else {
            listener.onFetchDataFailure("No data found");
        }
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();
            iStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            data = sb.toString();
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (iStream != null) {
                iStream.close();
            }
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return data;
    }

    private List<HashMap<String, String>> parseJson(String data) {
        List<HashMap<String, String>> placesList = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(data);
            JSONArray jsonArray = jsonObject.getJSONArray("results");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject placeObj = jsonArray.getJSONObject(i);
                HashMap<String, String> placeMap = new HashMap<>();
                placeMap.put("place_name", placeObj.getString("name"));
                placeMap.put("vicinity", placeObj.getString("vicinity"));
                JSONObject latLngObj = placeObj.getJSONObject("geometry").getJSONObject("location");
                placeMap.put("lat", latLngObj.getString("lat"));
                placeMap.put("lng", latLngObj.getString("lng"));
                placesList.add(placeMap);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return placesList;
    }

    public interface FetchDataListener {
        void onFetchDataSuccess(List<HashMap<String, String>> placesList);

        void onFetchDataFailure(String errorMessage);

        void onFetchDataComplete(List<HashMap<String, String>> list);
    }
}
//10120194_OmanSubadri_IF5