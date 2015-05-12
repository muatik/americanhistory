package com.muatik.americanhistory.DB;

import android.content.Context;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

/**
 * Created by muatik on 24.03.2015.
 */
public class DBHelper extends SQLiteAssetHelper {

    final static String DB_NAME = "ah.sql";
    final static int DB_VERSION = 1;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

}
