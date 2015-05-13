package com.muatik.americanhistory;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.AsyncTask;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import com.muatik.americanhistory.GoogleTranslator.GoogleTranslator;
import com.muatik.americanhistory.GoogleTranslator.GoogleTranslator.Translation;
import com.muatik.americanhistory.Vocabulary.Collection;
import com.muatik.americanhistory.Vocabulary.Word;
import com.squareup.otto.Bus;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by muatik on 08.04.2015.
 */
public class TranslationBox extends DialogFragment {

    public final static String KEYWORD = "keyword";
    protected String keyword;
    Translation translation;

    @InjectView(R.id.keyword) TextView keywordView;
    @InjectView(R.id.translation) TextView firstTranslationView;
    @InjectView(R.id.translationProgress) View translationProgress;
    @InjectView(R.id.main) View mainView;
    @InjectView(R.id.alert) View alertView;
    @InjectView(R.id.alertMessage) TextView alertMessage;
    @InjectView(R.id.favoriteToggle) ToggleButton favoriteToggle;

    private String lastErrorMsg;
    private Collection vocabulary;
    private Bus bus;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vocabulary = new Collection(getActivity());
        bus = BusProvider.get();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.translation_box, container, false);
        ButterKnife.inject(this, view);
        startTranslation();
        return view;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        // request a window without the title
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onAttach(Activity activity) {
        Bundle bundle = getArguments();
        keyword = bundle.getString(KEYWORD);
        if (keyword == null) {
            String msg = "TranslationBox could not find keyword in bundle arguments.";
            throw new IllegalArgumentException(msg);
        }
        super.onAttach(activity);
    }

    private void startTranslation(String keyword) {
        this.keyword = keyword;
        startTranslation();
    }

    private void startTranslation() {
        alertView.setVisibility(View.GONE);
        mainView.setVisibility(View.GONE);
        translationProgress.setVisibility(View.VISIBLE);
        new TranslationTask().execute(keyword);
    }

    class TranslationTask extends AsyncTask<String, Long, Translation> {

        @Override
        protected Translation doInBackground(String... keywords) {
            try {
                return GoogleTranslator.translate(keywords[0]);
            } catch (JSONException e) {
                e.printStackTrace();
                lastErrorMsg = getString(R.string.error_translation_parsing);
            } catch (IOException e) {
                e.printStackTrace();
                lastErrorMsg = getString(R.string.error_translation_io);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Translation translation) {
            super.onPostExecute(translation);
            if (keywordView == null)
                return;

            translationProgress.setVisibility(View.GONE);

            if (translation != null)
                onTranslationCompleted(translation);
            else
                onTranslationFailed();
        }
    }

    private void onTranslationFailed() {
        alertView.setVisibility(View.VISIBLE);
        alertMessage.setText(lastErrorMsg);
    }

    private void onTranslationCompleted(Translation translation) {
        this.translation = translation;
        keywordView.setText(keyword);
        firstTranslationView.setText(translation.firstTranslation);

        LayoutInflater inflater;
        try {
            inflater = getActivity().getLayoutInflater();
        } catch (NullPointerException e) {
            e.printStackTrace();
            return;
        }

        for (Translation.GroupedTranslation i: translation.translations) {
            View item = inflater.inflate(R.layout.translation_item, null);
            TextView groupName = (TextView) item.findViewById(R.id.groupName);
            TextView words = (TextView) item.findViewById(R.id.words);
            groupName.setText(i.groupName+": ");

            for (Object w: i.translations.toArray()) {
                words.setText(words.getText() + w.toString() + ", " );
            }

            ((ViewGroup)getView()).addView(item);
        }


        // if the word is already in the vocabulary, the word view should be displayed as favorited
        try {
            Word word = vocabulary.get(keyword);
            favoriteToggle.setChecked(true);
        } catch (Exception e) {}

        mainView.setVisibility(View.VISIBLE);
    }


    private void removeVocabulary() {
        vocabulary.remove(keyword);
        bus.post(new EventRemoveVocabulary(keyword));
        Toast.makeText(getActivity(), R.string.word_unfavorited, Toast.LENGTH_SHORT).show();
    }

    private void insertVocabulary() {
        Word w = new Word();
        w.word = keyword;
        w.translation= translation.firstTranslation;
        w.detail = null;
        vocabulary.insert(w);
        bus.post(new EventInsterVocabulary(w));
        Toast.makeText(getActivity(), R.string.word_favorited, Toast.LENGTH_SHORT).show();
    }


    @OnClick(R.id.favoriteToggle)
    public void favoriteClicked(ToggleButton toggle) {
        Boolean favorited = toggle.isChecked();
        if (favorited)
            insertVocabulary();
        else
            removeVocabulary();

    }

    public class EventInsterVocabulary {
        public Word word;
        public EventInsterVocabulary(Word word) {
            this.word = word;
        }
    }

    public class EventRemoveVocabulary {
        public String keyword;
        public EventRemoveVocabulary(String keyword) {
            this.keyword = keyword;
        }
    }
}
