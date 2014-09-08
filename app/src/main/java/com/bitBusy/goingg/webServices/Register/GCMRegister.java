/**
 * 
 */
package com.bitBusy.goingg.webServices.Register;

import java.io.IOException;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.util.Log;

import com.bitBusy.goingg.activity.ActivityCreatePrivateEvent;
import com.bitBusy.goingg.messaging.setup.gcmSetup.GCMRegistration;
import com.bitBusy.goingg.utility.Utility;

/**
 * @author SumaHarsha
 *
 */

public class GCMRegister extends AsyncTask<Void, Void, Void>{
	
	public static final String PROPERTY_REG_ID = "registration_id";
	private static final String REGISTERING = "Granting you a Going identity..";
	private String myRegistrationID;
	private GCMRegisterObserver myObserver;
	private static final String PROPERTY_APP_VERSION = "appVersion";
	private Context myContext;
	private ProgressDialog myProgressDialog;

	public GCMRegister(Context the_context)
	{
		if (the_context != null)
		{
			myContext = the_context;
			myProgressDialog = new ProgressDialog(the_context);
		}
	}
	
	@Override
    protected void onPreExecute()
	{
		myProgressDialog.setMessage(REGISTERING);
	    myProgressDialog.setCanceledOnTouchOutside(false);
		myProgressDialog.show();
	}
	public void registerObserver(GCMRegisterObserver the_observer)
	{
		myObserver = the_observer;
	}
	
		/* (non-Javadoc)
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected Void doInBackground(Void... params) {
			if (myContext != null)
				{
					 try { 
						 GCMRegistration instance = GCMRegistration.getInstance(myContext);
						 myRegistrationID = instance.getGCM().register(instance.getSenderID());

			                // You should send the registration ID to your server over HTTP,
			                // so it can use GCM/HTTP or CCS to send messages to your app.
			                // The request to your server should be authenticated if your app
			                // is using accounts.
			               // sendRegistrationIdToBackend();

			                // For this demo: we don't need to send it because the device
			                // will send upstream messages to a server that echo back the
			                // message using the 'from' address in the message.

			                // Persist the regID - no need to register again.
			                storeRegistrationId(myContext, myRegistrationID);
			            } catch (IOException ex) {
			               Utility.throwErrorMessage(myContext, GCMRegistration.OHOH, ex.getMessage());
			            }
			}
			return null;
		}
		
		
		 @Override
	        protected void onPostExecute(Void params) 
		 	{
			 	closeProgressDialog();
			 	if (myObserver != null)
			 	{
			 		Log.i(this.getClass().getSimpleName(), "registered :" + myRegistrationID);
			 		myObserver.onRegistered(myRegistrationID);
			 	}
	        }
		
		 private void closeProgressDialog()
			{
					if(myProgressDialog != null && myProgressDialog.isShowing())
					{
						myProgressDialog.dismiss();
					}
			}
			
		private SharedPreferences getGCMPreferences(Context the_context) {
			if (the_context != null)
			{
				 return the_context.getSharedPreferences(ActivityCreatePrivateEvent.class.getSimpleName(),
				            Context.MODE_PRIVATE);
			}
			return null;	      
		}
	
	private void storeRegistrationId(Context context, String regId) {
	    final SharedPreferences prefs = getGCMPreferences(context);
	    int appVersion = getAppVersion(context);
	    SharedPreferences.Editor editor = prefs.edit();
	    editor.putString(PROPERTY_REG_ID, regId);
	    editor.putInt(PROPERTY_APP_VERSION, appVersion);
	    editor.commit();
	}
	
	private static int getAppVersion(Context the_context) {
		if (the_context != null)
		{
			   try {
			        PackageInfo packageInfo = the_context.getPackageManager()
			                .getPackageInfo(the_context.getPackageName(), 0);
			        return packageInfo.versionCode;
			    } catch (NameNotFoundException e) {
			        Utility.throwErrorMessage(the_context, GCMRegistration.OHOH, e.getMessage());
			    }
		}
		return -1;
	}
}
