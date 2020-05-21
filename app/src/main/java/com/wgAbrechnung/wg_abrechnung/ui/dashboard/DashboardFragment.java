package com.wgAbrechnung.wg_abrechnung.ui.dashboard;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.wgAbrechnung.wg_abrechnung.MainActivity;
import com.wgAbrechnung.wg_abrechnung.R;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DashboardFragment extends Fragment implements View.OnClickListener{

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DashboardViewModel dashboardViewModel;
    private EditText strPROJEKT_NAME;
    private ProgressBar progressBar;
    private Button upButton;

    private String USER_ID = "";
    private long latestNR = 0;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                ViewModelProviders.of(this).get(DashboardViewModel.class);
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);

        //Onclicklistener auf alles setzten
        upButton = root.findViewById(R.id.CREATE_PROJEKT);
        upButton.setOnClickListener(this);

        MainActivity main  = (MainActivity) getActivity();
        USER_ID = main.GET_ID();

        strPROJEKT_NAME = root.findViewById(R.id.PROJEKT_NAME_NEU);
        progressBar = root.findViewById(R.id.progressBar);

        return root;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.CREATE_PROJEKT:
                //Ein neues Projekt erstellen
                NEUES_PROJEKT();
                break;
        }

    }

    public void TEST(){
        //Zum Testen
    }


    public void NEUES_PROJEKT(){
        String strProjektName = strPROJEKT_NAME.getText().toString();
        final String PROJEKT_ID = UUID.randomUUID().toString();

        // Einträge für die Datenbank
        Map<String, Object> data = new HashMap<>();
        data.put("PROJEKT_ID", PROJEKT_ID);
        data.put("NAME", strProjektName);

        //Loading
        progressBar.setVisibility(View.VISIBLE);
        upButton.setClickable(false);

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
                                                System.out.println(data);
                                                latestNR = (Long) data.get("NR");
                                                latestNR++;

                                                // Eintrag der Datenbank Zuordnung ProjektNR und ID
                                                Map<String, Object> dataNR = new HashMap<>();
                                                dataNR.put("NR", latestNR);

                                                db.collection("PROJEKT_NR").document(PROJEKT_ID)
                                                        .set(dataNR)
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {

                                                                //Latest NR um 1 erhöhen.
                                                                Map<String, Object> dataLatestNR = new HashMap<>();
                                                                dataLatestNR.put("NR", latestNR);

                                                                db.collection("PROJEKT_LATEST_NR").document("LATEST_NR")
                                                                        .set(dataLatestNR)
                                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                            @Override
                                                                            public void onSuccess(Void aVoid) {
                                                                                //Loading weg und button klickbar
                                                                                progressBar.setVisibility(View.INVISIBLE);
                                                                                upButton.setClickable(true);

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
}
