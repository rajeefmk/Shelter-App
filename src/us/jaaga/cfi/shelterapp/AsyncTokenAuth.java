package us.jaaga.cfi.shelterapp;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.GooglePlayServicesAvailabilityException;
import com.google.android.gms.auth.UserRecoverableAuthException;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

public class AsyncTokenAuth extends AsyncTask<Void, Void, String> {
	
	LoginActivity mLogin;
	String Email;
	String Token;
	
	private static final String SCOPE = "oauth2:https://www.googleapis.com/auth/userinfo.profile https://www.googleapis.com/auth/userinfo.email";		 
	private static final String TAG = "AsyncTaskToken";
	private static final String url = "http://192.168.0.123:3000/api/android/auth/";
	private static final int REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR = 1001;
	
	ProgressDialog mProgressDialog;
	String response;
	String api_key;
	
	JSONObject mHeaderData;
	JSONObject responseData;
	
	public AsyncTokenAuth(LoginActivity mLA, String mail ){
		
		this.mLogin = mLA;
		this.Email = mail;
	
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		mProgressDialog = new ProgressDialog(mLogin);
		mProgressDialog.setMessage("Please wait...");
		mProgressDialog.setCancelable(false);
		mProgressDialog.show();
		
	}

	@Override
	protected String doInBackground(Void... params) {
		try{
			
			String token = fetchtoken();
			
			if(token != null){
				
				mHeaderData = new JSONObject();
				mHeaderData.put("Authorization", token);
				mHeaderData.put("Android_Secret","Banjarapalya");
				
				Log.i(TAG,token + " is stored in Global Variable");
				
				ServiceHandler sh = new ServiceHandler();
				response = sh.tokenAuthenticate(mHeaderData, url);
				Log.i(TAG,"Response obtainted");
				
				//Getting api_key from JSON Object
				responseData = new JSONObject(response);
				Log.i(TAG,"Response is passed to JSONObject");
				
				api_key = responseData.getString("api_key");
				Log.i(TAG,api_key + "is obtained");
				
				
			}
		}catch(IOException e){
			e.printStackTrace();
		}catch(JSONException e){
			
			e.printStackTrace();
		}
		
		return api_key;
	}
	
private String fetchtoken() throws IOException {
		
		try{
			
			return GoogleAuthUtil.getToken(mLogin, Email, SCOPE);
		}catch (GooglePlayServicesAvailabilityException playEx){
			
			playEx.getConnectionStatusCode();
		}catch (UserRecoverableAuthException useRec){
			
			mLogin.startActivityForResult(useRec.getIntent(), REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR)
;		}catch (GoogleAuthException fatalException){
	
		fatalException.printStackTrace();
		
		}catch (RuntimeException e){
			
			e.printStackTrace();
			
		}catch (IOException e){
			
			e.printStackTrace();
		}
		
		return null;
	}
	
	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		
		if (mProgressDialog.isShowing())
			mProgressDialog.dismiss();
		
		
		mLogin.resultOfAuth(result);
		Log.i(TAG,result + "Is passed to main thread");
		
		
	}

}
