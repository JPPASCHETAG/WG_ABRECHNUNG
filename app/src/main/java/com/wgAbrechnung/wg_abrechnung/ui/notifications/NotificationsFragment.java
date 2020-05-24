package com.wgAbrechnung.wg_abrechnung.ui.notifications;


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
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.wgAbrechnung.wg_abrechnung.CustomListAdapter;
import com.wgAbrechnung.wg_abrechnung.MainActivity;
import com.wgAbrechnung.wg_abrechnung.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class NotificationsFragment extends Fragment implements Toolbar.OnMenuItemClickListener {

    private NotificationsViewModel notificationsViewModel;

    private String USER_ID = "";
    ArrayList<String> ListName = new ArrayList<String>();
    ArrayList<String> ListID = new ArrayList<String>();
    ArrayList<String> ListNR = new ArrayList<String>();

    ListView listView;
    private Toolbar toolbar;

    FirebaseFirestore db = FirebaseFirestore.getInstance();


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        notificationsViewModel =
                ViewModelProviders.of(this).get(NotificationsViewModel.class);
        final View root = inflater.inflate(R.layout.fragment_notifications, container, false);

        root.findViewById(R.id.progressBar2).setVisibility(View.VISIBLE);

        MainActivity main  = (MainActivity) getActivity();
        USER_ID = main.GET_ID();

        listView = root.findViewById(R.id.PROJEKT_LIST);
        toolbar = root.findViewById(R.id.toolbar);
        toolbar.setOnMenuItemClickListener(this);

        //Namen holen
        db.collection("USER").document(USER_ID).collection("PROJEKTE")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                //Namen der Liste hinzufügen
                                if(document.getString("NAME") != null){
                                    ListName.add(document.getString("NAME"));
                                    ListID.add(document.getId());
                                }
                            }
                            //Für jedes Projekt die Nummer holen
                            //@TODO Hier eine praktikablere Lösung als alle zu holen
                                db.collection("PROJEKT_NR")
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                                for ( QueryDocumentSnapshot document : task.getResult()) {
                                                    if(ListID.contains(document.getId())){
                                                        ListNR.add(document.getLong("NR").toString());
                                                    }
                                                }

                                                //liste in ein Array verwandeln
                                                //Namen in Array wandeln
                                                String[] arrNamen = new String[ListName.size()];
                                                arrNamen = ListName.toArray(arrNamen);
                                                //IDs in array wandweln
                                                String[] IDArray = new String[ListID.size()];
                                                IDArray = ListID.toArray(IDArray);
                                                //NR in array wandeln
                                                String[] arrNR = new String[ListNR.size()];
                                                arrNR = ListNR.toArray(arrNR);

                                                root.findViewById(R.id.progressBar2).setVisibility(View.INVISIBLE);

                                                //Liste aufbauen
                                                Activity context = getActivity();
                                                CustomListAdapter Listadapter = new CustomListAdapter(context, arrNamen, arrNR , IDArray);
                                                listView.setAdapter(Listadapter);
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Context context = getActivity().getApplicationContext();
                                                CharSequence text = "Fehler beim laden der Einladungsnummern.";
                                                Toast toast = Toast.makeText(context, text, Toast.LENGTH_LONG);
                                                toast.show();
                                            }
                                        });

                        } else {
                            root.findViewById(R.id.progressBar2).setVisibility(View.INVISIBLE);
                            //Rückmeldung
                            Context context = getActivity().getApplicationContext();
                            CharSequence text = "Fehler beim laden der Projekte.";
                            Toast toast = Toast.makeText(context, text, Toast.LENGTH_LONG);
                            toast.show();
                        }
                    }
                });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //Shared Preference ändern auf Projekt Id
                System.out.println(ListID.get(position));
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("CURRENT_PROJEKT", ListID.get(position));
                editor.apply();


            }
        });

        return root;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.ADD_PROJEKT:
                AddProjekt();
                break;
            case R.id.NEW_PROJEKT:
                NewProjekt();
                break;
        }

        return false;
    }

    public void AddProjekt(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Ein Projekt hinzufügen:");
        builder.setMessage("Hie kann ein Projekt einer anderen Person hinzugefügt werden.");

        //Eingabe des Zwecks
        final EditText ProjektNREditText = new EditText(getActivity().getApplicationContext());
        ProjektNREditText.setHint("Die Nummer des Projekts");
        ProjektNREditText.setMaxLines(1);

        builder.setView(ProjektNREditText);

        builder.setPositiveButton("Hinzufügen", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                Long inputNR = Long.parseLong(ProjektNREditText.getText().toString());

                db.collection("PROJEKT_NR").whereEqualTo("NR",inputNR)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful()) {
                                    String ProjektID = "";
                                    String ProjektName = "";
                                    for (QueryDocumentSnapshot document : task.getResult()) {

                                        ProjektID = document.getId();
                                        ProjektName = document.getString("NAME");

                                    }
                                    //das Projekt wird mit Nr und Name gesetzt
                                    Map<String, Object> dataExistingProjekt = new HashMap<>();
                                    dataExistingProjekt.put("PROJEKT_ID", ProjektID);
                                    dataExistingProjekt.put("NAME", ProjektName);

                                    final String finalProjektID = ProjektID;
                                    db.collection("USER").document(USER_ID).collection("PROJEKTE").document(ProjektID)
                                            .set(dataExistingProjekt)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                    //Shared Preference setzen auf das neue Projekt
                                                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
                                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                                    editor.putString("CURRENT_PROJEKT", finalProjektID);
                                                    editor.apply();

                                                    //Rückmeldung
                                                    Context context = getActivity().getApplicationContext();
                                                    CharSequence text = "Das Projekt wurde erfolgreih hinzugefügt.";
                                                    Toast toast = Toast.makeText(context, text, Toast.LENGTH_LONG);
                                                    toast.show();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    //Rückmeldung
                                                    Context context = getActivity().getApplicationContext();
                                                    CharSequence text = "Es ist ein Fehler aufgetreten.";
                                                    Toast toast = Toast.makeText(context, text, Toast.LENGTH_LONG);
                                                    toast.show();
                                                }
                                            });
                                }
                            }
                        });
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

    public void NewProjekt(){

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Ein neues Projekt erstellen");

        //Eingabe des Zwecks
        final EditText ProjektNameEditText = new EditText(getActivity().getApplicationContext());
        ProjektNameEditText.setHint("Der Name des Projekts");
        ProjektNameEditText.setMaxLines(1);

        builder.setView(ProjektNameEditText);

        builder.setPositiveButton("Hinzufügen", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                final String strProjektName = ProjektNameEditText.getText().toString();
                final String PROJEKT_ID = UUID.randomUUID().toString();

                // Einträge für die Datenbank
                Map<String, Object> data = new HashMap<>();
                data.put("PROJEKT_ID", PROJEKT_ID);
                data.put("NAME", strProjektName);


                // Einen neuen Eintrag in der UserProjekte Collection
                db.collection("USER").document(USER_ID).collection("PROJEKTE").document(PROJEKT_ID)
                        .set(data)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                //Einlagungsnummer generieren
                                //Die aktuell höchste NR holen
                                db.collection("PROJEKT_LATEST_NR")
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {

                                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                                        Map<String, Object> data = document.getData();
                                                        Long latestNR = (Long) data.get("NR");
                                                        latestNR++;

                                                        // Eintrag der Datenbank Zuordnung ProjektNR und ID
                                                        Map<String, Object> dataNR = new HashMap<>();
                                                        dataNR.put("NR", latestNR);
                                                        dataNR.put("NAME", strProjektName);

                                                        final Long finalLatestNR = latestNR;
                                                        db.collection("PROJEKT_NR").document(PROJEKT_ID)
                                                                .set(dataNR)
                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {

                                                                        //Latest NR um 1 erhöhen.
                                                                        Map<String, Object> dataLatestNR = new HashMap<>();
                                                                        dataLatestNR.put("NR", finalLatestNR);

                                                                        db.collection("PROJEKT_LATEST_NR").document("LATEST_NR")
                                                                                .set(dataLatestNR)
                                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                    @Override
                                                                                    public void onSuccess(Void aVoid) {

                                                                                        //aktuelles Projekt aktualisieren
                                                                                        //Shared Preference setzen auf das neue Projekt
                                                                                        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
                                                                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                                                                        editor.putString("CURRENT_PROJEKT", PROJEKT_ID);
                                                                                        editor.apply();


                                                                                        //Rückmeldung
                                                                                        Context context = getActivity().getApplicationContext();
                                                                                        CharSequence text = "Neues Projekt angelegt.";
                                                                                        Toast toast = Toast.makeText(context, text, Toast.LENGTH_LONG);
                                                                                        toast.show();
                                                                                    }
                                                                                })
                                                                                .addOnFailureListener(new OnFailureListener() {
                                                                                    @Override
                                                                                    public void onFailure(@NonNull Exception e) {
                                                                                        //Rückmeldung
                                                                                        Context context = getActivity().getApplicationContext();
                                                                                        CharSequence text = "Fehler beim erhöhen der fortlaufenden Nr.";
                                                                                        Toast toast = Toast.makeText(context, text, Toast.LENGTH_LONG);
                                                                                        toast.show();
                                                                                    }
                                                                                });
                                                                    }
                                                                })
                                                                .addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {
                                                                        //Rückmeldung
                                                                        Context context = getActivity().getApplicationContext();
                                                                        CharSequence text = "Fehler zuordnen der ProjektNR.";
                                                                        Toast toast = Toast.makeText(context, text, Toast.LENGTH_LONG);
                                                                        toast.show();
                                                                    }
                                                                });
                                                    }
                                                } else {
                                                    //Rückmeldung
                                                    Context context = getActivity().getApplicationContext();
                                                    CharSequence text = "Fehler beim Holen der aktuellen NR.";
                                                    Toast toast = Toast.makeText(context, text, Toast.LENGTH_LONG);
                                                    toast.show();
                                                }
                                            }
                                        });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                //Rückmeldung
                                Context context = getActivity().getApplicationContext();
                                CharSequence text = "Fehler beim Anlegen des Projekts.";
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
    }

}
