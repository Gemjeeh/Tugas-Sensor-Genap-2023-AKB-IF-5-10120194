package com.OmanSubadri_10120194_IF5;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.List;

public class LokasiMakanan extends AppCompatActivity implements OnMapReadyCallback, FetchData.FetchDataListener {
    private DrawerLayout drawerLayout;
    private ImageView ivMenu;
    private RecyclerView rvMenu;

    private double lat, lng;
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_CODE = 101;
    private ImageButton res;
    private boolean isShowingCurrentLocation = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lokasi_makanan);

        res = findViewById(R.id.res);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        res.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isShowingCurrentLocation) {
                    mMap.clear();
                    isShowingCurrentLocation = false;
                    StringBuilder stringBuilder = new StringBuilder("http://maps.googleapis.com/maps/api/place/nearbysearch/json?");
                    stringBuilder.append("location=" + lat + "," + lng);
                    stringBuilder.append("&radius=1000");
                    stringBuilder.append("&type=restaurant"); // aneh ga berhasil berhasil padahal di yutup sat set sat set jadi
                    stringBuilder.append("&sensor=true");
                    stringBuilder.append("&key=" + getResources().getString(R.string.google_maps_key));

                    String url = stringBuilder.toString();

                    FetchData fetchData = new FetchData(LokasiMakanan.this);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                        fetchData.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url);
                    } else {
                        fetchData.execute(url);
                    }
                } else {
                    getCurrentLocation();
                    isShowingCurrentLocation = true;
                }
            }
        });

        drawerLayout = findViewById(R.id.drawer_layout);
        ivMenu = findViewById(R.id.ivMenu);
        rvMenu = findViewById(R.id.rvMenu);
        rvMenu.setLayoutManager(new LinearLayoutManager(this));
        MainDrawerAdapter adapter = new MainDrawerAdapter(this, MainActivity.arrayList, MainActivity.image);
        rvMenu.setAdapter(adapter);

        ivMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
        }
    }

    private void getCurrentLocation() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(0);
        locationRequest.setFastestInterval(0);
        locationRequest.setNumUpdates(1);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.requestLocationUpdates(
                    locationRequest, locationCallback, Looper.myLooper()
            );
        }
    }

    private final LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location lastLocation = locationResult.getLastLocation();
            lat = lastLocation.getLatitude();
            lng = lastLocation.getLongitude();
            LatLng latLng = new LatLng(lat, lng);
            mMap.addMarker(new MarkerOptions().position(latLng).title("Lokasi Saya"));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        }
    };

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
    }

    @Override
    public void onFetchDataSuccess(List<HashMap<String, String>> placesList) {
        for (int i = 0; i < placesList.size(); i++) {
            HashMap<String, String> place = placesList.get(i);
            double lat = Double.parseDouble(place.get("lat"));
            double lng = Double.parseDouble(place.get("lng"));
            String placeName = place.get("place_name");
            String vicinity = place.get("vicinity");
            LatLng latLng = new LatLng(lat, lng);
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title(placeName + " : " + vicinity);
            mMap.addMarker(markerOptions);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(12));
        }
    }

    @Override
    public void onFetchDataFailure(String errorMessage) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFetchDataComplete(List<HashMap<String, String>> list) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }
}
//10120194_OmanSubadri_IF5