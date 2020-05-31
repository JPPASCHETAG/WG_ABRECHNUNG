package com.wgAbrechnung.wg_abrechnung;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HTTP_REQUEST extends AsyncTask<String, String, String> {

    @Override
    protected String doInBackground(String... strings) {
        HttpURLConnection conn = null;

        try {
            URL url;
            url = new URL(strings[0]);
            conn = (HttpURLConnection) url.openConnection();
            if( conn.getResponseCode() == HttpURLConnection.HTTP_OK ) {
                BufferedReader  br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                return br.readLine();
            }else{
                InputStream err = conn.getErrorStream();
                return "ERROR";
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(conn != null) {
                conn.disconnect();
            }
        }
        return null;
    }


    public JSONArray Output(String Input){
        try {
            JSONArray jsonArr = new JSONArray(Input);
            return jsonArr;
        }catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }






}
