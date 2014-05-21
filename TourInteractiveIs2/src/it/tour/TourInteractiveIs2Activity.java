package it.tour;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.net.URL;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;



public class TourInteractiveIs2Activity extends Activity {
	
	private DBInteractiveTour dbInteractiveTour;
	private DomEcho parser;
		
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		dbInteractiveTour = new DBInteractiveTour(this);
        
		
		Button Sel_tour = (Button)findViewById(R.id.seltour);
		Button Aggiorna = (Button)findViewById(R.id.aggiorna);
			
        Sel_tour.setOnClickListener(new OnClickListener() {
				
			@Override
			public void onClick(View view){
        		
				clickSuSelTour();
        		  
            }
        
		});
        
			

		Aggiorna.setOnClickListener(new OnClickListener() {
		      	  
				@Override
				public void onClick(View view){
					
					clickSuAggiorna();
				
      		  	}
      
		});
    
	}
	
	static void vDebug(String debugString){         //metodi di convenienza
		
		Log.v("DomParsing", debugString+"\n");
	
	}
	
	
	static void eDebug(String debugString){
		
		Log.e("DomParsing", debugString+"\n");
	
	}
	
	

	private void clickSuSelTour() {

		SQLiteDatabase db = dbInteractiveTour.getReadableDatabase();
   		
		String query1 = "Select * from Tour";
        
		Cursor cursor = db.rawQuery(query1, null);
		int lenghtQuery1 = cursor.getCount();

		vDebug("items--->"+lenghtQuery1);

		final CharSequence[] items = new CharSequence[lenghtQuery1];
         
		while (cursor.moveToNext()){
					
			items[cursor.getPosition()] = cursor.getString(0) ;
		
		}
 			
		if (cursor == null) {
 				cursor.close();
 		}
 		
		
		
		//Prepare the list dialog box
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        //Set its title
        builder.setTitle("Seleziona Tour");
        
        //Set the list items and assign with the click listener
        builder.setItems(items, new DialogInterface.OnClickListener() {

        // Click listener
        public void onClick(DialogInterface dialog, int item) {

        	Toast.makeText(getApplicationContext(), items[item], Toast.LENGTH_SHORT).show();
			ApplicationState.nomeTourselected= items[item].toString();
			Intent intent = new Intent(getBaseContext(), ProvalistacustomActivity.class);
			startActivity(intent);	
		
        }
      
        });

		
        AlertDialog alert = builder.create();

		//display dialog box
        alert.show();
   		
	}

   		
 
	public void clickSuAggiorna(){
			
		if(isOnline()){
		    	
			final ProgressDialog dialog = ProgressDialog.show(this, "Aggiornamento", "Download in corso...", true);
			final Handler handler = new Handler() {
				
				public void handleMessage(Message msg) {
					
					dialog.dismiss();
				
				}
			
			};
							
			Thread checkUpdate = new Thread() {  
							   
				public void run() {
							
					aggiorna();
				    handler.sendEmptyMessage(0);
				
				}
			
			};
							
			checkUpdate.start();
	    
		}

        else{
                
        	Toast.makeText(this, "Connessione internet non attiva", Toast.LENGTH_LONG).show();
        
        }
			

	}
			      	

	public void aggiorna() {
			
		SQLiteDatabase db = dbInteractiveTour.getReadableDatabase();	      		
		String query2 = "Select * from Image";
				    	   				
		parser = new DomEcho();
 		parser.parserXML();
 		
 		Element root=parser.document.getDocumentElement();
 		
 		NodeList tours=root.getElementsByTagName("tour");
 		NodeList pdis=root.getElementsByTagName("pdi");
 		NodeList images=root.getElementsByTagName("image");
	
 		int tours_lenght = tours.getLength();
 		int pdis_lenght = pdis.getLength();
 		int images_lenght = images.getLength();
 		 
        //popola la tabella Tour
		for(int j=0; j<tours_lenght;j++){
 			
			parser.creaValuesTour(tours,j);
 			dbInteractiveTour.insertDBTour(parser.valuesTour);
 			vDebug("tour "+j+" inserito");
 			
		}
 		
 		//popola la tabella Pdi
 		for(int t=0; t<pdis_lenght;t++){
 			
 			parser.creaValuesPdi(pdis,t);
 			dbInteractiveTour.insertDBPdi(parser.valuesPdi);
 			vDebug("pdi "+t+" inserito");
 		
 		}
 		
 		//popola la tabella Image
 		for(int s=0; s<images_lenght;s++){
 			
 			parser.creaValuesImage(images,s);
 			dbInteractiveTour.insertDBImage(parser.valuesImage);
 			vDebug("image "+s+" inserito");
 			
 		}
 		
 		Cursor cursor2 = db.rawQuery(query2, null);
		int lenghtQuery = cursor2.getCount();
		
		vDebug("numero di ennuple della tabella image--->"+lenghtQuery);

		//percorso del file immagine sul server
		String riferimenti;
		//chiave primaria nella tabella Image
		String cod_image;
		
		while (cursor2.moveToNext()){
			
			
			riferimenti = cursor2.getString(3) ;
			cod_image = cursor2.getString(0);
			
			try {
				
				getRemoteImage(riferimenti, cod_image);
			
			} catch (IOException e) {
				 
				// TODO Auto-generated catch block
				e.printStackTrace();
			  }finally {
	     			if (cursor2 == null) {
	     				cursor2.close();
	     			}
		
			  }
		}
		
	}
	
	//prelievo dell'immagine dal server
	public void getRemoteImage(String stringURL, String id_image) throws IOException {
		
		URL url = new URL (stringURL);
		InputStream input = url.openStream();
		
		try {
			
			File root = Environment.getExternalStorageDirectory();
			
			// create a File object for the parent directory
			File sddir = new File(root+"/interactivetour/images");
			
			sddir.mkdirs();
			
		    OutputStream output = new FileOutputStream (sddir+"/"+id_image+".jpg");
		    
		    try {
		        
			    byte[] buffer = new byte[8];
		        int bytesRead = 0;
		        while ((bytesRead = input.read(buffer, 0, buffer.length)) >= 0) {
		        
		        	output.write(buffer, 0, buffer.length);
		        
		        }
		    } finally {
		        
		    	output.close();
		    
		      }
		} finally {
		    
			input.close();
		  
		  }
		
	}

	
	public boolean isOnline() {
		
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		
        if(ni == null)
        	return false;

        return ni.isConnected();
	
	}


}
