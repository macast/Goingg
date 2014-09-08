/**
 * 
 */
package com.bitBusy.goingg.alarmSystem;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.bitBusy.goingg.dialog.EventInformationDialog;

/**
 * @author SumaHarsha
 *
 */
public class AlarmBroadcastReceiver extends BroadcastReceiver{
	
		
	 @Override
	 public void onReceive(Context the_context, Intent intent) {
	         Intent service = new Intent(the_context, AlarmReceiverService.class);
	         service.putExtra(EventInformationDialog.ALARMDETAILS, 
	        		 (EventReminder)intent.getExtras().getParcelable(EventInformationDialog.ALARMDETAILS));
	         the_context.startService(service);
	     }
	 
	    public void CancelAlarm(Context context)
	   {

	        Intent intent = new Intent(context, AlarmBroadcastReceiver.class);
	        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);
	        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
	        alarmManager.cancel(sender);
	    }


}
