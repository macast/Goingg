/**
 * 
 */
package com.bitBusy.goingg.events.PrivateEvents;

import java.util.HashSet;

import com.bitBusy.goingg.messaging.setup.gcmSetup.GCMID;

/**
 * @author SumaHarsha
 *
 */
public class Host extends PrivateEventParticipant{
	
	/**
	 * @param the_id
	 */
	public Host(String the_userid, PrivateEventUserName the_name, City the_city, String the_eventID, HashSet<GCMID> the_ids, 
			HashSet<String> the_friendIDs) {
		super(the_userid, the_name, the_city, the_eventID, the_ids, the_friendIDs);
	}

	
}
