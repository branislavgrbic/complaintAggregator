package com.brance.tuzibaba;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends Activity {


	private Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); 
        setContentView(R.layout.activity_main);
        this.button = (Button) this.findViewById(R.id.button1);
    }
    
    public void ListViewActivity(View view)
    {
    	// Send Customer ID
		Intent showDetails = new Intent(getApplicationContext(), ListViewActivity.class);	
		startActivity(showDetails);
    }
    
    public void PostComplaint(View view)
    {
    	// Send Customer ID
		Intent showDetails = new Intent(getApplicationContext(), PostComplaintActivity.class);	
		startActivity(showDetails);
    }
    
}
