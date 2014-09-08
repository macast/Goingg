/**
 * 
 */
package com.bitBusy.goingg.messaging.client;

import java.util.Calendar;
import java.util.HashSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.bitBusy.goingg.database.setup.DBInteractor;
import com.bitBusy.goingg.events.PrivateEvents.City;
import com.bitBusy.goingg.events.PrivateEvents.PrivateEventUser;
import com.bitBusy.goingg.events.PrivateEvents.PrivateEventUserName;
import com.bitBusy.goingg.messaging.refresh.EditDBRequest;
import com.bitBusy.goingg.messaging.setup.gcmSetup.GCMID;
import com.bitBusy.goingg.messaging.setup.gcmSetup.GCMMessage;
import com.bitBusy.goingg.messaging.setup.gcmSetup.GCMMessage.GCMMessageType;
import com.bitBusy.goingg.messaging.setup.gcmSetup.GCMMsgContentJSON;
import com.bitBusy.goingg.messaging.setup.gcmSetup.GCMSendFormatMsgJSON;
import com.bitBusy.goingg.utility.CurrentLoginState;
import com.bitBusy.goingg.utility.Encryption;
import com.bitBusy.goingg.utility.NotificationBuilder;
import com.bitBusy.goingg.utility.Utility;
import com.google.android.gms.gcm.GoogleCloudMessaging;

public class GcmIntentService extends IntentService {
    private static final String SENDER = "sender";
    private static final String RECEIVER = "receiver";
    private static final String USERNAME = "userName";
    private static final String FNAME = "firstName";
	private static final String LNAME = "lastName";
	private static final String CITY = "city";
	private static final String GCMIDS = "gcmids";

    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
            /*
             * Filter messages based on message type. Since it is likely that GCM
             * will be extended in the future with new message types, just ignore
             * any message types you're not interested in, or that you don't
             * recognize.
             */
            if (GoogleCloudMessaging.
                    MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
               // sendNotification("Send error: " + extras.toString());
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_DELETED.equals(messageType)) {
               // sendNotification("Deleted messages on server: " +
                     //   extras.toString());
            // If it's a regular GCM message, do some work.
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_MESSAGE.equals(messageType)) {
    			Log.i("Intent service", extras.getString(GCMSendFormatMsgJSON.MSGBODY));

            		GCMMessage msg = buildGCMMessage((extras!=null && extras.getString(GCMSendFormatMsgJSON.MSGBODY)!=null)?
            				extras.getString(GCMSendFormatMsgJSON.MSGBODY):null);
            		takeActionOnMsg(msg);
            	}
        	}
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }
    
	/**
	 * decides what to do with message based on its type
	 * @param msg
	 */
	private void takeActionOnMsg(GCMMessage the_msg) {
		if (the_msg != null && the_msg.getMessageType() != null){
			Log.i("Intent service", the_msg.toString());
			if (the_msg.getMessageType() == GCMMessageType.FriendRequest){
				handleFriendRequest(the_msg);
			}
			else if (the_msg.getMessageType() == GCMMessageType.EditFriends){
				handleEditFriendsRequest(the_msg);
			}
			else if (the_msg.getMessageType() == GCMMessageType.EditMessages){
				handleEditMsgsRequest(the_msg);
			}
		}
	}
	

	/**
	 * action to take if incomin
	 */
	private void handleEditMsgsRequest(GCMMessage the_msg) {
			new DBInteractor(this).deleteMessage(the_msg);
	}

	/**
	 * action to take if incoming msg is to edit friends list
	 */
	private void handleEditFriendsRequest(GCMMessage the_msg) {
		if (the_msg != null && the_msg.getSender() != null && the_msg.getSender().getUserName() != null && 
				the_msg.getReceiver() != null && the_msg.getReceiver().getUserName() != null){
			DBInteractor dbInteractor = new DBInteractor(this);
			if (the_msg.getContent() != null && the_msg.getContent() instanceof EditDBRequest.EDITPARAM){
				if (the_msg.getContent() == EditDBRequest.EDITPARAM.Add){
					dbInteractor.addFriends(the_msg.getSender().getUserName(), the_msg.getSender().getUserName());
				}
			}
		}
	}

	/**
	 * action to take if incoming msg is friend request
	 * @param the_msg
	 */
	private void handleFriendRequest(GCMMessage the_msg) {
		if (the_msg != null){
			if (new DBInteractor(this).saveMessage(the_msg, true)  && isMsgForCurrentUser(the_msg)){
				new NotificationBuilder(the_msg, this).show();
			}		
		}
	}

	private boolean isMsgForCurrentUser(GCMMessage the_msg) {
		if (the_msg != null && the_msg.getReceiver() != null && the_msg.getReceiver().getUserName() != null && 
				CurrentLoginState.getCurrentUser(this) != null && CurrentLoginState.getCurrentUser(this).equals(the_msg.getReceiver().getUserName())){
			return true;
		}
		return false;
	}

	private GCMMessage buildGCMMessage(String the_string) {
		if (the_string != null){
			JSONObject json = getJSONObject(the_string);
			GCMMessage msg = convertJSONToGCM(json);
			return msg;
		}
		return null;
	}

	private GCMMessage convertJSONToGCM(JSONObject the_json){
		if (the_json != null){
			try {
				String msgTypeStr = the_json.getString(GCMMsgContentJSON.MSGTYPE);
				GCMMessageType msgType = GCMMessageType.valueOf(msgTypeStr);
				PrivateEventUser sender = getUser(the_json.getJSONObject(SENDER));
				PrivateEventUser receiver = getUser(the_json.getJSONObject(RECEIVER));
				String sendDateTime = the_json.getString(GCMMsgContentJSON.MSGDATETIME);
				String receiveDateTime = Utility.getDateTime(Calendar.getInstance());
				String content = the_json.getString(GCMMsgContentJSON.MSGTEXT);
				GCMMessage msg = new GCMMessage(sender, receiver, content, sendDateTime, receiveDateTime);
				msg.setType(msgType);
				return msg;
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	 
	private PrivateEventUser getUser(JSONObject the_jsonObject) {
		if (the_jsonObject != null){
				String userid;
			try {
					userid = the_jsonObject.getString(USERNAME);
					PrivateEventUserName name = getUserName(the_jsonObject, userid);
					City city = getCity(the_jsonObject, userid);
					HashSet<GCMID> gcmids = getGCMIDs(the_jsonObject);
					return new PrivateEventUser(userid, name, city, gcmids, null);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	private HashSet<GCMID> getGCMIDs(JSONObject the_json){
		if (the_json != null){
			JSONArray idsArray;
			try {
				idsArray = the_json.getJSONArray(GCMIDS);
				HashSet<GCMID> idSet = new HashSet<GCMID>();
				JSONObject idObj;
				if (idsArray != null){
					for (int i = 0; i < idsArray.length(); i++){
						idObj = idsArray.getJSONObject(i);
						if (idObj != null){
							idSet.add(new GCMID(idObj.getString(GCMMsgContentJSON.GCMID)));
						}
					}
				}
				return idSet;
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	private City getCity(JSONObject the_json, String the_userid){
		if (the_json != null && the_userid != null){
			try {
				JSONObject cityobj = the_json.getJSONObject(CITY);
				if (cityobj != null){
					String cityname = cityobj.getString(GCMMsgContentJSON.NAME);
					return new City(Encryption.decryptTwoWay(this, cityname, the_userid));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	private PrivateEventUserName getUserName(JSONObject the_json, String the_userid){
		if (the_json != null && the_userid != null){
			JSONObject nameObj;
			try {
				nameObj = the_json.getJSONObject(GCMMsgContentJSON.NAME);
				if (nameObj != null){
					String fname = nameObj.getString(FNAME);
					String lname = nameObj.getString(LNAME);
					return new PrivateEventUserName(Encryption.decryptTwoWay(this, fname, the_userid), 
							Encryption.decryptTwoWay(this, lname, the_userid));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	private JSONObject getJSONObject(String the_string) {
		if (the_string != null){
			JSONObject json;
			try {
				json = new JSONObject(the_string);
				return json;
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
}
