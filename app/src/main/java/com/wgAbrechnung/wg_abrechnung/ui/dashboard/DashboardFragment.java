package com.wgAbrechnung.wg_abrechnung.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.wgAbrechnung.wg_abrechnung.MainActivity;
import com.wgAbrechnung.wg_abrechnung.R;

public class DashboardFragment extends Fragment {

    private DashboardViewModel dashboardViewModel;

    private String USER_ID = "";


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                ViewModelProviders.of(this).get(DashboardViewModel.class);
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);

        MainActivity main  = (MainActivity) getActivity();
        USER_ID = main.GET_ID();

        return root;
    }
    
}
