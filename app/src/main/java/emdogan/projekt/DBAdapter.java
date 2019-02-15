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
    static final String KEY_SHORTCUT = "shortcut";
    static final String TAG = "DBAdapter";

    static final String DATABASE_NAME = "MyDB";
    static final String DATABASE_TABLE = "predmeti";
    static final String DATABASE_TABLE2 = "raspored";
    static final String DATABASE_TABLE3 = "bodovi";
    static final String DATABASE_TABLE4 = "ljestvica";
    static final String DATABASE_TABLE5 = "todo";

    static final int DATABASE_VERSION = 3;

    static final String DATABASE_CREATE =
            "create table predmeti (_id integer primary key autoincrement, "
                    + "name text not null,"
                    + "shortcut text not null,"
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

    static final String KEY_ROWID3 = "_id";
    static final String KEY_TYPE = "type";
    static final String KEY_EARNED = "earned";
    static final String KEY_TOTAL = "total";
    static final String DATABASE_CREATE3 =
            "create table bodovi (_id integer primary key autoincrement, "
                    + "name text not null, "
                    + "type text not null,"
                    + "earned integer,"
                    + "total integer not null);";

    static final String KEY_DVA = "dva";
    static final String KEY_TRI = "tri";
    static final String KEY_CETIRI = "cetiri";
    static final String KEY_PET = "pet";
    static final String DATABASE_CREATE4 =
            "create table ljestvica (name String primary key, "
                    + "dva integer not null, "
                    + "tri integer not null,"
                    + "cetiri integer not null,"
                    + "pet integer not null);";

    static final String KEY_PORUKA = "poruka";
    static final String KEY_CHECKED = "checked"; //0 nije checked, 1 je
    static final String DATABASE_CREATE5 =
            "create table todo (_id integer primary key autoincrement, "
                    + "poruka text not null, "
                    + "checked int not null);";
    static final String KEY_DAN = "dan";
    static final String KEY_MJ = "mjesec";
    static final String KEY_GOD = "godina";
    static final String KEY_SAT = "sat";
    static final String KEY_MIN = "minuta";
    static final String DATABASE_CREATE6 =
            "create table obaveze (_id integer primary key autoincrement, "
                + "dan int not null, "
                + "mjesec int not null, "
                + "godina int not null, "
                + "sat int not null, "
                + "minuta int not null, "
                + "poruka string not null);";
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
                db.execSQL(DATABASE_CREATE3);
                db.execSQL(DATABASE_CREATE4);
                db.execSQL(DATABASE_CREATE5);
                db.exexSQL(DATABASE_CREATE6);
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
    public long insertSubject(String name, String shortcut, String color)
    {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_NAME, name);
        initialValues.put(KEY_COLOR, color);
        initialValues.put(KEY_SHORTCUT, shortcut);
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
                                KEY_NAME, KEY_SHORTCUT, KEY_COLOR}, KEY_ROWID + "=" + rowId, null,
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
                                KEY_NAME, KEY_SHORTCUT, KEY_COLOR}, KEY_NAME + "='" + name + "'", null,
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

    public Cursor getAllBounds()
    {
        return db.query(DATABASE_TABLE4, new String[] {KEY_SUBJECTID, KEY_DVA, KEY_TRI, KEY_CETIRI, KEY_PET
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

    public long insertTODO(String poruka)
    {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_PORUKA, poruka);
        initialValues.put(KEY_CHECKED, 0);
        return db.insert(DATABASE_TABLE5, null, initialValues);
    }

    public boolean deleteTODO(String s)
    {
        return db.delete(DATABASE_TABLE5, KEY_PORUKA + "='" + s + "'", null) > 0;
    }

    public Cursor getAllTODO()
    {
        return db.query(DATABASE_TABLE5, new String[] {KEY_ROWID, KEY_PORUKA, KEY_CHECKED
        }, null, null, null, null, null);
    }

    public boolean updateTODOPoruka(String poruka, String novaporuka)
    {
        ContentValues args = new ContentValues();
        args.put(KEY_PORUKA, novaporuka);
        return db.update(DATABASE_TABLE5, args, KEY_PORUKA + "='" + poruka + "'", null) > 0;
    }
    public boolean updateTODOChecked(String poruka, int checked){
        ContentValues args = new ContentValues();
        args.put(KEY_CHECKED, checked);
        return db.update(DATABASE_TABLE5, args, KEY_PORUKA + "='" + poruka + "'", null) > 0;
    }

    public long insertInScores(String name, String type, int total) {
        ContentValues initialValues = new ContentValues();

        initialValues.put(KEY_NAME, name);
        initialValues.put(KEY_TYPE, type);
        initialValues.put(KEY_TOTAL, total);
        initialValues.put(KEY_EARNED, 0);

        return db.insert(DATABASE_TABLE3, null, initialValues);
    }

    public Cursor getAllScoresEntries()
    {
        return db.query(DATABASE_TABLE3, new String[] {KEY_ROWID3, KEY_NAME, KEY_TYPE, KEY_EARNED, KEY_TOTAL}, null, null, null, null, null);
    }

    public Cursor getTypesByName(String name) throws SQLException
    {
        Cursor mCursor =
                db.query(DATABASE_TABLE3, new String[] {KEY_ROWID3, KEY_TYPE, KEY_EARNED, KEY_TOTAL},
                        KEY_NAME + "='" + name + "'", null, null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    public Cursor getBounds(String name) throws SQLException
    {
        Cursor mCursor =
                db.query(DATABASE_TABLE4, new String[] {KEY_DVA, KEY_TRI, KEY_CETIRI, KEY_PET},
                        KEY_NAME + "='" + name + "'", null, null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    public boolean updateEarned(String name, String type, int earned)
    {
        ContentValues args = new ContentValues();
        args.put(KEY_EARNED, earned);
        return db.update(DATABASE_TABLE3, args, KEY_NAME + "='" + name + "'" + " AND " + KEY_TYPE + "='" + type + "'", null) > 0;
    }

    public long insertBounds(String name, int dva, int tri, int cetiri, int pet)
    {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_NAME, name);
        initialValues.put(KEY_DVA, dva);
        initialValues.put(KEY_TRI, tri);
        initialValues.put(KEY_CETIRI, cetiri);
        initialValues.put(KEY_PET, pet);
        return db.insert(DATABASE_TABLE4, null, initialValues);
    }

    public Cursor getTotals(String name) throws SQLException
    {
        Cursor mCursor =
                db.query(DATABASE_TABLE3, new String[] {KEY_TOTAL},
                        KEY_NAME + "='" + name + "'", null, null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    public Cursor getEarned(String name) throws SQLException
    {
        Cursor mCursor =
                db.query(DATABASE_TABLE3, new String[] {KEY_EARNED},
                        KEY_NAME + "='" + name + "'", null, null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    public Cursor getEarnedForType(String name, String type) throws SQLException
    {
        Cursor mCursor =
                db.query(DATABASE_TABLE3, new String[] {KEY_EARNED},
                        KEY_NAME + "='" + name + "'" + " AND " + KEY_TYPE + "='" + type + "'", null, null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    public Cursor getTotalForType(String name, String type) throws SQLException
    {
        Cursor mCursor =
                db.query(DATABASE_TABLE3, new String[] {KEY_TOTAL},
                        KEY_NAME + "='" + name + "'" + " AND " + KEY_TYPE + "='" + type + "'", null, null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    public boolean deleteScore(String name, String type)
    {
        return db.delete(DATABASE_TABLE3, KEY_NAME + "='" + name + "'" + " AND " + KEY_TYPE + "='" + type + "'", null) > 0;
    }

    public int getGrade(String name, int current) throws SQLException
    {
        Cursor mCursor =
                db.query(DATABASE_TABLE4, new String[] {KEY_DVA, KEY_TRI, KEY_CETIRI, KEY_PET},
                        KEY_NAME + "='" + name + "'", null, null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        int ret = 1;

        for (int i = 0; i < 4; i++)
        {
            if (current >= mCursor.getInt(i)) ret = i+2;
        }
        db.close();
        return ret;
    }

    public int getNumber(String name, int current, int grade) throws SQLException
    {
        Cursor mCursor =
                db.query(DATABASE_TABLE4, new String[] {KEY_DVA, KEY_TRI, KEY_CETIRI, KEY_PET},
                        KEY_NAME + "='" + name + "'", null, null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        grade = grade-2;
        int ret = mCursor.getInt(grade) - current;

        db.close();
        return ret;
    }
    
        public long insertObaveza(long date, int sat, int minuta, String text)
    {
        Calendar c = Calendar.getInstance(TimeZone.getDefault());
        c.setTimeInMillis(date);
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_DAN, c.get(Calendar.DAY_OF_MONTH));
        initialValues.put(KEY_MJ, c.get(Calendar.MONTH));
        initialValues.put(KEY_GOD, c.get(Calendar.YEAR));
        initialValues.put(KEY_SAT, sat);
        initialValues.put(KEY_MIN, minuta);
        initialValues.put(KEY_PORUKA, text);
        //Toast.makeText(, String.valueOf(c.get(Calendar.DAY_OF_MONTH)), Toast.LENGTH_SHORT).show();
        return db.insert(DATABASE_TABLE6, null, initialValues);
    }

    public long insertObaveza2(int dan, int mj, int god, int sat, int minuta, String text)
    {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_DAN, dan);
        initialValues.put(KEY_MJ, mj);
        initialValues.put(KEY_GOD, god);
        initialValues.put(KEY_SAT, sat);
        initialValues.put(KEY_MIN, minuta);
        initialValues.put(KEY_PORUKA, text);
        //Toast.makeText(, String.valueOf(c.get(Calendar.DAY_OF_MONTH)), Toast.LENGTH_SHORT).show();
        return db.insert(DATABASE_TABLE6, null, initialValues);
    }

    public Cursor getOnDay(long date)
    {
        Calendar c = Calendar.getInstance(TimeZone.getDefault());
        c.setTimeInMillis(date);

        Cursor mCursor =
                db.query(DATABASE_TABLE6, new String[] {KEY_SAT, KEY_MIN, KEY_PORUKA}, KEY_DAN + "=" + c.get(Calendar.DAY_OF_MONTH) + " AND " +
                        KEY_MJ + "=" + c.get(Calendar.MONTH) + " AND " + KEY_GOD + "=" + c.get(Calendar.YEAR), null, null, null, KEY_SAT + ", " + KEY_MIN);

        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }

    public Cursor getOnDay2(int year, int month, int day)
    {
        Cursor mCursor =
                db.query(DATABASE_TABLE6, new String[] {KEY_SAT, KEY_MIN, KEY_PORUKA}, KEY_DAN + "=" + day + " AND " +
                        KEY_MJ + "=" + month + " AND " + KEY_GOD + "=" + year, null, null, null, KEY_SAT + ", " + KEY_MIN);

        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }

}
