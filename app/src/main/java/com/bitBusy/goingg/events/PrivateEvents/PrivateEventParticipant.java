/**
 * 
 */
package com.bitBusy.goingg.events.PrivateEvents;

import java.util.HashSet;

import android.os.Parcel;
import android.os.Parcelable;

import com.bitBusy.goingg.messaging.setup.gcmSetup.GCMID;

/**
 * @author SumaHarsha
 *
 */
public class PrivateEventParticipant extends PrivateEventUser{

	private String myEventID;
	/**
	 * @param the_username
	 */
	public PrivateEventParticipant(String the_userid, PrivateEventUserName the_userName, City the_city, String the_eventID, HashSet<GCMID> the_ids, 
			HashSet<String> the_friendIDs) {
		super(the_userid, the_userName, the_city, the_ids, the_friendIDs);
		myEventID = the_eventID;
	}
	
	public String getEventID()
	{
		return myEventID;
	}
	
	public PrivateEventParticipant(Parcel the_parcel)
	{
		super(the_parcel);
		if (the_parcel != null)
		{
			readEventID();
		}
	}
	/**
	 * 
	 */
	private void readEventID() {
		myEventID = myParcel.readString();
	}

		/* (non-Javadoc)
	 * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)
	 */
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		super.writeToParcel(dest, flags);
		if (dest != null)
		{
			writeEventID();
		}
	}
	private void writeEventID() {
		myParcel.writeString(myEventID);
	}
	
	public static final Creator<PrivateEventParticipant> CREATOR = new PrivateEventParticipant.Creator<PrivateEventParticipant>() {
        public PrivateEventParticipant createFromParcel(Parcel in) {
            return new PrivateEventParticipant(in); 
        }

        public PrivateEventParticipant[] newArray(int size) {
            return new PrivateEventParticipant[size];
        }
    };

}
