package com.example.googlemapapi;

import android.os.Bundle;
import android.widget.ListView;

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

	private ListView myListView;
	private ArrayList<String> campRegionList = new ArrayList<>();
	private ArrayList<String> campNameList = new ArrayList<>();
	private ArrayList<String> campStatusList = new ArrayList<>();
	private ArrayList<String> campIdList = new ArrayList<>();
	private ArrayList<String> campMapLatList = new ArrayList<>();
	private ArrayList<String> campMapLonList = new ArrayList<>();

	private RequestQueue mRequestQueue;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		myListView = (ListView) findViewById(R.id.campsitesListView);
		mRequestQueue = Volley.newRequestQueue(this);
		parseJSON();
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

							CustomAdapter customAdapter = new CustomAdapter(MainActivity.this, campRegionList, campNameList, campStatusList);
							myListView.setAdapter(customAdapter);

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
}
