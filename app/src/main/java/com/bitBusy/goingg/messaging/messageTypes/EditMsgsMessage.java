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
public class EditMsgsMessage extends GCMMessage{

	/**
	 * @param the_sender
	 * @param the_rx
	 * @param the_content
	 * @param the_sendDateTime
	 * @param the_rxDateTime
	 */
	public EditMsgsMessage(PrivateEventUser the_sender,
			PrivateEventUser the_rx, Object the_content,
			String the_sendDateTime, String the_rxDateTime) {
		super(the_sender, the_rx, the_content, the_sendDateTime, the_rxDateTime);
		setType(GCMMessageType.EditMessages);
	}
	
	@Override
	public boolean equals(Object the_obj){
		if (the_obj != null && the_obj instanceof EditMsgsMessage){
			EditMsgsMessage msg = (EditMsgsMessage) the_obj;
			PrivateEventUser sender = msg.getSender();
			PrivateEventUser receiver = msg.getReceiver();
			String sendDateTime = msg.getSendDateTime();
			return ((sender != null? sender.equals(mySender):mySender==null) &&
					(receiver != null? receiver.equals(myReceiver):myReceiver==null) &&
					(sendDateTime != null? sendDateTime.equals(mySendDateTime):mySendDateTime==null));
		}
		return false;
	}
	
	@Override
	public int hashCode(){
		if (mySender != null && myReceiver != null && mySendDateTime != null){
			return mySender.hashCode() + myReceiver.hashCode() + mySendDateTime.hashCode();
		}
		return 0;
	}	
}