package com.bitBusy.goingg.activity;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import org.w3c.dom.Document;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bitBusy.goingg.adapters.ListAdapter;
import com.bitBusy.goingg.dialog.DirectionDialogListener;
import com.bitBusy.goingg.dialog.DirectionsSettingsDialog;
import com.bitBusy.goingg.events.Address;
import com.bitBusy.goingg.fromLocation.FromLocation;
import com.bitBusy.goingg.fromLocation.LocalGeocoder;
import com.bitBusy.goingg.fromLocation.LocalGeocoderRequestor;
import com.bitBusy.goingg.mapDisplay.MapDirectionsSetter;
import com.bitBusy.goingg.mapDisplay.MapDirectionsUtil;
import com.bitBusy.goingg.mapDisplay.MarkerToAdd;
import com.bitBusy.goingg.mapDisplay.TransitStopDetails;
import com.bitBusy.goingg.settings.DirectionSetting;
import com.bitBusy.goingg.utility.ActionBarChoice;
import com.bitBusy.goingg.webServices.EventsData.Directions;
import com.bitBusy.goingg.webServices.EventsData.DirectionsDocumentRequestor;
import com.bitBusy.goingg.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class ActivityMapDirections extends Activity implements DirectionsDocumentRequestor, DirectionDialogListener, LocalGeocoderRequestor {
	
	/** event name*/
	public static final String EVENTNAME = "com.bitBusy.wevent.EVENTNAME";
	/** intent extra identifier*/
	public static final String LATLNGBUNDLE = "com.bitBusy.wevent.LATLNGBUNDLE";

		/** intent extra identifier*/
	public static final String TOLATLNG = "com.bitBusy.wevent.TOLATLNG";
	
	/** intent extra identifier*/
	public static final String EVENTCOLOR = "com.bitBusy.wevent.EVENTCOLOR";
	
	/** from address*/
	public static final String TOADDRESS = "com.bitBusy.wevent.FROMADDRESS";
	
	/** conversion unit*/
	private static final double ONEMETERMILE = 0.0006;
	
	/** format*/
	private static final String DECIMALFORMAT = "#.00";
	
	/** mile*/
	private static final String MILE = " mi";
	
	/** start*/
	private static final String STARTLOC = "start";
	
	//private HashMap<LatLng, Event> myMarkerPoints;
	
	/** event name*/
	private String myEventName;
	
	/** mode*/
	private String myMode = Directions.MODE_DRIVING;
	
	/** from address*/
	private Address myFromAddress;
	
	/** to address*/
	private Address myToAddress;
	
	/** map object*/
	private GoogleMap myMap;

	/** fragment*/
	private MapFragment myFragment;
	
	/** list view*/
	private ListView myListView;
	
	/** from latlng*/
	private LatLng myFromLatLng;
	
	/** to latlng*/
	private LatLng myToLatLng;
	
	/** directions document*/
	private Document myDirectionsDoc;
	 
	
	/** event color*/
	private float myEventColor;
	
	/** boolean indicating view*/
	private boolean isMapView = true;
	
	/** list*/
	private ArrayList<String> myDirectionsList;
	
	/** list of points - directions*/
	private ArrayList<LatLng> myDirectionCoords;
	
	/** transit stops*/
	private ArrayList<TransitStopDetails> myDepartStops;
	
	
		/** arrival stops*/
	private ArrayList<TransitStopDetails> myArrivalStops;
	
	/** departs*/
	private Calendar myDateTime = null;
	
	/** avoid highway*/
	private boolean myHighwayAvoided = false;
	
	/** avoid toll*/
	private boolean myTollAvoided = false;
	
	/** is depart Set*/
	private boolean myDepartSet = true;
	
	/** current settings*/
	private DirectionSetting myCurrentSetting;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map_directions);
		myFragment = ((MapFragment) getFragmentManager().findFragmentById(
                R.id.activity_directions_map));
		//prevent map from reloading on orientation change
		if (savedInstanceState == null)
		{
			getData();
			setDirections();
			myCurrentSetting = new DirectionSetting(myToAddress, myMode, myHighwayAvoided, myTollAvoided, myDepartSet, myDateTime);
			setMap();
		}

	}
	
	
	/** method to setup data*/
	private void getData()
	{
   		myFromLatLng = FromLocation.getInstance().getFromLoc();
        getIntentExtras();
	}
		
	
	
	/**
	 * method to display map
	 */
	private void setMap()
	{
		myFragment.setRetainInstance(true);
		try {
        	initializeMap();
           // setCurrentLocation();
            addMarkers();
            drawDirections();
           } catch (Exception e) {
            e.printStackTrace();
        }

	}
	

	/** method to display list of directions*/
	private void setList()
	{
		if (myDirectionsList == null)
		{
			setDirectionsListArray();
		}
		myListView.setAdapter(new ListAdapter(this, myDirectionsList, null));
	}

	/**
     * function to load map. If map is not created it will create it for you
     * */
    private void initializeMap() {
        if (myMap == null && myFragment != null) {
            myMap = myFragment.getMap();
            
            // check if map is created successfully or not
            if (myMap == null) {
                Toast.makeText(getApplicationContext(),
                        "Unable to create the map, sorry!", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }
 

    /**
	 * adds markers to start and end pins
	 */
	private void addMarkers() {
		ArrayList<MarkerToAdd> sendMarkers = new ArrayList<MarkerToAdd>();
		MarkerOptions dest = new MarkerOptions().position(myToLatLng).title(myEventName); 
		dest.icon(BitmapDescriptorFactory.defaultMarker(myEventColor));
		sendMarkers.add(new MarkerToAdd(dest, null, false));
		sendMarkers.add(new MarkerToAdd(new MarkerOptions().position(myFromLatLng).title(STARTLOC), null,false));
		if (Directions.MODE_TRANSIT.equals(myMode))
		{
			if (myDepartStops == null || myArrivalStops == null)
			{
				setTransitStops();
			}
			MarkerOptions transitMarker;
			for (TransitStopDetails transitStop : myDepartStops)
			{
				if (transitStop != null)
				{
						transitMarker = new MarkerOptions().position(transitStop.getLatLng()).title(transitStop.getVehicleDets() + 
								" departs at " + transitStop.getArrDepTime());
						transitMarker.icon(BitmapDescriptorFactory.fromResource(R.drawable.bus_map_marker));
						sendMarkers.add(new MarkerToAdd(transitMarker, transitStop, true));
						
					/*}
					else
					{
						//MODIFY
						/*String existingSnippet = addedMarkers.get(transitStop.getLatLng())!=null?addedMarkers.get(transitStop.getLatLng()).getTitle()
								:null;
						addedMarkers.get(transitStop.getLatLng()).title(existingSnippet.concat("-------------------").concat(
								getMarkerString(true, transitStop)));*/
					//}
				}
			}
			
			for (TransitStopDetails transitStop : myArrivalStops)
			{
				if (transitStop != null)
				{
					
						transitMarker = new MarkerOptions().position(transitStop.getLatLng()).title(transitStop.getVehicleDets() + " arrives at " + 
						transitStop.getArrDepTime());
						transitMarker.icon(BitmapDescriptorFactory.fromResource(R.drawable.bus_map_marker));
						sendMarkers.add(new MarkerToAdd(transitMarker, transitStop, false));
					/*}
					else
					{
						/*String existingSnippet = addedMarkers.get(transitStop.getLatLng())!=null?addedMarkers.get(transitStop.getLatLng()).getSnippet():null;
						addedMarkers.get(transitStop.getLatLng()).snippet(existingSnippet.concat("-------------------").concat(
								getMarkerString(false, transitStop)));*/
					//}
				}
			}
		}
		new MapDirectionsSetter(this, myMap, sendMarkers).addMarkers();
			
	}


	
	

	/**
	 * queries ws for directions
	 */
	private void setDirections() {
		
		Directions direct = new Directions(this);
		direct.registerRequestor(this);
		direct.execute(myFromLatLng, myToLatLng, myMode, myHighwayAvoided, myTollAvoided, myDepartSet, myDateTime);
		}


    
    /**
   	 * get coordinates & event name
   	 */
   	private void getIntentExtras() {
   		Intent intent = getIntent();
   		Bundle bundle = intent.getParcelableExtra(LATLNGBUNDLE);
   		myToLatLng = bundle.getParcelable(TOLATLNG); 
   		myEventName = intent.getStringExtra(EVENTNAME);
   		myEventColor = intent.getFloatExtra(EVENTCOLOR, BitmapDescriptorFactory.HUE_RED);
   		myToAddress = intent.getParcelableExtra(TOADDRESS);
   		if (myToAddress == null)
   		{
   			myToAddress = new Address(myEventName, null, null, null, null);
   		}
   	}
   	
 


	/* (non-Javadoc)
	 * @see webServices.DirectionsDocumentRequestor#acceptDocument(org.w3c.dom.Document)
	 */
	@Override
	public void acceptDocument(Document the_doc) {
		myDirectionsDoc = the_doc;
		if (the_doc != null)
			{
				setDistance();
				setTime();
				getFromAddress();
				if (isMapView)
				{
					setDirectionsPointsArray();
					addMarkers();
					drawDirections();
				}
				else
				{
					setDirectionsListArray();
					setList();
				}
			}
	}
	
	/** method to set distance*/
	private void setDistance()
	{
		TextView distance = (TextView) findViewById(R.id.activity_directions_distanceValue);
		NumberFormat nFormat = NumberFormat.getInstance(Locale.ENGLISH);
		DecimalFormat dFormat = (DecimalFormat) nFormat;
	    dFormat.applyPattern(DECIMALFORMAT); 
		distance.setText(String.valueOf(dFormat.format(MapDirectionsUtil.getDistanceValue(myDirectionsDoc) * ONEMETERMILE)).concat(MILE));
	}
	
	
	/** method to set time*/
	private void setTime()
	{
		TextView time = (TextView) findViewById(R.id.activity_directions_timeValue);
		time.setText(MapDirectionsUtil.getDurationText(myDirectionsDoc));
	}
	
	/** method to get from address*/
	private void getFromAddress()
	{
		myFromAddress = FromLocation.getInstance().getFromAdd();
				//new Address(null, MapDirectionsUtil.getStartAddress(myDirectionsDoc), null);
	}
	
	
	/** method to set directions points array*/
	private void setDirectionsPointsArray()
	{
		myDirectionCoords = MapDirectionsUtil.getDirection(myDirectionsDoc);
	}
	
	/** method to set directions list array*/
	private void setDirectionsListArray()
	{
		myDirectionsList = MapDirectionsUtil.getInstructionsList(myDirectionsDoc);
	}
	
	 /** sets transit stops array */
		private void setTransitStops() {
			myDepartStops = MapDirectionsUtil.getDepartureStops(myDirectionsDoc);
			myArrivalStops = MapDirectionsUtil.getArrivalStops(myDirectionsDoc);
		}

	/** method to draw directions on map*/	
	private void drawDirections()
	{
		if (myDirectionCoords == null)
		{
			setDirectionsPointsArray();
		}
		
		PolylineOptions rectLine = new PolylineOptions().width(8).color(Color.DKGRAY);
		for(int i = 0 ; i < myDirectionCoords.size() ; i++) {  
			rectLine.add(myDirectionCoords.get(i));
			}
		myMap.addPolyline(rectLine);
		
	}
	
	
	/**
	 * method called when display list button clicked
	 * @param the_view
	 */
	public void displayList(View the_view)
	{
		ImageButton listButton = (ImageButton) findViewById (R.id.activity_directions_map_listViewButton);
		listButton.setVisibility(View.INVISIBLE);

		ImageButton mapButton = (ImageButton) findViewById (R.id.activity_directions_map_mapViewButton);
		mapButton.setVisibility(View.VISIBLE);
	
		isMapView = false;
		
		if (myFragment != null)
		{
			myFragment.getView().setVisibility(View.INVISIBLE);
		}
		
		myListView = (ListView) findViewById(R.id.activity_directions_map_listDirs);
		myListView.setVisibility(View.VISIBLE);
		setList();
	}
	
	/**
	 * displays map
	 * @param the_view
	 */
	public void displayMap(View the_view)
	{
		ImageButton listButton = (ImageButton) findViewById (R.id.activity_directions_map_listViewButton);
		listButton.setVisibility(View.VISIBLE);

		ImageButton mapButton = (ImageButton) findViewById (R.id.activity_directions_map_mapViewButton);
		mapButton.setVisibility(View.INVISIBLE);
		
		isMapView = true;
		
		if (myListView != null)
		{
			myListView.setVisibility(View.INVISIBLE);
		}
		if (myFragment != null)
		{
			myFragment.getView().setVisibility(View.VISIBLE);
		}
		
		setMap();
	}
	
	public void showSettings(View the_view)
	{
		DirectionsSettingsDialog dialog = new DirectionsSettingsDialog(this, 
				 myToAddress, myMode, myHighwayAvoided, myTollAvoided, myDepartSet, myDateTime);
		dialog.registerListener(this);
		dialog.openDialog();
	}


	/* (non-Javadoc)
	 * @see dialog.DirectionDialogListener#onSet(com.google.android.gms.maps.model.LatLng, java.lang.String)
	 */
	@Override
	public void onSet(DirectionSetting the_setting) {
		
		if (the_setting != null && !the_setting.equals(myCurrentSetting))
		{
			if (the_setting.getMode() != null)
			{
				myMode = the_setting.getMode();
				if (myMode.equals(Directions.MODE_DRIVING))
				{
					myHighwayAvoided = the_setting.isHighwayAvoided();
					myTollAvoided = the_setting.isTollAvoided();
				}
				else if (myMode.equals(Directions.MODE_TRANSIT))
				{
					myDepartSet = the_setting.isDepartAtSet();
					myDateTime = the_setting.getDateTime()!=null?the_setting.getDateTime():null;
				}
				if (the_setting.getFromAddress() != null && !the_setting.getFromAddress().equals(myFromAddress))
				{
					setFromAddress(the_setting.getFromAddress());
				}
				else
				{
					reset();
				}
			}
			
		}
	}
	
	/**
	 * @param the_fromAddress
	 */
	private void setFromAddress(Address the_fromAddress) {
		myFromAddress = the_fromAddress;
		LocalGeocoder geocoder = new LocalGeocoder(this);
		geocoder.registerRequestor(this);
		geocoder.execute(myFromAddress);
	}
	
	/** method to reset*/
	private void reset()
	{
		clear();
		setDirections();			
	}
	/** clears previously set values*/
	private void clear()
	{
		myMap.clear();
		//addMarkers();
		myDirectionsDoc = null;
		myDirectionsList = null;
		myDirectionCoords = null;
	}


	/* (non-Javadoc)
	 * @see com.bitBusy.wevent.fromLocation.LocalGeocoderRequestor#acceptLatLng(com.google.android.gms.maps.model.LatLng)
	 */
	@Override
	public void acceptLatLng(LatLng the_latLng) {
		if (the_latLng != null)
		{
			myFromLatLng = the_latLng;
			//FromLocation.getInstance().setFromAdd(myFromAddress);
			//FromLocation.getInstance().setFromLoc(the_latLng);
			reset();
		}
		else
		{
			Toast.makeText(getApplicationContext(), "No results!Start address incorrect or incomplete", Toast.LENGTH_LONG).show();
		}
	}
	
	@Override
	  public boolean onCreateOptionsMenu(Menu the_menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.action_bar_menu, the_menu);
	    ActionBarChoice.setupActionBar(the_menu, this);
	    return true;
	  }
	  
	  @Override
	  public boolean onOptionsItemSelected(MenuItem the_item) {
	    com.bitBusy.goingg.utility.ActionBarChoice.choiceMade(this, the_item);

	    return true;
	  }
	
}
