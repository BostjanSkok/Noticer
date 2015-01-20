package com.empsem.bostjanskok.noticer;

import android.provider.BaseColumns;

public final class NotificationHistorContract {
    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public NotificationHistorContract() {}


    /* Inner class that defines the table contents */
    public static abstract class NotificationHistoryEntry implements BaseColumns {
        public static final String TABLE_NAME = "ReceivedNotice";
        public static final String COLUMN_NAME_NOTIFI_ID = "notificationId";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_TEXT = "text";
        public static final String COLUMN_NAME_POSTED = "PostedOn";
        public static final String COLUMN_NAME_REMOVED = "RemovedOn";

    }
}