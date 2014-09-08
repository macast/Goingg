/**
 * 
 */
package com.bitBusy.goingg.dialog;

import java.util.ArrayList;
import java.util.Calendar;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.bitBusy.goingg.activity.ActivityMapViewHome;
import com.bitBusy.goingg.events.Address;
import com.bitBusy.goingg.events.EventCategory;
import com.bitBusy.goingg.fromLocation.FromLocation;
import com.bitBusy.goingg.settings.QueryFilter;
import com.bitBusy.goingg.settings.SearchSetting;
import com.bitBusy.goingg.utility.Utility;
import com.bitBusy.goingg.R;

/**
 * @author SumaHarsha
 *
 */
public class SearchSettingsDialog {
	
	public static final String ALL = "all";
	
	public static final String MULTIPLE = "multiple";
	
	public static final String NONE = "none";

	public static final String EMPTYADDRESS = "Please enter at least one address field.";
	
	/** time splitter*/
	public static final String TIMESPLITTER = ":";
	
	public enum DISPLAYOPTIONS{ EVENTS, PLACES, BOTH};
	
	private static final String INCORRECTCATEGORY = "Please select at least one event category.";
	
	private static final String INCORRECTDATERANGE = "From date cannot be later than to date.";
	
	private DISPLAYOPTIONS myDisplayState;
	
	/** context*/
	private Context myContext;
		
	/** dialog*/
	private Dialog myDialog;
	
	/** listeners*/
	private ArrayList<SearchSettingListener> myListeners;

	/** query filter*/
	private QueryFilter myQueryFilter;
	
	private QueryFilter myModifiedFilter;
	
	/** categories chosen*/
	private EventCategory[] myCategories;

	protected boolean[] myTempSelectedCategories;
	
	public SearchSettingsDialog(Context the_context, SearchSetting the_setting)
	{
		myContext = the_context;
		if (the_setting != null)
		{
			myQueryFilter = the_setting.getQueryFilter();
			myCategories = myQueryFilter!=null?myQueryFilter.getCategories():null;
			myDisplayState = the_setting.getDisplayState();
		}
		
	}
	/**
	 * throws open dialog
	 */
	public void openDialog()
	{
		myDialog = new Dialog(myContext);
		myDialog.setCanceledOnTouchOutside(false);
		myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
	    LayoutInflater inflater = (LayoutInflater) myContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    
	    // Inflate and set the layout for the dialog
	    // Pass null as the parent view because its going in the dialog layout
	    myDialog.setContentView(inflater.inflate(R.layout.search_settings_dialog, null));
	    setCategory();
	    setDates();
	    setFromAddress();
	    setButtons();
	    setSpinner();
	    setValueListeners();
	    myDialog.show();

	}
	
	/** method that sets category*/
	private void setCategory()
	{
		Button categoryButton = (Button) myDialog.findViewById(R.id.search_settings_categoryValue);
		if (myQueryFilter != null && myQueryFilter.getCategories() != null)
		{
			EventCategory categories[] = myQueryFilter.getCategories();
			if (categories.length ==  EventCategory.values().length)
			{
				categoryButton.setText(ALL);
			}
			else if (categories.length == 1)
			{
				categoryButton.setText(categories[0]!=null?categories[0].getName().toString():"");
			}
			else 
			{
				categoryButton.setText(MULTIPLE);
			}
		}
	}
	
	
	/** sets date*/
	private void setDates()
	{
		setFromDate();
		setToDate();
	}

	/** sets from date/time*/
	private void setFromDate()
	{
		if (myQueryFilter != null)
		{
			final EditText fromButton = (EditText) myDialog.findViewById(R.id.search_settings_dateFromvalue);
			Calendar from = myQueryFilter.getStartDateTime();
			String dateTime = Utility.getDate(from);
			fromButton.setText(dateTime);
			fromButton.setOnClickListener(new OnClickListener() {						 
				@Override
				public void onClick(View arg0) {
					showDateTimePicker(fromButton);
					
				}
			});
			
		}
	}
	
	/** show date time picker*/
	private void showDateTimePicker(final EditText the_button)
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
						the_button.setText(String.format(Utility.ZEROPADDING,monthOfYear+1).
								concat(Utility.DATESPLITTER).concat(String.format(Utility.ZEROPADDING,dayOfMonth)).
								concat(Utility.DATESPLITTER).concat(String.valueOf(year)));
						
					}
				}, year, month, day);
		dialog.getDatePicker().setCalendarViewShown(true);
		dialog.getDatePicker().setSpinnersShown(false);
		dialog.show();	
	    
	}
	

	
	/** sets to date/time */
	private void setToDate()
	{
		if (myQueryFilter != null)
		{
			final EditText toButton = (EditText) myDialog.findViewById(R.id.search_settings_dateTovalue);
			Calendar to = myQueryFilter.getEndDateTime();
			String dateTime = Utility.getDate(to);
			toButton.setText(dateTime);
			toButton.setOnClickListener(new OnClickListener() {						 
				@Override
				public void onClick(View arg0) {
					showDateTimePicker(toButton);
					
				}
			});
		}
	}
	

		
	private void setFromAddress()
	{
			final EditText address = (EditText) myDialog.findViewById(R.id.search_settings_fromLocValue);
			address.setText(FromLocation.getInstance().getFromAdd().toString());
			address.setOnClickListener(new OnClickListener() {						 
				@Override
				public void onClick(View arg0) {
					openEnterAddressDialog(address);
					
				}
			});

				
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
						FromLocation.getInstance().setFromAdd(address);
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
		Toast toast = Toast.makeText(myContext.getApplicationContext(), EMPTYADDRESS, Toast.LENGTH_LONG);
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
	/** sets buttons*/
	private void setButtons()
	{
		setShowRadioButtons();
		setSearchButton();
		setCancelButton();
	}
	
	/**
	 * 
	 */
	private void setShowRadioButtons() {
		if (myDialog != null)
		{
			if (myDisplayState == DISPLAYOPTIONS.BOTH)
			{
				RadioButton both = (RadioButton) myDialog.findViewById(R.id.search_settings_radioBoth);
				both.setChecked(true);
			}
			else if (myDisplayState == DISPLAYOPTIONS.EVENTS)
			{
				RadioButton events = (RadioButton) myDialog.findViewById(R.id.search_settings_radioEvents);
				events.setChecked(true);		
			}
			else if (myDisplayState == DISPLAYOPTIONS.PLACES)
			{
				RadioButton places = (RadioButton) myDialog.findViewById(R.id.search_settings_radioPlaces);
				places.setChecked(true);	
			}
		}
	}
	/** sets search button*/
	private void setSearchButton()
	{
		if (myDialog != null)
		{
			Button search = (Button) myDialog.findViewById(R.id.search_settings_search);
			search.setOnClickListener(new OnClickListener() {						 
				@Override
				public void onClick(View the_view) {
					
					boolean categoryOK = isMoreThanZeroCategoryChosen() ;
					boolean dateOK = isDateRangeValid();
					if(categoryOK && dateOK)
					{
						setQueryFilter();
						notifyListeners();
						myDialog.dismiss();
					}
					else if (!categoryOK)
					{
						Toast toast = Toast.makeText(myContext.getApplicationContext(), INCORRECTCATEGORY, Toast.LENGTH_LONG);
						toast.setGravity(Gravity.CENTER, 0, 0);
						toast.show();
					}	
					else if (!dateOK)
					{
						Toast toast = Toast.makeText(myContext.getApplicationContext(), INCORRECTDATERANGE, Toast.LENGTH_LONG);
						toast.setGravity(Gravity.CENTER, 0, 0);
						toast.show();
					}

				}
			});
		}		
	}
	
	/**
	 * @return
	 */
	protected boolean isDateRangeValid() {
		EditText fromDate = (EditText) myDialog.findViewById(R.id.search_settings_dateFromvalue);
		String from = fromDate.getText().toString();
		
		EditText toDate = (EditText) myDialog.findViewById(R.id.search_settings_dateTovalue);
		String to = toDate.getText().toString();
	
		Calendar fromCal = Utility.getCalendarFromOnlyDate(from);
		Calendar toCal = Utility.getCalendarFromOnlyDate(to);
		
		if (fromCal != null && toCal != null)
		{
			
			if (((fromCal.get(Calendar.YEAR) == toCal.get(Calendar.YEAR)) && (fromCal.get(Calendar.MONTH) == toCal.get(Calendar.MONTH)) &&
					(fromCal.get(Calendar.DAY_OF_MONTH) == toCal.get(Calendar.DAY_OF_MONTH))))
			{
				return true;
			}
			else if ((fromCal.get(Calendar.YEAR) < toCal.get(Calendar.YEAR)) ||
					((fromCal.get(Calendar.YEAR) == toCal.get(Calendar.YEAR)) && (fromCal.get(Calendar.MONTH) < toCal.get(Calendar.MONTH)))||
					((fromCal.get(Calendar.YEAR) == toCal.get(Calendar.YEAR)) && (fromCal.get(Calendar.MONTH) == toCal.get(Calendar.MONTH)) &&
					(fromCal.get(Calendar.DAY_OF_MONTH) < toCal.get(Calendar.DAY_OF_MONTH))))
				{
					return true;
				}
		}
		
		return false;
	}
	/** set query filter*/
	private void setQueryFilter()
	{
		if (myDialog != null)
		{
			EditText fromDate = (EditText) myDialog.findViewById(R.id.search_settings_dateFromvalue);
			String from = fromDate.getText().toString();
			
			EditText toDate = (EditText) myDialog.findViewById(R.id.search_settings_dateTovalue);
			String to = toDate.getText().toString();
			
			Spinner spinner = (Spinner) myDialog.findViewById(R.id.search_settings_spinner);
			int miles = (Integer) spinner.getSelectedItem();
		
			myDisplayState = getDisplayState();
			
			myModifiedFilter =  new QueryFilter(myCategories, setStartCalendarTime(Utility.getCalendarFromOnlyDate(from)),
					setEndCalendarTime(Utility.getCalendarFromOnlyDate(to)), miles );
		}
	}
	
	
	/**
	 * @param calendarFromOnlyDate
	 * @return
	 */
	private Calendar setEndCalendarTime(Calendar the_calendar) {
		if (the_calendar != null)
		{
			the_calendar.set(Calendar.HOUR_OF_DAY, ActivityMapViewHome.DEFAULTENDHOUR);
			the_calendar.set(Calendar.MINUTE, ActivityMapViewHome.DEFAULTENDMIN);
			return the_calendar;
		}		
		return null;
	}
	/**
	 * @param calendarFromOnlyDate
	 * @return
	 */
	private Calendar setStartCalendarTime(Calendar the_calendar) {
		if (the_calendar != null)
		{
			the_calendar.set(Calendar.HOUR_OF_DAY, ActivityMapViewHome.DEFAULTSTARTHOUR);
			the_calendar.set(Calendar.MINUTE, ActivityMapViewHome.DEFAULTSTARTMIN);
			return the_calendar;
		}
		return null;
	}
	private DISPLAYOPTIONS getDisplayState()
	{
		if (myDialog != null)
		{
			RadioGroup group = (RadioGroup) myDialog.findViewById(R.id.search_settings_showValue);
			if (group != null)
			{
				int selected = group.getCheckedRadioButtonId();
				if (selected == R.id.search_settings_radioBoth)
				{
					return DISPLAYOPTIONS.BOTH;
				}
				else if (selected == R.id.search_settings_radioEvents)
				{
					return DISPLAYOPTIONS.EVENTS;
				}
				else if (selected == R.id.search_settings_radioPlaces)
				{
					return DISPLAYOPTIONS.PLACES;
				}
			}
		}
		return DISPLAYOPTIONS.BOTH;
	}
	
	private boolean isMoreThanZeroCategoryChosen() 
	{
		Button category = (Button) myDialog.findViewById(R.id.search_settings_categoryValue);
		if (category != null && category.getText() != null && category.getText().toString() != null)
		{
			String selection = category.getText().toString();
			if (selection != null && NONE.equals(selection))
			{
				return false;
			}
			return true;
		}
		
		return false;
	}
	/** sets cancel button*/
	private void setCancelButton()
	{
		if (myDialog != null)
		{
			Button cancel = (Button) myDialog.findViewById(R.id.search_settings_cancel);
			cancel.setOnClickListener(new OnClickListener() {						 
				@Override
				public void onClick(View the_view) {
			myDialog.dismiss();
				}
			});
		}	}
	
	/** sets spinner*/
	private void setSpinner()
	{
		if (myDialog != null)
		{
			Spinner spinner = (Spinner) myDialog.findViewById(R.id.search_settings_spinner);
			ArrayList<Integer> list = new ArrayList<Integer>();
			addToList(list);
			ArrayAdapter<Integer> dataAdapter = new ArrayAdapter<Integer>(myContext, android.R.layout.simple_spinner_item, list);
			dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spinner.setAdapter(dataAdapter);
			spinner.setSelection(myQueryFilter!=null?myQueryFilter.getDistance()/10:1);
		}
	}
	
	/** adds nos to list*/
	private void addToList(ArrayList<Integer> the_list)
	{
		for (int i = 0; i < 15000; i=i+10)
		{
			the_list.add(i);
		}
	}
	
	/** sets value button listener*/
	private void setValueListeners()
	{
		setCategoryValueListener();
	}
	
	
	/** Sets cat value listener*/
	private void setCategoryValueListener()
	{
		if (myDialog != null)
		{
			Button category = (Button) myDialog.findViewById(R.id.search_settings_categoryValue);
			category.setOnClickListener(new OnClickListener() {						 
				@Override
				public void onClick(View the_view) {
					openCategoryDialog();
				}

				private void openCategoryDialog() {
					 final ArrayList<Integer> selectedItems = new ArrayList<Integer>();  // Where we track the selected items
					    AlertDialog.Builder builder = new AlertDialog.Builder(myContext);
					    final CharSequence[] categories = Utility.getAllCategoryNames();
						final EventCategory[] allCategoryObjs = EventCategory.values();
						if (myTempSelectedCategories == null)
						{
							myTempSelectedCategories = getSelectedCategories(categories);
						}
					    for (int i = 0; i < myTempSelectedCategories.length; i++)
					    {
					    	if (myTempSelectedCategories[i] == true)
					    	{
					    		selectedItems.add(i);
					    	}
					    }
					    builder.setTitle("Select category(-ies)");
					    // Set the dialog title
					    builder.setMultiChoiceItems(categories, myTempSelectedCategories,
					                      new DialogInterface.OnMultiChoiceClickListener() {
					               @Override
					               public void onClick(DialogInterface dialog, int which,
					                       boolean isChecked) {
					            	   if (isChecked) {
					  		                	   selectedItems.add(which);
					                   } else if (selectedItems.contains(which)) {
						                	   selectedItems.remove(Integer.valueOf(which));
					                   }
					               }
					           })
					    // Set the action buttons
					           .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
								@Override
					               public void onClick(DialogInterface dialog, int id) {
					            	   setSelectedCategories(selectedItems);
					               }
					              /** sets selected categories*/
								private void setSelectedCategories(
										ArrayList<Integer> selectedItems) {
										if (selectedItems != null)
										{
											int index = 0;
											myCategories = new EventCategory[selectedItems.size()];
											for (int i = 0; i < selectedItems.size(); i++)
											{
												String name = categories[selectedItems.get(i)].toString();
												if(addCategoryToField(name, index))
												{
													index++;
													if (index == selectedItems.size())
													{
														break;
													}
												}
											}
											setButtonText();
										}
								}
								private void setButtonText()
								{
									if(myCategories != null)
									{
										Button category = (Button) myDialog.findViewById(R.id.search_settings_categoryValue);
										if (myCategories.length == 1)
										{
											category.setText(myCategories[0].getName().name());
										}
										else if (myCategories.length == allCategoryObjs.length)
										{
											category.setText(ALL);
										}
										else if (myCategories.length > 1)
										{
											category.setText(MULTIPLE);
										}
										else
										{
											category.setText(NONE);
										}
									}
								}
								/** Adds category to field*/
								private boolean addCategoryToField(String the_name, int the_index) {
									for (EventCategory category:allCategoryObjs)
									{
										if (category!= null && category.getName()!=null && category.getName().name()!=null && 
												category.getName().name().equals(the_name))
										{
											myCategories[the_index] = category;
											return true;
										}
									}
									return false;
								}
					           })
					           .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
					               @Override
					               public void onClick(DialogInterface dialog, int id) {
					            	   myTempSelectedCategories = null;
					            	   dialog.dismiss();
					               }
					           });			
						AlertDialog dialog = builder.create();
						dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
						dialog.show();
				}

				/** returns selected categories*/
				private boolean[] getSelectedCategories(
						CharSequence[] the_categoryNames) {
					if (the_categoryNames  != null)
					{
						boolean [] selectedCategories = new boolean[the_categoryNames.length];
						if  (myQueryFilter != null)
						{
							EventCategory[] categories = myQueryFilter.getCategories();
							for (int i = 0; i < the_categoryNames.length;i++)
							{
								String catName = the_categoryNames[i]!=null?the_categoryNames[i].toString():null;
								for (int j = 0; j < (categories!=null?categories.length:0);j++)
								{								
									EventCategory category = categories[j];
									if (category != null && category.getName()!= null && category.getName().name() != null && 
											category.getName().name().equals(catName))
									{
										selectedCategories[i] = true;
									}
								}
							}
						}
						return selectedCategories;
						}
					return null;
					}
				
			});
		}
	}
	
	/** register listener
	 * @param the_listener
	 */
		public void registerListener(SearchSettingListener the_listener)
		{
		if (myListeners == null)
		{
			myListeners = new ArrayList<SearchSettingListener>();
		}
		myListeners.add(the_listener);
		}
		
		
		/** notify listeners*/
		private void notifyListeners()
		{
			if (myListeners != null)
			{
				for (SearchSettingListener listener:myListeners)
				{
					listener.onSearch(new SearchSetting(myModifiedFilter, myDisplayState));
				}
			}
		}

}

