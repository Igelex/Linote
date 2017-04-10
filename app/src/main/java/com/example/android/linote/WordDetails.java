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
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.linote.Database.LinoteContract;


public class WordDetails extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private Uri mCurrentUri;
    private String wordWebUri, translationDirection;
    private TextView word, translation, details, description, examples, collocations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.word_details);

        mCurrentUri = getIntent().getData();

        if (mCurrentUri != null) {
            try {
                getLoaderManager().initLoader(0, null, this);
            } catch (Exception e) {
                Log.e("Details: ", "Error by initLoader", e);
            }
        } else {
            Toast.makeText(this, "Error, no data found", Toast.LENGTH_LONG).show();
        }

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
                intent.putExtra("title", wordWebUri);
                intent.putExtra("backUri", mCurrentUri.toString());
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(intent, 1);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data == null){
            Toast.makeText(this, "Error, invalid backUri", Toast.LENGTH_LONG).show();
            finish();
        } else {
            mCurrentUri = data.getData();
            try {
                getLoaderManager().initLoader(0, null, this);
            } catch (Exception e) {
                Log.e("Details: ", "Error by initLoader", e);
            }
        }
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

            wordWebUri = word.getText().toString();
            setTitle(wordWebUri);

            switch (language) {
                case LinoteContract.LinoteEntry.LANGUAGE_ENGLISH:
                    translationDirection = "en-ru";
                    break;
                case LinoteContract.LinoteEntry.LANGUAGE_GERMAN:
                    translationDirection = "de-ru";
            }

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
