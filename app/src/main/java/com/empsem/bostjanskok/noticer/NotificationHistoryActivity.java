package com.empsem.bostjanskok.noticer;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.sql.Date;
import java.text.DateFormat;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.HashMap;

import static com.empsem.bostjanskok.noticer.NotificationHistorContract.NotificationHistoryEntry;


public class NotificationHistoryActivity extends ActionBarActivity {
    private DbReceiver mReceiver;
    private SimpleAdapter sa;
    private ListView toUpdate;
    private NoticerDbHelper noticerdbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_history);
        noticerdbHelper = new NoticerDbHelper(this);
        LoadHistory();
    }

    private void LoadHistory() {
        ArrayList<HashMap<String, String>> res = ReturnHistorFromDb();
        toUpdate = (ListView) findViewById(R.id.historylistView);


        sa = new SimpleAdapter(this, res,
                android.R.layout.two_line_list_item,
                new String[]{"line1", "line2"},
                new int[]{android.R.id.text1, android.R.id.text2});
        toUpdate.setAdapter(sa);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_notification_history, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private ArrayList<HashMap<String, String>> ReturnHistorFromDb() {
        ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();

        SQLiteDatabase newDB = noticerdbHelper.getReadableDatabase();

        try {

            Cursor c = newDB.rawQuery("SELECT " + NotificationHistoryEntry.COLUMN_NAME_POSTED +
                    "," + NotificationHistoryEntry.COLUMN_NAME_TITLE + ","
                    + NotificationHistoryEntry.COLUMN_NAME_TEXT + " FROM "
                    + NotificationHistoryEntry.TABLE_NAME, null);

            if (c != null) {
                if (c.moveToFirst()) {
                    do {
                        String title = c.getString(c.getColumnIndex(NotificationHistoryEntry.COLUMN_NAME_TITLE));
                        String posted = c.getString(c.getColumnIndex(NotificationHistoryEntry.COLUMN_NAME_POSTED));
                        String Text = c.getString(c.getColumnIndex(NotificationHistoryEntry.COLUMN_NAME_TEXT));
                        Date date = new Date(Long.parseLong(posted));
                        DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getApplicationContext());

                        HashMap<String, String> item = new HashMap<String, String>();
                        item.put("line1", dateFormat.format(date) + " : " + title);
                        item.put("line2", Text);
                        list.add(item);
                    } while (c.moveToNext());
                }
            }
        } catch (SQLiteException se) {
            Log.e(getClass().getSimpleName(), "Could not create or Open the database");
        } finally {
            newDB.close();
        }
        return list;
    }
}
