package android.health.dietlogging;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * This class stores the FDA database of food nutrients. It is designed to be created and
 * populated with data on first run of the application, accessed only on a read-only basis
 * after this.
 * 
 * 
 * @author Joel Botner
 */
public class DietDatabaseAdapter {

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

    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    /**
     * Key SQL statements to create the database. NOTE: the _id column must go first
     * Android uses it as an internal reference when interacting with other components.
     */
    private static final String FDA_DATABASE_CREATE = "create table fdaDetails (_id integer primary key autoincrement, "
        + "description text not null, calories text not null, protein text not null, totalFat text not null, carbs text not null, "
        + "dietaryFiber text not null, sugar text not null, calcium text not null, iron text not null, sodium text not null"
        + ", vitaminC text not null, retinol text not null, saturatedFat text not null, cholesterol text not null);";
    private static final String DATABASE_NAME = "excercise_session_data";
    private static final String fda_DATABASE_TABLE = "fdaDetails";
    private static final int DATABASE_VERSION = 3;
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
            db.execSQL(FDA_DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS fdaDetails");
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
    public DietDatabaseAdapter(Context theContext) {
        this.mContext = theContext;
    }

    
    /**
     * Open the food entrys database. If it cannot be opened, try to create a new
     * instance of the database. If it cannot be created, throw an exception to
     * signal the failure
     * 
     * @return this (self reference, allowing this to be chained in an
     *         initialization call)
     * @throws SQLException if the database could be neither opened or created
     */
    public DietDatabaseAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(this.mContext);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        mDbHelper.close();
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
        initialValues.put(KEY_RETINOL, retinol);
        initialValues.put(KEY_SATURATED_FAT, saturated_fat);
        initialValues.put(KEY_CHOLESTEROL, cholesterol);

        return mDb.insert(fda_DATABASE_TABLE, null, initialValues);
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
    	return mDb.query(fda_DATABASE_TABLE, null, KEY_DESCRIPTION + " LIKE ?",new String[]{entry}, null, null, null);
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
}
