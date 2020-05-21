package com.example.googlemapapi;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
	private LocationRequest mLocationRequest;
	private LocationCallback mLocationCallback;
	private LocationManager mLocationManager;

	private static final String fineLocationPermission = Manifest.permission.ACCESS_FINE_LOCATION;
	private static final String coarseLocationPermission = Manifest.permission.ACCESS_COARSE_LOCATION;
	private static final int LOCATION_PERMISSION_REQUEST_CODE = 9001;
	private GoogleMap mMap;
	SupportMapFragment mapFragment;
	SearchView mSearchView;
	ImageView myLocationImageView;
	private int getLocationInterval = 60;
	private int getLocationFast = 10;

	private FusedLocationProviderClient mFusedLocationProviderClient;
	private static final float Map_ZOOM = 16f;

	private Boolean mLocationPermissionGranted;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.googlemapview);

		mapFragment = (SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map);
		mSearchView = (SearchView) findViewById(R.id.searchLocationBar);
		mLocationPermissionGranted = (Boolean) getIntent().getBooleanExtra("permissionB", false);
		mapFragment.getMapAsync(GoogleMapActivity.this);
		mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

		// Simple location search request.
		mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
			@Override
			public boolean onQueryTextSubmit(String query) {
				String searchLocation = mSearchView.getQuery().toString();
				List<Address> addressList = null;

				if (searchLocation != null || !searchLocation.equals("")) {
					Geocoder geocoder = new Geocoder(GoogleMapActivity.this);
					try {
						addressList = geocoder.getFromLocationName(searchLocation, 1);
					} catch (IOException e) {
						e.printStackTrace();
					}
					if (addressList.size() != 0) {
						Address address = addressList.get(0);
						LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
						mMap.addMarker(new MarkerOptions().position(latLng).title(searchLocation));
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
		myLocationImageView = (ImageView) findViewById(R.id.myLocationImageView);

		mLocationCallback = new LocationCallback() {
			@Override
			public void onLocationResult(LocationResult locationResult) {
				super.onLocationResult(locationResult);
				Log.d(TAG, String.valueOf(locationResult));
			}
		};


		mLocationRequest = LocationRequest.create();
		mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		mLocationRequest.setInterval(1000 * getLocationInterval); // 60 seconds, this app get location period
		mLocationRequest.setFastestInterval(1000 * getLocationFast); // 10 seconds,
		getCurrentLocation();


	}


	private void getCurrentLocation() {

		if (mLocationPermissionGranted) {

			Log.d(TAG, "Location permission approved");

			mFusedLocationProviderClient.getLocationAvailability().addOnSuccessListener(GoogleMapActivity.this, new OnSuccessListener<LocationAvailability>() {
				@Override
				public void onSuccess(LocationAvailability locationAvailability) {
					Log.d(TAG, "onSuccess: locationAvailability.isLocationAvailable " + locationAvailability.isLocationAvailable());
					locationEnabled (); // Check if Location service enable.

					Task<Location> locationTask = mFusedLocationProviderClient.getLastLocation();
					locationTask.addOnCompleteListener(new OnCompleteListener<Location>() {
						@Override
						public void onComplete(@NonNull Task<Location> task) {
							Location location = task.getResult();
							Log.d(TAG, "Location information!"+ String.valueOf(location));
							if (location == null) {
								if (ActivityCompat.checkSelfPermission(GoogleMapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(GoogleMapActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
									// TODO: Consider calling
									//    ActivityCompat#requestPermissions
									// here to request the missing permissions, and then overriding
									//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
									//                                          int[] grantResults)
									// to handle the case where the user grants the permission. See the documentation
									// for ActivityCompat#requestPermissions for more details.
									return;
								}
								mFusedLocationProviderClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
							}
							else {
								mFusedLocationProviderClient.getLastLocation().addOnSuccessListener(GoogleMapActivity.this, new OnSuccessListener<Location>() {
									@Override
									public void onSuccess(Location location) {
										updateUILocation(location);
									}
								});
							}
						}
					});
				}
			});

		}
	}

	// Update current location information.
	private void updateUILocation (Location location) {
		if (location !=null) {
			try {
				LatLng mLatLng = new LatLng(location.getLatitude(),location.getLongitude());
				Log.d(TAG, "get location");
				mMap.addMarker(new MarkerOptions().position(mLatLng).title("Current Location"));
				mMap.moveCamera(CameraUpdateFactory.newLatLng(mLatLng));
				mMap.animateCamera(CameraUpdateFactory.zoomTo(16f));
//								mMap.setMyLocationEnabled(true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}


	public void onMapReady(GoogleMap googleMap) {
		toastMessage("GoogleMap is Ready");
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
		}

		myLocationImageView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				getCurrentLocation();
			}
		});

		mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
			@Override
			public boolean onMarkerClick(Marker marker) {
				String  campId = marker.getId();
				String campsiteId = campIdList.get(Integer.parseInt(campId.substring(1)));
				toastMessage("Receiving information, please wait!" );
				Intent intent = new Intent(GoogleMapActivity.this, CampsiteActivity.class);
				intent.putExtra("assetId", campsiteId);
				startActivity(intent);
				return false;
			}
		});
	}

	@Override
	protected void onStop() {
		super.onStop();
		Log.d(TAG, "Remove the Location Request.");
		mFusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.d(TAG, "Resume the Location Request.");
		mFusedLocationProviderClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
	}

	// Update Google map icon.
	private BitmapDescriptor mBitmapDescriptor (Context context, int id) {
			Drawable vectorDrawable = ContextCompat.getDrawable(context, id);
			vectorDrawable.setBounds(0,0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
			Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(),
					Bitmap.Config.ARGB_8888);
			Canvas canvas = new Canvas(bitmap);
			vectorDrawable.draw(canvas);
			return BitmapDescriptorFactory.fromBitmap(bitmap);
		}

	/**
	 * customizable toast
	 * @param message
	 */
	private void toastMessage(String message){
		Toast.makeText(this,message, Toast.LENGTH_SHORT).show();
	}

	// Check if Location Enabled!
	private void locationEnabled () {
		 mLocationManager = (LocationManager) getSystemService(Context. LOCATION_SERVICE ) ;
		boolean gpsEnabled = false;
		boolean networkEnabled = false;
		try {
			gpsEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
			Log.d(TAG, "GPS enable is "+gpsEnabled);
		} catch (Exception e) {
			e.printStackTrace() ;
		}
		try {
			networkEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
			Log.d(TAG, "Network enable is " + networkEnabled);
		} catch (Exception e) {
			e.printStackTrace() ;
		}
		if (!gpsEnabled && !networkEnabled) {
			new AlertDialog.Builder(GoogleMapActivity.this).setMessage("Please Enable The GPS signal!").
					setPositiveButton( "Open Settings" , new DialogInterface.OnClickListener() {
								@Override
								public void onClick (DialogInterface paramDialogInterface , int paramInt)
								{
									startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
								}
							}).setNegativeButton( "Cancel" , null ).show() ;
		}
	}

}

