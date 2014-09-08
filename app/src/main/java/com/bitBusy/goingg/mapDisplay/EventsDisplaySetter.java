/**S
 * 
 */
package com.bitBusy.goingg.mapDisplay;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.concurrent.ArrayBlockingQueue;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bitBusy.goingg.activity.ActivityMapViewHome;
import com.bitBusy.goingg.adapters.EventWithDistance;
import com.bitBusy.goingg.adapters.ListAdapter;
import com.bitBusy.goingg.dialog.EventInformationDialog;
import com.bitBusy.goingg.dialog.EventQuerySearchDialog;
import com.bitBusy.goingg.dialog.SearchSettingsDialog;
import com.bitBusy.goingg.events.PublicEvent;
import com.bitBusy.goingg.fromLocation.FromLocation;
import com.bitBusy.goingg.settings.QueryFilter;
import com.bitBusy.goingg.settings.SearchSetting;
import com.bitBusy.goingg.utility.Utility;
import com.bitBusy.goingg.webServices.EventsData.DataExtractor;
import com.bitBusy.goingg.webServices.EventsData.MoreLinksToQuery;
import com.bitBusy.goingg.webServices.EventsData.Querier;
import com.bitBusy.goingg.webServices.EventsData.QuerierWorker;
import com.bitBusy.goingg.webServices.EventsData.QueryPipelineParameters;
import com.bitBusy.goingg.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;



/**
 * @author SumaHarsha
 *
 */
public class EventsDisplaySetter{
	
	
	private static final String NOEVENTSFOUND = "Sorry, no events found for your search settings!";
	
	private static final int RESULTSQUEUESIZE = 1000;
	
	private static final String NEAREST = "nearest";

	private static final String SOONEST = "soonest starting";
	
	private static final String CATEGORYAZ = "category (a-z)";

	private static final String CATEGORYZA = "category (z-a)";

	private static final String[] SPINNERCHOICES = new String[]{NEAREST, SOONEST, CATEGORYAZ, CATEGORYZA};
		
	private DisplaySetterStates myState = DisplaySetterStates.IDLE;
	
	private HashMap<LatLng, MarkerAtLocation> myLocationMarkers;
	
	private LinkedList<EventWithDistance> myDistanceSortedList;
	
	private LinkedList<EventWithDistance> myStartTimeSortedList;
	
	private LinkedList<EventWithDistance> myCategoryAZSortedList;

	private LinkedList<EventWithDistance> myCategoryZASortedList;
	
	private HashSet<EventWithDistance> myMasterForListView;
	
	private MoreLinksToQuery myMoreLinksToQuery;
	
	private Marker myPreviousClickedMarker;
	
	private int mySpinnerSelection = 0;
	
	private Marker myClickedMarker;
	
	/** Google map*/
	private GoogleMap myMap;
	
	/** Query filter*/
	private QueryFilter myQueryFilter;
	
	/** Context*/
	private Context myContext;
	
	private Spinner mySpinner;
	
	/** list of events*/
	private HashSet<PublicEvent> myResultEvents;
	
	private ArrayList<EventWithDistance> myResultsEventsForListView;

	
	private ArrayBlockingQueue<PublicEvent> myResultsQueue;
	
	private SearchSettingsDialog.DISPLAYOPTIONS myDisplayState = SearchSettingsDialog.DISPLAYOPTIONS.BOTH;
	
	private ListView myListView;
	
	/** map of event name and event*/
	private HashMap<String, PublicEvent> myEventNameEventMap;
	
	private boolean isMapView;
	
	private TextView myEventsNum;
	
	private ListAdapter myListAdapter;
	
	private ImageButton myShowMoreButton;
			
	/**
	 * Parameterized constructor
	 * @param the_map
	 * @param the_filter for querying
	 */
	public EventsDisplaySetter(Context the_context, GoogleMap the_map, ListView the_list, TextView the_eventsNum,
			boolean the_isMapView, SearchSetting the_setting)// LatLng the_currentLoc)
	{
		myMap = the_map;
		myContext = the_context;
		isMapView = the_isMapView;
		myListView = the_list;
		myResultEvents = new HashSet<PublicEvent>();
		myResultsQueue = new ArrayBlockingQueue<PublicEvent>(RESULTSQUEUESIZE);
		myEventNameEventMap = new HashMap<String, PublicEvent>();
		myMoreLinksToQuery = MoreLinksToQuery.getInstance();
		myEventsNum = the_eventsNum;
		myResultsEventsForListView = new ArrayList<EventWithDistance>();
		myMasterForListView = new HashSet<EventWithDistance>();
		myLocationMarkers = new HashMap<LatLng, MarkerAtLocation>();
		if (the_setting != null)
		{
			myQueryFilter = the_setting.getQueryFilter();
			myDisplayState = the_setting.getDisplayState();

		}
	   	setListeners();
	   	setSpinnerAdapter();
		myShowMoreButton =  (ImageButton)((ActivityMapViewHome)myContext).findViewById(R.id.home_showmoreButton);
	}
	
	
	public void showMore()
	{
		QueryPipelineParameters params = QueryPipelineParameters.getInstance();
		params.getExecutorService().submit(new QuerierWorker(myContext, this, myQueryFilter, myMoreLinksToQuery.removeFirstLink()));
		params.incrementSubmittedURICount();
    	checkShowMoreButton();
	}
	
	
	private void setSpinnerAdapter() {
		mySpinner = (Spinner) ((Activity) myContext).findViewById(R.id.activity_home_sortspinner);
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(myContext, R.layout.activity_home_spinner_layout,SPINNERCHOICES);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mySpinner.setAdapter(dataAdapter);
	}

	private void displayWorker()
	{
		if (isMapView)
		{
			if (myState.equals(DisplaySetterStates.IDLE) || 
					myState.equals(DisplaySetterStates.HASSETMAPIDLE))
			{
				addQueuedEventsToMap();
			}
			else if(myState.equals(DisplaySetterStates.VIEWTOGGLED) || myState.equals(DisplaySetterStates.RESET))
			{
				setMarkers();
			}			
		}
		else	
		{
			if(myState.equals(DisplaySetterStates.IDLE)|| myState.equals(DisplaySetterStates.HASSETLISTIDLE))
			{
				addQueuedEventsToList();
			}
			else if (myState.equals(DisplaySetterStates.VIEWTOGGLED) || myState.equals(DisplaySetterStates.RESET))
			{
				queueAndList();
			}
		}
	}
	
	

	/**
	 * 
	 */
	private void queueAndList() {
		addToResultQueue();
		addQueuedEventsToList();	
	}





	/**
	 * 
	 */
	private void addQueuedEventsToList() 
	{
		new ListSetter().execute();
	}


	private void setListeners() {
		if (myMap != null)
		{
			myMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
				
				@Override
				public boolean onMarkerClick(Marker the_marker) {
					if (the_marker != null && the_marker != myPreviousClickedMarker)
					{
						myPreviousClickedMarker = the_marker;
						PublicEvent event = myEventNameEventMap != null?myEventNameEventMap.get(the_marker.getTitle()):null;
						if (event != null)
						{
							MarkerAtLocation markersHere = myLocationMarkers.get(event.getCoordinates());
							if (markersHere != null)
							{
								int total = markersHere.getTotalMarkers();
								if (total > 1)
								{
									int clicks = markersHere.getClicks();
									toast(total, clicks);
									int updateClicks =(clicks+1)%(total+1);
									clicks = updateClicks==0?1:updateClicks;
									markersHere.setClicks(clicks);
								}
							}
						}
					}
					return false;
				}
			});
			myMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener()
			{
				@Override
				public void onInfoWindowClick(Marker the_marker) {
					PublicEvent event = myEventNameEventMap != null?myEventNameEventMap.get(the_marker.getTitle()):null;
					if (event != null)
					{
						if(event.isGooglePlace())
						{
							myClickedMarker = null;
							new EventQuerySearchDialog(myContext, event).openDetailsDialog();
						}
						else
						{
							myClickedMarker = the_marker;
							new EventInformationDialog(myContext, event, EventsDisplaySetter.this).openDetailsDialog();
						}
					}
				
				}		
			});	
		}	
	}


	/**
	 * @param total
	 * @param clicks
	 */
	private void toast(int the_total, int the_clicks) 
	{
		Toast toast = Toast.makeText(myContext.getApplicationContext(),  "( "+String.valueOf(the_clicks) + " of " + 
				String.valueOf(the_total)+" )" + " \n" +	String.valueOf(the_total) + " events here. Click again for next.", Toast.LENGTH_LONG);
		LinearLayout layout = (LinearLayout) toast.getView();
		if (layout.getChildCount() > 0) {
		  TextView tv = (TextView) layout.getChildAt(0);
		  tv.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
		}
		toast.show();
		
	}


	private void addQueuedEventsToMap()
	{
		new MarkerSetter().execute();
	}
	
		public void display()
		{
			if (myResultEvents.isEmpty())
			{
				query();
			}	
	}
		
		private void setMarkers() 
		{
			myLocationMarkers.clear();
			addToResultQueue();
			addQueuedEventsToMap();
		}
		
		private void addToResultQueue()
		{
			for (PublicEvent event: myResultEvents)
			{
				addEventToResultsQueue(event);
			}
		}
	/** 
	 * call the Querier
	 */
	
	private void query()
	{
		Querier querier = new Querier(this, myContext, myQueryFilter);
		Thread queryThread = new Thread(querier);
		 queryThread.start();
	}
	
	public synchronized void acceptQueryResults(final HashSet<PublicEvent> the_resultEvents) {
            	if (the_resultEvents != null)
            	{
            		for (PublicEvent event: the_resultEvents)
            		{
            			addEventToResultsQueue(event);
            			myResultEvents.add(event);
            		}
            	}
            	checkShowMoreButton();
        		displayWorker();
	}
	

	/**
	 * @param event
	 */
	private void addEventToResultsQueue(PublicEvent the_event) {
		if (the_event != null)
		{
			try 
			{
				if (myDisplayState == SearchSettingsDialog.DISPLAYOPTIONS.BOTH)
				{
					myResultsQueue.put(the_event);
				}
				else if (myDisplayState == SearchSettingsDialog.DISPLAYOPTIONS.EVENTS && !the_event.isGooglePlace())
				{
					myResultsQueue.put(the_event);
				}
				else if (myDisplayState == SearchSettingsDialog.DISPLAYOPTIONS.PLACES && the_event.isGooglePlace())
				{
					myResultsQueue.put(the_event);
				}
			}
		 catch (InterruptedException e) {
			e.printStackTrace();
		}
		}
	}


	private void checkShowMoreButton()
	{
		if (myShowMoreButton != null)
		{
			if (myMoreLinksToQuery.getLinksQueueSize() > 0 && myShowMoreButton.getVisibility() != View.VISIBLE)
			{
				setShowMoreButtonVisibility(View.VISIBLE);
			}
			else if (myMoreLinksToQuery.getLinksQueueSize() == 0 && myShowMoreButton.getVisibility() != View.GONE)
			{
				setShowMoreButtonVisibility(View.GONE);
			}
		}
	}
	/**
	 * @param visible
	 */
	private void setShowMoreButtonVisibility(final int the_visibility) {
		((ActivityMapViewHome) myContext).runOnUiThread( new Runnable()
		{
			public void run()
			{
				myShowMoreButton.setVisibility(the_visibility);
			}
		});		
	}

	/**
	 * @param array
	 * @return
	 */
	private EventWithDistance getEventsWithDistance(
			PublicEvent the_event) {
		if (the_event != null)
		{
			EventWithDistance returnEvent = null;
				float[] res = new float[3];
				if (the_event != null && the_event.getCategory() != null && FromLocation.getInstance().getFromLoc() != null)
				{
				try
				{
					 Location.distanceBetween(the_event.getCoordinates().latitude, 
								the_event.getCoordinates().longitude, FromLocation.getInstance().getFromLoc().latitude,
								FromLocation.getInstance().getFromLoc().longitude,res );
					double dist = Math.round((res[0] * DataExtractor.METERTOMILE) * 100.0) / 100.0;
					returnEvent = new EventWithDistance(the_event, dist);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}

				}
				return returnEvent;			
			}
		return null;
	}


	private void setList()
	{
		if (myListView != null)
		{ 
			if (myListAdapter == null)
			{
				myListAdapter = new ListAdapter(myContext,myResultsEventsForListView, this);
			}
			if (myListView.getAdapter() == null)
			{
				myListView.setAdapter(myListAdapter);
			}
			else
			{
				myListAdapter.notifyDataSetChanged();
			}
		}
		
	}
	
	public void setQueryFilter(SearchSetting the_setting, boolean hasLocationChanged)
	{
		if (the_setting != null)
		{
			QueryFilter the_filter = the_setting.getQueryFilter();
			SearchSettingsDialog.DISPLAYOPTIONS the_state = the_setting.getDisplayState();
	
			if (hasLocationChanged)
			{
				myQueryFilter = the_filter;
				myDisplayState = the_state;
				myResultEvents.clear();
				clearMapAndList();
				display();
			}
			else
			{
				if (the_filter != null)
				{
					if (!the_filter.equals(myQueryFilter))
					{
						myQueryFilter = the_filter;
						myDisplayState = the_state;
						myResultEvents.clear();
						clearMapAndList();
						display();
					}
					else if (the_state != myDisplayState)
					{
						myDisplayState = the_state;
						resetView();
						displayWorker();
					}
				}
			}
		}
	}
	
	/**
	 * 
	 */
	private void resetView() {
		myState = DisplaySetterStates.RESET;
		basicClear();
	}


	private void clearMapAndList()
	{
		myMoreLinksToQuery.clearQueue();
		checkShowMoreButton();	
		basicClear();
		myState = DisplaySetterStates.IDLE;
	}
	
	private void basicClear()
	{
		basicListClear();
		basicMapClear();
		myResultsQueue.clear();
		updateEventCount(0);
	
	}
	

	/**
	 * @param i
	 */
	private void updateEventCount(final int the_count) {
		((ActivityMapViewHome) myContext).runOnUiThread( new Runnable()
		{
			public void run()
			{
				if (myEventsNum != null)
				{
					myEventsNum.setText(String.valueOf(the_count));
				}
			}
		});
	}


	/**
	 * 
	 */
	private void basicMapClear() {
		myEventNameEventMap.clear();
		myMap.clear();
		myLocationMarkers.clear();
	}


	/**
	 * 
	 */
	private void basicListClear() {
		myResultsEventsForListView.clear();
		myMasterForListView.clear();
		clearAllSortedLists();
		if (myListAdapter != null)
		{
			myListAdapter.notifyDataSetChanged();
			myListAdapter = null;
		}
	}


	private void clearAllSortedLists()
	{
		myDistanceSortedList = null;
		myStartTimeSortedList = null;
		myCategoryAZSortedList = null;
		myCategoryZASortedList = null;
	}


	public void setIsMapView(boolean the_flag)
	{
		if (isMapView != the_flag)
		{
			isMapView = the_flag;
			myMap.clear();
			myLocationMarkers.clear();
			myResultsEventsForListView.clear();
			myMasterForListView.clear();
			if (myState != DisplaySetterStates.IDLE)
			{
				myState = DisplaySetterStates.VIEWTOGGLED;
				updateEventCount(0);
			}
			displayWorker();
			myClickedMarker = null;
		}
	}
	
	
	private void updateEventCount()
{
				int count = 0;
				if (isMapView && myEventNameEventMap != null)
				{
					count = myEventNameEventMap.size();
				}
				else if (!isMapView && myResultsEventsForListView != null)
				{
					count = myResultsEventsForListView.size();
				}
			updateEventCount(count);
	}
	private void setSpinnerListener()
	{
	mySpinner.setOnItemSelectedListener(new OnItemSelectedListener()
	{

		@Override
		public void onItemSelected(AdapterView<?> the_parent, View the_view,
				int the_pos, long the_id) {
			if (the_parent != null)
			{
				mySpinnerSelection = the_pos;
				String choice = the_parent.getItemAtPosition(the_pos).toString();
				if (choice != null)
				{
					sortedDisplay();
				}
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub
					
				}
				
			});
		}
		
		/**
	 * @param choice
	 */
	private void sortedDisplay() {
		String the_choice = SPINNERCHOICES[mySpinnerSelection];
		if (the_choice != null)
		{
			if (NEAREST.equals(the_choice))
			{
				if (myDistanceSortedList == null)
				{
					sortEventsByDistance();
				}
			}
			else if (SOONEST.equals(the_choice))
			{
				if (myStartTimeSortedList == null)
				{
					sortEventsByStartTime();
				}
			}
			else if (CATEGORYAZ.equals(the_choice))
			{
				if (myCategoryAZSortedList == null)
				{
					sortEventsByCategoryAZ();
				}
			}
			else if (CATEGORYZA.equals(the_choice))
			{
				if (myCategoryZASortedList == null)
				{
					sortEventsByCategoryZA();
				}
			}
			displayList();
		}
			
	}


		private void displayList() {
		String the_choice = SPINNERCHOICES[mySpinnerSelection];
		if (the_choice != null)
		{
			myResultsEventsForListView.clear();
			updateEventCount(0);
			if (the_choice.equals(NEAREST) && myDistanceSortedList != null)
			{
				myResultsEventsForListView.addAll(myDistanceSortedList);
			}
			else if (the_choice.equals(SOONEST) && myStartTimeSortedList != null)
			{
				myResultsEventsForListView.addAll(myStartTimeSortedList);
			}
			else if (the_choice.equals(CATEGORYAZ) && myCategoryAZSortedList != null)
			{
				myResultsEventsForListView.addAll(myCategoryAZSortedList);
			}
			else if (the_choice.equals(CATEGORYZA) && myCategoryZASortedList != null)
			{
				myResultsEventsForListView.addAll(myCategoryZASortedList);
			}
			updateEventCount();
			myListView.setSelectionAfterHeaderView();
			if (myListAdapter == null){
				setList();
				setSpinnerListener();
			}
			myListAdapter.notifyDataSetChanged();			
		}		
	}
		
			private void sortEventsByCategoryZA() {
			if (myCategoryAZSortedList == null)
			{
				sortEventsByCategoryAZ();
			}
			myCategoryZASortedList = new LinkedList<EventWithDistance>();
			myCategoryZASortedList.addAll(myCategoryAZSortedList);
			Collections.reverse(myCategoryZASortedList);	
		}
		
		/**
		 * 
		 */
		private void sortEventsByCategoryAZ() {
			EventWithDistance[] events = sortWithComparator(EventWithDistance.CategoryAZComparator);
			if (events != null)
			{
				myCategoryAZSortedList = new LinkedList<EventWithDistance>();
				myCategoryAZSortedList.addAll(Arrays.asList(events));
			}
		}
		
		private void sortEventsByStartTime() {
			EventWithDistance[] events = sortWithComparator(EventWithDistance.StartTimeComparator);
			if (events != null)
			{
				myStartTimeSortedList = new LinkedList<EventWithDistance>();
				myStartTimeSortedList.addAll(Arrays.asList(events));
			}
		}
		
		private void sortEventsByDistance() {
			EventWithDistance[] events = sortWithComparator(EventWithDistance.DistanceComparator);
			if (events != null)
			{
				myDistanceSortedList = new LinkedList<EventWithDistance>();
				myDistanceSortedList.addAll(Arrays.asList(events));
			}
		}

	private EventWithDistance[] sortWithComparator(Comparator<EventWithDistance> the_comparator)
	{
		EventWithDistance[] eventarray = myMasterForListView.toArray(new EventWithDistance[myMasterForListView.size()]);
		Arrays.sort(eventarray, the_comparator);
		return eventarray;
	}
	/**
	 * @return
	 */
	
	private class MarkerSetter extends AsyncTask<Void, ArrayList<MarkerOptions>, Void>
	{
		private boolean iSetProgressBsr= false;
		private ProgressBar myProgressBar = (ProgressBar) ((ActivityMapViewHome)myContext).findViewById(R.id.home_progressbar);
		
		@Override
		protected void onPreExecute()
		{
			myState = DisplaySetterStates.ISSETTINGMAPNOW;
			if (myProgressBar!=null && !(myProgressBar.getVisibility() == View.VISIBLE))
			{
				iSetProgressBsr = true;
				myProgressBar.setVisibility(View.VISIBLE);
			}
		}
		/** Sets markers*/
		@SuppressWarnings("unchecked")
		@Override
		protected Void doInBackground(Void... params) {
			{

			ArrayList<MarkerOptions> markers = new ArrayList<MarkerOptions>();
			if (myResultsQueue != null)
			{ 		
				while (!myResultsQueue.isEmpty())
				{
					PublicEvent event = myResultsQueue.remove();
					if (event != null)
					{
						LatLng eventCoordinates = event.getCoordinates();
						if (eventCoordinates != null)
						{	
							MarkerOptions marker = new MarkerOptions().position
									(eventCoordinates).title(event.getName());
							marker.icon(BitmapDescriptorFactory.defaultMarker(event.getCategory()!=null?event.getCategory().getColor():
								BitmapDescriptorFactory.HUE_YELLOW));
							myEventNameEventMap.put(event.getName(), event);
				    		addToLocationMap(event);			    			
							markers.add(marker);				}
					}				
				}
				publishProgress(markers);
				myState = DisplaySetterStates.HASSETMAPIDLE;
				
			}
				else
				{
					Toast.makeText(myContext.getApplicationContext(), NOEVENTSFOUND, Toast.LENGTH_LONG).show();
				}
		}

				return null;
		}
		
		
		protected void onProgressUpdate(ArrayList<MarkerOptions>... the_markers)
		{
			if (the_markers != null && the_markers.length > 0)
			{
				ArrayList<MarkerOptions> markers = the_markers[0];
				if (myMap != null && markers != null)
				{
					for (MarkerOptions marker: markers)
					{
						try
						{
							myMap.addMarker(marker);
							updateEventCount();
						}
						catch(Exception e)
						{
							break;
						}
					}
				}
			}
		}
		
		/**
		 * @param the_event
		 */
		private void addToLocationMap(PublicEvent the_event) {
			if (the_event!=null)
    		{
    			MarkerAtLocation markershere = myLocationMarkers.get(the_event.getCoordinates());
    			if (markershere == null)
    			{
    				markershere = new MarkerAtLocation(1,1);
    				myLocationMarkers.put(the_event.getCoordinates(), markershere);
    			}
    			else
    			{
    				markershere.setTotalMarkers((markershere.getTotalMarkers()+1));
    			}
    		
    		}
		}
	
		@Override
		protected void onPostExecute(Void param)
		{
			if (iSetProgressBsr && myProgressBar!=null && (myProgressBar.getVisibility() == View.VISIBLE))
			{
				iSetProgressBsr = false;
				myProgressBar.setVisibility(View.GONE);
			}
		}
		
		}
	
	private class ListSetter extends AsyncTask<Void, ArrayList<EventWithDistance>, Void>
	{
		private boolean iSetProgressBsr= false;
		private ProgressBar myProgressBar = (ProgressBar) ((ActivityMapViewHome)myContext).findViewById(R.id.home_progressbar);
		
		@Override
		protected void onPreExecute()
		{
			myState = DisplaySetterStates.ISSETTINGLISTNOW;
			if (myProgressBar!=null && !(myProgressBar.getVisibility() == View.VISIBLE))
			{
				iSetProgressBsr = true;
				myProgressBar.setVisibility(View.VISIBLE);
			}
		}
		/** Sets markers*/
		@SuppressWarnings("unchecked")
		@Override
		protected Void doInBackground(Void... params) {
			{

			ArrayList<EventWithDistance> events = new ArrayList<EventWithDistance>();
			if (myResultsQueue != null)
			{ 		
				while (!myResultsQueue.isEmpty())
				{
					PublicEvent event = myResultsQueue.remove();
					if (event != null)
					{
						events.add(getEventsWithDistance(event));
					}				
				}
				publishProgress(events);
				myState = DisplaySetterStates.HASSETLISTIDLE;
				
			}
				else
				{
					Toast.makeText(myContext.getApplicationContext(), NOEVENTSFOUND, Toast.LENGTH_LONG).show();
				}
		}

				return null;
		}
		
		
		protected void onProgressUpdate(ArrayList<EventWithDistance>... the_events)
		{
			if (the_events != null && the_events.length > 0)
			{
				showInList(the_events[0]);
			}
		}
			
		
		@Override
		protected void onPostExecute(Void param)
		{
			if (iSetProgressBsr && myProgressBar!=null && (myProgressBar.getVisibility() == View.VISIBLE))
			{
				iSetProgressBsr = false;
				myProgressBar.setVisibility(View.GONE);
			}
		}
		
		}
	


		public void showInList(ArrayList<EventWithDistance> the_events)
		{
			if (the_events != null && the_events.size() > 0)
			{
				myMasterForListView.addAll(the_events);
				clearAllSortedLists();
				sortedDisplay();
			}
		}


		
		
		public void removeEvent(PublicEvent the_event)
		{
			if (the_event != null)
			{
				try
				{
					if (myClickedMarker != null)
					{
						myClickedMarker.remove();
					}
					myResultEvents.remove(the_event);
					myResultsEventsForListView.remove(getEventsWithDistance(the_event));
					myEventNameEventMap.remove(the_event.getName());
					updateEventCount();
					resetList();
				}
				catch(Exception e)
				{
					Utility.throwErrorMessage(myContext, "Error", e.getMessage());
				}
			}
		}


		/**
		 * 
		 */
		private void resetList() {
		if (!isMapView)
		{
			basicListClear();
			myState = DisplaySetterStates.RESET;
			displayWorker();
		}
			
		}
}
