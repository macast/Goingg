/**
 * 
 */
package com.bitBusy.goingg.utility;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.bitBusy.goingg.events.DataSources;
import com.bitBusy.goingg.events.EventCategory;
import com.bitBusy.goingg.events.PublicEvent;
import com.bitBusy.goingg.events.PrivateEvents.PrivateEventUser;
import com.bitBusy.goingg.messaging.setup.gcmSetup.GCMMessage.GCMMessageType;

/**
 * @author SumaHarsha
 *
 */
public class Utility {

	public static final boolean LOGGING = true;
	
	public static final String SPACE = " ";
	
	public static final String DATESPLITTER = "-";
	
	public static final String DATESPLITTERSLASH = "/";

	public static final String TIMESPLITTER = ":";
	
	/** Zero padding*/
	public static final String ZEROPADDING = "%02d";

	
	/** returns calendar obj from str
	 * @param the_dateTime
	 * @return
	 */
	public static Calendar getCalendar(String the_dateTime)
	{
		Calendar resultCalendar = null;
		if (the_dateTime != null)
		{
			String[] components = the_dateTime.split(SPACE);
			if (components != null && components.length == 2)
			{
				resultCalendar = getCalendar(components);
			}
		}
		return resultCalendar;
	}
	
	public static Calendar getCalendarTSeparated(String the_dateTime)
	{
		Calendar resultCalendar = null;
		if (the_dateTime != null)
		{
			String[] components = the_dateTime.split("T");
			if (components != null && components.length == 2)
			{
				resultCalendar = getCalendar(components);
			}
		}
		return resultCalendar;
	}
		
	/**
	 * @param components
	 * @return
	 */
	private static Calendar getCalendar(String[] the_components) {
		if (the_components != null)
		{
			Calendar resultCalendar = Calendar.getInstance();
			int month = 0, day = 0, year = 0, hour = 0, min = 0;
			String[] dateComponents = the_components[0].split(DATESPLITTER);
			if (dateComponents != null && dateComponents.length == 3)
			{
				 month = Integer.parseInt(dateComponents[1]);
				 day = Integer.parseInt(dateComponents[2]);
				 year = Integer.parseInt(dateComponents[0]);
			}
			String[] timeComponents = the_components[1].split(TIMESPLITTER);
			if (timeComponents != null && timeComponents.length >= 2)
			{
				hour = Integer.parseInt(timeComponents[0]);
				min = Integer.parseInt(timeComponents[1]);
			}
			resultCalendar.set(Calendar.MONTH, month-1);
			resultCalendar.set(Calendar.DAY_OF_MONTH, day);
			resultCalendar.set(Calendar.YEAR, year);
			resultCalendar.set(Calendar.HOUR_OF_DAY, hour);
			resultCalendar.set(Calendar.MINUTE, min);
			return resultCalendar;
		}
		return null;
	}



	public static Calendar getCalendarForReminder(String the_dateTime)
	{
		Calendar resultCalendar = null;
		if (the_dateTime != null)
		{
			resultCalendar = Calendar.getInstance();
			String[] components = the_dateTime.split(SPACE);
			if (components != null && components.length == 2)
			{
				int month = 0, day = 0, year = 0, hour = 0, min = 0;
				String[] dateComponents = components[0].split(DATESPLITTER);
				if (dateComponents != null && dateComponents.length == 3)
				{
					 month = Integer.parseInt(dateComponents[0]);
					 day = Integer.parseInt(dateComponents[1]);
					 year = Integer.parseInt(dateComponents[2]);
				}
				String[] timeComponents = components[1].split(TIMESPLITTER);
				if (timeComponents != null && timeComponents.length >= 2)
				{
					hour = Integer.parseInt(timeComponents[0]);
					min = Integer.parseInt(timeComponents[1]);
				}
				resultCalendar.set(Calendar.MONTH, month-1);
				resultCalendar.set(Calendar.DAY_OF_MONTH, day);
				resultCalendar.set(Calendar.YEAR, year);
				resultCalendar.set(Calendar.HOUR_OF_DAY, hour);
				resultCalendar.set(Calendar.MINUTE, min);
			}
		}
		return resultCalendar;
	}
	public static Calendar getCalendarFromOnlyDate(String the_date)
	{
		Calendar resultCalendar = Calendar.getInstance();
		if (the_date != null)
		{
				int month = 0, day = 0, year = 0;//, hour = 0, min = 0;
				String[] dateComponents = the_date.split(DATESPLITTER);
				if (dateComponents != null && dateComponents.length == 3)
				{
					 month = Integer.parseInt(dateComponents[0]);
					 day = Integer.parseInt(dateComponents[1]);
					 year = Integer.parseInt(dateComponents[2]);
				}
				resultCalendar.set(Calendar.MONTH, month-1);
				resultCalendar.set(Calendar.DAY_OF_MONTH, day);
				resultCalendar.set(Calendar.YEAR, year);
		}
		return resultCalendar;
	}
	
	/** Gets category names
	 * @return
	 */
	public static CharSequence[] getCategoryNames(EventCategory[] the_categories)
	{
		if (the_categories != null)
		{
			CharSequence[] resultArr = new CharSequence[the_categories.length];
			int i = 0;
			for (EventCategory category : the_categories)
			{
			resultArr[i] = category.getName().toString();
			i++;
			}
		return resultArr;
		}
		return null;
	}
	
	
	/** gets all cat names*/
	public static CharSequence[] getAllCategoryNames()
	{
		CharSequence[] result = new CharSequence[EventCategory.values().length];
		int i = 0;
		for(EventCategory category: EventCategory.values())
		{
			result[i] = category.getName().name();
			i++;
		}
		return result;
	}
	
	public boolean compareCategories(EventCategory the_first, EventCategory the_second)
	{
		if (the_first == null && the_second == null)
		{
			return true;
		}
		else if (the_first != null && the_second != null)
		{
			return the_first.getName()!=null?the_first.getName().equals(the_second.getName()):the_second.getName()==null &&
					the_first.getColor() == the_second.getColor() ;//&& compareMaps(the_first.getURIs(), the_second.getURIs());
					//compareArrays(the_first.getURIs(), the_second.getURIs());
		}
		return false;
	}
	
	
	public static boolean compareMaps(HashMap<String, String[]> the_first, HashMap<String,String[]> the_second)
	{
		if (the_first == null && the_second == null)
		{
			return true;
		}
		else if (the_first != null && the_second != null)
		{
			boolean returnValue = true;
			Set<String> keyset = the_first.keySet();
			Iterator<String> iterator = keyset.iterator();
			if(iterator != null)
			{
				while(returnValue && iterator.hasNext())
				{
					String key = iterator.next();
					if (key != null)
					{
						String[] firstArray = the_first.get(key);
						String[] secondArray = the_second.get(key);
						returnValue = returnValue && compareArrays(firstArray, secondArray);
					}
				}
			}
		}
		return false;
	}
	public static boolean compareArrays(String[] the_first, String[] the_second)
	{
		boolean returnValue = false;
		if (the_first != null && the_second != null && the_first.length == the_second.length)
		{
			returnValue = true;
			for (int i=0; i < the_first.length; i++)
			{
				if (returnValue && !the_first[i].equals(the_second[i]))
				{
					returnValue = false;
				}
			}
		}
		return returnValue;
	}
	
	public static String getDateTime(Calendar the_calendar)
	{
		if (the_calendar != null)
		{
			StringBuilder builder = new StringBuilder();
			builder.append(getDate(the_calendar)).append(SPACE).
			append(String.format(ZEROPADDING,the_calendar.get(Calendar.HOUR_OF_DAY))).
			append(TIMESPLITTER).append(String.format(ZEROPADDING,the_calendar.get(Calendar.MINUTE)));
			return builder.toString();			
		}
		return null;
	}
	
	public static String getDate(Calendar the_calendar)
	{
		if (the_calendar != null)
		{
			StringBuilder builder = new StringBuilder(); 
			builder.append(String.format(ZEROPADDING,the_calendar.get(Calendar.MONTH) + 1))
			.append(DATESPLITTER).append(String.format(ZEROPADDING, the_calendar.get(Calendar.DAY_OF_MONTH))).
			append(DATESPLITTER).append(the_calendar.get(Calendar.YEAR));
			return builder.toString();			
		}
		return null;
	}
	/** compares two cal dates
	 * @param the_first
	 * @param the_second
	 * @return 1 if first > sec, -1 if first < sec, 0 if first == sec
	 */
	public static int compareCalendarDates(Calendar the_first, Calendar the_second)
	{
		int ret = 1;
		if (the_first != null && the_second != null)
		{
			int day1 = the_first.get(Calendar.DAY_OF_MONTH);
			int day2 = the_second.get(Calendar.DAY_OF_MONTH);
			int month1 = the_first.get(Calendar.MONTH);
			int month2 = the_second.get(Calendar.MONTH);
			int year1 = the_first.get(Calendar.YEAR);
			int year2 = the_second.get(Calendar.YEAR);
		
			if(year1< year2 || ((year1 == year2) && month1 < month2)|| ((year1== year2) && (month1 == month2) && (day1 < day2)))
			{
				ret =  -1;
			}
			else if ((year1 == year2) && (month1 == month2) && (day1 == day2))
			{
				ret =  0;
			}
		}
		return ret;
	}
	
	
	
	/**
	 * @param startDateTime
	 * @return
	 */
	public static String getAWSDBDateTime(Calendar the_calendar) {
		if (the_calendar != null)
		{
			StringBuilder buffer = new StringBuilder();
			buffer.append(the_calendar.get(Calendar.YEAR));
			buffer.append(String.format(ZEROPADDING,the_calendar.get(Calendar.MONTH)+1)).
			append(String.format(ZEROPADDING,the_calendar.get(Calendar.DAY_OF_MONTH))).append("T").
			append(String.format(ZEROPADDING, the_calendar.get(Calendar.HOUR_OF_DAY))).
			append(String.format(ZEROPADDING, the_calendar.get(Calendar.MINUTE)));
			return buffer.toString();
		}
		return null;
	}

	/**
	 * @param string
	 */
	public static void throwErrorMessage(Context the_context, String the_title, String the_msg) {
		final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				the_context);
 			alertDialogBuilder.setTitle(the_title!=null?the_title:"");
 			alertDialogBuilder
				.setMessage(the_msg!=null?the_msg:"")
				.setCancelable(false)
				.setPositiveButton("OK",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
						dialog.dismiss();
					}
				  });
 			Activity activity = (Activity) the_context;
 			activity.runOnUiThread(new Runnable() 
 			{

				@Override
				public void run() {
					AlertDialog alertDialog = alertDialogBuilder.create();
	 				alertDialog.show();						
				}
 			});
	}
	
	/**
	 * @return
	 */
	public static String getAWSUserStatsTableSuffix(String the_id) {
		if (the_id != null && the_id.length() > 0)
		{
			char first = the_id.charAt(0);
			if ( (first >= 'a' && first <= 'z') || (first >= 'A' && first <= 'Z') || (first >= '0' && first <= '9'))
			{
				return String.valueOf(Character.toUpperCase(first));
			}
			else
			{
				return DataSources.ASPLCHAR;
			}
		}
		return null;
	}
	
	public static boolean isGoingerEvent(PublicEvent the_event)
	{
		if (the_event != null && !the_event.isGooglePlace() && the_event.getCreator() != null)
		{
			String creator = the_event.getCreator();
			if (!DataSources.EVENTBRITEPRIMARYLINK.equals(creator) && !DataSources.SEATTLELINKS.equals(creator))
			{
				return true;
			}
		}
		return false;
	}
	
	public static <T> List<T> castList(Class<? extends T> clazz, Collection<?> c) {
	    List<T> r = new ArrayList<T>(c.size());
	    for(Object o: c)
	      r.add(clazz.cast(o));
	    return r;
	}
	
	public static String getDisplayDateTimeFromAWS(String the_str){
		if (the_str != null){
			Calendar cal = getCalendarTSeparated(the_str);
			return (String.valueOf(cal.get(Calendar.MONTH)+ 1)).concat(DATESPLITTERSLASH).concat(
					String.valueOf(cal.get(Calendar.DAY_OF_MONTH))).concat(DATESPLITTERSLASH).concat(
					String.valueOf(cal.get(Calendar.YEAR))).concat(" ").concat(
					String.valueOf(cal.get(Calendar.HOUR_OF_DAY))).concat(TIMESPLITTER).concat(
					String.valueOf(cal.get(Calendar.MINUTE)));
		}
		return null;
	}
	
	public static GCMMessageType getGCMMsgTypeFrmVal(String the_val){
		if (the_val != null){
			GCMMessageType[] msgTypes = GCMMessageType.values();
			for (GCMMessageType msgType: msgTypes){
				if (msgType.name() != null && msgType.name().equalsIgnoreCase(the_val)){
					return msgType;
				}
			}
		}
		return null;
	}
	
	public static String mapMsgTypeToStr(GCMMessageType the_msgType){
		if (the_msgType != null){
			if (the_msgType == GCMMessageType.FriendRequest){
				return "Friend request";
			}
		}
		return null;
	}
	
	public static String getUserName(Context the_context, PrivateEventUser the_user) {
		if (the_context != null && the_user != null && the_user.getName() != null){
			String firstName = the_user.getName().getFirstName();
			firstName = Encryption.decryptTwoWay(the_context, firstName, the_user.getUserName());
			String lastName = the_user.getName().getLastName();
			lastName = Encryption.decryptTwoWay(the_context, lastName, the_user.getUserName());
			return (firstName!=null?firstName:"") + " " + (lastName != null?lastName:"");
		}
		return null;
	}
}
	