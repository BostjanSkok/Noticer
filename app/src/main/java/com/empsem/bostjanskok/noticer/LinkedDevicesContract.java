package com.empsem.bostjanskok.noticer;

import android.provider.BaseColumns;

public final class LinkedDevicesContract {

    public LinkedDevicesContract() {}


    public static abstract class LinkedDeviceEntry implements BaseColumns {
        public static final String TABLE_NAME = "LinkedDevice";
        public static final String COLUMN_NAME_LINK_ID = "LinkedDeviceId";
        public static final String COLUMN_NAME_NAME = "Name";
        public static final String COLUMN_NAME_SSID = "SSID";
        public static final String COLUMN_NAME_KEY = "Key";
        public static final String COLUMN_NAME_CASTIP = "Ip";
        public static final String COLUMN_NAME_VECTOR = "Vector";
        public static final String COLUMN_NAME_ADDED = "AddedOn";

    }
}
