/**
 * 
 */
package com.bitBusy.goingg.adapters;

import java.util.Calendar;
import java.util.Comparator;

import com.bitBusy.goingg.events.PublicEvent;

/**
 * @author SumaHarsha
 *
 */
public class EventWithDistance {

	private static final String DISTANCEMETRIC = " mi.";
	private PublicEvent myEvent;
	
	private double myDistance;
	
	public EventWithDistance(PublicEvent the_event, double the_distance)
	{
		myEvent = the_event;
		myDistance = the_distance;
	}
	
	public PublicEvent getEvent()
	{
		return myEvent;
	}
	
	public String getDistance()
	{
		return String.valueOf(myDistance) + DISTANCEMETRIC;
	}
	public double getDistanceValue()
	{
		return myDistance;
	}
	
	public static Comparator<EventWithDistance> DistanceComparator 
    = new Comparator<EventWithDistance>() {

		@Override
		public int compare(EventWithDistance the_first, EventWithDistance the_second) {
			if (the_first != null && the_second != null)
			{
				return Double.compare(the_first.getDistanceValue(), the_second.getDistanceValue());
			}
			return 0;
		}
	};
	
	public static Comparator<EventWithDistance> StartTimeComparator 
    = new Comparator<EventWithDistance>() {

		@Override
		public int compare(EventWithDistance the_first, EventWithDistance the_second) {
			if (the_first != null) 
			{
				if (the_second != null)
				{
					PublicEvent firstEvent = the_first.getEvent();
					PublicEvent secondEvent = the_second.getEvent();
					if (firstEvent != null && firstEvent.getStartDateTime() != null)  
					{
						if (secondEvent != null && secondEvent.getStartDateTime()!=null)
						{ 
							Calendar first = firstEvent.getStartDateTime();
							Calendar second = secondEvent.getStartDateTime();
							if ((first.get(Calendar.YEAR) > second.get(Calendar.YEAR)) ||
							((first.get(Calendar.YEAR) == second.get(Calendar.YEAR)) && (first.get(Calendar.MONTH) > second.get(Calendar.MONTH))) ||
							 ((first.get(Calendar.YEAR) == second.get(Calendar.YEAR)) && (first.get(Calendar.MONTH) == second.get(Calendar.MONTH)) && (first.get(Calendar.DAY_OF_MONTH) > second.get(Calendar.DAY_OF_MONTH))) ||
							 ((first.get(Calendar.YEAR) == second.get(Calendar.YEAR)) && (first.get(Calendar.MONTH) == second.get(Calendar.MONTH)) && (first.get(Calendar.DAY_OF_MONTH) == second.get(Calendar.DAY_OF_MONTH)) && (first.get(Calendar.HOUR_OF_DAY) > second.get(Calendar.HOUR_OF_DAY)))||
							 ((first.get(Calendar.YEAR) == second.get(Calendar.YEAR)) && (first.get(Calendar.MONTH) == second.get(Calendar.MONTH)) && (first.get(Calendar.DAY_OF_MONTH) == second.get(Calendar.DAY_OF_MONTH)) && (first.get(Calendar.HOUR_OF_DAY) == second.get(Calendar.HOUR_OF_DAY)) && (first.get(Calendar.MINUTE) > second.get(Calendar.MINUTE))))
							{
								return 1;
							}
							else if ((first.get(Calendar.YEAR) < second.get(Calendar.YEAR)) ||
									((first.get(Calendar.YEAR) == second.get(Calendar.YEAR)) && (first.get(Calendar.MONTH) < second.get(Calendar.MONTH))) ||
									 ((first.get(Calendar.YEAR) == second.get(Calendar.YEAR)) && (first.get(Calendar.MONTH) == second.get(Calendar.MONTH)) && (first.get(Calendar.DAY_OF_MONTH) < second.get(Calendar.DAY_OF_MONTH))) ||
									 ((first.get(Calendar.YEAR) == second.get(Calendar.YEAR)) && (first.get(Calendar.MONTH) == second.get(Calendar.MONTH)) && (first.get(Calendar.DAY_OF_MONTH) == second.get(Calendar.DAY_OF_MONTH)) && (first.get(Calendar.HOUR_OF_DAY) < second.get(Calendar.HOUR_OF_DAY)))||
									 ((first.get(Calendar.YEAR) == second.get(Calendar.YEAR)) && (first.get(Calendar.MONTH) == second.get(Calendar.MONTH)) && (first.get(Calendar.DAY_OF_MONTH) == second.get(Calendar.DAY_OF_MONTH)) && (first.get(Calendar.HOUR_OF_DAY) == second.get(Calendar.HOUR_OF_DAY)) && (first.get(Calendar.MINUTE) < second.get(Calendar.MINUTE))))
									{
										return -1;
									}
							else
							{
								return 0;
							}
						}
						return 1;
					}
					if (secondEvent != null && secondEvent.getStartDateTime()!=null)
					{ 
						return -1;
					}
					else
					{
						return 0;
					}
				}
				return 1;
			}
			if (the_second == null)
			{
				return 0;
			}
			else
			{
				return 1;
			}
		}
	};
	
	public static Comparator<EventWithDistance> CategoryAZComparator 
    = new Comparator<EventWithDistance>() {

		@Override
		public int compare(EventWithDistance the_first, EventWithDistance the_second) {
			if (the_first != null && the_second != null)
			{
				PublicEvent first = the_first.getEvent();
				PublicEvent second = the_second.getEvent();
				if (first != null && first.getCategory()!=null && first.getCategory().getName() != null && first.getCategory().getName().toString() != null)
				{
					if (second != null && second.getCategory()!=null && second.getCategory().getName() != null && second.getCategory().getName().toString() != null)
					{
						return first.getCategory().getName().toString().compareToIgnoreCase(second.getCategory().getName().toString());
					}
					return 1;
				}
			if (second != null && second.getCategory()!=null && second.getCategory().getName() != null && second.getCategory().getName().toString() != null)
				{
					return -1;
				}
				else
				{
					return 0;
				}
			}
			return 0;
		}
	};
}
