package com.muatik.americanhistory.GoogleTranslator;


import android.util.Log;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by muatik on 09.04.2015.
 */
public class GoogleTranslator {

    public static final String TAG = "GoogleTranslator";

    protected final static String clientUrl = "https://translate.google.com/translate_a/single?client=t&sl=en&tl=tr&dt=t&hl=tr&dt=bd&dt=ld&ie=UTF-8&oe=UTF-8&otf=1&ssel=0&tsel=0&q=";
    protected static int timeout = 10;

    public static class Translation
    {
        public String text;
        public String firstTranslation;
        public List<GroupedTranslation> translations = new ArrayList<GroupedTranslation>();

        public void add(String groupName, List<String> translations) {
            this.translations.add(new GroupedTranslation(groupName, translations));
        }

        public class GroupedTranslation
        {
            public String groupName;
            public List<String> translations;

            public GroupedTranslation(String groupName, List<String> translations) {
                this.groupName = groupName;
                this.translations = translations;
            }
        }
    }

    public static Translation translate(String keyword) throws JSONException, IOException {
        JSONArray json;

        try {
            JSONArray rawjson = fetchJson(keyword);

            JSONArray brief = (JSONArray)((JSONArray) rawjson.get(0)).get(0);

            Translation translation = new Translation();
            translation.text = brief.get(1).toString();
            translation.firstTranslation = brief.get(0).toString();

            try {
                JSONArray subTranslationGroups = ((JSONArray) rawjson.get(1));
                for (int i = 0; i < subTranslationGroups.length(); i++) {
                    JSONArray subTranslation = subTranslationGroups.getJSONArray(i);

                    JSONArray subMeanings = (JSONArray) subTranslation.get(1);
                    List<String> meanings = new ArrayList<String>();
                    for (int m = 0; m < subMeanings.length(); m++) {
                        meanings.add(subMeanings.getString(m));
                    }

                    translation.add(subTranslation.get(0).toString(), meanings);
                }
            } catch (Exception e) {

            }

            return translation;

        } catch (JSONException e) {
            Log.e(TAG, "Google translator cannot parse response json properly.");
            throw e;
        } catch (IOException e) {
            Log.e(TAG, "Google translator has a IO error.");
            throw e;
        }
    }

    protected static JSONArray fetchJson(String keyword) throws JSONException, IOException {
        OkHttpClient client = new OkHttpClient();
        client.setConnectTimeout(timeout, TimeUnit.SECONDS);

        URL url = new URL(clientUrl + keyword);

        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = client.newCall(request).execute();

        String rawJson = response.body().string()
                .replaceAll(",,", ",").replaceAll(",,", ",").replaceAll(",,", ",")
                .replace("[,", "[");

        return new JSONArray(rawJson);
    }
}
