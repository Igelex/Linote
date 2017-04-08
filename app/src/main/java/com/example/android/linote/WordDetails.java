package com.example.android.linote;

import android.app.LoaderManager;
import android.app.SearchManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.android.linote.Database.LinoteContract;

import static android.R.id.edit;
import static android.provider.Contacts.SettingsColumns.KEY;

public class WordDetails extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private boolean mWordHasChanged = false;
    private Uri mCurrentUri;
    private String wordWebUri, intentString, translationDirection;
    private TextView word, translation, details, description, examples, collocations;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mWordHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.word_details);

        Intent intent = getIntent();
        mCurrentUri = intent.getData();

        getLoaderManager().initLoader(0, null, this);

        word = (TextView) findViewById(R.id.details_default_word);
        translation = (TextView) findViewById(R.id.details_translation);
        details = (TextView) findViewById(R.id.details_details);
        description = (TextView) findViewById(R.id.details_desc);
        examples = (TextView) findViewById(R.id.details_examples);
        collocations = (TextView) findViewById(R.id.details_collocations);

        ImageButton abbyButton = (ImageButton) findViewById(R.id.wiki_button);
        ImageButton googleButton = (ImageButton) findViewById(R.id.google_button);

        abbyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Uri abbyUri = Uri.parse("https://www.lingvolive.com/en-us/translate/" + translationDirection
                        + "/" + wordWebUri);
                Intent intent = new Intent(WordDetails.this, WebActivity.class);
                intent.setData(abbyUri);
                intent.putExtra("title", intentString);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });

        googleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
                intent.putExtra(SearchManager.QUERY, wordWebUri.toLowerCase()); // query contains search string
                startActivity(intent);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public Loader onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                mCurrentUri,
                LinoteContract.LinoteEntry.PROJECTION_DETAILS,
                null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        if (cursor.moveToFirst()) {
            int language = (cursor.getInt(cursor.getColumnIndex(LinoteContract.LinoteEntry.COLUMN_NAME_LANGUAGE)));
            word.setText(cursor.getString(cursor.getColumnIndexOrThrow(LinoteContract.LinoteEntry.COLUMN_NAME_WORD)));
            translation.setText(cursor.getString(cursor.getColumnIndexOrThrow(LinoteContract.LinoteEntry.COLUMN_NAME_TRANSLATION)));
            String pos = (cursor.getString(cursor.getColumnIndexOrThrow(LinoteContract.LinoteEntry.COLUMN_NAME_PARTOFSPEECH)));
            String article = (cursor.getString(cursor.getColumnIndexOrThrow(LinoteContract.LinoteEntry.COLUMN_NAME_ARTICLE)));
            String desc = (cursor.getString(cursor.getColumnIndexOrThrow(LinoteContract.LinoteEntry.COLUMN_NAME_DESCRIPTION)));
            String examp = (cursor.getString(cursor.getColumnIndexOrThrow(LinoteContract.LinoteEntry.COLUMN_NAME_EXAMPLES)));
            String colloc = (cursor.getString(cursor.getColumnIndexOrThrow(LinoteContract.LinoteEntry.COLUMN_NAME_COLLOCATIONS)));

            intentString = word.getText().toString();
            switch (language) {
                case LinoteContract.LinoteEntry.LANGUAGE_ENGLISH:
                    translationDirection = "en-ru";
                    break;
                case LinoteContract.LinoteEntry.LANGUAGE_GERMAN:
                    translationDirection = "de-ru";
            }

            wordWebUri = word.getText().toString();
            setTitle(word.getText().toString());

            if (desc.trim().isEmpty()) {
                TextView sectionDesc = (TextView) findViewById(R.id.details_selection_desc);
                sectionDesc.setVisibility(View.GONE);
                description.setVisibility(View.GONE);
            } else {
                description.setText(desc);
            }

            if (examp.trim().isEmpty()) {
                examples.setVisibility(View.GONE);
                TextView sectionExamp = (TextView) findViewById(R.id.details_selection_examples);
                sectionExamp.setVisibility(View.GONE);
            } else {
                examples.setText(examp);
            }
            if (colloc.trim().isEmpty()) {
                collocations.setVisibility(View.GONE);
                TextView sectionColloc = (TextView) findViewById(R.id.details_selection_collocations);
                sectionColloc.setVisibility(View.GONE);
            } else {
                collocations.setText(colloc);
            }
            if (article == null) {
                details.setText(pos);
            } else {
                details.setText(pos + ", " + article);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }
}
