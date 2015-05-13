package com.muatik.americanhistory;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.muatik.americanhistory.Vocabulary.Collection;
import com.muatik.americanhistory.Vocabulary.Word;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by muatik on 5/13/15.
 */
public class FragmentVocabulary extends Fragment {

    @InjectView(R.id.list_words) ListView wordList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_vocabulary, container, false);
        ButterKnife.inject(this, view);
        List<Word> words = new Collection(getActivity()).getList();
        Toast.makeText(getActivity(),String.valueOf(words.size()), Toast.LENGTH_LONG).show();
        VocabularyAdapter adapter = new VocabularyAdapter(getActivity(), R.layout.vocabulary_list_item, words);
        wordList.setAdapter(adapter);
        return view;
    }

    public class VocabularyAdapter extends ArrayAdapter<Word>
    {
        private int viewResource;
        private List<Word> words;
        private Context context;

        public class ViewHolder
        {
            TextView wordView;
            TextView translationView;
        }

        public VocabularyAdapter(Context context, int viewResource, List<Word> words) {
            super(context, viewResource, words);
            this.words = words;
            this.context = context;
            this.viewResource = viewResource;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(viewResource, parent, false);
                holder = new ViewHolder();
                holder.wordView = (TextView) convertView.findViewById(R.id.word);
                holder.translationView= (TextView) convertView.findViewById(R.id.translation);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            Word word = words.get(position);
            holder.wordView.setText(word.word);
            holder.translationView.setText(word.translation);
            return convertView;
        }
    }
}
