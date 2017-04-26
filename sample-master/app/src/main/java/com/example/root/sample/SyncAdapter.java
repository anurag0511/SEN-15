package com.example.root.sample;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import static android.database.sqlite.SQLiteDatabase.openOrCreateDatabase;

/**
 * //Author: Aditi Bhatnagar
 .
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {
    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }


    /*
     * Specify the code you want to run in the sync adapter. The entire
     * sync adapter runs in a background thread, so you don't have to set
     * up your own background processing.
     */
    @Override
    public void onPerformSync(
            Account account,
            Bundle extras,
            String authority,
            ContentProviderClient provider,
            SyncResult syncResult) {
    /*
     * Put the data transfer code here.
     */

    SQLiteDatabase newDB = null;
            JSONObject jObject;
            JSONArray jArray = new JSONArray();

        try {
            newDB = SQLiteDatabase.openDatabase("surdb", null, SQLiteDatabase.CONFLICT_NONE);
        } catch (SQLException e) {
            Log.d("Error", "Error while Opening Database");
            e.printStackTrace();
        }

        Cursor cq = newDB.rawQuery("SELECT name FROM "+ "surdb" +" WHERE type='table'", null);


        if (cq.moveToFirst()) {
            while (!cq.isAfterLast()) {
              //  Toast.makeText(activityName.this, "Table Name=> " + c.getString(0), Toast.LENGTH_LONG).show();



                try {

  //we need ngo id and form id to be passed as params to php

                    Cursor c = newDB.rawQuery("Select * from "+ cq.getString(0) +" where synced = 0", null);
                    Log.d("Tblllll:",cq.getString(0));
                    int cnt = c.getColumnCount();
                    if (c.getCount() == 0) {
                        c.close();
                        Log.d("", "no items on the table");
                    } else {
                        c.moveToFirst();

                        int j = 1;

                        while (c.moveToNext()) {
                            jObject = new JSONObject();

                            for (j = 1; j <= cnt; j++) {
                                jObject.put("" + j, c.getString(c.getColumnIndex("" + j)));

                            }

                            jArray.put(jObject);


                        }

                        c.close();
                        Log.d("", "ALL the data in the DB" + jArray.toString());
                        int arrayLength = jArray.length();
                        Log.d("", "Length of the jArray" + arrayLength);
                        HttpParams httpParams = new BasicHttpParams();
                        HttpConnectionParams.setConnectionTimeout(httpParams, 9000);
                        HttpConnectionParams.setSoTimeout(httpParams, 9000);

                        HttpClient client = new DefaultHttpClient(httpParams);

                        String url = "http://192.168.64.1:80/team14/updateDatabase.php?arrayLength=" + arrayLength+"&colcount="+(cnt-1)+"&ngoid_formid="+cq.getString(0);

                        HttpPost request = new HttpPost(url);
                        request.setEntity(new ByteArrayEntity(jArray.toString().getBytes("UTF8")));
                        request.setHeader("json", jArray.toString());
                        Log.d("Beforeeee:", "eeeee");
                        HttpResponse response = client.execute(request);

                        HttpEntity entity = response.getEntity();
                        // If the response does not enclose an entity, there is no need
                        Log.d("Afterrrr:", ""+entity);

                        if (entity != null) {
                            InputStream instream = entity.getContent();


                            String result = getStringFromInputStream(instream);
                            if (result.equalsIgnoreCase("true")) {

                                Log.d("Synciinnnng", "1");

                                newDB.rawQuery("UPDATE "+ cq.getString(0) + " SET synced= 1",null);




                                //update the cols to 1
                            }
                            Log.i("Read from server", result);
                        }
                    }
                } catch (UnsupportedEncodingException uee) {
                    Log.d("Exceptions", "UnsupportedEncodingException");
                    uee.printStackTrace();
                } catch (Throwable t) {
                    Log.d("", "request fail" + t.toString());
                }

                newDB.close();

                cq.moveToNext();
               // ArrayList<String> arrTblNames = new ArrayList<String>();
                //SQLiteDatabase db =openOrCreateDatabase("surdb",SQLiteDatabase.CREATE_IF_NECESSARY,null);
                //Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

                //if (c.moveToFirst()) {
                //  while ( !c.isAfterLast() ) {
                //    arrTblNames.add( c.getString( c.getColumnIndex("name")) );
                //  c.moveToNext();
            }
        }
                cq.close();


            }



    private static String getStringFromInputStream(InputStream is) {

        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();

        String line;
        try {

            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return sb.toString();

    }
        }








