package in.abongcher.tbec;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import android.database.sqlite.SQLiteDatabase;

import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;



/**
 * Created by root on 15/7/16.
 */
public class DataBaseHandler extends SQLiteOpenHelper {


    private final String name = "Name\t\t\t:";
    private final String gender = "Gender\t\t:";
    private final String mobilenumber = "Mobile\t\t:";
    private final String amount = "Amount\t:";
    private final String next = "\n";

    private long state;
    public StringBuffer _id = new StringBuffer();
    StringBuffer id;

    public static final String DATABASE_NAME = "TBEC.db";
    public static final String DATABASE_TABLE= "TBEC_Monthly_Contribution";
    public static final int DATABASE_VERSION = 1;

    public static final String KEY_ID = "_id";
    public static final String CUSTOMER_PHOTO = "Customer_Photo";
    public static final String CUSTOMER_NAME = "Customer_Name";
    public static final String CUSTOMER_GENDER = "Customer_Gender";
    public static final String CUSTOMER_MOBILE_NO = "Customer_Mobile_No";
    public static final String CONTRIBUTION_FEE = "Customer_Monthly_Fee";

    //SQL Statement to create a new database.
    private static final String DATABASE_CREATE = "CREATE TABLE" + " " + DATABASE_TABLE + "(" + KEY_ID + " " + "INTEGER PRIMARY KEY AUTOINCREMENT, " + " " + CUSTOMER_PHOTO +" " + "BLOB, " + " " +CUSTOMER_NAME + " " + "text not null, " + " " + CUSTOMER_GENDER + " " + " text not null, "  + " " + CUSTOMER_MOBILE_NO + " " + " text not null, " + " " + CONTRIBUTION_FEE + " int);";

    //create constructor here
    public DataBaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF IT EXISTS" + " " + DATABASE_TABLE);
        onCreate(db);
    }

    public SQLiteDatabase getWritable(){
        return this.getWritableDatabase();
    }

    public SQLiteDatabase getReadable(){
        return this.getReadableDatabase();
    }




    //Add customer
    public long isCustomerAdded(Contributor con) throws Exception{

        getWritable();
        ContentValues values = new ContentValues();
        values.put(CUSTOMER_PHOTO, con.getImage()); //Contributor Photo
        values.put(CUSTOMER_NAME, con.getName()); // Contributor Name
        values.put(CUSTOMER_GENDER, con.getGender()); // Contributor Gender
        values.put(CUSTOMER_MOBILE_NO, con.getMobileNo()); // Contributor Phone Number
        values.put(CONTRIBUTION_FEE, con.getAmount()); // Contributor monthly fee
        //Placing Exception in this place is faster
        //rather than placing at the start of this function
        try{
            if(con.getName().isEmpty()){
                throw new Exception("Blank Name: Input Name.");
            }
            else if(con.getMobileNo().isEmpty()){
                throw new Exception("Blank Mobile Number: Input Mobile Number.");
            }
            else if(con.getMobileNo().length() != 10){
                throw new Exception("Mobile Number Short: Input 10 digit number.");
            }
        }catch (Exception ex){
            throw ex;
        }
        state = getWritable().insertOrThrow(DATABASE_TABLE, null, values);
        getWritable().close();
        return state;
    }

    public String id(){
        return String.valueOf(_id);
    }

    //get the cursor
    public Cursor getReadableCursor(){
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT  * FROM " + " " + DATABASE_TABLE, null);
    }

    public Cursor getWritableCursor(){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT  * FROM " + " " + DATABASE_TABLE, null);
    }

    public int getIdIndex(){
        return getReadableCursor().getColumnIndexOrThrow(KEY_ID);
    }

    public int getImageIndex(){
        return getReadableCursor().getColumnIndexOrThrow(CUSTOMER_PHOTO);
    }

    public int getNameIndex(){
        return getReadableCursor().getColumnIndexOrThrow(CUSTOMER_NAME);
    }

    public int getGenderIndex(){
        return getReadableCursor().getColumnIndexOrThrow(CUSTOMER_GENDER);
    }

    public int getMobileIndex(){
        return getReadableCursor().getColumnIndexOrThrow(CUSTOMER_MOBILE_NO);
    }

    public int getAmountIndex(){
        return getReadableCursor().getColumnIndexOrThrow(CONTRIBUTION_FEE);
    }

    public String getAllId(){
        id = new StringBuffer();
        Cursor cursor = getReadableCursor();
        if(cursor != null && cursor.getCount() > 0) {
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                id.append(String.valueOf(Integer.parseInt(getReadableCursor().getString(getIdIndex()))) + "\n");
            }
        }
        cursor.close();
        return id.toString();
    }

    public List<Contributor> getAllData(){
        List<Contributor> contributorList = new ArrayList<Contributor>();
        // Select All Query
        Cursor cursor = getWritableCursor();

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Contributor contributor = new Contributor();
                contributor.setImage(cursor.getBlob(getImageIndex()));
                contributor.setName(cursor.getString(getNameIndex()));
                contributor.setGender(cursor.getString(getGenderIndex()));
                contributor.setMobileNo(cursor.getString(getMobileIndex()));
                contributor.setAmount(cursor.getInt(getAmountIndex()));
                // Adding contact to list
                contributorList.add(contributor);
            } while (cursor.moveToNext());
        }
        cursor.close();
        // return contact list
        return contributorList;
    }

    //Retreive data for storing in the sdcard
    public String get_TBEC_Data(){
        StringBuffer container = new StringBuffer();
        // Select All Query
        Cursor cursor = getWritableCursor();

        // looping through all rows and adding to stringBuffer
        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()){
            container.append(name+cursor.getString(getNameIndex())+next+gender+cursor.getString(getGenderIndex())+next+mobilenumber+cursor.getString(getMobileIndex())+next+amount+cursor.getInt(getAmountIndex())+next+"=========================\n\n");
        }
        cursor.close();
        // return contact list
        return container.toString();
    }


    //Retreive data for storing in the sdcard
    public List<String> getAllMobileNo(){
        List<String> container = new ArrayList<>();
        // Select All Query
        Cursor cursor = getWritableCursor();

        // looping through all rows and adding to stringBuffer
        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()){
            container.add(cursor.getString(getMobileIndex()));
        }
        cursor.close();
        // return contact list
        return container;
    }




    //get the cursor
    //quering customer detail by name
    public Cursor getQueryByName(String name){
        getWritable();
//        SQLiteDatabase db = this.getReadableDatabase();
        String countQuery = "SELECT * FROM " + " " + DATABASE_TABLE+ " " + "WHERE" + " " + CUSTOMER_NAME + " " + "= ? ";
        Cursor cursor = getWritable().rawQuery(countQuery, new String[] {name});
        return cursor;
    }

    public void deleteByName(String name){
        getWritable().delete(DATABASE_TABLE, CUSTOMER_NAME + " = ?", new String[]{name});
        getWritable().close();
    }

    public long updateBalance(Contributor con, String name){
        ContentValues values = new ContentValues();
        values.put(CONTRIBUTION_FEE, con.getAmount());
        synchronized (this) {
            // updating row
            state = getWritable().update(DATABASE_TABLE, values, CUSTOMER_NAME + " = ?", new String[]{name});
        }
        getWritable().close();
        return state;
    }


    // Deleting all customers data
    public void deleteTBECData() {
        getWritable().delete(DATABASE_TABLE, null, null);
        getWritable().close();
    }

    //Quering contribution fee by mobile number of contributor
    public Cursor queryByNumber(String mobileNo){
        //SQLiteDatabase db = this.getReadableDatabase();
        String sqlQuery = "SELECT * FROM " + " " + DATABASE_TABLE+ " " + "WHERE" + " " + CUSTOMER_MOBILE_NO + " " + "= ? ";
        Cursor cursor = getReadable().rawQuery(sqlQuery, new String[] {mobileNo});
        return cursor;
    }


    public String getQuiryBalance(String mobileno){
        StringBuffer bal = new StringBuffer();
        // Select All Query
        Cursor cursor = queryByNumber(mobileno);
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            bal.append(cursor.getInt(getAmountIndex()));
        }
        cursor.close();
        // return contact list
        return bal.toString();
    }


    public String getQuiryGender(String mobileno){
        StringBuffer gen = new StringBuffer();
        Cursor cursor = queryByNumber(mobileno);
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            gen.append(cursor.getString(getGenderIndex()));
        }
        cursor.close();
        // return contact list
        return gen.toString();
    }


    public String getQuiryName(String mobileno){
        StringBuffer name = new StringBuffer();
        Cursor cursor = queryByNumber(mobileno);
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            name.append(cursor.getString(getNameIndex()));
        }
        cursor.close();
        // return contact list
        return name.toString();
    }

    // Updating single customer
    //self update by sending sms
    public long isMobileUpdated(String senderMobile, String updateMobile) {
        ContentValues values = new ContentValues();
        values.put(CUSTOMER_MOBILE_NO, updateMobile);
        // updating row
        synchronized (this){
            state = getWritable().update(DATABASE_TABLE, values, CUSTOMER_MOBILE_NO + " = ?", new String[]{senderMobile});
        }
        getWritable().close();
        return state;
    }

    public String getNewMobileNumber(String name){
        StringBuffer number = new StringBuffer();
        Cursor cursor = getQueryByName(name);
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            number.append(cursor.getString(getMobileIndex()));
        }
        cursor.close();
        // return contact list
        return number.toString();
    }



}
