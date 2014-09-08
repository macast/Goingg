/**
 * 
 */
package com.bitBusy.goingg.messaging.setup.gcmSetup;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

/**
 * @author SumaHarsha
 *
 */
public class GCMRegistration {
	public static final String OHOH = "OH NO!";
	public static final String EXTRA_MESSAGE = "message";
    //private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	private static GCMRegistration myInstance;
	private static String mySenderID; 
	private static String myRegistrationID;
    private static GoogleCloudMessaging myGCM;

    public static GCMRegistration getInstance(Context the_context)
	{
		if (myInstance == null)
		{
			myInstance = new GCMRegistration();
			initializeSenderID();
			initializeGCM(the_context);
		}
		return myInstance;
	}
	

	/**
	 * 
	 */
	private static void initializeGCM(Context the_context) 
	{
        if (myGCM == null) {
        	myGCM = GoogleCloudMessaging.getInstance(the_context);
        }     		
	}

	public String getSenderID()
	{
		return mySenderID;
	}
	/**
	 * 
	 */
	//CHANGE THIS CHANGE THIS CHANGE THIS
	private static void initializeSenderID() {
		Log.i("gcm", "CHANGE THIS");
		mySenderID = "58548578962"; 
	}


	private GCMRegistration()
	{
		
	}
	
	public String getRegId()
	{
		return myRegistrationID;
	}
	
	public GoogleCloudMessaging getGCM()
	{
		return myGCM;
	}
}
	
	/**
	 * 
	 */
	/*private static void initializeRegID(Context the_context)
	{
        myRegistrationID = "";
		final SharedPreferences prefs = getGCMPreferences(the_context);
		if (prefs != null)
		{
			  String registrationId = prefs.getString(PROPERTY_REG_ID, "");
			    int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
			    int currentVersion = getAppVersion(the_context);
			    if (registeredVersion == currentVersion) 
			    {
			    	Regis
			    }		
		}
	  }
	

	
	*/
	

