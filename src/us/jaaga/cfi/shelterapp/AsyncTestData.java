package us.jaaga.cfi.shelterapp;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;


public class AsyncTestData extends AsyncTask<Void, Void, String> {
	
	TestPage mTestPage;
	String api_key;
	ProgressDialog mProgressDialog;
	private static final String url = "http://192.168.0.123:3000/api/users/";
	String response;
	
	public AsyncTestData(TestPage mTestPage, String api_key) {
		
		this.mTestPage = mTestPage;
		this.api_key = api_key;
		Log.i("tag",api_key+"and Testpage is obtained in AsyncTestData");
		
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		mProgressDialog = new ProgressDialog(mTestPage);
		mProgressDialog.setMessage("Please wait...");
		mProgressDialog.setCancelable(false);
		mProgressDialog.show();
		
	}

	@Override
	protected String doInBackground(Void... params) {
		
		ServiceHandler sh = new ServiceHandler();
		response = sh.testData(api_key, url);
		Log.i("tag",response + "is obtained from 2nd call");
		
		return response;
	}
	
	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		if (mProgressDialog.isShowing())
			mProgressDialog.dismiss();
		
		//mTestPage.setDisplay(result);
	
	}

}
