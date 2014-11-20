package us.jaaga.cfi.shelterapp;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class TestPage extends FragmentActivity {
	
	private GoogleMap mMap;
	private SupportMapFragment mMapFragment;
	private Marker customMarker;
	private LatLng markerLatLng;
	
	ArrayList<MarkerObject> mapMarkers = new ArrayList<MarkerObject>();
	
	TextView mStatus;
	Button mButton;
	ProgressDialog pDialog;
	String api_key;
	//private static final String url = "http://192.168.0.107:3000/api/shelters/";
	//private static final String url = " http://demo3635045.mockable.io/jsonarray";
	private static final String url = "http://demo3635045.mockable.io/latlng";
	
	private static String TAG = TestPage.class.getSimpleName();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.test_data);
		
		
		//mStatus = (TextView) findViewById(R.id.testData);
		
		SharedPreferences mSharedP = getSharedPreferences("shelterapp",MODE_PRIVATE);
		api_key = mSharedP.getString("api_key", null);
		Log.i("tag",api_key+"is obtained from SP");
		
		pDialog = new ProgressDialog(this);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);
        showpDialog();
        
        JsonObjectRequest jsonObject = new JsonObjectRequest(Method.GET, url, null, new Response.Listener<JSONObject>() {

		@Override
			public void onResponse(JSONObject response) {
			Log.i(TAG, response.toString());
			
			try{
				
				String jsonResponse = "";
				
				JSONArray testArray = response.getJSONArray("shelters");
				
				for(int i =0; i<testArray.length(); i++){
					
					MarkerObject mMO = new MarkerObject();
					
					JSONObject member = (JSONObject) testArray.get(i);
					
					String name = member.getString("name");
					String contact = member.getString("contact");
					Double latitude = member.getDouble("latitude");
					Double longitude = member.getDouble("longitude");
					
					
					mMO.setName(name);
					mMO.setContact(contact);
					mMO.setLatitude(latitude);
					mMO.setLongitude(longitude);
					
					mapMarkers.add(mMO);
					
					
					
					/*jsonResponse += "address " + address + "\n\n";
					jsonResponse += "latitude " + latitude + "\n\n";
					jsonResponse += "longitude " + longitude + "\n\n";*/
					
				}
				
				//setDisplay(jsonResponse);
				setUpMapIfNeeded();
				hidepDialog();
				
			}catch  (JSONException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(),
                        "Error: " + e.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
		}
    	
    	
        	}, new Response.ErrorListener() {

		@Override
		public void onErrorResponse(VolleyError error) {
			VolleyLog.d(TAG, "Error: " + error.getMessage());
            Toast.makeText(getApplicationContext(),
                    error.getMessage(), Toast.LENGTH_SHORT).show();
            hidepDialog();
            Log.i(TAG, "Error: " + error.getMessage());
		}
		
	 }) {
    	
        	public Map<String, String> getHeaders() throws AuthFailureError {
			
	    		HashMap<String, String> headers = new HashMap<String, String>();
				headers.put("Content-Type", "application/json");
				headers.put("api_key",api_key);
    		
    		return headers;
    		
        	}
    	
        	};
    
    
        	//Adding request to request queue
        	AppController.getInstance().addToRequestQueue(jsonObject);
        
        		
	
		
	}
	
	private void setUpMapIfNeeded() {
		
		if(mMap == null) {
			
			mMapFragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map));
			mMap = mMapFragment.getMap();
			if(mMap != null) {
				
				setUpMap(mapMarkers);
			}
		}
		
	}
			
	private void setUpMap(ArrayList<MarkerObject> mapMarkers2) {
		
		//markerLatLng = new LatLng(12.830268,77.485662);
		
		for(int i=0; i<mapMarkers2.size(); i++) {
			
			customMarker = mMap.addMarker(new MarkerOptions()
			.position(new LatLng(mapMarkers.get(i).getLatitude(), mapMarkers.get(i).getLongitude()))
			.title(mapMarkers.get(i).getName())
			.snippet(mapMarkers.get(i).getContact()));
			
		}
		
		
		final View mapView = getSupportFragmentManager().findFragmentById(R.id.map).getView();
		if(mapView.getViewTreeObserver().isAlive()) {
			
			mapView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
				
				@Override
				public void onGlobalLayout() {
					
					/*LatLngBounds bounds = new LatLngBounds.Builder()
														  .include(markerLatLng).build();*/
					
					LatLngBounds.Builder bld = new LatLngBounds.Builder();
					for(int i=0; i<mapMarkers.size(); i++) {
						
						LatLng ll = new LatLng(mapMarkers.get(i).getLatitude(), mapMarkers.get(i).getLongitude());
						
						bld.include(ll);
					}
					
					LatLngBounds bounds = bld.build();
					
					if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
						
						mapView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
					}else {
						
						mapView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
					}
					
					mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds,50));
					
				}
			});
			
			
			
			
		}
	
		
	}

	private void hidepDialog() {
		if(pDialog.isShowing())
			pDialog.dismiss();
	}

	private void showpDialog() {
		if (!pDialog.isShowing())
			pDialog.show();
	}

	public void setDisplay(String result){
		
		if(result != null){
			mStatus.setText(result);
		}
		
		else{
			
			mStatus.setText("Error !!");
		}
		
		
	}

}
