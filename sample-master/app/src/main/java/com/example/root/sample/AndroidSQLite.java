package com.example.root.sample;

/**
 * Created by root on 30/3/15.
 */
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.ParseException;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;

public class AndroidSQLite extends Activity {
    private static String url_all_forms = "http://webscrapper.in/team14/get_all_forms.php";
    ListView listContent;
    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_FORMS = "forms";
    private static final String TAG_FID = "fid";
    private static final String TAG_TITLE = "title";
    private static final String TAG_XML = "xml";
    String ngoid = "";
    String volunteer_id = "";
    // products JSONArray


    // products JSONArray
    JSONArray products = null;
    private SQLiteAdapter mySQLiteAdapter;

    public void onBackPressed() {
        // Write your code here
        //moveTaskToBack(true);
        // RunForm.this.finish();
        // Switching to ListView screen
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(i);     //either save instance checkout how
    }

    /**
     * Called when the activity is first created.
     */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        listContent = (ListView) findViewById(R.id.contentlist);


        Intent startingIntent = getIntent();
        if (startingIntent == null) {

            finish();
            return;
        }

        ngoid = startingIntent.getStringExtra("ngoid");
        volunteer_id = startingIntent.getStringExtra("volid");
        listContent.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                TextView txtTechCharacteristic = (TextView) v.findViewById(R.id.id);
                String txt = (String) txtTechCharacteristic.getText();
                Intent i = new Intent(AndroidSQLite.this, RunForm.class);

                i.putExtra("ID", txt);
                i.putExtra("ngoID", ngoid);
                i.putExtra("volid", volunteer_id);
                startActivity(i);
            }


        });

        try {
            formsFetch();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

      /*
       *  Create/Open a SQLite database
       *  and fill with dummy content
       *  and close it
       */

        mySQLiteAdapter = new SQLiteAdapter(this);
        mySQLiteAdapter.openToRead();

        Cursor cursor = mySQLiteAdapter.queueAll(ngoid,volunteer_id);
        startManagingCursor(cursor);

        String[] from = new String[]{SQLiteAdapter.KEY_CONTENT, SQLiteAdapter.KEY_ID};
        int[] to = new int[]{R.id.text, R.id.id};

        Log.d("pseudo value:", cursor.getCount()+","+cursor.getColumnCount());

        SimpleCursorAdapter cursorAdapter =
                new SimpleCursorAdapter(this, R.layout.row, cursor, from, to);

        listContent.setAdapter(cursorAdapter);

        mySQLiteAdapter.close();


    }

    public void formsFetch() throws IOException, JSONException {
        if (haveNetworkConnection()) {
            mySQLiteAdapter = new SQLiteAdapter(this);

            mySQLiteAdapter.openToWrite();
            SQLiteAdapter sl = new SQLiteAdapter(this);
            SQLiteDatabase newDb = sl.openToWrite();


            //mySQLiteAdapter.deleteAll();
//        String xml="<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
            //              "<xmlgui>\n" +
            //            "<form id=\"1\" name=\"Robotics Club Registration\" submitTo=\"http://servername/xmlgui1-post.php\" ><field label=\"First Name\" type=\"text\" required=\"Y\" options=\"\"/><field label=\"Last Name\" type=\"text\" required=\"Y\" options=\"\"/>\n" +
            //          "<field label=\"Gender\" type=\"choice\" required=\"Y\" options=\"Male|Female|sdksfnekfn\"/>\n" +
            //        "<field label=\"Age on 15 Oct. 2010\" type=\"numeric\" required=\"N\" options=\"\"/>\n" +
            //      "<field label=\"checkbox\" type=\"CheckBox\" required=\"Y\" options=\"meoww|bowwwow|sfnjdnkjdbv\"/>\n" +
            //    "<field label=\"checkboxmeoww\" type=\"CheckBox2\" required=\"Y\" options=\"meoww|bowwwow|sfnjdnkjdbv\"/>\n" +
            //  "</form>\n" +
            //"</xmlgui>\n";
            // xml = xml.replace("\n", "\\n").replace("\r", "\\r");

//        String jStr = "{\"forms\":[{\"fid\":\"50\",\"title\":\"bmjana\",\"xml\":\"okiedokie\",\"updated_at\":\"2015-03-27 19:53:26\"},{\"fid\":\"55\",\"title\":\"Men Health\",\"xml\":\"wokeydokey\",\"updated_at\":\"2015-03-27 19:53:50\"},{\"fid\":\"58\",\"title\":\"child Health\",\"xml\":\"oladola\",\"updated_at\":\"2015-03-27 19:53:50\"}],\"success\":1}";

            //String jStr='{"forms":[{"fid":"1","title":"Agriculture Survey","xml":"<?xml version='1.0' encoding='utf-8'?><xmlgui><form id='1' name='Robotics Club Registration' submitTo='http:\/\/servername\/xmlgui-post.php'><field label='First Name' type='text' required='Y' options=''\/><field label='Last Name' type='text' required='Y' options=''\/><field label='Gender' type='choice' required='Y' options='Male|Female|sdksfnekfn'\/><field label='Age on 15 Oct. 2010' type='numeric' required='N' options=''\/><field label='checkbox' type='CheckBox' required='Y' options='meoww|bowwwow|sfnjdnkjdbv'\/><field label='checkboxmeoww' type='CheckBox2' required='Y' options='meoww|bowwwow|sfnjdnkjdbv'\/><\/form><\/xmlgui>'","updated_at":"2015-03-31 17:15:06"},{"fid":"2","title":"Women Health","xml":"<?xml version='1.0' encoding='utf-8'?><xmlgui><form id='1' name='Robotics Club Registration' submitTo='http:\/\/servername\/xmlgui-post.php'><field label='First Name' type='text' required='Y' options=''\/><field label='Last Name' type='text' required='Y' options=''\/><field label='Gender' type='choice' required='Y' options='Male|Female|sdksfnekfn'\/><field label='Age on 15 Oct. 2010' type='numeric' required='N' options=''\/><field label='checkbox' type='CheckBox' required='Y' options='meoww|bowwwow|sfnjdnkjdbv'\/><field label='checkboxmeoww' type='CheckBox2' required='Y' options='meoww|bowwwow|sfnjdnkjdbv'\/><\/form><\/xmlgui>'","updated_at":"2015-03-31 17:15:15"}],"success":1}';

            //String jStr= "{\"forms\":[{\"fid\":\"1\",\"title\":\"Agriculture Survey\",\"xml\":\"oa?xml version='1.0' encoding='utf-8'?caoaxmlguicaoaform id='1' name='RoboticsClubRegistration'caoafield label='FirstName' type='text' required='Y' options='' cbaoafield label='LastName' type='text' required='Y' options='' cbaoafield label='Gender' type='choice' required='Y' options='Male|Female' cbaoafield label='Age' type='numeric' required='N' options='' cbaoafield label='checkbox' type='CheckBox' required='Y' options='X|XII|Diploma' cbaoafield label='checkboxmeoww' type='CheckBox2' required='Y' options='Minor|Major|NA' cba obaformca obaxmlguica\",\"updated_at\":\"2015-04-06 03:07:57\"},{\"fid\":\"2\",\"title\":\"Women Health\",\"xml\":\"oa?xml version='1.0' encoding='utf-8'?caoaxmlguicaoaform id='1' name='RoboticsClubRegistration'caoafield label='FirstName' type='text' required='Y' options='' cbaoafield label='LastName' type='text' required='Y' options='' cbaoafield label='Gender' type='choice' required='Y' options='Male|Female|sdksfnekfn' cbaoafield label='Ageon15Oct2010' type='numeric' required='N' options='' cbaoafield label='checkbox' type='CheckBox' required='Y' options='code|teach|cook' cbaoafield label='checkboxmeoww' type='CheckBox2' required='Y' options='Stressed|Contended|Very Happy' cba obaformca obaxmlguica\",\"updated_at\":\"2015-04-06 03:08:05\"}],\"success\":1}";
            //     Log.d("jjjj:",""+jStr);
            url_all_forms += "?ngoid=" + ngoid + "&volunteer_id=" + volunteer_id;
            Log.d("uuuuu",url_all_forms);
            HttpResponse response = null;
            try {
                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet();
                request.setURI(new URI(
                        url_all_forms));
                response = client.execute(request);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            String responseText = null;
            try {
                responseText = EntityUtils.toString(response.getEntity());
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Log.i("Parse Exception", e + "");

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Log.i("IO Exception 2", e + "");

            }

            Log.i("responseText", responseText);

            JSONObject json = new JSONObject(responseText);
       /*     JSONObject json = null;
            JSONParser jp = new JSONParser();
            //Log.d("MMMMMMM","RRRRRR");
            url_all_forms += "?ngoid=" + ngoid + "&volunteer_id=" + volunteer_id;
           // HttpResponse response=null;
            HttpClient httpClient=new DefaultHttpClient();;
            HttpPost post = new HttpPost(url_all_forms);

            try {
                response = httpClient.execute(post);
                Log.d("rrrrrrrr", String.valueOf(response));
            } catch (IOException e) {
                e.printStackTrace();
            }
            HttpEntity entity = response.getEntity();
            String result = EntityUtils.toString(entity);
          Log.d("vvvvv:", result);
          //  json= new JSONObject(result.substring(result.indexOf("{"), result.lastIndexOf("}") + 1));
           // json = (JSONObject ) new JSONTokener(result).nextValue();
            /*BufferedReader in = new BufferedReader
                    (new InputStreamReader(response.getEntity().getContent()));
            StringBuffer sb = new StringBuffer("");
            String line = "";
            String NL = System.getProperty("line.separator");
            while ((line = in.readLine()) != null) {
                sb.append(line + NL);
            }
            in.close();
            String page = sb.toString();
*/
            // json = jp.makeHttpRequest(url_all_forms);
            Log.d("WWWWWWW", "YYYYY");

            //try {
            // json = new JSONObject(page);
            //Log.d("jjjj:", "" + json);
            //}
            //catch (JSONException e) {
            //e.printStackTrace();
            //}

            // Check your log cat for JSON reponse
            //   Log.d("All Forms: ", json.toString());


            try {
                // Checking for SUCCESS TAG
                int success = 0;
                try {
                    success = json.getInt(TAG_SUCCESS);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (success == 1) {
                    // products found
                    // Getting Array of Products
                    try {
                        products = json.getJSONArray(TAG_FORMS);
                        Log.d("Products no:",products.length()+"");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    mySQLiteAdapter.deleteAll();
                    // looping through All Products from table
                    Cursor cl;
                    for (int i = 0; i < products.length(); i++) {
                        JSONObject c = products.getJSONObject(i);

                        // Storing each json item in variable
                        String id = c.getString(TAG_FID);
                        Log.d("******: ", "preInserting .." + id);
                       // cl = newDb.rawQuery("Select Content from MYTABLE where volid =" + volunteer_id + " and _id=" + id, null);
                       // Log.d("cursorrr:",""+cl.getCount());
                       // if (cl.getCount() == 0)
                        //   Log.d("ididid: ", ""+db.getForm(Integer.parseInt(id)).getXML());

                        //if(db.getForm(Integer.parseInt(id))==null) {
                        //{
                            String name = c.getString(TAG_TITLE);

                            String xmll = c.getString(TAG_XML);
                            // creating new HashMap
                            Log.d("++++Insert: ", "Inserting ..");
                            mySQLiteAdapter.insert(id, name, xmll, volunteer_id,ngoid);
                        //}

//                        }


                    }
                }
                newDb.close();
                sl.close();
                mySQLiteAdapter.close();


            } catch (JSONException e) {
                e.printStackTrace();
            }
            Toast.makeText(AndroidSQLite.this, "Forms updated ! ", Toast.LENGTH_LONG).show();

        }
        else
        {
            Toast.makeText(AndroidSQLite.this, "New forms would be fetched, once connected to internet ! ", Toast.LENGTH_LONG).show();

        }

      /*
       *  Open the same SQLite database
       *  and read all it's content.
       */

        mySQLiteAdapter = new SQLiteAdapter(this);
        mySQLiteAdapter.openToRead();

        Cursor cursor = mySQLiteAdapter.queueAll(ngoid,volunteer_id);
        startManagingCursor(cursor);

        String[] from = new String[]{SQLiteAdapter.KEY_CONTENT, SQLiteAdapter.KEY_ID};
        int[] to = new int[]{R.id.text, R.id.id};

        Log.d("pseudo value:", cursor.getCount()+","+cursor.getColumnCount());

        SimpleCursorAdapter cursorAdapter =
                new SimpleCursorAdapter(this, R.layout.row, cursor, from, to);

        listContent.setAdapter(cursorAdapter);

        mySQLiteAdapter.close();




    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
// Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.sync, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
// Handle action bar item clicks here. The action bar will
// automatically handle clicks on the Home/Up button, so long
// as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
//noinspection SimplifiableIfStatement

        if(id == R.id.refresh)
        {
            try {
                formsFetch();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (id == R.id.sync) {

            SQLiteAdapter sla = new SQLiteAdapter(this);
            ArrayList<String> p = sla.getAllTable();


            Log.d("xxxxxxxxx:", "Entered");
            SQLiteDatabase newDB = null;
            JSONObject jObject;

            //String dbPath= "/data/data/"
            //        + getApplicationContext().getPackageName() + "/databases/"
            //      + "MY_DATABASE";

            try {
                newDB = sla.openToWrite();
                Log.d("dbdbdb:", newDB.toString());
            } catch (SQLException e) {
                Log.d("Error", "Error while Opening Database");
                e.printStackTrace();


            }


            for (int y = 0; y < p.size(); y++) {
                Log.d("TTTTNNNN:", p.get(y));
            }


            int i = 0;
            if (!p.isEmpty()) {
                while (i < p.size()) {
                    //  Toast.makeText(activityName.this, "Table Name=> " + c.getString(0), Toast.LENGTH_LONG).show();

                    //    Log.d("Tblllll:", cq.getString(0));
                    try {

                        //we need ngo id and form id to be passed as params to php


                        // Cursor c = newDB.rawQuery("Select * from " + p.get(i) + " where synced = '0'", null);

                        String cols[]=sla.getColumnNames(p.get(i));
                        String cll= cols[0];
                        for(int o=1; o<cols.length-1; o++)
                            cll=cll+", "+cols[o];

                        Cursor c = newDB.rawQuery("Select "+cll+" from " + p.get(i) + " where synced = 0 ", null);

                        Log.d("Tblllll:", p.get(i));
                        int cnt = c.getColumnCount();
                        Log.d("Unsssscolcnt:",cnt+"" );
                        if (c.getCount() == 0) {
                            c.close();
                            Log.d("", "no items on the table to be synced.");
                        } else {

                            JSONArray jArray = new JSONArray();
                            c.moveToFirst();

                            int j = 1;

                            while (c.moveToNext()) {
                                jObject = new JSONObject();

                                for (j = 1; j <= cnt; j++) {
                                    jObject.put("" + j, c.getString(j - 1));

                                }

                                jArray.put(jObject);


                            }

                            c.close();
                            Log.d("", "ALL the data in the table" + jArray.toString());
                            int arrayLength = jArray.length();

                            Log.d("", "Length of the jArray" + arrayLength);
                            //    HttpParams httpParams = new BasicHttpParams();
                            //  HttpConnectionParams.setConnectionTimeout(httpParams, 9000);
                            //HttpConnectionParams.setSoTimeout(httpParams, 9000);

                            //HttpClient client = new DefaultHttpClient(httpParams);

                            String url = "http://webscrapper.in/team14/updateDatabase.php?arrayLength=" + arrayLength + "&colcount=" + (cnt - 1) + "&ngoid_formid=" + p.get(i);
                            //Log.d("url:", url);
                            //HttpPost request = new HttpPost(url);
                            //request.setEntity(new ByteArrayEntity(jArray.toString().getBytes("UTF8")));
                            //request.setHeader("json", jArray.toString());
                            Log.d("Beforeeee:", "eeeee");
                            // HttpResponse response = client.execute(request);
                            JSONTransmitter jt = new JSONTransmitter(jArray,url,p,i,newDB, this);
                            jt.execute();
                            String result= jt.getResult();
                            // HttpEntity entity = response.getEntity();
                            // If the response does not enclose an entity, there is no need
                            Log.d("Afterrrr:", "partyyyy");

//                               Toast.makeText(getApplicationContext(), "Synced, submitted!",
                            //    Toast.LENGTH_SHORT).show();

                            //if (entity != null) {
                            //InputStream instream = entity.getContent();


                            //String result = getStringFromInputStream(instream);
                            //                  Log.i("Read from server", result);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }



                    i++;
                    // ArrayList<String> arrTblNames = new ArrayList<String>();
                    //SQLiteDatabase db =openOrCreateDatabase("surdb",SQLiteDatabase.CREATE_IF_NECESSARY,null);
                    //Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

                    //if (c.moveToFirst()) {
                    //  while ( !c.isAfterLast() ) {
                    //    arrTblNames.add( c.getString( c.getColumnIndex("name")) );
                    //  c.moveToNext();
                }


            }
            newDB.close();
            Toast.makeText(getApplicationContext(), "Synced to Server!",
                    Toast.LENGTH_SHORT).show();
            sla.close();

            return true;
        }

        return false;
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
    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }
}