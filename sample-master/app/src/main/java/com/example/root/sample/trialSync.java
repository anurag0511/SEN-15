package com.example.root.sample;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by root on 9/4/15.
 */
public class trialSync extends SQLiteOpenHelper {


    public trialSync(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public void createDatabases()
    {



    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE table1 ( ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "Name" + " TEXT NOT NULL, " +
                        "Surname" + " TEXT NOT NULL);"
        );

        db.execSQL("CREATE TABLE table2 ( FID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "Question" + " TEXT NOT NULL, " +
                        "Answer" + " TEXT NOT NULL);"
        );
        db.execSQL("CREATE TABLE table3 ( ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "Day" + " TEXT NOT NULL, " +
                        "Time" + " TEXT NOT NULL);"
        );

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void showTables(SQLiteDatabase db)
    {

       String q= "SELECT * FROM dbname.sqlite_master WHERE type='table'";

        ArrayList<String> arrTblNames = new ArrayList<String>();
        Cursor c = db.rawQuery(q, null);

        if (c.moveToFirst()) {
            while ( !c.isAfterLast() ) {
                arrTblNames.add( c.getString( c.getColumnIndex("name")) );
                c.moveToNext();
            }
        }

        int s=arrTblNames.size();
        for(int i=0; i<s; i++)
        {
            Log.d("TTTTTTT:",arrTblNames.get(i));
        }
    }
}
