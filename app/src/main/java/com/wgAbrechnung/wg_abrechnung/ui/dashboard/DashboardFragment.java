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

import org.json.JSONArray;
import org.json.JSONException;


public class DashboardFragment extends Fragment implements View.OnClickListener, HTTP_REQUEST.AsyncResponse {

    private DashboardViewModel dashboardViewModel;

    private String USER_ID = "";
    Button btn;


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

                String urlWebService = "http://192.168.2.120/AppCOnnect/connect.php";
                new HTTP_REQUEST(this).execute(urlWebService);

                break;
        }
    }

    @Override
    public void processFinish(String output) {
        try {
            JSONArray jsonArray = new JSONArray(output);
            System.out.println(jsonArray.getJSONObject(1));
        } catch (JSONException e){
            System.out.println(e);
        }
    }
}
