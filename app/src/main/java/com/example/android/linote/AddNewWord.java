package com.example.android.linote;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;

import com.example.android.linote.Database.LinoteContract;


public class AddNewWord extends AppCompatActivity {

    private Uri mCurrentPetUri;
    private ScrollView scroll;
    private EditText inputWord;
    private EditText inputTranslation;
    private EditText inputDescription;
    private EditText inputCollocations;
    private EditText inputExamples;
    private int lang;
    private int pos;
    private int article;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_new_word_activity);

        scroll = (ScrollView) findViewById(R.id.scroll_view);
        inputWord = (EditText) findViewById(R.id.input_word);
        inputTranslation  = (EditText) findViewById(R.id.input_translation);
        inputDescription  = (EditText) findViewById(R.id.input_description);
        inputCollocations  = (EditText) findViewById(R.id.input_collocations);
        inputExamples  = (EditText) findViewById(R.id.input_examples);

        setupPosSpinner();
        final Spinner mArticleSpinner = setupArticleSpinner();

        Spinner spinnerChoseLang = (Spinner) findViewById(R.id.chose_lang_spinner);
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.chose_lang_spinner, android.R.layout.simple_spinner_dropdown_item);
        spinnerChoseLang.setAdapter(spinnerAdapter);

        spinnerChoseLang.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);

                if(selection.equals(getString(R.string.page_title_eng))){
                    Snackbar.make(view, "Item on Position :" + position + " ist Selected: " + selection, Snackbar.LENGTH_SHORT).show();
                }
                switch (position){
                    case 0:
                        Snackbar.make(view, "Item Selected: " + selection, Snackbar.LENGTH_SHORT).show();
                        break;
                    case 1:
                        lang = LinoteContract.LinoteEntry.LANGUAGE_ENGLISH;
                        Snackbar.make(view, "Item Selected: " + selection, Snackbar.LENGTH_SHORT).show();
                        mArticleSpinner.setVisibility(View.GONE);
                        break;
                    case 2:
                        lang = LinoteContract.LinoteEntry.LANGUAGE_GERMAN;
                        Snackbar.make(view, "Item Selected: " + selection, Snackbar.LENGTH_SHORT).show();
                        mArticleSpinner.setVisibility(View.VISIBLE);
                        break;
                }
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

    public int deleteWord(Uri uri){
        mCurrentPetUri = uri;
        int rowsDeleted = getContentResolver().delete(mCurrentPetUri, null, null);
        if (rowsDeleted > 0) {
            Snackbar.make(scroll, "Word deleted", Snackbar.LENGTH_SHORT).show();
        } else {
            Snackbar.make(scroll, "Word not deleted", Snackbar.LENGTH_SHORT).show();
        }
        return  rowsDeleted;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Do nothing for now
                saveWord();
                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                //showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
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
    private void saveWord (){
        ContentValues content = new ContentValues();
        content.put(LinoteContract.LinoteEntry.COLUMN_NAME_LANGUAGE, lang);
        content.put(LinoteContract.LinoteEntry.COLUMN_NAME_WORD, String.valueOf(inputWord.getText()));
        content.put(LinoteContract.LinoteEntry.COLUMN_NAME_TRANSLATION, String.valueOf(inputTranslation.getText()));
        content.put(LinoteContract.LinoteEntry.COLUMN_NAME_PARTOFSPEECH, pos);
        content.put(LinoteContract.LinoteEntry.COLUMN_NAME_ARTICLE, article);
        content.put(LinoteContract.LinoteEntry.COLUMN_NAME_DESCRIPTION, String.valueOf(inputDescription.getText()));
        content.put(LinoteContract.LinoteEntry.COLUMN_NAME_COLLOCATIONS, String.valueOf(inputCollocations.getText()));
        content.put(LinoteContract.LinoteEntry.COLUMN_NAME_EXAMPLES, String.valueOf(inputExamples.getText()));
        Uri uriResult = getContentResolver().insert(LinoteContract.LinoteEntry.CONTENT_URI, content);
        if(uriResult != null){
            Snackbar.make(scroll, "Word was added", Snackbar.LENGTH_SHORT).show();
        }else{
            Snackbar.make(scroll, "Word was not added", Snackbar.LENGTH_SHORT).show();

        }

    }

    private void setupPosSpinner(){
        Spinner spinnerChosePos = (Spinner) findViewById(R.id.spinner_partofspeech);
        ArrayAdapter<CharSequence> spinnerAdapterPos = ArrayAdapter.createFromResource(this,
                R.array.chose_pos_spinner, android.R.layout.simple_spinner_dropdown_item);
        spinnerChosePos.setAdapter(spinnerAdapterPos);

        spinnerChosePos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);

                if(selection.equals(getString(R.string.page_title_eng))){
                    Snackbar.make(view, "Item on Position :" + position + " ist Selected: " + selection, Snackbar.LENGTH_SHORT).show();
                }
                switch (position){
                    case 0:
                        break;
                    case 1:
                        pos = LinoteContract.LinoteEntry.POS_NOUN;
                        break;
                    case 2:
                        pos = LinoteContract.LinoteEntry.POS_PRONOUN;
                        break;
                    case 3:
                        pos = LinoteContract.LinoteEntry.POS_VERB;
                        break;
                    case 4:
                        pos = LinoteContract.LinoteEntry.POS_ADVERB;
                        break;
                    case 5:
                        pos = LinoteContract.LinoteEntry.POS_ADJECTIVE;
                        break;
                    case 6:
                        pos = LinoteContract.LinoteEntry.POS_CONJUNCTIVE;
                        break;
                    case 7:
                        pos = LinoteContract.LinoteEntry.POS_PREPOSITION;
                        break;
                    case 8:
                        pos = LinoteContract.LinoteEntry.POS_INTERJECTION;
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    private Spinner setupArticleSpinner(){
        final Spinner spinnerChoseArticle = (Spinner) findViewById(R.id.spinner_article);
        ArrayAdapter<CharSequence> spinnerAdapterArticle = ArrayAdapter.createFromResource(this,
                R.array.chose_article_spinner, android.R.layout.simple_spinner_dropdown_item);
        spinnerChoseArticle.setAdapter(spinnerAdapterArticle);
        spinnerChoseArticle.setVisibility(View.GONE);

        spinnerChoseArticle.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);

                if(selection.equals(getString(R.string.page_title_eng))){
                    Snackbar.make(view, "Item on Position :" + position + " ist Selected: " + selection, Snackbar.LENGTH_SHORT).show();
                }
                switch (position){
                    case 0:
                        break;
                    case 1:
                        article = LinoteContract.LinoteEntry.ARTICLE_DER;
                        break;
                    case 2:
                        pos = LinoteContract.LinoteEntry.ARTICLE_DIE;
                        break;
                    case 3:
                        pos = LinoteContract.LinoteEntry.ARTICLE_DAS;
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        return spinnerChoseArticle;
    }
}
