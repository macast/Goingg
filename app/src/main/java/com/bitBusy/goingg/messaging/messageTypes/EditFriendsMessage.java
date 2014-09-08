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
public class EditFriendsMessage extends GCMMessage{

	/**
	 * @param the_sender
	 * @param the_rx
	 * @param the_content
	 * @param the_sendDateTime
	 * @param the_rxDateTime
	 */
	public EditFriendsMessage(PrivateEventUser the_sender,
			PrivateEventUser the_rx, Object the_content,
			String the_sendDateTime, String the_rxDateTime) {
		super(the_sender, the_rx, the_content, the_sendDateTime, the_rxDateTime);
		setType(GCMMessageType.EditFriends);
	}
	
	@Override
	public boolean equals(Object the_obj){
		if (the_obj != null && the_obj instanceof EditFriendsMessage){
			EditFriendsMessage msg = (EditFriendsMessage) the_obj;
			PrivateEventUser sender = msg.getSender();
			PrivateEventUser receiver = msg.getReceiver();
			String sendDateTime = msg.getSendDateTime();
			String content = msg.getContent()!=null?msg.getContent().toString():null;
			return ((sender != null? sender.equals(mySender):mySender==null) &&
					(receiver != null? receiver.equals(myReceiver):myReceiver==null) &&
					(sendDateTime != null? sendDateTime.equals(mySendDateTime):mySendDateTime==null) &&
					(content != null?content.equals(myContent.toString()):myContent == null));
		}
		return false;
	}
	
	@Override
	public int hashCode(){
		if (mySender != null && myReceiver != null && mySendDateTime != null && myContent != null && myContent.toString() != null){
			return mySender.hashCode() + myReceiver.hashCode() + mySendDateTime.hashCode() + myContent.toString().hashCode();
		}
		return 0;
	}	
}
