package com.muatik.americanhistory;

import android.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.muatik.americanhistory.FragmentTitles.IFragmentTitles;
import com.muatik.americanhistory.Vocabulary.Collection;
import com.muatik.americanhistory.Vocabulary.Word;
import com.squareup.otto.Bus;


public class MainActivity extends ActionBarActivity
        implements IFragmentTitles {

    public final static String TAG = "americanhh";
    public static final String PREF_NAME = "ah_prefs";

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
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (hasFragmentContainer())
            replaceActiveFrame(new FragmentTitles());

        bus = BusProvider.get();
    }

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

    public void showModalClick(MenuItem item) {
    }

}