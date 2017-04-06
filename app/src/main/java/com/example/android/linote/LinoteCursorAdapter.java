package com.example.android.linote;

import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v4.provider.DocumentFile;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.linote.Database.LinoteContract;
import com.example.android.linote.R;

import static java.security.AccessController.getContext;

/**
 * Created by Pastuh on 05.04.2017.
 */

public class LinoteCursorAdapter extends CursorAdapter {

    private Context mContext;

    public LinoteCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
        this.mContext = context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.item_card, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {

        TextView defaultWord = (TextView) view.findViewById(R.id.default_word);
        TextView translation = (TextView) view.findViewById(R.id.translation);
        TextView details = (TextView) view.findViewById(R.id.details);

        ImageButton deleteButton = (ImageButton) view.findViewById(R.id.image_button_delete);
        ImageButton editButton = (ImageButton) view.findViewById(R.id.image_button_edit);
        final ImageButton menuButton = (ImageButton) view.findViewById(R.id.image_button_menu);

        defaultWord.setText(cursor.getString(cursor.getColumnIndexOrThrow(LinoteContract.LinoteEntry.COLUMN_NAME_WORD)));
        translation.setText(cursor.getString(cursor.getColumnIndexOrThrow(LinoteContract.LinoteEntry.COLUMN_NAME_TRANSLATION)));
        String pos = (cursor.getString(cursor.getColumnIndexOrThrow(LinoteContract.LinoteEntry.COLUMN_NAME_PARTOFSPEECH)));
        String article = (cursor.getString(cursor.getColumnIndexOrThrow(LinoteContract.LinoteEntry.COLUMN_NAME_ARTICLE)));
        final Uri mCurrentUri = ContentUris.withAppendedId(LinoteContract.LinoteEntry.CONTENT_URI,
                cursor.getInt(cursor.getColumnIndex(LinoteContract.LinoteEntry._ID)));

        if (article == null) {
            details.setText(pos);
        } else {
            details.setText(pos + ", " + article);
        }

        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(context, menuButton);
                popup.inflate(R.menu.card_bar_menu);
                Snackbar.make(view, "Menu clicked: " + mCurrentUri, Snackbar.LENGTH_SHORT).show();
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDeleteConfirmationDialog(mCurrentUri);
            }
        });

    }

    private void showDeleteConfirmationDialog(final Uri uri) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                AddNewWord n = new AddNewWord();
                n.deleteWord(uri);
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
}
