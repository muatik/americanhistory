package com.muatik.americanhistory.Stories;

import android.database.Cursor;

import com.muatik.americanhistory.Stories.DB.DBHelper;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by muatik on 24.03.2015.
 */
public class Builder {
    public static Story build(Cursor c) {
        Story i = new Story();
        i.id = c.getLong(c.getColumnIndex(DBHelper.CID));
        i.title = c.getString(c.getColumnIndex(DBHelper.CTITLE));
        i.detail = c.getString(c.getColumnIndex(DBHelper.CDETAIL));
        try {
            i.audioUrl = new URL(c.getString(c.getColumnIndex(DBHelper.CAUDIO_URL)));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        i.availableOffline = c.getInt(c.getColumnIndex(DBHelper.CAVAILABLE_OFFLINE)) != 0;
        i.scrollPosition = c.getInt(c.getColumnIndex(DBHelper.CSCROLL_POSITION));
        return i;
    }
}
