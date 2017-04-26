package com.example.oiltanker.test;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.sql.Blob;
import java.sql.Struct;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

public class ToDoOpenHelper extends SQLiteOpenHelper {

    private class Bs {
        private byte[] bytes = null;
        private int length = 0;

        public Bs (byte[] bytes, int length) {
            this.bytes = bytes;
            this.length = length;
        }
        /*public Bs (byte[] bytes) {
            ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
            this.length = stream.read();
            stream.read(this.bytes, 0, this.length);
        }*/

        public byte[] getBytes() {
            return bytes;
        }

        public int getLength() {
            return length;
        }

        /*public byte[] toByteArray() {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            stream.write(length);
            if (bytes != null)
                stream.write(bytes, 0, length);
            return  stream.toByteArray();
        }*/
    }
    private Bs getBytesFromBitmap(Bitmap bitmap) {
        if (bitmap == null) {
            Log.d("LOL", "no shit");
            return new Bs(null, 0);
        }
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return new Bs(stream.toByteArray(), stream.size());
    }
    private Bitmap getBitmapFromBytes(byte[] bytes, int size) {
        if (size == 0) return null;
        return BitmapFactory.decodeByteArray(bytes, 0, size);
    }

    public ToDoOpenHelper(Context context) {
        super(context, ToDoDBContract.DATABASE_NAME, null, 16);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
            "CREATE TABLE " + ToDoDBContract.ToDoList.TABLE_NAME + " ( " +
                ToDoDBContract.ToDoList.COLUMN_ID + " " + ToDoDBContract.ToDoList.COLUMN_ID_TYPE + " PRIMARY KEY NOT NULL" + ToDoDBContract.COMMA_SEP +
                ToDoDBContract.ToDoList.COLUMN_NAME + " " + ToDoDBContract.ToDoList.COLUMN_NAME_TYPE + ToDoDBContract.COMMA_SEP +
                ToDoDBContract.ToDoList.COLUMN_DATE + " " + ToDoDBContract.ToDoList.COLUMN_DATE_TYPE + ToDoDBContract.COMMA_SEP +
                ToDoDBContract.ToDoList.COLUMN_CHECK + " " + ToDoDBContract.ToDoList.COLUMN_CHECK_TYPE + ToDoDBContract.COMMA_SEP +
                ToDoDBContract.ToDoList.COLUMN_CONTENTS + " " + ToDoDBContract.ToDoList.COLUMN_CONTENTS_TYPE + ToDoDBContract.COMMA_SEP +
                ToDoDBContract.ToDoList.COLUMN_PICTURE + " " + ToDoDBContract.ToDoList.COLUMN_PICTURE_TYPE + ToDoDBContract.COMMA_SEP +
                ToDoDBContract.ToDoList.COLUMN_PIC_SIZE + " " + ToDoDBContract.ToDoList.COLUMN_PIC_SIZE_TYPE +
            " )"
        );
        db.execSQL(
            "CREATE TABLE " + ToDoDBContract.Settings.TABLE_NAME + " ( " +
                ToDoDBContract.Settings.COLUMN_NAME + " " + ToDoDBContract.Settings.COLUMN_NAME_TYPE + " PRIMARY KEY NOT NULL" + ToDoDBContract.COMMA_SEP +
                ToDoDBContract.Settings.COLUMN_VALUE + " " + ToDoDBContract.Settings.COLUMN_VALUE_TYPE +
            " )"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + ToDoDBContract.ToDoList.TABLE_NAME);
        db.execSQL("drop table if exists " + ToDoDBContract.Settings.TABLE_NAME);
        onCreate(db);
    }

    public boolean insertItem(ToDoItem item) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(ToDoDBContract.ToDoList.COLUMN_ID, item.id);
        contentValues.put(ToDoDBContract.ToDoList.COLUMN_NAME, item.name);
        contentValues.put(ToDoDBContract.ToDoList.COLUMN_DATE, ToDoDBContract.iso8601Format.format(item.date));
        contentValues.put(ToDoDBContract.ToDoList.COLUMN_CHECK, item.check);
        contentValues.put(ToDoDBContract.ToDoList.COLUMN_CONTENTS, item.contents);

        Bs bs;
        if (item.picture != null)
            bs = getBytesFromBitmap(item.picture);
        else
            bs = new Bs(null, 0);
        contentValues.put(ToDoDBContract.ToDoList.COLUMN_PICTURE, bs.getBytes());
        contentValues.put(ToDoDBContract.ToDoList.COLUMN_PIC_SIZE, bs.getLength());

        db.insert(ToDoDBContract.ToDoList.TABLE_NAME, null, contentValues);
        return true;
    }

    /*public ToDoItem get(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from " + ToDoDBContract.ToDoList.TABLE_NAME + " where " + ToDoDBContract.ToDoList.COLUMN_ID + "=" + id, null);
        res.moveToFirst();

        try {
            byte[] bytes = res.getBlob(res.getColumnIndex(ToDoDBContract.ToDoList.COLUMN_PICTURE));
            int size = res.getInt(res.getColumnIndex(ToDoDBContract.ToDoList.COLUMN_PIC_SIZE));
            Bitmap bitmap;
            if (size == 0)
                bitmap = null;
            else
                bitmap = getBitmapFromBytes(bytes, size);

            return  new ToDoItem(
                res.getInt(res.getColumnIndex(ToDoDBContract.ToDoList.COLUMN_ID)),
                res.getString(res.getColumnIndex(ToDoDBContract.ToDoList.COLUMN_NAME)),
                ToDoDBContract.iso8601Format.parse(res.getString(res.getColumnIndex(ToDoDBContract.ToDoList.COLUMN_DATE))),
                (res.getInt(res.getColumnIndex(ToDoDBContract.ToDoList.COLUMN_CHECK)) == 1),
                res.getString(res.getColumnIndex(ToDoDBContract.ToDoList.COLUMN_CONTENTS)),
                bitmap
            );
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }*/

    public int numberOfRows() {
        SQLiteDatabase db = this.getReadableDatabase();
        return (int) DatabaseUtils.queryNumEntries(db, ToDoDBContract.ToDoList.COLUMN_ID);
    }

    /*public boolean updateItem(ToDoItem item) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(ToDoDBContract.ToDoList.COLUMN_NAME, item.name);
        contentValues.put(ToDoDBContract.ToDoList.COLUMN_DATE, ToDoDBContract.iso8601Format.format(item.date));
        contentValues.put(ToDoDBContract.ToDoList.COLUMN_CHECK, item.check);
        contentValues.put(ToDoDBContract.ToDoList.COLUMN_CONTENTS, item.contents);

        Bs bs;
        if (item.picture != null)
            bs = getBytesFromBitmap(item.picture);
        else
            bs = new Bs(null, 0);
        contentValues.put(ToDoDBContract.ToDoList.COLUMN_PICTURE, bs.getBytes());
        contentValues.put(ToDoDBContract.ToDoList.COLUMN_PIC_SIZE, bs.getLength());

        db.update(ToDoDBContract.ToDoList.TABLE_NAME, contentValues, ToDoDBContract.ToDoList.COLUMN_ID + " = ? ", new String[]{Integer.toString(item.id)});
        return true;
    }*/
    /*public boolean updateItem_check(ToDoItem item) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(ToDoDBContract.ToDoList.COLUMN_CHECK, item.check);

        db.update(ToDoDBContract.ToDoList.TABLE_NAME, contentValues, ToDoDBContract.ToDoList.COLUMN_ID + " = ? ", new String[]{Integer.toString(item.id)});
        return true;
    }*/

    public Integer deleteItem(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(ToDoDBContract.ToDoList.TABLE_NAME,
                ToDoDBContract.ToDoList.COLUMN_ID + " = ? ",
                new String[]{Integer.toString(id)});
    }

    /*public ArrayList<ToDoItem> getAll() {
        ArrayList<ToDoItem> arrayList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from " + ToDoDBContract.ToDoList.TABLE_NAME, null);
        res.moveToFirst();

        while (!res.isAfterLast()) {
            try {
                int size = res.getInt(res.getColumnIndex(ToDoDBContract.ToDoList.COLUMN_PIC_SIZE));
                byte[] bytes;
                if (size != 0)
                    bytes = res.getBlob(res.getColumnIndex(ToDoDBContract.ToDoList.COLUMN_PICTURE));
                else
                    bytes = null;

                Bitmap bitmap;
                if (size == 0)
                    bitmap = null;
                else
                    bitmap = getBitmapFromBytes(bytes, size);

                arrayList.add(
                    new ToDoItem(
                        res.getInt(res.getColumnIndex(ToDoDBContract.ToDoList.COLUMN_ID)),
                        res.getString(res.getColumnIndex(ToDoDBContract.ToDoList.COLUMN_NAME)),
                        ToDoDBContract.iso8601Format.parse(res.getString(res.getColumnIndex(ToDoDBContract.ToDoList.COLUMN_DATE))),
                        (res.getInt(res.getColumnIndex(ToDoDBContract.ToDoList.COLUMN_CHECK)) == 1),
                        res.getString(res.getColumnIndex(ToDoDBContract.ToDoList.COLUMN_CONTENTS)),
                        bitmap
                    )
                );
            } catch (ParseException e) {
                e.printStackTrace();
            }
            res.moveToNext();
        }
        return arrayList;
    }*/

    public int getLastID() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + ToDoDBContract.Settings.TABLE_NAME + " where " + ToDoDBContract.Settings.COLUMN_NAME + "=\"" + ToDoDBContract.Settings.LAST_ID_NAME + "\"", null);
        if (cursor.getCount() != 0) {
            cursor.moveToFirst();
            return Integer.parseInt(cursor.getString(cursor.getColumnIndex(ToDoDBContract.Settings.COLUMN_VALUE)));
        }

        SQLiteDatabase dbw = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ToDoDBContract.Settings.COLUMN_NAME, ToDoDBContract.Settings.LAST_ID_NAME);
        contentValues.put(ToDoDBContract.Settings.COLUMN_VALUE, "0");
        dbw.insert(ToDoDBContract.Settings.TABLE_NAME, null, contentValues);

        return 0;
    }
    public boolean updateLastID(int id) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(ToDoDBContract.Settings.COLUMN_VALUE, Integer.toString(id));

        db.update(ToDoDBContract.Settings.TABLE_NAME, contentValues, ToDoDBContract.Settings.COLUMN_NAME + " = ? ", new String[]{ToDoDBContract.Settings.LAST_ID_NAME});
        return true;
    }

    /*public int getThemeID() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + ToDoDBContract.Settings.TABLE_NAME + " where " + ToDoDBContract.Settings.COLUMN_NAME + "=\"" + ToDoDBContract.Settings.THEME_ID_NAME + "\"", null);
        if (cursor.getCount() != 0) {
            cursor.moveToFirst();
            return Integer.parseInt(cursor.getString(cursor.getColumnIndex(ToDoDBContract.Settings.COLUMN_VALUE)));
        }

        SQLiteDatabase dbw = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ToDoDBContract.Settings.COLUMN_NAME, ToDoDBContract.Settings.THEME_ID_NAME);
        contentValues.put(ToDoDBContract.Settings.COLUMN_VALUE, Integer.toString(R.style.AppTheme));
        dbw.insert(ToDoDBContract.Settings.TABLE_NAME, null, contentValues);

        return R.style.AppTheme;
    }
    public boolean updateThemeID(int id) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(ToDoDBContract.Settings.COLUMN_VALUE, Integer.toString(id));

        db.update(ToDoDBContract.Settings.TABLE_NAME, contentValues, ToDoDBContract.Settings.COLUMN_NAME + " = ? ", new String[]{ToDoDBContract.Settings.THEME_ID_NAME});
        return true;
    }*/
}
