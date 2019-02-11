package emdogan.projekt;

/**
 * Created by emdogan on 2/8/19.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

public class DBAdapter {

    static final String KEY_ROWID = "_id";
    static final String KEY_NAME = "name";
    static final String KEY_COLOR = "color";
    static final String TAG = "DBAdapter";

    static final String DATABASE_NAME = "MyDB";
    static final String DATABASE_TABLE = "predmeti";
    static final String DATABASE_TABLE2 = "raspored";

    static final int DATABASE_VERSION = 1;

    static final String DATABASE_CREATE =
            "create table predmeti (_id integer primary key autoincrement, "
                    + "name text not null,"
                    + "color text not null);";


    static final String KEY_ROWID2 = "_id";
    static final String KEY_SUBJECTID = "idPredmeta";
    static final String KEY_DAY = "day";
    static final String KEY_TIME = "time";
    static final String DATABASE_CREATE2 =
            "create table raspored (_id integer primary key autoincrement, "
                    + "idPredmeta integer not null, "
                    + "day integer not null,"
                    + "time integer not null);";

    final Context context;

    DatabaseHelper DBHelper;
    SQLiteDatabase db;

    public DBAdapter(Context ctx)
    {
        this.context = ctx;
        DBHelper = new DatabaseHelper(context);
    }

    private static class DatabaseHelper extends SQLiteOpenHelper
    {
        DatabaseHelper(Context context)
        {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db)
        {
            try {
                db.execSQL(DATABASE_CREATE);
                db.execSQL(DATABASE_CREATE2);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
        {
            Log.w(TAG, "Upgrading db from" + oldVersion + "to"
                    + newVersion );
            db.execSQL("DROP TABLE IF EXISTS contacts");
            onCreate(db);
        }
    }

    //---opens the database---
    public DBAdapter open() throws SQLException
    {
        db = DBHelper.getWritableDatabase();
        return this;
    }

    //---closes the database---
    public void close()
    {
        DBHelper.close();
    }

    //---ubaci novi predmet s nazivom name---
    public long insertSubject(String name, String color)
    {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_NAME, name);
        initialValues.put(KEY_COLOR, color);
        return db.insert(DATABASE_TABLE, null, initialValues);
    }

    //---obriši predmet (red iz baze) po id-u---
    public boolean deleteSubject(long rowId)
    {
        return db.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
    }

    //---dohvati sve predmete---
    public Cursor getAllSubjects()
    {
        return db.query(DATABASE_TABLE, new String[] {KEY_ROWID, KEY_NAME
                    }, null, null, null, null, null);
    }


    //---dohvati određeni predmet---
    public Cursor getSubject(long rowId) throws SQLException
    {
        Cursor mCursor =
                db.query(true, DATABASE_TABLE, new String[] {KEY_ROWID,
                                KEY_NAME, KEY_COLOR}, KEY_ROWID + "=" + rowId, null,
                        null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    public Cursor getSubjectByName(String name) throws SQLException
    {
        Cursor mCursor =
                db.query(true, DATABASE_TABLE, new String[] {KEY_ROWID,
                                KEY_NAME, KEY_COLOR}, KEY_NAME + "='" + name + "'", null,
                        null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    //---ažuriraj predmet---
    public boolean updateSubject(long rowId, String name, String color)
    {
        ContentValues args = new ContentValues();
        args.put(KEY_NAME, name);
        args.put(KEY_COLOR, color);
        return db.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
    }


    public long insertInTimetable(int subjectId, int day, int time) {
        /*initialValues = ContentValues().apply {
            put(FeedEntry.COLUMN_NAME_TITLE, title)
            put(FeedEntry.COLUMN_NAME_SUBTITLE, subtitle)
        }*/

        ContentValues initialValues = new ContentValues();

        initialValues.put(KEY_SUBJECTID, subjectId);
        initialValues.put(KEY_DAY, day);
        initialValues.put(KEY_TIME, time);

        return db.insert(DATABASE_TABLE2, null, initialValues);
    }

    public Cursor getAllTimetableEntries()
    {
        return db.query(DATABASE_TABLE2, new String[] {KEY_SUBJECTID, KEY_DAY, KEY_TIME
        }, null, null, null, null, null);
    }

    public Cursor getEntry(long rowId, long columnId) throws SQLException
    {
        Cursor mCursor =
                db.query(true, DATABASE_TABLE2, new String[] {
                                KEY_SUBJECTID, KEY_DAY, KEY_TIME}, KEY_DAY + "=" + columnId + " AND " + KEY_TIME + "=" + (rowId+7), null,
                        null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    public boolean deleteTimetableEntry(int rowId, int columnId) {
        return db.delete(DATABASE_TABLE2, KEY_TIME + "=" + (rowId+7) + " AND " + KEY_DAY + "=" + columnId , null) > 0;
    }
}