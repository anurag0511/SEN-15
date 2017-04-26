package com.example.root.sample;
//Author: Aditi Bhatnagar

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Toast;

public class MainActivity extends Activity {
String msg="++++++";
ContentResolver mResolver;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setting default screen to login.xml

        // Get the content resolver for your app
        mResolver = getContentResolver();
        // Turn on automatic syncing for the default account and authority
        mResolver.setMasterSyncAutomatically(true);

        setContentView(R.layout.activity_main);

        Button enter = (Button) findViewById(R.id.button);
        final EditText vol= (EditText) findViewById(R.id.editText);
        final EditText ngo= (EditText) findViewById(R.id.editText2);

        // Listening to register new account link
        enter.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // Switching to ListView screen

                Log.d("org id:",ngo.getText().toString());
                Log.d("vol id:",vol.getText().toString());

                if(ngo.getText().toString().equals("")  || vol.getText().toString().equals(""))
                {
                    Toast.makeText(getApplicationContext(), "Oops, enter the repective ids pls!",
                            Toast.LENGTH_SHORT).show();

                }
                else {
                    Log.d("oooooo:",ngo.getText().toString());
                    Log.d("vvvvvv",vol.getText().toString());

                    Intent i = new Intent(getApplicationContext(), AndroidSQLite.class);
                    i.putExtra("ngoid", ngo.getText().toString());
                    i.putExtra("volid", vol.getText().toString());
                    startActivity(i);
                }
            }
        });
        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            finish();
            return;
        }







    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
// Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
// Handle action bar item clicks here. The action bar will
// automatically handle clicks on the Home/Up button, so long
// as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
//noinspection SimplifiableIfStatement
        if (id == R.id.help) {

            setContentView(R.layout.help);
            Button btnDismiss = (Button)findViewById(R.id.button2);
            btnDismiss.setOnClickListener(new Button.OnClickListener(){

                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(i);

                }});

          //  popupWindow.showAsDropDown(btnOpenPopup, 50, -30);


}

        return true;

    }









    public void onBackPressed() {
        // Write your code here
        moveTaskToBack(true);
         MainActivity.this.finish();
        // Switching to ListView screen
       // Intent i = new Intent(getApplicationContext(), MainActivity.class);
        //startActivity(i);     //either save instance checkout how
    }
    @Override
    protected void onStart() {
        super.onStart();
        Log.d(msg, "The onStart() event");
    }

    /** Called when the activity has become visible. */
    @Override
    protected void onResume() {
        super.onResume();

        Log.d(msg, "The onResume() event");
    }

    /** Called when another activity is taking focus. */
    @Override
    protected void onPause() {
        super.onPause();
        Log.d(msg, "The onPause() event");
    }

    /** Called when the activity is no longer visible. */
    @Override
    protected void onStop() {
        super.onStop();
        Log.d(msg, "The onStop() event");
    }

    /** Called just before the activity is destroyed. */
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(msg, "The onDestroy() event");
    }

}
