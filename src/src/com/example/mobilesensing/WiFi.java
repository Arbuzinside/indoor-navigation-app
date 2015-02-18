package com.example.mobilesensing;

import java.io.BufferedReader;
import java.io.File;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

import java.util.List;
import java.util.Set;



import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Environment;



public class WiFi {
	
private List<ScanResult> wifi;
	
	/**
	 * 
	 * @param mgr
	 * @return
	 */
	public List<ScanResult> getWifi(WifiManager mgr){
		    mgr.startScan();
			wifi = mgr.getScanResults();    
		    
		    
		    return wifi;
		
	}
	
	
	public boolean saveWifiData(List<ScanResult> lst, Context context) throws IOException{
		
		
			String root = Environment.getExternalStorageDirectory().toString();  
	
			File f = new File(root, "MobileSensing");
			 
			if (f.exists())
			{
				writeToFile(lst, f);
			}
			else {
				f.mkdirs();
				writeToFile(lst, f);
			}
			 
 
      
		
		return true;
		
	}
	
	
	private void writeToFile(List<ScanResult> lst, File f){
		
		JSONObject obj, measurement, ap;
		FileWriter fw;
		File file = new File(f, "data1.txt");
		
	
		try {
		
		fw = new FileWriter(file, true);
		
		int i = 1;
		
		measurement = new JSONObject();
		try {
			ap = new JSONObject();
		for (ScanResult r: lst)
		{
			
				obj = new JSONObject();
				obj.put("capabilities", r.capabilities.toString());
				obj.put("level", r.level);
				obj.put("frequency", r.frequency);
				obj.put("BSSID", r.BSSID);
				obj.put("SSID", r.SSID);
				ap.put("ap"+i, obj);
				i++;
		  	}
		measurement.put("real_time measurement" + i, ap);
		fw.append(measurement.toString());
		
		//measurement.put("measurement"+i, ap.toString());
		
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
		fw.close();
	  } catch (IOException e) {
          e.printStackTrace();

      }
	}
	
	public JSONObject parseScanList(List<ScanResult> L){
		JSONObject obj, ap;
		int z = 1;
		try{
			ap = new JSONObject();
			for (ScanResult r: wifi)
			{
			
				obj = new JSONObject();
				obj.put("capabilities", r.capabilities.toString());
				obj.put("level", r.level);
				obj.put("frequency", r.frequency);
				obj.put("BSSID", r.BSSID);
				obj.put("SSID", r.SSID);
				ap.put("ap"+z, obj);
				z++;
		  	}
			return ap;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}  
	}
	
	
	public JSONObject readWifi(Context context){
		
		JSONObject obj;
		
		
		try {
			obj = new JSONObject(readJSON((context), "data1.txt"));
			//Log.e("Sensing", obj.toString());
			return obj;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
		
	}
	
	
	public List<AccessPoint> readParse(String readWifi) throws Exception{
		
		List<AccessPoint> aps = new ArrayList<AccessPoint>();

		
		JSONObject root = new JSONObject(readWifi);
		JSONArray rootlist = root.names();
		for (int i = 0; i < rootlist.length(); i++)
		{
			
			JSONObject rt = root.getJSONObject(rootlist.optString(i));
			JSONArray aplist= rt.names();
			for(int j=0; j < aplist.length();j++){
				   JSONObject ap = rt.getJSONObject(aplist.optString(j)); 
				   String BSSID = ap.getString("BSSID");
			        float level = ap.getLong("level");
			        aps.add(new AccessPoint(BSSID, level));
			}
			
			
			
		}	
	    return aps;
	}
	
	public List<AccessPoint> parseRealTime(String realTimeWifi) throws Exception{
		List<AccessPoint> aps = new ArrayList<AccessPoint>();
		
		
		ArrayList<JSONObject> jsonlist = new ArrayList<JSONObject>();
		
		
		
		String delims = "[}]{3}";
		String[] tokens = realTimeWifi.split(delims);
		
		JSONObject r = null;
		for (int i = 0; i < tokens.length - 1;  i++)
		{
			String s1 = tokens[i] + "}}}";
				 r = new JSONObject(s1);
			jsonlist.add(r);
			
			
		}
	
		for(JSONObject root : jsonlist){							//rootlist.length() is test time
			
			JSONArray rootlist = root.names();
			
			for(int i=0; i < rootlist.length(); i++){							//rootlist.length() is test time
				JSONObject rt = root.getJSONObject(rootlist.optString(i));
				JSONArray aplist= rt.names();
				for(int j=0; j < aplist.length();j++){
					   JSONObject ap = rt.getJSONObject(aplist.optString(j)); 	
					   String BSSID = ap.getString("BSSID");
				        float level = ap.getLong("level");
				        aps.add(new AccessPoint(BSSID, level));			        
				}
			}
		}
		
		return aps;
		
	}
	
	
	
	public Double caclPosition(List<AccessPoint> sPoints, List<AccessPoint> rPoints)
	{
		
		ArrayList<Double> points = new ArrayList<Double>();
		Double tempPoint;
		double d = 0;
		
			for (AccessPoint sPoint: sPoints)
				for(AccessPoint rPoint: rPoints)
				{
				   if(sPoint.getBssid().equals(rPoint.getBssid()))
				   {
					   tempPoint = Math.pow(sPoint.getLevel() - rPoint.getLevel(),2);
					   points.add(tempPoint);
				   }		
				}
		
			for(Double td: points)
			{
				d += td;
			}
			
			d = Math.pow(d, 0.5);
		
		return d;
	}
	
	
	public String Position(HashMap<Double, String> positions)
	{
		String position = null;
		Collection<Double> pos = positions.keySet();
		double key;
		
		key = Collections.min(pos);
		
		position = positions.get(key);
		
		
		return position;
	}
	
	
	

	
	
	
public List<AccessPoint> calcRev(List<AccessPoint> givenData, int _occur, int def) throws Exception{
		List<AccessPoint> aps = givenData;
		List<AccessPoint> av = new ArrayList<AccessPoint>();
		
		String[] bssid = new String[aps.size()];		
		List<String> fb = new ArrayList<String>();
		List<String> fc = new ArrayList<String>();
		for(int k = 0; k < aps.size(); k++){
			bssid[k] = aps.get(k).getBssid();			
		}		
		List<String> asList = Arrays.asList(bssid);
		Set<String> mySet = new HashSet<String>(asList);
		for(String s: mySet){
			int occurrence = Collections.frequency(asList,s);
			if(occurrence >= _occur){
				fb.add(s);
			}
			else{
				fc.add(s);
			}
		}
		
		for (String b : fb) {
			float sum = 0; 
			float ave = 0;
			int num = 0;
			for (AccessPoint a : aps) {		    
		        if (a.getBssid() == b) {
		        	sum += a.getLevel();
		            num++;
		        }
		    }
			ave = sum/num;
			if (!b.isEmpty())
			av.add(new AccessPoint(b, ave));
		}
		
		for (String c : fc){
			float ave = 0;
			float sum = 0;
		
			int num = 0;
			for (AccessPoint a: aps) {
				if (a.getBssid() == c){
					sum += a.getLevel();
					num++;
				}
			}
			sum += (_occur - num)*def;
			ave = sum/_occur;
			av.add(new AccessPoint(c, ave));
		}
		return av;		
	}
	
	
	
	public String readRealTime(Context context)
	{
		   String root = Environment.getExternalStorageDirectory().toString();  
		   String everything = null;
		   
			String file = root + "/MobileSensing/" + "data1.txt";
			  try(BufferedReader br = new BufferedReader(new FileReader(file))) {
			        StringBuilder sb = new StringBuilder();
			        String line = br.readLine();

			        while (line != null) {
			            sb.append(line);
			            sb.append(System.lineSeparator());
			            line = br.readLine();
			        }
			        everything = sb.toString();
			    } catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			  
			 
		  
		 
		   return everything;
	}
	
	
	
	public String readJSON(Context context, String filename)
	{
		 
		 	
	        String json = null;
	        try {
               
	            InputStream is = context.getAssets().open(filename);
	        	
	            int size = is.available();
	            byte[] buffer = new byte[size];
	            is.read(buffer);

	            is.close();

	            json = new String(buffer, "UTF-8");
	            return json;

	        } catch (IOException ex) {
	            ex.printStackTrace();
	            return null;
	        }
	}
	
	
	
	
	private boolean isExternalStorageWritable() {
	    String state = Environment.getExternalStorageState();
	    if (Environment.MEDIA_MOUNTED.equals(state)) {
	        return true;
	    }
	    return false;
	}
	
	private boolean isExternalStorageReadable() {
	    String state = Environment.getExternalStorageState();
	    if (Environment.MEDIA_MOUNTED.equals(state) ||
	        Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
	        return true;
	    }
	    return false;
	}
	

}
