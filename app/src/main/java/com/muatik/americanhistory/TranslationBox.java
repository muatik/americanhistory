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

import butterknife.ButterKnife;
import butterknife.InjectView;
import com.muatik.americanhistory.GoogleTranslator.GoogleTranslator;
import com.muatik.americanhistory.GoogleTranslator.GoogleTranslator.Translation;

import org.json.JSONException;

import java.io.IOException;


/**
 * Created by muatik on 08.04.2015.
 */
public class TranslationBox extends DialogFragment {

    public final static String KEYWORD = "keyword";
    protected String keyword;

    @InjectView(R.id.keyword) TextView keywordView;
    @InjectView(R.id.translation) TextView firstTranslationView;
    @InjectView(R.id.translationProgress) View translationProgress;
    @InjectView(R.id.main) View mainView;

    private String lastErrorMsg;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.translation_box, container, false);
        getDialog().setTitle(R.string.translation_title);
        ButterKnife.inject(this, view);

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
            Log.d(MainActivity.TAG, msg);
            throw new IllegalArgumentException(msg);
        }
        super.onAttach(activity);
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
            if (keywordView ==null)
                return;
            translationProgress.setVisibility(View.GONE);
            mainView.setVisibility(View.VISIBLE);
            if (translation != null)
                onTranslationCompleted(translation);
            else
                keywordView.setText(lastErrorMsg);
        }
    }

    private void onTranslationCompleted(Translation translation) {
        keywordView.setText(keyword);
        firstTranslationView.setText(translation.firstTranslation);

        for (Translation.GroupedTranslation i: translation.translations) {
            View item = getActivity().getLayoutInflater().inflate(R.layout.translation_item, null);
            TextView groupName = (TextView) item.findViewById(R.id.groupName);
            TextView words = (TextView) item.findViewById(R.id.words);
            groupName.setText(i.groupName+": ");

            for (Object w: i.translations.toArray()) {
                words.setText(words.getText() + w.toString() + ", " );
            }

            ((ViewGroup)getView()).addView(item);
        }






    }

}
