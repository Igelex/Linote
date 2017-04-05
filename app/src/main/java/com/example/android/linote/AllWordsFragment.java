package com.example.android.linote;


import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.linote.Database.LinoteContract;

/**
 * A simple {@link Fragment} subclass.
 */
public class AllWordsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {


    public AllWordsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.word_list, container, false);
        ListView listView = (ListView) rootView.findViewById(R.id.words_list_view);

        LinoteCursorAdapter adapter = new LinoteCursorAdapter(getContext(),null);
        listView.setAdapter(adapter);


        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(getContext(),
                LinoteContract.LinoteEntry.CONTENT_URI,
                LinoteContract.LinoteEntry.PROJECTION,
                null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
