package com.example.android.linote;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.android.linote.Database.LinoteContract;
import com.example.android.linote.R;

/**
 * Created by Pastuh on 05.04.2017.
 */

public class LinoteCursorAdapter extends CursorAdapter {
    public LinoteCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.item_card, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView defaultWord = (TextView) view.findViewById(R.id.default_word);
        TextView translation = (TextView) view.findViewById(R.id.translation);
        TextView details = (TextView) view.findViewById(R.id.details);

        ImageButton deleteButton = (ImageButton) view.findViewById(R.id.image_button_delete);
        ImageButton editButton = (ImageButton) view.findViewById(R.id.image_button_edit);
        ImageButton menuButton = (ImageButton) view.findViewById(R.id.image_button_menu);

        defaultWord.setText(cursor.getString(cursor.getColumnIndexOrThrow(LinoteContract.LinoteEntry.COLUMN_NAME_WORD)));
        translation.setText(cursor.getString(cursor.getColumnIndexOrThrow(LinoteContract.LinoteEntry.COLUMN_NAME_TRANSLATION)));
        String pos =(cursor.getString(cursor.getColumnIndexOrThrow(LinoteContract.LinoteEntry.COLUMN_NAME_TRANSLATION)));
        String article =(cursor.getString(cursor.getColumnIndexOrThrow(LinoteContract.LinoteEntry.COLUMN_NAME_ARTICLE)));

        if(TextUtils.isEmpty(article)){
            details.setText(pos);
        }else{
            details.setText(pos + "," + article);
        }

    }
}
