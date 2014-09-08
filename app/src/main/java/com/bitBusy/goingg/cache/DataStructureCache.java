/**
 * 
 */
package com.bitBusy.goingg.cache;

import java.util.ArrayList;
import java.util.HashSet;

import com.bitBusy.goingg.events.PrivateEvents.PrivateEvent;
import com.bitBusy.goingg.events.PrivateEvents.PrivateEventUser;
import com.bitBusy.goingg.messaging.setup.gcmSetup.GCMID;
import com.bitBusy.goingg.messaging.setup.gcmSetup.GCMMessage;

/**
 * @author SumaHarsha
 *
 */
public class DataStructureCache {
	
	private static DataStructureCache myInstance;
	private HashSet<PrivateEventUser> myFriends;
	private boolean isFriendsListInitialized = false;
	private boolean isGCMIDsListInitialized = false;
	private boolean isMsgsListInitialized = false;
	private HashSet<GCMMessage> myMessages;
	private ArrayList<PrivateEvent> myEvents;
	private HashSet<GCMID> myGCMIDs;
	private GCMID myGCMID;
	
	private DataStructureCache(){
		
	}
	
	public static DataStructureCache getInstance(){
		if (myInstance == null){
			myInstance = new DataStructureCache();
		}
		return myInstance;
	}
	
	public boolean isFriendsListInitialized(){
		return isFriendsListInitialized;
	}
	public void addFriends(HashSet<PrivateEventUser> the_friends){
		if (the_friends != null){
			if (myFriends == null){
				initializeFriendsList();
			}
			myFriends.addAll(the_friends);
			isFriendsListInitialized = true;
		}
	}
	
	private void initializeFriendsList(){
		myFriends = new HashSet<PrivateEventUser>();
	}
	
	public  HashSet<PrivateEventUser> getFriends(){
		return myFriends;
	}
	
	public void clearFriendsList(){
		if (myFriends != null){
			myFriends.clear();
			isFriendsListInitialized = false;
		}
	}
	
	public boolean isGCMIDsListInitialized(){
		return isGCMIDsListInitialized;
	}
	
	public boolean isMsgsInitialized(){
		return isMsgsListInitialized;
	}
	public void addMsgs(HashSet<GCMMessage> the_msgs){
		if (the_msgs != null){
			if (myMessages == null){
				initializeMsgsList();
			}
			myMessages.addAll(the_msgs);
			isMsgsListInitialized = true;
		}
	}
	
	private void initializeMsgsList(){
		myMessages = new HashSet<GCMMessage>();
	}
	
	public  HashSet<GCMMessage> getMessages(){
		return myMessages;
	}
	
	public void clearMessagesList(){
		if (myMessages != null){
			myMessages.clear();
			isMsgsListInitialized = false;
		}
	}
}