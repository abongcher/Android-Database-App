package in.abongcher.tbec;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by abongcher on 28/6/17.
 */
public class logHandler extends SQLiteOpenHelper {


    public static final String LOG_DATABASE_NAME = "LOG.db";
    public static final String LOG_TABLE= "logHandler";
    public static final int LOG_VERSION = 1;

    public static final String _ID = "id";
    public static final String _LOG = "log";

    //SQL Statement to create a new database.
    private static final String LOG_CREATE = "CREATE TABLE" + " " + LOG_TABLE + "(" + _ID + " " + "INTEGER PRIMARY KEY AUTOINCREMENT," + " " + _LOG + " text not null);";


    public logHandler(Context context) {
        super(context, LOG_DATABASE_NAME, null, LOG_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(LOG_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF IT EXISTS" + " " + LOG_TABLE);
        onCreate(sqLiteDatabase);
    }

    public SQLiteDatabase getWritable(){
        return this.getWritableDatabase();
    }



    public long AddLog(ContributorLogHolder lh){
        getWritable();
        ContentValues values = new ContentValues();
        values.put(_LOG, lh.getLog());
        long isAdded = getWritable().insert(LOG_TABLE, null, values);
        getWritable().close();
        return isAdded;
    }

    public Cursor getReadableCursor(){
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT  * FROM " + " " + LOG_TABLE, null);
    }

    public Cursor getWritableCursor(){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT  * FROM " + " " + LOG_TABLE, null);
    }

    public List<ContributorLogHolder> getAllLog(){
        List<ContributorLogHolder> logList = new ArrayList<ContributorLogHolder>();
        // Select All Query
        Cursor cursor = getWritableCursor();

        // looping through all rows and adding to list
        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()){
            ContributorLogHolder logholder = new ContributorLogHolder();
            logholder.setLog(cursor.getString(cursor.getColumnIndexOrThrow(_LOG))+"\n");
            logList.add(logholder);
        }
        cursor.close();
        // return contact list
        return logList;
    }

    public String getId(){
        StringBuffer id = new StringBuffer();
        // Select All Query
        Cursor cursor = getReadableCursor();

        // looping through all rows and adding to list

        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()){
            int currentId = cursor.getInt(cursor.getColumnIndexOrThrow(_ID));
            id.append(currentId);
        }
        cursor.close();
        // return contact list
        return id.toString();
    }


    public void clear() {
        getWritable().delete(LOG_TABLE, null, null);
        getWritable().close();
    }
}
