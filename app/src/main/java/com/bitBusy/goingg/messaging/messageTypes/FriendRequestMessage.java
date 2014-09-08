/**
 * 
 */
package com.bitBusy.goingg.messaging.messageTypes;

import com.bitBusy.goingg.events.PrivateEvents.PrivateEventUser;
import com.bitBusy.goingg.messaging.setup.gcmSetup.GCMMessage;

/**
 * @author SumaHarsha
 *
 */
public class FriendRequestMessage extends GCMMessage{

	public FriendRequestMessage(PrivateEventUser the_sender, PrivateEventUser the_rx, Object the_content, String the_sendDateTime, 
			String the_rxDateTime){
		super(the_sender, the_rx, the_content, the_sendDateTime, the_rxDateTime);
		setType(GCMMessageType.FriendRequest);
	}
	
	@Override
	public boolean equals(Object the_obj){
		if (the_obj != null && the_obj instanceof FriendRequestMessage){
			FriendRequestMessage msg = (FriendRequestMessage) the_obj;
			PrivateEventUser sender = msg.getSender();
			return (sender != null? sender.equals(mySender):mySender==null);
		}
		return false;
	}
	
	@Override
	public int hashCode(){
		if (mySender != null){
			return mySender.hashCode();
		}
		return 0;
	}	
}
