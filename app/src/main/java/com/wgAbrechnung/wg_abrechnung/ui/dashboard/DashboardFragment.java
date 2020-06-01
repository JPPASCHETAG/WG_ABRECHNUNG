package com.wgAbrechnung.wg_abrechnung.ui.dashboard;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Pie;
import com.wgAbrechnung.wg_abrechnung.HTTP_REQUEST;
import com.wgAbrechnung.wg_abrechnung.MainActivity;
import com.wgAbrechnung.wg_abrechnung.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class DashboardFragment extends Fragment implements HTTP_REQUEST.AsyncResponse {

    private DashboardViewModel dashboardViewModel;
    AnyChartView anyChartView;
    private String USER_ID = "";
    private String CURRENT_PROJEKT = "";
    private Integer MODE = 0;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                ViewModelProviders.of(this).get(DashboardViewModel.class);
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);

        MainActivity main  = (MainActivity) getActivity();
        USER_ID = main.GET_ID();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        CURRENT_PROJEKT = sharedPreferences.getString("CURRENT_PROJEKT", "");

        anyChartView = root.findViewById(R.id.CHART);

        String urlWebService = "http://saufkumpanen.ddns.net/AppConnect/connect.php?MODE=6&PROJEKT_TOKEN=" + CURRENT_PROJEKT;
        MODE = 6;
        new HTTP_REQUEST(this).execute(urlWebService);

        return root;
    }

    @Override
    public void processFinish(String output) {

        try {
            JSONArray jsonArray = new JSONArray(output);
            List<DataEntry> data = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                boolean isSet = false;
                //Betrag formatieren
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String strBetrag = jsonObject.getString("BETRAG");
                strBetrag = strBetrag.replace("€","");
                strBetrag = strBetrag.replace(",",".");
                jsonObject.put("BETRAG",strBetrag);

                if(data.size() == 0){
                    data.add(new ValueDataEntry(jsonObject.getString("NAME"), jsonObject.getDouble("BETRAG")));
                }else {

                    for (DataEntry Entry : data) {
                        if (Entry.getValue("x").equals(jsonObject.getString("NAME"))) {
                            isSet = true;
                        }
                    }

                        if (isSet) {
                            for (DataEntry Entry : data) {
                                if (Entry.getValue("x").equals(jsonObject.getString("NAME"))) {
                                    Double oldBetrag = Double.parseDouble(Entry.getValue("value").toString());
                                    Double newBetrag = Double.parseDouble(strBetrag) + oldBetrag;
                                    Entry.setValue("value", newBetrag);
                                }
                            }
                        } else {
                            data.add(new ValueDataEntry(jsonObject.getString("NAME"), jsonObject.getDouble("BETRAG")));
                        }
                    }
        }

        Pie pie = AnyChart.pie();
            anyChartView.setChart(pie);
            pie.data(data);

        }catch (JSONException e) {
            System.out.println(e);
        }




    }
}
