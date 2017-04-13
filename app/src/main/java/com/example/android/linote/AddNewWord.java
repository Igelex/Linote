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
import android.support.design.widget.TextInputLayout;
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

import static android.text.InputType.TYPE_CLASS_TEXT;
import static android.text.InputType.TYPE_TEXT_FLAG_CAP_WORDS;
import static com.example.android.linote.Database.LinoteContract.*;
import static com.example.android.linote.R.string.spinnerpos_input_error_msg;


public class AddNewWord extends AppCompatActivity implements android.app.LoaderManager.LoaderCallbacks<Cursor> {

    private ScrollView scroll;
    private EditText inputWord;
    private EditText inputTranslation;
    private EditText inputDescription;
    private EditText inputCollocations;
    private EditText inputExamples;
    private int lang;
    private int pos;
    private int article;
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
                if(checkUserInput() && validateUserInputWord() && validateUserInputTranslation()){
                    saveWord();
                }
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
                    Snackbar.make(scroll, getString(R.string.notation_add_failed_msg), Snackbar.LENGTH_LONG).show();

                }
                break;
            case 1:
                int rowsUpdate = getContentResolver().update(mCurrentUri, values, null, null);
                if (rowsUpdate > 0) {
                    Toast.makeText(this, getString(R.string.notation_updated_msg), Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Snackbar.make(scroll, getString(R.string.notation_update_failed), Snackbar.LENGTH_SHORT).show();

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
                switch (position) {
                    case 0:
                        pos = LinoteEntry.POS_NO_POS_SELECTED;
                        break;
                    case 1:
                        pos = LinoteEntry.POS_NOUN;
                        break;
                    case 2:
                        pos = LinoteEntry.POS_PRONOUN;
                        break;
                    case 3:
                        pos = LinoteEntry.POS_VERB;
                        break;
                    case 4:
                        pos = LinoteEntry.POS_ADVERB;
                        break;
                    case 5:
                        pos = LinoteEntry.POS_ADJECTIVE;
                        break;
                    case 6:
                        pos = LinoteEntry.POS_CONJUNCTIVE;
                        break;
                    case 7:
                        pos = LinoteEntry.POS_PREPOSITION;
                        ;
                        break;
                    case 8:
                        pos = LinoteEntry.POS_INTERJECTION;
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
                        article = LinoteEntry.ARTICLE_NO_ARTICLE_SELECTED;
                        break;
                    case 1:
                        article = LinoteEntry.ARTICLE_DER;
                        break;
                    case 2:
                        article = LinoteEntry.ARTICLE_DIE;
                        break;
                    case 3:
                        article = LinoteEntry.ARTICLE_DAS;
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        return spinnerChoseArticle;
    }

    /*
    Set articleSpinner visible and convert first letter from @inputWord to Uppercase
     */
    private boolean setArticleSpinnerVisible() {
        if (spinnerChosePos.getSelectedItemPosition() == LinoteEntry.POS_NOUN
                && spinnerChoseLang.getSelectedItemPosition() == LinoteEntry.LANGUAGE_GERMAN) {
            mArticleSpinner.setVisibility(View.VISIBLE);
            inputWord.setInputType(TYPE_TEXT_FLAG_CAP_WORDS);
            if (!inputWord.getText().toString().trim().isEmpty()) {
                String bufferInputString = inputWord.getText().toString().trim();
                String upperString = bufferInputString.substring(0, 1).toUpperCase() + bufferInputString.substring(1);
                inputWord.setText(upperString);
            }
            return true;
        } else {
            mArticleSpinner.setSelection(0);
            article = 0;
            mArticleSpinner.setVisibility(View.GONE);
            if (!inputWord.getText().toString().trim().isEmpty()) {
                inputWord.setInputType(TYPE_CLASS_TEXT);
                String bufferInputString = inputWord.getText().toString().trim();
                inputWord.setText(bufferInputString.toLowerCase());
            }

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

    /*
    Validate User Input for Word and Translation
     */
    private boolean validateUserInputWord() {
        TextInputLayout userInputWord = (TextInputLayout) findViewById(R.id.inputlayout_word);
        if (inputWord.getText().toString().trim().isEmpty()) {
            userInputWord = (TextInputLayout) findViewById(R.id.inputlayout_word);
            userInputWord.setErrorEnabled(true);
            userInputWord.setError(getString(R.string.user_input_word_error_msg));
            inputWord.setError(getString(R.string.required_input_error_msg));
            inputWord.requestFocus();
            return false;
        }
        userInputWord.setErrorEnabled(false);
        return true;
    }

    private boolean validateUserInputTranslation() {
        TextInputLayout userInputTranslation = (TextInputLayout) findViewById(R.id.inputlayout_word);
        if (inputTranslation.getText().toString().trim().isEmpty()) {
            userInputTranslation = (TextInputLayout) findViewById(R.id.inputlayout_translation);
            userInputTranslation.setErrorEnabled(true);
            userInputTranslation.setError(getString(R.string.user_input_translation_error_msg));
            inputTranslation.setError(getString(R.string.required_input_error_msg));
            inputTranslation.requestFocus();
            return false;
        }
        userInputTranslation.setErrorEnabled(false);
        return true;
    }

    private boolean checkUserInput() {
        if (spinnerChoseLang.getSelectedItemPosition() == LinoteEntry.LANGUAGE_NO_LANGUAGE_SELECTED) {
            Snackbar.make(scroll, getString(R.string.spinnerlang_input_error_msg), Toast.LENGTH_SHORT).show();
            return false;
        } else if (spinnerChosePos.getSelectedItemPosition() == 0) {
            Snackbar.make(scroll, getString(spinnerpos_input_error_msg), Toast.LENGTH_SHORT).show();
            return false;
        } else if (setArticleSpinnerVisible() && mArticleSpinner.getSelectedItemPosition() == 0) {
            Snackbar.make(scroll, getString(R.string.spinnerarticle_input_error_msg), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
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
            pos = (cursor.getInt(cursor.getColumnIndexOrThrow(LinoteContract.LinoteEntry.COLUMN_NAME_PARTOFSPEECH)));
            article = (cursor.getInt(cursor.getColumnIndexOrThrow(LinoteContract.LinoteEntry.COLUMN_NAME_ARTICLE)));
            inputDescription.setText(cursor.getString(cursor.getColumnIndexOrThrow(LinoteContract.LinoteEntry.COLUMN_NAME_DESCRIPTION)));
            inputExamples.setText(cursor.getString(cursor.getColumnIndexOrThrow(LinoteContract.LinoteEntry.COLUMN_NAME_EXAMPLES)));
            inputCollocations.setText(cursor.getString(cursor.getColumnIndexOrThrow(LinoteContract.LinoteEntry.COLUMN_NAME_COLLOCATIONS)));
        }

        switch (pos) {
            case 0:
                spinnerChosePos.setSelection(0);
                break;
            case 1:
                spinnerChosePos.setSelection(1);
                break;
            case 2:
                spinnerChosePos.setSelection(2);
                break;
            case 3:
                spinnerChosePos.setSelection(3);
                break;
            case 4:
                spinnerChosePos.setSelection(4);
                break;
            case 5:
                spinnerChosePos.setSelection(5);
                break;
            case 6:
                spinnerChosePos.setSelection(6);
                break;
            case 7:
                spinnerChosePos.setSelection(7);
                break;
            case 8:
                spinnerChosePos.setSelection(8);
                break;
        }

        switch (article) {
            case 0:
                mArticleSpinner.setSelection(0);
                break;
            case 1:
                mArticleSpinner.setSelection(1);
                break;
            case 2:
                mArticleSpinner.setSelection(2);
                break;
            case 3:
                mArticleSpinner.setSelection(3);
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
