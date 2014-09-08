/**
 * 
 */
package com.bitBusy.goingg.dialog;

import java.util.ArrayList;
import java.util.Calendar;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bitBusy.goingg.events.Address;
import com.bitBusy.goingg.fromLocation.FromLocation;
import com.bitBusy.goingg.settings.DirectionSetting;
import com.bitBusy.goingg.utility.Utility;
import com.bitBusy.goingg.webServices.EventsData.Directions;
import com.bitBusy.goingg.R;

/**
 * @author SumaHarsha
 *
 */
public class DirectionsSettingsDialog 
{

	/** date splitter*/
	private static final String DATESPLITTER = "/";
	
	/** time splitter*/
	private static final String TIMESPLITTER = ":";
	/** context*/
	private Context myContext;
	
	/** to address*/
	private Address myToAddress;
	
	/** mode of transport*/
	private String myMode;
	
	/** dialog*/
	private Dialog myDialog;
	
	/** listeners*/
	private ArrayList<DirectionDialogListener> myListeners;

	/** is highway avoided*/
	private boolean myHighwayAvoided;

	/** is toll avoided*/
	private boolean myTollAvoided;

	/** is depart set*/
	private boolean myDepartSet;

	/** date time*/
	private Calendar myDateTime;
	
	/**
	 * parameterized constructor
	 * @param the_context
	 * @param the_fromAddress
	 * @param the_toAddress
	 * @param the_mode
	 * @param myDateTime 
	 * @param myDepartSet 
	 * @param myTollAvoided 
	 * @param myHighwayAvoided 
	 */	
	public DirectionsSettingsDialog(Context the_context, Address the_toAddress, String the_mode,
			boolean the_HighwayAvoided, boolean the_TollAvoided, boolean the_DepartSet, Calendar the_dateTime)
	{
		myContext = the_context;
		myToAddress = the_toAddress;
		myMode = the_mode;
		myHighwayAvoided = the_HighwayAvoided;
		myTollAvoided = the_TollAvoided;
		myDepartSet= the_DepartSet;
		myDateTime = the_dateTime;
	}
	
	/**
	 * throws open dialog
	 */
	public void openDialog()
	{
		myDialog = new Dialog(myContext);
		myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
	    LayoutInflater inflater = (LayoutInflater) myContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    
	    // Inflate and set the layout for the dialog
	    // Pass null as the parent view because its going in the dialog layout
	    myDialog.setContentView(inflater.inflate(R.layout.directions_settings_dialog, null));
	    setAddresses();
	    setRadioButtons();
	    setButtons();
	    myDialog.show();

	}
	
	/** method to set addresses*/
	private void setAddresses()
	{
		setFromAddress();
		setToAddress();
	}

	/** sets from address*/
	private void setFromAddress() {
	if (myDialog != null)
	{
		final EditText from = (EditText) myDialog.findViewById(R.id.directions_settings_startValue);
		from.setText(FromLocation.getInstance().getFromAdd().toString());
		from.setCursorVisible(false);
		from.setOnClickListener(new OnClickListener() {						 
			@Override
			public void onClick(View the_view) {
				openEnterAddressDialog(from);
			}
		});
	}
}
	
	private void openEnterAddressDialog(EditText the_address) {
		Dialog dialog = new Dialog(myContext);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
	    LayoutInflater inflater = (LayoutInflater) myContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    
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
						the_address.setText(address!=null?address.toString():"");
						the_dialog.dismiss();
					}
				}
			});
		}
	}
	
	private void toastEmptyAddress() {
		Toast toast = Toast.makeText(myContext.getApplicationContext(), SearchSettingsDialog.EMPTYADDRESS, Toast.LENGTH_LONG);
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
	/** sets to address	 */
	private void setToAddress() {
		if (myDialog != null && myToAddress != null)
		{
			TextView from = (TextView) myDialog.findViewById(R.id.directions_settings_destinationValue);					
			from.setText(myToAddress.toString());
		}
		
	}
	
	/** sets radio buttons*/
	private void setRadioButtons()
	{
		if (myMode != null && myDialog != null)
		{
			setRadioListeners();
			if (myMode.equals(Directions.MODE_WALKING))
			{
				RadioButton walkButton = (RadioButton) myDialog.findViewById(R.id.directions_settings_mode_walk);
				walkButton.setChecked(true);
				setCheckboxes(View.GONE);
				setDateTime(View.GONE);
			}
			else if (myMode.equals(Directions.MODE_TRANSIT))
			{
				RadioButton transitButton = (RadioButton) myDialog.findViewById(R.id.directions_settings_mode_publictransport);
				transitButton.setChecked(true);	
				setCheckboxes(View.GONE);
				setDateTime(View.VISIBLE);
				if (myDepartSet)
				{
					RadioButton depart = (RadioButton) myDialog.findViewById(R.id.directions_settings_depart);
					depart.setChecked(true);
				}			
				else
				{
					RadioButton arrive = (RadioButton) myDialog.findViewById(R.id.directions_settings_arrive);
					arrive.setChecked(true);
				
				}
			}
			else if (myMode.equals(Directions.MODE_BIKING))
			{
				RadioButton bikeButton = (RadioButton) myDialog.findViewById(R.id.directions_settings_mode_bike);
				bikeButton.setChecked(true);		
				setCheckboxes(View.GONE);
				setDateTime(View.GONE);
			}
			else
			{
				setCheckboxes(View.VISIBLE);
				setDateTime(View.GONE);
			}
			
		}
	}
	
	/** method to control visibility of date time components*/
	private void setDateTime(int the_visibility)
	{
		RadioGroup dateTime = (RadioGroup) myDialog.findViewById(R.id.directions_settings_dateTimeGroup);
		dateTime.setVisibility(the_visibility);
		final Button date = (Button)myDialog.findViewById(R.id.direction_settings_date);
		date.setVisibility(the_visibility);
		date.setOnClickListener(new OnClickListener()
		{
			public void	onClick(View the_view)
			{
				showDate(date);
			}
		});
		final Button time = (Button) myDialog.findViewById(R.id.direction_settings_time);
		time.setVisibility(the_visibility);
		time.setOnClickListener(new OnClickListener()
		{
			public void	onClick(View the_view)
			{
				showTime(time);
			}
		});
	
		if (the_visibility == View.VISIBLE)
		{
			setCurrentDateTime(date,time);
		}
	}
	
	/** show date
	 * @param the_button
	 */
	private void showDate(final Button the_button)
	{
		Calendar calendar = Calendar.getInstance();
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		int month = calendar.get(Calendar.MONTH);
		int year = calendar.get(Calendar.YEAR);
		DatePickerDialog dialog = new DatePickerDialog(myContext, 
				new DatePickerDialog.OnDateSetListener() {
					
					@Override
					public void onDateSet(DatePicker view, int year, int monthOfYear,
							int dayOfMonth) {
						the_button.setText(String.valueOf(monthOfYear).concat(DATESPLITTER).concat(String.valueOf(dayOfMonth)).
								concat(String.valueOf(year)));
						
					}
				}, year, month, day);
		dialog.show();		
	}
	
	/** show time
	 * @param the_button
	 */
	private void showTime(final Button the_button)
	{
		Calendar calendar = Calendar.getInstance();
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int min = calendar.get(Calendar.MINUTE);
		TimePickerDialog dialog = new TimePickerDialog(myContext, 
				new TimePickerDialog.OnTimeSetListener() {
					
					@Override
					public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
						the_button.setText(String.valueOf(hourOfDay).concat(TIMESPLITTER).concat(String.format(
								Utility.ZEROPADDING,minute)));
				
					}
				}, hour, min, true);
					
				dialog.show();		
	}
	
	/**
	 * sets current date and time
	 * @param date
	 * @param time
	 */
	private void setCurrentDateTime(Button the_date, Button the_time) {
		int day;
		int month;
		int year;
		int hour;
		int min;
		if (myDateTime == null)
		{
			Calendar calendar = Calendar.getInstance();
			day = calendar.get(Calendar.DAY_OF_MONTH);
			month = calendar.get(Calendar.MONTH);
			year = calendar.get(Calendar.YEAR);
			hour = calendar.get(Calendar.HOUR_OF_DAY);
			min = calendar.get(Calendar.MINUTE);
		}
		else
		{
			day = myDateTime.get(Calendar.DAY_OF_MONTH);
			month = myDateTime.get(Calendar.MONTH);
			year = myDateTime.get(Calendar.YEAR);
			hour = myDateTime.get(Calendar.HOUR_OF_DAY);
			min = myDateTime.get(Calendar.MINUTE);
		}
		the_date.setText(String.valueOf(month).concat(DATESPLITTER).concat(String.valueOf(day)).concat(DATESPLITTER).concat(String.valueOf(year)));
		the_time.setText(String.valueOf(hour).concat(TIMESPLITTER).concat(String.valueOf(min)));

}

	/** sets radio listeners*/
	private void setRadioListeners()
	{
		RadioButton walkButton = (RadioButton) myDialog.findViewById(R.id.directions_settings_mode_walk);
		walkButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setCheckboxes(View.GONE);
				setDateTime(View.GONE);
			}
		});
		
		RadioButton transitButton = (RadioButton) myDialog.findViewById(R.id.directions_settings_mode_publictransport);
		transitButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setCheckboxes(View.GONE);
				setDateTime(View.VISIBLE);

			}
		});
		
		RadioButton bikeButton = (RadioButton) myDialog.findViewById(R.id.directions_settings_mode_bike);
		bikeButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setCheckboxes(View.GONE);
				setDateTime(View.GONE);

			}
		});
		RadioButton driveButton = (RadioButton) myDialog.findViewById(R.id.directions_settings_mode_drive);
		driveButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setCheckboxes(View.VISIBLE);
				setDateTime(View.GONE);

			}
		});
	
	


	}
	/**
	 * sets state of checkboxes
	 * @param visible
	 */
	private void setCheckboxes(int the_visibility) {
		if (myDialog != null)
		{
			CheckBox highway = (CheckBox) myDialog.findViewById(R.id.directions_settings_avoidHighway);
			highway.setVisibility(the_visibility);
			CheckBox toll = (CheckBox) myDialog.findViewById(R.id.directions_settings_avoidToll);
			toll.setVisibility(the_visibility);
			
			if (the_visibility == View.VISIBLE)
			{
				if (myHighwayAvoided)
				{
					highway.setChecked(true);
				}
				if (myTollAvoided)
				{
					toll.setChecked(true);
				}
			}
		}
			
	}

	/**
	 * sets buttons
	 */
	private void setButtons() {
		setSetButton();
		setCancelButton();
	}

	/**
	 * sets the set button
	 */
	private void setSetButton() {
		if (myDialog != null)
		{

			Button set = (Button) myDialog.findViewById(R.id.directions_settings_set);
			set.setOnClickListener(new OnClickListener() {						 
			@Override
			public void onClick(View the_view) {
				
				DirectionSetting setting;
				boolean isHighwayAvoided = false;
				boolean isTollAvoided = false;
				boolean isDepartSet = false;
				Calendar calendarSet = null;
				
				
				EditText fromAddText = (EditText) myDialog.findViewById(R.id.directions_settings_startValue);
				Address fromAdd = fromAddText.getText()!=null?
						new Address(fromAddText.getText().toString(),null, null, null,null):null;
			
				
				int driveButton = R.id.directions_settings_mode_drive;
				int walkButton = R.id.directions_settings_mode_walk;
				int transitButton = R.id.directions_settings_mode_publictransport;
				
				RadioGroup modeGroup = (RadioGroup) myDialog.findViewById(R.id.directions_settings_modeGroup);
				int checkedId = modeGroup.getCheckedRadioButtonId();
				
				if (driveButton == checkedId)
				{
					
					CheckBox highway = (CheckBox) myDialog.findViewById(R.id.directions_settings_avoidHighway);
					isHighwayAvoided = highway.isChecked();
					CheckBox toll = (CheckBox) myDialog.findViewById(R.id.directions_settings_avoidToll);
					isTollAvoided = toll.isChecked();
					setting = new DirectionSetting(fromAdd,Directions.MODE_DRIVING,isHighwayAvoided, isTollAvoided, isDepartSet, calendarSet);
				}
				else if (walkButton == checkedId)
				{
					setting = new DirectionSetting
							(fromAdd,Directions.MODE_WALKING,isHighwayAvoided, isTollAvoided, isDepartSet, calendarSet);
				}
				else if (transitButton == checkedId)
				{
					RadioButton depart = (RadioButton) myDialog.findViewById(R.id.directions_settings_depart);
					isDepartSet = depart.isChecked();
					Button dateButton = (Button) myDialog.findViewById(R.id.direction_settings_date);
					String date = dateButton.getText()!=null?dateButton.getText().toString():null;
					Button timeButton = (Button) myDialog.findViewById(R.id.direction_settings_time);
					String time = timeButton.getText()!=null?timeButton.getText().toString():null;
					calendarSet = getCalendarObject(date, time);
					setting = new DirectionSetting
							(fromAdd,Directions.MODE_TRANSIT,isHighwayAvoided, isTollAvoided, isDepartSet, calendarSet);
				}
				else
				{
					setting = new DirectionSetting
							(fromAdd,Directions.MODE_BIKING,isHighwayAvoided, isTollAvoided, isDepartSet, calendarSet);
		
				}
			
				if (myListeners != null)
				{
					for (DirectionDialogListener listener: myListeners)
					{
						listener.onSet(setting);
					}
				}
				myDialog.dismiss();

			}

			private Calendar getCalendarObject(String the_date, String the_time) {
				if (the_date != null && the_time != null)
				{
					Calendar calendar = Calendar.getInstance();
					String[] dateComponents = the_date.split(DATESPLITTER);
					String[] timeComponents = the_time.split(TIMESPLITTER);
					if (dateComponents.length == 3 && timeComponents.length == 2)
					{
						int month = Integer.parseInt(dateComponents[0]);
						int day = Integer.parseInt(dateComponents[1]);
						int year = Integer.parseInt(dateComponents[2]);
						int hour = Integer.parseInt( timeComponents[0]);
						int min = Integer.parseInt(timeComponents[1]);
						calendar.set(Calendar.MONTH, month);
						calendar.set(Calendar.DAY_OF_MONTH, day);
						calendar.set(Calendar.YEAR, year);
						calendar.set(Calendar.HOUR_OF_DAY, hour);
						calendar.set(Calendar.MINUTE, min);
						return calendar;
					}					
				}
				return null;
			}
 		});
    	}
	}
	
	/** sets cancel button*/
	private void setCancelButton()
	{
		if (myDialog != null)
		{
			Button cancel = (Button) myDialog.findViewById(R.id.directions_settings_cancel);
			cancel.setOnClickListener(new OnClickListener() {						 
				@Override
				public void onClick(View the_view) {
			myDialog.dismiss();
				}
			});
		}
	}

	/**
	 * method to registerlistener
	 * @param the_listener
	 */
	public void registerListener(DirectionDialogListener the_listener)
	{
		if (myListeners == null)
		{
			myListeners = new ArrayList<DirectionDialogListener>();
		}
		myListeners.add(the_listener);
	}
}