/**
 * 
 */
package com.bitBusy.goingg.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.widget.Toast;

import com.bitBusy.goingg.activity.ActivityMapViewHome;
import com.bitBusy.goingg.cache.DataStructureCache;
import com.bitBusy.goingg.utility.CurrentLoginState;

/**
 * @author SumaHarsha
 *
 */
public class ConfirmLogoutDialog {
	
	private static final String  SIGNEDOUT = "Logged out";
	private static final String CONFIRMLOGOUTTITLE = "Confirm logout";
	private static final String CONFIRMLOGOUTMSG = "Are you sure you'd like to log out?";
	private Activity myCurrentActivity;
	
	public ConfirmLogoutDialog(Activity the_activity)
	{
		myCurrentActivity = the_activity;
	}
	
	public void openDialog()
	{
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(myCurrentActivity);
		alertDialogBuilder.setTitle(CONFIRMLOGOUTTITLE);
			alertDialogBuilder
				.setMessage(CONFIRMLOGOUTMSG)
				.setCancelable(false)
				.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
						logout();
					}
				  })
				.setNegativeButton("No",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
						dialog.cancel();
					}
				});
 				AlertDialog alertDialog = alertDialogBuilder.create();
 				alertDialog.show();
	}

	/**
	 * 
	 */
	protected void logout() {
		 Toast.makeText(myCurrentActivity.getApplicationContext(), SIGNEDOUT, Toast.LENGTH_LONG).show();
		 CurrentLoginState.setUserLoggedInGoing(myCurrentActivity, false, "");
		 DataStructureCache.getInstance().clearFriendsList();
		 DataStructureCache.getInstance().clearMessagesList();
  		 Intent intent = new Intent(myCurrentActivity, ActivityMapViewHome.class);
		 intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		 myCurrentActivity.startActivity(intent);		
	}

}
