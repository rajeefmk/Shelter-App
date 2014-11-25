package us.jaaga.cfi.archive;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.util.Log;

public class ServiceHandler {
	String response = "Didn't get any";
	JSONObject mJson;
	
	public String tokenAuthenticate(JSONObject headerdata, String url) {
		
		
		
		try{
			DefaultHttpClient mDHC = new DefaultHttpClient();
			HttpEntity mHE = null;
			HttpResponse mHR = null;
			StringEntity se = new StringEntity(headerdata.toString());
			se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
			
			HttpPost mHP = new HttpPost(url);
			mHP.setHeader("Content-Type","application/json");
			mHP.setEntity(se);
			Log.i("entity", se.toString());
			mHR = mDHC.execute(mHP);
			mHE = mHR.getEntity();
			response = EntityUtils.toString(mHE);
			
			
		}catch (UnsupportedEncodingException e){
			e.printStackTrace();
			
		}catch (ClientProtocolException e){
			e.printStackTrace();
			
		}catch (IOException e){
			e.printStackTrace();
			
		}
		
		return response;
	}
	
	
	public String testData(String token, String url){
		
		try{
			DefaultHttpClient mDHC = new DefaultHttpClient();
			HttpEntity mHE = null;
			HttpResponse mHR = null;
			
			HttpGet mHG = new HttpGet(url);
			mHG.setHeader("Content-Type","application/json");
			mHG.setHeader("api_key", token);
			
			mHR = mDHC.execute(mHG);
			mHE = mHR.getEntity();
			response = EntityUtils.toString(mHE);
			
			
		}catch (UnsupportedEncodingException e){
			e.printStackTrace();
			
		}catch (ClientProtocolException e){
			e.printStackTrace();
			
		}catch (IOException e){
			e.printStackTrace();
			
		}
		
		return response;
	}
}

	

