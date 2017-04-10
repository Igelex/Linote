package com.example.android.linote;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.support.design.widget.FloatingActionButton;
import android.content.CursorLoader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.linote.Database.LinoteContract;

import java.util.zip.Inflater;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static LinoteCursorAdapter mAdapter;
    private static final int LOADER_INDEX = 0;
    private ListView listView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = (ProgressBar) findViewById(R.id.progressBar_main);
        listView = (ListView) findViewById(R.id.list_view);

        View view = getLayoutInflater().inflate(R.layout.list_footer, null);
        listView.addFooterView(view);

        mAdapter = new LinoteCursorAdapter(this, null);
        listView.setAdapter(mAdapter);
        listView.setEmptyView(findViewById(R.id.empty_view_container));

        getLoaderManager().initLoader(LOADER_INDEX, null, this);

        FloatingActionButton fb = (FloatingActionButton) findViewById(R.id.fb);
        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, AddNewWord.class));

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(MainActivity.this, WordDetails.class);
                intent.setData(ContentUris.withAppendedId(LinoteContract.LinoteEntry.CONTENT_URI, l));
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        progressBar.setVisibility(View.VISIBLE);
        return new CursorLoader(this,
                LinoteContract.LinoteEntry.CONTENT_URI,
                LinoteContract.LinoteEntry.PROJECTION,
                null, null,
                LinoteContract.LinoteEntry.SORT_ORDER);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        progressBar.setVisibility(View.GONE);
        if (cursor == null) {
            listView.setEmptyView(findViewById(R.id.empty_view_container));
        } else {
            mAdapter.swapCursor(cursor);
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }
}
