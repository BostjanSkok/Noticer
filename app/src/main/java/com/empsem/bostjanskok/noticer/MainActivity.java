package com.empsem.bostjanskok.noticer;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;


public class MainActivity extends ActionBarActivity {
    private String TAG = this.getClass().getSimpleName();

    private String[] mainMenuMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SetupMainMenuList();
        ContentResolver contentResolver = this.getContentResolver();
        String enabledNotificationListeners = Settings.Secure.getString(contentResolver, "enabled_notification_listeners");
        String packageName = this.getPackageName();

// check to see if the enabledNotificationListeners String contains our package name
        if (enabledNotificationListeners == null || !enabledNotificationListeners.contains(packageName)) {
            Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
            MainActivity.this.startActivity(intent);
        }
        startService();
        //  if(! isListenerRunning(NoticerListenerService.class))
        // startService(new Intent(this,NoticerListenerService.class));
    }


    private boolean isListenerRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    // Method to start the service
    public void startService() {
        startService(new Intent(getBaseContext(), NotificationMonitor.class));
    }

    // Method to stop the service
    public void stopService() {
        stopService(new Intent(getBaseContext(), NotificationMonitor.class));
    }

    private void SetupMainMenuList() {
        mainMenuMap = new String[]{
                getString(R.string.MainMenuListEventsToNotify),
                getString(R.string.MainMenuListLinkedDevices),
                getString(R.string.MainMenuListNotificationHistory),
                getString(R.string.MainMenuListSendTest),
        };
        final ListView listview = (ListView) findViewById(R.id.MainMenuList);
        ArrayList<String> mainMenuL = new ArrayList<>(Arrays.asList(mainMenuMap));
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mainMenuL);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                navigateToActivity(position);
            }
        });
    }

    private void navigateToActivity(int position) {
        Class nextActivity = null;
        switch (position) {
            case 0:
                nextActivity = EventsToNotifyActivity.class;
                break;
            case 1:
                nextActivity = LinkedDevicesActivity.class;
                break;
            case 2:
                nextActivity = NotificationHistoryActivity.class;
                break;
            case 3:
                nextActivity = null;
                SendTestNotification();
                break;
        }
        if (nextActivity != null) {
            Intent myIntent = new Intent(MainActivity.this, nextActivity);
            MainActivity.this.startActivity(myIntent);
        }
    }

    private void SendTestNotification() {
        sendBroadcast("\"NewDevice\":{\"DeviceName\":\"TestDevice\",\"ID\"=\"122\"}");
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void sendBroadcast(String messageStr) {
        // Hack Prevent crash (sending should be done using an async task)
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {
            //Open a random port to send the package
            DatagramSocket socket = new DatagramSocket();
            socket.setBroadcast(true);
            byte[] sendData = messageStr.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, getBroadcastAddress(), 55555);
            socket.send(sendPacket);
            System.out.println(getClass().getName() + "Broadcast packet sent to: " + getBroadcastAddress().getHostAddress());
        } catch (IOException e) {
            Log.e(TAG, "IOException: " + e.getMessage());
        }
    }

    InetAddress getBroadcastAddress() throws IOException {
        WifiManager wifi = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        DhcpInfo dhcp = wifi.getDhcpInfo();
        // handle null somehow

        int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
        byte[] quads = new byte[4];
        for (int k = 0; k < 4; k++)
            quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
        return InetAddress.getByAddress(quads);
    }

}
