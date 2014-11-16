package us.jaaga.cfi.shelterapp;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

public class TestPage extends Activity {
	
	TextView mStatus;
	Button mButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.test_data);
		
		mStatus = (TextView) findViewById(R.id.testData);
		
		SharedPreferences mSharedP = getSharedPreferences("shelterapp",MODE_PRIVATE);
		String api_key = mSharedP.getString("api_key", null);
		Log.i("tag",api_key+"is obtained from SP");
		
		AsyncTestData mAsyncTestData = new AsyncTestData(this,api_key);
		mAsyncTestData.execute();
		Log.i("tag","AsyncTestData is executed");
		
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
