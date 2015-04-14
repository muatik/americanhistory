package com.muatik.americanhistory;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.muatik.americanhistory.Stories.API.StoryFetchTask;
import com.muatik.americanhistory.Stories.Story;
import com.squareup.otto.Subscribe;
import com.muatik.americanhistory.DisplayEditor.*;


import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by muatik on 23.03.2015.
 */
public class FragmentStory extends FragmentDebug
        implements FragmentTitles.IFragmentTitles,
                   StoryFetchTask.IStoryFetchTask {

    protected  Long id;
    protected SharedPreferences preferences;

    View mainView;
    @InjectView(R.id.detail) TextView viewDetail;
    @InjectView(R.id.title) TextView viewTitle;
    @InjectView(R.id.storyProgress) View storyProgress;
    @InjectView(R.id.storyView) View storyView;
    @InjectView(R.id.media_player) View mediaPlayerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.fragment_story, container, false);
        ButterKnife.inject(this, mainView);
        Log.d(MainActivity.TAG, "onCreateView");
        return mainView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getArguments() != null) {
            id = getArguments().getLong("id");
            onTitleSelected(id);
        }

        this.setHasOptionsMenu(true);

        BusProvider.get().register(this);

        ((ActionBarActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        preferences = getActivity().getSharedPreferences(MainActivity.PREF_NAME, 0);

        setFontFamily(preferences.getString(
                DisplayEditor.FONT_FAMILY,
                DisplayEditor.DEFAULT_FONT_FAMILY));

        setFontSize(preferences.getInt(
                DisplayEditor.FONT_SIZE,
                DisplayEditor.DEFAULT_FONT_SIZE));

        setFontColor(preferences.getString(
                DisplayEditor.FONT_COLOR,
                DisplayEditor.DEFAULT_FONT_COLOR));

        setBackgroundColor(preferences.getString(
                DisplayEditor.BG_COLOR,
                DisplayEditor.DEFAULT_BG_COLOR));

        viewDetail.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                String selectedText = viewDetail.getText().toString().substring(
                        viewDetail.getSelectionStart(), viewDetail.getSelectionEnd());
                showTranslation(selectedText);
                return false;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.findItem(R.id.displaySettings).setVisible(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (R.id.displaySettings == item.getItemId()) {
            DisplayEditor d = new DisplayEditor();
            d.setTargetFragment(this, 0);
            d.show(getFragmentManager(), null);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTitleSelected(Long id) {
        new StoryFetchTask(this).execute(Long.valueOf(4));
    }

    @Override
    public void onStoryFetchCompleted(Story story) {
        viewTitle.setText(story.title);
        viewDetail.setText(story.detail);
        storyProgress.setVisibility(View.GONE);
        storyView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onStoryFetchFailed(Exception e) {
        e.printStackTrace();
        viewTitle.setText(R.string.error_storyAPI_io);
        mediaPlayerView.setVisibility(View.GONE);
        storyProgress.setVisibility(View.GONE);
        storyView.setVisibility(View.VISIBLE);
    }


    @Subscribe
    public void onFotSizeChanged(FontSizeChanged event) {
        setFontSize(event.fontSize);
        preferences
                .edit().putInt(DisplayEditor.FONT_SIZE, event.fontSize).apply();
    }

    @Subscribe
    public void onFontFamilyChanged(FontFamilyChanged event) {
        setFontFamily(event.fontFamily);
        preferences
                .edit().putString(DisplayEditor.FONT_FAMILY, event.fontFamily).apply();
    }

    @Subscribe
    public void onFontColorChanged(FontColorChanged event) {
        setFontColor(event.fontColor);
        preferences
                .edit().putString(DisplayEditor.FONT_COLOR, event.fontColor).apply();
    }

    @Subscribe
    public void onBackgroundColorChanged(BackgroundColorChanged event) {
        setBackgroundColor(event.backgroundColor);
        preferences
                .edit().putString(DisplayEditor.BG_COLOR, event.backgroundColor).apply();
    }



    protected void setBackgroundColor(String color) {
        mainView.setBackgroundColor(Color.parseColor(color));
    }

    protected void setFontColor(String color) {
        viewDetail.setTextColor(Color.parseColor(color));
        viewTitle.setTextColor(Color.parseColor(color));
    }

    protected void setFontSize(int size) {
        viewTitle.setTextSize(size);
        viewDetail.setTextSize(size);
    }

    protected void setFontFamily(String family) {
        viewTitle.setTypeface(Typeface.create(family, Typeface.BOLD));
        viewDetail.setTypeface(Typeface.create(family, 0));
    }


    protected void showTranslation(String keyword) {
        Bundle bundle = new Bundle();
        bundle.putString(TranslationBox.KEYWORD, keyword);

        TranslationBox translationBox = new TranslationBox();
        translationBox.setArguments(bundle);
        translationBox.setTargetFragment(this, 0);
        translationBox.show(getFragmentManager(), null);
    }
}
