package com.joevirn.joyce.melanie.ibm2105_group3_code;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class HistoryListOpenHelper extends SQLiteOpenHelper{
    private static final String DATABASE_NAME = "HistoryList";
    public static final int DB_VERSION = 1;
    private SQLiteDatabase mReadableDB;
    private SQLiteDatabase mWritableDB;
    
    public HistoryListOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sqlStatement1 = "CREATE TABLE user (userName TEXT PRIMARY KEY, " +
                "userPassword TEXT, historyID INTEGER)";
        db.execSQL(sqlStatement1);
        String sqlStatement2 = "CREATE TABLE history ( historyID INTEGER PRIMARY KEY, " +
                "date TEXT, " +
                "time TEXT, " +
                "totalRoundPlayed INTEGER, " +
                "totalRoundWon INTEGER, " +
                "winningPercentage REAL)";
        db.execSQL(sqlStatement2);
        fillDatabaseWithData(db);
    }

    private void fillDatabaseWithData(SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        values.put("historyID", 1);
        values.put("date", "11/11");
        values.put("time", "11:11");
        values.put("totalRoundPlayed", 1);
        values.put("totalRoundWon", 1);
        values.put("winningPercentage", 10.0);


        db.insert("history", null, values);

    }

    public HistoryItem query(int position){
        String sql = "Select * from word_entries " + "order by word ASC limit " + position
                + ",1";

        Cursor cursor = null;
        HistoryItem entry = new HistoryItem();

        try {
            if (mReadableDB == null) {
                mReadableDB = getReadableDatabase();
            }
            cursor = mReadableDB.rawQuery(sql, null);
            cursor.moveToFirst();
            entry.setHistoryID(cursor.getInt(cursor.getColumnIndex("historyID")));
            entry.setDate(cursor.getString(cursor.getColumnIndex("date")));
            entry.setTime(cursor.getString(cursor.getColumnIndex("time")));
            entry.setTotalRoundPlayed(cursor.getString(cursor.getColumnIndex("totalRoundPlayed")));
            entry.setTotalRoundWon(cursor.getString(cursor.getColumnIndex("totalRoundWon")));
            entry.setWinningPercentage(cursor.getDouble(cursor.getColumnIndex("winningPercentage")));

        }catch (Exception e){
            Log.d("Query Exception: ", e.getMessage());

        }finally {
            cursor.close();
            return entry;
        }
    }

    public long insertTableUser (int historyID, String userName, String userPassword){
        long newId = 0;

        ContentValues values = new ContentValues();
        values.put("historyID", historyID);
        values.put("userName", userName);
        values.put("userPassword", userPassword);

        try{
            if (mWritableDB == null){
                mWritableDB = getWritableDatabase();
            }
            newId = mWritableDB.insert("user", null, values);

        }catch (Exception e){

        }
        return newId;
    }

    public long insertTableHistory (String date, String time, String totalRoundPlayed,
                                    String totalRoundWon, String winningPercentage){
        long newId = 0;

        ContentValues values = new ContentValues();
        values.put("date", date);
        values.put("time", time);
        values.put("totalRoundPlayed", totalRoundPlayed);
        values.put("totalRoundWon", totalRoundWon);
        values.put("winningPercentage", winningPercentage);

        try{
            if (mWritableDB == null){
                mWritableDB = getWritableDatabase();
            }
            newId = mWritableDB.insert("history", null, values);

        }catch (Exception e){

        }
        return newId;
    }

    public int update (int historyId, String userName, String date, String time, String totalRoundPlayed,
                       String totalRoundWon, String winningPercentage){
        int numOfRowUpdate = -1;

        ContentValues values = new ContentValues();
        values.put("date", date);
        values.put("time", time);
        values.put("totalRoundPlayed", totalRoundPlayed);
        values.put("totalRoundWon", totalRoundWon);
        values.put("winningPercentage", winningPercentage);

        try{
            if (mWritableDB == null){
                mWritableDB = getWritableDatabase();
            }
            numOfRowUpdate = mWritableDB.update("history", values, "_id=?", new String[]{String.valueOf(historyId)});
        }catch (Exception e){
            Log.d("Updated Exception: ", e.getMessage());
        }

        return  numOfRowUpdate;
    }

    public boolean check(String username){
        boolean hasObject = false;
        String selectString = "SELECT * FROM user" + " WHERE userName" + " = " + username;
        //String sql = "SELECT count(*) FROM user WHERE userName = " + username;

        Cursor cursor = null;
        HistoryItem entry = new HistoryItem();

        try {
            if (mReadableDB == null) {
                mReadableDB = getReadableDatabase();
            }

            if (mReadableDB.rawQuery(selectString, null) == null){
                hasObject = false;
            }else{
                cursor = mReadableDB.rawQuery(selectString, null);
                cursor.moveToFirst();
                entry.setHUserName(cursor.getString(cursor.getColumnIndex("username")));

                if (username.equals(entry.getHUserName())){
                    hasObject = true;
                }else{
                    hasObject = false;
                }
            }
        }catch (Exception e){
            Log.d("Query Exception: ", e.getMessage());

        }finally {
            return hasObject;
            //cursor.close();
        }

        //cursor.close();          // Dont forget to close your cursor
        //mReadableDB.close();              //AND your Database!
        //return hasObject;
    }

    public boolean checkPassword(String username, String password){
        boolean success = false;
        HistoryItem entry = new HistoryItem();

        String selectString = "SELECT password FROM user" + " WHERE userName" + " =" + username;

        // Add the String you are searching by here.
        // Put it in an array to avoid an unrecognized token error
        Cursor cursor = mReadableDB.rawQuery(selectString, new String[] {password});

        if(cursor.moveToFirst()){
            if(password.equals(cursor.getColumnIndex(password))){
                success = true;
            }else{
                success = false;
            }

            int count = 0;
            while(cursor.moveToNext()){
                count++;
            }

            //endregion

        }
/*
        cursor.close();          // Dont forget to close your cursor
        mReadableDB.close();     */         //AND your Database!
        return success;
    }

    public long count(){
        if (mReadableDB == null){
            mReadableDB = getReadableDatabase();
        }
        return DatabaseUtils.queryNumEntries(mReadableDB, "user");
    }

    public HistoryItem displayRecord(int historyID) {
        String selectString = "SELECT * FROM history" + " WHERE historyID" + " =" + historyID;

        HistoryItem entry = new HistoryItem();

        Cursor cursor = mReadableDB.rawQuery(selectString, new String[]{historyID + ""});

        if (mReadableDB == null) {
            mReadableDB = getReadableDatabase();
        }

        if (mReadableDB.rawQuery(selectString, null) == null) {

        } else {
            cursor = mReadableDB.rawQuery(selectString, null);
            cursor.moveToFirst();
            entry.setTime(cursor.getString(cursor.getColumnIndex("time")));
            entry.setDate(cursor.getString(cursor.getColumnIndex("date")));
            entry.setTotalRoundPlayed(cursor.getString(cursor.getColumnIndex("totalRoundPlayed")));
            entry.setTotalRoundWon(cursor.getString(cursor.getColumnIndex("totalRoundWon")));
            entry.setWinningPercentage(cursor.getInt(Integer.parseInt("winningPercentage")));
        }
        return entry;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
