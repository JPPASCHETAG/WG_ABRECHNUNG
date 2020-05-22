package com.wgAbrechnung.wg_abrechnung.ui.home;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.wgAbrechnung.wg_abrechnung.CustomListAdapter;
import com.wgAbrechnung.wg_abrechnung.HomeListAdapter;
import com.wgAbrechnung.wg_abrechnung.R;

public class HomeFragment extends Fragment {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String CURRENT_PROJEKT = "";
    private HomeViewModel homeViewModel;

    ListView listView;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        CURRENT_PROJEKT = sharedPreferences.getString("CURRENT_PROJEKT", "");

        listView = root.findViewById(R.id.HomeListView);


        String[] arrZweck = {
                //"Tanken"
        };
        String[] arrDatum = {
                //"23.05.20"
        };
        String[] arrName = {
                //"Julian"
        };
        String[] arrBetrag = {
                //"55€"
        };

        Activity context = getActivity();
        HomeListAdapter Listadapter = new HomeListAdapter(context, arrZweck, arrDatum , arrName, arrBetrag);
        listView.setAdapter(Listadapter);

        return root;
    }
}
