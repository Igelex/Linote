package com.example.android.linote;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;


import static com.example.android.linote.Database.LinoteContract.*;


public class AddNewWord extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private ScrollView scroll;
    private EditText inputWord;
    private EditText inputTranslation;
    private EditText inputDescription;
    private EditText inputCollocations;
    private EditText inputExamples;
    private int lang;
    private String pos;
    private String article;
    private Spinner spinnerChosePos;
    private Spinner mArticleSpinner;
    private Spinner spinnerChoseLang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_new_word_activity);

        scroll = (ScrollView) findViewById(R.id.scroll_view);
        inputWord = (EditText) findViewById(R.id.input_word);
        inputTranslation = (EditText) findViewById(R.id.input_translation);
        inputDescription = (EditText) findViewById(R.id.input_description);
        inputCollocations = (EditText) findViewById(R.id.input_collocations);
        inputExamples = (EditText) findViewById(R.id.input_examples);

        mArticleSpinner = setupArticleSpinner();
        setupPosSpinner();

        spinnerChoseLang = (Spinner) findViewById(R.id.chose_lang_spinner);
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.chose_lang_spinner, android.R.layout.simple_spinner_dropdown_item);
        spinnerChoseLang.setAdapter(spinnerAdapter);

        spinnerChoseLang.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        lang = LinoteEntry.LANGUAGE_NO_LANGUAGE_SELECTED;
                        break;
                    case 1:
                        lang = LinoteEntry.LANGUAGE_ENGLISH;
                        break;
                    case 2:
                        lang = LinoteEntry.LANGUAGE_GERMAN;
                        break;
                }
                setArticleSpinnerVisible();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_new_word_menu, menu);
        return true;
    }

    public int deleteWord(Uri uri) {
        int rowsDeleted = getContentResolver().delete(uri, null, null);
        if (rowsDeleted > 0) {
            Snackbar.make(scroll, "Word deleted", Snackbar.LENGTH_SHORT).show();
        } else {
            Snackbar.make(scroll, "Word not deleted", Snackbar.LENGTH_SHORT).show();
        }
        return rowsDeleted;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                saveWord();
                finish();
                return true;
            case R.id.action_delete:
                //showDeleteConfirmationDialog();
                return true;
            case android.R.id.home:
                /*if (!mPetHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;*/
        }
        return super.onOptionsItemSelected(item);
    }

    /*
    Helper Method to Save Word
    */
    private void saveWord() {

        String word = inputWord.getText().toString().trim();

        ContentValues values = new ContentValues();
        values.put(LinoteEntry.COLUMN_NAME_LANGUAGE, lang);
        values.put(LinoteEntry.COLUMN_NAME_WORD, inputWord.getText().toString().trim());
        values.put(LinoteEntry.COLUMN_NAME_TRANSLATION, inputTranslation.getText().toString().trim());
        values.put(LinoteEntry.COLUMN_NAME_PARTOFSPEECH, pos);
        values.put(LinoteEntry.COLUMN_NAME_ARTICLE, article);
        values.put(LinoteEntry.COLUMN_NAME_DESCRIPTION, inputDescription.getText().toString().trim());
        values.put(LinoteEntry.COLUMN_NAME_COLLOCATIONS, inputCollocations.getText().toString().trim());
        values.put(LinoteEntry.COLUMN_NAME_EXAMPLES, inputExamples.getText().toString().trim());

        Uri uriResult = getContentResolver().insert(LinoteEntry.CONTENT_URI, values);
        if (uriResult != null) {
            Snackbar.make(scroll, "Word was added with ID: " + ContentUris.parseId(uriResult), Snackbar.LENGTH_SHORT).show();
        } else {
            Snackbar.make(scroll, "Word was not added", Snackbar.LENGTH_SHORT).show();

        }

    }

    private void setupPosSpinner() {
        spinnerChosePos = (Spinner) findViewById(R.id.spinner_partofspeech);
        ArrayAdapter<CharSequence> spinnerAdapterPos = ArrayAdapter.createFromResource(this,
                R.array.chose_pos_spinner, android.R.layout.simple_spinner_dropdown_item);
        spinnerChosePos.setAdapter(spinnerAdapterPos);

        spinnerChosePos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);

                if (selection.equals(getString(R.string.page_title_eng))) {
                    Snackbar.make(view, "Item on Position :" + position + " ist Selected: " + selection, Snackbar.LENGTH_SHORT).show();
                }
                switch (position) {
                    case 0:
                        pos = getString(R.string.chose_pos);
                        break;
                    case 1:
                        pos = getString(R.string.noun);
                        break;
                    case 2:
                        pos = getString(R.string.pronoun);
                        break;
                    case 3:
                        pos = getString(R.string.verb);
                        break;
                    case 4:
                        pos = getString(R.string.adverb);
                        break;
                    case 5:
                        pos = getString(R.string.adjective);
                        break;
                    case 6:
                        pos = getString(R.string.conjunction);
                        break;
                    case 7:
                        pos = getString(R.string.preposition);
                        ;
                        break;
                    case 8:
                        pos = getString(R.string.interjection);
                        ;
                        break;
                }
                setArticleSpinnerVisible();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    private Spinner setupArticleSpinner() {
        Spinner spinnerChoseArticle = (Spinner) findViewById(R.id.spinner_article);
        ArrayAdapter<CharSequence> spinnerAdapterArticle = ArrayAdapter.createFromResource(this,
                R.array.chose_article_spinner, android.R.layout.simple_spinner_dropdown_item);
        spinnerChoseArticle.setAdapter(spinnerAdapterArticle);
        spinnerChoseArticle.setVisibility(View.GONE);

        spinnerChoseArticle.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        article = null;
                        break;
                    case 1:
                        article = getString(R.string.der);
                        break;
                    case 2:
                        article = getString(R.string.die);
                        break;
                    case 3:
                        article = getString(R.string.das);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        return spinnerChoseArticle;
    }

    private void setArticleSpinnerVisible() {
        if (spinnerChosePos.getSelectedItemPosition() == LinoteEntry.POS_NOUN
                && spinnerChoseLang.getSelectedItemPosition() == LinoteEntry.LANGUAGE_GERMAN) {
            mArticleSpinner.setVisibility(View.VISIBLE);
        } else {
            mArticleSpinner.setSelection(0);
            article = null;
            mArticleSpinner.setVisibility(View.GONE);

        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
