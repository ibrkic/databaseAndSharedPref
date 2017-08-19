package com.example.brkic.assecoassignment.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import com.example.brkic.assecoassignment.models.WebPageModel;

/**
 * Created by brka on 19.08.2017..
 */
public class DBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "WebPageDatabase.db";
    public static final String TABLE_WEB_PAGES = "pages";
    public static final String PAGES_COLUMN_ID = "id";
    public static final String PAGES_COLUMN_URL = "url";
    public static final String PAGES_COLUMN_HASH = "hash";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_WEB_PAGES + "("
                + PAGES_COLUMN_ID + " INTEGER PRIMARY KEY," + PAGES_COLUMN_URL + " TEXT,"
                + PAGES_COLUMN_HASH + " TEXT" + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WEB_PAGES);
        onCreate(db);
    }

    /**
     * Insert web page info to database
     *
     * @param webPage Web page model that contains url and hash
     */
    public boolean addWebPage(WebPageModel webPage) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(PAGES_COLUMN_URL, webPage.getWebPageUrl());
        values.put(PAGES_COLUMN_HASH, webPage.getWebPageHash());

        db.insert(TABLE_WEB_PAGES, null, values);
        db.close();
        return true;
    }

    /**
     * Check if web page is already saved to DB
     * @param webPageUrl    Url to web page (we check if url appears inside PAGES_COLUMN_URL column)
     * @return  Boolean that indicates if web page in question is already inside DB
     */
    public boolean checkIfWebPageExists(String webPageUrl) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + TABLE_WEB_PAGES + " where "
                + PAGES_COLUMN_URL + " = " + DatabaseUtils.sqlEscapeString(webPageUrl), null);
        boolean exist = (cursor.getCount() > 0);
        cursor.close();
        db.close();
        return exist;
    }

    /**
     * For provided web page url return it's hash
     * @param webPageUrl    Url to the web page whose hash code we wish to get
     * @return  String containing encoded hash
     */
    public String getHashForUrl(String webPageUrl) {
        String hash = null;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select " + PAGES_COLUMN_HASH + " from " + TABLE_WEB_PAGES + " where "
                + PAGES_COLUMN_URL + " = " + DatabaseUtils.sqlEscapeString(webPageUrl), null);
        if (cursor != null) {
            cursor.moveToFirst();
            hash = cursor.getString(cursor.getColumnIndex(PAGES_COLUMN_HASH));
        }
        return hash;
    }
}
