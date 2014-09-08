package com.bitBusy.goingg.activity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;
import android.widget.SimpleAdapter;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bitBusy.goingg.dialog.SearchSettingsDialog;
import com.bitBusy.goingg.events.Address;
import com.bitBusy.goingg.events.EventCategory;
import com.bitBusy.goingg.utility.Utility;
import com.bitBusy.goingg.R;

public class ActivityCreatePrivateEvent extends Fragment {

	private static final String ESCAPECHAR = "\\COMMA\\";
	private static final String NAME = "Name";
	private static final String PHONE = "Phone";
	protected static final String TYPE = "Type";
	private static final String INVALIDSENDER = "sender name must have a value";
	private static final String INVALIDINVITEES = "specify at least one invitee";
	private EventCategory myTempSelectedCategory = EventCategory.CATEGORYOTHER;
	private EventCategory mySelectedCategory = EventCategory.CATEGORYOTHER;
	private ArrayList<ContactsMap> myContacts;
	private View myParentView;
	private Context myContext;
	private Address myAddress;
	private String mySender;
	private String myEventName;
	private List<String> myInvitees;
	private Calendar myFromDateTime;
	private Calendar myToDateTime;
//	private ArrayList<Contact> mySelectedContacts;
	private Cursor myCursor;
	
	  @Override
	  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		 myParentView = inflater.inflate(R.layout.activity_create_private_event, container, false);
		 myContext = getActivity();
		setCategoryButton();
		setDateTimeEditTexts();
		setAddressEditText();
		setAutoCompleteTV();
		return myParentView;
	}

/**
	 * 
	 */
	private void setAutoCompleteTV() 
	{
			new AutoComplCntctsTVSetup().execute();	    
	}
	
	private void setAddressEditText()
	{
		if (myParentView != null)
		{
			final EditText address = (EditText) myParentView.findViewById(R.id.activity_create_private_event_addressvalue);
			address.setOnClickListener(new OnClickListener() {						 
				@Override
				public void onClick(View arg0) {
					openEnterAddressDialog(address);
					
				}
			});
		}		
	}
	
	private void openEnterAddressDialog(EditText the_address) 
	{
		if (myParentView != null && myContext != null)
		{
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
	private void toastEmptyAddress()
	{
		if (myContext != null)
		{
			Toast toast = Toast.makeText(myContext.getApplicationContext(), SearchSettingsDialog.EMPTYADDRESS, Toast.LENGTH_LONG);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();	
		}
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
		if (myParentView != null)
		{
			final EditText fromButton = (EditText) myParentView.findViewById(R.id.activity_create_private_event_dateTovalue);
			String dateTime = Utility.getDateTime(Calendar.getInstance());
			fromButton.setText(dateTime);
			fromButton.setOnClickListener(new OnClickListener() {						 
				@Override
				public void onClick(View arg0) {
					showDateTimePicker(fromButton);
					
				}
			});		
		}
	}
	
	/**
	 * 
	 */
	private void setFromDateTime() 
	{
		if (myParentView != null)
		{
			final EditText fromButton = (EditText) myParentView.findViewById(R.id.activity_create_private_event_dateFromvalue);
			String dateTime = Utility.getDateTime(Calendar.getInstance());
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
		if (myContext != null)
		{
			Dialog dateDialog = new Dialog(myContext);
			dateDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			LayoutInflater inflater = (LayoutInflater) myContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			
			// Inflate and set the layout for the dialog
			// Pass null as the parent view because its going in the dialog layout
			dateDialog.setContentView(inflater.inflate(R.layout.date_time_picker_dialog, null));
			setDateTime(dateDialog, the_button, Calendar.getInstance());
			setDateDialogButtons(dateDialog, the_button);
			dateDialog.show();
		}
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
		if (myParentView != null)
		{
			Button categoryButton = (Button) myParentView.findViewById(R.id.activity_create_private_event_categoryvalue);
			categoryButton.setText(myTempSelectedCategory.getName().name());			
		}
	}
	
	public void selectCategories(View the_view)
	{
		if (myContext != null && myParentView != null)
		{
			 AlertDialog.Builder builder = new AlertDialog.Builder(myContext);
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
								Button category = (Button) myParentView.findViewById(R.id.activity_create_private_event_categoryvalue);
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
	}
	
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
	
	
	
	
	private class AutoComplCntctsTVSetup extends AsyncTask<Void, Void, Void>
	{

		/* (non-Javadoc)
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected Void doInBackground(Void... arg0) {
			if (myContext != null)
			{
				myContacts = new ArrayList<ContactsMap>();

			    Cursor people = myContext.getContentResolver().query(
			            ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
			    while (people.moveToNext()) {
			        String contactName = people.getString(people
			                .getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
			        String contactId = people.getString(people
			                .getColumnIndex(ContactsContract.Contacts._ID));
			        String hasPhone = people
			                .getString(people
			                        .getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

			        if ((Integer.parseInt(hasPhone) > 0)){
			            // You know have the number so now query it like this
			            myCursor = myContext.getContentResolver().query(
			            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
			            null,
			            ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ contactId,
			            null, null);
			            while (myCursor.moveToNext()){
			                //store numbers and display a dialog letting the user select which.
			                String phoneNumber = myCursor.getString(
			                		myCursor.getColumnIndex(
			                ContactsContract.CommonDataKinds.Phone.NUMBER));
			                String numberType = myCursor.getString(myCursor.getColumnIndex(
			                ContactsContract.CommonDataKinds.Phone.TYPE));
			                ContactsMap NamePhoneType = new ContactsMap();
			                NamePhoneType.put(NAME, contactName);
			                NamePhoneType.put(PHONE, phoneNumber);
			                if(numberType.equals("0"))
			                    NamePhoneType.put(TYPE, "(Work)");
			                    else
			                    if(numberType.equals("1"))
			                    NamePhoneType.put(TYPE, "(Home)");
			                    else if(numberType.equals("2"))
			                    NamePhoneType.put(TYPE,  "(Mobile)");
			                    else
			                    NamePhoneType.put(TYPE, "(Other)");
			                    //Then add this map to the list.
			                myContacts.add(NamePhoneType);
			            }
//			            myCursor.close();
			        }
			    }
			    people.close();
			   // ((Activity) myContext).startManagingCursor(people);
			    myCursor.close();
			}
		    return null;
		}
		
		@Override
		protected void onPostExecute(Void params)
		{
		    setUpContactsTV();
		}
		

		
	}

	public void setUpContactsTV() 
	{
		if (myParentView != null)
		{
			final MultiAutoCompleteTextView textView = (MultiAutoCompleteTextView)
					myParentView.findViewById(R.id.activity_create_private_event_private_sendTo_value);
			if (textView != null)
			{
				 textView.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
				textView.setAdapter(new SimpleAdapter(myContext, myContacts, R.layout.contacts_autocomplete_layout, new String[]{"Name", "Phone", "Type"},
						new int[]{R.id.ccontName, R.id.ccontNo, R.id.ccontType}));		
				textView.setOnItemSelectedListener(new OnItemSelectedListener() 
				{

					@Override
					public void onItemSelected(AdapterView<?> the_view, View arg1,
							int pos, long arg3) {
						if (the_view != null)
						{
							HashMap<String, String> map = (HashMap<String, String>) the_view.getItemAtPosition(pos);
							if (map != null)
							{
								String name = map.get(NAME);
								String detail = map.get(PHONE);
								//String detailType 
							}
						}
					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
						// TODO Auto-generated method stub
						
					}
					
				});
			}
		}
	
	}

	public void createEvent(View the_view)
	{
		if (isInputValid())
		{
			//getOtherParameters();
		//	new SendInvite(myContext).send();
		}
	}
	
	
	private boolean isInputValid() 
	{
		return true;
				//isSenderValid() & isInviteesValid() & isNameValid() & isDateRangeValid();
	}

	/**
	 * @return
	 */
	private boolean isDateRangeValid() 
	{
		if (myParentView != null && myContext != null)
		{
			EditText fromDate = (EditText) myParentView.findViewById(R.id.activity_create_private_event_dateFromvalue);
			String from = fromDate.getText().toString();
			myFromDateTime = Utility.getCalendarForReminder(from);
			EditText toDate = (EditText) myParentView.findViewById(R.id.activity_create_private_event_dateTovalue);
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
			Toast toast = Toast.makeText(myContext.getApplicationContext(), ActivityCreateEvent.INVALIDDATERANGEMESSAGE, Toast.LENGTH_LONG);
			toast.setGravity(Gravity.CENTER, 40, 40);
			toast.show();
		}
		return false;
	}

	/**
	 * @return
	 */
	private boolean isNameValid() 
	{
		if (myParentView != null)
		{
			EditText nameVal = (EditText) myParentView.findViewById(R.id.activity_create_private_event_namevalue);
			if (nameVal != null)
			{
				String name = nameVal.getText()!=null?nameVal.getText().toString():null;
				if (name == null || name.length() == 0 || ActivityCreateEvent.INVALIDNAMEMESSAGE.equals(name))
				{
					nameVal.setText("");
					nameVal.setHint(ActivityCreateEvent.INVALIDNAMEMESSAGE);
					nameVal.setHintTextColor(Color.RED);
					return false;
				}
				myEventName = name;
				return true;
			}
		}
		return false;
	}

	
	private boolean isInviteesValid() 
	{
		if (myParentView != null)
		{
			final MultiAutoCompleteTextView textView = (MultiAutoCompleteTextView) myParentView.findViewById(R.id.activity_create_private_event_private_sendTo_value);
			if (textView != null && textView.getText() != null)
			{
				String invitees = textView.getText().toString();
				if (invitees == null || invitees.length() == 0 || INVALIDINVITEES.equals(invitees))
				{
					textView.setText("");
					textView.setHint(INVALIDINVITEES);
					textView.setHintTextColor(Color.RED);
					return false;
				}
			}
		}
		return false;
	}

	
	private boolean isSenderValid()
	{
		if (myParentView != null)
		{
			EditText nameVal = (EditText) myParentView.findViewById(R.id.activity_create_private_event_private_sendAs_value);
			if (nameVal != null)
			{
				String name = nameVal.getText()!=null?nameVal.getText().toString():null;
				if (name == null || name.length() == 0 || INVALIDSENDER.equals(name))
				{
					nameVal.setText("");
					nameVal.setHint(INVALIDSENDER);
					nameVal.setHintTextColor(Color.RED);
					return false;
				}
				mySender = name;
				return true;
			}
		}
		return false;
	}
	

	private class ContactsMap extends HashMap<String, String>
	{
		
		private static final long serialVersionUID = 1L;

		@Override
		public String toString()
		{
			String name = get(NAME);
			String type = get(TYPE);
			name = replaceCommas(name);
			type = replaceCommas(type);
			if (name !=  null && type != null)
			{
				return name + type + ",";
			}
			return null;
		}

		/**
		 * @param type
		 * @return
		 */
		private String replaceCommas(String the_str)
		{
			if (the_str != null)
			{
				return the_str.replaceAll(",", ESCAPECHAR);
			}
			return null;
		}
		
	}
}
