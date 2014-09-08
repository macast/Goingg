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
public class Guest extends PrivateEventParticipant{

	private boolean isGoing;
	
	/**
	 * @param the_id
	 */
	public Guest(String the_userid, PrivateEventUserName the_username, City the_city, String the_eventID,  HashSet<GCMID> the_ids, 
			HashSet<String> the_friendIDs, boolean the_isGoingFlag) {
		super(the_userid, the_username, the_city, the_eventID, the_ids, the_friendIDs);
		isGoing = the_isGoingFlag;
	}

	public boolean isGoing()
	{
		return isGoing;
	}
	
	public Guest(Parcel the_parcel)
	{
		super(the_parcel);
		if (the_parcel != null)
		{
			readIsGoingFlag();
		}
	}
	/**
	 * 
	 */
	private void readIsGoingFlag() {
		isGoing = myParcel.readByte()!=0;
	}

		/* (non-Javadoc)
	 * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)
	 */
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		super.writeToParcel(dest, flags);
		if (dest != null)
		{
			writeIsGoingFlag();
		}
	}
	private void writeIsGoingFlag() {
		myParcel.writeByte((byte) (isGoing?1:0));
	}
	
	public static final Parcelable.Creator<Guest> CREATOR = new Guest.Creator<Guest>() {
        public Guest createFromParcel(Parcel in) {
            return new Guest(in); 
        }

        public Guest[] newArray(int size) {
            return new Guest[size];
        }
    };
}
