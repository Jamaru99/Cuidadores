package com.delicia.cuidadores;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class DbHelper extends SQLiteOpenHelper {
    private static final String DB_NAME="Cuidadores";
    private static final int DB_VER = 9;
    public static final String TASK_TABLE="Task";
    public static final String HISTORY_TABLE="History";
    public static final String TASK_NAME = "TaskName";
    public static final String TASK_STATUS = "TaskStatus";
    public static final String TASK_FREQUENCY = "TaskDate";
    public static final String TASK_SPEC = "TaskSpec";
    public static final String TASK_PERIOD = "TaskPeriod";
    public static final String TASK_DATE = "TaskDate";
    public static final String TASK_TIME = "TaskTime";
    public static final String TASK_RESULT = "TaskResult";

    private static final String TASK_TABLE_CREATE =
            "create table " + TASK_TABLE + " ("
                    + "ID" + " integer primary key autoincrement, "
                    + TASK_NAME + " text not null, "
                    + TASK_STATUS + " integer not null,"
                    + TASK_FREQUENCY + " integer not null,"
                    + TASK_PERIOD + " integer, "
                    + TASK_SPEC + " text not null);";

    private static final String HISTORY_TABLE_CREATE = 
            "create table " + HISTORY_TABLE + " ("
                    + "ID" + " integer primary key autoincrement, "
                    + TASK_NAME + " text not null, "
                    + TASK_DATE + " text not null, "
                    + TASK_TIME + " text not null, "
                    + TASK_RESULT + " text);";

    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

    
    public DbHelper(Context context) {
        super(context, DB_NAME, null, DB_VER);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(TASK_TABLE_CREATE);
        db.execSQL(HISTORY_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE Task;");
        db.execSQL("DROP TABLE History;");
        onCreate(db);
    }

    public void insertNewTask(String task, int status, int frequency, String spec, int period){
        SQLiteDatabase db= this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TASK_NAME,task);
        values.put(TASK_STATUS, status);
        values.put(TASK_FREQUENCY, frequency);
        values.put(TASK_SPEC, spec);
        values.put(TASK_PERIOD, period);
        db.insert(TASK_TABLE, null, values);
        db.close();
    }

    public void concludeTask(String task, String result){
        String date = dateFormat.format(new Date());
        String time = timeFormat.format(new Date());
        SQLiteDatabase db= this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TASK_NAME,task);
        values.put(TASK_DATE, date);
        values.put(TASK_TIME, time);
        values.put(TASK_RESULT, result);
        db.insert(HISTORY_TABLE, null, values);
        String query = "SELECT * FROM " + TASK_TABLE + " WHERE " + TASK_NAME + " = ?" ;
        Cursor cursor = db.rawQuery(query, new String[]{task});
        cursor.moveToFirst();
        int frequency = cursor.getInt(cursor.getColumnIndex(TASK_FREQUENCY));
        values = new ContentValues();
        if(frequency == 2){
            int spec = Integer.parseInt(cursor.getString(cursor.getColumnIndex(TASK_SPEC)));
            if(spec > 1){
                spec--;
                values.put(TASK_SPEC, spec);
                db.update(TASK_TABLE, values, TASK_NAME + " = ?", new String[]{task});
            } else {
                values.put(TASK_STATUS, 0);
                db.update(TASK_TABLE, values, TASK_NAME + " = ?", new String[]{task});
            }
        } else {
            values.put(TASK_STATUS, 0);
            db.update(TASK_TABLE, values, TASK_NAME + " = ?", new String[]{task});
        }

        cursor.close();
        db.close();
    }

    public void updateTask(String oldTask, String newTask, int status, int frequency, String spec, int period){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TASK_NAME, newTask);
        values.put(TASK_STATUS, status);
        values.put(TASK_FREQUENCY, frequency);
        values.put(TASK_SPEC, spec);
        values.put(TASK_PERIOD, period);
        db.update(TASK_TABLE, values, TASK_NAME + " = ?", new String[]{oldTask});
        db.close();
    }

    public void resetDay(){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TASK_STATUS, 1);
        Cursor cursor = db.query(TASK_TABLE,new String[]{TASK_PERIOD},null,null,null,null,null);
        while(cursor.moveToNext()){
            int period = cursor.getInt(cursor.getColumnIndex(TASK_PERIOD));
            if(period > 0) period--;
            values.put(TASK_PERIOD, period);
        }
        db.update(TASK_TABLE, values, null, null);
        cursor.close();
        db.close();
    }

    public void deleteTask(String task){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TASK_TABLE, TASK_NAME + " = ?",new String[]{task});
        db.close();
    }

    String getWeekDay(){
        int index = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        if(index == 1) return "Domingo";
        if(index == 2) return "Segunda-feira";
        if(index == 3) return "Terça-feira";
        if(index == 4) return "Quarta-feira";
        if(index == 5) return "Quinta-feira";
        if(index == 6) return "Sexta-feira";
        if(index == 7) return "Sábado";
        return "";
    }

    public ArrayList<String> getTaskDayList(){
        ArrayList<String> taskList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TASK_TABLE,new String[]{TASK_NAME, TASK_STATUS, TASK_FREQUENCY, TASK_SPEC},null,null,null,null,null);
        Calendar cal = Calendar.getInstance();

        while(cursor.moveToNext()){
            int indexStatus = cursor.getColumnIndex(TASK_STATUS);
            int indexName = cursor.getColumnIndex(TASK_NAME);
            if(cursor.getInt(indexStatus) == 1){
                switch(cursor.getInt(cursor.getColumnIndex(TASK_FREQUENCY))){
                    case 1:
                        String date = dateFormat.format(new Date());
                        if(cursor.getString(cursor.getColumnIndex(TASK_SPEC)).equals(date)){
                            taskList.add(cursor.getString(indexName));
                        }
                        break;
                    case 2:
                        taskList.add(cursor.getString(indexName) + "-" + cursor.getString(cursor.getColumnIndex(TASK_SPEC)) + " restantes");
                        break;
                    case 3:
                        String weekDay = getWeekDay();
                        if(cursor.getString(cursor.getColumnIndex(TASK_SPEC)).contains(weekDay)){
                            taskList.add(cursor.getString(indexName));
                        }
                        break;
                    case 4:
                        int d = cal.get(Calendar.DAY_OF_MONTH);
                        String dayMonth = "" + d;
                        if(cursor.getString(cursor.getColumnIndex(TASK_SPEC)).equals(dayMonth)) {
                            taskList.add(cursor.getString(indexName));
                        }
                        break;
                }
            }
        }
        cursor.close();
        db.close();
        return taskList;
    }

    public ArrayList<String> getAllTaskList(){
        ArrayList<String> taskList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TASK_TABLE,new String[]{TASK_NAME, TASK_SPEC, TASK_PERIOD, TASK_FREQUENCY},null,null,null,null,null);
        while(cursor.moveToNext()){
            int indexName = cursor.getColumnIndex(TASK_NAME);
            int indexSpec = cursor.getColumnIndex(TASK_SPEC);
            int indexPeriod = cursor.getColumnIndex(TASK_PERIOD);
            switch(cursor.getInt(cursor.getColumnIndex(TASK_FREQUENCY))){
                case 1:
                    taskList.add(cursor.getString(indexName) + "-" + cursor.getString(indexSpec));
                    break;
                case 2:
                    if(cursor.getInt(indexPeriod) > 0)
                        taskList.add(cursor.getString(indexName) + "-" + cursor.getString(indexSpec) + "x por dia-" + cursor.getInt(indexPeriod) + " dias restantes");
                    else
                        taskList.add(cursor.getString(indexName) + "-" + cursor.getString(indexSpec) + "x por dia");
                    break;
                case 3:
                    if(cursor.getInt(indexPeriod) > 0)
                        taskList.add(cursor.getString(indexName) + "-" + cursor.getString(indexSpec) + "-" + cursor.getInt(indexPeriod) + " dias restantes");
                    else
                        taskList.add(cursor.getString(indexName) + "-" + cursor.getString(indexSpec));
                    break;
                case 4:
                    if(cursor.getInt(indexPeriod) > 0)
                        taskList.add(cursor.getString(indexName) + "-Todo dia " + cursor.getString(indexSpec) + "-" + cursor.getInt(indexPeriod) + " dias restantes");
                    else
                        taskList.add(cursor.getString(indexName) + "-Todo dia " + cursor.getString(indexSpec));
                    break;
            }

        }
        cursor.close();
        db.close();
        return taskList;
    }

    public ArrayList<String> getNameList(){
        ArrayList<String> taskList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TASK_TABLE,new String[]{TASK_NAME},null,null,null,null,null);
        while(cursor.moveToNext()){
            int indexName = cursor.getColumnIndex(TASK_NAME);
            taskList.add(cursor.getString(indexName));
        }
        cursor.close();
        db.close();
        return taskList;
    }

    //-------------------------------HISTÓRICO-----------------------------------------

    public ArrayList<String> getHistoryList(){
        ArrayList<String> taskList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(HISTORY_TABLE,new String[]{TASK_NAME, TASK_DATE, TASK_TIME, TASK_RESULT},null,null,null,null,null);
        while(cursor.moveToNext()){
            int indexName = cursor.getColumnIndex(TASK_NAME);
            int indexDate = cursor.getColumnIndex(TASK_DATE);
            int indexTime = cursor.getColumnIndex(TASK_TIME);
            int indexResult = cursor.getColumnIndex(TASK_RESULT);

            taskList.add(cursor.getString(indexName)+ "-" + cursor.getString(indexDate) +
                    "-" + cursor.getString(indexTime) + "\n" + cursor.getString(indexResult));
        }
        cursor.close();
        db.close();
        return taskList;
    }

    public void clearHistory(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DROP TABLE " + HISTORY_TABLE + ";";
        db.execSQL(query);
        db.execSQL(HISTORY_TABLE_CREATE);
        db.close();
    }

    public void updateHistory(String s, String date, String time, String result){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TASK_DATE, date);
        values.put(TASK_TIME, time);
        values.put(TASK_RESULT, result);
        db.update(HISTORY_TABLE, values, TASK_NAME + " = ?", new String[]{s});
        db.close();
    }

    public void deleteHistory(String s){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(HISTORY_TABLE, TASK_NAME + " = ?",new String[]{s});
        db.close();
    }
}