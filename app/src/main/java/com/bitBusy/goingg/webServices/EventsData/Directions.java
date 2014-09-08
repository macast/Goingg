/**
 * 
 */
package com.bitBusy.goingg.webServices.EventsData;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.w3c.dom.Document;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.bitBusy.goingg.cache.Cache;
import com.google.android.gms.maps.model.LatLng;

/**
 * @author SumaHarsha
 *
 */
public class Directions extends AsyncTask<Object, Void, Document>{
	
	
	
		/** mode*/
		public final static String MODE_DRIVING = "driving";
			
		/** mode*/
	    public final static String MODE_WALKING = "walking";
	    
		/** mode*/
	    public final static String MODE_BIKING = "bicycling";
	    
		/** mode*/
	    public final static String MODE_TRANSIT = "transit";
	    
		/** mode string*/
	    private static final String MODE = "&mode=";
	    
	    /** departure time string*/
	    private static final String DEPARTURETIME = "&departure_time=";
	    
	    /** arrival time string*/
	    private static final String ARRIVALTIME = "&arrival_time=";

	    /** avoid string*/
	    private static final String AVOID = "&avoid=";
	    
	    /** highway string*/
	    private static final String HIGHWAY= "highways";
	    
	    /** tolls string*/
	    private static final String TOLLS = "tolls";
	    
	    /** message to be displayed while preparing to display*/
	    private static final String MAPLOADINGDIRECTIONS = "Preparing route..";
	    
	    
	    /** progress dialog*/
		private ProgressDialog myProgressDialog;
		
			
		/** list of requestors*/
		private ArrayList<DirectionsDocumentRequestor> myDocRequestors;
		
		private Context myContext;

		
		/** parameterized constructor*/
		public Directions(Context the_context)
		{
			if (the_context != null)
			{
				myProgressDialog = new ProgressDialog(the_context);
				myContext = the_context;
			}
		}

		@Override
	    protected void onPreExecute()
		{
			try
			{
				myProgressDialog.setMessage(MAPLOADINGDIRECTIONS);
			    myProgressDialog.setCanceledOnTouchOutside(false);
				myProgressDialog.show();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		
		@Override
	    protected void onPostExecute(final Document the_doc)

	    {
			notifyRequestors(the_doc);

			if (myProgressDialog.isShowing())
			{
				myProgressDialog.dismiss();
			}
	    }
		
		/** register requestor
		 * @param requestor
		 */
		public void registerRequestor(DirectionsDocumentRequestor the_requestor)
		{
			if (myDocRequestors == null)
			{
				myDocRequestors = new ArrayList<DirectionsDocumentRequestor>();
			}
			myDocRequestors.add(the_requestor);
			
		}
	    /** method to notify requestors
		 * @param the_doc
		 */
		private void notifyRequestors(Document the_doc) {
			if (myDocRequestors != null)
			{
				for (DirectionsDocumentRequestor requestor:myDocRequestors)
				{
					requestor.acceptDocument(the_doc);
				}
			}
		}

			
		/* (non-Javadoc)
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected Document doInBackground(Object... params) {
			//myProgressDialog.setMessage(MAPLOADINGDIRECTIONS);
			//myProgressDialog.show();

			if ( params.length == 7)
			{
				LatLng start = (LatLng)params[0];
				LatLng end = (LatLng) params[1];
				String mode = (String)params[2];
				Boolean highwayAvoided = (Boolean)params[3];
				Boolean tollAvoided = (Boolean)params[4];
				Boolean departSet = (Boolean)params[5];
				Calendar dateTime = (Calendar)params[6];
				
				String url = "http://maps.googleapis.com/maps/api/directions/xml?" 
			                + "origin=" + start.latitude + "," + start.longitude  
			                + "&destination=" + end.latitude + "," + end.longitude 
			                + "&sensor=false&units=metric";
				
				if (mode != null && MODE_TRANSIT.equals(mode))
				{
					String time = dateTime!=null?String.valueOf(dateTime.getTimeInMillis()/1000):null;
					if (departSet)
					{
						url = url.concat(DEPARTURETIME).concat(time);
					}
					else
					{
						url = url.concat(ARRIVALTIME).concat(time);
					}
				}
			  
				if (mode != null && MODE_DRIVING.equals(mode))
				{
					if(highwayAvoided)
					{
						url = url.concat(AVOID).concat(HIGHWAY);
					}
					if(tollAvoided)
					{
						url = url.concat(AVOID).concat(TOLLS);
					}
				}
				url = url.concat(MODE).concat(mode);
				Document doc = null;
				if (Cache.getInstance().isCached(myContext, url))
				{
					Log.i(this.getClass().getSimpleName(), "cached maps");
					doc = Cache.getInstance().getDirsCachedData(myContext, url);
				}
				else
				{
			        try {
			            HttpClient httpClient = new DefaultHttpClient();
			            HttpContext localContext = new BasicHttpContext();
			            HttpPost httpPost = new HttpPost(url);
			            HttpResponse response = httpClient.execute(httpPost, localContext);
			            InputStream in = response.getEntity().getContent();
			            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			            doc = builder.parse(in);
			            Cache.getInstance().cache(myContext, url, doc);
			        } catch (Exception e) {
			            e.printStackTrace();
			        }
				}
	            return doc;
			}
	        return null;
	
		}
}
