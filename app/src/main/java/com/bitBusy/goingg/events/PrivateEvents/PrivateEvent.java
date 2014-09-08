/**
 * 
 */
package com.bitBusy.goingg.events.PrivateEvents;

import java.util.Calendar;
import java.util.List;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import com.bitBusy.goingg.events.Address;
import com.bitBusy.goingg.events.Event;
import com.bitBusy.goingg.events.EventCategory;
import com.bitBusy.goingg.events.Link;
import com.bitBusy.goingg.utility.AWSItemNameGenerator;
import com.bitBusy.goingg.utility.Utility;
import com.google.android.gms.maps.model.LatLng;

/**
 * @author SumaHarsha
 *
 */
public class PrivateEvent extends Event {
	
	private static final String PRIVATE = "PrivateGoingEvent";

	private List<Guest> myGuestList;
	
	private Host myHost;

	public PrivateEvent(EventCategory the_category, String the_name,
			String the_description, Address the_address,
			Calendar the_startDateTime, Calendar the_endDateTime,
			LatLng the_coordinates, Link the_infoLink, Bitmap the_img,
			String the_price, boolean the_isGooglePlace, String the_creator,
			String the_rating, int the_numGoing, int the_numNope,
			int the_numMaybe, List<Guest> the_guests, String the_eventID, Host the_host){
		super(the_category, the_name, the_description, the_address, the_startDateTime,
				the_endDateTime, the_coordinates, the_infoLink, the_img, the_price,
				the_isGooglePlace, the_creator, the_rating, the_numGoing, the_numNope,
				the_numMaybe);
		myGuestList = the_guests;
		myHost = the_host;
	}
	
	
	public List<Guest> getGuestList()
	{
		return myGuestList;
	}
	
	public Host getHost()
	{
		return myHost;
	}

	@Override
	public boolean equals(Object the_obj)
	{
		PrivateEvent the_event = (PrivateEvent) the_obj;
		if (the_event != null)
		{
			String name = the_event.getName();
			Calendar startDate = the_event.getStartDateTime();
			Calendar endDate = the_event.getEndDateTime();
			LatLng coords = the_event.getCoordinates();
			boolean googlePlace = the_event.isGooglePlace();
			Host host = the_event.getHost();
			List<Guest> guests = the_event.getGuestList();
		return (name!=null?name.equals(myName):myName==null 
				&& startDate != null?startDate.equals(myStartDateTime):myStartDateTime==null
				&& endDate != null?endDate.equals(myEndDateTime):myEndDateTime==null
				&& coords != null?coords.equals(myCoordinates):myCoordinates==null
				&& googlePlace == isGooglePlace &&
				host != null?host.equals(myHost):myHost == null &&
				guests != null?guests.equals(myGuestList):myGuestList == null);
		}
		return false;
		}
	
	@Override
	public int hashCode()
	{
		int nameLen = myName!=null?myName.length():0;
		String startDate = Utility.getDateTime(myStartDateTime);
		int startDateLen = startDate != null?startDate.length():0;
		String endDate = Utility.getDateTime(myEndDateTime);
		int endDateLen = endDate!=null?endDate.length():0;
		int retHashcode = myAddress!=null?myAddress.hashCode():0;
		if (myCoordinates != null)
		{
			retHashcode += Double.valueOf(myCoordinates.latitude).hashCode() + Double.valueOf(myCoordinates.longitude).hashCode();
		}
		if (myName != null)
		{
			char[] name = myName.toCharArray();
			for (int i = 0; i < nameLen; i++)
			{
				retHashcode += Math.pow(name[i]*31, nameLen-i+1);
			}
		}
		
		if (startDate != null)
		{
			char[] start = startDate.toCharArray();
			for (int i = 0; i < startDateLen; i++)
			{
				retHashcode += Math.pow(start[i]*31, startDateLen-i+1);
			}
		}	
		
		if (endDate != null)
		{
			char[] end = endDate.toCharArray();
			for (int i = 0; i < endDateLen; i++)
			{
				retHashcode += Math.pow(end[i]*31, endDateLen-i+1);
			}
		}	
		if (myHost != null && myHost.getUserName() != null)
		{
			char[] host = myHost.getUserName().toCharArray();
			for (int i = 0; i < myHost.getUserName().length(); i++)
			{
				retHashcode += Math.pow(host[i]*31, myHost.getUserName().length()-i+1);
			}
		}	
		return retHashcode;
	}
	
	public PrivateEvent(Parcel the_parcel)
	{
		super(the_parcel);
		if (the_parcel != null)
		{
			readHost();
			readGuestList();
		}
	}


		private void readHost() {
			myHost = myParcel.readParcelable(Host.class.getClassLoader());
		}
		
		private void readGuestList(){
			myGuestList  = Utility.castList(Guest.class, myParcel.readArrayList(Guest.class.getClassLoader()));
		}
		
		@Override
		public void writeToParcel(Parcel dest, int flags) {
			super.writeToParcel(dest, flags);
			writeHost();
			writeGuestList();
		}


		private void writeHost() {
			myParcel.writeParcelable(myHost, 0);
		}
		
		private void writeGuestList()
		{
			myParcel.writeList(myGuestList);
		}
		
		public static final Parcelable.Creator<PrivateEvent> CREATOR = new PrivateEvent.Creator<PrivateEvent>() {
	        public PrivateEvent createFromParcel(Parcel in) {
	            return new PrivateEvent(in); 
	        }

	        public PrivateEvent[] newArray(int size) {
	            return new PrivateEvent[size];
	        }
	    };
	    
	    protected void initializeID()
		{
			myID = PRIVATE.concat(AWSItemNameGenerator.generate(this));
		}
	    
}
