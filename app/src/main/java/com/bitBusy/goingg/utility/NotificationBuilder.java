/**
 * 
 */
package com.bitBusy.goingg.utility;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.bitBusy.goingg.activity.ActivityViewMyAccount;
import com.bitBusy.goingg.events.PrivateEvents.PrivateEventUser;
import com.bitBusy.goingg.messaging.setup.gcmSetup.GCMMessage;
import com.bitBusy.goingg.messaging.setup.gcmSetup.GCMMessage.GCMMessageType;
import com.bitBusy.goingg.R;

/**
 * @author SumaHarsha
 *
 */
public class NotificationBuilder {

    private static final int NOTIFICATION_ID = 1;
    private String myTitle;
    private String myMsg;
    private Context myContext;
    private GCMMessage myGCMMsg;

    public NotificationBuilder(GCMMessage the_message, Context the_context){
    	myGCMMsg = the_message;
    	myContext = the_context;    	
    }
    
	public void show(){
		if (myGCMMsg != null && myContext != null){
			setTitleAndMessage();
			Intent activityIntent = getActivityIntent();
		    NotificationManager notificationManager =  (NotificationManager)
		    	       myContext.getSystemService(Context.NOTIFICATION_SERVICE);
		    PendingIntent contentIntent = PendingIntent.getActivity(myContext, 0,
		    	      activityIntent, PendingIntent.FLAG_ONE_SHOT);
		    	NotificationCompat.Builder builder =
		    	       new NotificationCompat.Builder(myContext)
		    	.setSmallIcon(R.drawable.notification_icon)
		    	.setContentTitle(myContext.getResources().getString(R.string.app_name))
		    	.setStyle(new NotificationCompat.BigTextStyle()
		    	.bigText(myTitle))
		    	.setContentText(myMsg);
		    	builder.setContentIntent(contentIntent);
		    	builder.setAutoCancel(true);
		    	notificationManager.notify(NOTIFICATION_ID, builder.build());
		}
	}

	private Intent getActivityIntent() {
		Intent intent = new Intent(myContext, ActivityViewMyAccount.class);
		return intent;
	}

	private void setTitleAndMessage() {
		if (myGCMMsg != null && myGCMMsg.getSender() != null && myGCMMsg.getSender().getName() != null){
			GCMMessageType msgType = myGCMMsg.getMessageType();
			PrivateEventUser sender = myGCMMsg.getSender();
			if (msgType == GCMMessageType.FriendRequest){
				myMsg = (sender.getName().getFirstName() != null?sender.getName().getFirstName():"").concat(" ").concat(
						sender.getName().getLastName() != null?sender.getName().getLastName():"").concat(" sent you a friend request.");
			}			
		}
	}
}
