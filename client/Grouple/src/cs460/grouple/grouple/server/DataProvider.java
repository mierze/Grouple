package cs460.grouple.grouple.server;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public class DataProvider extends ContentProvider {
 
    public static final String COL_ID = "_id";
     
    public static final String TABLE_MESSAGES = "messages";
    public static final String COL_MSG = "message";
    public static final String COL_SENDER = "sender";
    public static final String COL_RECEIVER = "receiver";
    public static final String COL_DATE = "date";
     
    public static final String TABLE_USERS = "users";
    public static final String COL_FIRST = "first";
    public static final String COL_LAST = "last";
    public static final String COL_EMAIL = "email";
    public static final String COL_COUNT = "count";                
           
    public static final Uri CONTENT_URI_MESSAGES = Uri.parse("content://com.appsrox.instachat.provider/messages");
    public static final Uri CONTENT_URI_USERS = Uri.parse("content://com.appsrox.instachat.provider/users");
     
    private static final int MESSAGES_ALLROWS = 1;
    private static final int MESSAGES_SINGLE_ROW = 2;
    private static final int USER_ALLROWS = 3;
    private static final int USER_SINGLE_ROW = 4;
     
    private static final UriMatcher uriMatcher;
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI("com.appsrox.instachat.provider", "messages", MESSAGES_ALLROWS);
        uriMatcher.addURI("com.appsrox.instachat.provider", "messages/#", MESSAGES_SINGLE_ROW);
        uriMatcher.addURI("com.appsrox.instachat.provider", "profile", USER_ALLROWS);
        uriMatcher.addURI("com.appsrox.instachat.provider", "profile/#", USER_SINGLE_ROW);
    }
    private DbHelper dbHelper;
 
    
    private static class DbHelper extends SQLiteOpenHelper {
        
        private static final String DATABASE_NAME = "grouple";
        private static final int DATABASE_VERSION = 1;
     
        public DbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
     
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("create table messages (_id integer primary key autoincrement, msg text, email text, email2 text, at datetime default current_timestamp);");
            db.execSQL("create table profile (_id integer primary key autoincrement, name text, email text unique, count integer default 0);");
        }
     
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }
    }
    
    @Override
    public boolean onCreate() {
        //dbHelper = new DbHelper(getContext());
        return true;
    }
 
    @Override
    public String getType(Uri uri) {
        return null;
    }

 
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
         
        switch(uriMatcher.match(uri)) {
        case MESSAGES_ALLROWS:
        case USER_ALLROWS:
            qb.setTables(getTableName(uri));
            break;
             
        case MESSAGES_SINGLE_ROW:
        case USER_SINGLE_ROW:
            qb.setTables(getTableName(uri));
            qb.appendWhere("_id = " + uri.getLastPathSegment());
            break;
             
        default:
            throw new IllegalArgumentException("Unsupported URI: " + uri);         
        }
         
        Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }
     
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
         
        long id;
        switch(uriMatcher.match(uri)) {
        case MESSAGES_ALLROWS:
            id = db.insertOrThrow(TABLE_MESSAGES, null, values);
            if (values.get(COL_RECEIVER) == null) {
                db.execSQL("update profile set count=count+1 where email = ?", new Object[]{values.get(COL_SENDER)});
                getContext().getContentResolver().notifyChange(CONTENT_URI_USERS, null);
            }
            break;
             
        case USER_ALLROWS:
            id = db.insertOrThrow(TABLE_USERS, null, values);
            break;
             
        default:
            throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
         
        Uri insertUri = ContentUris.withAppendedId(uri, id);
        getContext().getContentResolver().notifyChange(insertUri, null);
        return insertUri;
    }
     
    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
         
        int count;
        switch(uriMatcher.match(uri)) {
        case MESSAGES_ALLROWS:
        case USER_ALLROWS:
            count = db.update(getTableName(uri), values, selection, selectionArgs);
            break;
             
        case MESSAGES_SINGLE_ROW:
        case USER_SINGLE_ROW:
            count = db.update(getTableName(uri), values, "_id = ?", new String[]{uri.getLastPathSegment()});
            break;
             
        default:
            throw new IllegalArgumentException("Unsupported URI: " + uri);         
        }
         
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }
     
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
         
        int count;
        switch(uriMatcher.match(uri)) {
        case MESSAGES_ALLROWS:
        case USER_ALLROWS:
            count = db.delete(getTableName(uri), selection, selectionArgs);
            break;
             
        case MESSAGES_SINGLE_ROW:
        case USER_SINGLE_ROW:
        	//TODO: changed _id->email??
            count = db.delete(getTableName(uri), "email = ?", new String[]{uri.getLastPathSegment()});
            break;
             
        default:
            throw new IllegalArgumentException("Unsupported URI: " + uri);         
        }
         
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }
     
    private String getTableName(Uri uri) {
        switch(uriMatcher.match(uri)) {
        case MESSAGES_ALLROWS:
        case MESSAGES_SINGLE_ROW:
            return TABLE_MESSAGES;
             
        case USER_ALLROWS:
        case USER_SINGLE_ROW:
            return TABLE_USERS;          
        }
        return null;
    }
 

}