package com.example.android.linote.Database;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static com.example.android.linote.Database.LinoteContract.*;

/**
 * Created by Pastuh on 05.04.2017.
 */

public class LinoteDbHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = LinoteDbHelper.class.getSimpleName();
    private static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "linote.db";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + LinoteEntry.TABLE_NAME + " ("
            + LinoteEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + LinoteEntry.COLUMN_NAME_LANGUAGE + " INTEGER NOT NULL, "
            + LinoteEntry.COLUMN_NAME_WORD + " TEXT NOT NULL, "
            + LinoteEntry.COLUMN_NAME_TRANSLATION + " TEXT NOT NULL, "
            + LinoteEntry.COLUMN_NAME_PARTOFSPEECH + " INTEGER NOT NULL, "
            + LinoteEntry.COLUMN_NAME_ARTICLE + " INTEGER DEFAULT 0, "
            + LinoteEntry.COLUMN_NAME_DESCRIPTION + " TEXT), "
            + LinoteEntry.COLUMN_NAME_COLLOCATIONS + " TEXT, "
            + LinoteEntry.COLUMN_NAME_EXAMPLES + " TEXT) "
            + ");";

    public LinoteDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(SQL_CREATE_ENTRIES);
        } catch (SQLException e){
            Log.e(LOG_TAG, "Can not create Table",e);
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
