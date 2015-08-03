package com.muatik.americanhistory;

import android.app.Fragment;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.muatik.americanhistory.FragmentTitles.IFragmentTitles;
import com.muatik.americanhistory.Vocabulary.StoryPlayer;
import com.squareup.otto.Bus;


public class MainActivity extends ActionBarActivity
        implements IFragmentTitles {

    public final static String TAG = "americanhh";
    public static final String PREF_NAME = "ah_prefs";

    public static MediaPlayerService myService;
    boolean isBound = false;

    protected Bus bus;

    protected boolean hasFragmentContainer() {
        return findViewById(R.id.main_container) != null;
    }

    protected void replaceActiveFrame(Fragment fragment) {
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.main_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 1) {
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        Long storyId = intent.getLongExtra("storyId", -1);
        if (storyId != -1) {
            onTitleSelected(storyId);
            return;
        }

        if (hasFragmentContainer())
            replaceActiveFrame(new FragmentTitles());

        Intent mediaPlayerServiceIntent = new Intent(this, MediaPlayerService.class);
        bindService(mediaPlayerServiceIntent, myConnection, Context.BIND_AUTO_CREATE);
        bus = BusProvider.get();
    }

    private ServiceConnection myConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            MediaPlayerService.MyLocalBinder binder = (MediaPlayerService.MyLocalBinder) service;
            myService = binder.getService();
            isBound = true;
        }

        public void onServiceDisconnected(ComponentName arg0) {
            isBound = false;
        }

    };
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            replaceActiveFrame(new FragmentVocabulary());
            return true;
        }

        if (id == android.R.id.home) {
            onBackPressed();
            if (getFragmentManager().getBackStackEntryCount() == 2)
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTitleSelected(Long id) {
        Log.d(TAG, String.valueOf(id));

        if (hasFragmentContainer()) {
            FragmentStory fragmentStory = new FragmentStory();
            Bundle bundle = new Bundle();
            bundle.putLong("id", id);
            fragmentStory.setArguments(bundle);
            replaceActiveFrame(fragmentStory);
        } else {
            IFragmentTitles fragmentStory = (IFragmentTitles)
                    getFragmentManager().findFragmentById(R.id.fragment_story);
            fragmentStory.onTitleSelected(id);
        }
    }

    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }
}