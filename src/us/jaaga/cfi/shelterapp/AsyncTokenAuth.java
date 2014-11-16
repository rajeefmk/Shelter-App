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
	
	JSONObject mHeaderData;
	
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
				mHeaderData.put("Android_Secret"," 814035995890-cr3rvvteqpmvr1m0llnlndtphb7bta3h");
				
				
				
				
				Log.i(TAG,Token + " is stored in Global Variable");
				
				ServiceHandler sh = new ServiceHandler();
				response = sh.tokenAuthenticate(mHeaderData, url);
			}
		}catch(IOException e){
			e.printStackTrace();
		}catch(JSONException e){
			
			e.printStackTrace();
		}
		
		return response;
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
		
		
	}

}
