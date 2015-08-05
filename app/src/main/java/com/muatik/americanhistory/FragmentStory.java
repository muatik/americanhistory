package com.muatik.americanhistory;

import android.app.Application;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RemoteViews;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.muatik.americanhistory.Stories.API.StoryFetchTask;
import com.muatik.americanhistory.Stories.Collection;
import com.muatik.americanhistory.Stories.Story;
import com.muatik.americanhistory.Vocabulary.StoryPlayer;
import com.squareup.otto.Subscribe;
import com.muatik.americanhistory.DisplayEditor.*;


import java.io.IOException;
import java.net.URI;
import java.net.URL;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by muatik on 23.03.2015.
 */
public class FragmentStory extends FragmentDebug
        implements FragmentTitles.IFragmentTitles,
                   StoryFetchTask.IStoryFetchTask {

    protected  Long id;
    protected SharedPreferences preferences;
    protected StoryPlayer player;
    protected String playerStatus="stopped";
    protected Story story;

    View mainView;
    @InjectView(R.id.detail) TextView viewDetail;
    @InjectView(R.id.title) TextView viewTitle;
    @InjectView(R.id.storyProgress) View storyProgress;
    @InjectView(R.id.storyView) View storyView;
    @InjectView(R.id.play) ImageButton playButton;
    @InjectView(R.id.media_player) View mediaPlayerView;
    @InjectView(R.id.player_progress) SeekBar seekbar;

    @Override
    public void onResume() {
        super.onResume();
        String a = MainActivity.myService.getUrl();
        Log.d("------------serviceUrl", a);

        if (MainActivity.myService.getMediaPlayer().isPlaying()) {
            playButton.setImageResource(android.R.drawable.ic_media_pause);
            Log.d("----------------", "playing" + a);
            if (a !="")
                StoryPlayer.updateSeekbar();

        } else {
            Log.d("----------------", "paused " + a);
            playButton.setImageResource(android.R.drawable.ic_media_play);
            if (a !="")
                StoryPlayer.updateSeekbar();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.fragment_story, container, false);
        ButterKnife.inject(this, mainView);
        return mainView;
    }

    private BroadcastReceiver MediaPlayerServiceListener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String status = intent.getStringExtra("status");
            if (status =="playing") {
                playButton.setImageResource(android.R.drawable.ic_media_pause);
            } if (status =="pause") {
                playButton.setImageResource(android.R.drawable.ic_media_play);
            }
        }
    };

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        LocalBroadcastManager.getInstance(getActivity().getBaseContext()).registerReceiver(MediaPlayerServiceListener,
                new IntentFilter("MediaPlayerService"));

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
    public void onStop() {
        try {
            BusProvider.get().unregister(this);
        } catch (Exception e) {

        }
        super.onStop();
    }

    @Override
    public void onTitleSelected(Long id) {
        //((MainActivity) getActivity()).setActionBarTitle("loading...");
        this.story = new Collection(getActivity().getApplicationContext()).get(id);
        viewTitle.setText(story.title);
        viewDetail.setText(story.detail);
        storyProgress.setVisibility(View.GONE);
        storyView.setVisibility(View.VISIBLE);
        //new StoryFetchTask(this).execute(Long.valueOf(4));
    }

    @Override
    public void onStoryFetchCompleted(Story story) {
        //this.story = story;
        ((MainActivity) getActivity()).setActionBarTitle(story.title);
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
        try {
            TranslationBoxHelper.show(this, keyword);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @OnClick(R.id.play)
    public void audioPlay(View view) {
        final ImageButton audioPlay = (ImageButton) view;
        String url = "http://av.voanews.com/clips/VLE/2015/06/16/5746df4f-750a-4f17-b1ca-d717d0e69bc1.mp3";
        if (playerStatus == "stopped") {
            audioPlay.setImageResource(android.R.drawable.ic_media_pause);
            audioPlay.refreshDrawableState();
            StoryPlayer.set(story, seekbar, getActivity().getApplication());
            playerStatus = "playing";
            StoryPlayer.play();
            StoryPlayer.getPlayer().setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    audioPlay.setImageResource(android.R.drawable.ic_media_play);
                    StoryPlayer.clearNotification();
                    StoryPlayer.resetProgress();
                }
            });

        } else if (StoryPlayer.getPlayer().isPlaying()) {
            audioPlay.setImageResource(android.R.drawable.ic_media_play);
            StoryPlayer.pause();
        } else if (!StoryPlayer.getPlayer().isPlaying()) {
            audioPlay.setImageResource(android.R.drawable.ic_media_pause);
            StoryPlayer.play();
        }
    }
}
