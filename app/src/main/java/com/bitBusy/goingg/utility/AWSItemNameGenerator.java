/**
 * 
 */
package com.bitBusy.goingg.utility;

import java.util.Calendar;

import com.bitBusy.goingg.events.Event;
import com.bitBusy.goingg.utility.GeoHashing.GeoHash;
import com.google.android.gms.maps.model.LatLng;

/**
 * @author SumaHarsha
 *
 */
public class AWSItemNameGenerator {

	private static final int MAXLEN = 1024;
	private static final String SPLITKEY = "GSPLITG.";
	/*private static final int CATLOW = 0;
	private static final int CATHIGH = 169;
	private static final int LATLOW = 170;
	private static final int LATHIGH = 339;
	private static final int LNGLOW = 340;
	private static final int LNGHIGH = 509;
	private static final int FROMLOW = 510;
	private static final int FROMHIGH = 679;
	private static final int TOLOW = 680;
	private static final int TOHIGH = 849;
	private static final int NAMELOW = 850;*/
	
	public static String generate(Event the_event)
	{
		if (the_event != null && the_event.getCategory() != null && the_event.getCoordinates() != null && the_event.getEndDateTime() != null && the_event.getStartDateTime() != null
					&& the_event.getName() != null && the_event.getCreator() != null)
		{
			StringBuilder builder = new StringBuilder(MAXLEN);
			addGeohash(the_event.getCoordinates(), builder);
			addSplitKey(builder);
			addUserid(the_event.getCreator(), builder);
			addSplitKey(builder);
			addDateRange(the_event.getStartDateTime(), the_event.getEndDateTime(), builder);
			addSplitKey(builder);
			addName(the_event.getName(), builder);
			String builderStr = builder.toString();
			if (builderStr != null)
			{
				return getValidXMLString(builderStr.length() < 1025?builderStr:builderStr.substring(0, 1024));
			}
		}
		return null;
		
	}

	/**
	 * @param name
	 * @param builder
	 */
	private static void addName(String the_name, StringBuilder builder) {
		if (the_name != null && builder != null)
		{
			builder.append(the_name);
		}
	}

	/**
	 * @param startDateTime
	 * @param endDateTime
	 * @param builder
	 */
	private static void addDateRange(Calendar the_start,
			Calendar the_end, StringBuilder builder) {
		if (the_start != null && the_end != null && builder != null)
		{
			String starts = Utility.getAWSDBDateTime(the_start);
			String ends = Utility.getAWSDBDateTime(the_end);
			if (starts != null && ends != null)
			{
				builder.append(starts.concat(ends));
			}
		}
	}

	/**
	 * @param builder 
	 * @param creator
	 */
	private static void addUserid(String the_creator, StringBuilder builder) {
		if (the_creator != null && builder != null)
		{
			builder.append(the_creator);
		}
	}

	/**
	 * @param builder
	 */
	private static void addSplitKey(StringBuilder builder) {
		if (builder != null)
		{
			builder.append(SPLITKEY);
		}
	}

	/**
	 * @param coordinates
	 * @param builder
	 */
	private static void addGeohash(LatLng the_coordinates, StringBuilder builder) {
		if (the_coordinates != null && builder != null)
		{
			double lat = the_coordinates.latitude;
			double lng = the_coordinates.longitude;
			String geohash = GeoHash.geoHashStringWithCharacterPrecision(lat, lng, GeoHash.DEFAULTCHARPRECISION);
			if (geohash != null)
			{
				builder.append(geohash);
			}
		}
	}

	/**
	 * @param string
	 * @return
	 */
	private static String getValidXMLString(String the_string) {
		        StringBuffer out = new StringBuffer(); // Used to hold the output.
		        char current; // Used to reference the current character.

		        if (the_string == null || ("".equals(the_string))) return ""; // vacancy test.
		        for (int i = 0; i < the_string.length(); i++) {
		            current = the_string.charAt(i); // NOTE: No IndexOutOfBoundsException caught here; it should not happen.
		            if ((current == 0x9) ||
		                (current == 0xA) ||
		                (current == 0xD) ||
		                ((current >= 0x20) && (current <= 0xD7FF)) ||
		                ((current >= 0xE000) && (current <= 0xFFFD)) ||
		                ((current >= 0x10000) && (current <= 0x10FFFF)))
		                out.append(current);
		        }
		        return out.toString();
		    }   	

	/**
	 * @param name
	 * @param arr
	 */
/*	private static void addName(String the_name, StringBuilder the_builder) {
		if (the_builder != null && the_name != null)		{
			if (the_name != null)
			{
				try
				{
					int min = Math.min(the_name.length(), MAXLEN-NAMELOW);
					the_builder.append(the_name,
							0, Math.min(the_name.length(), MAXLEN-NAMELOW));
					if (min < TOHIGH-TOLOW)
					{
						dumpSpaces(the_builder, MAXLEN-NAMELOW-min);
					}
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}
		
	}

	/**
	 * @param the_arr 
	 * @param min
	 * @param length
	 */
	/*private static void dumpSpaces(StringBuilder the_builder, int the_no) {
		if (the_builder != null)
		{
			int count = 0;
			while (count < the_no)
			{
				the_builder.append(" ");
				count++;
				
			}
		}
	}

	/**
	 * @param endDateTime
	 * @param arr
	 */
	/*private static void addTo(Calendar the_end, StringBuilder the_builder) {
		if (the_end != null && the_builder != null)
		{
			String time = Utility.getAWSDBDateTime(the_end);
			if (time != null)
			{
				try
				{
					int min = Math.min(time.length(), TOHIGH-TOLOW);
					the_builder.append(time,
							0, Math.min(time.length(), TOHIGH-TOLOW));
					if (min < TOHIGH-TOLOW)
					{
						dumpSpaces(the_builder, TOHIGH-TOLOW-min);
					}
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}		
	}

	/**
	 * @param startDateTime
	 * @param arr
	 */
	/*private static void addFrom(Calendar the_start, StringBuilder the_builder) {
		if (the_start != null && the_builder != null)
		{
			String time = Utility.getAWSDBDateTime(the_start);
			if (time != null)
			{
					try
					{
						int min = Math.min(time.length(), FROMHIGH-FROMLOW);
						the_builder.append(time,
								0, Math.min(time.length(), FROMHIGH-FROMLOW));
						if (min < FROMHIGH-FROMLOW)
						{
							dumpSpaces(the_builder, FROMHIGH-FROMLOW-min);
						}
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
			}
		}
	}

	/**
	 * @param coordinates
	 * @param arr
	 */
	/*private static void addLat(LatLng the_coordinates, StringBuilder the_builder) {
		if (the_coordinates != null && the_builder != null)
		{
			try
			{
				double lat = the_coordinates.latitude;
				String latV = String.valueOf(lat);
				if (latV != null)
				{
					int min = Math.min(latV.length(), LATHIGH - LATLOW);
					the_builder.append(latV, 0, min);
					if (min < LATHIGH-LATLOW)
					{
						dumpSpaces(the_builder, LATHIGH-LATLOW-min);
					}
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	private static void addLng(LatLng the_coordinates, StringBuilder the_builder)
	{
		if (the_coordinates != null && the_builder != null)
		{
			try
			{
				double lng = the_coordinates.longitude;
				String lngV = String.valueOf(lng);
				if (lngV != null)
				{
					int min = Math.min(lngV.length(), LNGHIGH - LNGLOW);
					the_builder.append(lngV, 0, min);
					if (min < LNGHIGH - LNGLOW)
					{
						dumpSpaces(the_builder, LNGHIGH - LNGLOW-min);
					}
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			}
	}

	/**
	 * @param category
	 * @param the_builder
	 */
/*	private static void addCategory(EventCategory the_category, StringBuilder the_builder) {
		if (the_category != null && the_category.getName() != null && the_category.getName().name() != null && the_builder != null)
		{
			try
			{
				int min = Math.min(the_category.getName().name().length(), CATHIGH-CATLOW);
				the_builder.append(the_category.getName().name(),
						0, Math.min(the_category.getName().name().length(), CATHIGH-CATLOW));
				if (min < CATHIGH-CATLOW)
				{
					dumpSpaces(the_builder, CATHIGH-CATLOW-min);
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}*/
}
