package com.muatik.americanhistory.Stories.API;

import android.util.Log;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.muatik.americanhistory.Stories.Story;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

/**
 * Created by muatik on 24.03.2015.
 */
public class REST {

    protected static final String REST_URL = "https://livechat24-7.com/api/agents";
    private int timeout = 10;

    public String makeRequest(URL url) throws IOException {


        OkHttpClient client = new OkHttpClient();
        client.setConnectTimeout(timeout, TimeUnit.SECONDS);

        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = client.newCall(request).execute();
        return response.toString();
    }

    public Story fetch(Long id) throws IOException {
        String rawResponse;

        URL url = new URL(REST_URL + "/" + String.valueOf(id));
        rawResponse = makeRequest(url);

        rawResponse = "{\"id\":1,\"title\":\"Clash of Cultures in the New World\",\"detail\":\"The first recorded meetings between Europeans and the natives of the East Coast took place in the 1500s.  Fishermen from France and the Basque area of Spain crossed the Atlantic Ocean.  They searched for whales along the east coast of North America.  They made temporary camps along the coast.  They often traded with the local Indians.\\n\\nThe Europeans often paid Indians to work for them.  Both groups found this to be a successful relationship.  Several times different groups of fishermen tried to establish a permanent settlement on the coast, but the severe winters made it impossible.  These fishing camps were only temporary.\\n\\nThe first permanent settlers in New England began arriving in 1620.  They wanted to live in peace with the Indians. They needed to trade with them for food.  The settlers also knew that a battle would result in their own, quick defeat because they were so few in number.\\n\\nYet, problems began almost immediately.  Perhaps the most serious was the different way the American Indians and the Europeans thought about land.  This difference created problems that would not be solved during the next several hundred years.\\n\\nLand was extremely important to the European settlers.  In England, and most other countries, land meant wealth.  Owning large amounts of land meant a person had great wealth and political power.\\n\\nMany of the settlers in this new country could never have owned land in Europe.  They were too poor.  And they belonged to minority religious groups.  When they arrived in the new country, they discovered no one seemed to own the huge amounts of land.\\n\\nCompanies in England needed to find people willing to settle in the new country.  So they offered land to anyone who would take the chance of crossing the Atlantic Ocean.  For many, it was a dream come true.  It was a way to improve their lives.  The land gave them a chance to become wealthy and powerful.\\n\\nAmerican Indians believed no person could own land.  They believed, however, that anyone could use it.  Anyone who wanted to live on and grow crops on a piece of land was able to do so.\\n\\nThe American Indians lived within nature.  They lived very well without working very hard.  They were able to do this because they understood the land and their environment.  They did not try to change the land.  They might farm in an area for a few years. Then they would move on.  They permitted the land on which they had farmed to become wild again.\\n\\nThey might hunt on one area of land for some time, but again they would move on.  They hunted only what they could eat, so the numbers of animals continued to increase.  The Indians understood nature and made it work for them.\\n\\nThe first Europeans to settle in New England in the northeastern part of America were few in number.  They wanted land.  The Indians did not fear them.  There was enough land for everyone to use and plant crops.  It was easy to live together.  The Indians helped the settlers by teaching them how to plant crops and survive on the land.\\n\",\"audioUrl\":\"http://blabla...\",\"scrollPosition\":0,\"availableOffline\":0}";

        ObjectMapper mapper = new ObjectMapper();
        Story story = mapper.readValue(rawResponse.getBytes(), Story.class);
        return story;
    }

}
