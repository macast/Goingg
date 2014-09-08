package com.bitBusy.goingg.activity;

import java.util.Calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bitBusy.goingg.dialog.SearchSettingsDialog;
import com.bitBusy.goingg.events.Address;
import com.bitBusy.goingg.events.EventCategory;
import com.bitBusy.goingg.events.Link;
import com.bitBusy.goingg.events.PublicEvent;
import com.bitBusy.goingg.fromLocation.LocalGeocoder;
import com.bitBusy.goingg.fromLocation.LocalGeocoderRequestor;
import com.bitBusy.goingg.utility.ActionBarChoice;
import com.bitBusy.goingg.utility.UUIDGenerator;
import com.bitBusy.goingg.utility.Utility;
import com.bitBusy.goingg.webServices.EventsData.AWSPut;
import com.bitBusy.goingg.R;
import com.google.android.gms.maps.model.LatLng;

public class ActivityCreateEvent extends Activity  implements LocalGeocoderRequestor {

	public static final String INVALIDNAMEMESSAGE = "event name must have a value";
	public static final String INVALIDDATERANGEMESSAGE = "From date/time should be earlier than To value";
	private static final String INVALIDURLMESSAGE = "invalid url!";
	private static final String IDENTIFYINGLOCATION = "Determinig the address coordinates...";
	private static final String CREATIONFAIL = "Event Creation failed";
	private static final String ADDRESSMAPFAIL = "Address coordinates could not be found!";

	private String myName;
	private Calendar myFromDateTime;
	private Calendar myToDateTime;
	private Address myAddress;
	private String myPrice;
	private String myDescription;
	private LatLng myLatLng;
	private Link myInfoLink;
	private ProgressDialog myProgressDialog;
	private EventCategory myTempSelectedCategory = EventCategory.CATEGORYOTHER;
	private EventCategory mySelectedCategory = EventCategory.CATEGORYOTHER;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_event);
		setCategoryButton();
		setDateTimeEditTexts();
		setAddressEditText();
	}

	/**
	 * 
	 */
	private void setAddressEditText() {
		final EditText address = (EditText) findViewById(R.id.activity_create_event_addressvalue);
		address.setOnClickListener(new OnClickListener() {						 
			@Override
			public void onClick(View arg0) {
				openEnterAddressDialog(address);
				
			}
		});

			
}

private void openEnterAddressDialog(EditText the_address) {
	Dialog dialog = new Dialog(this);
	dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
    LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    
    // Inflate and set the layout for the dialog
    // Pass null as the parent view because its going in the dialog layout
    dialog.setContentView(inflater.inflate(R.layout.enter_address_dialog, null));
  //  setDateTime(dateDialog, the_button);
    setAddressDialogButtons(dialog, the_address);
    dialog.show();					
}

private void setAddressDialogButtons(final Dialog the_dialog,
		final EditText the_address) {
	if (the_dialog != null && the_address != null)
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
					myAddress = address;
					the_address.setText(address!=null?address.toString():"");
					the_dialog.dismiss();
				}
			}
		});
	}
}

/**
 * 
 */
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
	/**
	 * 
	 */
	private void setDateTimeEditTexts() {
		setFromDateTime();
		setToDateTime();
	}

	/**
	 * 
	 */
	private void setToDateTime() {
		final EditText fromButton = (EditText) findViewById(R.id.activity_create_event_dateTovalue);
		String dateTime = Utility.getDateTime(Calendar.getInstance());
		fromButton.setText(dateTime);
		fromButton.setOnClickListener(new OnClickListener() {						 
			@Override
			public void onClick(View arg0) {
				showDateTimePicker(fromButton);
				
			}
		});		
	}

	/**
	 * 
	 */
	private void setFromDateTime() {
		final EditText fromButton = (EditText) findViewById(R.id.activity_create_event_dateFromvalue);
		String dateTime = Utility.getDateTime(Calendar.getInstance());
		fromButton.setText(dateTime);
		fromButton.setOnClickListener(new OnClickListener() {						 
			@Override
			public void onClick(View arg0) {
				showDateTimePicker(fromButton);
				
			}
		});
		
	}


/** show date time picker*/
private void showDateTimePicker(final EditText the_button)
{
	Dialog dateDialog = new Dialog(this);
	dateDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
    LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    
    // Inflate and set the layout for the dialog
    // Pass null as the parent view because its going in the dialog layout
    dateDialog.setContentView(inflater.inflate(R.layout.date_time_picker_dialog, null));
    setDateTime(dateDialog, the_button, Calendar.getInstance());
    setDateDialogButtons(dateDialog, the_button);
    dateDialog.show();
    
}

private void setDateTime(Dialog the_dialog, EditText the_button, Calendar the_datetime)
{
	if (the_button != null && the_dialog != null)
	{
		DatePicker datepcker = (DatePicker) the_dialog.findViewById(R.id.date_time_picker_datepicker);
		TimePicker timepcker = (TimePicker) the_dialog.findViewById(R.id.date_time_picker_timepicker);
		Calendar cal;
		cal = the_datetime;
		if (cal != null)
		{
			datepcker.init(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), null);
			timepcker.setCurrentHour(cal.get(Calendar.HOUR_OF_DAY));
			timepcker.setCurrentMinute(cal.get(Calendar.MINUTE));
		}
	}
}

/** sets buttons
 * @param the_dialog
 */
private void setDateDialogButtons(final Dialog the_dialog, final EditText the_button)
{
	if (the_dialog != null && the_button != null)
	{
		Button cancel = (Button) the_dialog.findViewById(R.id.date_time_picker_cancel);
		cancel.setOnClickListener(new OnClickListener() {						 
			@Override
			public void onClick(View the_view) {
				the_dialog.dismiss();
			}
		});
		
		Button set = (Button) the_dialog.findViewById(R.id.date_time_picker_set);
		final DatePicker datepcker = (DatePicker) the_dialog.findViewById(R.id.date_time_picker_datepicker);
		final TimePicker timepcker = (TimePicker) the_dialog.findViewById(R.id.date_time_picker_timepicker);
		set.setOnClickListener(new OnClickListener() {						 
			@Override
			public void onClick(View the_view) {
				Calendar cal = Calendar.getInstance();
				int day = datepcker.getDayOfMonth();
				int month = datepcker.getMonth();
				int year = datepcker.getYear();
				int hour = timepcker.getCurrentHour();
				int min = timepcker.getCurrentMinute();
				cal.set(Calendar.DAY_OF_MONTH, day);
				cal.set(Calendar.MONTH, month);
				cal.set(Calendar.YEAR, year);
				cal.set(Calendar.HOUR_OF_DAY, hour);
				cal.set(Calendar.MINUTE, min);
				the_button.setText(Utility.getDateTime(cal));
				the_dialog.dismiss();
			}
		});
	}
}

	/** method that sets category*/
	private void setCategoryButton()
	{
		Button categoryButton = (Button) findViewById(R.id.activity_create_event_categoryvalue);
		categoryButton.setText(myTempSelectedCategory.getName().name());
	}
	public void createEvent(View the_view)
	{
		if (isInputValid())
		{
			getOtherParameters();
		}
	}
	
	
	/**
	 * 
	 */
	private void getOtherParameters() {
		EditText descET = (EditText) findViewById(R.id.activity_create_event_descriptionvalue);
		myDescription = (descET!=null && descET.getText() != null)?descET.getText().toString():null;
		EditText priceET = (EditText) findViewById(R.id.activity_create_event_pricevalue);
		myPrice = (priceET!=null && priceET.getText() != null)?priceET.getText().toString():null;
		getLatLng();
	}

	/**
	 
	 */
	private void saveEvent() {
				new AWSPut(this, 
				new Object[]{new PublicEvent
				(mySelectedCategory, myName, myDescription, myAddress, myFromDateTime, myToDateTime, myLatLng, myInfoLink, null,
						myPrice, false, UUIDGenerator.id(this), null,0,0,0)}).execute();
		
		
	}

	private boolean isInputValid()
	{
		return isNameValid() & isDateRangeValid() & isURLValid() ;//& isAddressLatLngValid();
	}
	
	/**
	 * @return
	 */
	private void getLatLng() {
		myProgressDialog = new ProgressDialog(this);
		myProgressDialog.setMessage(IDENTIFYINGLOCATION);
		myProgressDialog.setCanceledOnTouchOutside(false);
		LocalGeocoder geocoder = new LocalGeocoder(this);
		geocoder.registerRequestor(this);
		geocoder.execute(myAddress);		
	}

	/**
	 * @return
	 */
	private boolean isURLValid() {
		EditText urlval = (EditText) findViewById(R.id.activity_create_event_urlvalue);
		if (urlval != null)
		{
			String url = urlval.getText()!=null?urlval.getText().toString():null;
			if (url != null && url.length() > 0)
			{
				if (!URLUtil.isValidUrl(url))
				{
					urlval.setText("");
					urlval.setHint(INVALIDURLMESSAGE);
					urlval.setHintTextColor(Color.RED);
					return false;
				}
				myInfoLink = new Link(url);
			}
		}
		return true;
		}

	/**
	 * @return
	 */
	private boolean isDateRangeValid() {
			EditText fromDate = (EditText) findViewById(R.id.activity_create_event_dateFromvalue);
			String from = fromDate.getText().toString();
			myFromDateTime = Utility.getCalendarForReminder(from);
			EditText toDate = (EditText) findViewById(R.id.activity_create_event_dateTovalue);
			String to = toDate.getText().toString();
			myToDateTime = Utility.getCalendarForReminder(to);

			Calendar fromCal = Utility.getCalendar(from);
			Calendar toCal = Utility.getCalendar(to);
			
			if (fromCal != null && toCal != null)
			{
				
				if ((fromCal.get(Calendar.YEAR) == toCal.get(Calendar.YEAR)) && (fromCal.get(Calendar.MONTH) == toCal.get(Calendar.MONTH)) &&
						(fromCal.get(Calendar.DAY_OF_MONTH) == toCal.get(Calendar.DAY_OF_MONTH)) && 
						(fromCal.get(Calendar.HOUR_OF_DAY) == toCal.get(Calendar.HOUR_OF_DAY)) && 
						(fromCal.get(Calendar.MINUTE) == toCal.get(Calendar.MINUTE)))
				{
					return true;
				}
				else if ((fromCal.get(Calendar.YEAR) < toCal.get(Calendar.YEAR)) ||
						((fromCal.get(Calendar.YEAR) == toCal.get(Calendar.YEAR)) && (fromCal.get(Calendar.MONTH) < toCal.get(Calendar.MONTH)))||
						((fromCal.get(Calendar.YEAR) == toCal.get(Calendar.YEAR)) && (fromCal.get(Calendar.MONTH) == toCal.get(Calendar.MONTH)) &&
						(fromCal.get(Calendar.DAY_OF_MONTH) < toCal.get(Calendar.DAY_OF_MONTH)))||
						(((fromCal.get(Calendar.YEAR) == toCal.get(Calendar.YEAR)) && (fromCal.get(Calendar.MONTH) == toCal.get(Calendar.MONTH)) &&
						(fromCal.get(Calendar.DAY_OF_MONTH) == toCal.get(Calendar.DAY_OF_MONTH)) && (fromCal.get(Calendar.HOUR_OF_DAY) < toCal.get(Calendar.HOUR_OF_DAY))))||
						((fromCal.get(Calendar.YEAR) == toCal.get(Calendar.YEAR)) && (fromCal.get(Calendar.MONTH) == toCal.get(Calendar.MONTH)) &&
								(fromCal.get(Calendar.DAY_OF_MONTH) == toCal.get(Calendar.DAY_OF_MONTH)) && 
								(fromCal.get(Calendar.HOUR_OF_DAY) == toCal.get(Calendar.HOUR_OF_DAY)) && 
								(fromCal.get(Calendar.MINUTE) < toCal.get(Calendar.MINUTE))))
								{
									return true;
								}
			}
			Toast toast = Toast.makeText(getApplicationContext(), INVALIDDATERANGEMESSAGE, Toast.LENGTH_LONG);
			toast.setGravity(Gravity.CENTER, 40, 40);
			toast.show();
			return false;
	}

	/**
	 * @return
	 */

	private boolean isNameValid()
	{
		EditText nameVal = (EditText) findViewById(R.id.activity_create_event_namevalue);
		if (nameVal != null)
		{
			String name = nameVal.getText()!=null?nameVal.getText().toString():null;
			if (name == null || name.length() == 0 || INVALIDNAMEMESSAGE.equals(name))
			{
				nameVal.setText("");
				nameVal.setHint(INVALIDNAMEMESSAGE);
				nameVal.setHintTextColor(Color.RED);
				return false;
			}
			myName = name;
			return true;
		}
		return false;
	}
	

	public void selectCategories(View the_view)
	{
		    AlertDialog.Builder builder = new AlertDialog.Builder(this);
		    final CharSequence[] categories = Utility.getAllCategoryNames();
			final EventCategory[] allCategoryObjs = EventCategory.values();
			builder.setTitle("Select category");
		    builder.setSingleChoiceItems(categories, getSelectedCategoryPos(),  new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					myTempSelectedCategory = allCategoryObjs[which];
				}
			})
		    // Set the action buttons
		           .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
					@Override
		               public void onClick(DialogInterface dialog, int id) {
		            	  // setSelectedCategories(selectedItems);
						mySelectedCategory = myTempSelectedCategory;
						setButtonText();
		               }
		          	private void setButtonText()
					{
						if(mySelectedCategory != null && mySelectedCategory.getName()!=null&& mySelectedCategory.getName().name()!=null)
						{
							Button category = (Button) findViewById(R.id.activity_create_event_categoryvalue);
							category.setText(mySelectedCategory.getName().name());
						}
					}
		           })
		           .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
		               @Override
		               public void onClick(DialogInterface dialog, int id) {
		            	   myTempSelectedCategory = EventCategory.CATEGORYOTHER;
		            	   dialog.dismiss();
		               }
		           });			
			AlertDialog dialog = builder.create();
			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			dialog.show();
	}

	
	
/**
	 * @return
	 */
	private int getSelectedCategoryPos() {
		final EventCategory[] allCategoryObjs = EventCategory.values();
		if (allCategoryObjs != null)
		{
			for (int i = 0; i < allCategoryObjs.length;i++)
			{
				if (allCategoryObjs[i] == myTempSelectedCategory)
				{
					return i;
				}
			}
		}
		return 0;
	}



/* (non-Javadoc)
 * @see com.bitBusy.going.fromLocation.LocalGeocoderRequestor#acceptLatLng(com.google.android.gms.maps.model.LatLng)
 */
@Override
public void acceptLatLng(LatLng the_latLng) {
	myLatLng = the_latLng;
	closeProgressDialog();
	allParametersSet();
}

/**
 * 
 */
private void closeProgressDialog() {
	if (myProgressDialog != null &&  myProgressDialog.isShowing())
	{
		myProgressDialog.dismiss();
	}	
}

/**
 * 
 */
private void allParametersSet() {
	if (myLatLng != null)
	{
		saveEvent();
	}
	else
	{
		showFailDialog(ADDRESSMAPFAIL);
	}
}

/**
 * @param addressmapfail2
 */
private void showFailDialog(String the_msg) {
	AlertDialog.Builder builder = new AlertDialog.Builder(this);
	builder.setTitle(CREATIONFAIL);
	builder.setMessage(the_msg);
	builder.setPositiveButton("OK",new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog,int id) {
			dialog.dismiss();
		}
	  });
	builder.create().show();
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
