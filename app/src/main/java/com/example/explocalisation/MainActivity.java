package com.example.explocalisation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    Button btn;
    TextView txt1,txt2,txt3,txt4,txt5;

    FusedLocationProviderClient lFusProvider;
    LocationCallback lCallback;
     @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lFusProvider = LocationServices.getFusedLocationProviderClient(this);
        txt1=(TextView)findViewById(R.id.txt1);
        txt2=(TextView)findViewById(R.id.txt2);
        txt3=(TextView)findViewById(R.id.txt3);
        txt4=(TextView)findViewById(R.id.txt4);
        txt5=(TextView)findViewById(R.id.txt5);
        btn = (Button) findViewById(R.id.bt_location);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Si la permission est accordée
                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) {
                        Localiser();
                } else {
                    //Si la permission n'est pas accordée la demander
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                }
            }
        });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Si la permission a été demandée et accordée
                       Localiser();
                } else {
                    //SI la permission a été demandée et refusée
                    Toast.makeText(getApplicationContext(), "not granted",
                            Toast.LENGTH_SHORT).show();

                }
        }

    }

    void Localiser() {
    try {
        //paramétrer les conditions pour la localisation
        LocationRequest lReq =
                new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000)
                        .setWaitForAccurateLocation(false)
                        .setMinUpdateIntervalMillis(5000) //en millisecondes
                        .setMaxUpdateDelayMillis(10) //en mètres
                        .build();
        //paramétrer le callback pour la localisation
        lCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                txt1.setText("Latitude:" + locationResult.getLastLocation().getLatitude());
                txt2.setText("Longitude:" + locationResult.getLastLocation().getLongitude());
                try {
                    afficherOtherData(locationResult.getLastLocation());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        //FusedProider est recommandé par Google car il optimise l'energie
        //et ne consomme pas beaucoup de ressources
        //Demander la localisation
        lFusProvider.requestLocationUpdates(lReq, lCallback, null);
    }
       catch (SecurityException ex)
       {
            //error reaction
       }
    }
        private void afficherOtherData(Location loc) throws IOException {
            Geocoder geocoder = new Geocoder(this,
                    Locale.getDefault()) ; //Langue par défaut
            List<Address> addresses = geocoder.getFromLocation(
                    loc.getLatitude(), loc.getLongitude(), 1);
           txt3.setText("Pays :" + addresses.get(0).getCountryName());
            txt4.setText("Localité :" + addresses.get(0).getLocality());
            txt5.setText("Adresse :" + addresses.get(0).getAddressLine(0));
              }

    @Override
    protected void onPause() {
        super.onPause();
        if(lFusProvider!=null ) {
            if(lCallback!=null) {
                lFusProvider.removeLocationUpdates(lCallback);
            }
            lCallback=null;
            lFusProvider=null;
            txt1.setText("");txt2.setText("");txt3.setText("");txt4.setText("");txt5.setText("");
        }
    }
}

