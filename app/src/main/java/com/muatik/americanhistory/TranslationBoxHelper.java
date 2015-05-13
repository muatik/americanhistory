package com.muatik.americanhistory;


import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;

import com.muatik.americanhistory.GoogleTranslator.GoogleTranslator;

/**
 * Created by muatik on 5/13/15.
 */
public class TranslationBoxHelper {

    public static TranslationBox show(Fragment fragment, String keyword) throws IllegalArgumentException{
        keyword = keyword.replaceAll("\\s","");
        if (keyword == "")
            throw new IllegalArgumentException("translation box could be shown due to that keyword was null.");

        Bundle bundle = new Bundle();
        bundle.putString(TranslationBox.KEYWORD, keyword);

        TranslationBox translationBox = new TranslationBox();
        translationBox.setArguments(bundle);
        translationBox.setTargetFragment(fragment, 0);
        translationBox.show(fragment.getFragmentManager(), null);
        return translationBox;
    }
}
