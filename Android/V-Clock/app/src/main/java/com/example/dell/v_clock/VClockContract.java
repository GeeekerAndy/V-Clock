package com.example.dell.v_clock;

import android.provider.BaseColumns;

/**
 * Created by andy on 7/6/17.
 */

public final class VClockContract {

    private VClockContract(){}

    public static class MessageInfo implements BaseColumns {
        public static final String TABLE_NAME = "message";
        public static final String COLUMN_NAME_GNAME = "gname";
        public static final String COLUMN_NAME_DATE = "date";
    }
}
