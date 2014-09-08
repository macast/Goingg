/**
 * 
 */
package com.bitBusy.goingg.webServices.EventsData;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import android.content.Context;
import android.widget.ProgressBar;

import com.bitBusy.goingg.activity.ActivityMapViewHome;
import com.bitBusy.goingg.events.DataSources;
import com.bitBusy.goingg.events.EventCategory;
import com.bitBusy.goingg.fromLocation.FromLocation;
import com.bitBusy.goingg.mapDisplay.EventsDisplaySetter;
import com.bitBusy.goingg.settings.QueryFilter;
import com.bitBusy.goingg.utility.Utility;
import com.bitBusy.goingg.utility.GeoHashing.GeoHash;
import com.bitBusy.goingg.R;
import com.google.android.gms.maps.model.LatLng;

/**
 * @author SumaHarsha
 *
 */
public class Querier implements Runnable {
//extends AsyncTask<QueryFilter, Void, Boolean>{


	
	public static final int DIALOG_DOWNLOAD_PROGRESS = 0;
	
	private static final String RADIUS = "radius";
	
	private static final String GEOHASHAPPEND =  DataSources.AITEMNAME.concat(" like ").concat("'"); 
	
	public static final String PIPE = "|";
		
	/** query filter*/
	private QueryFilter myQueryFilter;
	
	private QueryPipelineParameters myQueryParameters;
	
	//private LinkedList<ProcessCategoryLink> myURISQ;
		
	/** requestors requesting query*/
	//private List<QueryRequestor> myRequestors;
	
	//private ArrayList<Future<String>> myQueriedData;
	
//	private ConcurrentHashSet<Event> myEvents;
	


	/** context*/
	private Context myContext;
	
	private ConcurrentLinkedQueue<String> myQueries;	
	
	private EventsDisplaySetter myEventsSetter;
	
	private ProgressBar myProgressBar;

	/**
	 * constructor parameterized
	 * @param the_context
	 */
	public Querier(EventsDisplaySetter the_setter, Context the_context, QueryFilter the_queryFilter)	
	{
		myContext = the_context;
		myQueryFilter = the_queryFilter;
		 myProgressBar = myContext!=null?(ProgressBar)((ActivityMapViewHome)myContext).findViewById(R.id.home_progressbar):null;
		 myEventsSetter = the_setter;
		 myQueryParameters = QueryPipelineParameters.getInstance();
		 myQueryParameters.setContext(myContext);
		 myQueryParameters.setProgressBar(myProgressBar);
	}
	
	
	
	public void run()
	{
		if (myQueryFilter != null)
		{
			//setProgressBarVisibility(View.VISIBLE);
			buildQueries();
			processQueries();
		}
	}
	
	/**
	 * @param buildQueries
	 */
	private void processQueries() {
		if (myQueries != null)
		{
		    String processingURI;
			while (!myQueries.isEmpty())
			{
				processingURI = myQueries.remove();
				if (processingURI != null)
				{
					myQueryParameters.getExecutorService().submit(new QuerierWorker(myContext, myEventsSetter, myQueryFilter, processingURI));
					myQueryParameters.incrementSubmittedURICount();
				}
			}
		}
	}


	private void buildQueries()
	{
		myQueries = new ConcurrentLinkedQueue<String>();
		EventCategory[] selectedCategories = myQueryFilter.getCategories();
		ArrayList<HashMap<Object, ArrayList<String>>> sourcesMap = DataSources.getCategorySourceCategoryMaps(myQueryFilter.getDistance());
		if (selectedCategories != null && sourcesMap != null)
		{
			for(HashMap<Object, ArrayList<String>> sourceCategoriesMap: sourcesMap)
			{
				if(sourceCategoriesMap != null)
				{
					ArrayList<String> primary = sourceCategoriesMap.get(DataSources.SOURCE);
					if (primary != null)
					{
						String source = primary.get(0);
						if (source != null)
						{
							if(source.equals(DataSources.EVENTBRITEPRIMARYLINK) || source.equals(DataSources.GOOGLEPLACEPRIMARY))
							{
								addEventbriteGooglePlaceLinks(selectedCategories, sourceCategoriesMap);
							}
							else if (source.equals(DataSources.SEATTLELINKS))
									//|| source.equals(DataSources.CITYOFMADISON))
							{
								addSeattleMadisonLinks(selectedCategories, sourceCategoriesMap);
							}
						}
					}
				}
			}
			buildAWSQuery();
		}
		}
		
	

	
/**
	 * 
	 */
	private void buildAWSQuery() {
	 if (myQueryFilter.getCategories()!=null && myQueryFilter.getCategories() != null && myQueryFilter.getStartDateTime()!=null && myQueryFilter.getEndDateTime()!=null)
	 {
		 EventCategory[] categories = myQueryFilter.getCategories();
		 if (categories != null)
		 {
			 for (EventCategory category: categories)
			 {
				 if (category != null && category.getName() != null && category.getName().name() != null)
				 {
					 addLinkToQueue(DataSources.AWSSELECTSTATEMENT.concat(category.getName().name()).concat(getGeoHashesToCheck()).concat(getDateRange()));
				 }
			 }
		 }
		 }
	}
/**
 * @return
 */
private String getDateRange() {
	if (myQueryFilter.getStartDateTime() != null && myQueryFilter.getEndDateTime() != null)
	{
		String starts = Utility.getAWSDBDateTime(myQueryFilter.getStartDateTime());
		String ends = Utility.getAWSDBDateTime(myQueryFilter.getEndDateTime());
		if (starts != null && ends != null)
		{
			StringBuilder builder = new StringBuilder();
			builder.append(" and ").append(" (").append(DataSources.ASTART).
			append(" >= '").append(starts).append("' and ").append(DataSources.AEND).append(" ").append(" <= '").append(ends).append("')");
			return builder.toString();
		}
	}
	return null;
}


/**
 * @param neighbors
 * @return
 */
private String getGeoHashesToCheck() {
	LatLng current = FromLocation.getInstance().getFromLoc();
	if (current != null)
	{
		 GeoHash currentGeoHash = GeoHash.withCharacterPrecision(current.latitude, current.longitude, 4);
		 GeoHash[] neighbors = currentGeoHash.getAdjacent();
	
		if (neighbors != null)
		{
			StringBuilder builder = new StringBuilder();
			builder.append(" where (");
			appendGeoHash(builder, currentGeoHash);
			for (int i = 0; i < neighbors.length; i++)
			{
				if (neighbors[i] != null)
				{
					builder.append(" or ");
					appendGeoHash(builder, neighbors[i]);
				}
			}
			builder.append(")");
			return builder.toString();
		}
	}
	return null;
}



/**
 * @return
 */




/**
 * @param builder
 * @param currentGeoHash
 */
private void appendGeoHash(StringBuilder the_builder, GeoHash the_geoHash) {
	if (the_builder != null && the_geoHash != null)
	{
		the_builder.append(GEOHASHAPPEND);
		the_builder.append(the_geoHash.toBase32());
		the_builder.append("%'");	
	}
}



/**
	 * @param selectedCategories
	 * @param sourceCategoriesMap
	 */
	private void addSeattleMadisonLinks(EventCategory[] selectedCategories,
			HashMap<Object, ArrayList<String>> sourceCategoriesMap) {
		if (selectedCategories != null && sourceCategoriesMap != null)
		{
			for (EventCategory category: selectedCategories)
			{
				DataSources.Categories eventCategory = category!=null?category.getName():null;
				
					if (eventCategory != null)
					{
						ArrayList<String> sourceCategories = sourceCategoriesMap.get(eventCategory);
						if (sourceCategories != null)
						{
							for (String sourceCat: sourceCategories)
							{
									addLinkToQueue(sourceCat);
							}
						}
					}
			}
		}
	}



/**
 * @param selectedCategories 
 * @param sourceCategoriesMap 
	 * 
	 */
	private void addEventbriteGooglePlaceLinks(EventCategory[] selectedCategories, HashMap<Object, ArrayList<String>> sourceCategoriesMap) {
		if (selectedCategories != null && sourceCategoriesMap != null)
		{
			ArrayList<String> primary = sourceCategoriesMap.get(DataSources.SOURCE);
			String link = null;
			for (EventCategory category: selectedCategories)
			{
				DataSources.Categories eventCategory = category!=null?category.getName():null;
				
						if (eventCategory != null)
						{
							ArrayList<String> sourceCategories = sourceCategoriesMap.get(eventCategory);
							if (sourceCategories != null)
							{
								for (String sourceCat: sourceCategories)
								{
									if ( link != null)
									{
										if (link.startsWith(DataSources.EVENTBRITEPRIMARYLINK))
										{
											link = link.concat(DataSources.COMMA).concat(sourceCat);
										}
										else if(link.startsWith(DataSources.GOOGLEPLACEPRIMARY))
										{
											try {
												link = link.concat(URLEncoder.encode(PIPE, "UTF-8")).concat(sourceCat);
											} catch (UnsupportedEncodingException e) {
												e.printStackTrace();
											}
										}
									}
									else
									{
										link = primary!=null && primary.get(0)!=null?primary.get(0).concat(sourceCat):null;
									}
								}
							}
						}
					}
				addLinkToQueue(link);
		}
	}
		
	/**
 * @param link
 */
	private void addLinkToQueue(String the_link) {
		if (the_link != null && the_link.length() > 0)
		{
			try
			{
				the_link = appendDateLocation(the_link);
				myQueries.add(the_link);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}	
		}
	}



	/*	private void setProgressBarVisibility(final int the_value)
	{
		if (myContext != null)
		{
			((ActivityMapViewHome) myContext).runOnUiThread( new Runnable()
			{
				public void run()
				{
					myProgressBar.setVisibility(the_value);
				}
			});
		}
	}*/
	private String appendDateLocation(String the_uri)
	{
		if (the_uri != null) 
		{
			LatLng current = FromLocation.getInstance().getFromLoc();
			if (the_uri.startsWith(DataSources.EVENTBRITEPRIMARYLINK))
			{
				if (current != null)
				{
					the_uri = the_uri.concat(DataSources.AND).concat(DataSources.LATITUDE).concat(DataSources.EQUALS).
						concat(String.valueOf(current.latitude)).concat(DataSources.AND).concat(DataSources.LONGITUDE).concat(DataSources.EQUALS).
						concat(String.valueOf(current.longitude)).
						concat(DataSources.AND).concat(DataSources.WITHIN).concat(DataSources.EQUALS).concat(String.valueOf(myQueryFilter.getDistance()));
				}
				String starts = getDateTimeEventbriteSeattleMadison(myQueryFilter.getStartDateTime());
				String ends = getDateTimeEventbriteSeattleMadison(myQueryFilter.getEndDateTime());
				if (starts != null && ends != null)
					{
						try {
							the_uri = the_uri.concat(DataSources.AND).concat(DataSources.DATE).concat(DataSources.EQUALS).concat(starts).concat
									(URLEncoder.encode(Utility.SPACE, "UTF-8")).concat(ends);
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}
					}			
			}
			else if (the_uri.startsWith(DataSources.GOOGLEPLACEPRIMARY))
			{
				if (current != null)
				{
					the_uri = the_uri.concat(DataSources.AND).concat(DataExtractor.LOCATION).concat(DataSources.EQUALS).
						concat(String.valueOf(current.latitude)).concat(DataSources.COMMA).concat(String.valueOf(current.longitude)).
						concat(DataSources.AND).concat(RADIUS).concat(DataSources.EQUALS).concat(String.valueOf(myQueryFilter.getDistance()/DataExtractor.METERTOMILE));
				}
			}
			else if (the_uri.startsWith(DataSources.SEATTLELINKS))
				//|| the_uri.startsWith(DataSources.CITYOFMADISON))
			{
				String starts =	getDateTimeEventbriteSeattleMadison(myQueryFilter.getStartDateTime());
				String ends = getDateTimeEventbriteSeattleMadison(myQueryFilter.getEndDateTime());
				if (starts != null && ends != null)
				{
					try {
						the_uri = the_uri.concat(DataSources.QUESTIONMARK).concat(DataSources.DOLLAR).concat(DataSources.WHERE).concat(DataSources.EQUALS).
								concat(DataSources.STARTTIME).concat(URLEncoder.encode(DataSources.GREATER, "UTF-8")).concat(DataSources.EQUALS).concat(DataSources.APOSTROPHE).
								concat(starts).concat(DataSources.APOSTROPHE).concat(URLEncoder.encode(" AND ", "UTF-8")).concat(DataSources.ENDTIME).concat(URLEncoder.encode(DataSources.LESSER, "UTF-8")).concat(DataSources.EQUALS).
								concat(DataSources.APOSTROPHE).concat(ends).concat(DataSources.APOSTROPHE);
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				}
			}
		}
	return the_uri;
	}
	



	/**
	 * @param startDateTime
	 * @return
	 */
	private String getDateTimeEventbriteSeattleMadison(Calendar the_calendar) {
		if (the_calendar != null)
		{
			StringBuilder builder = new StringBuilder(); 
			builder.append(the_calendar.get(Calendar.YEAR)).append(Utility.DATESPLITTER)
			.append(String.format(Utility.ZEROPADDING, the_calendar.get(Calendar.MONTH) + 1)).append(Utility.DATESPLITTER)
			.append(String.format(Utility.ZEROPADDING, the_calendar.get(Calendar.DAY_OF_MONTH)));
			return builder.toString();			
		}
		return null;
	}


	
	
}