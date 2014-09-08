/**
 * 
 */
package com.bitBusy.goingg.messaging.setup.gcmSetup;

import android.content.Context;

import com.bitBusy.goingg.utility.CurrentLoginState;
import com.bitBusy.goingg.webServices.Register.GCMRegister;
import com.bitBusy.goingg.webServices.Register.GCMRegisterObserver;
import com.bitBusy.goingg.webServices.UserData.SaveMyGCMID;

/**
 * @author SumaHarsha
 *
 */
public class GCMRegistrationChecker implements GCMRegisterObserver{
	
	private Context myContext;
	private GCMCheckListener myListener;
	private String myUserName;
	
	public GCMRegistrationChecker(Context the_context, String the_username){
		myContext = the_context;
		myUserName = the_username;
	}

	public void registerListener(GCMCheckListener the_listener){
		myListener = the_listener;
	}
	public void checkMyGCMRegistration(){
		String storedID = getStoredID();
		if (storedID == null || storedID == ""){
			registerWithGCM();
		}
		else{
		notifyListener();
		}
	}

	private String getStoredID() {
		return CurrentLoginState.getGCMID(myUserName, myContext);
	}

	private void registerWithGCM() 
	{
		GCMRegister reg = new GCMRegister(myContext);
		reg.registerObserver(this);
		reg.execute();
	}

	/* (non-Javadoc)
	 * @see com.bitBusy.going.webServices.Register.GCMRegisterObserver#onRegistered(java.lang.String)
	 */
	@Override
	public void onRegistered(String the_regID) {
		saveIDOnAWS(the_regID);
		storeIDLocal(the_regID);
		notifyListener();
	}
	
	private void storeIDLocal(String the_regID) {
		CurrentLoginState.setGCMID(myUserName, the_regID, myContext);
	}

	private void saveIDOnAWS(String the_regID){
		new SaveMyGCMID(myContext, new Object[]{myUserName, the_regID}).execute();
	}
	
	private void notifyListener(){
		if (myListener != null){
			myListener.onChecked();
		}
	}
	
	
	
}
