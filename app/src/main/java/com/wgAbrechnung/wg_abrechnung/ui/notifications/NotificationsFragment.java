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

import com.google.firebase.firestore.FirebaseFirestore;
import com.wgAbrechnung.wg_abrechnung.CustomListAdapter;
import com.wgAbrechnung.wg_abrechnung.HTTP_REQUEST;
import com.wgAbrechnung.wg_abrechnung.MainActivity;
import com.wgAbrechnung.wg_abrechnung.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.UUID;

public class NotificationsFragment extends Fragment implements Toolbar.OnMenuItemClickListener,HTTP_REQUEST.AsyncResponse {

    private NotificationsViewModel notificationsViewModel;

    private String USER_ID = "";
    ArrayList<String> ListName = new ArrayList<String>();
    ArrayList<String> ListID = new ArrayList<String>();
    ArrayList<String> ListNR = new ArrayList<String>();

    ListView listView;
    private Toolbar toolbar;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private Integer MODE = 0;


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


        String urlWebService = "http://192.168.2.120/AppCOnnect/connect.php?MODE=2&USER_TOKEN=" + USER_ID;
        MODE = 2;
        new HTTP_REQUEST(this).execute(urlWebService);

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
                AddProjektDialog();
                break;
            case R.id.NEW_PROJEKT:
                NewProjektDialog();
                break;
        }

        return false;
    }

    public void AddProjektDialog(){
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
                ADD_PROJEKT(inputNR);

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

    public void NewProjektDialog(){

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

                NEW_PROJEKT(strProjektName, PROJEKT_ID);

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

    public void NEW_PROJEKT(String strName, String ProjektID){

        String urlWebService = "http://192.168.2.120/AppCOnnect/connect.php?MODE=1&NAME="+ strName +"&USER_TOKEN=" + USER_ID + "&PROJEKT_TOKEN="+ ProjektID;
        MODE = 1;
        new HTTP_REQUEST(this).execute(urlWebService);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("CURRENT_PROJEKT", ProjektID);
        editor.apply();
    }


    public void ADD_PROJEKT(Long inputNR){
        String urlWebService = "http://192.168.2.120/AppCOnnect/connect.php?MODE=3&USER_TOKEN=" + USER_ID + "&ID="+ inputNR;
        MODE = 3;
        new HTTP_REQUEST(this).execute(urlWebService);

        //@TODO CURRENT_PROJEKT setzen
    }


    @Override
    public void processFinish(String output) {

        Context context = getActivity().getApplicationContext();

        switch (MODE)
        {
            case 1:
            case 3:
                MODE = 0;

                CharSequence text = "Projekt erfolgreich angelegt!";
                Toast toast = Toast.makeText(context, text, Toast.LENGTH_LONG);
                toast.show();

            break;
            case 2:
                MODE = 0;
                try {
                    JSONArray jsonArray = new JSONArray(output);
                    for (int i=0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        ListName.add(jsonObject.getString("NAME"));
                        ListID.add(jsonObject.getString("PROJEKT_TOKEN"));
                        ListNR.add(jsonObject.getString("ID"));

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
                    //Liste aufbauen
                    Activity activity = getActivity();
                    CustomListAdapter Listadapter = new CustomListAdapter(activity, arrNamen, arrNR , IDArray);
                    listView.setAdapter(Listadapter);

                } catch (JSONException e){
                    System.out.println(e);
                }

            break;
        }
    }
}
