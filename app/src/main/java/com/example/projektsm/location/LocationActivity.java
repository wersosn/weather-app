package com.example.projektsm.sensors;

import static androidx.core.content.ContextCompat.getString;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
//import android.location.LocationRequest;
import android.os.Looper;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.example.projektsm.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
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
    private LocationCallback locationCallback;

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
        // Wersja, gdzie potrzeba chwili na to aby zajarzyło
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(10000); // Co 10 sekund
            locationRequest.setFastestInterval(5000); // Minimalny czas między aktualizacjami

            // Utwórz instancję LocationCallback tylko raz
            if (locationCallback == null) {
                locationCallback = new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        if (locationResult != null) {
                            fusedLocationClient.removeLocationUpdates(locationCallback); // Zatrzymanie aktualizacji
                            Location location = locationResult.getLastLocation();
                            if (location != null) {
                                double latitude = location.getLatitude();
                                double longitude = location.getLongitude();
                                String cityName = getCityFromLocation(latitude, longitude);
                                callbackInterface.onLocationRetrieved(latitude, longitude, cityName);
                                Log.d(TAG, "latitude: " + latitude + ", longitude: " + longitude + ", city: " + cityName);
                            } else {
                                callbackInterface.onLocationError(context.getString(R.string.no_location));
                            }
                        }
                    }
                };
            }

            // Żądanie aktualizacji lokalizacji
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        } else {
            callbackInterface.onLocationError(context.getString(R.string.no_location_permission));
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
