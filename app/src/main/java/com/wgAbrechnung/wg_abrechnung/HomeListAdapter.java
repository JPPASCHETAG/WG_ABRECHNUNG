package com.wgAbrechnung.wg_abrechnung;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class HomeListAdapter extends ArrayAdapter {

    //to reference the Activity
    private final Activity context;

    //der angegebene Verwendungszweck
    private final String[] ZweckArray;

    //beinhaltet das Datum der Buchung
    private final String[] DatumArray;

    //Der Angegebene Name
    private final String[] NameArray;

    //Der angegebene Betrag
    private final String[] BetragArray;


    public HomeListAdapter(Activity context, String[] ZweckArrayParam, String[] DatumArrayParam, String[] NameArrayParam, String[] BetragArrayParam){

        super(context,R.layout.main_list_layout , ZweckArrayParam);

        this.context=context;
        this.ZweckArray = ZweckArrayParam;
        this.DatumArray = DatumArrayParam;
        this.NameArray = NameArrayParam;
        this.BetragArray = BetragArrayParam;

    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.main_list_layout, null,true);
        rowView.setId(view.generateViewId());

        //this code gets references to objects in the listview_row.xml file
        TextView ZweckTextField = (TextView) rowView.findViewById(R.id.Zweck);
        TextView DatumTextField = (TextView) rowView.findViewById(R.id.Date);
        TextView nameTextField = (TextView) rowView.findViewById(R.id.Name);
        TextView BetragTextField = (TextView) rowView.findViewById(R.id.Betrag);

        //this code sets the values of the objects to values from the arrays
        if(ZweckArray.length == 0){
            nameTextField.setText("Es wurden noch keine Einträge angelegt");
        }else {
            ZweckTextField.setText(ZweckArray[position]);
            DatumTextField.setText(DatumArray[position]);
            nameTextField.setText(NameArray[position]);
            BetragTextField.setText(BetragArray[position]);
        }


        return rowView;

    }



}
