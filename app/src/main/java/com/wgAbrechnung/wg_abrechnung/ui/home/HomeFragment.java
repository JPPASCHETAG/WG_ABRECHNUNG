package com.wgAbrechnung.wg_abrechnung.ui.home;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.google.firebase.firestore.FirebaseFirestore;
import com.wgAbrechnung.wg_abrechnung.HTTP_REQUEST;
import com.wgAbrechnung.wg_abrechnung.HomeListAdapter;
import com.wgAbrechnung.wg_abrechnung.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class HomeFragment extends Fragment implements Toolbar.OnMenuItemClickListener, HTTP_REQUEST.AsyncResponse {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String CURRENT_PROJEKT = "";
    private Integer MODE = 0;

    private ListView listView;
    private Toolbar toolbar;

    private ArrayList<String> ListZweck = new ArrayList<String>();
    private ArrayList<String> ListDatum = new ArrayList<String>();
    private ArrayList<String> ListName = new ArrayList<String>();
    private ArrayList<String> ListBetrag = new ArrayList<String>();


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        CURRENT_PROJEKT = sharedPreferences.getString("CURRENT_PROJEKT", "");

        listView = root.findViewById(R.id.HomeListView);
        toolbar = root.findViewById(R.id.toolbar);
        toolbar.setOnMenuItemClickListener(this);


        String urlWebService = "http://192.168.2.120/AppCOnnect/connect.php?MODE=5&PROJEKT_TOKEN=" + CURRENT_PROJEKT;
        MODE = 5;
        new HTTP_REQUEST(this).execute(urlWebService);

        return root;
    }

    /*
    Der Betrag wird formatiert.
     */
    public String FormatBetrag(String strBetrag){

        if(strBetrag.contains(".")){
            strBetrag.replace(".",",");
        }
        if(!strBetrag.contains("€")){
            strBetrag += "€";
        }
        return strBetrag;
    }

    public void NewEntryDialog(){

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Einen Eintrag anlegen:");

        LinearLayout layout = new LinearLayout(getActivity().getApplicationContext());
        layout.setOrientation(LinearLayout.VERTICAL);

        //Eingabe des Zwecks
        final EditText ZweckEditText = new EditText(getActivity().getApplicationContext());
        ZweckEditText.setHint("Verwendungszweck");
        ZweckEditText.setMaxLines(1);
        layout.addView(ZweckEditText);

        //Eingabe des Datums
        final EditText DateEditText = new EditText(getActivity().getApplicationContext());
        //Das aktuelle Datum holen
        Calendar kalender = Calendar.getInstance();
        SimpleDateFormat datumsformat = new SimpleDateFormat("dd.MM.yyyy");
        DateEditText.setText(datumsformat.format(kalender.getTime()));
        layout.addView(DateEditText);

        //Eingabe des Namen
        final EditText NameEditText = new EditText(getActivity().getApplicationContext());
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        String lastNameUsed = sharedPreferences.getString("LAST_USED_NAME", "noID");
        if(lastNameUsed != "noID"){
            NameEditText.setText(lastNameUsed);
        }else{
            NameEditText.setHint("Name");
        }
        layout.addView(NameEditText);

        //Eingabe des Betrags
        final EditText BetragEditText = new EditText(getActivity().getApplicationContext());

        BetragEditText.setHint("Betrag");
        BetragEditText.setMaxLines(1);
        layout.addView(BetragEditText);

        builder.setView(layout);

        builder.setPositiveButton("Hinzufügen", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                ADD_ENTRY(ZweckEditText.getText().toString(),DateEditText.getText().toString(),NameEditText.getText().toString(),FormatBetrag(BetragEditText.getText().toString()));

                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("LAST_USED_NAME", NameEditText.getText().toString());
                editor.apply();
            }
        });
        builder.setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void ADD_ENTRY(String strZweck, String strDatum, String strName, String strBetrag) {

        String urlWebService = "http://192.168.2.120/AppCOnnect/connect.php?MODE=4&PROJEKT_TOKEN=" + CURRENT_PROJEKT;
                urlWebService += "&ZWECK=" + strZweck;
                urlWebService += "&DATUM=" + strDatum;
                urlWebService += "&NAME=" + strName;
                urlWebService += "&BETRAG=" + strBetrag;
        MODE = 4;
        new HTTP_REQUEST(this).execute(urlWebService);

    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()){
            case R.id.NEW_ENTRY:
                NewEntryDialog();
                break;
            case R.id.MONTH_SUMMARY:
                //@TODO neue Activität mit graphen etc
                break;
        }
        return false;
    }

    @Override
    public void processFinish(String output) {

        Context context = getActivity().getApplicationContext();

        switch (MODE) {
            case 4:

                CharSequence text = "Eintrag erfolgreich angelegt!";
                Toast toast = Toast.makeText(context, text, Toast.LENGTH_LONG);
                toast.show();

            break;
            case 5:
                MODE = 0;
                try {
                    JSONArray jsonArray = new JSONArray(output);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        ListZweck.add(jsonObject.getString("ZWECK"));
                        ListName.add(jsonObject.getString("NAME"));
                        ListDatum.add(jsonObject.getString("DATUM"));
                        ListBetrag.add(jsonObject.getString("BETRAG"));

                    }

                    //Zweck in Array wandeln
                    String[] arrZweck = new String[ListZweck.size()];
                    arrZweck = ListZweck.toArray(arrZweck);
                    //Datum in Array wandeln
                    String[] arrDatum = new String[ListDatum.size()];
                    arrDatum = ListDatum.toArray(arrDatum);
                    //Namen in Array wandeln
                    String[] arrName = new String[ListName.size()];
                    arrName = ListName.toArray(arrName);
                    //Betrag in Array wandlen
                    String[] arrBetrag = new String[ListBetrag.size()];
                    arrBetrag = ListBetrag.toArray(arrBetrag);

                    //ListView füllen
                    Activity activity = getActivity();
                    HomeListAdapter Listadapter = new HomeListAdapter(activity, arrZweck, arrDatum, arrName, arrBetrag);
                    listView.setAdapter(Listadapter);

                } catch (JSONException e) {
                    System.out.println(e);
                }
            break;

        }

    }
}
