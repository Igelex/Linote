package com.example.android.linote;


import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.linote.Database.LinoteContract;

/**
 * A simple {@link Fragment} subclass.
 */
public class EngWordListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static LinoteCursorAdapter mAdapterEng;
    private static final int LOADER_INDEX = 1;

    public EngWordListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.word_list, container, false);

        ListView listView = (ListView) rootView.findViewById(R.id.list_view);

        mAdapterEng = new LinoteCursorAdapter(getContext(), null);
        listView.setAdapter(mAdapterEng);

        getLoaderManager().initLoader(LOADER_INDEX, null, this);

        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getContext(),
                LinoteContract.LinoteEntry.CONTENT_URI,
                LinoteContract.LinoteEntry.PROJECTION,
                LinoteContract.LinoteEntry.SELECTION_ENG_WORDS,
                LinoteContract.LinoteEntry.SELECTION_ARGS_ENG_WORDS,
                LinoteContract.LinoteEntry.SORT_ORDER);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapterEng.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapterEng.swapCursor(null);
    }
}
