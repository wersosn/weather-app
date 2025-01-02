package com.example.projektsm.sensors;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LocationActivity {
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private static final String TAG = "LocationActivity";
    private final Context context;
    private final FusedLocationProviderClient fusedLocationClient;
    private final LocationCallbackInterface callbackInterface;

    // Interfejs do przekazywania danych
    public interface LocationCallbackInterface {
        void onLocationRetrieved(double latitude, double longitude, String cityName);
        void onLocationError(String message);
    }

    // Konstruktor
    public LocationActivity(Context context, LocationCallbackInterface callbackInterface) {
        this.context = context;
        this.callbackInterface = callbackInterface;
        this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
    }

    // Żądanie uprawnień lokalizacji i pobieranie lokalizacji
    public void requestLocationPermissionAndFetch(Activity activity) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    activity,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION
            );
        } else {
            fetchLocation();
        }
    }

    // Pobieranie ostatniej znanej lokalizacji
    public void fetchLocation() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(location -> {
                        if (location != null) {
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();
                            String cityName = getCityFromLocation(latitude, longitude);
                            callbackInterface.onLocationRetrieved(latitude, longitude, cityName);
                        } else {
                            callbackInterface.onLocationError("Nie udało się pobrać lokalizacji");
                        }
                    })
                    .addOnFailureListener(e -> callbackInterface.onLocationError("Error podczas pobierania lokalizacji: " + e.getMessage()));
        } else {
            callbackInterface.onLocationError("Brak uprawnień do lokalizacji");
        }
    }

    // Odczytywanie miasta z lokalizacji
    private String getCityFromLocation(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                return addresses.get(0).getLocality();
            } else {
                Log.e(TAG, "Brak wyników geokodowania dla współrzędnych");
                return null;
            }
        } catch (IOException e) {
            Log.e(TAG, "Error geokodowania: " + e.getMessage());
            return null;
        }
    }
}
