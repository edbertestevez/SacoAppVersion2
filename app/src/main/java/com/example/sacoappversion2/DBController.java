package com.example.sacoappversion2;

/**
 * Created by gestevez76 on 5/15/2017.
 */


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DBController extends SQLiteOpenHelper{

    private static final String TAG = "DBController";
    //Database infr
    private static final String DATABASE_NAME = "DbAccount";
    private static final int DATABASE_VERSION = 1;
    // Table name
    private static final String TABLE_SAVINGS = "tblSavings";
    private static final String TABLE_EXPENSES = "tblExpenses";
    private static final String TABLE_RETURN = "tblReturn";
    private static final String TABLE_BORROW = "tblBorrow";
    private static final String TABLE_CATEGORY = "tblCategory";

    // Define tb fields savings and expenses, borrow return
    public static final String ID = "_id";
    public static final String AMOUNT = "amount";
    public static final String DATE = "date";
    public static final String TIME = "time";
    public static final String CATEGORY = "category";
    //tblCategory Unique Fields
    public static final String CATNAME = "catname";
    //tblBorrowReturn Unique Fields
    public static final String BOR_NAME = "borName";
    public static final String NOTE = "note";
    public static final String RET_ID = "_id";
    public static final String RET_NAME = "retName";
    public static final String RET_CAT = "category";
    public static final String RET_NOTE = "note";
    public static final String RET_AMT = "amount";

    //OTHER SIGNIFICANT VARIABLES
    public static final String TOTAL_SUM = "total_sum";
    private static DBController mDBController;
    private SQLiteDatabase database;


    public static synchronized DBController getInstance(Context context){
        if(mDBController == null){
            mDBController = new DBController(context.getApplicationContext());
        }
        return mDBController;
    }

    private DBController(Context context){ super(context,DATABASE_NAME,null,DATABASE_VERSION);}

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_SAVINGS_TABLE = "CREATE TABLE " + TABLE_SAVINGS + "(" + ID +
                " INTEGER PRIMARY KEY, " + AMOUNT + " DOUBLE," + DATE +" DATETIME DEFAULT CURRENT_DATE," + TIME +" DATETIME DEFAULT CURRENT_TIME"+")";
        String CREATE_CATEGORY_TABLE = "CREATE TABLE " + TABLE_CATEGORY + "(" + ID +" INTEGER PRIMARY KEY, " + CATNAME + " TEXT"+")";
        String CREATE_EXPENSES_TABLE = "CREATE TABLE " + TABLE_EXPENSES + "(" + ID +
                " INTEGER PRIMARY KEY, " + AMOUNT + " DOUBLE," + DATE +" DATETIME DEFAULT CURRENT_DATE," + TIME +" DATETIME DEFAULT CURRENT_TIME,"+ CATEGORY +" TEXT"+")";
        String CREATE_BORROW_TABLE = "CREATE TABLE " + TABLE_BORROW + "(" +
                ID + " INTEGER PRIMARY KEY, " +
                BOR_NAME + " TEXT," +
                CATEGORY +  " TEXT," +
                NOTE + " TEXT," + AMOUNT + " DOUBLE" + ")";
        String CREATE_RETURN_TABLE = "CREATE TABLE " + TABLE_RETURN + "(" +
                RET_ID + " INTEGER PRIMARY KEY, " +
                RET_NAME + " TEXT," +
                RET_CAT +  " TEXT," +
                RET_NOTE + " TEXT," + RET_AMT + " DOUBLE" + ")";

        db.execSQL(CREATE_SAVINGS_TABLE);
        db.execSQL(CREATE_CATEGORY_TABLE);
        db.execSQL(CREATE_EXPENSES_TABLE);
        db.execSQL(CREATE_BORROW_TABLE);
        db.execSQL(CREATE_RETURN_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(oldVersion != newVersion){
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_SAVINGS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORY);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXPENSES);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_BORROW);
            db.execSQL("DROP TABLE IF EXISTS" + TABLE_RETURN);
            onCreate(db);
        }
    }

    //BORROW AND RETURN
    public void insertContentBorrow(BorrowData borrowData){
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try{
            ContentValues values = new ContentValues();
            values.put(ID, borrowData._id);
            values.put(BOR_NAME, borrowData.borname);
            values.put(CATEGORY, borrowData.category);
            values.put(AMOUNT, borrowData.amount);
            values.put(NOTE, borrowData.note);

            db.insertOrThrow(TABLE_BORROW,null,values);
            db.setTransactionSuccessful();
        }catch(Exception e){
            e.printStackTrace();
            Log.e(TAG, "Error while adding data.");
        }finally{
            db.endTransaction();
        }
    }

    public void insertContentReturn(ReturnData returnData){
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try{
            ContentValues values = new ContentValues();
            values.put(RET_ID, returnData._id);
            values.put(RET_NAME, returnData.retname);
            values.put(RET_CAT, returnData.category);
            values.put(RET_AMT, returnData.amount);
            values.put(NOTE, returnData.note);

            db.insertOrThrow(TABLE_RETURN,null,values);
            db.setTransactionSuccessful();
        }catch(Exception e){
            e.printStackTrace();
            Log.e(TAG, "Error while adding data.");
        }finally{
            db.endTransaction();
        }
    }

    //END HERE

    public String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "MMMM dd, yyyy", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    public String getDateFormat(String strDate) throws ParseException {
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-DD", Locale.getDefault());
        Date date = formatter.parse(strDate);
        SimpleDateFormat newFormat = new SimpleDateFormat("MMMM dd, yyyy");
        String sDate = newFormat.format(date);
        return sDate;
    }

    public String getStartMonth() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "MMMM 1, yyyy", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }
    public String getEndMonth() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "MMMM 31, yyyy", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }
    public String getTime(){
        SimpleDateFormat timeFormat = new SimpleDateFormat(
                "hh:mm:ss", Locale.getDefault());
        Date time = new Date();
        return timeFormat.format(time);
    }

    public void insertSavings(SavingsData savingsData){
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try{
            ContentValues values = new ContentValues();
            values.put(ID, savingsData._id);
            values.put(AMOUNT, savingsData.amount);
            values.put(DATE, getDateTime());
            values.put(TIME, getTime());

            db.insertOrThrow(TABLE_SAVINGS,null,values);
            db.setTransactionSuccessful();
        }catch(Exception e){
            e.printStackTrace();
            Log.e(TAG, "Error while adding data.");
        }finally{
            db.endTransaction();
        }
    }
    public void insertSyncSavings(SavingsData savingsData){
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try{
            ContentValues values = new ContentValues();
            values.put(ID, savingsData._id);
            values.put(AMOUNT, savingsData.amount);
            values.put(DATE, getDateFormat(savingsData.date));
            values.put(TIME, savingsData.time);

            db.insertOrThrow(TABLE_SAVINGS,null,values);
            db.setTransactionSuccessful();
        }catch(Exception e){
            e.printStackTrace();
            Log.e(TAG, "Error while adding data.");
        }finally{
            db.endTransaction();
        }
    }

    public void insertSyncExpense(ExpenseData expenseData){
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try{
            ContentValues values = new ContentValues();
            values.put(ID, expenseData._id);
            values.put(AMOUNT, expenseData.amount);
            values.put(DATE, getDateFormat(expenseData.date));
            values.put(TIME, expenseData.time);
            values.put(CATEGORY, expenseData.category);
            db.insertOrThrow(TABLE_EXPENSES,null,values);
            db.setTransactionSuccessful();
        }catch(Exception e){
            e.printStackTrace();
            Log.e(TAG, "Error while adding data.");
        }finally{
            db.endTransaction();
        }
    }

    //SAMPLE
    public void insertSavingsSample(SavingsData savingsData){
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try{
            ContentValues values = new ContentValues();
            values.put(ID, savingsData._id);
            values.put(AMOUNT, savingsData.amount);
            values.put(DATE, "February 10, 2017");
            values.put(TIME, getTime());

            db.insertOrThrow(TABLE_SAVINGS,null,values);
            db.setTransactionSuccessful();
        }catch(Exception e){
            e.printStackTrace();
            Log.e(TAG, "Error while adding data.");
        }finally{
            db.endTransaction();
        }
    }
    public void insertExpenseSample(ExpenseData expenseData){
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try{
            ContentValues values = new ContentValues();
            values.put(ID, expenseData._id);
            values.put(AMOUNT, "50");
            values.put(DATE, "May 10, 2015");
            values.put(TIME, getTime());
            values.put(CATEGORY, "1");
            db.insertOrThrow(TABLE_EXPENSES,null,values);
            db.setTransactionSuccessful();
        }catch(Exception e){
            e.printStackTrace();
            Log.e(TAG, "Error while adding data.");
        }finally{
            db.endTransaction();
        }
    }
    //END OF SAMPLE
    public void insertExpense(ExpenseData expenseData){
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try{
            ContentValues values = new ContentValues();
            values.put(ID, expenseData._id);
            values.put(AMOUNT, expenseData.amount);
            values.put(DATE, getDateTime());
            values.put(TIME, getTime());
            values.put(CATEGORY, expenseData.category);
            db.insertOrThrow(TABLE_EXPENSES,null,values);
            db.setTransactionSuccessful();
        }catch(Exception e){
            e.printStackTrace();
            Log.e(TAG, "Error while adding data.");
        }finally{
            db.endTransaction();
        }
    }

    public void insertCategory(CategoryData categoryData){
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try{
            ContentValues values = new ContentValues();
            values.put(ID, categoryData._id);
            values.put(CATNAME, categoryData.catname);

            db.insertOrThrow(TABLE_CATEGORY,null,values);
            db.setTransactionSuccessful();
        }catch(Exception e){
            e.printStackTrace();
            Log.e(TAG, "Error while adding data.");
        }finally{
            db.endTransaction();
        }
    }
    //AMO NI MAGKWA SANG TOTAL BALANCE BY EDBERT LABLAB HAHAHA PINATAKA LNG NI PARA ISA NA LANG
    public double getTotalBalance(){
        SQLiteDatabase db = getReadableDatabase();
        //TOTAL SAVINGS
        String query_savings = "SELECT SUM("+AMOUNT+") AS sum_savings FROM "+TABLE_SAVINGS;
        Cursor sCursor = db.rawQuery(query_savings, null);
        if (sCursor !=null) {
            sCursor.moveToFirst();
        }

        //TOTAL EXPENSE
        String query_expense = "SELECT SUM("+AMOUNT+") AS sum_expenses FROM "+TABLE_EXPENSES;
        Cursor eCursor = db.rawQuery(query_expense, null);
        if (eCursor !=null) {
            eCursor.moveToFirst();
        }

        //TOTAL BORROW (I MINUS)
        String query_borrow = "SELECT SUM("+AMOUNT+") AS sum_borrowMinus FROM "+TABLE_BORROW+" WHERE "+CATEGORY+" ='Money'";
        Cursor bCursor = db.rawQuery(query_borrow, null);
        if (bCursor !=null) {
            bCursor.moveToFirst();
        }

        //TOTAL RETURN (I ADD)
        String query_return = "SELECT SUM("+RET_AMT+") AS sum_returnAdd FROM "+TABLE_RETURN+" WHERE "+CATEGORY+" ='Money'";
        Cursor rCursor = db.rawQuery(query_return, null);
        if (rCursor !=null) {
            rCursor.moveToFirst();
        }
        return sCursor.getDouble(sCursor.getColumnIndex("sum_savings"))+rCursor.getDouble(rCursor.getColumnIndex("sum_returnAdd"))-bCursor.getDouble(bCursor.getColumnIndex("sum_borrowMinus"))-eCursor.getDouble(eCursor.getColumnIndex("sum_expenses"));
    }
    ///END HERE BABY HAHAHA EDBERT WAS HERE



    //SAVINGS ACTIVITY
    public Cursor getAllSavings() {
        SQLiteDatabase db = getReadableDatabase();

        Cursor mCursor = db.query(TABLE_SAVINGS, new String[] {ID,AMOUNT,DATE,TIME},
                null,null,null,null,"DATE DESC,TIME DESC");
        // PARAMETER (TABLE_NAME, Table Columns, whereClaue, whereArgs, Group By, Having, Order By);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    public Cursor getTodaySavings() {
        SQLiteDatabase db = getReadableDatabase();
        String dateToday = getDateTime();
        String[] where={dateToday};
        Cursor mCursor = db.query(TABLE_SAVINGS, new String[] {ID,AMOUNT,DATE,TIME},
                DATE+"=?",where,null,null,"DATE DESC,TIME DESC");
        // PARAMETER (TABLE_NAME, Table Columns, whereClaue, whereArgs, Group By, Having, Order By);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    public Cursor getMonthSavings() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor mCursor = db.rawQuery("SELECT * FROM "+TABLE_SAVINGS+" WHERE "+DATE+" >= '"+getStartMonth()+"' AND "+DATE+" <= '"+getEndMonth()+"'",null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }
    public double totalMonthSavings() {
        SQLiteDatabase db = getReadableDatabase();
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "MMMM dd, yyyy", Locale.getDefault());
        Date date = new Date();
        String query = "SELECT SUM("+AMOUNT+") AS sum_savings FROM "+TABLE_SAVINGS+" WHERE "+DATE+" >= '"+getStartMonth()+"' AND "+DATE+" <= '"+getEndMonth()+"'";
        Cursor mCursor = db.rawQuery(query, null);
        if (mCursor !=null) {
            mCursor.moveToFirst();
            return mCursor.getDouble(mCursor.getColumnIndex("sum_savings"));
        }
        return 0;
    }

    public double totalAllSavings() {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT SUM("+AMOUNT+") AS sum_savings FROM "+TABLE_SAVINGS;
        Cursor mCursor = db.rawQuery(query, null);
        if (mCursor !=null) {
            mCursor.moveToFirst();
            return mCursor.getDouble(mCursor.getColumnIndex("sum_savings"));
        }
        return 0;
    }
    public double totalTodaySavings() {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT SUM(" + AMOUNT + ") AS sum_amount FROM " + TABLE_SAVINGS + " WHERE DATE ='"+getDateTime()+"'";
        Cursor rCursor = db.rawQuery(query, null);
        if (rCursor != null) {
            rCursor.moveToFirst();
            return rCursor.getDouble(rCursor.getColumnIndex("sum_amount"));
        }
        return 0;
    }
    //END OF SAVINGS ACTIVITY
    public Cursor fetchCategoryContents() {
        SQLiteDatabase db = getReadableDatabase();

        Cursor mCursor = db.query(TABLE_CATEGORY, new String[] {ID,CATNAME},
                null,null,null,null,null);

        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    //EXPENSE CURSOR RAWQUERY
    public Cursor getAllCategoryExpense() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor mCursor = db.rawQuery("SELECT "+TABLE_CATEGORY+"."+ID+", "+CATNAME+",  SUM("+AMOUNT+") as "+TOTAL_SUM+" FROM "+TABLE_EXPENSES+"," +TABLE_CATEGORY+" WHERE "+TABLE_EXPENSES+"."+CATEGORY+"="+TABLE_CATEGORY+"."+ID +" GROUP BY "+CATEGORY,null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }
    public Cursor getTodayCategoryExpense() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor mCursor = db.rawQuery("SELECT "+TABLE_CATEGORY+"."+ID+", "+CATNAME+",  SUM("+AMOUNT+") as "+TOTAL_SUM+" FROM "+TABLE_EXPENSES+"," +TABLE_CATEGORY+" WHERE "+TABLE_EXPENSES+"."+CATEGORY+"="+TABLE_CATEGORY+"."+ID +" AND "+DATE+"='"+getDateTime()+"' GROUP BY "+CATEGORY,null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    public Cursor getMonthCategoryExpense() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor mCursor = db.rawQuery("SELECT "+TABLE_CATEGORY+"."+ID+", "+CATNAME+",  SUM("+AMOUNT+") as "+TOTAL_SUM+" FROM "+TABLE_EXPENSES+"," +TABLE_CATEGORY+" WHERE "+TABLE_EXPENSES+"."+CATEGORY+"="+TABLE_CATEGORY+"."+ID +" AND "+DATE+" >= '"+getStartMonth()+"' AND "+DATE+" <= '"+getEndMonth()+"' GROUP BY "+CATEGORY,null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    public double totalAllExpense(){
        SQLiteDatabase db = getReadableDatabase();
        String query_expense = "SELECT SUM("+AMOUNT+") AS sum_expenses FROM "+TABLE_EXPENSES;
        Cursor eCursor = db.rawQuery(query_expense, null);
        if (eCursor !=null) {
            eCursor.moveToFirst();
        }
        return eCursor.getDouble(eCursor.getColumnIndex("sum_expenses"));
    }

    public double totalTodayExpense(){
        SQLiteDatabase db = getReadableDatabase();
        String query_expense = "SELECT SUM("+AMOUNT+") AS sum_expenses FROM "+TABLE_EXPENSES+" WHERE "+DATE+"='"+getDateTime()+"'";
        Cursor eCursor = db.rawQuery(query_expense, null);
        if (eCursor !=null) {
            eCursor.moveToFirst();
        }
        return eCursor.getDouble(eCursor.getColumnIndex("sum_expenses"));
    }
    public double totalMonthExpense() {
        SQLiteDatabase db = getReadableDatabase();
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "MMMM dd, yyyy", Locale.getDefault());
        Date date = new Date();
        String query = "SELECT SUM("+AMOUNT+") AS sum_savings FROM "+TABLE_EXPENSES+" WHERE "+DATE+" >= '"+getStartMonth()+"' AND "+DATE+" <= '"+getEndMonth()+"'";
        Cursor mCursor = db.rawQuery(query, null);
        if (mCursor !=null) {
            mCursor.moveToFirst();
            return mCursor.getDouble(mCursor.getColumnIndex("sum_savings"));
        }
        return 0;
    }

    public Cursor getAllCategorySpecific(String posid) {
        SQLiteDatabase db = getReadableDatabase();
        String[] where={posid}; //EDIT
        Cursor mCursor = db.query(TABLE_EXPENSES, new String[] {ID,AMOUNT,DATE,TIME,CATEGORY},
                CATEGORY + "=?", where, null, null, "DATE DESC, TIME DESC");
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    //END OF SAMPLE CURSOR


    public Cursor fetchSpecificCategory(String posid) {
        SQLiteDatabase db = getReadableDatabase();
        String[] where={posid};
        Cursor mCursor = db.query(TABLE_CATEGORY, new String[] {ID,CATNAME},
                ID+"=?",where,null,null,null);

        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    public Cursor fetchSpecificExpense(String posid) {
        SQLiteDatabase db = getReadableDatabase();
        String[] where={posid}; //EDIT
        Cursor mCursor = db.query(TABLE_EXPENSES, new String[] {ID,AMOUNT,DATE,TIME,CATEGORY},
                CATEGORY + "=?", where, null, null, "DATE DESC, TIME DESC");

        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    public double fetchTotalSpecificExpence(String posid) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT SUM("+AMOUNT+") AS sum_catex FROM "+TABLE_EXPENSES+" WHERE CATEGORY='"+posid+"'";
        Cursor mCursor = db.rawQuery(query, null);
        if (mCursor !=null) {
            mCursor.moveToFirst();
            return mCursor.getDouble(mCursor.getColumnIndex("sum_catex"));
        }else{
            return 0;
        }

    }

    public void deleteSavingsRecord(String posid) {
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL("DELETE FROM " + TABLE_SAVINGS + " WHERE " + ID + "= '" + posid + "'");
        database.close();
    }

    public void deleteExpenseRecord(String posid) {
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL("DELETE FROM " + TABLE_EXPENSES + " WHERE " + ID + "= '" + posid + "'");
        database.close();
    }

    //BORROW RETURN FUNCTIONS
    public Cursor fetchBorrowContents() {
        SQLiteDatabase db = getReadableDatabase();

        Cursor mCursor = db.query(TABLE_BORROW, new String[] {ID,BOR_NAME,CATEGORY,AMOUNT,NOTE},
                null,null,null,null,null);

        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    public Cursor fetchReturnContents() {
        SQLiteDatabase db = getReadableDatabase();

        Cursor mCursor = db.query(TABLE_RETURN, new String[] {RET_ID,RET_NAME,RET_CAT,RET_AMT,RET_NOTE},
                null,null,null,null,null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    /*public void updateBorrowRecord(String posid, int quantity) {
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL("UPDATE " + TABLE_BORROW + " SET "+AMOUNT+"="+AMOUNT+"-"+quantity +" WHERE " + ID + "= '" + posid + "'");
        database.close();
    }*/
    public void deleteBorrowRecord(String posid) {
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL("DELETE FROM " + TABLE_BORROW + " WHERE " + ID + "= '" + posid + "'");
        database.close();
    }
    public void deleteReturnRecord(String posid) {
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL("DELETE FROM " + TABLE_RETURN + " WHERE " + ID + "= '" + posid + "'");
        database.close();
    }
    public Cursor fetchSpecificBorrow(String posid) {
        SQLiteDatabase db = getReadableDatabase();
        String[] where={posid};
        Cursor mCursor = db.query(TABLE_BORROW, new String[] {ID,BOR_NAME,CATEGORY,AMOUNT,NOTE},
                ID+"=?",where,null,null,null);

        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }
    public Cursor fetchSpecificReturn(String posid) {
        SQLiteDatabase db = getReadableDatabase();
        String[] where={posid};
        Cursor mCursor = db.query(TABLE_RETURN, new String[] {RET_ID,RET_NAME,RET_CAT,RET_AMT,RET_NOTE},
                ID+"=?",where,null,null,null);

        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }
    public void editBorrowRecord(String posid, String borname, String note, double amount) {
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL("UPDATE " + TABLE_BORROW + " SET "+BOR_NAME+"= '"+borname+"', "+NOTE+"='"+note+"', "+AMOUNT+"='"+amount+"' WHERE " + ID + "= '" + posid + "'");
        database.close();
    }
    public void editReturnRecord(String posid, String borname, String note, double amount) {
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL("UPDATE " + TABLE_RETURN + " SET "+RET_NAME+"= '"+borname+"', "+RET_NOTE+"='"+note+"', "+RET_AMT+"='"+amount+"' WHERE " + RET_ID + "= '" + posid + "'");
        database.close();
    }

    public boolean ifCategoryExists(String catname)
    {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        String checkQuery = "SELECT " + CATNAME + " FROM " + TABLE_CATEGORY + " WHERE " + CATNAME + "= '"+catname + "'";
        cursor= db.rawQuery(checkQuery,null);
        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        return exists;
    }

    public double getCategoryCount() {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT COUNT(*) AS ctr FROM "+TABLE_CATEGORY;
        Cursor mCursor = db.rawQuery(query, null);
        if (mCursor !=null) {
            mCursor.moveToFirst();
            return mCursor.getDouble(mCursor.getColumnIndex("ctr"));
        }
        return 0;
    }

    public int totalBorrowRecord() {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT COUNT(*) AS ctr FROM "+TABLE_BORROW;
        Cursor mCursor = db.rawQuery(query, null);
        if (mCursor !=null) {
            mCursor.moveToFirst();
            return mCursor.getInt(mCursor.getColumnIndex("ctr"));
        }
        return 0;
    }
    public int totalReturnRecord() {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT COUNT(*) AS ctr FROM "+TABLE_RETURN;
        Cursor mCursor = db.rawQuery(query, null);
        if (mCursor !=null) {
            mCursor.moveToFirst();
            return mCursor.getInt(mCursor.getColumnIndex("ctr"));
        }
        return 0;
    }

    public void clearBorrow() {
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL("DELETE FROM " + TABLE_BORROW);
        database.close();
    }
    public void clearReturn() {
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL("DELETE FROM " + TABLE_RETURN);
        database.close();
    }
    public void clearSavings() {
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL("DELETE FROM " + TABLE_SAVINGS);
        database.close();
    }
    public void clearExpense() {
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL("DELETE FROM " + TABLE_EXPENSES);
        database.close();
    }
}
