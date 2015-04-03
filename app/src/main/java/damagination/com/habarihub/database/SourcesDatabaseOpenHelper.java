package damagination.com.habarihub.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import damagination.com.habarihub.rss.Source;

/**
 * Created by Newton Bujiku, Updated by Damas on 1/13/15.
 */
public class SourcesDatabaseOpenHelper extends SQLiteOpenHelper {

    private Context context;
    private SQLiteDatabase database;
    private final String LOG_TAG = SourcesDatabaseOpenHelper.class.getSimpleName();


    //Name of the database
    public static final String DATABASE_NAME ="news_sources.db";

    //Database path
    public static final String DATABASE_PATH="/data/data/com.tanzoft.habarihub/databases/";


    //Version of the database
    public  static final int DATABASE_VERSION = 1;

    public static final String EXACT_PATH=DATABASE_PATH+DATABASE_NAME;



    public SourcesDatabaseOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context=context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        SourcesDatabase.onCreate(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        SourcesDatabase.onUpgrade(db, oldVersion, newVersion);
    }

    //Adding a reading material
    public void addRead(Source item){

        SQLiteDatabase db = this.getWritableDatabase();
        //Inserting a row
        db.insert(SourcesDatabase.DATABASE_TABLE_READ, null, addItemInfo(item));

    }

    ////Adding blogs  to the database
    public void addWatch(Source item){

        SQLiteDatabase db = this.getWritableDatabase();

        //Inserting a row

        db.insert(SourcesDatabase.DATABASE_TABLE_WATCH, null, addItemInfo(item));

    }

    public void ifSourceExists(Source item, String category){

        String table = null;

        if(category.equals("Read")){
            table = "read";
        } else if(category.equals("Watch")){
            table = "watch";
        }

        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT _url FROM " + table + " WHERE _url = ?";
        Cursor cursor = db.rawQuery(query, new String[]{item.getUrl()});

        if(cursor.getCount() == 0){
            if(category.equals("Read")){
                addRead(item);
            } else if(category.equals("Watch")){
                addWatch(item);
            }
        } else {
            Toast.makeText(context, "Source already exists", Toast.LENGTH_LONG).show();
            return;
        }
        Toast.makeText(context, "Source Added", Toast.LENGTH_LONG).show();
    }

    // Getting All reading sources from the database
    public ArrayList<Source> getAllRead() {
        ArrayList<Source> itemList = new ArrayList<Source>();
        // Select All Query
        String selectQuery = "SELECT * FROM "+SourcesDatabase.DATABASE_TABLE_READ+" ORDER BY "+SourcesDatabase.COLUMN_ID+" ASC ";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {

            do {

                Source item = new Source();
                item.set_id(Integer.parseInt(cursor.getString(cursor.getColumnIndex(SourcesDatabase.COLUMN_ID))));
                item.setDisplayName(cursor.getString(cursor.getColumnIndex(SourcesDatabase.COLUMN_TITLE)));
                item.setUrl(cursor.getString(cursor.getColumnIndex(SourcesDatabase.COLUMN_LINK)));

                // Adding contact to list
                itemList.add(item);

            } while (cursor.moveToNext());

        }
        cursor.close();
        db.close();
        return itemList;

    }

    // Getting All watchable content from the database
    public ArrayList<Source> getAllWatch() {
        ArrayList<Source> itemList = new ArrayList<Source>();



        // Select All Query
        String selectQuery = "SELECT * FROM "+SourcesDatabase.DATABASE_TABLE_WATCH+" ORDER BY "+SourcesDatabase.COLUMN_ID+" ASC ";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {

            do {

                Source item = new Source();
                item.set_id(Integer.parseInt(cursor.getString(cursor.getColumnIndex(SourcesDatabase.COLUMN_ID))));
                item.setDisplayName(cursor.getString(cursor.getColumnIndex(SourcesDatabase.COLUMN_TITLE)));
                item.setUrl(cursor.getString(cursor.getColumnIndex(SourcesDatabase.COLUMN_LINK)));

                // Adding contact to list
                itemList.add(item);
                Log.i("dbhelper ", "item added");

            } while (cursor.moveToNext());

        }
        cursor.close();
        // close inserting data from database
        db.close();
        return itemList;

    }

    public void deleteRead(int id){
        String deleteQuery = "DELETE FROM " + SourcesDatabase.DATABASE_TABLE_READ + " WHERE " + SourcesDatabase.COLUMN_ID + " = " + id;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(deleteQuery, null);

        db.delete(SourcesDatabase.DATABASE_TABLE_READ, SourcesDatabase.COLUMN_ID + " = " + id, null);
        Log.i("dbhelper ", "item deleted");
        cursor.close();
        db.close();
    }


    private ContentValues addItemInfo(Source item) {

        ContentValues values = new ContentValues();
        values.put(SourcesDatabase.COLUMN_TITLE, item.getDisplayName());//Client's name
        values.put(SourcesDatabase.COLUMN_LINK, item.getUrl());
        return values;
    }

    public void createEmptyDatabase() throws IOException{
		/*
		 * This method creates an empty database in the system and overwrites it with the
		 * provided populated database from the assets folder*/

        boolean databaseExists=verifyDatabaseExistence();

        if(databaseExists){
            //do nothing because the database already exists
        }else{

			/*
			 * By executing this block an empty database will be created into the default system path
			 * of Digicom application i.e /data/data/com.gottibujiku.adigicom.android.ui/databases/
			 * So that it will be possible to overwrite that database with the pre-populated database*/
            this.getWritableDatabase();

            try{

                overwriteDatabase();

            }catch (IOException e){

                throw new Error("Error Overwriting Database");
            }

        }


    }

    private void overwriteDatabase() throws IOException {

		/*
		 * Copies the database from the local assets folder and overwrites the created empty database
		 * in the system path of the application where it can handled and used
		 * This is done by transferring byte stream*/

        //Open your local database as the input stream
        InputStream inputDatabaseStream =context.getAssets().open(DATABASE_NAME);

        //Open the empty database as the output stream
        OutputStream outputDatabaseStream = new FileOutputStream(EXACT_PATH);

        //Transfer bytes from the inputDatabaseStream to OutputDatabaseStream

        byte[] buffer = new byte[1024];
        int length;
        while((length=inputDatabaseStream.read(buffer))>0){

            outputDatabaseStream.write(buffer,0,length);

        }

        //Close the streams
        outputDatabaseStream.flush();
        outputDatabaseStream.close();
        inputDatabaseStream.close();

    }

    private boolean verifyDatabaseExistence() {

        database=null;

        try{

            database = SQLiteDatabase.openDatabase(EXACT_PATH,null,SQLiteDatabase.OPEN_READONLY);

        }catch(SQLiteException e){

            //Database does not exist yet
            return false;
        }


        database.close();
        return true;
    }

    @Override
    public synchronized void close(){

        if(database!=null){

            database.close();

        }

        super.close();
    }

    public void openDatabase(){

        SQLiteDatabase db = this.getWritableDatabase();

    }

}
