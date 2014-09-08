/**
 * 
 */
package com.bitBusy.goingg.messaging.refresh;

import java.util.Calendar;

import android.content.Context;

import com.bitBusy.goingg.events.PrivateEvents.PrivateEventUser;
import com.bitBusy.goingg.messaging.messageTypes.EditFriendsMessage;
import com.bitBusy.goingg.utility.Utility;

/**
 * @author SumaHarsha
 *
 */
public class EditFriends extends EditDBRequest{

	/**
	 * @param the_context
	 * @param the_sender
	 * @param the_rx
	 * @param the_content
	 */
	public EditFriends(Context the_context, PrivateEventUser the_sender,
			PrivateEventUser the_rx, Object the_content) {
		super(the_context, the_sender, the_rx, the_content);
		buildMsg();
	}

	/**
	 * builds msg to be sent
	 */
	private void buildMsg() {
		myMsg = new EditFriendsMessage(mySender, myReceiver, myContent, Utility.getDateTime(Calendar.getInstance()), null);
	}
	
	
}
