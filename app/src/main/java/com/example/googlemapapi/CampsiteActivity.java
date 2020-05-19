package com.example.googlemapapi;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CampsiteActivity extends AppCompatActivity {

	private static final String TAG = "CampsiteActivity";
	private RequestQueue mRequestQueue;
	private String campName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.campsite_item);


		mRequestQueue = Volley.newRequestQueue(this);
		parseJSON();

//		campNameTextView.setText(campName);
	}

	private void parseJSON() {
		final String campsiteId = this.getIntent().getStringExtra("assetId");
		String urlDetail = "https://api.doc.govt.nz/v2/campsites/" + campsiteId + "/detail";
		final TextView campNameTextView = (TextView) findViewById(R.id.campNameTextView);
		final ImageView campImageView = (ImageView) findViewById(R.id.campImageView);
		final TextView campLocationTextView = (TextView) findViewById(R.id.campLocationTextView);
		final TextView campIntroductionTextView = (TextView) findViewById(R.id.campIntroductionTextView);
		final TextView campFacilitiesTextView = (TextView) findViewById(R.id.campFacilitiesTextView);


		final JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, urlDetail, null,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {

						try {
							String campImageURL = response.getString("introductionThumbnail");
							Picasso.get().load(campImageURL).fit().into(campImageView);
							campNameTextView.setText(response.getString("name"));
							campLocationTextView.setText(response.getString("locationString"));
							campIntroductionTextView.setText(response.getString("introduction"));
							String fac = response.getString("facilities");
							campFacilitiesTextView.setText(fac.substring(1,fac.length()-1));
							final String campSiteURL = response.getString("staticLink");

							campImageView.setOnClickListener(new View.OnClickListener() {
								@Override
								public void onClick(View v) {
									Intent campsite = new Intent(Intent.ACTION_VIEW);
									campsite.setData(Uri.parse(campSiteURL));
									startActivity(campsite);
								}
							});

						} catch (JSONException e) {
							e.printStackTrace();
						}


					}
				}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				toastMessage("Campsite information is missing, please check it later!");
//				Log.e(TAG, error.getLocalizedMessage() );
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

	/**
	 * customizable toast
	 * @param message
	 */
	private void toastMessage(String message){
		Toast.makeText(this,message, Toast.LENGTH_SHORT).show();
	}

}
