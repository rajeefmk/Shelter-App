package us.jaaga.cfi.shelterapp;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
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
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;




public class MainMaps extends ActionBarActivity{
	
	private GoogleMap mMap=null;
	private SupportMapFragment mMapFragment;
	private Marker customMarker;
	private LatLng markerLatLng;
	LatLng myLatLng;
	LatLngBounds.Builder bld;
	
	ArrayList<MarkerObject> mapMarkers = new ArrayList<MarkerObject>();
	
	TextView mStatus;
	Button mButton;
	ProgressDialog pDialog;
	String api_key;
	LatLngBounds llb;
	//private static final String url = "http://192.168.0.107:3000/api/shelters/";
	//private static final String url = " http://demo3635045.mockable.io/jsonarray";
	private static final String url = "http://demo3635045.mockable.io/latlng";
	
	private static String TAG = MainMaps.class.getSimpleName();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.test_data);
		
		try{
			
			ActionBar mActionBar = getActionBar();
			mActionBar.show();
			
		}catch(NullPointerException e) {
			
			e.printStackTrace();
		}catch(RuntimeException e) {
			
			e.printStackTrace();
		}
	
		
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
				
				//String jsonResponse = "";
				
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
					Log.i(TAG,"Object is added to ArrayList ");
					
					
					
					/*jsonResponse += "address " + address + "\n\n";
					jsonResponse += "latitude " + latitude + "\n\n";
					jsonResponse += "longitude " + longitude + "\n\n";*/
					
				}
				
				//setDisplay(jsonResponse);
				
				
				setUpMapIfNeeded(mapMarkers);
				Log.i(TAG,"SetUpMapifNeeded is Called");
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
        	Log.i(TAG,"Volley Object added to requst");
        
        		
	
		
	}
	
	private void setUpMapIfNeeded(ArrayList<MarkerObject> mapMarkers2) {
		
		Log.i(TAG,"Received mapMakers ArrayList" + mapMarkers2);
		
		if(mMap == null) {
			
			mMapFragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map));
			Log.i(TAG,"MapFragment is loaded");
			
			mMap = mMapFragment.getMap();
			Log.i(TAG,"Map is obtained from fragment");
			
			if(mMap != null) {
				
				/*setUpMap(mapMarkers);
				Log.i(TAG,"SetUpMap is called");*/
				
				for(int i=0; i<mapMarkers2.size(); i++) {
					
					customMarker = mMap.addMarker(new MarkerOptions()
					.position(new LatLng(mapMarkers.get(i).getLatitude(), mapMarkers.get(i).getLongitude()))
					.title(mapMarkers.get(i).getName())
					.snippet(mapMarkers.get(i).getContact()));
					
					Log.i(TAG,"CustomMarker Created ");
					
				}
				
				bld = new LatLngBounds.Builder();
				
				for(int i=0; i<mapMarkers.size(); i++) {
					
					markerLatLng = new LatLng(mapMarkers.get(i).getLatitude(), mapMarkers.get(i).getLongitude());
						
					  bld.include(markerLatLng);
				}
				
				llb = bld.build();
				
				
				
				try{
					mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(llb,15));
					Log.i(TAG, "mMap.animateCamera inside try is called");
					mMap.setMyLocationEnabled(true);
					
					mMap.setOnMyLocationButtonClickListener(new OnMyLocationButtonClickListener() {
						
						@Override
						public boolean onMyLocationButtonClick() {
							/*try{
								Thread.sleep(2000);
							}catch(InterruptedException e){
								
								e.printStackTrace();
								
							}*/
							
							runThread();
							
							//LatLngBounds.Builder bld = new LatLngBounds.Builder();
							
							return false;
						}
					});
					
					
			
				}catch (IllegalStateException e) {
				
					e.printStackTrace();
					
					final View mapView = getSupportFragmentManager().findFragmentById(R.id.map).getView();
					Log.i(TAG,"MapView is obtained ");
					
					if(mapView.getViewTreeObserver().isAlive()) {
						
						mapView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
							
									@Override
									public void onGlobalLayout() {
								
										if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
										mapView.getViewTreeObserver()
				                        .removeGlobalOnLayoutListener(this);
										} else {
											mapView.getViewTreeObserver()
											.removeOnGlobalLayoutListener(this);
										}
								
										mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(llb,1));
										Log.i(TAG, "mMap.animateCamera inside getViewTreeObs is called");
									}
						});
					
					};
						
					mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(llb,15));
					Log.i(TAG, "mMap.animateCamera outside getViewTreeObs  is called");
				
				};
			}
				
			
		}
	};
		
			
	
	protected void runThread() {
		
		Thread background = new Thread(){
			
			public void run() {
				
				try{
					
					sleep(2*1000);
					
					myLatLng = new LatLng(mMap.getMyLocation().getLatitude(), mMap.getMyLocation().getLatitude());
					bld.include(myLatLng);
					llb = bld.build();
					
					mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(llb,15));
					Log.i(TAG, "mMap.animateCamera inside runThread is called");
					
					
				}catch(Exception e){
					
					e.printStackTrace();
					
				}
			}
			
		};
		
		background.start();
		
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
