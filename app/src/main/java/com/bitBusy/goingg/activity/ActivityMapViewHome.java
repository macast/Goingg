package com.bitBusy.goingg.activity;

import java.util.ArrayList;
import java.util.Calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bitBusy.goingg.WIDAuthentication.WIDAccessToken;
import com.bitBusy.goingg.adapters.ListAdapter;
import com.bitBusy.goingg.database.setup.DBInteractor;
import com.bitBusy.goingg.dialog.SearchSettingListener;
import com.bitBusy.goingg.dialog.SearchSettingsDialog;
import com.bitBusy.goingg.dialog.WIDAuthenticationDialog;
import com.bitBusy.goingg.dialog.WIDAuthenticationListener;
import com.bitBusy.goingg.events.Address;
import com.bitBusy.goingg.events.EventCategory;
import com.bitBusy.goingg.fromLocation.FromLocation;
import com.bitBusy.goingg.fromLocation.LocalGeocoder;
import com.bitBusy.goingg.fromLocation.LocalGeocoderRequestor;
import com.bitBusy.goingg.fromLocation.LocationClientListener;
import com.bitBusy.goingg.fromLocation.LocationIdentifier;
import com.bitBusy.goingg.license.EULA;
import com.bitBusy.goingg.mapDisplay.EventsDisplaySetter;
import com.bitBusy.goingg.rating.Rate;
import com.bitBusy.goingg.settings.QueryFilter;
import com.bitBusy.goingg.settings.SearchSetting;
import com.bitBusy.goingg.utility.ActionBarChoice;
import com.bitBusy.goingg.utility.CurrentLoginState;
import com.bitBusy.goingg.utility.Utility;
import com.bitBusy.goingg.webServices.awsSetup.AWSCredentialsStore;
import com.bitBusy.goingg.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;

//nup: fix bug for no internet, no location; ADD NEW PERM TO BLOG , 
//ADD AWS TO AWS, ADD KEYS TO AWS
public class ActivityMapViewHome extends Activity implements LocationClientListener, SearchSettingListener,LocalGeocoderRequestor,
	WIDAuthenticationListener{

	public static final String DIALOGSHOWING = "showDialog";
	
	public static final int DEFAULTSTARTHOUR = 0;
	
	public static final int DEFAULTENDHOUR = 23;
	
	public static final int DEFAULTSTARTMIN = 0;
	
	public static final int DEFAULTENDMIN = 59;

	public static final boolean LOGGING = true;
	/** default dist*/
	private static final int DEFAULTDISTANCE = 50;
	
	private enum WIDAuthenticationResults {Authenticated, NotAuthenticated, AuthenticatedTokenExpired}; 
	
	/** Zoom level*/
	private static final float ZOOMLEVEL = 8;
		
	/** default from add*/
	private static final String DEFAULTFROMADD = "current location";
	
		/** from add*/
	private Address myFromAddress;
	
	/** map object*/
	private GoogleMap myMap;

	private ListView myListView;
	
	private LinearLayout myListLayout;
	
	/** fragment*/
	private MapFragment myFragment;
		
	private SearchSetting mySearchSetting;
	
	private EventsDisplaySetter myEventsDisplay;
	
	private boolean isMapView = true;
	
	private static Context myAppContext;
				
	private WIDAuthenticationDialog myWIDDialog; 

	
	private WIDAccessToken myIDToken;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
			myAppContext = getApplicationContext();
			WIDAuthenticationResults currentState = getWIDAuthenticationState();
			if (currentState == WIDAuthenticationResults.Authenticated){
				beginDisplay(savedInstanceState);
			//	initiateFriendsList();
			}
			else if (currentState == WIDAuthenticationResults.AuthenticatedTokenExpired){
				getNewToken();
			//	initiateFriendsList();
			}
			else{
				openWIDAuthenticationDialog();
			}
		  }	
	
	
	private void getNewToken() {
		if (myIDToken != null && myIDToken.getEmailID() != null){
			new WIDAuthenticationDialog().setContext(this).getGoogleAccessToken(myIDToken.getEmailID());
		}
		else{
			Utility.throwErrorMessage(this, "Token fetch error", "Unable to retrieve new ID token");
		}
	}

	private void openWIDAuthenticationDialog()
	{
			myWIDDialog = new WIDAuthenticationDialog();
			myWIDDialog.setContext(this).openDialog();		
	}

	private WIDAuthenticationResults getWIDAuthenticationState() 
	{	
		if (CurrentLoginState.getUserLoggedInGoogle(this)){
			WIDAccessToken token = CurrentLoginState.getWIDAccessToken(this);
			if (token != null){
				long expiration = token.getDate();
				String email = token.getEmailID();
				if (token != null && email != null){
					myIDToken = token;
					if (getSecondsSinceEpoch() < expiration){
						return WIDAuthenticationResults.Authenticated;
					}
					else{
						return WIDAuthenticationResults.AuthenticatedTokenExpired;
					}
				}
			}
		}
		return WIDAuthenticationResults.NotAuthenticated;
	}

	private long getSecondsSinceEpoch() {
		Calendar calendar = Calendar.getInstance();
		long time = (calendar.getTimeInMillis())/1000L;
		return time;
	}

	@Override
	    public void onActivityResult(int requestCode, int resultCode, Intent data) {
			if (requestCode == WIDAuthenticationDialog.GPLUS_SIGNIN)
			{
				myWIDDialog.connectToGplus();
			}
	}

	/**
	 * @param savedInstanceState
	 */
	private void beginDisplay(Bundle the_savedInstanceState)
	{
		new EULA(this).show();
		initializeAWSCredentials();
		Rate.app_launched(this);
		setContentView(R.layout.activity_map_view_home);
		setMap(the_savedInstanceState);		
	}

	private void initializeAWSCredentials(){
		if (myIDToken != null && myIDToken.getToken() != null){
		AWSCredentialsStore awsCreds = AWSCredentialsStore.getInstance();
		awsCreds.initializeCredentials(this, myIDToken.getToken());
		}
		else
		{
			Utility.throwErrorMessage(this, "Credentials error", "Unable to initialize server credentials");
		}
	}

	public void showEvents(View the_view)
	{
		updateShowEventsVisibility(View.GONE);
		setProgressBarVisibility(View.VISIBLE);
		setList();
		initializeLocalDBCache();
		searchForLocation();
	}

	private void updateShowEventsVisibility(int the_vis) {
		Button button = (Button) findViewById(R.id.activity_mapviewhome_showeventsbutton);
		button.setVisibility(the_vis);
		}

	private void setProgressBarVisibility(int the_visibility) {
		ProgressBar progressBar = (ProgressBar) findViewById (R.id.home_progressbar);
		progressBar.setVisibility(the_visibility);	
	}


	private void initializeLocalDBCache() 
	{
		new DBInteractor(this).initializeLocalDBCache();
	}

	
	private void searchForLocation() {
		new LocationQuerier().execute();
	}


	private void setMap(Bundle savedInstanceState)
	{
		myFragment = ((MapFragment) getFragmentManager().findFragmentById(
                R.id.map));
		//prevent map from reloading on orientation change
		if (savedInstanceState == null && !myFragment.getRetainInstance())
		{
			myFragment.setRetainInstance(true);
			    initializeMap();
	        	disableCompass();
	        	disableFindMe();
	            setCurrentLocation();	
	     }
	}
	
	/** disables find me button from map 
	 */
	private void disableFindMe(){
		if (myMap != null && myMap.getUiSettings() != null){
			myMap.getUiSettings().setMyLocationButtonEnabled(false);
		}
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
	 * Disables comapss off map
	 */
	private void disableCompass() {
		if (myMap != null && myMap.getUiSettings() != null)
		{
			myMap.getUiSettings().setCompassEnabled(false);
		}
		
	}

	 /** sets current location*/
    private void setCurrentLocation()
    {
    	if (myMap != null)
    	{
    		myMap.setMyLocationEnabled(true);
      	}
    }
    
	public void showMore(View the_view)
	{
		if (myEventsDisplay != null)
		{
			myEventsDisplay.showMore();
		}
	}
    
	private void setList()
	{
		myListLayout = (LinearLayout) findViewById(R.id.activity_home_listlayout);
		myListView = (ListView) findViewById(R.id.activity_mapviewhome_list);
		//setListViewFooter();
	}
	
	/** creation of query filter*/
	private void createSearchSetting()
	{
		EventCategory[] categories = EventCategory.values();
		Calendar fromDateTime = Calendar.getInstance();
		fromDateTime.set(Calendar.HOUR_OF_DAY, DEFAULTSTARTHOUR);
		fromDateTime.set(Calendar.MINUTE, DEFAULTSTARTMIN);
		Calendar toDateTime = Calendar.getInstance();	
		toDateTime.set(Calendar.HOUR_OF_DAY, DEFAULTENDHOUR);
		toDateTime.set(Calendar.MINUTE, DEFAULTENDMIN);
		mySearchSetting = new SearchSetting((new QueryFilter(categories, fromDateTime, toDateTime, 	
				 DEFAULTDISTANCE)), SearchSettingsDialog.DISPLAYOPTIONS.BOTH);
	}

 
    @Override
    protected void onResume() {
        super.onResume();
 //       setMap(); 
    }
 
	
	/* (non-Javadoc)
	 * @see com.bitBusy.wevent.FromLocation.LocationClientListener#onLocationIdentified(com.google.android.gms.maps.model.LatLng)
	 */
	@Override
	public void onLocationIdentified(LatLng the_location) {
		if (the_location != null)
		{
			moveCamera(the_location);
			FromLocation.getInstance().setFromLoc(the_location);
			myFromAddress = new Address(DEFAULTFROMADD, null , null, null,  null);
			FromLocation.getInstance().setFromAdd(myFromAddress);
			createSearchSetting();
			show();
		}
		else
		{
			throwNoLocationDialog();
		}
	}
	
	
		private void throwNoLocationDialog() {
		setProgressBarVisibility(View.GONE);
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setTitle("Location undetermined");
			// set dialog message
			alertDialogBuilder
				.setMessage("Location services disabled?" +
						"\nAlso, please check if you're connected to the Internet. If you are, would you like to enter your location manually?")
				.setCancelable(false)
				.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
						openEnterAddressDialog();
					}
				  })
				.setNegativeButton("No",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
						dialog.cancel();
						goHome();
					}

					private void goHome() {
						Intent homeIntent = new Intent(Intent.ACTION_MAIN);
					    homeIntent.addCategory( Intent.CATEGORY_HOME );
					    homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);  
					    startActivity(homeIntent); 						
					}
				});
 
				// create alert dialog
				AlertDialog alertDialog = alertDialogBuilder.create();
 
				// show it
				alertDialog.show();
	}

	private void moveCamera(LatLng the_location)
	{
		if (the_location != null && myMap != null)
		{
			CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(the_location, ZOOMLEVEL);
    		myMap.animateCamera(cameraUpdate);
		}
	}

	/* (non-Javadoc)
	 * @see com.bitBusy.wevent.dialog.SearchSettingListener#onSearch(com.bitBusy.wevent.dialog.SearchSetting)
	 */
	@Override
	public void onSearch(SearchSetting the_setting) {
		if (the_setting != null)
		{
			if (myFromAddress != null && !myFromAddress.equals(FromLocation.getInstance().getFromAdd()))
			{
				if (!the_setting.equals(mySearchSetting))
				{
					mySearchSetting = the_setting;
				}
				findFromLatLng();
			}
			else 
			{
				if (the_setting != null && !the_setting.equals(mySearchSetting))
				{
					mySearchSetting = the_setting;
					myEventsDisplay.setQueryFilter(the_setting, false);
				}
			}
		}
	}
	
	private void findFromLatLng()
	{
		LocalGeocoder geocoder = new LocalGeocoder(this);
		geocoder.registerRequestor(this);
		geocoder.execute(FromLocation.getInstance().getFromAdd());
	}
	
	/** method to reinitialilze map*/
	private void show()
	{
		if (myEventsDisplay == null)
		{
			setEventsDisplay();
		}
			myEventsDisplay.display();	
	}

	public void search(View the_view)
	{			
		if (mySearchSetting != null)
		{
			SearchSettingsDialog dialog = new SearchSettingsDialog(this, mySearchSetting);
			dialog.registerListener(this);
			dialog.openDialog();
		}

	}
	/* (non-Javadoc)
	 * @see com.bitBusy.wevent.fromLocation.LocalGeocoderRequestor#acceptLatLng(com.google.android.gms.maps.model.LatLng)
	 */
	@Override
	public void acceptLatLng(LatLng the_latLng) {
		if (the_latLng != null)
		{
			myFromAddress = FromLocation.getInstance().getFromAdd();
			FromLocation.getInstance().setFromLoc(the_latLng);
			moveCamera(the_latLng);
			myEventsDisplay.setQueryFilter(mySearchSetting, true);
		}
		else
		{
			updateShowEventsVisibility(View.VISIBLE);
			Toast toast = Toast.makeText(getApplicationContext(), "Location not identified." +
					" Address incorrect or incomplete/ request timed out", Toast.LENGTH_LONG);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
		}
	}
	
	public void displayMap(View the_view)
	{
		isMapView = true;
		myMap.clear();
		makeMapVisible();
	}
	
	public void displayList(View the_view)
	{
		isMapView = false;
		makeMapVisible();
	}
	

	private void makeMapVisible()
	{
		if (myFragment != null && myListLayout != null)
		{
			ImageButton listButton = (ImageButton) findViewById (R.id.activity_mapviewhome_listbutton);
			ImageButton mapButton = (ImageButton) findViewById (R.id.activity_mapviewhome_mapbutton);
			Button legend = (Button) findViewById(R.id.home_legendButton);
			if (isMapView)
			{
				myFragment.getView().setVisibility(View.VISIBLE);
				myListLayout.setVisibility(View.GONE);
				listButton.setVisibility(View.VISIBLE);
				mapButton.setVisibility(View.INVISIBLE);
				legend.setVisibility(View.VISIBLE);
			}
			else
			{
				myFragment.getView().setVisibility(View.GONE);
				myListLayout.setVisibility(View.VISIBLE);
				listButton.setVisibility(View.INVISIBLE);
				mapButton.setVisibility(View.VISIBLE);
				legend.setVisibility(View.GONE);
			}
			setEventsDisplayFlag();
		}
	}
	
	private void setEventsDisplayFlag()
	{
		if (myEventsDisplay == null)
		{
			setEventsDisplay();
		}
		else{
			myEventsDisplay.setIsMapView(isMapView);
		}
	}
	private void setEventsDisplay()
	{
		TextView eventsCount = (TextView) findViewById(R.id.activity_mapviewhome_eventsNoValue);
		myEventsDisplay = 	new EventsDisplaySetter(this, myMap, myListView, eventsCount, isMapView, mySearchSetting);
	}
	
	 public static Context getAppContext()
	 {
		 return myAppContext;
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
	  
	  public void openLegend(View the_view)
	  {
		  Dialog dlg = createLegendDialog();
		  if (dlg != null)
		  {
			  Window window = dlg.getWindow();
			  WindowManager.LayoutParams wlp = window.getAttributes();
			  wlp.gravity = Gravity.BOTTOM;
			  wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
			  window.setAttributes(wlp);
			  window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
			  dlg.setCanceledOnTouchOutside(true);
			  dlg.show();
		  }
	  }
	  
	  private Dialog createLegendDialog()
	  {
		  AlertDialog.Builder builder = new AlertDialog.Builder(this);
		    LayoutInflater inflater = getLayoutInflater();
		    View view = inflater.inflate(R.layout.home_map_legend, null);
			  setLegendGrid(view);
			  builder.setView(view);
		    return builder.create();
	  }
	  
	  private void setLegendGrid(View the_view)
	  {
		  GridView legendView = (GridView) the_view.findViewById(R.id.legend_grid);
		  ArrayList<EventCategory> list = getCategoriesList();
		  legendView.setAdapter(new ListAdapter(this, list, null));
	  }
	  private ArrayList<EventCategory> getCategoriesList()
	  {
		  ArrayList<EventCategory> list = new ArrayList<EventCategory>();
		  EventCategory[] array = EventCategory.values();
		  for (EventCategory category: array)
		  {
			  list.add(category);
		  }
		  return list;
	  }
	  private class LocationQuerier extends AsyncTask<Void, Void, Void>
	  {

		@Override
		protected Void doInBackground(Void... arg0) {
			LocationIdentifier locIdentifier = new LocationIdentifier(ActivityMapViewHome.this);
			locIdentifier.registerListener(ActivityMapViewHome.this);
			locIdentifier.getLastKnownLoc();
			return null;
		
		}	  
	  }
	  
	  private void openEnterAddressDialog() {
			Dialog dialog = new Dialog(this);
			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		    LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		    dialog.setContentView(inflater.inflate(R.layout.enter_address_dialog, null));
		    setAddressDialogButtons(dialog);
		    dialog.show();					
		}

		private void setAddressDialogButtons(final Dialog the_dialog) {
			if (the_dialog != null)
			{
				Button cancel = (Button) the_dialog.findViewById(R.id.enter_address_cancel);
				cancel.setOnClickListener(new OnClickListener() {						 
					@Override
					public void onClick(View the_view) {
						the_dialog.dismiss();
					}
				});
				
				Button set = (Button) the_dialog.findViewById(R.id.enter_address_set);
				set.setOnClickListener(new OnClickListener() {						 
					@Override
					public void onClick(View the_view) {
						Address address = getAddress(the_dialog);
						if (address == null)
						{
							toastEmptyAddress();
						}
						else
						{
							FromLocation.getInstance().setFromAdd(address);
							LocalGeocoder geocoder = new LocalGeocoder(ActivityMapViewHome.this);
							geocoder.registerRequestor(ActivityMapViewHome.this);
							geocoder.execute(address);
							createSearchSetting();
							setEventsDisplay();
							the_dialog.dismiss();
						}
					}
				});
			}
		}
		
		private void toastEmptyAddress() {
			Toast toast = Toast.makeText(getApplicationContext(), SearchSettingsDialog.EMPTYADDRESS, Toast.LENGTH_LONG);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();	
		}
		private Address getAddress(Dialog the_dialog)
		{
			if (the_dialog != null)
			{
				EditText street = (EditText)the_dialog.findViewById(R.id.enter_address_streetvalue);
				EditText city = (EditText)the_dialog.findViewById(R.id.enter_address_cityvalue);
				EditText state = (EditText)the_dialog.findViewById(R.id.enter_address_statevalue);
				EditText country = (EditText)the_dialog.findViewById(R.id.enter_address_countryvalue);
				EditText zipcode = (EditText)the_dialog.findViewById(R.id.enter_address_postalcodevalue);
				
				String streetAdd = street!=null&&street.getText()!=null?street.getText().toString():null;
				String cityAdd = city!=null&&city.getText()!=null?city.getText().toString():null;
				String stateAdd = state!=null&&state.getText()!=null?state.getText().toString():null;
				String countryAdd = country!=null&&country.getText()!=null?country.getText().toString():null;
				String zipcodeAdd = zipcode!=null&&zipcode.getText()!=null?zipcode.getText().toString():null;
				if (!isAddressValid(streetAdd, cityAdd, stateAdd, countryAdd, zipcodeAdd))
				{
					return null;
				}
				return new Address(streetAdd, cityAdd, stateAdd, countryAdd, zipcodeAdd);
			}
			return null;
		}


		/**
		 * @param streetAdd
		 * @param cityAdd
		 * @param stateAdd
		 * @param countryAdd
		 * @param zipcodeAdd
		 * @return
		 */
		private boolean isAddressValid(String the_streetAdd, String the_cityAdd,
				String the_stateAdd, String the_countryAdd, String the_zipcodeAdd) {
			if ((the_streetAdd == null || the_streetAdd.length()==0) &&
				(the_cityAdd == null || the_cityAdd.length()==0) &&
				 (the_stateAdd == null || the_stateAdd.length()==0) &&
				 (the_countryAdd == null || the_countryAdd.length()==0) &&
				 (the_zipcodeAdd == null || the_zipcodeAdd.length()==0))
			{
				return false;
			}
			return true;
		}

		/* (non-Javadoc)
		 * @see com.bitBusy.going.dialog.WIDAuthenticationListener#onWIDAuthenticated(boolean)
		 */
		@Override
		public void onWIDAuthenticated
		(boolean wasAuthenticated, WIDAccessToken the_token)
		{
			if (wasAuthenticated && the_token != null && the_token.getToken() != null)
			{
				CurrentLoginState.setUserLoggedInGoogle(this, wasAuthenticated, the_token);
				myIDToken = the_token;
				beginDisplay(null);
			}
		}
		
}


