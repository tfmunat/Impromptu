package com.laserscorpion.toyapp2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by joel on 10/3/17.
 */

public class DatabaseHelper extends SQLiteOpenHelper {


    private static final String SQL_CREATE_NAMES=
            "CREATE TABLE names (id INTEGER PRIMARY KEY, name TEXT, uni TEXT)";

    private SQLiteDatabase db;

    public DatabaseHelper(Context context) {
        super(context, "Names.db", null, 1);
        db = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_NAMES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public long add(String name, String uni) {
        //SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put("name", name);
        values.put("uni", uni);
        long row = db.insert("names", null, values);
        return row;
    }

    public void update(int id, String name, String uni) {
        SQLiteDatabase db = getWritableDatabase();
    }

    public ArrayList<Person> getAllNames() {
        SQLiteDatabase db = getReadableDatabase();

        ArrayList<Person> people = new ArrayList<>();
        String query = "SELECT * FROM names";

        Cursor result = db.rawQuery(query, null);

        while (result.moveToNext()) {
            int id = result.getInt(result.getColumnIndex("id"));
            String name = result.getString(result.getColumnIndex("name"));
            String uni = result.getString(result.getColumnIndex("uni"));
            Person person = new Person();
            person.name = name;
            person.uni = uni;
            person.id = id;
            people.add(person);
        }
        result.close();

        return people;
    }

}
