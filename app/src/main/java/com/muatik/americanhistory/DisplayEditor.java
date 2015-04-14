package com.muatik.americanhistory;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.squareup.otto.Bus;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.internal.ButterKnifeProcessor;

/**
 * Created by muatik on 26.03.2015.
 */
public class DisplayEditor extends DialogFragment {

    public static final String BG_COLOR = "bg_color";
    public static final String FONT_COLOR = "font_color";
    public static final String FONT_SIZE = "font_size";
    public static final String FONT_FAMILY = "font_family";

    public static final String DEFAULT_FONT_FAMILY = "sans";
    public static final int DEFAULT_FONT_SIZE = 20;
    public static final String DEFAULT_FONT_COLOR = "#000000";
    public static final String DEFAULT_BG_COLOR = "#ffffff";

    protected int currentFontSize = 20;
    protected int maxFontSize = 25;
    protected int minFontSize = 12;


    public class FontSizeChanged {
        public int fontSize;
    }

    public class FontFamilyChanged {
        public String fontFamily;
    }

    public class FontColorChanged {
        public String fontColor;
    }

    public class BackgroundColorChanged {
        public String backgroundColor;
    }

    protected Bus bus;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.modal_dialog, container, false);
        ButterKnife.inject(this, view);
        getDialog().setTitle(R.string.display_title);

        SharedPreferences preferences = getActivity().getSharedPreferences(MainActivity.PREF_NAME, 0);
        currentFontSize = preferences.getInt(FONT_SIZE, DEFAULT_FONT_SIZE);

        bus = BusProvider.get();
        return view;
    }


    @OnClick(R.id.buttonSizeInc)
    public void incFontSize(View view) {
        if (currentFontSize + 1 > maxFontSize)
            return;
        currentFontSize +=1;

        FontSizeChanged e = new FontSizeChanged();
        e.fontSize = currentFontSize;
        bus.post(e);
    }

    @OnClick(R.id.buttonSizeDec)
    public void decFontSize(View view) {
        if (currentFontSize - 1 < minFontSize)
            return;
        currentFontSize -=1;

        FontSizeChanged e = new FontSizeChanged();
        e.fontSize = currentFontSize;
        bus.post(e);
    }

    @OnClick({R.id.familiy_sans, R.id.familiy_serif})
    public void setFontFamily_sans(View view) {
        FontFamilyChanged e = new FontFamilyChanged();
        e.fontFamily= view.getTag().toString();
        bus.post(e);
    }


    @OnClick({R.id.background_blue, R.id.background_green, R.id.background_red})
    public void setBackgroundColor(View view) {
        String[] colorHex = view.getTag().toString().split(",");

        FontColorChanged fe = new FontColorChanged();
        BackgroundColorChanged be = new BackgroundColorChanged();
        be.backgroundColor = colorHex[0];
        fe.fontColor = colorHex[1];
        bus.post(be);
        bus.post(fe);
    }
}
