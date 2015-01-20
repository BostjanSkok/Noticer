package com.empsem.bostjanskok.noticer;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.empsem.bostjanskok.noticer.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;


public class LinkedDevicesActivity extends ActionBarActivity {
    private NoticerDbHelper noticerdbHelper;
    private SimpleAdapter sa;
    private ListView toUpdate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_linked_devices);
        noticerdbHelper = new NoticerDbHelper(this);
        LoadDevice();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_linked_devices, menu);
        return true;
    }

    private void LoadDevice() {
        ArrayList<HashMap<String, String>> res = ReturnDevicesFromDb();
        toUpdate = (ListView) findViewById(R.id.listLinkedDevices);


        sa = new SimpleAdapter(this, res,
                android.R.layout.two_line_list_item,
                new String[]{"line1", "line2"},
                new int[]{android.R.id.text1, android.R.id.text2});
        toUpdate.setAdapter(sa);
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
        if(id== R.id.action_add){
            // 1. Instantiate an AlertDialog.Builder with its constructor
            AlertDialog.Builder builder = new AlertDialog.Builder(LinkedDevicesActivity.this);

// 2. Chain together various setter methods to set the dialog characteristics
            builder.setTitle("Select method of input");
            builder.setPositiveButton("QR code", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    try {
                        Intent intent = new Intent("com.google.zxing.client.android.SCAN");
                        intent.putExtra("SCAN_MODE", "QR_CODE_MODE"); // "PRODUCT_MODE for bar codes

                        startActivityForResult(intent, 0);
                    } catch (Exception e) {
                        try{Uri marketUri = Uri.parse("market://details?id=com.google.zxing.client.android");
                        Intent marketIntent = new Intent(Intent.ACTION_VIEW,marketUri);
                        startActivity(marketIntent);
                        }catch (Exception er)
                        {

                        }
                    }
                }
            });
            builder.setNegativeButton("Manual", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                }
            });

// 3. Get the AlertDialog from create()
            AlertDialog dialog = builder.create();
            dialog.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {

            if (resultCode == RESULT_OK) {
                String contents = data.getStringExtra("SCAN_RESULT");
                JSONObject mainObject = null;
                try {
                    mainObject = new JSONObject(contents);
                    JSONObject newDeviceObject = mainObject.optJSONObject("NewDevice");
                    String  name = newDeviceObject.getString("name");
                    int id = newDeviceObject.getInt("ID");
                    SaveToDb(id,name);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
            if(resultCode == RESULT_CANCELED){
                //handle cancel
            }
        }
    }

    private void SaveToDb(int Id, String name ) {


        SQLiteDatabase db = noticerdbHelper.getWritableDatabase();
        try {

            ContentValues values = new ContentValues();
            values.put(LinkedDevicesContract.LinkedDeviceEntry.COLUMN_NAME_LINK_ID, Id);
            values.put(LinkedDevicesContract.LinkedDeviceEntry.COLUMN_NAME_NAME, name);
            values.put(LinkedDevicesContract.LinkedDeviceEntry.COLUMN_NAME_ADDED, new Date().getTime());
// Insert the new row, returning the primary key value of the new row
            long newRowId;
            newRowId = db.insert(
                    LinkedDevicesContract.LinkedDeviceEntry.TABLE_NAME,
                    null,
                    values);
        } finally {
            db.close();

        }
    }
    private ArrayList<HashMap<String, String>> ReturnDevicesFromDb() {
        ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();

        SQLiteDatabase newDB = noticerdbHelper.getReadableDatabase();

        try {

            Cursor c = newDB.rawQuery("SELECT " + LinkedDevicesContract.LinkedDeviceEntry.COLUMN_NAME_ADDED +
                    "," + LinkedDevicesContract.LinkedDeviceEntry.COLUMN_NAME_NAME + ","
                    + LinkedDevicesContract.LinkedDeviceEntry.COLUMN_NAME_LINK_ID + " FROM "
                    + LinkedDevicesContract.LinkedDeviceEntry.TABLE_NAME, null);

            if (c != null) {
                if (c.moveToFirst()) {
                    do {
                        String name = c.getString(c.getColumnIndex(LinkedDevicesContract.LinkedDeviceEntry.COLUMN_NAME_NAME));
                        String posted = c.getString(c.getColumnIndex(LinkedDevicesContract.LinkedDeviceEntry.COLUMN_NAME_ADDED));

                        java.sql.Date date = new java.sql.Date(Long.parseLong(posted));
                        DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getApplicationContext());

                        HashMap<String, String> item = new HashMap<String, String>();
                        item.put("line1", dateFormat.format(date));
                        item.put("line2", name);
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
