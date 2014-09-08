/**
 * 
 */
package com.bitBusy.goingg.database.setup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import android.content.Context;
import android.util.Log;

import com.bitBusy.goingg.alarmSystem.EventReminder;
import com.bitBusy.goingg.cache.DataStructureCache;
import com.bitBusy.goingg.cache.LocalDBCache;
import com.bitBusy.goingg.database.queries.QueryTableMarkedEvents;
import com.bitBusy.goingg.database.queries.QueryTableMessages;
import com.bitBusy.goingg.database.queries.QueryTableSavedReminders;
import com.bitBusy.goingg.database.queries.QueryTableUserAccount;
import com.bitBusy.goingg.events.PrivateEvents.PrivateEventUser;
import com.bitBusy.goingg.messaging.setup.gcmSetup.GCMID;
import com.bitBusy.goingg.messaging.setup.gcmSetup.GCMMessage;

/**
 * @author SumaHarsha
 *
 */
public class DBInteractor {
	
	public enum EVENTMARKING {Going, Nope, Maybe};
	/**Context*/
	private Context myContext;
	
	/**
	 * @param the_context DB context
	 * @param the activity context
	 */
	public DBInteractor(Context the_context) {
		myContext = the_context;
	}
	
	/**
	 * @param the_calendar
	 * @return 
	 */
	public boolean saveAlarm(EventReminder the_reminder) {
		if (the_reminder != null)
		{
			return new QueryTableSavedReminders(myContext).saveAlarm(the_reminder);
		}
		return false;
		
	}


	/**
	 * @param parcelable
	 * @return 
	 */
	public boolean deleteAlarm(EventReminder the_reminder) {
		if (the_reminder != null)
		{
			return new QueryTableSavedReminders(myContext).deleteAlarm(the_reminder);
		}		
		return false;
	}

	/**
	 * @return
	 */
	public ArrayList<EventReminder> getAllReminders() {
		return new QueryTableSavedReminders(myContext).getAllReminders();
	}
		
	public boolean markEvent(String the_eventID, EVENTMARKING the_mark)
	{
			boolean returnVal = new QueryTableMarkedEvents(myContext).markEvent(the_eventID, the_mark);
			if (returnVal)
			{
				addToLocalCache(the_eventID, the_mark);
			}
			return returnVal;
	}
	
	/**
	 * @param the_eventID
	 * @param the_mark
	 */
	private void addToLocalCache(String the_eventID, EVENTMARKING the_mark) 
	{
		if (the_eventID != null)
		{
			HashMap<String, EVENTMARKING> map = new HashMap<String, EVENTMARKING>();
			map.put(the_eventID, the_mark);
			LocalDBCache.getInstance().addToDBCache(map);
		}
	}
	

	public boolean deleteEventMarking(String the_eventID)
	{
		boolean retVal = new QueryTableMarkedEvents(myContext).deleteEventMarking(the_eventID);
		if (retVal)
		{
			removeFromLocalCache(the_eventID);
		}
		return retVal;
	}
	
	private void removeFromLocalCache(String the_eventID) {
		LocalDBCache.getInstance().deleteFromCache(the_eventID);
	}

	public void initializeLocalDBCache() 
	{
		LocalDBCache.getInstance().addToDBCache(new QueryTableMarkedEvents(myContext).getAllEventMarkings());
	}
	
	public HashSet<GCMID> getAllGCMIDs(String the_user) {
		return new QueryTableUserAccount(myContext).getAllGCMIDs(the_user);
	}
	
	public HashSet<PrivateEventUser> getAllFriends(String the_user) {
		return new QueryTableUserAccount(myContext).getAllFriends(the_user);
	}	
	
	public boolean saveUser(PrivateEventUser the_user){
		return new QueryTableUserAccount(myContext).saveUser(the_user);
	}
	
	public PrivateEventUser getUser(String the_userID){
		return new QueryTableUserAccount(myContext).getUser(the_userID);
	}
	
	/**
	 * saves message in db
	 * @param the_msg
	 * @param received to indicate if this is a received msg (true) or a sent msg (false)
	 * @return
	 */
	public boolean saveMessage(GCMMessage the_msg, boolean received){
		Log.i(this.getClass().getSimpleName(), "saving msg: " + the_msg);
		boolean saved = new QueryTableMessages(myContext).saveMessage(the_msg, received);
		//re initialize msg list in cache
		if (saved){
			DataStructureCache.getInstance().clearMessagesList();
		}
		return saved;
	}
	
	public HashSet<GCMMessage> getAllInboxMessages(String the_user){
		return new QueryTableMessages(myContext).getAllInboxMessages();
	}
	
	/**
	 * deletes msg from db
	 * @param the_msg
	 * @return true if delete successful
	 */
	public boolean deleteMessage(GCMMessage the_msg){
		boolean deleted = new QueryTableMessages(myContext).deleteMsg(the_msg.getMsgID());
		if (deleted){
			DataStructureCache.getInstance().clearMessagesList();
		}
		return deleted;
	}
	
	public boolean addFriends(String the_firstuser, String the_seconduser){
		boolean added  = new QueryTableUserAccount(myContext).addFriends(the_firstuser, the_seconduser);
		if (added){
			DataStructureCache.getInstance().clearFriendsList();
		}
		return added;
	}
	
}
