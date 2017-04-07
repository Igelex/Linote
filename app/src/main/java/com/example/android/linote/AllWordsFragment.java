package com.example.android.linote;


import android.content.ContentUris;
import android.content.Intent;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.android.linote.Database.LinoteContract;

/**
 * A simple {@link Fragment} subclass.
 */
public class AllWordsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static LinoteCursorAdapter mAdapter;
    private static final int LOADER_INDEX = 0;



    public AllWordsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.word_list, container, false);
        ListView listView = (ListView) rootView.findViewById(R.id.list_view);

        mAdapter = new LinoteCursorAdapter(getContext(),null);
        listView.setAdapter(mAdapter);

        getLoaderManager().initLoader(LOADER_INDEX, null, this);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getContext(), WordDetails.class);
                intent.setData(ContentUris.withAppendedId(LinoteContract.LinoteEntry.CONTENT_URI, l));
                if (intent.resolveActivity(getContext().getPackageManager()) != null){
                    startActivity(intent);
                }
            }
        });


        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(getContext(),
                LinoteContract.LinoteEntry.CONTENT_URI,
                LinoteContract.LinoteEntry.PROJECTION,
                null, null,
                LinoteContract.LinoteEntry.SORT_ORDER);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mAdapter.swapCursor(cursor);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }
}
