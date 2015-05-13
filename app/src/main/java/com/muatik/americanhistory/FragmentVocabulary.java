package com.muatik.americanhistory;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.muatik.americanhistory.Vocabulary.Collection;
import com.muatik.americanhistory.Vocabulary.Word;
import com.squareup.otto.Subscribe;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by muatik on 5/13/15.
 */
public class FragmentVocabulary extends Fragment {

    @InjectView(R.id.list_words) ListView wordList;
    VocabularyAdapter vocabularyAdapter;
    List<Word> words;

    public class VocabularyAdapter extends ArrayAdapter<Word>
    {
        private int viewResource;
        private List<Word> words;
        private Context context;

        public void remove(String keyword) {
            for (int i=0; i < words.size(); i++) {
                if (words.get(i).word.equals(keyword)) {
                    words.remove(i);
                    Log.e(MainActivity.TAG, "found and removed;");
                    break;
                }
            }
        }

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


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_vocabulary, container, false);
        ButterKnife.inject(this, view);

        words = new Collection(getActivity()).getList();
        Toast.makeText(getActivity(),String.valueOf(words.size()), Toast.LENGTH_LONG).show();
        vocabularyAdapter = new VocabularyAdapter(getActivity(), R.layout.vocabulary_list_item, words);
        wordList.setAdapter(vocabularyAdapter);

        wordList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String keyword = ((TextView) view.findViewById(R.id.word)).getText().toString();
                wordSelected(keyword);
            }
        });

        BusProvider.get().register(this);

        return view;
    }

    private void wordSelected(String keyword) {
        try {
            TranslationBoxHelper.show(this, keyword);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Subscribe
    public void onWordRemoved(TranslationBox.EventRemoveVocabulary e) {
        vocabularyAdapter.remove(e.keyword);
        vocabularyAdapter.notifyDataSetChanged();
    }

    @Subscribe
    public void onWordInserted(TranslationBox.EventInsterVocabulary e) {
        vocabularyAdapter.add(e.word);
        vocabularyAdapter.notifyDataSetChanged();
    }

}
