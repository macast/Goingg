/**
 * 
 */
package com.bitBusy.goingg.dialog;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bitBusy.goingg.activity.ActivityMapDirections;
import com.bitBusy.goingg.events.PublicEvent;
import com.bitBusy.goingg.weather.Forecast;
import com.bitBusy.goingg.webServices.EventsData.WeatherForecast;
import com.bitBusy.goingg.webServices.EventsData.WeatherForecastRequestor;
import com.bitBusy.goingg.R;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

/**
 * @author SumaHarsha
 *
 */
public class EventQuerySearchDialog implements WeatherForecastRequestor {
	
		
	public static final String ALARMDETAILS = "com.bitBusy.wevent.ALARMDETAILS";

	private static final String NOFORECAST = "Sorry, we don't seem to know the weather will be like for this/these date(s)!";
	
	public static final String APPENDMSG = "\n\nFound it on 'Going'! " +
			"\n https://play.google.com/store/apps/details?id=com.bitBusy.goingg";
	
	public static final String MESSAGESTRING = "Check this out! ";

	public static final String LOADING = "Loading...";
	
	public static final String PLACEDETAILS = "Place details";
	/** context*/
	private Context myContext;
	
	/** dialog*/
	private Dialog myDialog;
	
	/** event*/
	private PublicEvent myEvent;

	/** from latlng for map*/
	//private LatLng myFromLatLng;
	/**
	 * parameterized constructor
	 * @param the_context
	 * @param the_event
	 */
	public EventQuerySearchDialog(Context the_context, PublicEvent the_event)// LatLng the_fromLatLng)
	{
		myContext = the_context;
		myEvent = the_event;
//		myFromLatLng = new LatLng(40,122);
	}
	
	
	/** throws open a dialog*/
	public void openDetailsDialog() {
		if (myEvent != null)
		{
			myDialog = new Dialog(myContext);
			myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		    LayoutInflater inflater = (LayoutInflater) myContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		    
		    // Inflate and set the layout for the dialog
		    // Pass null as the parent view because its going in the dialog layout
			myDialog.setContentView(inflater.inflate(R.layout.event_query_search_dialog, null));
			setEventDetails(myEvent);
			setTitleButtonListeners();
			setCloseButtonListener();
			setMapButtonListener();
			setWeatherButtonListener();
			myDialog.show();
		}
	}
	
	private void setTitleButtonListeners()
	{
		setReminderButtonListener();
		setShareButtonListener();
	}
	
	private void setReminderButtonListener()
	{
		ImageButton reminder = (ImageButton) myDialog.findViewById(R.id.eventquery_reminderbutton);
		reminder.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new SetReminderDialog(myContext, myEvent).openDialog();
			}
			
		});
	}
	private void setShareButtonListener()
	{
		ImageButton share = (ImageButton) myDialog.findViewById(R.id.eventquery_sharebutton);
		share.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
					  Intent shareIntent = new Intent();
				      shareIntent.setAction(Intent.ACTION_SEND);
				      shareIntent.setType("text/plain");
						shareIntent.putExtra(Intent.EXTRA_TEXT,
								  MESSAGESTRING.concat(myEvent.getName()).concat(" at ").concat(myEvent.getAddress().toString()
								  .concat("\n\n more: " + (myEvent.getInfoLink()!=null?myEvent.getInfoLink().getURL():"-")).concat(APPENDMSG)));
				     ((Activity)myContext).startActivity(Intent.createChooser(shareIntent, "Share"));
				myDialog.dismiss();
			}
			
		});

	}

	/**
	 * listener to close the dialog on click
	 * @param the_detailsDialog
	 */
	private void setCloseButtonListener() {
		Button close = (Button) myDialog.findViewById(R.id.eventQuery_close);
		close.setOnClickListener(new OnClickListener() {						 
			@Override
			public void onClick(View arg0) {
				myDialog.dismiss();
			}
 
		});
	}
	
		/**listener to get directions*/
	private void setMapButtonListener() {
		ImageButton info = (ImageButton) myDialog.findViewById(R.id.eventQuery_map);
		info.setOnClickListener(new OnClickListener() {						 
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(myContext,ActivityMapDirections.class);
				Bundle args = new Bundle();
				args.putParcelable(ActivityMapDirections.TOLATLNG, myEvent!=null?myEvent.getCoordinates():null);
				intent.putExtra(ActivityMapDirections.LATLNGBUNDLE, args);
				intent.putExtra(ActivityMapDirections.EVENTNAME, myEvent!=null?myEvent.getName():"");
				intent.putExtra(ActivityMapDirections.EVENTCOLOR, myEvent!=null? myEvent.getCategory().getColor():BitmapDescriptorFactory.HUE_RED);
				intent.putExtra(ActivityMapDirections.TOADDRESS, myEvent!=null?myEvent.getAddress():null);
				myContext.startActivity(intent);
			}
 
		});
	}
	
	/**listener to get directions*/
	private void setWeatherButtonListener() {
		ImageButton weather = (ImageButton) myDialog.findViewById(R.id.eventQuery_weather);
		weather.setOnClickListener(new OnClickListener() {						 
			@Override
			public void onClick(View arg0) {
				 WeatherForecast weather = new WeatherForecast(myContext);
				 weather.registerRequestor(EventQuerySearchDialog.this);
				 weather.execute(myEvent);

				}
 
		});
	}
	
	/** 
	 * sets details on dialog
	 * @param the_event
	 * @param the_detailsDialog
	 */
	private void setEventDetails(PublicEvent the_event) {
		if (myDialog != null && the_event != null)
		{
			WebView webView = (WebView) myDialog.findViewById(R.id.event_query_webview);
			webView.getSettings().setJavaScriptEnabled(true);
	        webView.setInitialScale(1);
		    webView.getSettings().setBuiltInZoomControls(true);
		    webView.getSettings().setSupportZoom(true);
		    webView.getSettings().setLoadWithOverviewMode(true);
		    webView.getSettings().setUseWideViewPort(true);
		    final Activity activity = (Activity)myContext;;

		    webView.setWebChromeClient(new WebChromeClient(){

		         public void onProgressChanged(WebView view, int progress) {
		        	// myDialog.setTitle("Loading...");
		        	 TextView title = (TextView) myDialog.findViewById(R.id.eventQuery_titleName);
		        	 title.setText(LOADING);
		        	 activity.setProgress(progress * 100);
		                    if(progress == 100)
		                    	title.setText(PLACEDETAILS);
		                 }
		});

		    String url = the_event.getInfoLink()!=null?the_event.getInfoLink().getURL():null;
		    if (url != null)
		    {
		    	try {
					url = url.replace("&", URLEncoder.encode("&", "UTF-8"));
			    	webView.loadUrl(url);
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
		    }
		    	
		    }
	}
	
	/* (non-Javadoc)
	 * @see com.bitBusy.wevent.webServices.WeatherForecastRequestor#acceptForecast(java.util.ArrayList)
	 */
	@Override
	public void acceptForecast(ArrayList<Forecast> the_forecast) {
		if (the_forecast != null && the_forecast.size() > 0)
		{
			new WeatherForecastDialog(myContext, the_forecast).openDialog();
		}
		else
		{
			Toast.makeText(myContext.getApplicationContext(), NOFORECAST, Toast.LENGTH_LONG).show();
		}
	}  
	
	
}
