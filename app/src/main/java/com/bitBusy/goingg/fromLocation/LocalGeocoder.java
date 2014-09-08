/**
 * 
 */
package com.bitBusy.goingg.fromLocation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.bitBusy.goingg.events.Address;
import com.bitBusy.goingg.utility.Utility;
import com.google.android.gms.maps.model.LatLng;

/**
 * @author SumaHarsha
 *
 */
public class LocalGeocoder extends AsyncTask<Address, Void, LatLng>{

		/** first part of url*/
		private static final String URL = "http://maps.googleapis.com/maps/api/geocode/json?address=";
		
		 /** message to be displayed while preparing to display*/
	    private static final String DETERMINING = "Determining location..";
	    
	  	/**Results*/
	    private static final String TAG_RESULTS = "results";

		/** sensor*/
		private static final String SENSOR = "&sensor=true";
	
		/** lat tag*/
		private static final String TAG_LAT = "lat";
		
		/** lng tag*/
	    private static final String TAG_LNG = "lng";
	    
	    /** northeast*/
	    private static final String TAG_NORTHEAST = "northeast";
	    
	    /** geometry*/
	    private static final String TAG_GEOMETRY = "geometry";
	    
	    /** viewport*/
	    private static final String TAG_VIEWPORT = "viewport";
	 
	    /** progress dialog*/
  		private ProgressDialog myProgressDialog;
	  		
  		/** list of requestors*/
  		private ArrayList<LocalGeocoderRequestor> myRequestors;


	    /** results array*/
	    private JSONArray myJsonArray = null;
	    
	    /** Address*/
	    private Address myAddress;	    
	    
	    /** context*/
	    private Context myContext;
	    
	    public LocalGeocoder(Context the_context)
	    {
	    	myContext = the_context;
	    	myProgressDialog = new ProgressDialog(myContext);
	    }
	    
	    @Override
	    protected void onPreExecute()
		{
	    	if (myProgressDialog != null)
	    	{
				myProgressDialog.setMessage(DETERMINING);
				myProgressDialog.show();
			}
		}
	    
	    @Override
	    protected void onPostExecute(final LatLng the_latLng)

	    {
			notifyRequestors(the_latLng);

			if (myProgressDialog.isShowing())
			{
				myProgressDialog.dismiss();
			}
	    }
		
		/** register requestor
		 * @param requestor
		 */
		public void registerRequestor(LocalGeocoderRequestor the_requestor)
		{
			if (myRequestors == null)
			{
				myRequestors  = new ArrayList<LocalGeocoderRequestor>();
			}
			myRequestors.add(the_requestor);
			
		}
	    /** method to notify requestors
		 * @param the_doc
		 */
		private void notifyRequestors(LatLng the_latLng) {
			if (myRequestors != null)
			{
				for (LocalGeocoderRequestor requestor:myRequestors)
				{
					requestor.acceptLatLng(the_latLng);
				}
			}
		}

			
		/* (non-Javadoc)
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected LatLng doInBackground(Address... params) {
		
	      	LatLng result = null;
	      	myAddress = params[0];
	      	if (myAddress != null)
	      	{
		    	String address = myAddress.toString();
		        address = address.replaceAll(" ", "%20");
		        String url = URL + address + SENSOR;
	
		        // getting JSON string from URL
		        JSONObject json = getJSONfromURL(url);
	
		        if (json != null)
		        {
			        try {
			            // Getting Array of results
			            myJsonArray = json.getJSONArray(TAG_RESULTS);
		
			            for (int i = 0; i < myJsonArray.length(); i++) {
			                JSONObject object = myJsonArray.getJSONObject(i);
			                // geometry and location is again JSON Object
			                JSONObject geometry = object.getJSONObject(TAG_GEOMETRY);
			                JSONObject viewport = geometry.getJSONObject(TAG_VIEWPORT);
			                JSONObject northest = viewport.getJSONObject(TAG_NORTHEAST);
			                String lat = northest.getString(TAG_LAT);
			                String lng = northest.getString(TAG_LNG);
			                result = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
			            	}	
		
			        } catch (JSONException e) {
			            e.printStackTrace();
			        }
		      	}
	      	}
	      	return result;
	    }

		/**
		 * @param url2
		 * @return
		 */
		private JSONObject getJSONfromURL(String the_url) {
			InputStream is = null;
		    JSONObject jObj = null;
		    String json = "";
		    
			  // Making HTTP request
	        try {
	            // defaultHttpClient
	            DefaultHttpClient httpClient = new DefaultHttpClient();
	            HttpPost httpPost = new HttpPost(the_url);
	            HttpResponse httpResponse = httpClient.execute(httpPost);
	            HttpEntity httpEntity = httpResponse.getEntity();
	            is = httpEntity.getContent();            

	        } catch (UnsupportedEncodingException e) {
	            e.printStackTrace();
	        } catch (ClientProtocolException e) {
	            e.printStackTrace();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	        catch(IllegalArgumentException e)
	        {
	        	Utility.throwErrorMessage(myContext, "Search failed", "Illegal argument in address!");
	        }
	        try {
	            BufferedReader reader = new BufferedReader(new InputStreamReader(
	                    is, "iso-8859-1"), 8);
	            StringBuilder sb = new StringBuilder();
	            String line = null;
	            while ((line = reader.readLine()) != null) {
	                sb.append(line + "\n");
	            }
	            is.close();
	            json = sb.toString();
	        } catch (Exception e) {
	        	e.printStackTrace();
	        }

	        // try parse the string to a JSON object
	        try {
	            jObj = new JSONObject(json);
	        } catch (JSONException e) {
	        	e.printStackTrace();
	        }

	        // return JSON String
	        return jObj;

	    }
		
}