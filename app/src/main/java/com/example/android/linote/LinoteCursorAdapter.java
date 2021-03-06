package com.example.android.linote;

import android.app.SearchManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.android.linote.Database.LinoteContract;

/**
 * Created by Pastuh on 05.04.2017.
 */

public class LinoteCursorAdapter extends CursorAdapter {

    private Context mContext;
    private PopupMenu popup;
    private Animation anim;
    //private View mCurrentview;

    public LinoteCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
        this.mContext = context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.item_card, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {

        //mCurrentview = view;
        anim = AnimationUtils.loadAnimation(mContext, R.anim.myanim);

        TextView defaultWord = (TextView) view.findViewById(R.id.default_word);
        TextView translation = (TextView) view.findViewById(R.id.translation);
        TextView details = (TextView) view.findViewById(R.id.details);

        ImageButton deleteButton = (ImageButton) view.findViewById(R.id.image_button_delete);
        ImageButton editButton = (ImageButton) view.findViewById(R.id.image_button_edit);
        final ImageButton menuButton = (ImageButton) view.findViewById(R.id.image_button_menu);

        int language = (cursor.getInt(cursor.getColumnIndex(LinoteContract.LinoteEntry.COLUMN_NAME_LANGUAGE)));
        defaultWord.setText(cursor.getString(cursor.getColumnIndexOrThrow(LinoteContract.LinoteEntry.COLUMN_NAME_WORD)));
        translation.setText(cursor.getString(cursor.getColumnIndexOrThrow(LinoteContract.LinoteEntry.COLUMN_NAME_TRANSLATION)));
        int pos = (cursor.getInt(cursor.getColumnIndexOrThrow(LinoteContract.LinoteEntry.COLUMN_NAME_PARTOFSPEECH)));
        int article = (cursor.getInt(cursor.getColumnIndexOrThrow(LinoteContract.LinoteEntry.COLUMN_NAME_ARTICLE)));
        final Uri mCurrentUri = ContentUris.withAppendedId(LinoteContract.LinoteEntry.CONTENT_URI,
                cursor.getInt(cursor.getColumnIndex(LinoteContract.LinoteEntry._ID)));

        final String intentString = defaultWord.getText().toString();
        final String wordWebUri = defaultWord.getText().toString();

        TextView languageView = (TextView) view.findViewById(R.id.lang);
        String translationDirection = null;
        switch (language) {
            case LinoteContract.LinoteEntry.LANGUAGE_ENGLISH:
                translationDirection = "en-ru";
                languageView.setText(R.string.eng);
                break;
            case LinoteContract.LinoteEntry.LANGUAGE_GERMAN:
                translationDirection = "de-ru";
                languageView.setText(R.string.ger);
                break;
        }

        String articleString = null;
        switch (article) {
            case 0:
                articleString = null;
                break;
            case 1:
                articleString = mContext.getString(R.string.der);
                break;
            case 2:
                articleString = mContext.getString(R.string.die);
                break;
            case 3:
                articleString = mContext.getString(R.string.das);
                break;
        }

        String posString = null;
        switch (pos) {
            case 0:
                posString = mContext.getString(R.string.chose_pos);
                break;
            case 1:
                posString = mContext.getString(R.string.noun);
                break;
            case 2:
                posString = mContext.getString(R.string.pronoun);
                break;
            case 3:
                posString = mContext.getString(R.string.verb);
                break;
            case 4:
                posString = mContext.getString(R.string.adverb);
                break;
            case 5:
                posString = mContext.getString(R.string.adjective);
                break;
            case 6:
                posString = mContext.getString(R.string.conjunction);
                break;
            case 7:
                posString = mContext.getString(R.string.preposition);
                break;
            case 8:
                posString = mContext.getString(R.string.interjection);
                break;
        }

        final String tr = translationDirection;
        if (articleString == null) {
            details.setText(posString);
        } else {
            details.setText(posString + ", " + articleString);
        }

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDeleteConfirmationDialog(mCurrentUri);
            }
        });

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentEdit = new Intent(mContext, AddNewWord.class);
                intentEdit.setData(mCurrentUri);
                if (intentEdit.resolveActivity(mContext.getPackageManager()) != null){
                    mContext.startActivity(intentEdit);
                }
            }
        });

        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popup = new PopupMenu(context, menuButton);
                popup.inflate(R.menu.card_bar_menu);
                setCustomPopupListener(wordWebUri, tr, intentString, view);
                popup.show();
            }
        });

        view.startAnimation(anim);
    }

    private void showDeleteConfirmationDialog(final Uri uri) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                /*anim = AnimationUtils.loadAnimation(mContext, R.anim.delete_anim);
                mCurrentview.startAnimation(anim);*/
                mContext.getContentResolver().delete(uri, null, null);
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

    private void setCustomPopupListener(final String wordWebUri, final String translationDirection, final String intentString, final View view) {
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            Intent intent;
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.menu_item_abby:
                        if (isOnline()) {
                            Uri abbyUri = Uri.parse("https://www.lingvolive.com/en-us/translate/" + translationDirection
                                    + "/" + wordWebUri);
                            intent = new Intent(mContext, WebActivity.class);
                            intent.setData(abbyUri);
                            intent.putExtra("title", intentString);
                            if (intent.resolveActivity(mContext.getPackageManager()) != null) {
                                mContext.startActivity(intent);
                            }
                        }else {
                            Snackbar snackbar = Snackbar.make(view, mContext.getString(R.string.no_intenet_connection_msg), Snackbar.LENGTH_LONG);
                            View snackbarView = snackbar.getView();
                            TextView snackBarText = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
                            snackBarText.setTextColor(Color.RED);
                            snackbar.show();
                        }
                        break;
                    case R.id.menu_item_google:
                        if(isOnline()) {
                            Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
                            intent.putExtra(SearchManager.QUERY, wordWebUri);
                            mContext.startActivity(intent);
                            if (intent.resolveActivity(mContext.getPackageManager()) != null) {
                                mContext.startActivity(intent);
                            }
                        }else {
                            Snackbar snackbar = Snackbar.make(view, mContext.getString(R.string.no_intenet_connection_msg), Snackbar.LENGTH_LONG);
                            View snackbarView = snackbar.getView();
                            TextView snackBarText = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
                            snackBarText.setTextColor(Color.RED);
                            snackbar.show();
                        }
                        break;


                }
                return true;
            }

        });

    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

}
