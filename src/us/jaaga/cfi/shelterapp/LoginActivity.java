package us.jaaga.cfi.shelterapp;

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.AccountPicker;
import com.google.android.gms.common.SignInButton;


public class LoginActivity extends Activity {
	
	protected static final String TAG = "debug";
	private static final int REQUEST_CODE_PICK_ACCOUNT = 0;
	private static final int REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR = 0;
	SignInButton googlePlusButton;
	TextView mStatus;
	String mEmail;
	

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
				
				new AsyncTokenAuth(LoginActivity.this,mEmail).execute();
				Log.i(TAG,"AsyncToken Async task is passed arguments from getUsername");
			}
			else{
				
				Toast.makeText(getApplicationContext(), "There is no Internet", Toast.LENGTH_LONG).show();
			}
		}
		
		
	}
    
    private void pickUserAccount() {
    	
		String[] mAccountTypes = new String[]{"com.google"};
		Intent mAccountPicker = AccountPicker.newChooseAccountIntent(null, null, mAccountTypes, false, null, null, null, null);
		startActivityForResult(mAccountPicker, REQUEST_CODE_PICK_ACCOUNT);
    }
    
    protected void resultOfAuth(String result){
	
    	Log.i(TAG,"resultofAuth is called");
    	mStatus.setText(result);
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
