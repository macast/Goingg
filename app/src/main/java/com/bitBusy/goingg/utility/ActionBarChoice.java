/**
 * 
 */
package com.bitBusy.goingg.utility;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;

import com.bitBusy.goingg.activity.ActivityAppSettings;
import com.bitBusy.goingg.activity.ActivityCreateEvent;
import com.bitBusy.goingg.activity.ActivityMapViewHome;
import com.bitBusy.goingg.activity.ActivityViewMyAccount;
import com.bitBusy.goingg.activity.ActivityViewReminders;
import com.bitBusy.goingg.cache.DataStructureCache;
import com.bitBusy.goingg.database.setup.DBInteractor;
import com.bitBusy.goingg.dialog.ConfirmLogoutDialog;
import com.bitBusy.goingg.dialog.LoginDialog;
import com.bitBusy.goingg.R;

/**
 * @author SumaHarsha
 *
 */
public class ActionBarChoice{

//	private static final String CHOOSEEVENTTYPE = "Select type of event to create";
//	private static final String[] EVENTTYPESARR = new String[]{"Public", "Private (Only invitees can see)"};
	
	/** method to navigate based on choice
	 * @param context
	 * @param choice
	 */
	public static void choiceMade(final Activity the_currActivity, MenuItem the_item)
	{
		if (the_currActivity != null && the_item != null)
		{
			int itemId = the_item.getItemId();
			
			if (itemId == android.R.id.home) {
				if (!the_currActivity.getClass().equals(ActivityMapViewHome.class))
		    	  {
			    	  Intent intent = new Intent(the_currActivity, ActivityMapViewHome.class);
			 		 intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
			    	  the_currActivity.startActivity(intent);
		    	  }
			} else if (itemId == R.id.action_view_reminders) {
				if (!the_currActivity.getClass().equals(ActivityViewReminders.class))
		    	  {
			      	Intent intent = new Intent(the_currActivity, ActivityViewReminders.class);
			      	the_currActivity.startActivity(intent);
		    	  }
			} else if (itemId == R.id.action_personalize) {
				  if (!the_currActivity.getClass().equals(ActivityCreateEvent.class))
				  {
				  	Intent intent = new Intent(the_currActivity, ActivityCreateEvent.class);
				  	the_currActivity.startActivity(intent);
				  }
			} else if (itemId == R.id.action_signin) {
				new LoginDialog(the_currActivity).openDialog();
			} else if (itemId == R.id.action_signout) {
				if (!the_currActivity.getClass().equals(ActivityViewMyAccount.class))
				  {
					 // Utility.initiateFriendsList(the_currActivity);
						initializeMsgList(the_currActivity);
						initializeFrndsList(the_currActivity);
					  Intent intent = new Intent(the_currActivity, ActivityViewMyAccount.class);
					 intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
					  the_currActivity.startActivity(intent);
				  }
				 else
				 {
					 new ConfirmLogoutDialog(the_currActivity).openDialog();
				 }
			} else if (itemId == R.id.action_settings){
				 if (!the_currActivity.getClass().equals(ActivityAppSettings.class))
				  {
				  	Intent intent = new Intent(the_currActivity, ActivityAppSettings.class);
				  	the_currActivity.startActivity(intent);
				  }
				}
		}
	}
	
	 private static void initializeMsgList(Context the_context){
	    	DataStructureCache.getInstance().
	    	addMsgs(new DBInteractor(the_context).getAllInboxMessages(CurrentLoginState.getCurrentUser(the_context)));
	    }
	 
	    private static void initializeFrndsList(Context the_context){
	    	DataStructureCache.getInstance().
	    	addFriends(new DBInteractor(the_context).getAllFriends(CurrentLoginState.getCurrentUser(the_context)));
	    }
	
	
	public static void setupActionBar(Menu the_menu, Context the_context)
	{
		if (the_menu != null)
		{
			MenuItem signout = the_menu.findItem(R.id.action_signout);
			MenuItem signin = the_menu.findItem(R.id.action_signin);
			if (isUserLoggedIn(the_context))
			{
				signout.setVisible(true);
				signin.setVisible(false);
			}
			else
			{
				signout.setVisible(false);
				signin.setVisible(true);
			}
			
		}
	}
	
	private static boolean isUserLoggedIn(Context the_context) {
		String user = CurrentLoginState.getCurrentUser(the_context);
		return (user!=null && user.length() > 0);
	}
}
