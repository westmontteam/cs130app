//used a tutorial from vogella.com
package edu.westmont.course;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteHelper extends SQLiteOpenHelper {

  public static final String TABLE_POSITIONS = "positions";
  public static final String COLUMN_ID = "_id";
  public static final String COLUMN_LATITUDE = "latitude";
  public static final String COLUMN_LONGITUDE = "longitude";
  public static final String COLUMN_HEIGHT = "height";
  public static final String COLUMN_RUN = "run";

  private static final String DATABASE_NAME = "positions.db";
  private static final int DATABASE_VERSION = 2;

  // Database creation sql statement
  private static final String DATABASE_CREATE = "create table "
      + TABLE_POSITIONS + "(" + COLUMN_ID
      + " integer primary key autoincrement, " + COLUMN_LATITUDE
      + " text not null, " + COLUMN_LONGITUDE + " text not null, "
      + COLUMN_HEIGHT + " text not null, " + COLUMN_RUN + ");";

  public MySQLiteHelper(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
  }

  @Override
  public void onCreate(SQLiteDatabase database) {
    database.execSQL(DATABASE_CREATE);
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    Log.w(MySQLiteHelper.class.getName(),
        "Upgrading database from version " + oldVersion + " to "
            + newVersion + ", which will destroy all old data");
    db.execSQL("DROP TABLE IF EXISTS " + TABLE_POSITIONS);
    onCreate(db);
  }

} 