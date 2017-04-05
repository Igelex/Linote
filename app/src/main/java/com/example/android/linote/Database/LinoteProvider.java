package com.example.android.linote.Database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import static android.R.attr.id;
import static com.example.android.linote.Database.LinoteContract.*;

/**
 * Created by Pastuh on 05.04.2017.
 */

public class LinoteProvider extends ContentProvider {

    public static final String LOG_TAG = LinoteProvider.class.getSimpleName();
    private LinoteDbHelper mDbHelper;
    SQLiteDatabase database;
    Cursor cursor;
    private static final int WORDS = 100;
    private static final int WORD_ID = 101;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(LinoteEntry.CONTENT_URI_AUTHORITY, LinoteEntry.PATH_WORDS, WORDS);
        sUriMatcher.addURI(LinoteEntry.CONTENT_URI_AUTHORITY, LinoteEntry.PATH_WORDS + "/#", WORD_ID);
    }


    @Override
    public boolean onCreate() {
        mDbHelper = new LinoteDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        database = mDbHelper.getReadableDatabase();

        int match = sUriMatcher.match(uri);

        switch (match) {
            case WORDS:
                cursor = database.query(LinoteEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case WORD_ID:
                selection = LinoteEntry._ID + " =?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(LinoteEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null);
                break;
            default:
                throw new IllegalStateException("Unknown Uri " + uri + " with match " + match);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case WORDS:
                return LinoteEntry.CONTENT_LIST_TYPE;
            case WORD_ID:
                return LinoteEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        int match = sUriMatcher.match(uri);

        switch (match) {
            case WORDS:
                return insertWord(uri, contentValues);
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);

        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        database = mDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);

        switch (match) {
            case WORD_ID:
                selection = LinoteEntry._ID + " =?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                int rowsDeleted = database.delete(LinoteEntry.TABLE_NAME, selection, selectionArgs);
                getContext().getContentResolver().notifyChange(uri, null);
                return rowsDeleted;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case WORDS:
                return updateWord(uri, values, selection, selectionArgs);
            case WORD_ID:
                selection = LinoteEntry._ID + " =?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateWord(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    /*
    Helper Method Insert
    */
    private Uri insertWord(Uri uri, ContentValues values) {
        database = mDbHelper.getWritableDatabase();
        long id;
        try {
            id = database.insert(LinoteEntry.TABLE_NAME, null, values);
            if (id == -1) {
                return null;
            }
        } catch (SQLException e){
            Log.e(LOG_TAG, "Failed to insert row for " + uri, e);
            return null;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }

    /*
    Helper Method UPDATE
     */
    private int updateWord(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        database = mDbHelper.getWritableDatabase();
        int id = database.update(LinoteEntry.TABLE_NAME, values, selection, selectionArgs);

        if (id == 0) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return id;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return ((int) ContentUris.parseId(uri));
    }
}
