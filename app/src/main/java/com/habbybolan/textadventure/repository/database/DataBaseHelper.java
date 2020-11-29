package com.habbybolan.textadventure.repository.database;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/*
Helper for dataBase adapter for accessing database in assets folder
 */
public class DataBaseHelper extends SQLiteOpenHelper {

    private static String TAG = "DataBaseHelper"; // Tag just for the LogCat window
    private static String DB_NAME ="db.sqlite"; // Database name
    private static int DB_VERSION = 1; // Database version
    private final File DB_FILE;
    private SQLiteDatabase mDataBase;
    private final Context mContext;
    private String DB_PATH;

    public DataBaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        DB_FILE = context.getDatabasePath(DB_NAME);
        this.mContext = context;
        DB_PATH = context.getApplicationInfo().dataDir + "/databases/" + DB_NAME;
    }

    public void createDataBase() throws IOException {
        // If the database does not exist, copy it from the assets.
        boolean mDataBaseExist = checkDataBase();
        // todo: updates every time it's called - only update once a change has occurred
        //if(!mDataBaseExist) {
            this.getReadableDatabase();
            this.close();
            try {
                // Copy the database from assets
                copyDataBase();
                Log.i(TAG, "Database created");
            } catch (IOException mIOException) {
                throw new Error("ErrorCopyingDataBase");
            }
        //}
    }

    // Check that the database file exists in databases folder
    private boolean checkDataBase() {
        return DB_FILE.exists();
    }

    // Copy the database from assets
    private void copyDataBase() throws IOException {
        InputStream mInput = mContext.getAssets().open(DB_NAME);
        OutputStream mOutput = new FileOutputStream(DB_FILE);
        byte[] mBuffer = new byte[1024];
        int mLength;
        while ((mLength = mInput.read(mBuffer)) > 0) {
            mOutput.write(mBuffer, 0, mLength);
        }
        mOutput.flush();
        mOutput.close();
        mInput.close();
    }

    // Open the database, so we can query it
    boolean openDataBase() throws SQLException {
        // Log.v("DB_PATH", DB_FILE.getAbsolutePath());
        mDataBase = SQLiteDatabase.openDatabase(DB_PATH, null, SQLiteDatabase.OPEN_READONLY);
        // mDataBase = SQLiteDatabase.openDatabase(DB_FILE, null, SQLiteDatabase.NO_LOCALIZED_COLLATORS);
        return mDataBase != null;
    }

    @Override
    public synchronized void close() {
        if(mDataBase != null) {
            mDataBase.close();
        }
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

}