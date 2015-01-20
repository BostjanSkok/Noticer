package com.empsem.bostjanskok.noticer;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.empsem.bostjanskok.noticer.LinkedDevicesContract.LinkedDeviceEntry;
import static com.empsem.bostjanskok.noticer.NotificationHistorContract.NotificationHistoryEntry;

public class NoticerDbHelper extends SQLiteOpenHelper {
    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_HISTORY =
            "CREATE TABLE " + NotificationHistoryEntry.TABLE_NAME + " (" +
                    NotificationHistoryEntry._ID + " INTEGER PRIMARY KEY," +
                    NotificationHistoryEntry.COLUMN_NAME_NOTIFI_ID + " INTEGER" + COMMA_SEP +
                    NotificationHistoryEntry.COLUMN_NAME_TITLE + TEXT_TYPE + COMMA_SEP +
                    NotificationHistoryEntry.COLUMN_NAME_TEXT + TEXT_TYPE + COMMA_SEP +
                    NotificationHistoryEntry.COLUMN_NAME_POSTED + TEXT_TYPE + COMMA_SEP +
                    NotificationHistoryEntry.COLUMN_NAME_REMOVED + TEXT_TYPE + " )";
    private static final String SQL_DELETE_HISTORY =
            "DROP TABLE IF EXISTS " + NotificationHistoryEntry.TABLE_NAME;
    private static final String SQL_CREATE_LINKED_DEVICES =
            "CREATE TABLE " + LinkedDeviceEntry.TABLE_NAME + " (" +
                    LinkedDeviceEntry._ID + " INTEGER PRIMARY KEY," +
                    LinkedDeviceEntry.COLUMN_NAME_LINK_ID + " INTEGER" + COMMA_SEP +
                    LinkedDeviceEntry.COLUMN_NAME_NAME + TEXT_TYPE + COMMA_SEP +
                    LinkedDeviceEntry.COLUMN_NAME_SSID + TEXT_TYPE + COMMA_SEP +
                    LinkedDeviceEntry.COLUMN_NAME_KEY + TEXT_TYPE + COMMA_SEP +
                    LinkedDeviceEntry.COLUMN_NAME_CASTIP + TEXT_TYPE + COMMA_SEP +
                    LinkedDeviceEntry.COLUMN_NAME_VECTOR + TEXT_TYPE + COMMA_SEP +
                    LinkedDeviceEntry.COLUMN_NAME_ADDED + TEXT_TYPE + " )";
    private static final String SQL_DELETE_LINKED_DEVICES =
            "DROP TABLE IF EXISTS " + LinkedDeviceEntry.TABLE_NAME;


    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "FeedReader.db";

    public NoticerDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_HISTORY);
        db.execSQL(SQL_CREATE_LINKED_DEVICES);

    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_HISTORY);
        db.execSQL(SQL_DELETE_LINKED_DEVICES);

        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
