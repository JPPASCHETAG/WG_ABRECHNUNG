package com.wgAbrechnung.wg_abrechnung.ui.home;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.wgAbrechnung.wg_abrechnung.HomeListAdapter;
import com.wgAbrechnung.wg_abrechnung.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class HomeFragment extends Fragment implements View.OnClickListener{

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String CURRENT_PROJEKT = "";

    private ListView listView;
    private Button btnNewEntry;



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
        btnNewEntry = root.findViewById(R.id.NEW_ENTRY);
        btnNewEntry.setOnClickListener(this);

        System.out.println(CURRENT_PROJEKT);
        db.collection(CURRENT_PROJEKT)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){

                            for (QueryDocumentSnapshot document : task.getResult()) {

                                //den Zweck holen
                                if(document.getString("ZWECK") != null){

                                    ListZweck.add(document.getString("ZWECK"));
                                    ListDatum.add(document.getString("DATUM"));
                                    ListName.add(document.getString("NAME"));
                                    ListBetrag.add(document.getString("BETRAG"));

                                }
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
                            Activity context = getActivity();
                            HomeListAdapter Listadapter = new HomeListAdapter(context, arrZweck, arrDatum , arrName, arrBetrag);
                            listView.setAdapter(Listadapter);

                        }else{
                            //Rückmeldung
                            Context context = getActivity().getApplicationContext();
                            CharSequence text = "Fehler beim laden der Eintäge.";
                            Toast toast = Toast.makeText(context, text, Toast.LENGTH_LONG);
                            toast.show();
                        }
                    }
                });

        return root;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.NEW_ENTRY:
                System.out.println("Button gevlivkt");
                // 1. Instantiate an <code><a href="/reference/android/app/AlertDialog.Builder.html">AlertDialog.Builder</a></code> with its constructor
                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

// 2. Chain together various setter methods to set the dialog characteristics
                builder.setTitle("Einen Eintrag anlegen:");
                        //.setMessage("Eine Nachricht");

                LinearLayout layout = new LinearLayout(getActivity().getApplicationContext());
                layout.setOrientation(LinearLayout.VERTICAL);

                //Eingabe des Zwecks
                final EditText ZweckEditText = new EditText(getActivity().getApplicationContext());
                ZweckEditText.setHint("Verwendungszweck");
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
                layout.addView(BetragEditText);

                builder.setView(layout);

                builder.setPositiveButton("Hinzufügen", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        Map<String, Object> data = new HashMap<>();
                        data.put("ZWECK", ZweckEditText.getText().toString());
                        data.put("DATUM", DateEditText.getText().toString());
                        data.put("NAME", NameEditText.getText().toString());
                        //Betrag formatieren
                        String Betrag = FormatBetrag(BetragEditText.getText().toString());
                        data.put("BETRAG", Betrag);

                        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("LAST_USED_NAME", NameEditText.getText().toString());
                        editor.apply();


                        db.collection(CURRENT_PROJEKT).document()
                                .set(data)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        Context context = getActivity().getApplicationContext();
                                        CharSequence text = "Eintrag erfolgreich angelegt.";
                                        Toast toast = Toast.makeText(context, text, Toast.LENGTH_LONG);
                                        toast.show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Context context = getActivity().getApplicationContext();
                                        CharSequence text = "Fehler beim Anlegen des Eintrags.";
                                        Toast toast = Toast.makeText(context, text, Toast.LENGTH_LONG);
                                        toast.show();
                                    }
                                });
                    }
                });
                builder.setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });

// 3. Get the <code><a href="/reference/android/app/AlertDialog.html">AlertDialog</a></code> from <code><a href="/reference/android/app/AlertDialog.Builder.html#create()">create()</a></code>
                AlertDialog dialog = builder.create();
                dialog.show();

                //FireMissilesDialogFragment dialog = new FireMissilesDialogFragment();
                //dialog.show(getFragmentManager(), "missiles");
                break;
        }
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

}
