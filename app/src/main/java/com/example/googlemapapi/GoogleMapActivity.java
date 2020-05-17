package com.example.googlemapapi;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GoogleMapActivity extends AppCompatActivity implements OnMapReadyCallback{


	private static final String TAG = "GoogleMapActivity";

	private ArrayList<String> campRegionList = new ArrayList<>();
	private ArrayList<String> campNameList = new ArrayList<>();
	private ArrayList<String> campStatusList = new ArrayList<>();
	private ArrayList<String> campIdList = new ArrayList<>();
	private ArrayList<String> campMapLatList = new ArrayList<>();
	private ArrayList<String> campMapLonList = new ArrayList<>();

	private static final String fineLocationPermission = Manifest.permission.ACCESS_FINE_LOCATION;
	private static final String coarseLocationPermission = Manifest.permission.ACCESS_COARSE_LOCATION;
	private static final int LOCATION_PERMISSION_REQUEST_CODE = 9001;
	private GoogleMap mMap;
	SupportMapFragment mapFragment;
	SearchView mSearchView;

	private FusedLocationProviderClient mFusedLocationProviderClient;
	private static final float Map_ZOOM = 16f;

	private Boolean mLocationPermissionGranted = false;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.googlemapview);

		mapFragment = (SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map);
		mSearchView = (SearchView) findViewById(R.id.searchLocationBar);

		getLocationPermission();

		mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
			@Override
			public boolean onQueryTextSubmit(String query) {
				String searchlocation = mSearchView.getQuery().toString();
				List<Address> addressList = null;

				if (searchlocation != null || !searchlocation.equals("")) {
					Geocoder geocoder = new Geocoder(GoogleMapActivity.this);
					try {
						addressList = geocoder.getFromLocationName(searchlocation, 1);
					} catch (IOException e) {
						e.printStackTrace();
					}
					if (addressList.size() != 0) {
						Address address = addressList.get(0);
						LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
						mMap.addMarker(new MarkerOptions().position(latLng).title(searchlocation));
						mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, Map_ZOOM));
					} else
						Toast.makeText(GoogleMapActivity.this, "Unable to get Search!", Toast.LENGTH_SHORT).show();
					}
					return false;
				}

			@Override
			public boolean onQueryTextChange(String newText) {
				return false;
			}
		});

	}

	private void getCurrentLocation() {

		Log.d(TAG, "getCurrentLocation: get user current location");

		mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

		try{
			if(mLocationPermissionGranted){

				final Task location = mFusedLocationProviderClient.getLastLocation();
				location.addOnCompleteListener(new OnCompleteListener() {
					@Override
					public void onComplete(@NonNull Task task) {
						if(task.isSuccessful()){
							Log.d(TAG, "onComplete: found location!");
							Location currentLocation = (Location) task.getResult();
							LatLng cLocation = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
							mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cLocation, Map_ZOOM));
							mMap.addMarker(new MarkerOptions().position(cLocation).title("Current Location").
									icon(mBitmapDescriptor(getApplicationContext(),R.drawable.ic_person_pin_black_24dp)));
						}else{
							Log.d(TAG, "onComplete: current location is null");
							Toast.makeText(GoogleMapActivity.this, "unable to get current location", Toast.LENGTH_SHORT).show();
						}
					}
				});
			}
		}catch (SecurityException e){
			Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage() );
		}
	}


	private void getLocationPermission() {
		Log.d(TAG, "getLocationPermission: get location permissions");

		String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
		Manifest.permission.ACCESS_COARSE_LOCATION};

		if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
				fineLocationPermission) == PackageManager.PERMISSION_GRANTED) {
			if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
					coarseLocationPermission) == PackageManager.PERMISSION_GRANTED) {
				mLocationPermissionGranted = true;

				mapFragment.getMapAsync(GoogleMapActivity.this);
			} else {
				ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
			}
		} else {
			ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
			}
		}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		Log.d(TAG, "onRequestPermissionResult: Started");
//		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		mLocationPermissionGranted = false;

		switch (requestCode) {
			case LOCATION_PERMISSION_REQUEST_CODE: {
				if (grantResults.length > 0) {
					for (int i = 0; i < grantResults.length; i++) {
						if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
						mLocationPermissionGranted = false;
						Log.d(TAG, "onRequestPermissionResult: Failed!!");
						return;
					}
				}
				mLocationPermissionGranted = true;
					Log.d(TAG, "onRequestPermissionResult: Granted.");
//				SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
//						.findFragmentById(R.id.map);
				mapFragment.getMapAsync(GoogleMapActivity.this);
			}
		}
		}
	}

	public void onMapReady(GoogleMap googleMap) {
		Toast.makeText(this, "GoogleMap is Ready", Toast.LENGTH_SHORT).show();
		Log.d(TAG, "onMapReady: Google Map is ready");
		mMap = googleMap;
		mMap.setBuildingsEnabled(true);

		campIdList = (ArrayList<String>) getIntent().getSerializableExtra("campId");
		campNameList = (ArrayList<String>) getIntent().getSerializableExtra("campName");
		campStatusList = (ArrayList<String>) getIntent().getSerializableExtra("campStatus");
		campRegionList = (ArrayList<String>) getIntent().getSerializableExtra("campRegion");
		campMapLatList = (ArrayList<String>) getIntent().getSerializableExtra("campMapLat");
		campMapLonList = (ArrayList<String>) getIntent().getSerializableExtra("campMapLon");


		for (int i=0;i<campIdList.size();i++){
			String nName = campNameList.get(i);
			LatLng latLng = new LatLng(Float.parseFloat(campMapLatList.get(i)),Float.parseFloat(campMapLonList.get(i)));
			mMap.addMarker(new MarkerOptions().position(latLng).title(nName).
					icon(mBitmapDescriptor(getApplicationContext(),R.drawable.ic_home_black_24dp)));
//			mMap.animateCamera(CameraUpdateFactory.zoomTo(Map_ZOOM));
//			mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
		}
		if (mLocationPermissionGranted) {
			getCurrentLocation();

			if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
					!= PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
					Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
				return;
			}
			mMap.setMyLocationEnabled(true);

		}
	}

		private BitmapDescriptor mBitmapDescriptor (Context context, int id) {
			Drawable vectorDrawable = ContextCompat.getDrawable(context, id);
			vectorDrawable.setBounds(0,0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
			Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(),
					Bitmap.Config.ARGB_8888);
			Canvas canvas = new Canvas(bitmap);
			vectorDrawable.draw(canvas);
			return BitmapDescriptorFactory.fromBitmap(bitmap);
		}
}

