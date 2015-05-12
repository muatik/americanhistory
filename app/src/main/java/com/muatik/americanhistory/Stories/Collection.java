package com.muatik.americanhistory.Stories;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


import com.muatik.americanhistory.DB.DBHelper;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by muatik on 24.03.2015.
 */
public class Collection {

    public final static String TABLE_STORIES= "stories";

    public static final String CID = "_id";
    public static final String CTITLE = "title";
    public static final String CDETAIL = "detail";
    public static final String CAUDIO_URL = "audioUrl";
    public static final String CAVAILABLE_OFFLINE = "availableOffline";
    public static final String CSCROLL_POSITION = "scrollPosition";;

    private SQLiteDatabase db;
    private static final String TAG = "AH";

    public Collection(Context context) {
        db =  new DBHelper(context).getWritableDatabase();
    }

    public static Story BuildStory(Cursor c) {
        Story i = new Story();
        i.id = c.getLong(c.getColumnIndex(Collection.CID));
        i.title = c.getString(c.getColumnIndex(Collection.CTITLE));
        i.detail = c.getString(c.getColumnIndex(Collection.CDETAIL));
        try {
            i.audioUrl = new URL(c.getString(c.getColumnIndex(Collection.CAUDIO_URL)));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        i.availableOffline = c.getInt(c.getColumnIndex(Collection.CAVAILABLE_OFFLINE)) != 0;
        i.scrollPosition = c.getInt(c.getColumnIndex(Collection.CSCROLL_POSITION));
        return i;
    }

    public List<Story> getList() {
        Cursor c = db.rawQuery("select * from " + TABLE_STORIES, null);
        List<Story> stories = new ArrayList<Story>();

        while(c.moveToNext())
            stories.add(BuildStory(c));

        return stories;
    }

    public Story get(Long id) {
        Cursor c = db.rawQuery(
                "select * from " + TABLE_STORIES + " where "
                        + CID + " = " + String.valueOf(id) + " limit 1", null);
        c.moveToNext();
        return BuildStory(c);
    }

}
