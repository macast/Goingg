/**
 * 
 */
package com.bitBusy.goingg.dialog;

import java.util.Calendar;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import com.bitBusy.goingg.alarmSystem.EventReminder;
import com.bitBusy.goingg.alarmSystem.LocalAlarmManager;
import com.bitBusy.goingg.events.PublicEvent;
import com.bitBusy.goingg.utility.Utility;
import com.bitBusy.goingg.R;

/**
 * @author SumaHarsha
 *
 */
public class SetReminderDialog {

	private Context myContext;
	
	private PublicEvent myEvent;
	
	private Dialog myDialog;
	public SetReminderDialog(Context the_context, PublicEvent the_event)
	{
		myContext = the_context;
		myEvent = the_event;
	}
	
	
	public void openDialog()
	{
		if (myContext != null && myEvent != null)
		{
			myDialog = new Dialog(myContext);
			if (myDialog != null)
			{
				myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			    LayoutInflater inflater = (LayoutInflater) myContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			    
			    // Inflate and set the layout for the dialog
			    // Pass null as the parent view because its going in the dialog layout
			    myDialog.setContentView(inflater.inflate(R.layout.set_reminder_dialog, null));
				setContent();
				setButtonListeners();
			    myDialog.show();
			}
		}
	}
	
	private void setContent()
	{
		setReminderDateTime();
		setEventDetails();
	}
	
	private void setReminderDateTime()
	{
		 Calendar startDateTime = shallowCopyCalendar(myEvent.getStartDateTime());
		if (startDateTime != null)
		{
			startDateTime.add(Calendar.DAY_OF_MONTH, -1);
		}
		else
		{
			startDateTime = Calendar.getInstance();
		}
		final Calendar date = startDateTime;
		String dateTime = Utility.getDateTime(startDateTime);
		final Button displayButton = (Button) myDialog.findViewById(R.id.set_reminder_dialog_datetime);
		displayButton.setText(dateTime);
		displayButton.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View the_view) {
					showDateTimePicker(displayButton, date);
				}
		});

		
	}
	/** show date time picker*/
	private void showDateTimePicker(Button the_button, Calendar the_datetime)
	{
		Dialog dateDialog = new Dialog(myContext);
		dateDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
	    LayoutInflater inflater = (LayoutInflater) myContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    
	    // Inflate and set the layout for the dialog
	    // Pass null as the parent view because its going in the dialog layout
	    dateDialog.setContentView(inflater.inflate(R.layout.date_time_picker_dialog, null));
	    setDateTime(dateDialog, the_button, the_datetime);
	    setDateDialogButtons(dateDialog, the_button);
	    dateDialog.show();
	    
	}
	
	private void setDateTime(Dialog the_dialog, Button the_button, Calendar the_datetime)
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
	private void setDateDialogButtons(final Dialog the_dialog, final Button the_button)
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
	private Calendar shallowCopyCalendar(Calendar the_datetime)
	{
		Calendar calendar = the_datetime;
		if (the_datetime != null)
		{
			calendar = Calendar.getInstance();
			calendar.set(Calendar.HOUR_OF_DAY, the_datetime.get(Calendar.HOUR_OF_DAY));
			calendar.set(Calendar.MINUTE, the_datetime.get(Calendar.MINUTE));
			calendar.set(Calendar.DAY_OF_MONTH, the_datetime.get(Calendar.DAY_OF_MONTH));
			calendar.set(Calendar.MONTH, the_datetime.get(Calendar.MONTH));
			calendar.set(Calendar.YEAR, the_datetime.get(Calendar.YEAR));
		}
		return calendar;
	}
		
	private void setEventDetails()
	{
		 if (myEvent != null)
		 {
			 TextView name = (TextView) myDialog.findViewById(R.id.set_reminder_dialog_eventname);
			 name.setText(myEvent.getName());
			 
			 TextView starts = (TextView) myDialog.findViewById(R.id.set_reminder_dialog_startsvalue);
			 starts.setText(Utility.getDateTime(myEvent.getStartDateTime()));
			 
			 TextView ends = (TextView) myDialog.findViewById(R.id.set_reminder_dialog_endsvalue);
			 ends.setText(Utility.getDateTime(myEvent.getEndDateTime()));
		 }
	}

	private void setButtonListeners()
	{
		setSetButtonListener();
		setCancelButtonListener();
	}
	
	private void setSetButtonListener()
	{
		Button set = (Button) myDialog.findViewById(R.id.set_reminder_dialog_set);
		Button datetime = (Button) myDialog.findViewById(R.id.set_reminder_dialog_datetime);
		String dateTimeStr = datetime.getText().toString();
		final EventReminder reminder = new EventReminder(myEvent, Utility.getCalendarForReminder(dateTimeStr));
		set.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				LocalAlarmManager.setAlarm(reminder);
				myDialog.dismiss();
			}
			
		});
	}
	
	private void setCancelButtonListener()
	{
		Button cancel = (Button) myDialog.findViewById(R.id.set_reminder_dialog_cancel);
		cancel.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				myDialog.dismiss();
			}
			
		});
	}
}
