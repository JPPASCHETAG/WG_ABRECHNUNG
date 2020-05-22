package com.wgAbrechnung.wg_abrechnung.ui.home;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
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
import com.wgAbrechnung.wg_abrechnung.CustomListAdapter;
import com.wgAbrechnung.wg_abrechnung.FireMissilesDialogFragment;
import com.wgAbrechnung.wg_abrechnung.HomeListAdapter;
import com.wgAbrechnung.wg_abrechnung.R;

import java.util.ArrayList;

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
                FireMissilesDialogFragment dialog = new FireMissilesDialogFragment();
                dialog.show(getFragmentManager(), "missiles");
                break;
        }
    }
}
