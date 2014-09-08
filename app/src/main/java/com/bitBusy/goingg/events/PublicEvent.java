/**
 * 
 */
package com.bitBusy.goingg.events;

import java.util.Calendar;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import com.bitBusy.goingg.utility.Utility;
import com.google.android.gms.maps.model.LatLng;

/**
 * @author SumaHarsha
 *
 */
public class PublicEvent extends Event{

			
	public PublicEvent(EventCategory the_category, String the_name, String the_description,Address the_address,
			Calendar the_startDateTime, Calendar the_endDateTime, 
			LatLng the_coordinates, Link the_infoLink, Bitmap the_img, String the_price, boolean the_isGooglePlace, String the_creator, String the_rating,
			int the_numGoing, int the_numNope, int the_numMaybe)
	{
		super(the_category, the_name, the_description, the_address, the_startDateTime, the_endDateTime, 
			the_coordinates,the_infoLink, the_img, the_price, the_isGooglePlace,  the_creator,  the_rating,
			the_numGoing, the_numNope,the_numMaybe);
	}
	
	public PublicEvent(Parcel the_parcel)
	{
		super(the_parcel);
	}
	
	
	@Override
	public boolean equals(Object the_obj)
	{
		PublicEvent the_event = (PublicEvent) the_obj;
		if (the_event != null)
		{
			String name = the_event.getName();
			Calendar startDate = the_event.getStartDateTime();
			Calendar endDate = the_event.getEndDateTime();
			LatLng coords = the_event.getCoordinates();
			boolean googlePlace = the_event.isGooglePlace();
			String id = the_event.getID();
			
		return (name!=null?name.equals(myName):myName==null 
				&& startDate != null?startDate.equals(myStartDateTime):myStartDateTime==null
				&& endDate != null?endDate.equals(myEndDateTime):myEndDateTime==null
				&& coords != null?coords.equals(myCoordinates):myCoordinates==null
				&& id != null?id.equals(myID):myID == null
				&& googlePlace == isGooglePlace);
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
		return retHashcode;
	}
	
	public static final Parcelable.Creator<PublicEvent> CREATOR = new PublicEvent.Creator<PublicEvent>() {
        public PublicEvent createFromParcel(Parcel in) {
            return new PublicEvent(in); 
        }

        public PublicEvent[] newArray(int size) {
            return new PublicEvent[size];
        }
    };
}
