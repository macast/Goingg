/**
 * 
 */
package com.bitBusy.goingg.alarmSystem;

import java.util.Calendar;

import android.os.Parcel;
import android.os.Parcelable;

import com.bitBusy.goingg.events.PublicEvent;

/**
 * @author SumaHarsha
 *
 */
public class EventReminder  implements Parcelable{

	private int myID;
	
	private PublicEvent myEvent;
	
	private Calendar myDateTime;
	
	private Parcel myParcel;
	
	public EventReminder(PublicEvent the_event, Calendar the_dateTime)
	{
		myEvent = the_event;
		myDateTime = the_dateTime;
	}
	
	public EventReminder(int the_id, PublicEvent the_event, Calendar the_dateTime)
	{
		myID = the_id;
		myEvent = the_event;
		myDateTime = the_dateTime;
	}
	
	public EventReminder(Parcel the_input)
	{
		if (the_input != null)
		{
			myParcel = the_input;
			readID();
			readEvent();
			readDateTime();		
		}
	}
	
	private void readID()
	{
		if (myParcel != null)
		{
			myID = myParcel.readInt();
		}
	}
	
	private void readEvent()
	{
		if (myParcel != null)
		{
			myEvent = myParcel.readParcelable(PublicEvent.class.getClassLoader());
		}
	}
	
	private void readDateTime()
	{
		if (myParcel != null)
		{
			myDateTime = (Calendar) myParcel.readSerializable();
		}
	}
	public PublicEvent getEvent()
	{
		return myEvent;
	}
	
	public Calendar getDateTime()
	{
		return myDateTime;
	}
	
	public int getID()
	{
		return myID;
	}
	
	public void setID(int the_id)
	{
		myID = the_id;
	}
	
	/* (non-Javadoc)
	 * @see android.os.Parcelable#describeContents()
	 */
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		if (dest != null)
		{
			myParcel = dest;
			writeID();
			writeEvent();
			writeDateTime();
		}
	}
	
	private void writeID()
	{
		myParcel.writeInt(myID);
	}
	
	private void writeEvent()
	{
		myParcel.writeParcelable(myEvent, 0);
	}
	  /**
	 * 
	 */
	private void writeDateTime()
	{
		myParcel.writeSerializable(myDateTime);
	}
	
	public static final Creator<EventReminder> CREATOR = new Creator<EventReminder>() {
        public EventReminder createFromParcel(Parcel in) {
            return new EventReminder(in); 
        }

        public EventReminder[] newArray(int size) {
            return new EventReminder[size];
        }
    };
}

