package com.example.android.linote.Database;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Pastuh on 05.04.2017.
 */

public final class LinoteContract {
    private LinoteContract() {}


    /* Inner class that defines the table contents */
    public static class LinoteEntry implements BaseColumns{
        public static final String TABLE_NAME = "words";
        public static final String _ID= BaseColumns._ID;
        public static final String COLUMN_NAME_LANGUAGE = "language";
        public static final String COLUMN_NAME_WORD = "word";
        public static final String COLUMN_NAME_TRANSLATION = "translation";
        public static final String COLUMN_NAME_PARTOFSPEECH = "partofspeech";
        public static final String COLUMN_NAME_ARTICLE = "article";
        public static final String COLUMN_NAME_DESCRIPTION = "description";
        public static final String COLUMN_NAME_COLLOCATIONS= "collocations";
        public static final String COLUMN_NAME_EXAMPLES= "examples";

        /*
        Possible Values for the Language
         */
        public static final int LANGUAGE_NO_LANGUAGE_SELECTED  = 0;
        public static final int LANGUAGE_ENGLISH  = 1;
        public static final int LANGUAGE_GERMAN  = 2;

        /*
        Possible Values for the Part of Speech
         */
        public static final int POS_NO_POS_SELECTED  = 0;
        public static final int POS_NOUN  = 1;
        public static final int POS_PRONOUN  = 2;
        public static final int POS_VERB  = 3;
        public static final int POS_ADVERB  = 4;
        public static final int POS_ADJECTIVE  = 5;
        public static final int POS_CONJUNCTIVE  = 6;
        public static final int POS_PREPOSITION  = 6;
        public static final int POS_INTERJECTION  = 7;

        /*
       Possible Values for the ARTICLE
        */
        public static final int ARTICLE_NO_ARTICLE  = 0;
        public static final int ARTICLE_DER  = 1;
        public static final int ARTICLE_DIE  = 2;
        public static final int ARTICLE_DAS  = 3;

        /**
         * URI
         */
        public static final String CONTENT_URI_AUTHORITY = "com.example.android.linote";
        public static final String PATH_WORDS = "words";
        public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_URI_AUTHORITY);
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_WORDS);

        /**
         *MIME TYPES
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_URI_AUTHORITY + "/" + PATH_WORDS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_URI_AUTHORITY + "/" + PATH_WORDS;

        /**
         * Projections
         */
        public static final String [] PROJECTION = {_ID + COLUMN_NAME_WORD + COLUMN_NAME_TRANSLATION
                                                + COLUMN_NAME_PARTOFSPEECH + COLUMN_NAME_ARTICLE};

        /**
         * Selections
         */
        public static final String SELECTION_GER_WORDS = COLUMN_NAME_LANGUAGE + " =?";
        public static final String [] SELECTION_ARGS_GER_WORDS = {String.valueOf(LANGUAGE_GERMAN)};

        public static final String SELECTION_ENG_WORDS = COLUMN_NAME_LANGUAGE + " =?";
        public static final String [] SELECTION_ARGS_ENG_WORDS = {String.valueOf(LANGUAGE_ENGLISH)};


    }
}
