/**
 * 
 */
package com.bitBusy.goingg.messaging.refresh;

import java.util.HashSet;

import android.content.Context;
import com.bitBusy.goingg.events.PrivateEvents.PrivateEventUser;
import com.bitBusy.goingg.messaging.setup.gcmSetup.GCMID;
import com.bitBusy.goingg.messaging.setup.gcmSetup.GCMMessage;
import com.bitBusy.goingg.messaging.setup.gcmSetup.GCMMessageManager;
import com.bitBusy.goingg.messaging.setup.gcmSetup.GCMMessengerListener;

/**
 * @author SumaHarsha
 *
 */
public class EditDBRequest{
	
	public static enum EDITPARAM {Add, Delete};
	protected PrivateEventUser myRefreshReceiver;
	protected PrivateEventUser mySender;
	protected PrivateEventUser myReceiver;
	protected Context myContext;
	protected Object myContent;
	private GCMMessengerListener myListener;
	protected GCMMessage myMsg;


	
	/**
	 * @param the_context
	 * @param the_sender
	 * @param the_rx
	 * @param the_content
	 * @param the_sendDateTime
	 * @param the_rxDateTime
	 * @param the_adapter
	 * @param the_msgList
	 * @param the_pos
	 */
	public EditDBRequest(Context the_context, PrivateEventUser the_sender,
			PrivateEventUser the_rx, Object the_content){
			myContext = the_context;
			mySender = the_sender;
			myReceiver = the_rx;
			myContent = the_content;
			buildRefreshReceiver();
	}
	
	public void registerListener(GCMMessengerListener the_listener){
		myListener = the_listener;
	}
	
	/**
	 * adds both sender and receiver gcmids as receiver 
	 */
	private void buildRefreshReceiver(){
		if (mySender != null && myReceiver != null && mySender.getGCMIDs() != null && myReceiver.getGCMIDs() != null){
			HashSet<GCMID> ids = new HashSet<GCMID>();
			ids.addAll(mySender.getGCMIDs());
			ids.addAll(myReceiver.getGCMIDs());
			myRefreshReceiver = new PrivateEventUser(null, null, null, ids, null);
		}
	}
	
	public void sendMessage(){
		GCMMessageManager mngr = new GCMMessageManager(myMsg, myContext);
		mngr.registerListener(myListener);
		mngr.sendMessage();		
	}
}