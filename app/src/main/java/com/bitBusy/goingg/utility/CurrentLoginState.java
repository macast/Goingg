/**
 * 
 */
package com.bitBusy.goingg.utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import com.bitBusy.goingg.WIDAuthentication.WIDAccessToken;

/**
 * @author SumaHarsha
 *
 */
public class CurrentLoginState {
	
		public static final String SIGNIN = "Sign in";
		public static final String SIGNOUT = "Sign out";
		private static final String CURRENTLY_LOGGED_IN = "current_user";
		private static final String DATE00 = "Date00";
		private static final String DATE01 = "Date01";		
		private static final String DATE02 = "Date02";
		private static final String DATE03 ="Date03";

	    public static void setUserLoggedInGoing(Context ctx, boolean userLoggedIn, String the_username) 
	    {
	    	SharedPreferences prefs = SharedPrefsMaintenance.getInstance(ctx).getGenSharedPrefs();
	    	if (prefs != null && the_username != null){
		        Editor editor = prefs.edit();
		        editor.putString(CURRENTLY_LOGGED_IN, userLoggedIn?the_username:"");
		        editor.commit();
	    	}
	    }
	    
	    public static String getCurrentUser(Context the_cntxt)
	    {
	    	SharedPreferences prefs = SharedPrefsMaintenance.getInstance(the_cntxt).getGenSharedPrefs();
	    	if (prefs != null){
	    		return prefs.getString(CURRENTLY_LOGGED_IN, "");
	    	}
	    	return null;
	    }
	    
	    public static void setUserLoggedInGoogle(Context ctx, boolean userLoggedIn, WIDAccessToken the_token){
	     	SharedPreferences prefs = SharedPrefsMaintenance.getInstance(ctx).getPrivSharedPrefs();
	    	if (prefs != null){
			    	if (prefs != null){
			        Editor editor = prefs.edit();
			        editor.putString(DATE00, the_token!=null?the_token.getToken():null);
					editor.putLong(DATE01, the_token!=null?the_token.getDate():0);
					editor.putString(DATE02, the_token!=null?the_token.getEmailID():null);
					editor.putBoolean(DATE03, userLoggedIn);
					editor.commit();
		    	}
	    	}
	    }
	    
	    public static boolean getUserLoggedInGoogle(Context ctx){
	    	SharedPreferences prefs = SharedPrefsMaintenance.getInstance(ctx).getPrivSharedPrefs();
	    	if (prefs != null){
	    		return prefs.getBoolean(DATE03, false);
	    	}
	    	return false;
	    }
	    
	    public static WIDAccessToken getWIDAccessToken(Context the_cntxt){
	    	SharedPreferences prefs = SharedPrefsMaintenance.getInstance(the_cntxt).getPrivSharedPrefs();
	    	if (prefs != null && prefs.getBoolean(DATE03, false)){
	    			return new WIDAccessToken(prefs.getString(DATE00, null),
	    				prefs.getLong(DATE01, 0), prefs.getString(DATE02, null));
	    		}
	    	return null;
	    }
	    
	    public static void setGCMID(String the_username, String the_id, Context the_context){
	    	if (the_id != null  && the_username != null){
	    	 	SharedPreferences prefs = SharedPrefsMaintenance.getInstance(the_context).getGCMSharedPrefs();
		    	if (prefs != null){
				        Editor editor = prefs.edit();
				        editor.putString(the_username, the_id);
						editor.commit();
		    	}

	    	}
	    }
	    
	    public static String getGCMID(String the_username, Context the_context){
	    	if (the_username != null){
	    	 	SharedPreferences prefs = SharedPrefsMaintenance.getInstance(the_context).getGCMSharedPrefs();
		    	if (prefs != null){
		    		return prefs.getString(the_username, null);
		    	}
	    	}
	    	return null;
	    }
}
