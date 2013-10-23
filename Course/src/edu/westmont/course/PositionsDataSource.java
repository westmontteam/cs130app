//used a tutorial from vogella.com
package edu.westmont.course;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

public class PositionsDataSource {
	
  public String run = "not initialized";

  // Database fields
  private SQLiteDatabase database;
  private MySQLiteHelper dbHelper;
  private String[] allColumns = { MySQLiteHelper.COLUMN_ID,
      MySQLiteHelper.COLUMN_LATITUDE, MySQLiteHelper.COLUMN_LONGITUDE, MySQLiteHelper.COLUMN_HEIGHT,
      MySQLiteHelper.COLUMN_RUN};

  public PositionsDataSource(Context context) {
    dbHelper = new MySQLiteHelper(context);
  }

  public void open() throws SQLException {
    database = dbHelper.getWritableDatabase();
  }

  public void close() {
    dbHelper.close();
  }

  public Position createPosition(Location loc) {
    ContentValues values = new ContentValues();
    values.put(MySQLiteHelper.COLUMN_LATITUDE, loc.getLatitude());
    values.put(MySQLiteHelper.COLUMN_LONGITUDE, loc.getLongitude());
    values.put(MySQLiteHelper.COLUMN_HEIGHT, loc.getAltitude());
    values.put(MySQLiteHelper.COLUMN_RUN, run);
    long insertId = database.insert(MySQLiteHelper.TABLE_POSITIONS, null,
        values);
    Cursor cursor = database.query(MySQLiteHelper.TABLE_POSITIONS,
        allColumns, MySQLiteHelper.COLUMN_ID + " = " + insertId, null,
        null, null, null);
    cursor.moveToFirst();
    Position newPosition = cursorToPosition(cursor);
    cursor.close();
    return newPosition;
  }

  public void deletePosition(Position position) {
    long id = position.getId();
    System.out.println("Position deleted with id: " + id);
    database.delete(MySQLiteHelper.TABLE_POSITIONS, MySQLiteHelper.COLUMN_ID
        + " = " + id, null);
  }
  
  public void setRunName(String runName){
	  run = runName;
  }

  public List<Position> getAllPositions() {
    List<Position> positions = new ArrayList<Position>();

    Cursor cursor = database.query(MySQLiteHelper.TABLE_POSITIONS,
        allColumns, null, null, null, null, null);

    cursor.moveToFirst();
    while (!cursor.isAfterLast()) {
      Position position = cursorToPosition(cursor);
      positions.add(position);
      cursor.moveToNext();
    }
    // make sure to close the cursor
    cursor.close();
    return positions;
  }

  private Position cursorToPosition(Cursor cursor) {
    Position position = new Position();
    position.setId(cursor.getLong(0));
    position.setll(new LatLng(cursor.getDouble(1),cursor.getDouble(2)));
    position.setHeight(cursor.getDouble(3));
    return position;
  }
} 
