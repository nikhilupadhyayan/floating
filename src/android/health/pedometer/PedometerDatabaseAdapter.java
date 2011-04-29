package android.health.pedometer;

import java.util.GregorianCalendar;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.StaleDataException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * This class strictly handles interaction between the exercise sessions database and the rest
 * of the app, only this class should directly touch the database at any point.
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
    
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_CALORIES_STORED = "calories";
    public static final String KEY_PROTEIN = "protein";
    public static final String KEY_TOTAL_FAT = "totalFat";
    public static final String KEY_CARBOHYDRATE = "carbs";
    public static final String KEY_DIETARY_FIBER = "dietaryFiber";
    public static final String KEY_SUGAR = "sugar";
    public static final String KEY_CALCIUM = "calcium";
    public static final String KEY_IRON = "iron";
    public static final String KEY_SODIUM = "sodium";
    public static final String KEY_VITAMIN_C = "vitaminC";
    public static final String KEY_RETINOL = "retinol";
    public static final String KEY_SATURATED_FAT = "saturatedFat";
    public static final String KEY_CHOLESTEROL = "cholesterol";
    public static final String KEY_ROWID_FOOD = "_id";
    
    public static final String KEY_MEAL_DATE = "meal_date";
    public static final String KEY_CALORIES_TOTAL = "calories_total";

    private static final String TAG = "PedometerDatabaseAdapter";
    private DatabaseHelper mDbHelper;
    Activity callingActivity;
    private SQLiteDatabase mDb;

    /**
     * Key SQL statements to create the database. NOTE: the _id column must go first
     * Android uses it as an internal reference when interacting with other components.
     * 
     * For the DATABASE_CREATE String, you see I'm creating a bunch of columns and storing the data
     * as Strings, with the exception of the date value. I'm using the date variable to filter the database
     * for certain operations, but using Strings is simpler and less error-prone overall. You can just convert
     * back to their original formats in the rest of the application.
     */
    private static final String DATABASE_CREATE = "create table sessions (_id integer primary key autoincrement, "
        + "title text  null, distance text not null, time text not null, date integer not null, type text not null, calories text not null);";
    private static final String DATABASE_NAME = "excercise_session_data";
    private static final String DATABASE_TABLE = "sessions";
    private static final int DATABASE_VERSION = 17;
    private final Context mContext;

    private static final String fda_DATABASE_TABLE = "fdaDetails";
    private static final String FDA_DATABASE_CREATE = "create table fdaDetails (_id integer primary key autoincrement, "
        + "description text, calories text, protein text, totalFat text, carbs text, "
        + "dietaryFiber text, sugar text, calcium text, iron text, sodium text"
        + ", vitaminC text, retinol text, saturatedFat text, cholesterol text);";
    
    private static final String meal_DATABASE_TABLE = "mealDetails";
    private static final String MEAL_DATABASE_CREATE = "create table mealDetails (_id integer primary key autoincrement, "
        + "meal_date text, calories_total text);";
    
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
           	try{
           		db.execSQL("DROP TABLE IF EXISTS fdaDetails");
           		db.execSQL(FDA_DATABASE_CREATE);
           		db.execSQL(MEAL_DATABASE_CREATE);
           	}
            catch(SQLException e){};
            Log.i("PedometerDB", "Tables Created");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS sessions");
            db.execSQL("DROP TABLE IF EXISTS fdaDetails");
            db.execSQL("DROP TABLE IF EXISTS mealDetails");
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
     * 
     * @param rowId id of note to delete
     * @return true if deleted, false otherwise
     */
    public boolean deleteSession(long rowId) {
        return mDb.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
    }
    
    /**
     * This is merely a convenience method that tallies up the calories from all sessions
     * that began after the passed point in time.
     * 
     * @param date The beginning date and time (millis in form dictated by {@link GregorianCalendar})
     * @return The number of calories burned since the specified time.
     */
    public int getCalsBurnedSince(long date){
    	int allCalories = 0;
    	Cursor theSessions = fetchAllSessionsAfter(date);
    	theSessions.moveToFirst();
    	for(int a = 0; a < theSessions.getCount(); a++){
    		allCalories += theSessions.getInt(theSessions.getColumnIndex(KEY_CALORIES));
    		if(!theSessions.moveToNext()){
    			break;
    		}
    		theSessions.close();
    	}
    	return allCalories;
    }

    /**
     * Return a {@link Cursor} over the list of all notes in the database
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
                KEY_DISTANCE, KEY_TIME, KEY_DATE, KEY_TYPE, KEY_CALORIES}, KEY_DATE + ">=" + date, null,
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
        mCursor.close();
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
    
    /**
     * Creates a new entry in the exercise food entry database with the specified attributes.
     * NOTE: Using the ContentValues isn't strictly necessary, but it automagically
     * handles the SQL syntax for you.
     * 
     * @param title - The title of this food entry.
     * @param distance - The distance (in meters) estimated to have traveled.
     * @param time - The amount of time (in milliseconds) the food entry took.
     * @param date - The date according to the GregorianCalendar this is (in milliseconds).
     * @param type - The type of exercise this was.
     * @param calories - The estimated number of calories that were burned.
     * @return The Row ID of this new entry.
     */
    public long createFoodEntry(String description, String calories, String protein, String total_fat, String carbs, String dietary_fiber, String sugar, String calcium, String iron, String sodium, String vitamin_c, String retinol, String saturated_fat, String cholesterol) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_DESCRIPTION, description);
        initialValues.put(KEY_CALORIES_STORED, calories);
        initialValues.put(KEY_PROTEIN, protein);
        initialValues.put(KEY_TOTAL_FAT, total_fat);
        initialValues.put(KEY_CARBOHYDRATE, carbs);
        initialValues.put(KEY_DIETARY_FIBER, dietary_fiber);
        initialValues.put(KEY_SUGAR, sugar);
        initialValues.put(KEY_CALCIUM, calcium);
        initialValues.put(KEY_IRON, iron);
        initialValues.put(KEY_SODIUM, sodium);
        initialValues.put(KEY_VITAMIN_C, vitamin_c);
        initialValues.put(KEY_RETINOL, retinol);
        initialValues.put(KEY_SATURATED_FAT, saturated_fat);
        initialValues.put(KEY_CHOLESTEROL, cholesterol);

        return mDb.insert(fda_DATABASE_TABLE, "0", initialValues);
    }

    /**
     * Delete the food entry with the given rowId
     * NOTE: rowId is that special Android identifier
     * 
     * @param rowId id of food entry to delete
     * @return true if deleted, false otherwise
     */
    public boolean deleteFoodEntry(long rowId) {
        return mDb.delete(fda_DATABASE_TABLE, KEY_ROWID_FOOD + "=" + rowId, null) > 0;
    }
  
    public Cursor getFoodMatches(String entry){
    	if (entry == null){
    		return fetchAllFoodEntrys();
    	}
    	return mDb.query(fda_DATABASE_TABLE, new String[] {KEY_DESCRIPTION, KEY_CALORIES_STORED}, KEY_DESCRIPTION + " LIKE ?",new String[]{"%" + entry + "%"}, null, null, null);
    }

    /**
     * Return a Cursor over the list of all food entrys in the database
     * NOTE: The cursor isn't a list itself, here's what it is:
     * http://developer.android.com/reference/android/database/Cursor.html
     * 
     * @return Cursor over all food entrys
     */
    public Cursor fetchAllFoodEntrys() {
        return mDb.query(fda_DATABASE_TABLE, new String[] {KEY_ROWID_FOOD, KEY_DESCRIPTION, KEY_CALORIES_STORED,
                KEY_PROTEIN, KEY_TOTAL_FAT, KEY_CARBOHYDRATE, KEY_DIETARY_FIBER, KEY_SUGAR,
                KEY_CALCIUM, KEY_IRON, KEY_SODIUM, KEY_VITAMIN_C, KEY_RETINOL, KEY_SATURATED_FAT
                ,KEY_CHOLESTEROL}, null, null, null, null, null);
    }

    /**
     * Convenience method that returns the kcal/g ratio of calories in the specified food.
     * 
     * @param rowId - The ID of the food item to retrieve calories for.
     * @return 
     */
    public long fetchCalories(long rowId) throws SQLException {
        Cursor mCursor = mDb.query(true, fda_DATABASE_TABLE, new String[] {KEY_CALORIES_STORED}, KEY_ROWID_FOOD + "=" + rowId, null,
                    null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        
        return Long.valueOf(mCursor.getString(mCursor.getColumnIndex(KEY_CALORIES_STORED)));
    }
    
    /**
     * Logs a meal in the database according to the specified data.
     * 
     * @param date - The date of the meal in millis, see {@link GregorianCalendar}
     * @param calories - The number of calories consumed in this meal
     * @return The Row ID of this new meal
     */
    public long createMeal(String date, String calories) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_MEAL_DATE, date);
        initialValues.put(KEY_CALORIES_TOTAL, calories);

        return mDb.insert(meal_DATABASE_TABLE, null, initialValues);
    }
    
    /**
     * Returns a cursor pointing to all meals consumed after the specified time.
     * 
     * @param date - The time to begin the search from (in millis, see {@link GregorianCalendar} format)
     * @return A Cursor pointing to all matchind database entries.
     */
    public Cursor fetchAllMealsAfter(long date){
    	return mDb.query(true, meal_DATABASE_TABLE, null, KEY_MEAL_DATE + ">=" + date, null, null, null, null, null);
    }
    
    /**
     * Returns all the logged calories the user has consumed since a certain time (in millis, see {@link GregorianCalendar})
     * 
     * @param date - The time in millis to begin the search from.
     * @return The number of calories consumed since the referenced time.
     */
    public int getCalsEatenSince(long date){
    	int allCalories = 0;
    	Cursor theSessions = fetchAllMealsAfter(date);
//    	((Activity)mContext).startManagingCursor
    	theSessions.moveToFirst();
    	try{
    	for(int a = 0; a < theSessions.getCount(); a++){
    		allCalories += theSessions.getInt(theSessions.getColumnIndex(KEY_CALORIES_TOTAL));
    		if(!theSessions.moveToNext()){
    			break;
    		}
    	}
    	}catch(StaleDataException e){
    		theSessions = fetchAllMealsAfter(date);
        	theSessions.moveToFirst();
        	allCalories = 0;
        	for(int a = 0; a < theSessions.getCount(); a++){
        		allCalories += theSessions.getInt(theSessions.getColumnIndex(KEY_CALORIES_TOTAL));
        		if(!theSessions.moveToNext()){
        			break;
        		}
        	}
    	}
		theSessions.close();

    	return allCalories;
    }
}
