package com.idpskuinfo.skuinfo.db;

import android.provider.BaseColumns;

public class DatabaseContract {
    public static String TABLE_NAME = "skuinfo";
    public static final class NoteColumns implements BaseColumns {
        public static String SKUID = "skuid";
        public static String DESCRIPTION = "description";
        public static String RETAIL_PRICE = "retailprice";
        public static String SKUTYPE = "skutype";
    }

    public static String TABLE_NAME_CURINFO = "currinfo";
    public static final class CurrColumns implements BaseColumns {
        public static String CURRID = "currid";
        public static String CURDES = "curdes";
        public static String CURRDATE = "currdate";
        public static String CUR_RET = "curret";
    }

    public static String TABLE_NAME_UPDATE = "updateinfo";
    public static final class UpdateColumns implements BaseColumns {
        public static String UPDATEID = "updateid";
        public static String UPDATEDATE = "updatedate";
        public static String UPDATETIME = "updatetime";
    }
}
