package com.example.android.linote;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;


import com.example.android.linote.Database.LinoteContract;

import static com.example.android.linote.Database.LinoteContract.*;


public class AddNewWord extends AppCompatActivity implements android.app.LoaderManager.LoaderCallbacks<Cursor> {

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
    private int INSERT_MODE = 0;
    private Uri mCurrentUri;

    private boolean mWordchanges = false;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mWordchanges = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_new_word_activity);

        Intent intent = getIntent();
        mCurrentUri = intent.getData();

        if (mCurrentUri != null) {
            setTitle(R.string.edit_word_titel);
            INSERT_MODE = 1;
            getLoaderManager().initLoader(0, null, this);
        }


        scroll = (ScrollView) findViewById(R.id.scroll_view);
        inputWord = (EditText) findViewById(R.id.input_word);
        inputTranslation = (EditText) findViewById(R.id.input_translation);
        inputDescription = (EditText) findViewById(R.id.input_description);
        inputCollocations = (EditText) findViewById(R.id.input_collocations);
        inputExamples = (EditText) findViewById(R.id.input_examples);

        inputWord.setOnTouchListener(mTouchListener);
        inputTranslation.setOnTouchListener(mTouchListener);
        inputExamples.setOnTouchListener(mTouchListener);
        inputCollocations.setOnTouchListener(mTouchListener);
        inputDescription.setOnTouchListener(mTouchListener);

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

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (mCurrentUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                saveWord();
                return true;
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            case android.R.id.home:
                if (!mWordchanges) {
                    NavUtils.navigateUpFromSameTask(AddNewWord.this);
                    return true;
                }
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                NavUtils.navigateUpFromSameTask(AddNewWord.this);
                            }
                        };
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /*
    Helper Method to delete Word
    */
    public int deleteWord(Uri uri) {
        int rowsDeleted = getContentResolver().delete(uri, null, null);
        if (rowsDeleted > 0) {
            finish();
        }
        return rowsDeleted;
    }

    /*
    Helper Method to Save Word
    */
    private void saveWord() {

        ContentValues values = new ContentValues();
        values.put(LinoteEntry.COLUMN_NAME_LANGUAGE, lang);
        values.put(LinoteEntry.COLUMN_NAME_WORD, inputWord.getText().toString().trim());
        values.put(LinoteEntry.COLUMN_NAME_TRANSLATION, inputTranslation.getText().toString().trim());
        values.put(LinoteEntry.COLUMN_NAME_PARTOFSPEECH, pos);
        values.put(LinoteEntry.COLUMN_NAME_ARTICLE, article);
        values.put(LinoteEntry.COLUMN_NAME_DESCRIPTION, inputDescription.getText().toString().trim());
        values.put(LinoteEntry.COLUMN_NAME_COLLOCATIONS, inputCollocations.getText().toString().trim());
        values.put(LinoteEntry.COLUMN_NAME_EXAMPLES, inputExamples.getText().toString().trim());

        switch (INSERT_MODE) {
            case 0:
                Uri uriResult = getContentResolver().insert(LinoteEntry.CONTENT_URI, values);
                if (uriResult != null) {
                    Toast.makeText(this, getString(R.string.notation_added_msg) + ContentUris.parseId(uriResult), Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Snackbar.make(scroll, getString(R.string.notation_add_faild_msg), Snackbar.LENGTH_LONG).show();

                }
                break;
            case 1:
                int rowsUpdate = getContentResolver().update(mCurrentUri, values, null, null);
                if (rowsUpdate > 0 ){
                    Toast.makeText(this, getString(R.string.notation_updated_msg), Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Snackbar.make(scroll, getString(R.string.notation_uptdate_faild), Snackbar.LENGTH_SHORT).show();

                }

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

    private boolean setArticleSpinnerVisible() {
        if (spinnerChosePos.getSelectedItemPosition() == LinoteEntry.POS_NOUN
                && spinnerChoseLang.getSelectedItemPosition() == LinoteEntry.LANGUAGE_GERMAN) {
            mArticleSpinner.setVisibility(View.VISIBLE);
            return true;
        } else {
            mArticleSpinner.setSelection(0);
            article = null;
            mArticleSpinner.setVisibility(View.GONE);
            return false;
        }
    }

    /*
    Helpe Method bevor Delete
     */
    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteWord(mCurrentUri);
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /*
    Helpe Method discarad Changes
     */
    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
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
            spinnerChoseLang.setSelection(cursor.getInt(cursor.getColumnIndex(LinoteContract.LinoteEntry.COLUMN_NAME_LANGUAGE)));
            inputWord.setText(cursor.getString(cursor.getColumnIndexOrThrow(LinoteContract.LinoteEntry.COLUMN_NAME_WORD)));
            inputTranslation.setText(cursor.getString(cursor.getColumnIndexOrThrow(LinoteContract.LinoteEntry.COLUMN_NAME_TRANSLATION)));
            pos = (cursor.getString(cursor.getColumnIndexOrThrow(LinoteContract.LinoteEntry.COLUMN_NAME_PARTOFSPEECH)));
            article = (cursor.getString(cursor.getColumnIndexOrThrow(LinoteContract.LinoteEntry.COLUMN_NAME_ARTICLE)));
            inputDescription.setText(cursor.getString(cursor.getColumnIndexOrThrow(LinoteContract.LinoteEntry.COLUMN_NAME_DESCRIPTION)));
            inputExamples.setText(cursor.getString(cursor.getColumnIndexOrThrow(LinoteContract.LinoteEntry.COLUMN_NAME_EXAMPLES)));
            inputCollocations.setText(cursor.getString(cursor.getColumnIndexOrThrow(LinoteContract.LinoteEntry.COLUMN_NAME_COLLOCATIONS)));
        }

        if (pos.equals(getString(R.string.noun))) {
            spinnerChosePos.setSelection(1);
        } else if (pos.equals(getString(R.string.pronoun))) {
            spinnerChosePos.setSelection(2);
        } else if (pos.equals(getString(R.string.verb))) {
            spinnerChosePos.setSelection(3);
        } else if (pos.equals(getString(R.string.adverb))) {
            spinnerChosePos.setSelection(4);
        } else if (pos.equals(getString(R.string.adjective))) {
            spinnerChosePos.setSelection(5);
        } else if (pos.equals(getString(R.string.conjunction))) {
            spinnerChosePos.setSelection(6);
        } else if (pos.equals(getString(R.string.preposition))) {
            spinnerChosePos.setSelection(7);
        } else if (pos.equals(getString(R.string.interjection))) {
            spinnerChosePos.setSelection(8);
        } else {
            spinnerChosePos.setSelection(0);
        }

        if (setArticleSpinnerVisible()) {
            if (article.equals(getString(R.string.der))) {
                mArticleSpinner.setSelection(1);
            } else if (article.equals(getString(R.string.die))) {
                mArticleSpinner.setSelection(2);
            } else if (article.equals(getString(R.string.das))) {
                mArticleSpinner.setSelection(3);
            }
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
