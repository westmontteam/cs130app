//used a tutorial from vogella.com
package edu.westmont.course;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

public class PositionsDataSource {
	
  public String run = "default";

  // Database fields
  private SQLiteDatabase database;
  private MySQLiteHelper dbHelper;
  private String[] allColumns = { MySQLiteHelper.COLUMN_ID,
      MySQLiteHelper.COLUMN_LATITUDE, MySQLiteHelper.COLUMN_LONGITUDE,
      MySQLiteHelper.COLUMN_HEIGHT,MySQLiteHelper.COLUMN_TIME,MySQLiteHelper.COLUMN_SPEED};

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
    values.put(MySQLiteHelper.COLUMN_TIME, loc.getTime());
    values.put(MySQLiteHelper.COLUMN_SPEED, loc.getSpeed());
    long insertId = database.insert(run, null,
        values);
    Cursor cursor = database.query(run,
        allColumns, MySQLiteHelper.COLUMN_ID + " = " + insertId, null,
        null, null, null);
    cursor.moveToFirst();
    Position newPosition = cursorToPosition(cursor);
    cursor.close();
    return newPosition;
  }

  //TODO fix this to work with multiple tables.
  public void deletePosition(Position position) {
    long id = position.getId();
    System.out.println("Position deleted with id: " + id);
    database.delete(MySQLiteHelper.TABLE_POSITIONS, MySQLiteHelper.COLUMN_ID
        + " = " + id, null);
  }
  
  public void setRunName(String runName){
	  run = sanitizeInput(runName);
  }
  
  //set the run name prior to calling this method.
  public void makeRun(){
	  if(!containsTable(run)) dbHelper.createTable(database,run);
  }
  
  public boolean containsTable(String tablename){
	  Cursor cursor = dbHelper.showAllTables(database);
	    if (cursor.moveToFirst()){
	    	do{
	    		if(cursor.getString(0).equals(tablename)) return true;
	    	}while (cursor.moveToNext());
	    }
	    return false;
  }
  
  //logs all table names.
  public void displayAllTables(){
	  Cursor cursor = dbHelper.showAllTables(database);
	  Log.w(MySQLiteHelper.class.getName(),"the current tables are: ");
	    if (cursor.moveToFirst()){
	    	do{
	    		Log.w(MySQLiteHelper.class.getName(),cursor.getString(0));
	    	}while (cursor.moveToNext());
	    }
  }

  //gets all positions from the current runs table
  public List<Position> getAllPositions() {
    List<Position> positions = new ArrayList<Position>();

    Cursor cursor = database.query(run,
        allColumns, null, null, null, null, null);

    cursor.moveToFirst();
    while (!cursor.isAfterLast()) {
      Position position = cursorToPosition(cursor);
      positions.add(position);
      cursor.moveToNext();
    }
    // make sure to close the cursor
    cursor.close();
    Log.w("dataSource","finished with getting all positions");
    return positions;
  }

  private Position cursorToPosition(Cursor cursor) {
    Position position = new Position("database");
    position.setId(cursor.getLong(0));
    
    position.setLatitude(cursor.getDouble(1));
    position.setLongitude(cursor.getDouble(2));
    position.setAltitude(cursor.getDouble(3));
    position.setTime(cursor.getLong(4));
    position.setSpeed(cursor.getFloat(5));
    position.setAccuracy(99); //arbitrary number for accuracy. I think I can get away with not storing accuracy.
    Log.w("PositionsDataSouce","The speed is: " + position.getSpeed());
    return position;
  }
  
  private String sanitizeInput(String runName){
	  runName = runName.trim();
	  //if the first thing in the string is a number, this replaces it with an _. (SQLite can't handle numbers first) 
	  if (runName.substring(0, 1).matches("[0-9]")) runName = "_" + runName.substring(1);
	  //replaces anything that is not a letter or a number with an underscore.
	  runName = runName.replaceAll("[^[a-zA-Z_0-9]]", "_");
	  return runName;
  }
  //adds the data from the current run to the Stats table
  private void addDataToStatistics(){
	  long time = 0;
	  double altitude = 0;
	  float speed = 0;
	  ContentValues values = new ContentValues();
	  Cursor cursor = database.query(run, allColumns, null, null, null, null, null);
	  cursor.moveToFirst();
	  while (!cursor.isAfterLast()) {
		  if (cursor.isFirst()) time = cursor.getLong(4);
		  if (altitude < cursor.getDouble(3)) altitude = cursor.getDouble(3);
		  if (speed < cursor.getFloat(5)) speed = cursor.getFloat(5);
		  if (cursor.isLast()) time = cursor.getLong(4) - time;
		  cursor.moveToNext();
	  }
	  cursor.close();
	  
	  values.put(MySQLiteHelper.COLUMN_RUN, run);
	  values.put(MySQLiteHelper.COLUMN_HIGHEST_SPEED, speed);
	  values.put(MySQLiteHelper.COLUMN_BEST_TIME, time);
	  values.put(MySQLiteHelper.COLUMN_HIGHEST_ALTITUDE, altitude);
	  database.insert(MySQLiteHelper.TABLE_STATS, null, values);
	  Log.w("PositionsDataSource","highest speed: "+ speed + ". time: " + time / 1000 + " seconds. highest altitude: " + altitude);
  }
  //deletes all entries without deleting the table.
  public void deleteAllEntries(){
	  Cursor cursor = database.query(run, allColumns, null, null, null, null, null);
	  cursor.moveToFirst();
	  while (!cursor.isAfterLast()){
		  database.delete(run, MySQLiteHelper.COLUMN_ID + "=" + cursor.getLong(0), null);
		  cursor.moveToNext();
	  }
	  cursor.close();
  }
  
  public void done(){
	  //add best time, speed, and altitude to statistics table
	  addDataToStatistics();
	  //delete data from current run table
	  //deleteAllEntries();
	  close(); //Maybe?
  }
} 
