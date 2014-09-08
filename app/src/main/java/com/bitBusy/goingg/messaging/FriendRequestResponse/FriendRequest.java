/**
 * 
 */
package com.bitBusy.goingg.messaging.FriendRequestResponse;

import java.util.Calendar;

import android.content.Context;
import android.widget.Toast;

import com.bitBusy.goingg.database.setup.DBInteractor;
import com.bitBusy.goingg.events.PrivateEvents.PrivateEventUser;
import com.bitBusy.goingg.messaging.messageTypes.FriendRequestMessage;
import com.bitBusy.goingg.messaging.setup.gcmSetup.GCMMessageManager;
import com.bitBusy.goingg.messaging.setup.gcmSetup.GCMMessengerListener;
import com.bitBusy.goingg.utility.CurrentLoginState;
import com.bitBusy.goingg.utility.Utility;
import com.bitBusy.goingg.webServices.UserData.GetPrivateEventUserForID;
import com.bitBusy.goingg.webServices.awsSetup.AWSOperationListenerInterface;
import com.bitBusy.goingg.webServices.awsSetup.ListenerAck;

/**
 * @author SumaHarsha
 *
 */
public class FriendRequest implements AWSOperationListenerInterface, GCMMessengerListener {

	private Context myContext;
	private String myFriendUserID;
	private PrivateEventUser myFriend;
	private PrivateEventUser myself;
	private DBInteractor myDBInteractor;
	private String myMsg;
	
	public FriendRequest(Context the_context,String the_friendID, String the_message){
		myContext = the_context;
		myFriendUserID = the_friendID;
		myDBInteractor = new DBInteractor(myContext);
		myMsg = the_message;
	}
	
	public void send(){
		getMyUserObject();
		getFriendGCMID();
	}
	
	private void getMyUserObject() {
		String userid = CurrentLoginState.getCurrentUser(myContext);
		if (myDBInteractor != null){
			myself = myDBInteractor.getUser(userid);
		}
	}

	private void getFriendGCMID() {
		if (myFriendUserID != null){
			if (myDBInteractor != null){
				myFriend = myDBInteractor.getUser(myFriendUserID);
			}
			if (myFriend == null){
				getFriendFromAWS();
			}
			else{
				sendRequest();
			}
		}
	}
	
	private void getFriendFromAWS(){
		GetPrivateEventUserForID getDets = new GetPrivateEventUserForID(myContext, new Object[]{myFriendUserID});
		getDets.registerListener(this);
		getDets.execute();		
	}
	
	private void onGCMIDObtained(Object[] the_params){
		if (the_params != null && the_params.length > 0){
			try{
				myFriend = (PrivateEventUser) the_params[0];
				sendRequest();
			}catch(Exception e){
			}
		}
	}

	private void sendRequest(){
		GCMMessageManager mngr = new GCMMessageManager(buildFriendRequestMsg(), myContext);
		mngr.registerListener(this);
		mngr.sendMessage();		
	}
	
	private FriendRequestMessage buildFriendRequestMsg() {
		return new FriendRequestMessage
				(myself, myFriend , myMsg, Utility.getDateTime(Calendar.getInstance()), null);
	}

	/* (non-Javadoc)
	 * @see com.bitBusy.going.webServices.awsSetup.AWSOperationListenerInterface#onOperationComplete(com.bitBusy.going.webServices.awsSetup.ListenerAck)
	 */
	@Override
	public void onOperationComplete(ListenerAck the_ack) {
		if (the_ack != null && the_ack.getClassName() != null){
			String className = the_ack.getClassName();
			if (className.equals(GetPrivateEventUserForID.class.getName())){
				onGCMIDObtained(the_ack.getParams());
			}
		}
		
	}

	/* (non-Javadoc)
	 * @see com.bitBusy.going.messaging.setup.gcmSetup.GCMMessengerListener#onSend(int)
	 */
	@Override
	public void onSend(int the_code) {
		if (the_code == GCMMessageManager.SUCCESSCODE){
			Toast.makeText(myContext, "Friend request sent to " + Utility.getUserName(myContext, myFriend), Toast.LENGTH_LONG).show();
		}
	}
}
