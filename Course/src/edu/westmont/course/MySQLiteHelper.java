//used a tutorial from vogella.com
package edu.westmont.course;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteHelper extends SQLiteOpenHelper {

  public static final String TABLE_POSITIONS = "positions";
  public static final String COLUMN_ID = "_id";
  public static final String COLUMN_LATITUDE = "latitude";
  public static final String COLUMN_LONGITUDE = "longitude";
  public static final String COLUMN_HEIGHT = "height";
  public static final String COLUMN_TIME = "time";
  public static final String COLUMN_SPEED = "speed";
  
  public static final String TABLE_STATS ="run_statistics";
  public static final String COLUMN_RUN = "run";
  public static final String COLUMN_HIGHEST_SPEED = "speed";
  public static final String COLUMN_BEST_TIME = "time";
  public static final String COLUMN_HIGHEST_ALTITUDE = "altitude";
  public static final String COLUMN_DATE = "date";
  
  public static final String TABLE_RUNS = "runs";
  public static final String COLUMN_RUN_NAME = "runName";
  public static final String COLUMN_RUN_ID = "runID";

  private static final String DATABASE_NAME = "positions.db";
  private static final int DATABASE_VERSION = 13;

  // Table creation sql statement
  private static final String TABLE_CREATE = "(" + COLUMN_ID
	      + ", " + COLUMN_LATITUDE
	      + " text not null, " + COLUMN_LONGITUDE + " text not null, "
	      + COLUMN_HEIGHT + " text not null, " + COLUMN_TIME + ", " + COLUMN_SPEED + ");";

  public MySQLiteHelper(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
  }

  @Override
  public void onCreate(SQLiteDatabase database) {
	  database.execSQL("create table " + TABLE_POSITIONS + TABLE_CREATE);
	  
	  database.execSQL("create table " + TABLE_STATS + "(" + COLUMN_RUN + ", " + COLUMN_HIGHEST_SPEED
			  + ", " + COLUMN_BEST_TIME + ", " + COLUMN_HIGHEST_ALTITUDE + ", " + COLUMN_DATE + ");");
	  
	  database.execSQL("create table " + TABLE_RUNS + "(" + COLUMN_RUN_NAME + ", " + COLUMN_RUN_ID + "AUTOINCREMENT INTEGER PRIMARY KEY" + ");");
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    Log.w(MySQLiteHelper.class.getName(),
        "Upgrading database from version " + oldVersion + " to "
            + newVersion + ", which will destroy all old data");
    
    Cursor cursor = showAllTables(db);
    if (cursor.moveToFirst()){
    	do{
    		Log.w(MySQLiteHelper.class.getName(),"Might be about to delete: " + cursor.getString(0));
    		if(!cursor.getString(0).equals("sqlite_sequence") && !cursor.getString(0).equals("android_metadata")){
    			db.execSQL("DROP TABLE IF EXISTS " + cursor.getString(0));
    		}
    	}while (cursor.moveToNext());
    }
    
    onCreate(db);
  }
  
  public Cursor showAllTables(SQLiteDatabase db){
      String mySql = " SELECT name FROM sqlite_master " + " WHERE type='table'             ";
      return db.rawQuery(mySql, null);
  }
  
  public void createTable(SQLiteDatabase db,String table){
	  db.execSQL("create table " + table + TABLE_CREATE);
  }

} 