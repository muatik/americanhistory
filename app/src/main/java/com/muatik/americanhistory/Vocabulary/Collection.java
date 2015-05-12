package com.muatik.americanhistory.Vocabulary;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.muatik.americanhistory.DB.DBHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by muatik on 24.03.2015.
 */
public class Collection {

    public final static String TABLE_VOCABULARY= "vocabulary";

    public static final String CID = "_id";
    public static final String CWORD = "word";
    public static final String CTRANSLATION = "translation";
    public static final String CDETAIL = "detail";

    private SQLiteDatabase db;
    public Collection(Context context) {
        db =  new DBHelper(context).getWritableDatabase();
    }

    private static Word WordBuilder(Cursor c) {
        Word word = new Word();
        word.id = c.getLong(c.getColumnIndex(CID));
        word.word = c.getString(c.getColumnIndex(CWORD));
        word.translation = c.getString(c.getColumnIndex(CTRANSLATION));
        word.detail= c.getString(c.getColumnIndex(CDETAIL));
        return word;
    }

    public List<Word> getList() {
        Cursor c = db.rawQuery("select * from " + TABLE_VOCABULARY, null);
        List<Word> vocabulary = new ArrayList<Word>();

        while(c.moveToNext()) {
            Word word = WordBuilder(c);
            vocabulary.add(word);
        }

        return vocabulary;
    }

    public Word get(String word) {
        Cursor c = db.rawQuery(
                "select * from " + TABLE_VOCABULARY + " where "
                        + CWORD + " = \"" + String.valueOf(word) + "\" limit 1", null);
        c.moveToNext();
        return WordBuilder(c);
    }

    public Word get(Long id) {
        Cursor c = db.rawQuery(
                "select * from " + TABLE_VOCABULARY + " where "
                        + CID + " = " + String.valueOf(id) + " limit 1", null);
        c.moveToNext();
        return WordBuilder(c);
    }

    public void insert(Word word) {
        ContentValues values = new ContentValues();
        values.put(CWORD, word.word);
        values.put(CTRANSLATION, word.translation);
        values.put(CDETAIL, word.detail);
        db.insert(TABLE_VOCABULARY, null, values);
    }

    public void remove(String keyword) {
        db.delete(TABLE_VOCABULARY, CWORD + " = \"" + keyword + "\"", null);
    }

    public void remove(Long id) {
        db.delete(TABLE_VOCABULARY, CID + " = " + id, null);
    }
}
