/**
 * 
 */
package com.bitBusy.goingg.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.bitBusy.goingg.events.User;
import com.bitBusy.goingg.R;

/**
 * @author SumaHarsha
 *
 */
public class UserStatsDialog {

	/** context*/
	private Context myContext;
	
	/** dialog*/
	private Dialog myDialog;
	
	private User myUser;
	
	public UserStatsDialog(Context the_context, User the_user)
	{
		myContext = the_context;
		myUser = the_user;
	}
	
	
	/** throws open a dialog*/
	public void openDialog() {
		if (myUser != null)
		{
			myDialog = new Dialog(myContext);
			myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		    LayoutInflater inflater = (LayoutInflater) myContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		    
		    // Inflate and set the layout for the dialog
		    // Pass null as the parent view because its going in the dialog layout
			myDialog.setContentView(inflater.inflate(R.layout.user_stats_dialog, null));
			setCloseButtonListener();
			setDetails();
			myDialog.show();
		}
	}
	
	/**
	 * 
	 */
	private void setDetails() {
		if (myDialog != null)
		{
			TextView eventsSub = (TextView) myDialog.findViewById(R.id.user_stats_events_value);
			if (eventsSub != null)
			{
				eventsSub.setText(String.valueOf(myUser.getEventsSubmitted()));
			}
			
			TextView spamGen = (TextView) myDialog.findViewById(R.id.user_stats_spam_gen_value);
			if (spamGen != null)
			{
				spamGen.setText(String.valueOf(myUser.getSpamGenerated()));
			}
		
			TextView spamRep = (TextView) myDialog.findViewById(R.id.user_stats_spam_rep_value);
			if (spamRep != null)
			{
				spamRep.setText(String.valueOf(myUser.getSpamReported()));
			}
		}
	}


	/**
	 * listener to close the dialog on click
	 * @param the_detailsDialog
	 */
	private void setCloseButtonListener() {
		if (myDialog != null)
		{
			Button close = (Button) myDialog.findViewById(R.id.user_stats_ok);
			close.setOnClickListener(new OnClickListener() {						 
				@Override
				public void onClick(View arg0) {
					myDialog.dismiss();
				}
	 
			});
		}
	}	

}
