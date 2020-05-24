package com.wgAbrechnung.wg_abrechnung;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private static String SYSTEM_ID = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(navView, navController);



        //Wenn eine userID vergeben nichts machen
        //ansonsten wir eine erstellt und lokal gespeichert
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String strID = sharedPreferences.getString("UNIQUE_ID", "noID");

        if(strID == "noID") {
            String new_ID = UUID.randomUUID().toString();
            editor.putString("UNIQUE_ID", new_ID);
            editor.apply();

            String generated_ID = sharedPreferences.getString("UNIQUE_ID", "noID");
            SYSTEM_ID = generated_ID;
        }else{
            SYSTEM_ID = strID;
        }

    }


    public String GET_ID(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String strID = sharedPreferences.getString("UNIQUE_ID", "noID");
        return strID;

    }



}
