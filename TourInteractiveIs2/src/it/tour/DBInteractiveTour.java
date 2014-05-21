package it.tour;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;



public class DBInteractiveTour extends SQLiteOpenHelper {
	
	private static final String DB_NAME = "tour_db";
	private static final int DB_VERSION = 1;


	public DBInteractiveTour(Context context) {
		
		super(context , DB_NAME, null, DB_VERSION);
	
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
	
		// Creazione delle tabelle
		String sql1 ="";
		String sql2 ="";
		String sql3 ="";
	
		sql1 += "CREATE TABLE Tour (";  
		sql1 += " nome_tour TEXT Primary key NULL,";
		sql1 += " ind_partenza TEXT NOT NULL,"; 
		sql1 += " ind_destinazione TEXT NOT NULL"; 
		sql1 += ")"; 
		db.execSQL(sql1);    
    
        sql2 += "CREATE TABLE Pdi ("; 
		sql2 += " id_pdi INTEGER PRIMARY KEY,";
		sql2 += " nome_pdi TEXT NOT NULL,";
		sql2 += " cod_tour string NOT NULL references Tour(nome_tour),";
		sql2 += " indirizzo TEXT NOT NULL,";
		sql2 += " latitudine double NOT NULL,";
		sql2 += " longitudine double NOT NULL,";
		sql2 += " descrizione TEXT NOT NULL"; 
		sql2 += ")"; 
		db.execSQL(sql2);
			
		sql3 += "CREATE TABLE Image ("; 
		sql3 += " id_image INTEGER PRIMARY KEY,"; 
		sql3 += " cod_pdi INTEGER not null references Pdi(id_pdi),";
		sql3 += " nome_image TEXT NOT null,";
		sql3 += " rif_image TEXT NOT null";
		sql3 += ")"; 
		db.execSQL(sql3);
		
	}


    @Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
    	// Aggiornamento delle tabelle
	
    }

	
    public void insertDBTour(ContentValues value){
			
		SQLiteDatabase db = getWritableDatabase();
        db.insert("Tour",null,value);
	
	}

	
    public void insertDBPdi(ContentValues value){
			
		SQLiteDatabase db = getWritableDatabase();
        db.insert("Pdi",null,value);
	

	}
		
	
    public void insertDBImage(ContentValues value){
			
    	SQLiteDatabase db = getWritableDatabase();
        db.insert("Image",null,value);
	
    }

	
}

