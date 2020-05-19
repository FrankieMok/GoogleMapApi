package com.example.googlemapapi;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

	private static final String TAG = "ListDataActivity";

	private ListView myListView;
	private ArrayList<String> campRegionList = new ArrayList<>();
	private ArrayList<String> campNameList = new ArrayList<>();
	private ArrayList<String> campStatusList = new ArrayList<>();
	private ArrayList<String> campIdList = new ArrayList<>();
	private ArrayList<String> campMapLatList = new ArrayList<>();
	private ArrayList<String> campMapLonList = new ArrayList<>();
	private Button currentLocation;

	private RequestQueue mRequestQueue;
	private Boolean mLocationPermissionGranted = false;

	private static final String fineLocationPermission = Manifest.permission.ACCESS_FINE_LOCATION;
	private static final String coarseLocationPermission = Manifest.permission.ACCESS_COARSE_LOCATION;
	private static final int LOCATION_PERMISSION_REQUEST_CODE = 9001;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		myListView = (ListView) findViewById(R.id.campsitesListView);
		currentLocation = (Button) findViewById(R.id.locationButton);
		mRequestQueue = Volley.newRequestQueue(this);
		parseJSON();

		final AlertDialog.Builder customAlert = new AlertDialog.Builder(MainActivity.this);
		View mView = getLayoutInflater().inflate(R.layout.custom_dialog, null);

		TextView campName = (TextView) mView.findViewById(R.id.TextView_name);
		TextView campLocation = (TextView) mView.findViewById(R.id.TextView_location);
		TextView campIntroduction = (TextView) mView.findViewById(R.id.TextView_introduction);

	}

	private void parseJSON() {
		String url = "https://api.doc.govt.nz/v2/campsites?coordinates=wgs84";

		JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
				new Response.Listener<JSONArray>() {
					@Override
					public void onResponse(JSONArray response) {
						try {
							for (int i = 0; i < response.length(); i++) {
								JSONObject jsonobject = response.getJSONObject(i);
								campIdList.add(jsonobject.getString("assetId"));
								campNameList.add(jsonobject.getString("name"));
								campStatusList.add(jsonobject.getString("status"));
								campRegionList.add(jsonobject.getString("region"));
								campMapLatList.add(jsonobject.getString("lat"));
								campMapLonList.add(jsonobject.getString("lon"));
							}

							CustomAdapter customAdapter = new CustomAdapter(MainActivity.this, campNameList, campRegionList, campStatusList);
							myListView.setAdapter(customAdapter);

							myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
								@Override
								public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
									String name = adapterView.getItemAtPosition(position).toString();
									int pos = adapterView.getPositionForView(view);
									Log.d(TAG, "onItemClick: You clicked on " + name +", Id: " + pos);
									String campsiteId = campIdList.get(pos);
									Intent intent = new Intent(MainActivity.this, CampsiteActivity.class);
									intent.putExtra("assetId", campsiteId);
									startActivity(intent);

								}
							});

						} catch (JSONException e) {
							e.printStackTrace();
						}

					}
				}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				Log.e(TAG, error.getLocalizedMessage() );
				error.printStackTrace();
			}
		}) {
			// Create the header information, json and api-key
		@Override
		public Map<String, String> getHeaders() {
			HashMap<String, String> headers = new HashMap<String, String>();
			headers.put("accept", "application/json");
			headers.put("x-api-key", "EbVPXKIztf2zZ6rmp6lwzayEpXva9Jto9ezdh4w9");
			return headers;
		}
	};

		mRequestQueue.add(request);
	}

	public void currentLocationListener(View view) {

		getLocationPermission();


	}


	/**
	 * customizable toast
	 * @param message
	 */
	private void toastMessage(String message){
		Toast.makeText(this,message, Toast.LENGTH_SHORT).show();
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
				Intent intent = new Intent(MainActivity.this, GoogleMapActivity.class);
				intent.putExtra("campId", campIdList);
				intent.putExtra("campName", campNameList);
				intent.putExtra("campStatus", campStatusList);
				intent.putExtra("campRegion", campRegionList);
				intent.putExtra("campMapLat", campMapLatList);
				intent.putExtra("campMapLon", campMapLonList);
				intent.putExtra("permissionB", mLocationPermissionGranted);
				startActivity(intent);
			} else {
				ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
			}
		} else {
			ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
		}
	}

}
