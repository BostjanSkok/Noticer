package com.empsem.bostjanskok.noticer;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;

import static com.empsem.bostjanskok.noticer.NotificationHistorContract.NotificationHistoryEntry;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class SaveNotification extends IntentService {
    private String TAG = this.getClass().getSimpleName();

    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_FOO = "com.empsem.bostjanskok.noticer.action.FOO";
    private static final String ACTION_BAZ = "com.empsem.bostjanskok.noticer.action.BAZ";

    // TODO: Rename parameters
    private static final String EXTRA_PARAM1 = "com.empsem.bostjanskok.noticer.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "com.empsem.bostjanskok.noticer.extra.PARAM2";

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionFoo(Context context, String param1, String param2) {
        Intent intent = new Intent(context, SaveNotification.class);
        intent.setAction(ACTION_FOO);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionBaz(Context context, String param1, String param2) {
        Intent intent = new Intent(context, SaveNotification.class);
        intent.setAction(ACTION_BAZ);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    private NoticerDbHelper noticerdbHelper;

    public SaveNotification() {

        super("SaveNotification");
        noticerdbHelper = new NoticerDbHelper(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            SQLiteDatabase newDB = noticerdbHelper.getWritableDatabase();
            int nId = intent.getIntExtra("ID", -1);
            if (nId == -1) {
                Log.i(TAG, "Notification id empty");
                return;
            }

            SaveToDb(intent, nId, newDB);

        }
    }



    private void SaveToDb(Intent intent, int nId, SQLiteDatabase newDB) {
        String title = intent.getStringExtra("EXTRA_TITLE");
        if (title == null) {
            Log.i(TAG, "Notification Title empty");
            return;

        }
        String text = intent.getStringExtra("EXTRA_TEXT");
        if (text == null) {
            Log.i(TAG, "Notification Text empty");
            return;

        }

        Cursor c = newDB.rawQuery("SELECT * FROM  "
                + NotificationHistoryEntry.TABLE_NAME + " WHERE "+NotificationHistoryEntry.COLUMN_NAME_NOTIFI_ID+" = " + nId, null);
        if (c != null) {
            if(c.getCount()==0)
                return;
        }

        SQLiteDatabase db = noticerdbHelper.getWritableDatabase();
        try {

            ContentValues values = new ContentValues();
            values.put(NotificationHistoryEntry.COLUMN_NAME_NOTIFI_ID, nId);
            values.put(NotificationHistoryEntry.COLUMN_NAME_TITLE, title);
            values.put(NotificationHistoryEntry.COLUMN_NAME_TEXT, text);
            values.put(NotificationHistoryEntry.COLUMN_NAME_POSTED, new Date().getTime());
// Insert the new row, returning the primary key value of the new row
            long newRowId;
            newRowId = db.insert(
                    NotificationHistoryEntry.TABLE_NAME,
                    null,
                    values);
        } finally {
            db.close();

        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionFoo(String param1, String param2) {
        // TODO: Handle action Foo
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionBaz(String param1, String param2) {
        // TODO: Handle action Baz
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
