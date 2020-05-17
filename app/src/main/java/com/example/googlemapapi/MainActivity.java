package com.example.googlemapapi;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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
									String idv = campIdList.get(pos);
									toastMessage(name + "  " + idv );
									String urlDetail = "https://api.doc.govt.nz/v2/campsites/"+idv+"/detail";


								}
							});

						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				error.printStackTrace();
			}
		}) {
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
		Intent intent = new Intent(MainActivity.this, GoogleMapActivity.class);
		intent.putExtra("campId", campIdList);
		intent.putExtra("campName", campNameList);
		intent.putExtra("campStatus", campStatusList);
		intent.putExtra("campRegion", campRegionList);
		intent.putExtra("campMapLat", campMapLatList);
		intent.putExtra("campMapLon", campMapLonList);
		startActivity(intent);
	}


	/**
	 * customizable toast
	 * @param message
	 */
	private void toastMessage(String message){
		Toast.makeText(this,message, Toast.LENGTH_SHORT).show();
	}
}
