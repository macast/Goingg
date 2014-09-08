/**
 * 
 */
package com.bitBusy.goingg.messaging.setup.gcmSetup;

import com.bitBusy.goingg.events.PrivateEvents.PrivateEventUser;
import com.bitBusy.goingg.messaging.setup.gcmSetup.GCMMessage.GCMMessageType;

/**
 * @author SumaHarsha
 *
 */
public interface GCMMessageInterface 
{
	public GCMMessageType getMessageType();
	public PrivateEventUser getReceiver();
	public PrivateEventUser getSender();
	public Object getContent();

	
}
