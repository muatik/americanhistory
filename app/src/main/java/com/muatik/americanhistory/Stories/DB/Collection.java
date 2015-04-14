package com.muatik.americanhistory.Stories.DB;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.muatik.americanhistory.Stories.Builder;
import com.muatik.americanhistory.Stories.Story;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by muatik on 24.03.2015.
 */
public class Collection {

    private SQLiteDatabase db;
    private static final String TAG = "AH";

    public Collection(Context context) {
        db =  new DBHelper(context).getWritableDatabase();
    }

    public List<Story> getList() {
        Cursor c = db.rawQuery("select * from " + DBHelper.TABLE_STORIES, null);
        List<Story> stories = new ArrayList<Story>();

        while(c.moveToNext())
            stories.add(Builder.build(c));

        return stories;
    }

    public Story get(Long id) {
        Cursor c = db.rawQuery(
                "select * from " + DBHelper.TABLE_STORIES + " where "
                        + DBHelper.CID + " = " + String.valueOf(id) + " limit 1", null);
        c.moveToNext();
        return Builder.build(c);
    }

}
