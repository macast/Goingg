/**
 * 
 */
package com.bitBusy.goingg.utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * @author SumaHarsha
 *
 */
public class SharedPrefsMaintenance {
	
    private static final String DATE = "DateDets";
    private static final String GSC = "gsc";
	private static SharedPreferences myGenSharedPrefs;
	private static SharedPreferences myPrivateSharedPrefs;
	private static SharedPreferences myGCMSharedPrefs;
	private static SharedPrefsMaintenance myInstance;
	private static Context myContext;
	
	private  SharedPrefsMaintenance(){
	}
	
	public static SharedPrefsMaintenance getInstance(Context the_context){
		if (myInstance == null && the_context != null){
			myInstance = new SharedPrefsMaintenance();
			myContext = the_context;
			setFieldValues();
		}
		return myInstance;
	}
	
	private static void setFieldValues(){
		setGenSharedPrefs();
		setPrivateSharedPrefs();
		setGCMSharedPrefs();
	}
	
	private static void setGenSharedPrefs(){
		if (myContext != null){
			myGenSharedPrefs = PreferenceManager.getDefaultSharedPreferences(myContext);
		}
	}
	
	private static void setPrivateSharedPrefs(){
		if (myContext != null){
			myPrivateSharedPrefs = myContext.getSharedPreferences(DATE,Context.MODE_PRIVATE);
		}
	}
	
	private static void setGCMSharedPrefs(){
		if (myContext != null){
			myGCMSharedPrefs = myContext.getSharedPreferences(GSC,Context.MODE_PRIVATE);
		}
	}
	
	public SharedPreferences getGenSharedPrefs(){
		return myGenSharedPrefs;
	}
	
	public SharedPreferences getPrivSharedPrefs(){
		return myPrivateSharedPrefs;
	}
	
	public SharedPreferences getGCMSharedPrefs(){
		return myGCMSharedPrefs;
	}
}