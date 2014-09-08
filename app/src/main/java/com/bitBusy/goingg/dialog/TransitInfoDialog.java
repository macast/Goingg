/**
 * 
 */
package com.bitBusy.goingg.dialog;

import android.app.Dialog;
import android.content.Context;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.bitBusy.goingg.mapDisplay.MarkerToAdd;
import com.bitBusy.goingg.mapDisplay.TransitStopDetails;
import com.bitBusy.goingg.R;

/**
 * @author SumaHarsha
 *
 */
public class TransitInfoDialog {
	/** arrives*/
	private static final String ARRIVES = "Arrives at:";
	
	/** departs */
	private static final String DEPARTS = "Departs at:";
	
		/** context*/
	private Context myContext;
	
	/** transit details*/
	private TransitStopDetails myTransitStopDetails;
	
	/** departs arrives*/
	private boolean isDepart;
	
	/**
	 * parameterized constructor
	 * @param the_context
	 * @param the_event
	 */
	public TransitInfoDialog(Context the_context, MarkerToAdd myDirectionMarker)
	{
		myContext = the_context;

		if (myDirectionMarker != null)
		{
			myTransitStopDetails = myDirectionMarker.getTransitDetails();
			isDepart = myDirectionMarker.isDepart();
		}
	}
	
	
	/** throws open a dialog*/
	public void openDetailsDialog() {
		if (myTransitStopDetails != null)
		{
			Log.i("Coming here", "info");
			Dialog detailsDialog = new Dialog(myContext);
			detailsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		    LayoutInflater inflater = (LayoutInflater) myContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		    
		    // Inflate and set the layout for the dialog
		    // Pass null as the parent view because its going in the dialog layout
			detailsDialog.setContentView(inflater.inflate(R.layout.transit_stop_details_dialog, null));
			setStopDetails(detailsDialog);
			setCloseButtonListener(detailsDialog);
			detailsDialog.show();
		}
	}
	
	private void setStopDetails(Dialog the_dialog)
	{
		if (the_dialog != null && myTransitStopDetails != null)
		{
			TextView title = (TextView) the_dialog.findViewById(R.id.transit_stop_details_titleText);
			title.setText(myTransitStopDetails.getVehicleDets());
			
			TextView name = (TextView) the_dialog.findViewById(R.id.transit_stop_details_stopName_value);
			name.setText(myTransitStopDetails.getStopName());
			
			TextView arrDep = (TextView) the_dialog.findViewById(R.id.transit_stop_details_arrivesDepartsPrompt);
			arrDep.setText(isDepart?DEPARTS:ARRIVES);
			
			TextView time = (TextView) the_dialog.findViewById(R.id.transit_stop_details_arrivesDepartsValue);
			time.setText(myTransitStopDetails.getArrDepTime());
			
			TextView numStopsPrompt = (TextView) the_dialog.findViewById(R.id.transit_stop_details_num_stops_prompt);
			TextView numStopsValue = (TextView) the_dialog.findViewById(R.id.transit_stop_details_num_stops_value);
			if (isDepart)
			{
				numStopsPrompt.setVisibility(View.GONE);
				numStopsValue.setVisibility(View.GONE);
			}
			else
			{
				numStopsPrompt.setVisibility(View.VISIBLE);
				numStopsValue.setVisibility(View.VISIBLE);
				numStopsValue.setText(myTransitStopDetails.getNoOfStops());
			}
		
			TextView lineURL = (TextView) the_dialog.findViewById(R.id.transit_stop_details_line_url_value);
			lineURL.setText(myTransitStopDetails.getLineURL()!=null?myTransitStopDetails.getLineURL().getURL():null);
			Linkify.addLinks(lineURL, Linkify.ALL);

			TextView agencyName = (TextView) the_dialog.findViewById(R.id.transit_stop_agency_name_value);
			agencyName.setText(myTransitStopDetails.getTransitAgencyName());
			
			TextView agencyURL = (TextView) the_dialog.findViewById(R.id.transit_stop_agency_url_value);
			agencyURL.setText(myTransitStopDetails.getTransitURL()!=null?myTransitStopDetails.getTransitURL().getURL():null);
			Linkify.addLinks(agencyURL, Linkify.ALL);

			TextView phoneNum = (TextView) the_dialog.findViewById(R.id.transit_stop_agency_phoneValue);
			phoneNum.setText(myTransitStopDetails.getPhoneNo());
			Linkify.addLinks(phoneNum, Linkify.ALL);


		}
	}

	/**
	 * listener to close the dialog on click
	 * @param the_detailsDialog
	 */
	private void setCloseButtonListener(final Dialog the_detailsDialog) {
		Button close = (Button) the_detailsDialog.findViewById(R.id.transit_stop_details_ok);
		close.setOnClickListener(new OnClickListener() {						 
			@Override
			public void onClick(View arg0) {
				the_detailsDialog.dismiss();
			}
 
		});
	}
	
	
	}
