/**
 * 
 */
package com.bitBusy.goingg.dialog;

import java.util.ArrayList;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;

import com.bitBusy.goingg.adapters.ListAdapter;
import com.bitBusy.goingg.weather.Forecast;
import com.bitBusy.goingg.R;

/**
 * @author SumaHarsha
 *
 */
public class WeatherForecastDialog {

	/** context*/
	private Context myContext;
	
	/** dialog*/
	private Dialog myDialog;
	
	/** forecast*/
	private ArrayList<Forecast> myForecast;
	
	public WeatherForecastDialog(Context the_context, ArrayList<Forecast> the_forecast)
	{
		myContext = the_context;
		myForecast = the_forecast;
	}
	
	
	/** throws open a dialog*/
	public void openDialog() {
		if (myForecast != null)
		{
			myDialog = new Dialog(myContext);
			myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		    LayoutInflater inflater = (LayoutInflater) myContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		    
		    // Inflate and set the layout for the dialog
		    // Pass null as the parent view because its going in the dialog layout
			myDialog.setContentView(inflater.inflate(R.layout.weather_forecast_dialog, null));
			setCloseButtonListener();
			setList();
			myDialog.show();
		}
	}
	
	/**
	 * listener to close the dialog on click
	 * @param the_detailsDialog
	 */
	private void setCloseButtonListener() {
		Button close = (Button) myDialog.findViewById(R.id.weather_forecast_close);
		close.setOnClickListener(new OnClickListener() {						 
			@Override
			public void onClick(View arg0) {
				myDialog.dismiss();
			}
 
		});
	}
	
	
	private void setList()
	{
		ListView listview = (ListView) myDialog.findViewById(R.id.weather_forecast_listview);
		listview.setAdapter(new ListAdapter(myContext, myForecast, null));
	}

}
