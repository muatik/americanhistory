package com.muatik.americanhistory.Stories.API;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.muatik.americanhistory.MainActivity;
import com.muatik.americanhistory.Stories.Story;

import java.io.IOException;
import java.lang.ref.WeakReference;

/**
 * Created by muatik on 24.03.2015.
 */
public class StoryFetchTask extends AsyncTask<Long, Story, Story> {

    private Exception lastError;

    public interface IStoryFetchTask {
        void onStoryFetchCompleted(Story story);
        void onStoryFetchFailed(Exception e);
    }

    private WeakReference<IStoryFetchTask> weakCaller;

    public StoryFetchTask(IStoryFetchTask caller){
        this.weakCaller = new WeakReference<IStoryFetchTask>(caller);
    }

    @Override
    protected Story doInBackground(Long... ids) {
        Long id = ids[0];
        Story story = null;
        try {
            story = new REST().fetch(id);
        } catch (IOException e) {
            lastError = e;
        }
        return story;
    }

    @Override
    protected void onPostExecute(Story story) {
        super.onPostExecute(story);
        IStoryFetchTask caller = weakCaller.get();
        if (caller == null)
            return;

        if (story != null)
            caller.onStoryFetchCompleted(story);
        else
            caller.onStoryFetchFailed(lastError);
    }

}
