package com.example.oiltanker.test;


import java.text.SimpleDateFormat;
import java.util.Locale;

public class ToDoDBContract {
    //Database
    public static final String DATABASE_NAME = "ToDoList.db";
    public static final String COMMA_SEP = ", ";

    //Settings table
    public class Settings {
        public static final String TABLE_NAME = "Settings";

        public static final String COLUMN_NAME = "S_name";
        public static final String COLUMN_VALUE = "S_value";

        public static final String COLUMN_NAME_TYPE = "TEXT";
        public static final String COLUMN_VALUE_TYPE = "TEXT";

        public static final String LAST_ID_NAME = "last_id";
        //public static final String THEME_ID_NAME = "theme_id";
    }

    //Data table
    public class ToDoList {
        public static final String TABLE_NAME = "ToDoList";

        public static final String COLUMN_ID =              "TD_id";
        public static final String COLUMN_NAME =            "TD_name";
        public static final String COLUMN_DATE =            "TD_date";
        public static final String COLUMN_CHECK =           "TD_check";
        public static final String COLUMN_CONTENTS =        "TD_contents";
        public static final String COLUMN_PICTURE =         "TD_picture";
        public static final String COLUMN_PIC_SIZE =        "TD_pic_size";

        public static final String COLUMN_ID_TYPE =         "INTEGER";
        public static final String COLUMN_NAME_TYPE =       "TEXT";
        public static final String COLUMN_DATE_TYPE =       "TEXT";
        public static final String COLUMN_CHECK_TYPE =      "INTEGER";
        public static final String COLUMN_CONTENTS_TYPE =   "TEXT";
        public static final String COLUMN_PICTURE_TYPE =    "BLOB";
        public static final String COLUMN_PIC_SIZE_TYPE =   "INTEGER";
    }

    //Date formatter
    public static final SimpleDateFormat iso8601Format = new SimpleDateFormat("yyyy-MM-dd HH:MM:SS", Locale.ENGLISH);
}
