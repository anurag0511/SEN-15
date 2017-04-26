package com.example.root.sample;

/**
 * Created by root on 12/4/15.
 */

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class JSONTransmitter extends AsyncTask<Void,Void, String > {

String url = "";

JSONArray json;
String response=null;
    ArrayList<String> p;
    int i;
    SQLiteDatabase newDB;
    Context context;


    public JSONTransmitter(JSONArray ja,String u, ArrayList<String> pp, int ii, SQLiteDatabase ndb , Context c){
        this.json = ja;
        this.url = u;
        this.p=pp;
        this.i=ii;
        this.newDB=ndb;
        this.context=c;


    }

    public String getResult()
    {
        return response;
    }
    @Override
    protected String doInBackground(Void... params) {

        HttpClient client = new DefaultHttpClient();
        HttpConnectionParams.setConnectionTimeout(client.getParams(), 100000);

        JSONObject jsonResponse = null;
        HttpPost post = new HttpPost(url);
        String page = null;
        try {
            StringEntity se = new StringEntity("json="+json.toString());
            post.addHeader("content-type", "application/x-www-form-urlencoded");
            post.setEntity(se);

            HttpResponse response;
            Log.d("cccccccc:","About to execute");
            response = client.execute(post);
           // Log.d("ddddd:",response.toString());

            BufferedReader in = new BufferedReader
                    (new InputStreamReader(response.getEntity().getContent()));
            StringBuffer sb = new StringBuffer("");
            String line = "";
            String NL = System.getProperty("line.separator");
            while ((line = in.readLine()) != null) {
                sb.append(line + NL);
            }
            in.close();
            page = sb.toString();
            System.out.println("response page:"+page);

            //resFromServer = org.apache.http.util.EntityUtils.toString(response.getEntity());

            //jsonResponse=new JSONObject(resFromServer);
            //Log.i("Response from server", jsonResponse.getString("msg"));
        } catch (Exception e) { e.printStackTrace();}

        return page;
    }

    @Override
    protected void onPostExecute(String s) {

       if (s.contains("true"))
       {

            SQLiteAdapter sla= new SQLiteAdapter(context);
           SQLiteDatabase newDB= sla.openToWrite();
           Log.d("dbbbbb:", newDB.isOpen()+"" );
            Log.d("resssssssssp", s);
           newDB.execSQL("UPDATE "+ p.get(i) +" SET synced = 1 where synced=0");

         //   newDB.("UPDATE D45_3 SET synced = 1 where synced=0", null);
           Log.d("Updated to 1:","ddddddd");
           sla.close();
           newDB.close();


            //update the cols to 1
        //}

    }
    }
}