/**
 * 
 */
package com.bitBusy.goingg.alarmSystem;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.bitBusy.goingg.activity.ActivityMapViewHome;
import com.bitBusy.goingg.database.setup.DBInteractor;
import com.bitBusy.goingg.dialog.EventInformationDialog;

/**
 * @author SumaHarsha
 *
 */
public class LocalAlarmManager {

		private static final Context ALARMCONTEXT = ActivityMapViewHome.getAppContext();
	 
		public static boolean setAlarm(EventReminder the_reminder)
		{
			if (the_reminder != null)
			{
			  AlarmManager alarmManager =(AlarmManager)ALARMCONTEXT.getSystemService(Context.ALARM_SERVICE);
			  Calendar cal = the_reminder.getDateTime();
			  long alarmID = cal!=null?cal.getTimeInMillis():0;
		       the_reminder.setID((int) alarmID);
			  PendingIntent pendingIntent = getPendingIntent(the_reminder);
		           alarmManager.set(AlarmManager.RTC_WAKEUP, alarmID , pendingIntent);
				return new DBInteractor(ALARMCONTEXT).saveAlarm(the_reminder);
			}
			return false;

		}
	
		   /**
		 * @param the_reminder
		 * @return
		 * 
		 */
		private static PendingIntent getPendingIntent(EventReminder the_reminder) {
			if (the_reminder != null)
			{
				Intent broadcastIntent = new Intent(ALARMCONTEXT, AlarmBroadcastReceiver.class);
				broadcastIntent.putExtra(EventInformationDialog.ALARMDETAILS, the_reminder);
				return PendingIntent.getBroadcast(ALARMCONTEXT,the_reminder.getID(), broadcastIntent,PendingIntent.FLAG_UPDATE_CURRENT);	  
			}
			return null;
		}

		public static boolean deleteAlarm(EventReminder the_reminder)
		{
				if (the_reminder != null)
				{
					  AlarmManager alarmManager =(AlarmManager)ALARMCONTEXT.getSystemService(Context.ALARM_SERVICE);
				        PendingIntent pendingIntent = getPendingIntent(the_reminder);
					  	alarmManager.cancel(pendingIntent);
					  	pendingIntent.cancel();
				       return deleteAlarmFromDB(the_reminder);
				}
				return false;
			}

		/**
		 * @param alarmReceiverService
		 * @param myReminder
		 */
		public static boolean deleteAlarmFromDB(EventReminder the_reminder) {
	       return new DBInteractor(ALARMCONTEXT).deleteAlarm(the_reminder);
			
		}

}
