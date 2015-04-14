package com.muatik.americanhistory.Stories.DB;

import android.content.Context;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

/**
 * Created by muatik on 24.03.2015.
 */
public class DBHelper extends SQLiteAssetHelper {

    final static String DB_NAME = "ah.sql";
    final static int DB_VERSION = 1;
    final static String TABLE_STORIES= "stories";

    public static final String CID = "_id";
    public static final String CTITLE = "title";
    public static final String CDETAIL = "detail";
    public static final String CAUDIO_URL = "audioUrl";
    public static final String CAVAILABLE_OFFLINE = "availableOffline";
    public static final String CSCROLL_POSITION = "scrollPosition";


    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

}
