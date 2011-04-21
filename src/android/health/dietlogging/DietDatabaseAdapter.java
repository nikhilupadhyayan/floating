package android.health.dietlogging;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.health.pedometer.PedometerDatabaseAdapter;
import android.util.Log;

/**
 * This class creates a blank database, then copies the FoodDB.sqlite database within the assets folder
 * into it in order to keep track of the nutrition Info and etc for the rest of the app. Only this class
 * should directly touch the database at any point. Based off the SQLite tutorial from the following example:
 * http://www.reigndesign.com/blog/using-your-own-sqlite-database-in-android-applications/
 * 
 * @author John Mauldin and Joel Botner
 */


public class DietDatabaseAdapter {

	public static final String KEY_TITLE = "title";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_PROTEIN = "protein";
    public static final String KEY_CARBBOHYDRATES = "carbohydrates";
    public static final String KEY_FIBER = "fiber";
    public static final String KEY_SUGAR = "sugar";
    public static final String KEY_CALCIUM = "calcium";
    public static final String KEY_IRON = "iron";
    public static final String KEY_MAGNESIUM = "magnesium";
    public static final String KEY_PHOSPHORUS = "phosphorus";
    public static final String KEY_POTASSIUM = "potassium";
    public static final String KEY_SODIUM = "sodium";
    public static final String KEY_ZINC = "zinc";
    public static final String KEY_VIT_C = "vit_C";
    public static final String KEY_VIT_B6 = "vit_B6";
    public static final String KEY_VIT_B12 = "vit_B12";
    public static final String KEY_VIT_A_IU = "vit_A_IU";
    public static final String KEY_VIT_A_RAE = "vit_A_RAE";
    public static final String KEY_VIT_E = "vit_E";
    public static final String KEY_VIT_D = "vit_D";
    public static final String KEY_VIT_K = "vit_K";
    public static final String KEY_CHOLESTEROL = "cholesterol";
    public static final String KEY_ROWID = "_id";

    private static final String TAG = "DietDatabaseAdapter";
    public DatabaseHelper mDbHelper;
    private SQLiteDatabase myDatabase;
    
    //The Android's default system path of your application database.
    private static String DB_PATH = "/data/data/android.health.manager/databases/";
    private static String DB_NAME = "FoodDB";
	
    /**
     * Key SQL statements to create the database. NOTE: the _id column must go first
     * Android uses it as an internal reference when interacting with other components.
     * 
     * For the DATABASE_CREATE String, you seeI'm creating a bunch of columns and storing the data
     * as Strings, with the exception of the date value. I'm using the date variable to filter the database
     * for certain operations, but using Strings is simpler and less error-prone overall. You can just convert
     * back to their original formats in the rest of the application.
     */
    private static final String DATABASE_CREATE = "create table foods (_id integer primary key autoincrement, "
        + "no piece of nutrition data is null);";
    private static final String DATABASE_NAME = "nutrition_info_data";
    private static final String DATABASE_TABLE = "foods";
    private static final int DATABASE_VERSION = 2;
    private final Context myContext;
   private final Activity myActivity;

    
    private class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
    
        /**
         * Creates a empty database on the system and rewrites it with your own database.
         * */
        public void createDataBase() throws IOException{
     
        	boolean dbExist = checkDataBase();
     
        	if(dbExist){
        		//do nothing - database already exist
        	}else{
     
        		//By calling this method an empty database will be created into the default system path
                //of your application so we are be able to overwrite the old database with Food database.
            	this.getReadableDatabase();
     
            	try {
     
        			copyDataBase();
     
        		} catch (IOException e) {
     
            		throw new Error("Error copying database");
     
            	}
        	}
     
        }
        /**
         * Check if the database already exist to avoid re-copying the file each time you open the application.
         * @return true if it exists, false if it doesn't
         */
        private boolean checkDataBase(){
     
        	SQLiteDatabase checkDB = null;
     
        	try{
        		String myPath = DB_PATH + DB_NAME;
        		checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
     
        	}catch(SQLiteException e){
     
        		//database does't exist yet.
     
        	}
     
        	if(checkDB != null){
     
        		checkDB.close();
     
        	}
     
        	return checkDB != null ? true : false;
        }
     
        /**
         * Copies your database from your local assets-folder to the just created empty database in the
         * system folder, from where it can be accessed and handled.
         * This is done by transfering bytestream.
         * */
        private void copyDataBase() throws IOException{
     
        	//Open your local db as the input stream
        	InputStream myInput = myContext.getAssets().open(DB_NAME);
     
        	// Path to the just created empty db
        	String outFileName = DB_PATH + DB_NAME;
     
        	//Open the empty db as the output stream
        	OutputStream myOutput = new FileOutputStream(outFileName);
     
        	//transfer bytes from the inputfile to the outputfile
        	byte[] buffer = new byte[1024];
        	int length;
        	while ((length = myInput.read(buffer))>0){
        		myOutput.write(buffer, 0, length);
        	}
     
        	//Close the streams
        	myOutput.flush();
        	myOutput.close();
        	myInput.close();
     
        }
     
        public void openDataBase() throws SQLException{
     
        	//Open the database
            String myPath = DB_PATH + DB_NAME;
        	myDatabase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
     
        }
     
        @Override
    	public synchronized void close() {
     
        	    if(myDatabase != null)
        		    myDatabase.close();
     
        	    super.close();
     
    	}
     
    	@Override
    	public void onCreate(SQLiteDatabase db) {
    		db.execSQL(DATABASE_CREATE);
    		try {
				this.createDataBase();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
     
    	@Override
    	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    		//Do no alter database
    		db.execSQL("DROP TABLE IF EXISTS " + DB_NAME);
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
        public DietDatabaseAdapter(Context theContext, Activity activity) {
            this.myContext = theContext;
            this.myActivity = activity;
        }
    	
        /**
         * Open the food database. If it cannot be opened, try to create a new
         * instance of the database. If it cannot be created, throw an exception to
         * signal the failure
         * 
         * @return this (self reference, allowing this to be chained in an
         *         initialization call)
         * @throws SQLException if the database could be neither opened or created
         */
        public DietDatabaseAdapter open() throws SQLException {
            mDbHelper = new DatabaseHelper(this.myContext);
            myDatabase = mDbHelper.getWritableDatabase();
            return this;
        }
        
        public void close() {
            mDbHelper.close();
        }
        
        //TODO: This stuff I'm currently rewriting, as I don't need to update the database like you do.
        //And, I'm actually curious if i can rewrite my food or nutrition info classes, so I could just
        //pull from the database for my meals and etc, instead of using those classes as a middleman
        
        

        /**
         * Return a Cursor over all the fields in a given row
         * NOTE: The cursor isn't a list itself, here's what it is:
         * http://developer.android.com/reference/android/database/Cursor.html
         * 
         * @return Cursor over all notes
         */
        public Cursor fetchAllInfo(long rowId) {
            return myDatabase.query(DATABASE_TABLE, new String[] {KEY_ROWID, KEY_TITLE,
                    KEY_CALCIUM, KEY_CARBBOHYDRATES, KEY_CHOLESTEROL, KEY_DESCRIPTION, KEY_FIBER,
                    KEY_IRON, KEY_MAGNESIUM, KEY_PHOSPHORUS, KEY_POTASSIUM, KEY_PROTEIN,
                    KEY_SODIUM, KEY_SUGAR, KEY_VIT_A_IU, KEY_VIT_A_RAE, KEY_VIT_B12, KEY_VIT_B6,
                    KEY_VIT_C, KEY_VIT_D, KEY_VIT_E, KEY_VIT_K, KEY_ZINC},
                    KEY_ROWID + "=" + rowId, null, null, null, null);
        }
        
        

        /**
         * Return a Cursor positioned at the spot that matches the given rowId
         * 
         * @param rowId id of row to retrieve
         * @param colId id of col to retrieve
         * @return Cursor positioned to matching note, if found
         * @throws SQLException if note could not be found/retrieved
         */
        public Cursor fetchInfo(long rowId, String colId) throws SQLException {
            Cursor mCursor =
                myDatabase.query(true, DATABASE_TABLE, new String[] {KEY_ROWID, KEY_TITLE,
                        KEY_CALCIUM, KEY_CARBBOHYDRATES, KEY_CHOLESTEROL, KEY_DESCRIPTION, KEY_FIBER,
                        KEY_IRON, KEY_MAGNESIUM, KEY_PHOSPHORUS, KEY_POTASSIUM, KEY_PROTEIN,
                        KEY_SODIUM, KEY_SUGAR, KEY_VIT_A_IU, KEY_VIT_A_RAE, KEY_VIT_B12, KEY_VIT_B6,
                        KEY_VIT_C, KEY_VIT_D, KEY_VIT_E, KEY_VIT_K, KEY_ZINC}, KEY_ROWID + "=" + rowId, null,
                        null, null, null, null);
            if (mCursor != null) {
                mCursor.moveToFirst();
            }
            mCursor.getLong(mCursor.getColumnIndex(colId));
            return mCursor;
        }
        
        public Cursor getMatchingStates(String constraint) throws SQLException{
        	String queryString =
                "SELECT _id, food, description FROM " + DB_NAME;
        	if (constraint != null) {
                // Query for any rows where the state name begins with the
                // string specified in constraint.
        		constraint = constraint.trim() + "%";
                queryString += " WHERE food LIKE ?";
        	}
        	String params[] = { constraint };
        	 
            if (constraint == null) {
                // If no parameters are used in the query,
                // the params arg must be null.
                params = null;
            }
            try {
                Cursor cursor = myDatabase.rawQuery(queryString, params);
                if (cursor != null) {
                    this.myActivity.startManagingCursor(cursor);
                    cursor.moveToFirst();
                    return cursor;
                }
            }
            catch (SQLException e) {
                Log.e("AutoCompleteDbAdapter", e.toString());
                throw e;
            }
     
            return null;
        }
    }
