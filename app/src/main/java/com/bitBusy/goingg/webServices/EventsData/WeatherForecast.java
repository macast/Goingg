/**
 * 
 */
package com.bitBusy.goingg.webServices.EventsData;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.bitBusy.goingg.cache.Cache;
import com.bitBusy.goingg.events.PublicEvent;
import com.bitBusy.goingg.utility.Utility;
import com.bitBusy.goingg.weather.Forecast;
import com.google.android.gms.maps.model.LatLng;

/**
 * @author SumaHarsha
 * 
		2-5313316191
		ph: 888 362 2363
 *
 */
public class WeatherForecast extends AsyncTask<PublicEvent, Void, Boolean>{
	
	public static final String ANDQEQUALS = "&q=";
	
	public static final String SERVERLINK = "http://api.worldweatheronline.com/free/v1/weather.ashx?key=xc7gcry3893zee3v9nz6qhcy&cc=no&format=json";

	private static final String RETRIEVINGFORECAST = "Retrieving forecast...";
	  
	private static final String VALUE = "value";
	
		
	private static final String ANDDATEEQUALS = "&date=";
	
	private static final String MAXTEMP = "tempMaxF";
	
	private static final String MINTEMP = "tempMinF";
	
	private static final String WEATHERDESC = "weatherDesc";
	
	private static final String WEATHERDESCURL = "weatherIconUrl";
	
	private Context myContext;

	private LatLng myLatLng;
	
	/** list of dates*/
	private  ArrayList<String> myDates;
	
    /** progress dialog*/
	private ProgressDialog myProgressDialog;

	/** list of requestors*/
	private ArrayList<WeatherForecastRequestor> myRequestors;

	/** forecast*/
	private ArrayList<Forecast> myForecast;
	
	/** event*/
	private PublicEvent myEvent;
	/** parameterized constructor*/
	public WeatherForecast(Context the_context)
	{
		if (the_context != null)
		{
			myContext = the_context;
			myProgressDialog = new ProgressDialog(the_context);
		}
		myForecast = new ArrayList<Forecast>();
	}

	@Override
    protected void onPreExecute()
	{
		myProgressDialog.setMessage(RETRIEVINGFORECAST);
	    myProgressDialog.setCanceledOnTouchOutside(false);
		myProgressDialog.show();
	}
	
	@Override
    protected void onPostExecute(final Boolean isSuccess)
    {
		closeDialog();
		notifyRequestors();
    }
	
	@Override
	protected Boolean doInBackground(PublicEvent... params) {
		myEvent = params[0];
		if (myEvent != null)
		{
			getFieldValues();
			return getForecast();
		}
		return false;
	}
	
	/** gets field values*/
	private void getFieldValues()
	{
		setCoordinates();
		setDatesList();
	}
	
	
	private void setCoordinates()
	{
		myLatLng = myEvent.getCoordinates();
	}
	private void setDatesList()
	{
		myDates = new ArrayList<String>();
		Calendar endDate = myEvent.getEndDateTime();
		Calendar date = myEvent.getStartDateTime();
		while(Utility.compareCalendarDates(date, endDate) <= 0)
		{
			String dateStr = getDate(date);
			if (dateStr != null)
			{
				myDates.add(dateStr);
			}
			incrementDay(date);
		}
			
		if (myDates.size() == 0)
		{
			myDates.add(getDate(Calendar.getInstance()));
		}
		
	}
		
	private void incrementDay(Calendar the_date)
	{
		if (the_date != null)
		{
			the_date.add(Calendar.DAY_OF_MONTH, 1);
		}
	}
	

	/**
	 * connect and read data
	 * @return boolean value indicating if successful
	 */
	private boolean getForecast()
	{
		boolean success = false;
		HttpClient client = new DefaultHttpClient();
		if (myDates != null)
			{
				success = true;
				String link = addQ();
				for (String date: myDates)
				{
					String data = null;
					String connect = addDate(link, date);
					if (Cache.getInstance().isCached(myContext, connect))
					{
						data = Cache.getInstance().getCachedData(myContext, connect);
					}
					else
					{
						HttpGet httpGet = new HttpGet(connect);
						data = readData(client, httpGet);
						Cache.getInstance().cache(myContext, connect, data);
					}
						if (data == null)
					{
						success = false;
					}
					else
					{
						success = success && addToResultArray(date, data);
					}
					if (!success)
					{
						break;
					}
				}
			}
		return success;
	}
	
	private String addQ()
	{
		if (myLatLng != null)
		{
			return SERVERLINK.concat(ANDQEQUALS).concat(String.valueOf(myLatLng.latitude)).concat(",").concat(String.valueOf(myLatLng.longitude));
		}
		return SERVERLINK;
	}
	
	private String addDate(String the_link, String the_date)
	{
		if (the_link != null && the_date != null)
		{
			return the_link.concat(ANDDATEEQUALS).concat(the_date);
		}
		return the_link;
	}
	
	private String readData(HttpClient the_client, HttpGet the_httpGet) {
		  StringBuilder dataBuilder = new StringBuilder();

		try {
			  HttpResponse response = the_client.execute(the_httpGet);
			  StatusLine statusLine = response.getStatusLine();
			  int statusCode = statusLine.getStatusCode();
			  if (statusCode == 200) {
			    HttpEntity entity = response.getEntity();
			    InputStream content = entity.getContent();
			    BufferedReader reader = new BufferedReader(new InputStreamReader(content));
			    String line;
			    while ((line = reader.readLine()) != null) {
			    	dataBuilder.append(line);
			    }
			  } else {
					closeDialog();
			  }
			} catch (ClientProtocolException e) {
				closeDialog();
			} catch (IOException e) {
				closeDialog();
			}
		return dataBuilder.toString();

	}


	/**
	 * @param requestCategory 
	 * @param data
	 */
	private boolean addToResultArray(String the_date, String the_data) {
		boolean success = false;
			if (the_data != null && the_data.length() > 0)
			{
				  JSONArray jsonArray = null;
					try {
						JSONObject jsonObj = new JSONObject(the_data);
						JSONObject data = jsonObj.getJSONObject("data");
						jsonArray = data.getJSONArray("weather");
					} catch (JSONException exc) {
						closeDialog();
						exc.printStackTrace();
				    	success = false;
						}
				 
				   success = true;
				   if (jsonArray != null)
				   {
					   for (int i = 0; i < jsonArray.length(); i++) 
					   {
							try {	
						    JSONObject jsonObject = jsonArray.getJSONObject(i);
						    if (jsonObject != null)
						    {
						    	Forecast res = getForecastObject(the_date, jsonObject);
						    	if (res != null)
						    	{
						    		myForecast.add(res);

						    	}
						    } 
						    else
						    {
						    	success = false;
						    	break;
						    }
					   }
					   catch (JSONException e) {
							continue;
						}
			
					   }
				   }
			}
				return success;
	}
	
	private Forecast getForecastObject(String the_date,JSONObject the_jsonObject)
	{
		String descText = null;
		Bitmap img = null;
		JSONArray descArray;
		try {
			
		if(!the_jsonObject.isNull(WEATHERDESC))
		{
			descArray = the_jsonObject.getJSONArray(WEATHERDESC);
			descText = descArray.getJSONObject(0)!=null?
					(descArray.getJSONObject(0).getString(VALUE)!=null?descArray.getJSONObject(0).getString(VALUE).toString():null):null;
		}
		if(!the_jsonObject.isNull(WEATHERDESCURL))
		{
			descArray  = the_jsonObject.getJSONArray(WEATHERDESCURL);
			String descLink = descArray.getJSONObject(0)!=null?
					(descArray.getJSONObject(0).getString(VALUE)!=null?descArray.getJSONObject(0).getString(VALUE).toString():null):null;
					img = getImg(descLink);
		}
					return (the_jsonObject.isNull(MAXTEMP) || the_jsonObject.isNull(MINTEMP)?null:
						new Forecast(
				getUIFormatDate(the_date),
				the_jsonObject.getString(MAXTEMP).toString(), 
				the_jsonObject.getString(MINTEMP).toString(),
				descText,img));
					} catch (JSONException e) {
				closeDialog();
				e.printStackTrace();
				return null;
					
					}
			}
	
	private Bitmap getImg(String the_src)
	{
		try {
	        URL url = new URL(the_src);
	        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	        connection.setDoInput(true);
	        connection.connect();
	        InputStream input = connection.getInputStream();
	        Bitmap img = BitmapFactory.decodeStream(input);
	        img = Bitmap.createScaledBitmap(img, 128, 128, false); 
	        return img;
	    } catch (IOException e) {
	        e.printStackTrace();
	        return null;
	    }
	}
	
	private String getUIFormatDate(String the_date)
	{
		if (the_date != null)
		{
			String[] dateComp = the_date.split(Utility.DATESPLITTER);
			if (dateComp != null && dateComp.length==3)
			{
				String year = dateComp[0];
				String month = dateComp[1];
				String day = dateComp[2];
				return month.concat(Utility.DATESPLITTER).concat(day).concat(Utility.DATESPLITTER).concat(year);
			}
		}
		return null;
	}
	private String getDate(Calendar the_calendar)
	{
		if (the_calendar != null)
		{
					StringBuilder builder = new StringBuilder(); 
					builder.append(the_calendar.get(Calendar.YEAR))
					.append(Utility.DATESPLITTER).append(String.format(Utility.ZEROPADDING, the_calendar.get(Calendar.MONTH) + 1))
					.append(Utility.DATESPLITTER).append(String.format(Utility.ZEROPADDING, the_calendar.get(Calendar.DAY_OF_MONTH)));
					return builder.toString();
		}
	return null;
	}
	/** register requestor
	 * @param requestor
	 */
	public void registerRequestor(WeatherForecastRequestor the_requestor)
	{
		if (myRequestors == null)
		{
			myRequestors = new ArrayList<WeatherForecastRequestor>();
		}
		myRequestors.add(the_requestor);
		
	}
    /** method to notify requestors
	 * @param the_doc
	 */
	private void notifyRequestors() {
		if (myRequestors != null)
		{
			for (WeatherForecastRequestor requestor:myRequestors)
			{
				requestor.acceptForecast(myForecast);
			}
		}
	}
	
	/** 
	 * method to close the progress dialog
	 */
	
	private void closeDialog()
	{
			if(myProgressDialog != null && myProgressDialog.isShowing())
			{
				myProgressDialog.dismiss();
			}	
	}

}
