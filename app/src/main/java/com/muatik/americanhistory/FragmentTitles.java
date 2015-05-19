package com.muatik.americanhistory;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.muatik.americanhistory.Stories.Collection;
import com.muatik.americanhistory.Stories.Story;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by muatik on 23.03.2015.
 */
public class FragmentTitles extends FragmentDebug {

    public interface IFragmentTitles {
        void onTitleSelected(Long id);
    }

    IFragmentTitles parent;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            parent = (IFragmentTitles) activity;
        } catch (Exception e) {
            throw new ClassCastException("implemente etmemeisin be gulum...");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("debuga", "titles onCreateView");
        View view = inflater.inflate(R.layout.fragment_titles, container, false);

        List<Story> stories = new Collection(getActivity().getApplicationContext()).getList();
        ArrayList<String> data = new ArrayList<String>();

        for (Story i : stories) {
            data.add(i.title);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                getActivity().getApplicationContext(),
                R.layout.title_item,
                data
        );

        ListView listTitles = (ListView) view.findViewById(R.id.list_titles);
        listTitles.setAdapter(adapter);

        listTitles.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
                parent.onTitleSelected(id);
            }
        });

        ((MainActivity) getActivity()).setActionBarTitle("American History");

        return view;
    }
}
