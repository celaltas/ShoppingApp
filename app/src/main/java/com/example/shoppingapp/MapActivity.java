package com.example.shoppingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback{

    private  GoogleMap map;
    private EditText edtSearch;
    private String id;
    private FirebaseFirestore firestore;
    private ImageButton showCities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);


        firestore = FirebaseFirestore.getInstance();
        edtSearch = findViewById(R.id.edtSearch);
        showCities = findViewById(R.id.showCities);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        showCities.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCityName();
            }
        });



        edtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == (EditorInfo.IME_ACTION_SEARCH)
                        ||(actionId == (EditorInfo.IME_ACTION_DONE))
                        || ((event.getAction() ==KeyEvent.ACTION_DOWN))
                        || ((event.getAction() ==KeyEvent.KEYCODE_ENTER))){

                    geoLocate();
                }
                return false;
            }
        });

        if (getIntent().getExtras() != null) {
            id = getIntent().getStringExtra("ShoppingID");

        }


    }

    private void getCityName() {
        ArrayList<String> listCities = new ArrayList<>();
        firestore.collection("Shoppings")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map map = document.getData();
                                if(map.containsKey("location")){
                                    System.out.println("map şehir: "+map.get("location").toString());
                                    String cityName = map.get("location").toString();
                                    listCities.add(cityName);
                                    showOnMapCities(listCities);
                                }

                            }
                        } else {
                            System.out.println("Error getting documents." +task.getException());
                        }
                    }
                });

    }

    private void showOnMapCities(ArrayList<String> listCities) {
        Geocoder geocoder = new Geocoder(MapActivity.this);
        List<Address> list = new ArrayList<>();
        for (String city : listCities) {
            try {
                list = geocoder.getFromLocationName(city, 1);
            } catch (IOException e) {
                System.out.println("Error" + e.toString());
            }
            if (list.size() > 0) {
                Address address = list.get(0);

                LatLng newPlace = new LatLng(address.getLatitude(), address.getLongitude());
                map.addMarker(new MarkerOptions()
                        .position(newPlace)
                        .title(address.getFeatureName()));
                map.animateCamera(CameraUpdateFactory.zoomTo(5.0f));
            }

        }
    }


    private void geoLocate() {

        String searchString = edtSearch.getText().toString();
        Geocoder geocoder = new Geocoder(MapActivity.this);
        List<Address> list = new ArrayList<>();
        try {
            list = geocoder.getFromLocationName(searchString,1);
        }catch (IOException e){
            System.out.println("Error" + e.toString());
        }
        if(list.size()>0){
            Address address = list.get(0);
            System.out.println("adres" + address.toString());
            LatLng newPlace = new LatLng(address.getLatitude(),address.getLongitude());
            map.addMarker(new MarkerOptions()
                    .position(newPlace)
                    .title(address.getFeatureName()));
            //map.moveCamera(CameraUpdateFactory.newLatLngZoom(newPlace,10));
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(newPlace, 10), 1000, new GoogleMap.CancelableCallback() {
                @Override
                public void onFinish() {
                    edtSearch.setText("");
                    updateLocation(id,address.getFeatureName());
                    Toast.makeText(MapActivity.this, "Got the location and returning back", Toast.LENGTH_SHORT).show();

                    try {
                        Thread.sleep(4000);

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Intent intent = new Intent(MapActivity.this, ShopDetails.class);
                    intent.putExtra("ShoppingID", id);
                    startActivity(intent);
                }

                @Override
                public void onCancel() {

                }
            });












        }

    }

    private void updateLocation(String id, String featureName) {
        DocumentReference docRef = firestore.collection("Shoppings").document(id);
        docRef.update("location", featureName);
    }

    @Override
    public void onMapReady(@NonNull  GoogleMap googleMap) {
        map = googleMap;

        LatLng istanbul = new LatLng(41.015137, 28.979530);

        map.moveCamera(CameraUpdateFactory.newLatLng(istanbul));





    }
}