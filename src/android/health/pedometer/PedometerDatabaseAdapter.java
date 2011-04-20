package android.health.pedometer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * This class strictly handles interaction between the exercise sessions database and the rest
 * of the app, only this class should directly touch the database at any point. Based off of
 * the NotesDbAdapter example from the following Google tutorial:
 * http://developer.android.com/resources/tutorials/notepad/notepad-ex1.html
 * 
 * See, now we know Google isn't evil.
 * 
 * @author Joel Botner
 */
public class PedometerDatabaseAdapter {

    public static final String KEY_TITLE = "title";
    public static final String KEY_DISTANCE = "distance";
    public static final String KEY_TIME = "time";
    public static final String KEY_DATE = "date";
    public static final String KEY_TYPE = "type";
    public static final String KEY_CALORIES = "calories";
    public static final String KEY_ROWID = "_id";

    private static final String TAG = "PedometerDatabaseAdapter";
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    /**
     * Key SQL statements to create the database. NOTE: the _id column must go first
     * Android uses it as an internal reference when interacting with other components.
     * 
     * For the DATABASE_CREATE String, you seeI'm creating a bunch of columns and storing the data
     * as Strings, with the exception of the date value. I'm using the date variable to filter the database
     * for certain operations, but using Strings is simpler and less error-prone overall. You can just convert
     * back to their original formats in the rest of the application.
     */
    private static final String DATABASE_CREATE = "create table sessions (_id integer primary key autoincrement, "
        + "title text not null, distance text not null, time text not null, date integer not null, type text not null, calories text not null);";
    private static final String DATABASE_NAME = "excercise_session_data";
    private static final String DATABASE_TABLE = "sessions";
    private static final int DATABASE_VERSION = 2;
    private final Context mContext;

    /**
     * DON'T TOUCH THIS, only modify the String constants above.
     */
    private class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS sessions");
            onCreate(db);
        }
    }

    /**
     * Constructor - takes the context to allow the database to be
     * opened/created. NOTE: You need to create this adapter in an actual Activity.
     * Share the created adapter object with other classes if necessary, but create it
     * in an Activity.
     * 
     * @param theContext the Context within which to work
     */
    public PedometerDatabaseAdapter(Context theContext) {
        this.mContext = theContext;
    }

    /**
     * Open the sessions database. If it cannot be opened, try to create a new
     * instance of the database. If it cannot be created, throw an exception to
     * signal the failure
     * 
     * @return this (self reference, allowing this to be chained in an
     *         initialization call)
     * @throws SQLException if the database could be neither opened or created
     */
    public PedometerDatabaseAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(this.mContext);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        mDbHelper.close();
    }

    /**
     * Creates a new entry in the exercise session database with the specified attributes.
     * NOTE: Using the ContentValues isn't strictly necessary, but it automagically
     * handles the SQL syntax for you.
     * 
     * @param title - The title of this session.
     * @param distance - The distance (in meters) estimated to have traveled.
     * @param time - The amount of time (in milliseconds) the session took.
     * @param date - The date according to the GregorianCalendar this is (in milliseconds).
     * @param type - The type of exercise this was.
     * @param calories - The estimated number of calories that were burned.
     * @return The Row ID of this new entry.
     */
    public long createSession(String title, String distance, String time, long date, String type, String calories) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_TITLE, title);
        initialValues.put(KEY_DISTANCE, distance);
        initialValues.put(KEY_TIME, time);
        initialValues.put(KEY_DATE, date);
        initialValues.put(KEY_TYPE, type);
        initialValues.put(KEY_CALORIES, calories);

        return mDb.insert(DATABASE_TABLE, null, initialValues);
    }

    /**
     * Delete the note with the given rowId
     * NOTE: rowId is that special Android identifier
     * 
     * @param rowId id of note to delete
     * @return true if deleted, false otherwise
     */
    public boolean deleteSession(long rowId) {
        return mDb.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
    }

    /**
     * Return a Cursor over the list of all notes in the database
     * NOTE: The cursor isn't a list itself, here's what it is:
     * http://developer.android.com/reference/android/database/Cursor.html
     * 
     * @return Cursor over all notes
     */
    public Cursor fetchAllSessions() {
        return mDb.query(DATABASE_TABLE, new String[] {KEY_ROWID, KEY_TITLE,
                KEY_DISTANCE, KEY_TIME, KEY_DATE, KEY_TYPE, KEY_CALORIES}, null, null, null, null, null);
    }
    
    /**
     * Returns a Cursor containing all exercise sessions on or after a certain date.
     * 
     * @param date - The date (must be in the standard millisecond format of java.util.Calendar and referring to midnight)
     * @return A cursor pointing to all exercise sessions on or after a certain date.
     */
    public Cursor fetchAllSessionsAfter(long date){
    	//NOTE: What I'm doing here is I'm requesting all database entries, but filtering by date
    	//Not that I've tested it yet, that should come soon    	
    	return mDb.query(true, DATABASE_TABLE, new String[] {KEY_ROWID, KEY_TITLE,
                KEY_DISTANCE, KEY_TIME, KEY_DATE, KEY_TYPE, KEY_CALORIES}, KEY_DATE + "<=" + date, null,
                null, null, null, null);
    }

    /**
     * Return a String containing the attributes of the row in tab-delimited
     * format.
     * 
     * @param rowId id of session to retrieve
     * @return String containing the attributes in tab-delimited format.
     * @throws SQLException if session could not be found/retrieved
     */
    public String fetchSession(long rowId) throws SQLException {
    	String theAttributes = "";
        Cursor mCursor =
            mDb.query(true, DATABASE_TABLE, new String[] {KEY_ROWID, KEY_TITLE,
                    KEY_DISTANCE, KEY_TIME, KEY_DATE, KEY_TYPE, KEY_CALORIES}, KEY_ROWID + "=" + rowId, null,
                    null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        theAttributes += mCursor.getString(mCursor.getColumnIndex(KEY_TITLE));
        theAttributes += "\t" + mCursor.getString(mCursor.getColumnIndex(KEY_DISTANCE));
        theAttributes += "\t" + mCursor.getString(mCursor.getColumnIndex(KEY_TIME));
        theAttributes += "\t" + mCursor.getLong(mCursor.getColumnIndex(KEY_DATE));
        theAttributes += "\t" + mCursor.getString(mCursor.getColumnIndex(KEY_TYPE));
        theAttributes += "\t" + mCursor.getString(mCursor.getColumnIndex(KEY_CALORIES));
        return theAttributes;
    }

    /**
     * Update the session using the details provided. The session to be updated is
     * specified using the rowId, and it is altered to use the new values
     * passed in
     * 
     * @param rowId - The database id of the session, if you know it.
     * @param title - The title of the session
     * @param distance - Estimated distance traveled in a session
     * @param time - Time taken to complete a session
     * @param date - Date a session began
     * @param type - Type of exercise this session was (bike, running, or walking)
     * @param calories - Estimated number of calories burned this session
     * @return If the entry was properly updated
     */
    public boolean updateSession(long rowId, String title, String distance, String time, String date, String type, String calories) {
        ContentValues args = new ContentValues();
        args.put(KEY_TITLE, title);
        args.put(KEY_DISTANCE, distance);
        args.put(KEY_TIME, time);
        args.put(KEY_DATE, date);
        args.put(KEY_TYPE, type);
        args.put(KEY_CALORIES, calories);

        return mDb.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
    }
}
