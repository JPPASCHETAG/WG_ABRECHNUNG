package com.wgAbrechnung.wg_abrechnung.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.wgAbrechnung.wg_abrechnung.HTTP_REQUEST;
import com.wgAbrechnung.wg_abrechnung.MainActivity;
import com.wgAbrechnung.wg_abrechnung.R;

import java.net.URLConnection;


public class DashboardFragment extends Fragment implements View.OnClickListener{

    private DashboardViewModel dashboardViewModel;

    private String USER_ID = "";
    Button btn;
    private URLConnection conn;

    public static final String POST_PARAM_KEYVALUE_SEPARATOR = "=";
    public static final String POST_PARAM_SEPARATOR = "&";
    private static final String DESTINATION_METHOD = "allEntrys";


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                ViewModelProviders.of(this).get(DashboardViewModel.class);
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);

        MainActivity main  = (MainActivity) getActivity();
        USER_ID = main.GET_ID();

        btn = root.findViewById(R.id.Test);
        btn.setOnClickListener(this);


        return root;
    }

    @Override
    public void onClick(View v) {
        System.out.println(v.getId());
        switch (v.getId()){
            case 2131230740:

                DB_CONNECTION();

                break;
        }
    }


    public String DB_CONNECTION() {

        String urlWebService = "http://192.168.2.120/AppCOnnect/connect.php";

        System.out.println(new HTTP_REQUEST().execute(urlWebService));
        //new HTTP_REQUEST().Output(lsdna);
        return null;
    }

}
