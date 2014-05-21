package it.tour;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.LinearLayout;

class MyAdapter extends ArrayAdapter<PDI> {

    private int resource;
    private LayoutInflater  inflater;

    public MyAdapter ( Context context, int resourceId, List<PDI> objects ) {
            super( context, resourceId, objects );
            resource = resourceId;//id degli elementi della lista!!!!
            inflater = LayoutInflater.from( context );
    }

    @Override
    public View getView ( int position, View convertView, ViewGroup parent ) {

            // Recuperiamo l'oggetto che dobbiamo inserire in questa posizione
            PDI person = getItem( position );

            PDIViewCache viewCache;

            if ( convertView == null ) {
                    convertView = ( LinearLayout ) inflater.inflate( resource, null );
                    viewCache = new PDIViewCache( convertView );
                    convertView.setTag( viewCache );
            }
            else {
                    convertView = ( LinearLayout ) convertView;
                    viewCache = ( PDIViewCache ) convertView.getTag();
            }

            // Prendiamo le view dalla cache e mettiamoci i valori

            TextView tvName = viewCache.getTextViewName();
            tvName.setText( person.getName() );

            TextView tvVisit = viewCache.getTextViewVisit();
            tvVisit.setText( person.getSurname() );

            return convertView;

    }
}