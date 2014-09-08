/**
 * 
 */
package com.bitBusy.goingg.webServices.EventsData;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;

import com.amazonaws.services.simpledb.model.Attribute;
import com.amazonaws.services.simpledb.model.Item;
import com.amazonaws.services.simpledb.model.SelectRequest;
import com.amazonaws.services.simpledb.model.SelectResult;
import com.bitBusy.goingg.database.setup.DBInteractor.EVENTMARKING;
import com.bitBusy.goingg.events.Address;
import com.bitBusy.goingg.events.DataSources;
import com.bitBusy.goingg.events.DataSources.Categories;
import com.bitBusy.goingg.events.EventCategory;
import com.bitBusy.goingg.events.Link;
import com.bitBusy.goingg.events.PublicEvent;
import com.bitBusy.goingg.fromLocation.FromLocation;
import com.bitBusy.goingg.mapDisplay.EventsDisplaySetter;
import com.bitBusy.goingg.settings.QueryFilter;
import com.bitBusy.goingg.utility.Utility;
import com.google.android.gms.maps.model.LatLng;

/**
 * @author SumaHarsha
 *
 */

// ADD MOMRE LINKS AND CHECK DISTANCE AWS
public class DataExtractor implements Runnable{
	
	public static final String LOCATION = "location";
	
	public static final double METERTOMILE = 0.000621371; 
	
	public static final String NULL = "null";
	
	private static final String CATEGORY = "category";
	
	private static final String VENUE = "venue";
	
	private static final String EVENTS = "events";

	private static final String EVENT = "event";
	
	private static final String POSTALCODE = "postal_code";
	
	private static final String LATITUDE = "latitude";

	private static final String LONGITUDE = "longitude";
	
	private static final String URL = "url";
	
	private static final String STARTDATE = "start_date";
	
	private static final String ENDDATE = "end_date";
	
	private static final String DESCRIPTION = "description";
	
	private static final String TITLE = "title";
	
	private static final String PRICE = "price";
	
	private static final String ADDRESS2 = "address_2";
	
	private static final String ADDRESS = "address";
	
	private static final String NAME = "name";
	
	private static final String CITY = "city";
	
	private static final String REGION = "region";
	
	private static final String COUNTRY = "country";
		
	private static final String LOGO = "logo";
	
	private static final String TICKETS = "tickets";
	
	private static final String SUMMARY = "summary";
	
	private static final String TOTALITEMS = "total_items";

	private static final String NUMSHOWING = "num_showing";
	
	private static final String LASTEVENT = "last_event";
	
	private static final String CURRENCY = "currency";	
	
	private static final String TICKET = "ticket";
	
	private static final String RESULTS = "results";
	
	private static final String LAT = "lat";
	
	private static final String LNG = "lng";
		
	private static final String ICON = "icon";
	
	private static final String TYPES = "types";

	private static final String VICINITY = "vicinity";
	
	private static final String GEOMETRY = "geometry";

	private static final String SINCEID = "since_id";

	private static final String GOOGLEPLACESURLSTART = "https://www.google.com/search?q=";
	
	private static final String NEXTPAGETOKEN = "next_page_token";

	private static final String EVENTDESCAGENDA = "event_description_agenda";
	
	private static final String STREETADDRESS = "street_address";
	
	private static final String EVENTINFOURL = "event_info_url";
	
	private static final String RATING = "rating";
	
	private static AtomicInteger myEventbriteCount = new AtomicInteger(0);
	
	private HashSet<PublicEvent> myResults;
	
	private QueryFilter myQueryFilter;
	
	private EventCategory[] myChosenCategories;
	
	private Object myUnreadData;
	
	private HashMap<Categories, ArrayList<String>> myCategoriesMapping;
		
	private JSONObject myUnreadJSONObj;
	
	private JSONArray myUnreadJSONArray;
		
	private String myURI;
	
	private EventsDisplaySetter myEventsSetter;
	
	private QueryPipelineParameters myQueryParameters;

	private SelectResult mySelectResult;
	
	public DataExtractor(QueryResult the_results, QueryFilter the_queryfilter, EventsDisplaySetter the_eventsetter)
	{
		if (the_results != null)
		{
			myUnreadData = the_results.getData();
			myURI = the_results.getURI();
		}
		 myQueryParameters = QueryPipelineParameters.getInstance();
		myQueryFilter = the_queryfilter;
		myResults = new HashSet<PublicEvent>();
		myChosenCategories = myQueryFilter != null?myQueryFilter.getCategories():null;
		myEventsSetter = the_eventsetter;
	}

	
	/* (non-Javadoc)
	 * @see java.util.concurrent.Callable#call()
	 */
	@Override
	public void run()
	{
		if (myUnreadData != null)
		{
			getCategoriesMapping();
			readData();
 		}
		else
		{
			sendToEventsSetter();
		}
	}
	
	/**
	 * 
	 */
	private void getCategoriesMapping() {
		myCategoriesMapping = DataSources.getCategorySubcategoryMapping();
	}

	private void readData()
	{
		convertData();
	   if (myUnreadJSONObj != null || myUnreadJSONArray != null || mySelectResult != null)
	   {
		   ArrayList<PublicEvent> res = getEventObjects();
			if (res != null && res.size() > 0)
			{
			  for(PublicEvent event: res)
				  {				 
				  	if(event != null)
				  	{
				  		try
				  		{
				  			myResults.add(event);	
				  		}
				  		catch(Exception e)
				  		{
				  			e.printStackTrace();
				  		}
				  	}
				  }
			}
	   }
		  sendToEventsSetter();	 
	}
	
	/**
	 * 
	 */
	private void convertData() {
		if (myUnreadData != null && myUnreadData instanceof String)
		{
			String data = (String) myUnreadData;
			Object json;
			try {
				json = new JSONTokener(data).nextValue();
				if (json instanceof JSONObject)
				{
					myUnreadJSONObj = (JSONObject) json;
				}
				else if (json instanceof JSONArray)
				{
					myUnreadJSONArray = (JSONArray) json;
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		else if (myUnreadData != null && myUnreadData instanceof SelectResult)
		{
			mySelectResult = (SelectResult)myUnreadData;
		}
	}

	/**
	 * 
	 */
	private void checkForMoreLinks() 
	{
		if (myURI.startsWith(DataSources.EVENTBRITEPRIMARYLINK))
		{
			checkForEventbriteLinks();
		}
		else if (myURI.startsWith(DataSources.GOOGLEPLACEPRIMARY) || myURI.startsWith(DataSources.GOOGLEPLACEPAGETOKEN))
		{
			checkForMoreGooglePlacesLinks();
		}		
		else if (myURI.startsWith(DataSources.AWSSELECTSTATEMENT))
		{
			checkForMoreAWSLinks();
		}
	}

	/**
	 * 
	 */
	private void checkForMoreAWSLinks() {
		if (mySelectResult != null)
		{
			String nexttoken = mySelectResult.getNextToken();
			if (nexttoken != null)
			{
				SelectRequest selectRequest = new SelectRequest(myURI);
				selectRequest.setConsistentRead(true);
				selectRequest.setNextToken(nexttoken);
				if (selectRequest.getSelectExpression()!=null)
				{
					MoreLinksToQuery.getInstance().addLink(selectRequest.getSelectExpression().concat(AWSPut.TOKEN).concat(selectRequest.getNextToken()));
				}
			}
			
		}
	}

	/**
	 * 
	 */
	private void checkForEventbriteLinks() {
		if (myUnreadJSONObj!=null && !myUnreadJSONObj.isNull(EVENTS))
		{
			try {		
				JSONArray events = myUnreadJSONObj.getJSONArray(EVENTS);
				if (events != null && events.length() > 0)
				{
					JSONObject summary = events.getJSONObject(0)!=null?events.getJSONObject(0).getJSONObject(SUMMARY):null;
						if (summary != null)
						{
							int total = Integer.parseInt(summary.getString(TOTALITEMS));
							int showing = Integer.parseInt(summary.getString(NUMSHOWING));
							myEventbriteCount.addAndGet(showing);
							if (myEventbriteCount.intValue() < total)
							{
								addEventbriteLink(myUnreadJSONObj.getString(LASTEVENT));
							}
						}
				}
						} catch (JSONException e) {
							e.printStackTrace();
						}
				
			catch (NumberFormatException e)
						{
							e.printStackTrace();
						}
		}		
	}

	/**
	 * @param string
	 */
	private void addEventbriteLink(String the_lastID) {
		if (myURI != null && the_lastID != null && the_lastID.length() > 0)
		{
			String[] components = myURI.split(DataSources.AND.concat(SINCEID));
			if (components != null && components.length > 0)
			{
				MoreLinksToQuery.getInstance().addLink(components[0].concat(DataSources.AND).concat(SINCEID).concat(DataSources.EQUALS).concat(the_lastID));
			}
		}
	}

	private void sendToEventsSetter()
	{
		if (myResults != null && myResults.size() > 0 && myEventsSetter != null)
		{
			checkForMoreLinks();
			myEventsSetter.acceptQueryResults(myResults);
		}
		myQueryParameters.incrementProcessedURICount();			
	}
	
	private ArrayList<PublicEvent> getEventObjects() 
	{
		if (myURI!= null) 
		{
			if (myURI.startsWith(DataSources.EVENTBRITEPRIMARYLINK))
			{
				return extractEventbriteResults();
			}
			else if (myURI.startsWith(DataSources.GOOGLEPLACEPRIMARY) || myURI.startsWith(DataSources.GOOGLEPLACEPAGETOKEN))
			{
				return extractGooglePlacesResults();				
			}
			else if (myURI.startsWith(DataSources.AWSSELECTSTATEMENT))
			{
				return extractAWSResults();
			}
			else if (myURI.startsWith(DataSources.SEATTLELINKS))
			{
				return extractSeattleGovResults();				
			}
		}
		return null;		
	}

	
	
	

	/**
	 * @return
	 */
	private ArrayList<PublicEvent> extractAWSResults() {
		if (mySelectResult != null)
		{
			List<Item> items = mySelectResult.getItems();
			return extractAWSItemsData(items);
		}
		return null;
	}

	/**
	 * @param items
	 * @return
	 */
	private ArrayList<PublicEvent> extractAWSItemsData(List<Item> the_items) {
		if (the_items != null)
		{
			ArrayList<PublicEvent> events = new ArrayList<PublicEvent>();
			for (Item item: the_items)
			{
				PublicEvent event = getAWSItemEvent(item);
				if (event != null)
				{
					events.add(event);
				}
			}
			return events;
		}
		return null;
	}

	/**
	 * @param item
	 * @return
	 */
	private PublicEvent getAWSItemEvent(Item the_item) {
		if (the_item != null)
		{
			String name = null,  starts = null, ends = null, desc = null, price = null, infolink = null, lat = null, lng = null,
					street = null, city = null, state = null, country = null, zip = null, creator = null;
			int numGoing = 0, numNope = 0, numMaybe = 0;
			
			List<Attribute> attributes = the_item.getAttributes();
			if (attributes != null)
			{
				for (Attribute attribute: attributes)
				{
					if (attribute!=null && attribute.getName()!=null)
					{
						String attname = attribute.getName();
						String value = attribute.getValue();
	
						try
						{
						     if (attname.equals(DataSources.ADESC))
							{
								desc = value;
							}
							else if (attname.equals(DataSources.AEND))
							{
								ends = value;
							}
							else if (attname.equals(DataSources.ALAT))
							{
								lat = value;
							}
							else if (attname.equals(DataSources.ALINK))
							{
								infolink = value;
							}
							else if (attname.equals(DataSources.ANAME))
							{
								name = value;
							}
							else if (attname.equals(DataSources.ALNG))
							{
								lng = value;
							}
							else if (attname.equals(DataSources.APRICE))
							{
								price = value;
							}
							else if (attname.equals(DataSources.ASTART))
							{
								starts = value;
							}
							else if (attname.equals(DataSources.ASTREETADD))
							{
								street = value;
							}
							else if (attname.equals(DataSources.ACITYADD))
							{
								city = value;
							}
							else if (attname.equals(DataSources.ASTATEADD))
							{
								state = value;
							}
							else if (attname.equals(DataSources.ACOUNTRYADD))
							{
								country = value;
							}
							else if (attname.equals(DataSources.AZIPADD))
							{
								zip = value;
							}
							else if (attname.equals(DataSources.AZIPADD))
							{
								zip = value;
							}
							else if (attname.equals(DataSources.ACREATEDBY))
							{
								creator = value;
							}
							else if (attname.equals(EVENTMARKING.Going.name()))
							{
								numGoing = Integer.parseInt(value);
							}
							else if (attname.equals(EVENTMARKING.Nope.name()))
							{
								numNope = Integer.parseInt(value);
							}
							else if (attname.equals(EVENTMARKING.Maybe.name()))
							{
								numMaybe = Integer.parseInt(value);
							}
						}
						catch(Exception e)
						{
							
						}
					}
				}
				return new PublicEvent(getAWSCategory(), name, desc, 
						new Address(street, city, state, country, zip),getCalendarFromAWSString(starts),getCalendarFromAWSString(ends),
						getAWSLatLng(lat, lng), new Link(infolink), null, price, false, creator, null, numGoing, numNope, numMaybe );
						
			}
		}
		return null;
	}

	private EventCategory getAWSCategory()
	{
		if (myURI != null)
		{
			String[] components = myURI.split(" from ");
			if (components != null && components.length >= 2 && components[1] != null)
			{
				String[] moreComponents = components[1].split(" where ");
				if (moreComponents != null && moreComponents.length > 0 && moreComponents[0] != null)
				{
					return EventCategory.getCategory(moreComponents[0]);
				}
			}
			
		}
		return EventCategory.CATEGORYOTHER;
	}
	/**
	 * @param lat2
	 * @param lng2
	 * @return
	 */
	private LatLng getAWSLatLng(String the_lat, String the_lng) {
		if (the_lat != null && the_lng != null)
		{
			try
			{
				double lat = Double.parseDouble(the_lat);
				double lng = Double.parseDouble(the_lng);
				return new LatLng(lat, lng);				
			}
			catch(Exception e)
			{	
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * @param starts
	 * @return
	 */
	private Calendar getCalendarFromAWSString(String the_datetime) {
		if(the_datetime != null && the_datetime.length() == 13)
		{
			try
			{
				Calendar calendar = Calendar.getInstance();
				calendar.set(Calendar.YEAR, Integer.parseInt(the_datetime.substring(0, 4)));
				calendar.set(Calendar.MONTH, (Integer.parseInt(the_datetime.substring(4, 6)) - 1));
				calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(the_datetime.substring(6, 8)));
				calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(the_datetime.substring(9, 11)));
				calendar.set(Calendar.MINUTE, Integer.parseInt(the_datetime.substring(11, 13)));	
				return calendar;
			}
			catch(Exception e)
			{
				
			}
		}
		return null;
	}

	/**
	 * @return
	 */
	private ArrayList<PublicEvent> extractSeattleGovResults() {
		ArrayList<PublicEvent> extractedEvents = new ArrayList<PublicEvent>();
		if (myUnreadJSONArray != null)
		{
			JSONObject eventElement;
			for (int i = 0; i < myUnreadJSONArray.length(); i++)
			{
				try
				{
					eventElement = myUnreadJSONArray.getJSONObject(i);
					if (eventElement != null)
					{
						LatLng coordinates = getSeattleResultLocation(!eventElement.isNull(LOCATION)?eventElement.getJSONObject(LOCATION):null);
						Calendar starts = Utility.getCalendarTSeparated(!eventElement.isNull(DataSources.STARTTIME)?
								eventElement.getString(DataSources.STARTTIME):null);
						Calendar ends = Utility.getCalendarTSeparated(!eventElement.isNull(DataSources.ENDTIME)?eventElement.getString(DataSources.ENDTIME):null);								
					
						if (checkDistance(coordinates) && checkDates(starts, ends))
						{
							ArrayList<String> category = new ArrayList<String>();
							category.add(myURI);
							EventCategory cat = mapToEventCategory(category);
							if (cat != null)
							{
								PublicEvent eventObj = new PublicEvent(cat,
										!eventElement.isNull(EVENT)?eventElement.getString(EVENT):null,
										!eventElement.isNull(EVENTDESCAGENDA)?eventElement.getString(EVENTDESCAGENDA):null,
										new Address(!eventElement.isNull(STREETADDRESS)?eventElement.getString(STREETADDRESS):null, null, null, null, null),
												starts,	ends, coordinates, 
										new Link(!eventElement.isNull(EVENTINFOURL)?eventElement.getString(EVENTINFOURL):null),
										null, null, false, DataSources.SEATTLELINKS, null,0, 0, 0);
								extractedEvents.add(eventObj);
							}
						}
					}
				}
				catch(JSONException e)
				{
					e.printStackTrace();
				}
			}
		}
		return extractedEvents;
	}



	/**
	 * @param object
	 * @return
	 */
	private LatLng getSeattleResultLocation(JSONObject the_locObj) {
		if (the_locObj != null)
		{
			try
			{
				String lat = the_locObj.getString(LATITUDE);
				String lng = the_locObj.getString(LONGITUDE);
				LatLng coordinates = new LatLng(Double.parseDouble(lat),
													Double.parseDouble(lng));
				return coordinates;
			}
			catch(JSONException e)
			{
				e.printStackTrace();
			}
			catch(NumberFormatException e)
			{
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * 
	 */
	private void checkForMoreGooglePlacesLinks() {
		if (myUnreadJSONObj!=null && !myUnreadJSONObj.isNull(NEXTPAGETOKEN))
		{			
			try {
				String token;
				token = myUnreadJSONObj.getString(NEXTPAGETOKEN);
				String link;
				if (token != null && token != "")
				{
						link = DataSources.GOOGLEPLACEPAGETOKEN.concat(token);
						MoreLinksToQuery.getInstance().addLink(link);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @return
	 */
	private ArrayList<PublicEvent> extractGooglePlacesResults() {
		if (myUnreadJSONObj!=null && !myUnreadJSONObj.isNull(RESULTS))
		{
			JSONArray events = null;
			try {
				events = myUnreadJSONObj.getJSONArray(RESULTS);
			} catch (JSONException e) {
				e.printStackTrace();
			}
				if (events != null)
				{
					ArrayList<PublicEvent> extractedEvents = new ArrayList<PublicEvent>();
					for (int i = 0; i < events.length(); i++)
					{
						JSONObject eventElement;
						try {
							eventElement = events.getJSONObject(i);
						if (eventElement != null)
						{
							LatLng coordinates = getGooglePlaceCoordinates(eventElement);
							if (checkDistance(coordinates))
							{
								EventCategory cat = getGooglePlaceEventCategory(eventElement);
								if (cat != null)
								{
									PublicEvent eventObj = new PublicEvent(cat,
											eventElement.getString(NAME), null, new Address(eventElement.getString(VICINITY),null,null,null,null), null, null,coordinates, 
											getGooglePlaceURL(eventElement), getGooglePlaceIcon(eventElement), null, true, DataSources.GOOGLEPLACEPRIMARY,
											eventElement.getString(RATING), 0, 0, 0);
									extractedEvents.add(eventObj);
								}
								}
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
					return extractedEvents;
				}
		}
		return null;
	}

	
	/**
	 * @param eventElement
	 * @return
	 */
	private Bitmap getGooglePlaceIcon(JSONObject the_eventElement) {
		if (the_eventElement != null && !the_eventElement.isNull(ICON))
		{
			try {
				String imageURL = the_eventElement.getString(ICON);				
				return connectAndGetImage(imageURL);
			} catch (JSONException e) {
				e.printStackTrace();	
			}
		}
		return null;
	}

	/**
	 * @param eventElement
	 * @return
	 */
	private Link getGooglePlaceURL(JSONObject the_eventElement) {
		if (the_eventElement != null)
		{
			try {
				String name = the_eventElement.getString(NAME);
				String address = the_eventElement.getString(VICINITY);
				if (name != null && address != null)
				{
					return new Link(GOOGLEPLACESURLSTART.concat(name).concat(" ").concat(address));
				}
			} catch (JSONException e) {
				e.printStackTrace();
				return null;
			}
		}
		return null;
	}

	/**
	 * @param eventElement
	 * @return
	 */
	private LatLng getGooglePlaceCoordinates(JSONObject the_eventElement) {
		if (the_eventElement != null)
		{
			try {
				JSONObject geometry = the_eventElement.getJSONObject(GEOMETRY);
				if (geometry != null)
				{
					JSONObject location = geometry.getJSONObject(LOCATION);
					if (location != null)
					{
						String lat = location.getString(LAT);
						String lng = location.getString(LNG);
						try
						{
							return new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
						}
						catch (NumberFormatException e)
						{
							e.printStackTrace();
							return null;
						}
					}
				}
				} catch (JSONException e) {
				e.printStackTrace();
			}
			
		}
		return null;
	}

	/**
	 * @param eventElement
	 * @return
	 */
	private EventCategory getGooglePlaceEventCategory(JSONObject the_eventElement) {
		if (the_eventElement != null)
		{
			try {
				JSONArray types = the_eventElement.getJSONArray(TYPES);
				ArrayList<String> categories = new ArrayList<String>();
				if (types != null)
				{
					for (int i = 0; i < types.length(); i++)
					{
						categories.add(types.getString(i));
					}
					return mapToEventCategory(categories);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
		}
		return null;
	}

	/**
	 * @return
	 */
	private ArrayList<PublicEvent> extractEventbriteResults() {
		try {
			if (myUnreadJSONObj!=null && !myUnreadJSONObj.isNull(EVENTS))
			{
			JSONArray events = myUnreadJSONObj.getJSONArray(EVENTS);
			if (events != null && events.length() > 0)
			{
				ArrayList<PublicEvent> extractedEvents = new ArrayList<PublicEvent>();
				PublicEvent eventObj = null;
				for (int i = 1; i < events.length(); i++)
				{
					JSONObject element = events.getJSONObject(i);
					if (element != null)
					{
						JSONObject eventElement = element.getJSONObject(EVENT);
						if (eventElement != null)
						{
							LatLng coordinates = getEventbriteLatLng(eventElement);
							Calendar starts = Utility.getCalendar(eventElement.getString(STARTDATE));
							Calendar ends =	Utility.getCalendar(eventElement.getString(ENDDATE)); 
							if (checkDistance(coordinates) && checkDates(starts, ends))
							{
								ArrayList<String> categoryIDs = getEventbriteCategoryIDs(eventElement);
								EventCategory category = mapToEventCategory(categoryIDs);
								if (category == null)
								{
									category = (myChosenCategories!=null && myChosenCategories.length > 0)? myChosenCategories[0]:EventCategory.CATEGORYOTHER;
								}
								//if (category != null)
								//{
									Address address = getEventbriteVenueAddress(eventElement);
									eventObj = new PublicEvent(category,
											eventElement.getString(TITLE), 
											eventElement.getString(DESCRIPTION), address, starts, ends,									
													coordinates, new Link(eventElement.getString(URL)), 
											getEventbriteImage(eventElement), 
														getEventbriteTicketPriceRange(eventElement), false, DataSources.EVENTBRITEPRIMARYLINK, null,
														0,0,0);
									extractedEvents.add(eventObj);
								//}
							}
							}
					}
				}
				return extractedEvents;
			}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}		return null;
	}

	

	/**
	 * @param starts
	 * @param ends
	 * @return
	 */
	private boolean checkDates(Calendar the_starts, Calendar the_ends) {
		if (the_starts != null && the_ends != null)
		{
			return checkStartingDate(the_starts) && checkEndingDate(the_ends);
		}
		return false;
	}

	/**
	 * @param the_ends
	 * @return
	 */
	private boolean checkEndingDate(Calendar the_ends) {
		if (the_ends != null && myQueryFilter.getEndDateTime()!=null)
		{
			Calendar first = the_ends;
			Calendar second = myQueryFilter.getEndDateTime();
			return ((first.get(Calendar.YEAR) < second.get(Calendar.YEAR)) ||
					((first.get(Calendar.YEAR) == second.get(Calendar.YEAR)) && (first.get(Calendar.MONTH) < second.get(Calendar.MONTH))) ||
					 ((first.get(Calendar.YEAR) == second.get(Calendar.YEAR)) && (first.get(Calendar.MONTH) == second.get(Calendar.MONTH)) && (first.get(Calendar.DAY_OF_MONTH) < second.get(Calendar.DAY_OF_MONTH)))) ||
					 datesEqual(first,second);				
		}
		return false;
	}

	/**
	 * @param the_starts
	 * @return
	 */
	private boolean checkStartingDate(Calendar the_starts) {
		if (the_starts != null && myQueryFilter.getStartDateTime() != null)
		{
			Calendar first = the_starts;
			Calendar second = myQueryFilter.getStartDateTime();
		return (((first.get(Calendar.YEAR) > second.get(Calendar.YEAR)) ||
				((first.get(Calendar.YEAR) == second.get(Calendar.YEAR)) && (first.get(Calendar.MONTH) > second.get(Calendar.MONTH))) ||
				 ((first.get(Calendar.YEAR) == second.get(Calendar.YEAR)) && (first.get(Calendar.MONTH) == second.get(Calendar.MONTH)) && (first.get(Calendar.DAY_OF_MONTH) > second.get(Calendar.DAY_OF_MONTH))))||
				 datesEqual(first, second));
		}
		return false;
	}

	/**
	 * @param first
	 * @param second
	 * @return
	 */
	private boolean datesEqual(Calendar first, Calendar second) {
		if (first != null && second != null)
		{
			return ((first.get(Calendar.YEAR) == second.get(Calendar.YEAR)) && (first.get(Calendar.MONTH) == second.get(Calendar.MONTH)) && (first.get(Calendar.DAY_OF_MONTH) == second.get(Calendar.DAY_OF_MONTH)));				
		}
		return false;
	}

	
	/**
	 * @param eventElement
	 * @return
	 */
	private String getEventbriteTicketPriceRange(JSONObject the_eventElement) {
		if (the_eventElement != null)
		{
			StringBuilder retStr = new StringBuilder();
			try {
				JSONArray tickets = the_eventElement.getJSONArray(TICKETS);
				if (tickets != null)
				{
					for (int i = 0; i < tickets.length(); i++)
					{
						JSONObject ticketElement = tickets.getJSONObject(i);
						if (ticketElement != null)
						{
							JSONObject ticket = ticketElement.getJSONObject(TICKET);
							if (ticket != null && !ticket.isNull(PRICE) && !ticket.isNull(CURRENCY))
							{
									String price = ticket.getString(PRICE);
									String currency = ticket.getString(CURRENCY);
									retStr.append(price!=null?price:"").append(" ").append(currency!=null?currency:" ");
							}
						}					
						
					}
				}
				return retStr.toString();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
		}
		return null;
	}

	/**
	 * @param eventElement
	 * @return
	 */
	private Bitmap getEventbriteImage(JSONObject the_eventElement) {
		if (the_eventElement != null && !the_eventElement.isNull(LOGO))
		{
			try {
				String imageURL = the_eventElement.getString(LOGO);				
				return connectAndGetImage(imageURL);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * @param eventElement
	 * @return
	 */
	private Address getEventbriteVenueAddress(JSONObject the_eventElement) {
		if (the_eventElement != null)
		{
			try {
				JSONObject venue = the_eventElement.getJSONObject(VENUE);
				if (venue != null)
				{
					StringBuilder street = new StringBuilder();
					String street0 = venue.getString(NAME);
					String street1 = venue.getString(ADDRESS2);
					String street2 = venue.getString(ADDRESS);
					street.append(street0!=null?street0:"").append(street1!=null?street1:"").append(street2!=null?street2:"");
					String city = venue.getString(CITY);
					String state = venue.getString(REGION);
					String country = venue.getString(COUNTRY);
					String zipcode = venue.getString(POSTALCODE);
					return new Address(street.toString(), city, state, country, zipcode);
				}
			
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * @param eventElement
	 * @return
	 */
	private LatLng getEventbriteLatLng(JSONObject the_eventElement) {
		if (the_eventElement != null)
		{
			try {
				JSONObject venue = the_eventElement.getJSONObject(VENUE);
				if (venue != null)
				{
					try
					{
					Double latitude = Double.parseDouble(venue.getString(LATITUDE));
					Double longitude = Double.parseDouble(venue.getString(LONGITUDE));
					return new LatLng(latitude, longitude);
					}
					catch (NumberFormatException e)
					{
						e.printStackTrace();
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
		}
		return null;
	}

	/**
	 * @param categoryIDs
	 * @return
	 */
	private EventCategory mapToEventCategory(ArrayList<String> the_categoryIDs) {
		if (myCategoriesMapping != null && myChosenCategories != null && the_categoryIDs != null)
		{
			for (String categoryid: the_categoryIDs)
			{
				if (categoryid != null)
				{
					for (EventCategory chosen: myChosenCategories)
					{
						if (chosen != null)
						{
							ArrayList<String> list = myCategoriesMapping.get(chosen.getName());
							if (list != null)
							{
								for (String id: list)
								{
									if (categoryid.equals(id))
									{
										return chosen;
									}
								}
							}
						}
					}
					
				}
			}
		}
		return null;
	}

	/**
	 * @param eventElement
	 * @return
	 */
	private ArrayList<String> getEventbriteCategoryIDs(JSONObject the_eventElement) {
		if (the_eventElement != null)
		{
			String categoryElement;
			try {
				categoryElement = the_eventElement.getString(CATEGORY);
				if (categoryElement != null && categoryElement != "")
				{
					String[] components = categoryElement.split(DataSources.COMMA);
					if (components != null)
					{
						ArrayList<String> categories = new ArrayList<String>();
						for (String category: components)
						{
							categories.add(category);
						}
						return categories;					
					}					
				}
				} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		return null;
	}

		


	

	/**
	 * @param imageURL 
	 */
	private Bitmap connectAndGetImage(String the_imageURL) {
		if (the_imageURL != null)
		{
			URL url;
			try {
				url = new URL(the_imageURL);
				HttpURLConnection connection  = (HttpURLConnection) url.openConnection();
				InputStream is;
				is = connection.getInputStream();
				Bitmap img = BitmapFactory.decodeStream(is);  
				return img;
				} 
		 catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		}
		return null;		
	}

		

	/** checks if event is within a particular distance
	 * @return boolean
	 */
	private boolean checkDistance(LatLng the_coordinates)
	{
		if (the_coordinates != null)
		{
			double eventLatitude = the_coordinates.latitude;
			double eventLongitude = the_coordinates.longitude;
			LatLng fromLoc = FromLocation.getInstance().getFromLoc();
			double fromLatitude = fromLoc.latitude;
			double fromLongitude = fromLoc.longitude;
			if (computeDistance(eventLatitude, eventLongitude, fromLatitude, fromLongitude) <= myQueryFilter.getDistance())
			{
				return true;
			}
		}
		return false;
	}
	
	private double computeDistance(double the_eventLatitude,
			double the_eventLongitude, double the_fromLatitude, double the_fromLongitude) {

		float[] res = new float[3];
		try
		{
		Location.distanceBetween(the_eventLatitude, the_eventLongitude, the_fromLatitude, the_fromLongitude,res );
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return res[0] * METERTOMILE;
	}
	
}
