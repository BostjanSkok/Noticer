package com.empsem.bostjanskok.noticer;

import android.os.Handler;
import android.os.Bundle;
import android.os.ResultReceiver;

public class DbReceiver extends ResultReceiver {

    private Receiver mReceiver;

    public DbReceiver(Handler handler) {
        super(handler);
    }

    public void setReceiver(Receiver receiver) {
        mReceiver = receiver;
    }

    public interface Receiver {
        public void onReceiveResult(int resultCode, Bundle resultData);
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        if (mReceiver != null) {
            mReceiver.onReceiveResult(resultCode, resultData);
        }
    }
}