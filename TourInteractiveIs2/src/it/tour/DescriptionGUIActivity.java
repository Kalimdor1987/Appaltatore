package it.tour;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.Gallery;
import android.widget.TextView;
import android.widget.Toast;

public class DescriptionGUIActivity extends Activity {
//avviata quando l' utente di trova vicino a un pdi   
    private DBInteractiveTour database;
    private String descrizionepdi;
    private ArrayList<String> rifimmagini;
    private Effect effect;

   
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        effect= new Effect(this,R.raw.suono);
        setContentView(R.layout.description);
        database=new DBInteractiveTour(this);
        rifimmagini=new ArrayList<String>();
        SQLiteDatabase	db	=database.getWritableDatabase();
        String pdipassato=ApplicationState.nomepdifordescription;
        Toast toast = Toast.makeText(DescriptionGUIActivity.this, "Ti trovi presso :   "+ pdipassato, Toast.LENGTH_LONG);
			toast.show();
		
			//   riproduzione suono   ////
			 effect.play();
             
             
             
			//query per prendere il rif all immagine e 
			//la descrizione del pdi
			String query = "SELECT descrizione FROM Pdi WHERE nome_pdi=?";
            String[] selectionArgs2 = { pdipassato };
            Cursor cursor2 = null;
            try {
    			cursor2 = db.rawQuery(query,selectionArgs2);
    			cursor2.moveToFirst();
    	
    			while (cursor2.isAfterLast()==false) {
    			descrizionepdi=cursor2.getString(0);
    				cursor2.moveToNext();
    			} 
    			} catch (Exception e) {
    			Log.e("descriptionActivity", "Errore nella descrizione", e);
    			Toast toast2 = Toast.makeText(DescriptionGUIActivity.this, "errore descrizione", Toast.LENGTH_SHORT);
 				toast2.show();    		} finally {
    			if (cursor2 != null) {
    				cursor2.close();
    			}
    		}
 				
 				
 				String query2 = "SELECT a.id_image FROM Image a INNER JOIN Pdi b ON a.cod_pdi=b.id_pdi WHERE b.nome_pdi=?";
 	            String[] selectionArgsI = { pdipassato };
 	            Cursor cursorI = null;
 	            try {
 	    			cursorI = db.rawQuery(query2,selectionArgsI);
 	    			cursorI.moveToFirst();
 	    	
 	    			while (cursorI.isAfterLast()==false) {
 	    			rifimmagini.add(cursorI.getString(0));
 	    			cursorI.moveToNext();
 	    			} 
 	    			} catch (Exception e) {
 	    			Log.e("descriptionActivity", "Errore neli rifimmagini", e);
 	    			Toast toast2 = Toast.makeText(DescriptionGUIActivity.this, "errore prelievo immagini", Toast.LENGTH_SHORT);
 	 				toast2.show();    		} finally {
 	    			if (cursorI != null) {
 	    				cursorI.close();
 	    			}
 	    		}
 	 	//ciclo per acquisizione di immagini come Drawable  ////			
		
		Drawable[] images = new Drawable[rifimmagini.size()];
		for(int i=0;i<rifimmagini.size();i++)
		{			
		String myJpgPath = "/sdcard/interactivetour/images/"+rifimmagini.get(i)+".jpg";
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 2;
		Bitmap bm1 = BitmapFactory.decodeFile(myJpgPath, options);
        Drawable bgrImage = new BitmapDrawable(bm1);
		images[i]=bgrImage;
		
		}
		
		Gallery gallery = (Gallery) findViewById(R.id.miaGalleria);
		TextView descr=(TextView) findViewById(R.id.TextView6);
		descr.setText(descrizionepdi);
		descr.setFocusable(false);
		gallery.setAdapter(new ImageAdapter(this, images));

	
	}

}
    
    
    
    
    
    
 