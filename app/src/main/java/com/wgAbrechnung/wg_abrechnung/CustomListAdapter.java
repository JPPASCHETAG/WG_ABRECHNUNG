package com.wgAbrechnung.wg_abrechnung;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

public class CustomListAdapter extends ArrayAdapter {

    //to reference the Activity
    private final Activity context;

    //to store the list of countries
    private final String[] nameArray;

    //to store the list of countries
    private final String[] infoArray;

    String currentProjekt = "";

    private final String[] infoArrayID;


    public CustomListAdapter(Activity context, String[] nameArrayParam, String[] infoArrayParam, String[] IDArrayParam){

        super(context,R.layout.list_view_layout , nameArrayParam);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        currentProjekt = sharedPreferences.getString("CURRENT_PROJEKT", "noID");

        this.context=context;
        this.nameArray = nameArrayParam;
        this.infoArray = infoArrayParam;
        this.infoArrayID = IDArrayParam;

    }

    @NotNull
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.list_view_layout, null,true);
        rowView.setId(view.generateViewId());

        //this code gets references to objects in the listview_row.xml file
        TextView nameTextField = (TextView) rowView.findViewById(R.id.nameTextViewID);
        TextView infoTextField = (TextView) rowView.findViewById(R.id.infoTextViewID);
        TextView IDTextField = (TextView) rowView.findViewById(R.id.IDTextViewID);
        ImageView imgCheck = (ImageView) rowView.findViewById(R.id.imgCheck);

        //this code sets the values of the objects to values from the arrays
        if(nameArray.length == 0){
            nameTextField.setText("Es wurden noch keine Projekte angelegt");
        }else {
            nameTextField.setText(nameArray[position]);
            infoTextField.setText(infoArray[position]);
            IDTextField.setText(infoArrayID[position]);
            if(infoArrayID[position].equals(currentProjekt)){
                imgCheck.setVisibility(View.VISIBLE);
            }
        }


        return rowView;

    }
}
