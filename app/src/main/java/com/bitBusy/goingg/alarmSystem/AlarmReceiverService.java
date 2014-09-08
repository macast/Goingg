/**
 * 
 */
package com.bitBusy.goingg.alarmSystem;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.IBinder;

import com.bitBusy.goingg.activity.ActivityViewReminders;
import com.bitBusy.goingg.dialog.EventInformationDialog;

	/**
	 * @author SumaHarsha
	 *
	 */
	public class AlarmReceiverService extends Service{

		/**
		 * @param name
		 */
		public AlarmReceiverService() {
			super();
			// TODO Auto-generated constructor stub
		}

		private EventReminder myReminder;
		
		    @Override
		    public IBinder onBind(Intent arg0)
		    {
		        return null;
		    }
		 
		    @Override
		    public void onCreate()  			
		    {
		    	super.onCreate();
		    }
		    @Override
		    public void onDestroy()  			
		    {
		    	myReminder = null;
		    	super.onDestroy();
		    }
		    @Override
		    public int onStartCommand(Intent the_intent, int flags, int startId) {


		    if (the_intent != null && the_intent.getExtras() != null)
	    	{
		    	myReminder = (EventReminder)the_intent.getExtras().getParcelable(EventInformationDialog.ALARMDETAILS);
		    	if (myReminder != null)
		    		{ 
				    	  	showNotification( myReminder.getEvent()!=null?myReminder.getEvent().getName():"");
		    		}	    	
		    }
		    return START_STICKY;
	}
		     
		
			
	
	/**
	 * @param bademailnotification2
	 */
	private void showNotification(String the_string) {
			 NotificationManager myNotificationManager = 
					 (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		       Intent intent = new Intent(this, ActivityViewReminders.class);
		       	intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		       PendingIntent pendingNotificationIntent = 
		    		   PendingIntent.getActivity(this,0, intent,PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);
	
		       Notification notification = new Notification.Builder
		    		   (getApplicationContext()).setSmallIcon(com.bitBusy.goingg.R.drawable.event).
		    		   setContentText(the_string).setContentTitle(getResources().getText(com.bitBusy.goingg.R.string.setReminder)).
		    		   setContentIntent(pendingNotificationIntent)
		    		   .setLights(Color.BLUE, 5000, 1000).setAutoCancel(true).build();
		       	notification.defaults = 0;
		 
		     notification.flags |= Notification.FLAG_AUTO_CANCEL;
		       
		       myNotificationManager.notify(0, notification);
			
	}
}
