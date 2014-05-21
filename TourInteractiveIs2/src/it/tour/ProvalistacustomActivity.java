package it.tour;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import java.util.ArrayList;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class ProvalistacustomActivity extends Activity implements LocationListener {
	private static final int DIALOG_ALERT_ID = 1;
	private LocationManager locationManager;
	private String provider;
	private ListView        listView;
    private DBInteractiveTour database;
    private TextView DA;
    private TextView A;
    private String da;
    private String a;
    private ArrayList<String> giavisti;
	private int posvicino;  //posizione dell' elemento pdi scelto

    ////////          metodi del gps     ////////
    
	@Override
	public void onLocationChanged(Location location) {
		double lat = (double) (location.getLatitude());
		double lng = (double) (location.getLongitude());
		
        SQLiteDatabase	db	=database.getWritableDatabase();

		//1 devo fare l accesso al database prelevare tutte le coordinate e i nomi dei pdi
		//2 e poi fare la funzione di vicinanza ... che mi ritorna il nome del pdi vicino o mi ritorna "nessuno"
		//3 adesso accedo e chiedo del tour primo ... 
		//4 poi dovro metterci il paramentro passato dalla activity principale
		ArrayList<String> latitu=new ArrayList<String>();
		ArrayList<String> longitu=new ArrayList<String>();
		ArrayList<String> nomeP=new ArrayList<String>();

		
		String query = "SELECT b.longitudine,b.latitudine,b.nome_pdi FROM Tour a INNER JOIN Pdi b ON a.nome_tour=b.cod_tour WHERE a.nome_tour=? ";
        String[] selectionArgs2 = { ApplicationState.nomeTourselected };//qui ci vuole il paramentro passato
        Cursor cursor2 = null;
        try {
			cursor2 = db.rawQuery(query,selectionArgs2);
			cursor2.moveToFirst();	
			while (cursor2.isAfterLast()==false) {
			longitu.add(cursor2.getString(0));
			latitu.add(cursor2.getString(1));
			nomeP.add(cursor2.getString(2));
			cursor2.moveToNext();
			} 
			} catch (Exception e) {
			Log.e("NotePadActivity", "Errore nel salvataggio della nota", e);
			alert("Errore nelle coordinate!!!!!!!!");
		} finally {
			if (cursor2 != null) {
				cursor2.close();
			}
		//una volta prelevati li mando ad una funzione
			//String vicino=calcoladist(lat,lng,latitu,longitu,nomeP);
		//test su funzionamento distance to
//pongo la distanza a 50 metri come limite!!!!
			String vicino=calcoladist(lat,lng,latitu,longitu,nomeP);
		//vediamo se calcola dist va bene
			System.out.println(vicino);
			System.out.println(posvicino);
						
			if(vicino.equalsIgnoreCase("nessuno")==false)
		{
		if(giavisto(vicino)==false)//funzione per vedere se e presente nei gia visti		
		{			//ho preso il nome del pdi
			//devo metterlo nella lista gia visti
			//devo cambiare la textview nella posizione giusta
			//devo passarlo alla descrizione
			//devo chiamare la activity descr
			giavisti.add(vicino);
			
			@SuppressWarnings("unchecked")
			ArrayAdapter<PDI> arrayAdapter1 = (ArrayAdapter<PDI>) listView.getAdapter();
			PDI pidscelto= arrayAdapter1.getItem(posvicino);
			pidscelto.setVisit("Visitato");
 				arrayAdapter1.setNotifyOnChange(true);
 				arrayAdapter1.notifyDataSetChanged();
			
 			//passarlo alla descrizione	
		ApplicationState.nomepdifordescription=vicino;
		
		//richiamo description activity
		Intent intent = new Intent(this, DescriptionGUIActivity.class);
		startActivity(intent);
		}		
		}
		
		}
		
	}

	private boolean giavisto(String vicino)
	{
		boolean presente=false;
		if(giavisti.size()>0)
		{
			for(int i=0;i<giavisti.size();i++)
		{
			
		if(giavisti.get(i).contentEquals(vicino)==true)	
		presente=true;
		}
		}	
	return presente;
	}
	
	
	
	private String calcoladist(double lat,double longi,ArrayList<String> latitu,
			ArrayList<String> longitu, ArrayList<String> nomeP) {
		//calcolo se qualche pdi e vicino alla posizione!!!!
		String vicino="nessuno";
		

		
		float distmin=50;
		Location locationCentro = new Location ("posrilevata");  
            locationCentro.setLatitude(lat);    
            locationCentro.setLongitude(longi);  
		for(int i=0;i<nomeP.size();i++)
		{
Location locationArrivo = new Location ("pospdiesimo");  
locationArrivo.setLatitude(Double.parseDouble(latitu.get(i)));    
locationArrivo.setLongitude(Double.parseDouble(longitu.get(i)));  

float distance = locationCentro.distanceTo(locationArrivo);  

//verifico chi e piu vicino tra quelli vicini	
if(distance<50)
		{
	if(distance<distmin)
	{
vicino=nomeP.get(i);
distmin=distance;	
posvicino=i;

	}
		}
		}
		
		return vicino;
	}
	
	
	@Override
	public void onProviderEnabled(String provider) {
		Toast.makeText(this, "Enabled new provider " + provider,
				Toast.LENGTH_SHORT).show();

	}

	@Override
	public void onProviderDisabled(String provider) {
		Toast.makeText(this, "Disenabled provider " + provider,
				Toast.LENGTH_SHORT).show();
	}
	
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}
	
	//// fine metodi del gps ////////
	
	////metodi del ciclo di vita dell' activity /////
	/* Request updates at startup */
	@Override
	protected void onResume() {
		super.onResume();
		//vedere l unita di misura di tempo e spazio che vuole!!!!
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 60000, 5, this);
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		database.close();
	}
		
	@Override
		public boolean onCreateOptionsMenu(Menu menu) {
			MenuInflater inflater = new MenuInflater(this);
			inflater.inflate(R.menu.menu1, menu);
			return true;
		}
		
		@Override
		public boolean onOptionsItemSelected(MenuItem item) {
			if (item.isCheckable()) {
				item.setChecked(!item.isChecked());
			}
			finish();
			return true;
		}

	
	
	
	
	
	
    @SuppressWarnings("unchecked")
	@Override
    public void onCreate ( Bundle savedInstanceState ) {
            super.onCreate( savedInstanceState );
            setContentView( R.layout.main2 );
    		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    		Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    		giavisti=new ArrayList<String>();
    		posvicino=0;
    		
    		
    		
    		if (location != null) {
    			System.out.println("Provider " + provider + " has been selected.");
    			int lat = (int) (location.getLatitude());
    			int lng = (int) (location.getLongitude());
                		
    		} else {
    			alert("nessuna posizione disp");
    		}
    	
    		    		
            listView = ( ListView ) findViewById( R.id.personListView );
            listView.setAdapter( new MyAdapter( this, R.layout.row_item, new ArrayList<PDI>() ) );
            ArrayAdapter<PDI> array; 
            array=(ArrayAdapter<PDI>)listView.getAdapter();
            database=new DBInteractiveTour(this);
            SQLiteDatabase	db	=database.getWritableDatabase();
            String selection = "nome_tour = ?";
     		String[] selectionArgs = { ApplicationState.nomeTourselected };
     		
     		Cursor cursor = null;
     		try {
     			cursor = db.query("Tour",
     					new String[] { "ind_partenza","ind_destinazione" }, selection, selectionArgs,
     					null, null, null);
     			if (cursor.moveToNext()) {
     				da= cursor.getString(0);
     				a=cursor.getString(1);
     				
     			} else {
     				alert("errore avvio Tour");
     			}
     		} catch (Exception e) {
     			Log.e("NotePadActivity", "Errore nel salvataggio della nota", e);
     			alert("Errore nella lettura!!!");
     		} finally {
     			if (cursor != null) {
     				cursor.close();
     			}
     		}

     		 DA=(TextView)findViewById(R.id.textViewDAin);
             DA.setText(da);
             A=(TextView)findViewById(R.id.textViewAin);
             A.setText(a);   
            
            //query per prelevare i nomi dei pdi da mettere nella lista
             String query = "SELECT b.nome_pdi FROM Tour a INNER JOIN Pdi b ON a.nome_tour=b.cod_tour WHERE a.nome_tour=?";
             String[] selectionArgs2 = { ApplicationState.nomeTourselected };
             Cursor cursor2 = null;
             try {
     			cursor2 = db.rawQuery(query,selectionArgs2);
     			cursor2.moveToFirst();
     			String nome;
     	
     			while (cursor2.isAfterLast()==false) {
     				nome=cursor2.getString(0);
     			array.add(new PDI(nome,""));
     			
     				cursor2.moveToNext();
     			} 
     			} catch (Exception e) {
     			Log.e("NotePadActivity", "Errore nel salvataggio della nota", e);
     			alert("Errore nella lista pdi!");
     		} finally {
     			if (cursor2 != null) {
     				cursor2.close();
     			}
     		}
                   
     		listView.setOnItemClickListener(new OnItemClickListener() {
     			@Override
     			public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
     				ArrayAdapter<PDI> arrayAdapter1 = (ArrayAdapter<PDI>) listView.getAdapter();
     				PDI item = arrayAdapter1.getItem(position);
    				String nome=item.getName();
     	             String query = "SELECT indirizzo FROM Pdi WHERE nome_pdi=?";
     	             String[] selectionArgs2 = { nome };
     	            Cursor cursor2 = null;
     	            SQLiteDatabase	db	=database.getWritableDatabase();
     	             try {
     	     			cursor2 = db.rawQuery(query,selectionArgs2);
     	     			cursor2.moveToFirst();
     	     			while (cursor2.isAfterLast()==false) {
     	     				nome=cursor2.getString(0);
     	     			    cursor2.moveToNext();
     	     			} 
     	     			} catch (Exception e) {
     	     			Log.e("TourActivity", "Errore nel prelievo dei pdi", e);
     	     			alert("Errore nel prelievo della via");
     	     		} finally {
     	     			if (cursor2 != null) {
     	     				cursor2.close();
     	     			}
     	     		}
     				
     				Toast toast = Toast.makeText(ProvalistacustomActivity.this,nome, Toast.LENGTH_LONG);
     				toast.show();
     				
     				     				
     			}
     		});
       ///////   MENU     ////////
   

    }
    // gestione tasto back e dialog corrispondente
    @Override
    public void onBackPressed() {
    // do something on back.
		showDialog(DIALOG_ALERT_ID);

    return;
    }
    
    @Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog;
		switch (id) {
		case DIALOG_ALERT_ID:
			dialog = createAlertDialog();
			break;
		default:
			dialog = null;
			break;
		}
		return dialog;
	}

    private Dialog createAlertDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Conferma Cancellazione Tour");
		builder.setMessage("Vuoi Terminare il Tour?");
		builder.setCancelable(false);
		builder.setPositiveButton("Termina", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dismissDialog(DIALOG_ALERT_ID);
			finish();
			}
		});
		builder.setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dismissDialog(DIALOG_ALERT_ID);
			}
		});
		AlertDialog alert = builder.create();
		return alert;
	}
 
    //metodo di supporto alert
     
    private void alert(String message) {
		Toast.makeText(this, message, Toast.LENGTH_LONG).show();
	}
	
	    
}