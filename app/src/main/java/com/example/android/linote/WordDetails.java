package com.example.android.linote;

import android.app.LoaderManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.design.widget.Snackbar;
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
                if(isOnline()) {
                    Uri abbyUri = Uri.parse("https://www.lingvolive.com/en-us/translate/" + translationDirection
                            + "/" + wordWebUri);
                    Intent intent = new Intent(WordDetails.this, WebActivity.class);
                    intent.setData(abbyUri);
                    intent.putExtra("title", wordWebUri);
                    intent.putExtra("backUri", mCurrentUri.toString());
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(intent, 1);
                    }
                }else {
                    Snackbar snackbar = Snackbar.make(view, getString(R.string.no_intenet_connection_msg), Snackbar.LENGTH_LONG);
                    View snackbarView = snackbar.getView();
                    TextView snackBarText = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
                    snackBarText.setTextColor(Color.RED);
                    snackbar.show();
                }
            }
        });

        googleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isOnline()) {
                    Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
                    intent.putExtra(SearchManager.QUERY, wordWebUri.toLowerCase()); // query contains search string
                    startActivity(intent);
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivity(intent);
                    }
                }else {
                    Snackbar snackbar = Snackbar.make(view, getString(R.string.no_intenet_connection_msg), Snackbar.LENGTH_LONG);
                    View snackbarView = snackbar.getView();
                    TextView snackBarText = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
                    snackBarText.setTextColor(Color.RED);
                    snackbar.show();
                }
            }
        });
    }
    /*
    Check internet connection
     */

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
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
            int pos = (cursor.getInt(cursor.getColumnIndexOrThrow(LinoteContract.LinoteEntry.COLUMN_NAME_PARTOFSPEECH)));
            int article = (cursor.getInt(cursor.getColumnIndexOrThrow(LinoteContract.LinoteEntry.COLUMN_NAME_ARTICLE)));
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

            String posString = null;
            switch (pos) {
                case 0:
                    posString = getString(R.string.chose_pos);
                    break;
                case 1:
                    posString = getString(R.string.noun);
                    break;
                case 2:
                    posString = getString(R.string.pronoun);
                    break;
                case 3:
                    posString = getString(R.string.verb);
                    break;
                case 4:
                    posString = getString(R.string.adverb);
                    break;
                case 5:
                    posString = getString(R.string.adjective);
                    break;
                case 6:
                    posString = getString(R.string.conjunction);
                    break;
                case 7:
                    posString = getString(R.string.preposition);
                    break;
                case 8:
                    posString = getString(R.string.interjection);
                    break;
            }

            String articleString = null;
            switch (article) {
                case 0:
                    articleString = null;
                    break;
                case 1:
                    articleString = getString(R.string.der);
                    break;
                case 2:
                    articleString = getString(R.string.die);
                    break;
                case 3:
                    articleString = getString(R.string.das);
                    break;
            }

            if (articleString == null) {
                details.setText(posString);
            } else {
                details.setText(posString + ", " + articleString);
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
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }
}
