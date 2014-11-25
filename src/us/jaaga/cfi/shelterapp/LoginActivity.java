package us.jaaga.cfi.shelterapp;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.AccountPicker;
import com.google.android.gms.common.SignInButton;


public class LoginActivity extends Activity {
	
	private static String TAG = LoginActivity.class.getSimpleName();
	private static final String url = "http://192.168.0.107:3000/api/android/auth/";
	private static final int REQUEST_CODE_PICK_ACCOUNT = 0;
	private static final int REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR = 0;
	SignInButton googlePlusButton;
	TextView mStatus;
	String mEmail;
	SharedPreferences mSharedP;
	ProgressDialog pDialog;
	String token;
	

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        mStatus = (TextView) findViewById(R.id.textview);
        googlePlusButton = (SignInButton) findViewById(R.id.btn_sign_in);
        
        googlePlusButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if(isDeviceOnline()){
					
					getUserName();
				}
				
				else{
					
					Toast.makeText(getApplicationContext(), "There is no Internet", Toast.LENGTH_LONG).show();
				}
				
			}

			
		});
        
        
    }
    
    private boolean isDeviceOnline() {
		
		ConnectivityManager connMgr = (ConnectivityManager) 
		        getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI); 
		boolean isWifiConn = networkInfo.isConnected();
		networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		boolean isMobileConn = networkInfo.isConnected();
		Log.d(TAG, "Wifi connected: " + isWifiConn);
		Log.d(TAG, "Mobile connected: " + isMobileConn);
		
		boolean network;
		
		if(isWifiConn == true){
			
			network = true;
			Log.i(TAG,"network is set true because of isWifiConn");
		}
		else if(isMobileConn == true){
			
			network = true;
			Log.i(TAG,"network is set true because of isMobileConn");
		}
		else {
			 network = false;
			 Log.i(TAG,"network is set false");
		}
		
		return network;
		
	}

	private void getUserName() {

		if(mEmail == null){
			Log.i(TAG,"pickUserAccount is called form getUsername");
			pickUserAccount();
			
		}else {
			
			if(isDeviceOnline()){
				
				pDialog = new ProgressDialog(this);
		        pDialog.setMessage("Please wait...");
		        pDialog.setCancelable(false);
		        showpDialog();
		        
		        new AsyncTokenAuth(LoginActivity.this,mEmail).execute();
		        Log.i(TAG,"AsyncToken Async task is passed arguments from getUsername");
				
			}
			else{
				
				Toast.makeText(getApplicationContext(), "There is no Internet", Toast.LENGTH_LONG).show();
			}
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
    
    private void pickUserAccount() {
    	
		String[] mAccountTypes = new String[]{"com.google"};
		Intent mAccountPicker = AccountPicker.newChooseAccountIntent(null, null, mAccountTypes, false, null, null, null, null);
		startActivityForResult(mAccountPicker, REQUEST_CODE_PICK_ACCOUNT);
    }
    
    protected void resultOfAuth(String result){
    	
    	token = result;
    	Log.i(TAG, result + " is passed to " + token);
    	
    	JsonObjectRequest jsonObjectToken = new JsonObjectRequest(Method.POST, url, null, new Response.Listener<JSONObject>() {
    		
			@Override
			public void onResponse(JSONObject response) {
				Log.i(TAG, response.toString());
				
				try{
					
					String api_key = response.getString("api_key");
					
					mSharedP = getSharedPreferences("shelterapp",MODE_PRIVATE);
			    	Editor mEditor = mSharedP.edit();
			    	mEditor.putString("api_key", api_key);
			    	mEditor.commit();
			    	Log.i("tag",api_key+"is stored in SP");
			    	
			    	Intent mIntent = new Intent(LoginActivity.this, MainMaps.class);
			    	Log.i("tag","Intent is intiated");
			    	startActivity(mIntent);
			    	Log.i("tag","Intent activity Started");
			    	
					
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
                Log.i(TAG, "Error: " + error);
			}
			
		}) {
			
        	public Map<String, String> getHeaders() throws AuthFailureError {
				
        		HashMap<String, String> headers = new HashMap<String, String>();
    			headers.put("Content-Type", "application/json");
    			headers.put("Android_Secret","Banjarapalya");
    			headers.put("Authorization", token);
    			
        		
        		return headers;
        	
        	}
	        		
       
	};
		
		jsonObjectToken.setRetryPolicy(new  DefaultRetryPolicy(
				50000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
			
			));
    
		AppController.getInstance().addToRequestQueue(jsonObjectToken);
    	
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
    	
    	if(requestCode == REQUEST_CODE_PICK_ACCOUNT){
			
			if(resultCode == RESULT_OK){
				
				mEmail = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
				getUserName();
			}
			else if(resultCode == RESULT_CANCELED) {
				
				Toast.makeText(this, "Please pick an account", Toast.LENGTH_LONG).show();
			}
		} 
		else if(requestCode == REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR){
			
			getUserName();
		}
    }

}
