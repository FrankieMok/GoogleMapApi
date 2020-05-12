package com.example.googlemapapi;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

public class GoogleMapAcivity extends AppCompatActivity implements OnMapReadyCallback{

	public void onMapReady(GoogleMap googleMap) {
		Toast.makeText(this, "GoogleMap is Ready", Toast.LENGTH_SHORT).show();
		mMap = googleMap;

//		mMap.clear();
	}


	private static final String TAG = "GoogleMapActivity";

	private static final String fineLocationPermission = Manifest.permission.ACCESS_FINE_LOCATION;
	private static final String coarseLocationPermission = Manifest.permission.ACCESS_COARSE_LOCATION;
	private static final int LOCATION_PERMISSION_REQUEST_CODE = 0;
	private GoogleMap mMap;


	private Boolean LocationPermissionGranted = false;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.googlemapview);

		getLocationPermission();

	}

	private void getLocationPermission() {
		Log.d(TAG, "getLocationPermission: getting location permissions");

		String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
		Manifest.permission.ACCESS_COARSE_LOCATION};

		if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
				fineLocationPermission) == PackageManager.PERMISSION_GRANTED) {
			if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
					coarseLocationPermission) == PackageManager.PERMISSION_GRANTED) {
				LocationPermissionGranted = true;
			} else {
				ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
			}
		} else {
			ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
			}
		}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		LocationPermissionGranted = false;

		switch (requestCode) {
			case LOCATION_PERMISSION_REQUEST_CODE: {
				if (grantResults.length > 0) {
					for (int i = 0; i < grantResults.length; i++) {
						if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
						LocationPermissionGranted = false;
//						Intent intent = new Intent(GoogleMapAcivity.this, MainActivity.class);
						return;
					}
				}
				LocationPermissionGranted = true;
				SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
						.findFragmentById(R.id.map);
				mapFragment.getMapAsync(GoogleMapAcivity.this);
			}
		}
		}
	}


}

