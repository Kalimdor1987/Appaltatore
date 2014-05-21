package it.tour;

import android.view.View;
import android.widget.TextView;

public class PDIViewCache {

        private View            baseView;
        private TextView        textViewName;
        private TextView        textViewVisit;

        public PDIViewCache ( View baseView ) {
                this.baseView = baseView;
        }

        public TextView getTextViewName () {
                if ( textViewName == null ) {
                        textViewName = ( TextView ) baseView.findViewById( R.id.personName );
                }
                return textViewName;
        }

        public TextView getTextViewVisit () {
                if ( textViewVisit == null ) {
                        textViewVisit = ( TextView ) baseView.findViewById( R.id.personSurname );
                }
                return textViewVisit;
        }

      

}