package com.example.mobilesensing;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONObject;

import android.support.v7.app.ActionBarActivity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {

	private SensorManager sMgr;
	private WifiManager wMgr;
	private SensorList sList = new SensorList();
	private WiFi wf = new WiFi();
	private ListView mListView;
	private WifiListAdapter wAdapter;
	private SensorListAdapter sAdapter;
	private EditText occ;
	private EditText defValue;
	private List<Sensor> sensors;
	private List<ScanResult> wifi;
	private List<String> givenPosition = Arrays.asList("A112_1.json","A112_2.json", "A112_3.json", "A118_1.json", "A118_2.json",
			"A118_3.json", "A124_1.json", "A124_2.json", "A124_3.json", "A130_1.json", "A130_2.json", "A130_3.json", 
			"A136_1.json", "A136_2.json", "A136_3.json", "A141_1.json", "A141_2.json", "A141_3.json");  
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		occ = (EditText) findViewById(R.id.occurs);
		defValue = (EditText) findViewById(R.id.defaultNumber);
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		
		if (id == R.id.action_settings) {
			return true;
		}
		else if (id == R.id.sensors_list){
			
			try{
			sMgr = (SensorManager) getSystemService(SENSOR_SERVICE);
			sensors = sList.getSensors(sMgr);
			sAdapter = new SensorListAdapter(this, sensors);
			mListView = (ListView) findViewById(R.id.myList);
			mListView.setAdapter(sAdapter);
			sAdapter.notifyDataSetChanged();
			
			}
			catch (Exception ex)
			{
				Log.e("mobilesensing", ex.getMessage());
			}
			return true;
		}
		else if (id == R.id.action_wifi){
			try{
				wMgr = (WifiManager) getSystemService(Context.WIFI_SERVICE);
				wifi = wf.getWifi(wMgr);
				wAdapter = new WifiListAdapter(this, wifi);
				mListView = (ListView) findViewById(R.id.myList);
				mListView.setAdapter(wAdapter);
				wAdapter.notifyDataSetChanged();
				}
			catch (Exception ex)
			{
				Log.e("mobilesensing", ex.getMessage());
			}
		
			
			return true;
		}
		else if (id == R.id.save_wifi && !(wifi == null)){
			try{
				
				if (wifi.isEmpty())
				{
					Toast.makeText(getBaseContext(), "Scan first", Toast.LENGTH_SHORT).show();
				}
				else {
				wf.saveWifiData(wifi, getBaseContext());
				Toast.makeText(getBaseContext(), "Data saved", Toast.LENGTH_SHORT).show();
				}
				
				}
			catch (Exception ex)
			{
				Log.e("mobilesensing", ex.getMessage());
			}
		
			
		}
		
		else if (id == R.id.measurement){
			
			 int def = Integer.parseInt(defValue.getText().toString());
			
			HashMap<Double, String> positions = new HashMap<Double, String>();
		
			try {
				List<AccessPoint> measureData = wf.readParse(wf.readJSON(getApplicationContext(), "A150.json"));
				int occur0  = new JSONObject(wf.readJSON(getApplicationContext(), "A126.json")).names().length();
				List<AccessPoint> measureFile = wf.calcRev(measureData, occur0, def);
				
				for (int i = 0; i < givenPosition.size(); i++) {	
	                 try {
	                
	                	 List<AccessPoint> givenData = wf.readParse(wf.readJSON(getApplication(), givenPosition.get(i)));
	                	 int occur1 = new JSONObject(wf.readJSON(getApplication(), givenPosition.get(i))).names().length();
	                	
	                	 List<AccessPoint> givenFile = wf.calcRev(givenData, occur1, def);	                	 
	                	 
	                	 
	                	 
	                	 positions.put(wf.caclPosition(givenFile, measureFile), givenPosition.get(i));
	                	 
	                 }
					 catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				
				
				Toast.makeText(getBaseContext(), wf.Position(positions), Toast.LENGTH_LONG).show();	
				
			}catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	 
        }
		
		else if (id == R.id.localization && !(wifi == null)){			
			HashMap<Double, String> positions = new HashMap<Double, String>();
			
			String everything = (wf.readRealTime(getBaseContext()));
			
			 int def = Integer.parseInt(defValue.getText().toString());
			try {
				List<AccessPoint> measureData = wf.parseRealTime(everything);
				 
				String delims = "[}]{3}";
				int occur0 = everything.split(delims).length - 1;
				
			  
				List<AccessPoint> measureFile = wf.calcRev(measureData, occur0, def);
				
				for (int i = 0; i < givenPosition.size(); i++) {	
	                 try {
	                	
	                	 
	                		 List<AccessPoint> givenData = wf.readParse(wf.readJSON(getApplication(), givenPosition.get(i)));
	                		 
	                		      
	                	 
	                	
	                	 //int occur1 = new JSONObject(wf.readJSON(getApplication(), givenPosition.get(i))).names().length();
	                	 int occur1 = Integer.parseInt(occ.getText().toString());
	                	
	                	 List<AccessPoint> givenFile = wf.calcRev(givenData, occur1, def);	                	 
	                	 
	                	 
	                	 
	                	 positions.put(wf.caclPosition(givenFile, measureFile), givenPosition.get(i));
	                	 
	                 }
					 catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				
				
				Toast.makeText(getBaseContext(), wf.Position(positions), Toast.LENGTH_LONG).show();	
				
			}catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	 
        }
		else if (id == R.id.delete)
		{
			String root = Environment.getExternalStorageDirectory().toString();
			String file = root + "/MobileSensing/" + "data1.txt";
			if (new File(file).delete())
				Toast.makeText(getBaseContext(), "Deleted", Toast.LENGTH_LONG).show();
			else 
				Toast.makeText(getBaseContext(), "Already deleted", Toast.LENGTH_LONG).show();
		}
		return super.onOptionsItemSelected(item);
	}
	
	
	

	
	class SensorListAdapter extends ArrayAdapter<Sensor>{

        // View lookup cache
        private class ViewHolder {
            TextView name;
            TextView options;
        }
        
      
        

        public SensorListAdapter(Context context, List<Sensor> sensors) {
            super(context, R.layout.sensor, sensors);
        }
        
     

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
            Sensor sensor = (Sensor) getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            ViewHolder viewHolder; // view lookup cache stored in tag
            if (convertView == null) {
                viewHolder = new ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.sensor, parent, false);
                viewHolder.name = (TextView) convertView.findViewById(R.id.list_name);
                viewHolder.options = (TextView) convertView.findViewById(R.id.list_options);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            // Populate the data into the template view using the data object
            viewHolder.name.setText(sensor.getName());
            viewHolder.options.setText(sensor.toString());
            // Return the completed view to render on screen
            return convertView;
        }
    }
	
	
	
	
	class WifiListAdapter extends ArrayAdapter<ScanResult>{

        // View lookup cache
        private class ViewHolder {
            TextView name;
            TextView options;
        }
        
      
        

        public WifiListAdapter(Context context, List<ScanResult> wifi) {
            super(context, R.layout.wifi, wifi);
        }
        
     

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
            ScanResult result = (ScanResult) getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            ViewHolder viewHolder; // view lookup cache stored in tag
            if (convertView == null) {
                viewHolder = new ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.wifi, parent, false);
                viewHolder.name = (TextView) convertView.findViewById(R.id.wifi_name);
                viewHolder.options = (TextView) convertView.findViewById(R.id.wifi_options);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            // Populate the data into the template view using the data object
            viewHolder.name.setText(result.SSID);
            viewHolder.options.setText(result.toString());
            // Return the completed view to render on screen
            return convertView;
        }
    }
	
}












