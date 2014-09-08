/**
 * 
 */
package com.bitBusy.goingg.invitation;

import com.bitBusy.goingg.events.PrivateEvents.PrivateEventUser;
import com.bitBusy.goingg.events.PrivateEvents.PrivateEventUserName;

/**
 * @author SumaHarsha
 *
 */
public class ExternalContact extends PrivateEventUser
{
	
	public static enum ContactType {Email, Skype, TextMsg, WhatsApp};
	private ContactType myType;
	
	public ExternalContact
	(String the_userid, PrivateEventUserName the_username,
			ContactType the_contactType)
	{
		super(the_userid, the_username, null, null, null);
		myType = the_contactType;
	}
	
	public ContactType getType()
	{
		return myType;
	}
}
