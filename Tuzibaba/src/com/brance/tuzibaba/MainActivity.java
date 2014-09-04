package com.brance.tuzibaba;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.TextView;

public class MainActivity extends Activity {

	private TextView responseTextView;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); 
        setContentView(R.layout.activity_main);
        
        this.responseTextView = (TextView) this.findViewById(R.id.responseTextView); 
        new GetAllFromDbTask().execute(new ApiConnector());
        
    } 

    public void setTextToTextView(JSONArray jsonArray)
    {
    	String s = "";
    	for (int i=0; i<jsonArray.length();i++)
    	{
    		
    		JSONObject json = null;
    		try
    		{
    			json = jsonArray.getJSONObject(i);
    			s = s + 
    					"First Name : " + json.getString("first")+" " + "\n"+
    					"Last Name : " + json.getString("last") + "\n\n" ;
    		
    		} catch (JSONException e )
    		{
    			e.printStackTrace();
    		}
    	}
    	
    	this.responseTextView.setText(s);
    	
    }
    
    // GetAllCustomerTask
    private class GetAllFromDbTask extends AsyncTask<ApiConnector, Long, JSONArray>
    {

		@Override
		protected JSONArray doInBackground(ApiConnector... params) {
			
			// It is executed on Background thread
			
			return params[0].GetAllFromDB();
			
		}
		
		@Override
		protected void onPreExecute() {
			
		}
		
		@Override
		protected void onPostExecute(JSONArray jsonArray) {
			
			// Executed on MainThread
			
			setTextToTextView(jsonArray);
			
		}
    	
    }
    
}
