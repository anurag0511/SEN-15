package com.example.root.sample;


        import android.annotation.TargetApi;
        import android.app.Activity;
        import android.app.AlertDialog;
        import android.content.ContentValues;
        import android.content.Context;
        import android.media.Image;
        import android.net.ConnectivityManager;
        import android.net.NetworkInfo;
        import android.net.Uri;
        import android.os.Build;
        import android.os.Bundle;
        import android.content.Intent;
        import android.provider.MediaStore;
        import android.util.Log;

        import java.io.ByteArrayInputStream;
        import java.io.DataInputStream;
        import java.io.DataOutputStream;
        import java.io.File;
        import java.io.FileInputStream;
        import java.io.FileNotFoundException;
        import java.io.IOException;
        import java.net.HttpURLConnection;
        import java.net.MalformedURLException;
        import java.net.ProtocolException;
        import java.net.URL;
        import java.net.URLConnection;
        import java.io.InputStream;
        import java.io.BufferedReader;
        import java.io.InputStreamReader;
        import java.io.BufferedOutputStream;
        import javax.xml.parsers.DocumentBuilder;
        import javax.xml.parsers.DocumentBuilderFactory;

        import java.lang.Thread;

        import java.util.ArrayList;
        import java.util.List;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.ScrollView;
        import android.widget.LinearLayout;
        import android.widget.Button;
        import android.widget.LinearLayout.LayoutParams;
        import android.app.ProgressDialog;
        import android.os.Handler;
        import android.os.Message;

        import org.w3c.dom.Document;
        import org.w3c.dom.Element;
        import org.w3c.dom.Node;
        import org.w3c.dom.NodeList;
        import org.w3c.dom.NamedNodeMap;
        import android.database.sqlite.SQLiteDatabase;
        import android.database.sqlite.SQLiteOpenHelper;
        import android.database.Cursor;
        import android.widget.Toast;

public class RunForm extends Activity {
    /** Called when the activity is first created. */
    String tag = RunForm.class.getName();

    XmlGuiForm theForm;
    ProgressDialog progressDialog;
    Handler progressHandler;
    XmlGuiEditBox textview ;
    XmlGuiPickOne spinview;
    XmlGuiCheckbox check2;
    //XmlGuiCheckbox2 check;
    SQLiteDatabase db;
    XmlGuiFormField f1;
    SQLiteOpenHelper help;
    public String path ;

    String ngoid="";
    String volid="";
    public Uri pickedImage;
    private static int LOAD_IMAGE_RESULT = 1;
    public boolean image=false;
    String formNumber = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //textview = new XmlGuiEditBox(null,null,null);



        Intent startingIntent = getIntent();
        if(startingIntent == null) {
            Log.e(tag,"No Intent?  We're not supposed to be here...");
            finish();
            return;
        }

        //192.168.3.1
        formNumber = startingIntent.getStringExtra("ID");
        ngoid = startingIntent.getStringExtra("ngoID");
        volid = startingIntent.getStringExtra("volid");
        Log.i(tag,"Running Form [" + formNumber + "]");
        if (GetFormData(formNumber)) {
            DisplayForm();
        }
        else
        {
            Log.e(tag,"Couldn't parse the Form.");
            AlertDialog.Builder bd = new AlertDialog.Builder(this);
            AlertDialog ad = bd.create();
            ad.setTitle("Error");
            ad.setMessage("Could not parse the Form data");
            ad.show();

        }
    }
    public void onBackPressed() {
        // Write your code here

        // Switching to ListView screen
        Intent i = new Intent(getApplicationContext(), AndroidSQLite.class);
        i.putExtra("ngoid", ngoid);
        i.putExtra("volid", volid);
        startActivity(i);
         }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private boolean DisplayForm() {

        try {
            ScrollView sv = new ScrollView(this);

            final LinearLayout ll = new LinearLayout(this);
            sv.addView(ll);
            //  sv.isSmoothScrollingEnabled();
            ll.setOrientation(LinearLayout.VERTICAL);
            //   ll.setOrientation(SCROLLBAR_POSITION_RIGHT);
            // walk thru our form elements and dynamically create them, leveraging our mini library of tools.
            int i;
            for (i = 0; i < theForm.fields.size(); i++) {
                if (theForm.fields.elementAt(i).getType().equals("text")) {
                    theForm.fields.elementAt(i).obj = new XmlGuiEditBox(this, (theForm.fields.elementAt(i).isRequired() ? "*" : "") + theForm.fields.elementAt(i).getLabel(), "");
                    ll.addView((View) theForm.fields.elementAt(i).obj);
                }
                if (theForm.fields.elementAt(i).getType().equals("numeric")) {
                    theForm.fields.elementAt(i).obj = new XmlGuiEditBox(this, (theForm.fields.elementAt(i).isRequired() ? "*" : "") + theForm.fields.elementAt(i).getLabel(), "");
                    ((XmlGuiEditBox) theForm.fields.elementAt(i).obj).makeNumeric();
                    ll.addView((View) theForm.fields.elementAt(i).obj);
                }
                if (theForm.fields.elementAt(i).getType().equals("choice")) {
                    theForm.fields.elementAt(i).obj = new XmlGuiPickOne(this, (theForm.fields.elementAt(i).isRequired() ? "*" : "") + theForm.fields.elementAt(i).getLabel(), theForm.fields.elementAt(i).getOptions());
                    ll.addView((View) theForm.fields.elementAt(i).obj);
                }
                if (theForm.fields.elementAt(i).getType().equals("CheckBox")) {
                    theForm.fields.elementAt(i).obj = new XmlGuiCheckbox(this, (theForm.fields.elementAt(i).isRequired() ? "*" : "") + theForm.fields.elementAt(i).getLabel(), theForm.fields.elementAt(i).getOptions());
                    ll.addView((View) theForm.fields.elementAt(i).obj);
                }
                if (theForm.fields.elementAt(i).getType().equals("CheckBox2")) {
                    theForm.fields.elementAt(i).obj = new XmlGuiCheckbox_m(this, (theForm.fields.elementAt(i).isRequired() ? "*" : "") + theForm.fields.elementAt(i).getLabel(), theForm.fields.elementAt(i).getOptions());
                    ll.addView((View) theForm.fields.elementAt(i).obj);
                }
                if (theForm.fields.elementAt(i).getType().equals("image")){
                    image = true;
                    Button btn2 = new Button(this);
                    btn2.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT));
                    ll.addView(btn2);
                    btn2.setGravity(View.FOCUS_LEFT);
                    btn2.setText("ADD PHOTO");
                    btn2.setOnClickListener(new Button.OnClickListener() {
                        public void onClick(View v){
                            //Submission();
                            Intent cameraintent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            cameraintent.putExtra(MediaStore.EXTRA_OUTPUT,MediaStore.Images.Media.EXTERNAL_CONTENT_URI.getPath());
                            startActivityForResult(cameraintent,1);
                            //String[] Path = { MediaStore.Images.Media.DATA };


                            //Intent intent= new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            //fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);

                        }
                    });
                    final Button btn3 = new Button(this);
                    btn3.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    ll.addView(btn3);
                    btn3.setGravity(View.FOCUS_LEFT);
                    btn3.setText("Select PHOTO");
                    btn3.setOnClickListener(new Button.OnClickListener() {
                        public void onClick(View v) {
                            int j = 1;
                            Intent i = new Intent(
                                    Intent.ACTION_GET_CONTENT);
                            // android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            i.setType("image/*");
                            //i.setType("image/jpeg");
                            //i.setType("image/gif");
                            startActivityForResult(i, LOAD_IMAGE_RESULT);
                            //String x=getpathfromUri(pickedImage);
                            //Image_submit(x);
                   /* if(connectioncheck(v.getContext())==false){
                        System.out.println("meoww");
                    }
                    else{
                        sendimages();
                    }*/
                            // System.out.println(getpathfromUri(pickedImage));
                            //  String[] filePath = { MediaStore.Images.Media.DATA };

                            //pickedImage = i.getData();
                            //Log.v("++++++",pickedImage.toString());
                            //System.out.println("????????????" + pickedImage);
                            //path = getRealPathFromURI(pickedImage);


                            //  Cursor cursor = getContentResolver().query(i.getData(), filePath, null, null, null);
                            // cursor.moveToFirst();
                            //path = cursor.getString(cursor.getColumnIndex(filePath[0]));
                            // cursor.close();
                        }

                        //}
                    });
                }


            }

            setContentView(sv);
            final Button btn = new Button(this);
            btn.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            ll.addView(btn);
            btn.setGravity(View.FOCUS_LEFT);
            btn.setText("Submit");
            btn.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View v) {
                    Submission();

                    Toast.makeText(RunForm.this, "Submitted ! ", Toast.LENGTH_LONG).show();
                    if(connectioncheck(v.getContext())==false){
                        System.out.println("meoww");
                    }
                    else{
                        sendimages();
                    }



                    // check if this form is Valid
                    /*if (!CheckForm())
	        		{
	        			AlertDialog.Builder bd = new AlertDialog.Builder(ll.getContext());
	            		AlertDialog ad = bd.create();
	            		ad.setTitle("Error");
	            		ad.setMessage("Please enter all required (*) fields");
	            		ad.show();
	            		return;

	        		}*/
	        		/*if (theForm.getSubmitTo().equals("loopback")) {
	        			// just display the results to the screen
	        			String formResults = theForm.getFormattedResults();
	        			Log.i(tag,formResults);
	        			AlertDialog.Builder bd = new AlertDialog.Builder(ll.getContext());
	            		AlertDialog ad = bd.create();
	            		ad.setTitle("Results");
	            		ad.setMessage(formResults);
	            		ad.show();
	            		return;

	        		} else {
	        			if (!SubmitForm()) {
		        			AlertDialog.Builder bd = new AlertDialog.Builder(ll.getContext());
		            		AlertDialog ad = bd.create();
		            		ad.setTitle("Error");
		            		ad.setMessage("Error submitting form");
		            		ad.show();
		            		return;
	        			}
	        		}*/

                }
            });
            Button btn1 = new Button(this);
            btn.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            ll.addView(btn1);
            btn1.setGravity(View.FOCUS_LEFT);
            btn1.setText("NEW");
            btn1.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View v) {
                    // Clearform();
                    GetFormData(formNumber);

                    //textview.setValue(null);
                    DisplayForm();
                }
            });

            //getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
           /* Button btn2 = new Button(this);
            btn2.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT));
            ll.addView(btn2);
            btn2.setGravity(View.FOCUS_LEFT);
            btn2.setText("ADD PHOTO");
            btn2.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View v){
                    //Submission();
                    Intent cameraintent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    cameraintent.putExtra(MediaStore.EXTRA_OUTPUT,MediaStore.Images.Media.EXTERNAL_CONTENT_URI.getPath());
                    startActivityForResult(cameraintent,1);
                    //String[] Path = { MediaStore.Images.Media.DATA };


                    //Intent intent= new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    //fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);

                }
            });*/
            /*final Button btn3 = new Button(this);
            btn3.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            ll.addView(btn3);
            btn3.setGravity(View.FOCUS_LEFT);
            btn3.setText("Select PHOTO");
            btn3.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View v) {
                    int j = 1;
                    Intent i = new Intent(
                            Intent.ACTION_GET_CONTENT);
                    // android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    i.setType("image/*");
                    //i.setType("image/jpeg");
                    //i.setType("image/gif");
                    startActivityForResult(i, LOAD_IMAGE_RESULT);
                    //String x=getpathfromUri(pickedImage);
                    //Image_submit(x);
                   /* if(connectioncheck(v.getContext())==false){
                        System.out.println("meoww");
                    }
                    else{
                        sendimages();
                    }*/
                    // System.out.println(getpathfromUri(pickedImage));
                    //  String[] filePath = { MediaStore.Images.Media.DATA };

                    //pickedImage = i.getData();
                    //Log.v("++++++",pickedImage.toString());
                    //System.out.println("????????????" + pickedImage);
                   //path = getRealPathFromURI(pickedImage);


                    //  Cursor cursor = getContentResolver().query(i.getData(), filePath, null, null, null);
                    // cursor.moveToFirst();
                    //path = cursor.getString(cursor.getColumnIndex(filePath[0]));
                    // cursor.close();
   //             }

                //}
 //           });*/
///}
           /* Button btn4 = new Button(this);
            btn4.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            ll.addView(btn4);
            btn4.setGravity(View.FOCUS_LEFT);
            btn4.setText("Submit PHOTO");
            btn4.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View v) {
                    System.out.println(getpathfromUri(pickedImage));
                    btn3.setText(getpathfromUri(pickedImage));
                    if(connectioncheck(v.getContext())==false){
                        System.out.println("meoww");
                    }
                    else{
                        sendimages();
                    }
                }
            });*/
            //TextView txt = null;
            //txt.setText(path);
            //System.out.println(getpathfromUri(pickedImage));
            setTitle(theForm.getFormName());

            return true;

        } catch (Exception e) {
            Log.e(tag, "Error Displaying Form");
            return false;
        }
    }
     public boolean connectioncheck(Context context) {
         ConnectivityManager conMgr = (ConnectivityManager) context
                 .getSystemService(Context.CONNECTIVITY_SERVICE);
         NetworkInfo i = conMgr.getActiveNetworkInfo();
         if (i == null)
             return false;
         if (!i.isConnected())
             return false;
         if (!i.isAvailable())
             return false;
         return true;
     }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOAD_IMAGE_RESULT && resultCode == RESULT_OK && data != null) {
            pickedImage = data.getData();

        }
    }

    public String getpathfromUri(Uri contentUri){
        String[] filePath = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(pickedImage, filePath, null, null, null);
        cursor.moveToFirst();
        path = cursor.getString(cursor.getColumnIndex(filePath[0]));
        cursor.close();
        System.out.println(path);
        return path;
    }

   /* protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            pickedImage = data.getData();

        }
    }*/

    /*    private String getRealPathFromURI(Uri contentURI) {
            String result;
            System.out.println(contentURI + ">>>>");

            Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
            if (cursor == null) {
                result = contentURI.getPath().toString();
            } else {
                cursor.moveToFirst();
                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                result = cursor.getString(idx).toString();
                cursor.close();
            }
            return result;
        }
    */
    /*public String getpathfromUri(Uri contentUri){
        String[] filePath = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(pickedImage, filePath, null, null, null);
        cursor.moveToFirst();
        path = cursor.getString(cursor.getColumnIndex(filePath[0]));
        cursor.close();
        return path;
    }
    */
    private void Image_submit(String path) {
        final Uri fileUri;
        HttpURLConnection connect = null;
        //Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
        //String uploadUri = "";
        String twoHyphens="--";
        String lineEnd = "\r\n";
        String boundary = "*****";
        DataOutputStream out = null;
        DataInputStream inStream = null;
        int bytesRead,bytesAvailable,bufferSize;
        //String filepath = getpathfromUri(pickedImage);

        byte[] buffer;
        int maxBufferSize = 1*1024*1024;
        String urlString = "http://webscrapper.in/team14/upimage.php?formid="+formNumber+"&ngoid="+ngoid;
        try{


            FileInputStream fileinputStream = new FileInputStream(new File(path));
            URL url = new URL(urlString);
            connect = (HttpURLConnection) url.openConnection();
            connect.setDoInput(true);
            connect.setDoOutput(true);
            connect.setUseCaches(false);
            connect.setRequestMethod("POST");
            connect.setRequestProperty("Connection", "Keep-Alive");
            connect.setRequestProperty("Content-Type", "multipart/form-data;boundary="+boundary);
            out = new DataOutputStream( connect.getOutputStream() );
            out.writeBytes(twoHyphens + boundary + lineEnd);
            out.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\"" + path + "\"" + lineEnd);
            out.writeBytes(lineEnd);
            Log.e("MediaPlayer","Headers are written");
            bytesAvailable = fileinputStream.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];
            bytesRead = fileinputStream.read(buffer, 0, bufferSize);
            while (bytesRead > 0)
            {
                out.write(buffer, 0, bufferSize);
                bytesAvailable = fileinputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileinputStream.read(buffer, 0, bufferSize);
            }
            out.writeBytes(lineEnd);
            out.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
            BufferedReader in = new BufferedReader(new InputStreamReader(connect.getInputStream()));
            String inputLine;
            // Appendable tv;
            while ((inputLine = in.readLine()) != null)

                Log.e("MediaPlayer","File is written");
            fileinputStream.close();
            out.flush();
            out.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            inStream = new DataInputStream ( connect.getInputStream() );
            String str;
            while (( str = inStream.readLine()) != null)
            {
                Log.e("MediaPlayer","Server Response"+str);
            }
        /*while((str = inStream.readLine()) !=null ){

        }*/
            inStream.close();
        }
        catch (IOException ioex){
            Log.e("MediaPlayer", "error: " + ioex.getMessage(), ioex);
        }
    }

    // }

    private void Submission() {
        Log.d("XXX:","Entered Submission, let's see!");
        db = openOrCreateDatabase("MY_DATABASE", MODE_PRIVATE, null);
        String query = "CREATE TABLE IF NOT EXISTS " + ngoid +"_"+ formNumber + "(";
        // db.execSQL("create table if not exists surtable(fname VARCHAR,lname VARCHAR)");
        // getWritableDatabase.insert()
        String x1, x2, x3, x4, x5,x6, y, z;
        ContentValues values = new ContentValues(theForm.fields.size());
        String aa = ngoid +"_"+ formNumber;
        List<String> l = new ArrayList<String>();
        List<String> m = new ArrayList<String>();
        for (int i = 0; i < theForm.fields.size(); i++) {
            if (theForm.fields.elementAt(i).getType().equals("numeric")) {
                //x1 = textview.getValue();
                //     x1 = theForm.fields.elementAt(i).getData().toString();
                //Log.v("++++++", x1.toString());
                //z = theForm.fields.elementAt(i).getLabel().toString();
                z="que"+i;
                z= z.replaceAll("\\s+","");
                z=z.replaceAll("<","oa");
                z= z.replaceAll(">","ca");
                z=z.replaceAll("</","oba");
                z=z.replaceAll("/>","cba");
                z=z.replaceAll(":","");
                System.out.println(z+"this is the column ");
                query += z;
                query += " INT,";
                //       values.put(z,x1);
                //   l.add(x1);
                //  y = l.get(i);
                // db.insert(aa,null,values);

            }
            if (theForm.fields.elementAt(i).getType().equals("text")) {
                //x2 = textview.getValue();
                //x2 = theForm.fields.elementAt(i).getData().toString();
                //z = theForm.fields.elementAt(i).getLabel().toString();
                z="que"+i;
                z= z.replaceAll("\\s+","");
                z=z.replaceAll("<","oa");
                z= z.replaceAll(">","ca");
                z=z.replaceAll("</","oba");
                z=z.replaceAll("/>","cba");
                z=z.replaceAll(":","");
                //values.put(z,x2);
                query += z;
                query += " VARCHAR,";
                //l.add(x2);
                //y = l.get(i);
                //db.insert(aa,null,values);
            }
            if (theForm.fields.elementAt(i).getType().equals("choice")) {
                //x3 = spinview.getValue();
                //x3 = theForm.fields.elementAt(i).getData().toString();
                //z = theForm.fields.elementAt(i).getLabel().toString();
                z="que"+i;
                z= z.replaceAll("\\s+","");
                z=z.replaceAll("<","oa");
                z= z.replaceAll(">","ca");         
                z=z.replaceAll("</","oba");         
                z=z.replaceAll("/>","cba");         
                z=z.replaceAll(":","");
                //values.put(z,x3);
                query += z;
                query += " VARCHAR,";
                //l.add(x3);
                //y=l.get(i);
                //db.insert(aa,null,values);
            }
            if (theForm.fields.elementAt(i).getType().equals("CheckBox")) {
                //x4 = check.getValue();
                //   x4 = theForm.fields.elementAt(i).getData().toString();

                //z = theForm.fields.elementAt(i).getLabel().toString();
                z="que"+i;
                z= z.replaceAll("\\s+","");
                z= z.replaceAll("\\s+","");
                z=z.replaceAll("<","oa");
                z= z.replaceAll(">","ca");
                z=z.replaceAll("</","oba");
                z=z.replaceAll("/>","cba");
                z=z.replaceAll(":","");
                //  values.put(z,x4);
                //l.add(x4);
                query += z;
                query += " VARCHAR,";
                //y=l.get(i);
                //db.insert(aa,null,values);
            }
            if (theForm.fields.elementAt(i).getType().equals("CheckBox2")) {
                //m= check2.getValue();
                //m = (List<String>) theForm.fields.elementAt(i).getData();
                //String data = "";
                //for (int j = 0; j < m.size(); j++) {
                //   data+= m.get(j);
                //  data += ",";
                //}
                //data = data.substring(0, data.length()-1);
                //z = theForm.fields.elementAt(i).getLabel();
                z="que"+i;
                z= z.replaceAll("\\s+","");
                z= z.replaceAll("\\s+","");
                z=z.replaceAll("<","oa");
                z= z.replaceAll(">","ca");
                z=z.replaceAll("</","oba");
                z=z.replaceAll("/>","cba");
                z=z.replaceAll(":","");
                //values.put("z", data);
                query += z;
                query += " VARCHAR,";
                // db.insert(aa,null,values);
            }
            if (theForm.fields.elementAt(i).getType().equals("image")){
                z="image_path";
                query +=z;
                query +=" VARCHAR,";
            }


        }
        query += "synced INT DEFAULT 0)";
        //query = query.substring(0, query.length() - 1);

        db.execSQL(query);

        for (int i = 0; i < theForm.fields.size(); i++) {
            if (theForm.fields.elementAt(i).getType().equals("text")) {
                //x1 = textview.getValue();
                x1 = theForm.fields.elementAt(i).getData().toString();
                Log.v("++++++", x1.toString());
                //z = theForm.fields.elementAt(i).getLabel().toString();
                z="que"+i;
                z= z.replaceAll("\\s+","");
                z= z.replaceAll("\\s+","");
                z=z.replaceAll("<","oa");
                z= z.replaceAll(">","ca");
                z=z.replaceAll("</","oba");
                z=z.replaceAll("/>","cba");
                z=z.replaceAll(":","");

                //   query += z;
                //  query += " VARCHAR,";
                values.put(z, x1);
                l.add(x1);
                //y = l.get(i);
              // db.insert(aa, null, values);

            }
            if (theForm.fields.elementAt(i).getType().equals("numeric")) {
            //x2 = textview.getValue();
                x2 = theForm.fields.elementAt(i).getData().toString();
                z="que"+i;
               //z = theForm.fields.elementAt(i).getLabel().toString();
                z= z.replaceAll("\\s+","");
                z= z.replaceAll("\\s+","");
                z=z.replaceAll("<","oa");
                z= z.replaceAll(">","ca");
                z=z.replaceAll("</","oba");
                z=z.replaceAll("/>","cba");
                z=z.replaceAll(":","");

                values.put(z,x2);
            // query += z;
            // query += " VARCHAR,";
            // l.add(x2);
            //  y = l.get(i);
             //db.insert(aa,null,values);
            //  }
            if (theForm.fields.elementAt(i).getType().equals("choice")) {
                //x3 = spinview.getValue();
                x3 = theForm.fields.elementAt(i).getData().toString();
                z="que"+i;
                //z = theForm.fields.elementAt(i).getLabel().toString();
                z= z.replaceAll("\\s+","");
                z= z.replaceAll("\\s+","");
                z=z.replaceAll("<","oa");
                z= z.replaceAll(">","ca");
                z=z.replaceAll("</","oba");
                z=z.replaceAll("/>","cba");
                z=z.replaceAll(":","");

                values.put(z, x3);
                // query += z;
                // query += " VARCHAR,";
                l.add(x3);
                //y=l.get(i);
               // db.insert(aa, null, values);
            }
            if (theForm.fields.elementAt(i).getType().equals("CheckBox")) {
                //x4 = check.getValue();
                x4 = theForm.fields.elementAt(i).getData().toString();
                z="que"+i;
                //z = theForm.fields.elementAt(i).getLabel().toString();
               
                z= z.replaceAll("\\s+","");
                z=z.replaceAll("<","oa");
                z= z.replaceAll(">","ca");
                z=z.replaceAll("</","oba");
                z=z.replaceAll("/>","cba");
                z=z.replaceAll(":","");

                values.put(z, x4);
                l.add(x4);
                //query += z;
                //query += " VARCHAR,";
                //y=l.get(i);

            }

                if (theForm.fields.elementAt(i).getType().equals("CheckBox2")) {

                /*int j = 0;
                m = (List<String>) theForm.fields.elementAt(i).getData();
                System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
                System.out.println(m);
                //Log.i("fhf");
                //Log.v()
                //Log.v();
                String data = "";
                for (j = 0; j < m.size(); j++) {
                    data += m.get(j);
                    data += ",";
                }
                data = data.substring(0, data.length() - 1);
                z = theForm.fields.elementAt(i).getLabel();
                values.put(z, data);
                //query += z;
                //query += " VARCHAR,";
                // db.insert(aa,null,values);*/
                    x5 = theForm.fields.elementAt(i).getData().toString();
                    System.out.println(x5+"this is the multicheck");
                    //x5=check.getVal();
                    z="que"+i;
                    //z = theForm.fields.elementAt(i).getLabel().toString();
                    z= z.replaceAll("\\s+","");
                    z= z.replaceAll("\\s+","");
                    z= z.replaceAll("\\s+","");
                    z=z.replaceAll("<","oa");
                    z= z.replaceAll(">","ca");
                    z=z.replaceAll("</","oba");
                    z=z.replaceAll("/>","cba");
                    z=z.replaceAll(":","");

                    values.put(z, x5);
                    l.add(x5);
                    //db.insert(aa,null,values);
                }
                if (theForm.fields.elementAt(i).getType().equals("image")){
                    z="image_path";
                    x6=ngoid+"_"+formNumber+"_";
                    x6 +=  getpathfromUri(pickedImage);
                    values.put(z,x6);
                    l.add(x6);
                }

            }
         //   db.close();

        }

        values.put("synced",0);
        Log.d("INSERT:","Inserting into "+aa+"::" +values);
        db.insert(aa, null, values);
        db.close();


    }



    private boolean SubmitForm()
    {
        try {
            boolean ok = true;
            this.progressDialog = ProgressDialog.show(this, theForm.getFormName(), "Saving Form Data", true,false);
            this.progressHandler = new Handler() {

                @Override
                public void handleMessage(Message msg) {
                    // process incoming messages here
                    switch (msg.what) {
                        case 0:
                            // update progress bar
                            progressDialog.setMessage("" + (String) msg.obj);
                            break;
                        case 1:
                            progressDialog.cancel();
                            finish();
                            break;
                        case 2:
                            progressDialog.cancel();
                            break;
                    }
                    super.handleMessage(msg);
                }

            };

            Thread workthread = new Thread(new TransmitFormData(theForm));

            workthread.start();

            return ok;
        } catch (Exception e) {
            Log.e(tag,"Error in SubmitForm()::" + e.getMessage());
            e.printStackTrace();
            // tell user we failed....
            Message msg = new Message();
            msg.what = 1;
            this.progressHandler.sendMessage(msg);

            return false;
        }

    }
    private boolean CheckForm()
    {
        try {
            int i;
            boolean good = true;


            for (i=0;i<theForm.fields.size();i++) {
                String fieldValue = (String) theForm.fields.elementAt(i).getData();
                Log.i(tag,theForm.fields.elementAt(i).getLabel() + " is [" + fieldValue + "]");
                if (theForm.fields.elementAt(i).isRequired()) {
                    if (fieldValue == null) {
                        good = false;
                    } else {
                        if (fieldValue.trim().length() == 0) {
                            good = false;
                        }
                    }

                }
            }
            return good;
        } catch(Exception e) {
            Log.e(tag,"Error in CheckForm()::" + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }


    private String Clearform(){
        //    try{
        //      int i=0;
        //    for(i=0;i<theForm.fields.size();i++){
        //      String xyz=(String) theForm.fields.elementAt(i).getData();
        //    if(theForm.fields.elementAt(i).getType().equals("text")|| theForm.fields.elementAt(i).getType().equals("numeric")){
        textview.setValue(null);

        //  }
        //}
        return null;

        //} catch(Exception e){
        //  Log.e(tag,"Error in Clearing Data::"+ e.getMessage());
        // e.printStackTrace();
        //return null;
    }
    //}*/
    //public
    public void sendimages(){

        String name[] = new String[1000];
        String[] a = new String[0];
        int i=0;
        try
        {
            Cursor c = null;
            c = db.rawQuery("Select image_path FROM "+ngoid +"_"+ formNumber, null);
            c.moveToFirst();
            if (c.moveToFirst()) {
                do {

                    name[i] = c.getString(c.getColumnIndex("image_path"));
                    a= (name[i].split("_",3));
                    File file = new File(a[2]);
                    if(file.exists()) {
                        Image_submit(a[2]);
                        Uri x = Uri.parse(a[2]);
                        getContentResolver().delete(x, null, null);

                        i++;
                    }
                    else{
                        i++;
                    }
                } while (c.moveToNext());
            }

            c.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        //db.execSQL("Select image_path FROM "+ngoid +"_"+ formNumber);
    }
    private boolean GetFormData(String formNumber) {
        try {

            Log.d("FFFFFN:",formNumber);
            SQLiteAdapter msa;
            msa = new SQLiteAdapter(this);
            msa.openToRead();
            String jxml= msa.getFormCode(formNumber);
            msa.close();
            Log.d("CCCCCCD:",jxml);

                Log.i(tag,"ProcessForm");
                //URL url = new URL("http://servername/xmlgui"  + ".xml");
                //Log.i(tag,url.toString());
            /*String vxml="<?xml version='1.0' encoding='utf-8'?>\n" +
                    "<xmlgui>\n" +
                    "<form id='1' name='Robotics Club Registration' submitTo='http://servername/xmlgui1-post.php' ><field label='First Name' type='text' required='Y' options=''/><field label='Last Name' type='text' required='Y' options=''/>\n" +
                    "<field label='Gender' type='choice' required='Y' options='Male|Female|sdksfnekfn'/>\n" +
                    "<field label='Age on 15 Oct. 2010' type='numeric' required='N' options=''/>\n" +
                    "<field label='checkbox' type='CheckBox' required='Y' options='meoww|bowwwow|sfnjdnkjdbv'/>\n" +
                    "<field label='checkboxmeoww' type='CheckBox2' required='Y' options='meoww|bowwwow|sfnjdnkjdbv'/>\n" +
                    "</form>\n" +
                    "</xmlgui>\n" +
                    "\n";*/
                //Log.d("&&&&&",vxml);

                //    String jxml="<?xml version='1.0' encoding='utf-8'?><xmlgui><form id='1' name='Robotics Club Registration' submitTo='http://servername/xmlgui-post.php'><field label='First Name' type='text' required='Y' options=''/><field label='Last Name' type='text' required='Y' options=''/><field label='Gender' type='choice' required='Y' options='Male|Female|sdksfnekfn'/><field label='Age on 15 Oct. 2010' type='numeric' required='N' options=''/><field label='checkbox' type='CheckBox' required='Y' options='meoww|bowwwow|sfnjdnkjdbv'/><field label='checkboxmeoww' type='CheckBox2' required='Y' options='meoww|bowwwow|sfnjdnkjdbv'/></form></xmlgui>'<?xml version='1.0' encoding='utf-8'?><xmlgui><form id='1' name='Robotics Club Registration' submitTo='http://servername/xmlgui-post.php'><field label='First Name' type='text' required='Y' options=''/><field label='Last Name' type='text' required='Y' options=''/><field label='Gender' type='choice' required='Y' options='Male|Female|sdksfnekfn'/><field label='Age on 15 Oct. 2010' type='numeric' required='N' options=''/><field label='checkbox' type='CheckBox' required='Y' options='meoww|bowwwow|sfnjdnkjdbv'/><field label='checkboxmeoww' type='CheckBox2' required='Y' options='meoww|bowwwow|sfnjdnkjdbv'/></form></xmlgui>'{\"forms\":[{\"fid\":\"1\",\"title\":\"Agriculture Survey\",\"xml\":\"<?xml version='1.0' encoding='utf-8'?><xmlgui><form id='1' name='Robotics Club Registration' submitTo='http:\\/\\/servername\\/xmlgui-post.php'><field label='First Name' type='text' required='Y' options=''\\/><field label='Last Name' type='text' required='Y' options=''\\/><field label='Gender' type='choice' required='Y' options='Male|Female|sdksfnekfn'\\/><field label='Age on 15 Oct. 2010' type='numeric' required='N' options=''\\/><field label='checkbox' type='CheckBox' required='Y' options='meoww|bowwwow|sfnjdnkjdbv'\\/><field label='checkboxmeoww' type='CheckBox2' required='Y' options='meoww|bowwwow|sfnjdnkjdbv'\\/><\\/form><\\/xmlgui>'\",\"updated_at\":\"2015-03-31 17:15:06\"},{\"fid\":\"2\",\"title\":\"Women Health\",\"xml\":\"<?xml version='1.0' encoding='utf-8'?><xmlgui><form id='1' name='Robotics Club Registration' submitTo='http:\\/\\/servername\\/xmlgui-post.php'><field label='First Name' type='text' required='Y' options=''\\/><field label='Last Name' type='text' required='Y' options=''\\/><field label='Gender' type='choice' required='Y' options='Male|Female|sdksfnekfn'\\/><field label='Age on 15 Oct. 2010' type='numeric' required='N' options=''\\/><field label='checkbox' type='CheckBox' required='Y' options='meoww|bowwwow|sfnjdnkjdbv'\\/><field label='checkboxmeoww' type='CheckBox2' required='Y' options='meoww|bowwwow|sfnjdnkjdbv'\\/><\\/form><\\/xmlgui>'\",\"updated_at\":\"2015-03-31 17:15:15\"}],\"success\":1}";
                // String jxml="oa?xml version='1.0' encoding='utf-8'?caoaxmlguicaoaform id='1' name='Robotics Club Registration'caoafield label='First Name' type='text' required='Y' options='' cbaoafield label='Last Name' type='text' required='Y' options='' cbaoafield label='Gender' type='choice' required='Y' options='Male|Female|sdksfnekfn' cbaoafield label='Age on 15 Oct. 2010' type='numeric' required='N' options='' cbaoafield label='checkbox' type='CheckBox' required='Y' options='meoww|bowwwow|sfnjdnkjdbv' cbaoafield label='checkboxmeoww' type='CheckBox2' required='Y' options='meoww|bowwwow|sfnjdnkjdbv' cba obaformca obaxmlguica";
                //String jxml=msa.getFormCode(formNumber);
//String jxml= "<?xml version='1.0' encoding='utf-8'?><xmlgui><form id='1' name='Robotics Club Registration'><field label='First Name' type='text' required='Y' options='' /><field label='Last Name' type='text' required='Y' options='' /><field label='Gender' type='choice' required='Y' options='Male|Female|sdksfnekfn' /><field label='Age on 15 Oct. 2010' type='numeric' required='N' options='' /><field label='checkbox' type='CheckBox' required='Y' options='meoww|bowwwow|sfnjdnkjdbv' /><field label='checkboxmeoww' type='CheckBox2' required='Y' options='meoww|bowwwow|sfnjdnkjdbv' /> </form> </xmlgui>";
                jxml=jxml.replaceAll("oba","</");
                jxml=jxml.replaceAll("cba","/>");
                jxml=jxml.replaceAll("oa","<");
                jxml=jxml.replaceAll("ca",">");


                //jxml=jxml.replaceAll(" '","'");
                //jxml=jxml.replaceAll("' ","'");
                ;

                //  String xxml="<?xml version='1.0' encoding='utf-8'?><xmlgui><form id='1' name='Robotics Club Registration'<field label='First Name' type='text' required='Y' options=''><field label='Last Name' type='text' required='Y' options=''><field label='Gender' type='choice' required='Y' options='Male|Female|sdksfnekfn'><field label='Age on 15 Oct. 2010' type='numeric' required='N' options=''><field label='checkbox' type='CheckBox' required='Y' options='meoww|bowwwow|sfnjdnkjdbv'><field label='checkboxmeoww' type='CheckBox2' required='Y' options='meoww|bowwwow|sfnjdnkjdbv'><form><xmlgui>";
                //xxml=xxml.replaceAll(">",">\n");
                Log.d("*****",jxml);


                InputStream is= new ByteArrayInputStream(jxml.getBytes());

                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = factory.newDocumentBuilder();
            Document dom = db.parse(is);
            Element root = dom.getDocumentElement();
            NodeList forms = root.getElementsByTagName("form");
            if (forms.getLength() < 1) {
                // nothing here??
                Log.e(tag,"No form, let's bail");
                return false;
            }
            Node form = forms.item(0);
            theForm = new XmlGuiForm();

            // process form level
            NamedNodeMap map = form.getAttributes();
            theForm.setFormNumber(map.getNamedItem("id").getNodeValue());
            theForm.setFormName(map.getNamedItem("name").getNodeValue());
            if (map.getNamedItem("submitTo") != null)
                theForm.setSubmitTo(map.getNamedItem("submitTo").getNodeValue());
            else
                theForm.setSubmitTo("loopback");

            // now process the fields
            NodeList fields = root.getElementsByTagName("field");
            for (int i=0;i<fields.getLength();i++) {
                Node fieldNode = fields.item(i);
                NamedNodeMap attr = fieldNode.getAttributes();
                XmlGuiFormField tempField =  new XmlGuiFormField();
                //tempField.setName(attr.getNamedItem("name").getNodeValue());
                tempField.setLabel(attr.getNamedItem("label").getNodeValue());
                tempField.setType(attr.getNamedItem("type").getNodeValue());
                if (attr.getNamedItem("required").getNodeValue().equals("Y"))
                    tempField.setRequired(true);
                else
                    tempField.setRequired(false);
                tempField.setOptions(attr.getNamedItem("options").getNodeValue());
                theForm.getFields().add(tempField);
            }

            Log.i(tag,theForm.toString());
            return true;
        } catch (Exception e) {
            Log.e(tag,"Error occurred in ProcessForm:" + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }


    private class TransmitFormData implements Runnable
    {
        XmlGuiForm _form;
        Message msg;
        TransmitFormData(XmlGuiForm form) {
            this._form = form;
        }

        public void run() {

            try {
                msg = new Message();
                msg.what = 0;
                msg.obj = ("Connecting to Server");
                progressHandler.sendMessage(msg);

                URL url = new URL(_form.getSubmitTo());
                URLConnection conn = url.openConnection();
                conn.setDoOutput(true);
                BufferedOutputStream wr = new BufferedOutputStream(conn.getOutputStream());
                String data = _form.getFormEncodedData();
                wr.write(data.getBytes());
                wr.flush();
                wr.close();

                msg = new Message();
                msg.what = 0;
                msg.obj = ("Data Sent");
                progressHandler.sendMessage(msg);

                // Get the response
                BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line = "";
                Boolean bSuccess = false;
                while ((line = rd.readLine()) != null) {
                    if (line.indexOf("SUCCESS") != -1) {
                        bSuccess = true;
                    }
                    // Process line...
                    Log.v(tag, line);
                }
                wr.close();
                rd.close();

                if (bSuccess) {
                    msg = new Message();
                    msg.what = 0;
                    msg.obj = ("Form Submitted Successfully");
                    progressHandler.sendMessage(msg);

                    msg = new Message();
                    msg.what = 1;
                    progressHandler.sendMessage(msg);
                    return;

                }
            } catch (Exception e) {
                Log.d(tag, "Failed to send form data: " + e.getMessage());
                msg = new Message();
                msg.what = 0;
                msg.obj = ("Error Sending Form Data");
                progressHandler.sendMessage(msg);
            }
            msg = new Message();
            msg.what = 2;
            progressHandler.sendMessage(msg);
        }

    }
}