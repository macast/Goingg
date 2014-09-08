/**
 * 
 */
package com.bitBusy.goingg.rating;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.net.Uri;

/**
 * @author SumaHarsha
 *
 */
public class RateWorker {
    private final static String APP_PNAME = "com.bitBusy.goingg";

	 private static final String RATENOW = "Sure, why not?";
	    private static final String REMINDLATER = "Will do later";
	    
	    private Context myContext;

		private Editor myEditor;

	    public RateWorker(Context the_context, Editor the_editor)
	    {
	    	myContext = the_context;
	    	myEditor = the_editor;
	    }
	
	/** Method that wont make another request*/
	private void dontAskAgain()
	{
		if (myEditor != null) {
            myEditor.putBoolean("dontshowagain", true);
            myEditor.commit();
        }
  
	}

	/**
	 * @param mContext
	 * @param editor
	 */
	public void showDialog() {
		if (myContext != null)
		{
			AlertDialog.Builder builder = new AlertDialog.Builder((Activity)myContext);
			builder.setTitle("Gone?");
			builder.setMessage("Please rate this app?\n");
			builder.setPositiveButton(RATENOW, new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			                myContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + APP_PNAME)));
			                dontAskAgain();
			           }
			       });
				builder.setNegativeButton(REMINDLATER, new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			               myEditor.putLong("launch_count", 0);
			               myEditor.commit();
			           }
			       });
			
			// Create the AlertDialog
			AlertDialog dialog = builder.create();
			dialog.show();
		}
	}
}
