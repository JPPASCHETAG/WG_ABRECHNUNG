package com.wgAbrechnung.wg_abrechnung.ui.notifications;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
import com.wgAbrechnung.wg_abrechnung.CustomListAdapter;
import com.wgAbrechnung.wg_abrechnung.MainActivity;
import com.wgAbrechnung.wg_abrechnung.R;

import java.util.ArrayList;

public class NotificationsFragment extends Fragment implements View.OnClickListener{

    private NotificationsViewModel notificationsViewModel;

    private String USER_ID = "";
    ArrayList<String> ListName = new ArrayList<String>();
    ArrayList<String> ListID = new ArrayList<String>();
    ArrayList<String> ListNR = new ArrayList<String>();

    ListView listView;


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
    public void onClick(View v) {


    }









}
