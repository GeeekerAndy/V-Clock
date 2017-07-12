package com.example.dell.v_clock;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by andy on 7/6/17.
 */

public class MessageDBHelper extends SQLiteOpenHelper{

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "VClock.db";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + VClockContract.MessageInfo.TABLE_NAME + " (" +
                    VClockContract.MessageInfo._ID + "," +
                    VClockContract.MessageInfo.COLUMN_NAME_GNAME + "," +
                    VClockContract.MessageInfo.COLUMN_NAME_DATE + "," +
                    "PRIMARY KEY (" + VClockContract.MessageInfo._ID + "," +
                    VClockContract.MessageInfo.COLUMN_NAME_GNAME + "," +
                    VClockContract.MessageInfo.COLUMN_NAME_DATE + ")" +
                    ");";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + VClockContract.MessageInfo.TABLE_NAME;

    public MessageDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

}
