package myteam.startup.pages;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyTeamDB{

    private static final String DATABASE_NAME = "MyTeamDataBase.db";    
    private static final int DATABASE_VERSION = 1;
    public static final String PLAY="PLAY";
    public static final String NOT_PLAY="NOT PLAY";
    private final String CODE="1";
    private final static String MyTeamRememberTable="MyTeamRememberMe";
    private final static String MyTeamSettingsTable="MyTeamSettings";
    
    private String StoredUserName=null;
    private String StoredPassWord=null;
    private String StoredVibrate=null;
    private String StoredSound=null;
    
    public DatabaseHelper DBHelper;
    private SQLiteDatabase db;
	private Context context;

//MemberData	
	public static final String MD_Name = "Name";

    
    private static final String CREATE_TABLE_REMEMBER ="create table MyTeamRememberMe (CODE text not null,USERNAME text not null, PASSWORD text not null);";

    private static final String CREATE_TABLE_SETTINGS="create table MyTeamSettings(CODE text not null,SOUND text not null,VIBRATE text not null);";
    
       public MyTeamDB(Context ctx) 
    {
        this.context = ctx;
        DBHelper = new DatabaseHelper(context);
    }
        
    
    //---opens the database---
    public MyTeamDB open() throws SQLException 
    {
        db = DBHelper.getWritableDatabase();
        return this;
    }

    //---closes the database---    
    public void close() 
    {
        DBHelper.close();
    }
    
    //---insert Members Data into the database---													MEMBER DATA
    public boolean insertIntoRememberTable(String usr,String pas) 
    {
    	if(!GetRememberData()){
        ContentValues initialValues = new ContentValues();
        initialValues.put("CODE",CODE);
        initialValues.put("USERNAME",usr);
        initialValues.put("PASSWORD",pas);
        db.insert(MyTeamRememberTable, null, initialValues);
        return true;
    	}else{
    		 ContentValues args = new ContentValues();
    	        args.put("CODE", CODE);
    	        args.put("USERNAME", usr);
    	        args.put("PASSWORD", pas);
    	        return db.update(MyTeamRememberTable, args, 
    	                         "CODE" + "=" + CODE, null) > 0;
    	}
    }

    public boolean GetRememberData() 
    {
        Cursor mcursor= db.query(MyTeamRememberTable, new String[] {
        		"CODE",
        		"USERNAME", 
        		"PASSWORD"
        		},"CODE" + "=" + CODE, null, null, null, null, null );
        if(mcursor.getCount()!=0){
        	mcursor.moveToFirst();
        	StoredUserName=mcursor.getString(1);
        	StoredPassWord=mcursor.getString(2);
        	mcursor.close();
    		return true;    		
        }
        StoredUserName=null;
    	StoredPassWord=null;
       return false; 
    }
    
    public String GetUserName(){
    	return StoredUserName;
    }
    
    public String GetPassWord(){
    	return StoredPassWord;
    }
    
    public void ClearRememberTable(){
    	 db.execSQL("DELETE FROM MyTeamRememberMe;");
    }
    
    public void DropAll(){
    	db.execSQL("DROP TABLE IF EXISTS MyTeamRememberMe;");
    	db.execSQL("DROP TABLE IF EXISTS MyTeamSettings;");
    }
    
    public boolean UpdateSettingsTable(String sound,String vibrate) 
    {
    	    ContentValues args = new ContentValues();
    	        args.put("CODE", CODE);
    	        args.put("SOUND", sound);
    	        args.put("VIBRATE", vibrate);
    	        return db.update(MyTeamSettingsTable, args, 
    	                         "CODE" + "=" + CODE, null) > 0;
    }
    
    public boolean GetSettingsData() 
    {
        Cursor mcursor= db.query(MyTeamSettingsTable, new String[] {
        		"CODE",
        		"SOUND", 
        		"VIBRATE"
        		},"CODE" + "=" + CODE, null, null, null, null, null );
        if(mcursor.getCount()!=0){
        	mcursor.moveToFirst();
        	StoredSound=mcursor.getString(1);
        	StoredVibrate=mcursor.getString(2);
        	mcursor.close();
    		return true;    		
        }
       return false; 
    }
    
    public boolean GetSoundSettings(){
    	if(StoredSound.equals(PLAY))
    		return true;
    	else
    		return false;
    }
    
    public boolean GetVibrateSettings(){
    	if(StoredVibrate.equals(PLAY))
    		return true;
    	else
    		return false;
    }
 
    /* //---deletes a particular title---
    public boolean deleteTitle(long rowId) 
    {
        return db.delete(DATABASE_TABLE, KEY_ROWID + 
        		"=" + rowId, null) > 0;
    }
    //---retrieves all the titles---
    public Cursor getAllTitles() 
    {
        return db.query(DATABASE_TABLE, new String[] {
        		KEY_ROWID, 
        		KEY_ISBN,
        		KEY_TITLE,
                KEY_PUBLISHER}, 
                null, 
                null, 
                null, 
                null, 
                null);
    }
    //---retrieves a particular title---
    public Cursor getTitle(long rowId) throws SQLException 
    {
        Cursor mCursor =
                db.query(true, DATABASE_TABLE, new String[] {
                		KEY_ROWID,
                		KEY_ISBN, 
                		KEY_TITLE,
                		KEY_PUBLISHER
                		}, 
                		KEY_ROWID + "=" + rowId, 
                		null,
                		null, 
                		null, 
                		null, 
                		null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }
    //---updates a title---
    public boolean updateTitle(long rowId, String isbn, 
    String title, String publisher) 
    {
        ContentValues args = new ContentValues();
        args.put(KEY_ISBN, isbn);
        args.put(KEY_TITLE, title);
        args.put(KEY_PUBLISHER, publisher);
        return db.update(DATABASE_TABLE, args, 
                         KEY_ROWID + "=" + rowId, null) > 0;
    }*/
    private static class DatabaseHelper extends SQLiteOpenHelper 
    {
        DatabaseHelper(Context context) 
        {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
        @Override
        public void onCreate(SQLiteDatabase db) 
        {
            db.execSQL(CREATE_TABLE_REMEMBER);
            db.execSQL(CREATE_TABLE_SETTINGS);
            ContentValues initialValues = new ContentValues();
            initialValues.put("CODE","1");
            initialValues.put("SOUND","PLAY");
            initialValues.put("VIBRATE","PLAY");
            db.insert(MyTeamSettingsTable, null, initialValues);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) 
        {
       /*  db.execSQL("DROP TABLE IF EXISTS MemberData;");
            onCreate(db); */
        }
    }    

}
