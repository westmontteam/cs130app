//used a tutorial from vogella.com
package edu.westmont.course;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteHelper extends SQLiteOpenHelper {

  public static final String TABLE_POSITIONS = "default";
  public static final String COLUMN_ID = "_id";
  public static final String COLUMN_LATITUDE = "latitude";
  public static final String COLUMN_LONGITUDE = "longitude";
  public static final String COLUMN_HEIGHT = "height";
  //public static final String COLUMN_RUN = "run";

  private static final String DATABASE_NAME = "positions.db";
  private static final int DATABASE_VERSION = 6;

  // Table creation sql statement
  private static final String TABLE_CREATE = "(" + COLUMN_ID
	      + " integer primary key autoincrement, " + COLUMN_LATITUDE
	      + " text not null, " + COLUMN_LONGITUDE + " text not null, "
	      + COLUMN_HEIGHT + " text not null);";

  public MySQLiteHelper(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
  }

  @Override
  public void onCreate(SQLiteDatabase database) {
    //createTable(database,"defualt"); //TODO test if I need a default table.
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