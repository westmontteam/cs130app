//used a tutorial from vogella.com
package edu.westmont.course;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
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

  public void createPosition(Location loc,String runName) {
	  int id = getLastID(runName);
	  
    ContentValues values = new ContentValues();
    values.put(MySQLiteHelper.COLUMN_ID,id);
    values.put(MySQLiteHelper.COLUMN_LATITUDE, loc.getLatitude());
    values.put(MySQLiteHelper.COLUMN_LONGITUDE, loc.getLongitude());
    values.put(MySQLiteHelper.COLUMN_HEIGHT, loc.getAltitude());
    values.put(MySQLiteHelper.COLUMN_TIME, loc.getTime());
    values.put(MySQLiteHelper.COLUMN_SPEED, loc.getSpeed());
    database.insert(MySQLiteHelper.TABLE_POSITIONS, null,values);
  }

  //TODO fix this to work with multiple tables.
  public void deletePosition(Position position) {
    long id = position.getId();
    System.out.println("Position deleted with id: " + id);
    database.delete(MySQLiteHelper.TABLE_POSITIONS, MySQLiteHelper.COLUMN_ID
        + " = " + id, null);
  }
  
  public void setRunName(String runName){
	  run = runName;
	  
	  ContentValues values = new ContentValues();
	  values.put(MySQLiteHelper.COLUMN_RUN_NAME, runName);
	  values.put(MySQLiteHelper.COLUMN_RUN_ID, getTableSize(MySQLiteHelper.TABLE_RUNS));
	  database.insert(MySQLiteHelper.TABLE_RUNS,null,values);
	  
	  Log.w("SetRunName","id is: " + getLastID(runName));
  }
  
  //TODO delete this if I don't need it after refactoring. //set the run name prior to calling this method.
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
  public List<Position> getAllPositions(String runName) {
    List<Position> positions = new ArrayList<Position>();
    int id = getLastID(runName);

    Cursor cursor = database.query(MySQLiteHelper.TABLE_POSITIONS,
        allColumns, MySQLiteHelper.COLUMN_ID + "=" + id, null, null, null, null);

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
    return position;
  }
  
  //adds the data from the current run to the Stats table
  private void addDataToStatistics(String runName){
	  int id = getLastID(runName);
	  long time = 0, date = 0;
	  double altitude = 0;
	  float speed = 0;
	  ContentValues values = new ContentValues();
	  Cursor cursor = database.query(MySQLiteHelper.TABLE_POSITIONS, allColumns, MySQLiteHelper.COLUMN_ID + "=" + id,
			  null, null, null, null);
	  
	  cursor.moveToFirst();
	  while (!cursor.isAfterLast()) {
		  if (cursor.isFirst()) time = cursor.getLong(4);
		  if (altitude < cursor.getDouble(3)) altitude = cursor.getDouble(3);
		  if (speed < cursor.getFloat(5)) speed = cursor.getFloat(5);
		  if (cursor.isLast()) {time = cursor.getLong(4) - time; date = cursor.getLong(4);}
		  cursor.moveToNext();
	  }
	  cursor.close();
	  
	  values.put(MySQLiteHelper.COLUMN_RUN, run);
	  values.put(MySQLiteHelper.COLUMN_HIGHEST_SPEED, speed);
	  values.put(MySQLiteHelper.COLUMN_BEST_TIME, time);
	  values.put(MySQLiteHelper.COLUMN_HIGHEST_ALTITUDE, altitude);
	  values.put(MySQLiteHelper.COLUMN_DATE, date);
	  database.insert(MySQLiteHelper.TABLE_STATS, null, values);
	  Log.w("PositionsDataSource","highest speed: "+ speed + ". time: " + time / 1000 + " seconds. highest altitude: " + altitude);
  }
  //TODO update this to work with new positions table. //deletes all entries without deleting the table.
  public void deleteAllEntries(){
	  Cursor cursor = database.query(run, allColumns, null, null, null, null, null);
	  cursor.moveToFirst();
	  while (!cursor.isAfterLast()){
		  database.delete(run, MySQLiteHelper.COLUMN_ID + "=" + cursor.getLong(0), null);
		  cursor.moveToNext();
	  }
	  cursor.close();
  }
  //total time in Seconds
  public Long totalTime(String runName){
	 int id = getLastID(runName);
	 Long time = (long) 0;
	 String[] column = {MySQLiteHelper.COLUMN_TIME};
	 Cursor cursor = database.query(MySQLiteHelper.TABLE_POSITIONS, column, MySQLiteHelper.COLUMN_ID + "=" + id,
			 null, null, null, null);
	 
	 if (cursor.getCount() != 0){
		 cursor.moveToFirst();
		 time = cursor.getLong(0);
		 cursor.moveToLast();
		 time = cursor.getLong(0) - time;
		 time = time / 1000; //convert to seconds.
	 }
	 cursor.close();
	 return time;
  }
  
  public double highest(String runName,String column){
	  String[] columns = {column, MySQLiteHelper.COLUMN_DATE};
	  double spdOrAlt;
	  Log.w("highest","runName is: " + runName);
	  Cursor cursor = database.query(MySQLiteHelper.TABLE_STATS, columns , MySQLiteHelper.COLUMN_RUN + "=" + "'"+runName+"'", 
			  null, null, null, null);
	  cursor.moveToLast();
	  spdOrAlt = cursor.getDouble(0);
	  cursor.close();
	  Log.w("PositionsDataSource", "highest Speed is: " + spdOrAlt);
	  return spdOrAlt;
  }
  
  public double totalDistance(String runName){
	 List<Position> positions = getAllPositions(runName);
	 Position position;
	 double totalDistance = 0;
	 int i;
	 for (i=0;i<positions.size()-1;i++){
		 position = positions.get(i);
		 totalDistance += position.distanceTo(positions.get(i+1));
	 }
	 return totalDistance;
  }
  
  public double averageSpeed(String runName){
	  return totalDistance(runName) / totalTime(runName);
  }
  
  public Point[] timeVsNumber(String runName){
	  Point[] points;
	  int i;
	  String[] columns = {MySQLiteHelper.COLUMN_BEST_TIME};
	  Cursor cursor = database.query(MySQLiteHelper.TABLE_STATS, columns , MySQLiteHelper.COLUMN_RUN + "=" + "'"+runName+"'", 
			  null, null, null, null);
	  cursor.moveToFirst();
	  Log.w("PositionsDataSource","cursor count is: " + cursor.getCount());
	  points = new Point[cursor.getCount()];
	  for (i=0;i<cursor.getCount();i++){
		  points[i] = new Point(i,cursor.getLong(0) / 1000);
		  cursor.moveToNext();
	  }
	  cursor.close();
	  return points;
  }
  
  private int getTableSize(String table){
	 Cursor cursor = database.query(table, null, null, null, null, null, null);
	 Log.w("getTableSize","table size of " + table + "is: " + cursor.getCount());
	 return cursor.getCount();
  }
  
  private int[] getIDs(String runName){
	  Cursor cursor = database.query(MySQLiteHelper.TABLE_RUNS, null,
			  MySQLiteHelper.COLUMN_RUN_NAME + "=" + "'"+runName+"'", null, null, null, null);
	  Log.w("getIDs", "cursor count is: " + cursor.getCount());
	  int[] ids = new int[cursor.getCount()];
	  int i;
	  cursor.moveToFirst();
	  Log.w("getIDs", "The IDs are: ");
	  for (i=0;i<cursor.getCount();i++){
		  ids[i] = cursor.getInt(1);
		  Log.w("getIDs", "" + ids[i]);
		  cursor.moveToNext();
	  }
	  cursor.close();
	  return ids;
  }
  
  //returns the highest id with the same run name.
  private int getLastID(String runName){
	  return getTableSize(MySQLiteHelper.TABLE_RUNS)-1;
  }
  
  public LinkedList<String> getAllRuns(){
	  LinkedList<String> runs = new LinkedList<String>();
	  String[] columns = {MySQLiteHelper.COLUMN_RUN_NAME};
	  Cursor cursor = database.query(MySQLiteHelper.TABLE_RUNS, columns, 
			  null, null, null, null, null, null);
	  cursor.moveToFirst();
	  while(!cursor.isAfterLast()){
		  if (!runs.contains(cursor.getString(0))) runs.add(cursor.getString(0));
		  cursor.moveToNext();
	  }
	  return runs;
  }
  
  public void done(String runName){
	  //add best time, speed, and altitude to statistics table
	  addDataToStatistics(runName);
	  //delete data from current run table
	  //deleteAllEntries();
	  close(); //Maybe?
  }
} 
