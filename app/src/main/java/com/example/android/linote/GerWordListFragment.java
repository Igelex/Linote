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

import com.example.android.linote.Database.LinoteContract;

/**
 * A simple {@link Fragment} subclass.
 */
public class GerWordListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static LinoteCursorAdapter mAdapterGer;
    private static final int LOADER_INDEX = 2;


    public GerWordListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.word_list, container, false);

        ListView listView = (ListView) rootView.findViewById(R.id.list_view);

        mAdapterGer = new LinoteCursorAdapter(getContext(),null);
        listView.setAdapter(mAdapterGer);

        getLoaderManager().initLoader(LOADER_INDEX, null, this);

        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getContext(),
                LinoteContract.LinoteEntry.CONTENT_URI,
                LinoteContract.LinoteEntry.PROJECTION,
                LinoteContract.LinoteEntry.SELECTION_GER_WORDS,
                LinoteContract.LinoteEntry.SELECTION_ARGS_GER_WORDS,
                LinoteContract.LinoteEntry.SORT_ORDER);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapterGer.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapterGer.swapCursor(null);
    }
}
