package com.empsem.bostjanskok.noticer;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.net.DhcpInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.os.StrictMode;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class NotificationMonitor  extends NotificationListenerService {
    private String TAG = this.getClass().getSimpleName();

    public NotificationMonitor () {
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        Log.i(TAG, "**********  onNotificationPosted");
        CallSave(sbn);


    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {

        Log.i(TAG, "********** onNOtificationRemoved");
        CallSave(sbn);
    }

    private void CallSave(StatusBarNotification sbn) {
        Notification notification = sbn.getNotification();
        Log.i(TAG, "" + sbn.getId());
        String title = notification.extras.getCharSequence(Notification.EXTRA_TITLE).toString();
        Log.i(TAG, title);
        String text = notification.extras.getCharSequence(Notification.EXTRA_TEXT).toString();
        Log.i(TAG, text);

        Intent mServiceIntent = new Intent(NotificationMonitor.this, SaveNotification.class);
        mServiceIntent.putExtra("ID", sbn.getId());
        mServiceIntent.putExtra("EXTRA_TITLE", title);
        mServiceIntent.putExtra("EXTRA_TEXT", text);
        startService(mServiceIntent);
        sendBroadcast("<ID>" + sbn.getId() + "</ID><TITLE>" + title + "</TITLE><TEXT>" + text + "</TEXT>");
    }


    /**
     * indicates how to behave if the service is killed
     */
    int mStartMode;

    /**
     * Called when the service is being created.
     */
    @Override
    public void onCreate() {
        Log.i(TAG, "Notification listener started");
        sendBroadcast("Notification listener started");
    }

    /**
     * The service is starting, due to a call to startService()
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return mStartMode;
    }


    /**
     * Called when a client is binding to the service with bindService()
     */
    @Override
    public void onRebind(Intent intent) {
        Log.i(TAG, "Notification listener rebind");
    }

    /**
     * Called when The service is no longer used and is being destroyed
     */
    @Override
    public void onDestroy() {
        Log.i(TAG, "Notification listener Destroyed");
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
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length,getBroadcastAddress(), 55555);
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