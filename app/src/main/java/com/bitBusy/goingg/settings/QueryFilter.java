/**
 * 
 */
package com.bitBusy.goingg.settings;

import java.util.Calendar;

import android.os.Parcel;
import android.os.Parcelable;

import com.bitBusy.goingg.events.EventCategory;

/**
 * @author SumaHarsha
 *
 */
public class QueryFilter implements Parcelable{

	/** data size*/
//	private static final int DATASIZE = 6;
	
	/** input parcel*/
	private Parcel myParcel;
	
	/** Event category*/
	private EventCategory[] myCategories;
	
	/** Date*/
	private Calendar myStartDateTime;
		
	/** End time*/
	private Calendar myEndDateTime;
	
	/** Distance*/
	private int myDistance;
	

	/**
	 * Parameterized constructor
	 * @param the_categories event categories
	 * @param the_date date of event
	 * @param the_fromTime start time
	 * @param the_toTime end time
	 * @param the_distance the distance from current location
	 */
	public QueryFilter(EventCategory[] the_categories, Calendar the_startDate, Calendar the_endDate,
						   int the_distance)
	{
		myCategories = the_categories;
		myStartDateTime = the_startDate;
		myEndDateTime = the_endDate;
		myDistance = the_distance;
	}
	
	public QueryFilter(Parcel the_input)
	{
	//	String[] data = new String[DATASIZE];
	//	the_input.readStringArray(data);
		if (the_input != null)
		{
			myParcel = the_input;
			readCategories();
			readDates();
			readDistance();		
		}
	}

	private void readCategories()
	{
		int size = myParcel.readInt();
		myCategories = new EventCategory[size];
		for (int i =0; i < size; i++)
		{
			myCategories[i] = (EventCategory)myParcel.readSerializable();
		}		
	}
	
	private void readDates()
	{
		myStartDateTime = (Calendar) myParcel.readSerializable();
		myEndDateTime = (Calendar) myParcel.readSerializable();
	}
	
	private void readDistance()
	{
		myDistance = myParcel.readInt();
	}

	
	/** 
	 * Set categories to query
	 * @param the_categories
	 */
	/*public void setCategories(EventCategory[] the_categories)
	{
		myCategories = the_categories;
	}
	
	/**
	 * Returns list of categories
	 * @return list of event categories
	 */
	public EventCategory[] getCategories()
	{
		return myCategories;
	}
	
	/**
	 * Sets the date
	 * @param the_date
	 */
	/*public void setStartDateTime(Calendar the_date)
	{
		myStartDateTime = the_date;
	}
	/**
	 * Returns date
	 * @return date
	 */
	public Calendar getStartDateTime()
	{
		return myStartDateTime;
	}
	public void setEndDateTime(Calendar the_endTime)
	{
		myEndDateTime = the_endTime;
	}
	/**
	 * Returns set end time
	 * @return end time
	 */
	public Calendar getEndDateTime()
	{
		return myEndDateTime;
	}
	
	/**
	 * Sets distance filter
	 * @param the_distance from current location
	 */
/*	public void setDistance(int the_distance)
	{
		myDistance = the_distance;
	}*/
	/**
	 * Returns set distance
	 * @return set distance filter
	 */
	public int getDistance()
	{
		return myDistance;
	}
	
	@Override
	public boolean equals(Object the_filter)
	{
		if (the_filter != null && the_filter instanceof QueryFilter)
		{
			QueryFilter filter = (QueryFilter) the_filter;

			return (compareCategories(filter)) && 
					(compareDates(myStartDateTime, filter.getStartDateTime())) &&
					(compareDates(myEndDateTime, filter.getEndDateTime())) &&
					(myDistance == filter.getDistance());

		}
		return false;
	}
	
	/**
	 * @param myEndDateTime2
	 * @param endDateTime
	 * @return
	 */
	private boolean compareDates(Calendar the_first, Calendar the_second) {
		if (the_first == null && the_second == null)
		{
			return true;
		}
		else if (the_first != null && the_second != null)
		{
			return (the_first.get(Calendar.YEAR) == the_second.get(Calendar.YEAR)) && (the_first.get(Calendar.MONTH) == the_second.get(Calendar.MONTH))
					&& (the_first.get(Calendar.DAY_OF_MONTH) == the_second.get(Calendar.DAY_OF_MONTH));
		}
		return false;
	}

	private boolean compareCategories(QueryFilter the_filter)
	{
		if (myCategories == null && the_filter.getCategories() == null)
		{
			return true;
		}
		else if (myCategories != null && the_filter.getCategories() != null)
		{
			if (myCategories.length != the_filter.getCategories().length)
			{
				return false;
			}
			else
			{
				for (int i=0; i < myCategories.length;i++)
				{
					if (!(myCategories[i] == the_filter.getCategories()[i]))
					{
						return false;
					}
				}
				return true;
			}
		}
		return false;
		
		}
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)
	 */
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		if (dest != null)
		{
			myParcel = dest;
			writeCategories();
			writeDates();
			writeDistance();		
		}
	}
	  /**
	 * 
	 */
	private void writeCategories() {
		int size = myCategories!=null?myCategories.length:0;
		myParcel.writeInt(size);
		for (int i = 0; i < size; i++)
		{
			myParcel.writeSerializable(myCategories[i]);
		}
	}
	
	private void writeDates()
	{
		myParcel.writeSerializable(myStartDateTime);
		myParcel.writeSerializable(myEndDateTime);
	}
	
		private void writeDistance()
	{
		myParcel.writeInt(myDistance);
	}
	public static final Creator<QueryFilter> CREATOR = new Creator<QueryFilter>() {
          public QueryFilter createFromParcel(Parcel in) {
              return new QueryFilter(in); 
          }

          public QueryFilter[] newArray(int size) {
              return new QueryFilter[size];
          }
      };
      
 
}
