package utils;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.InstrumentationInfo;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.IntegerRes;

/**
 * Database tool for the phone's database
 */

public class DBTools extends SQLiteOpenHelper {
    private final static String DATABASE_NAME = "SEAndroidApp.db";
    private static final int DATABASE_VERSION = 1;

    // Create table to store token
    private static final String TABLE_USER = "user";
    private static final String COLUMN_USER_ID = "id";
    private static final String COLUMN_USER_IS_TEACHER = "is_teacher";
    private static final String COLUMN_USER_TOKEN = "token";
    private static final String COLUMN_USERNAME = "username";
    private static final String TABLE_TOKEN_CREATE =
            "CREATE TABLE " + TABLE_USER + " (" + COLUMN_USER_ID + " INTEGER PRIMARY KEY, "
            + COLUMN_USER_IS_TEACHER + " BOOLEAN, " + COLUMN_USER_TOKEN + " TEXT NOT NULL, "
            + COLUMN_USERNAME + " TEXT)";

    public DBTools(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(TABLE_TOKEN_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        onCreate(sqLiteDatabase);
    }

    /**
     * Creates a token entry in the database for the user of this phone.
     * @param token represents the value of the token returned by the web service
     * @return returns the token back after the call
     * @throws SQLiteConstraintException
     */
    public void createUser(int id, String token, Boolean isTeacher, String username) throws SQLiteConstraintException {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID, id);
        values.put(COLUMN_USER_IS_TEACHER, isTeacher);
        values.put(COLUMN_USER_TOKEN, token);
        values.put(COLUMN_USERNAME, username);
        database.insertOrThrow(TABLE_USER, null, values);
        database.close();
    }

    public String getUsername() {
        try {
            SQLiteDatabase database = this.getReadableDatabase();
            // Get everything out of the table
            Cursor cursor = database.query(TABLE_USER, null, null, null, null, null, null, null);
            // Move to the last row and get the value of the token
            cursor.moveToLast();
            String username = cursor.getString(3);
            cursor.close();
            database.close();
            return username;
        } catch (CursorIndexOutOfBoundsException e) {
            return "";
        }
    }

    public Boolean isTeacher() {
        try {
            SQLiteDatabase database = this.getReadableDatabase();
            // Get everything out of the table
            Cursor cursor = database.query(TABLE_USER, null, null, null, null, null, null, null);
            // Move to the last row and get the value of the token
            cursor.moveToLast();
            Boolean isTeacher = cursor.getInt(1) == 1;
            cursor.close();
            database.close();
            return isTeacher;
        } catch (CursorIndexOutOfBoundsException e) {
            return false;
        }
    }

    public String getToken() {
        try {
            SQLiteDatabase database = this.getReadableDatabase();
            // Get everything out of the table
            Cursor cursor = database.query(TABLE_USER, null, null, null, null, null, null, null);
            // Move to the last row and get the value of the token
            cursor.moveToLast();
            String token = cursor.getString(2);
            cursor.close();
            database.close();
            return token;
        } catch (CursorIndexOutOfBoundsException e) {
            return "";
        }
    }

    /**
     * Deletes all of the tokens in the table.
     * @throws SQLiteConstraintException
     */
    public void deleteUsers() throws SQLiteConstraintException {
        SQLiteDatabase database = this.getWritableDatabase();
        database.delete(TABLE_USER, null, null);
        database.close();
    }
}
