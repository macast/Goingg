/**
 * 
 */
package com.bitBusy.goingg.messaging.setup.gcmSetup;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.bitBusy.goingg.database.setup.DBInteractor;
import com.bitBusy.goingg.messaging.setup.MessageManagerInterface;
import com.bitBusy.goingg.messaging.setup.gcmSetup.GCMMessage.GCMMessageType;
import com.bitBusy.goingg.utility.Utility;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author SumaHarsha
 *
 */
public class GCMMessageManager implements MessageManagerInterface{
	
	public static final int SUCCESSCODE = 200;
	private static final String MSGSENDERR = "Message send error";
	private static final String MSGRESENDMSG = "You have already sent a ";
	private GCMMessage myGCMMessage;
	private GCMMessengerListener myListener;
	private Context myContext;
	private int myResponseCode;
	
	public GCMMessageManager(GCMMessage the_gcmMessage, Context the_context){
		myGCMMessage = the_gcmMessage;
		myContext = the_context;
	}
	
	public void registerListener(GCMMessengerListener the_listener){
		myListener =the_listener;
	}

	/* (non-Javadoc)
	 * @see com.bitBusy.going.messaging.MessageManagerInterface#sendMessage()
	 */
	@Override
	public void sendMessage() {
		if (!isSendToSelf() && !isMessageResend()){
			Log.i(this.getClass().getSimpleName(), "Sending.." + myGCMMessage.toString());
			new SendGCMMessage().execute();
		}
	}

	private boolean isSendToSelf(){
		if (myGCMMessage != null && myGCMMessage.getSender() != null && myGCMMessage.getReceiver() != null){
			if (myGCMMessage.getMessageType() != GCMMessageType.EditFriends && myGCMMessage.getMessageType() != GCMMessageType.EditMessages){
				boolean retVal = myGCMMessage.getSender().equals(myGCMMessage.getReceiver());
				if (retVal){
					Utility.throwErrorMessage(myContext, MSGSENDERR, "You cannot send yourself a message, man!"); 
				}
				return retVal;
			}
			return false;
		}
		else{
			Utility.throwErrorMessage(myContext, MSGSENDERR, "Sorry, there was an issue composing your message!");
		}
		return true;
	}
	
	private boolean isMessageResend(){
		boolean retVal = !(new DBInteractor(myContext).saveMessage(myGCMMessage, false));
		if (retVal){
			String msgType = Utility.mapMsgTypeToStr(myGCMMessage.getMessageType());
			String usr = Utility.getUserName(myContext, myGCMMessage.getReceiver());
			String username = usr!=null?usr:" this user";
			Utility.throwErrorMessage(myContext, MSGSENDERR, 
					(msgType!=null? (MSGRESENDMSG + msgType.toLowerCase(Locale.ENGLISH) + " to " + username):
				("You have already sent this message!")));
		}
		return retVal;
	}
	
	private class SendGCMMessage extends AsyncTask<Void, Void, Void>
	    {
		
		@Override
		protected void onPostExecute(Void params){
			notifyListener();
		}
		
			/* (non-Javadoc)
			 * @see android.os.AsyncTask#doInBackground(Params[])
			 */
			@Override
			protected Void doInBackground(Void... arg0) {
				if (myGCMMessage != null
					   && myGCMMessage.getReceiver() != null){
					   URL url;
					try {
						url = new URL("https://android.googleapis.com/gcm/send");
					   HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				        conn.setRequestMethod("POST");
				        conn.setRequestProperty("Content-Type", "application/json");
				        conn.setRequestProperty("Authorization", "key= AIzaSyBei6vOg20FEVzvD0C-vs1Fw4UkgoCFYh0");
	     			    conn.setDoOutput(true);
	     			    sendMessage(conn);
					} catch (MalformedURLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ProtocolException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			   }
			return null;
			}

			private void sendMessage(HttpURLConnection the_conn) {
				GCMSendFormatMsgJSON message = getMessageInJson();
				if (the_conn != null && message != null){
					ObjectMapper mapper = new ObjectMapper();
		            DataOutputStream wr;
					try {
						wr = new DataOutputStream(the_conn.getOutputStream());
			            mapper.writeValue(wr, message);
			            wr.flush();
			            wr.close();
				            myResponseCode = the_conn.getResponseCode();
				            BufferedReader in = new BufferedReader(
				                    new InputStreamReader(the_conn.getInputStream()));
				            String inputLine;
				            StringBuffer response = new StringBuffer();
	
				            while ((inputLine = in.readLine()) != null) {
				                response.append(inputLine);
				            }
				            in.close();
						} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					}
			}

	private GCMSendFormatMsgJSON getMessageInJson() {
		if (myGCMMessage != null && myGCMMessage.getReceiver() != null){
			GCMSendFormatMsgJSON msg = new GCMSendFormatMsgJSON();
			msg.setRegistration_ids(getRegIDList());
			msg.addData((myGCMMessage.getMessageType()!=null? myGCMMessage.getMessageType().name():null)
					, getMsgContent());
			return msg;
		}
		return null;
	}
	
	private List<String> getRegIDList(){
		List<String> regIDs = null;
		if (myGCMMessage != null &&  myGCMMessage.getReceiver() != null){
			regIDs = new ArrayList<String>();
			HashSet<GCMID> gcmIDs = myGCMMessage.getReceiver().getGCMIDs();
			if (gcmIDs != null){
				for (GCMID id: gcmIDs){
					if (id != null){
						regIDs.add(id.getGCMID());
					}
				}
			}
		}
		return regIDs;
	}

	private GCMMsgContentJSON getMsgContent() {
		if (myGCMMessage != null){
			GCMMsgContentJSON msgContent = new GCMMsgContentJSON();
			msgContent.setMsgType((myGCMMessage.getMessageType() != null)?myGCMMessage.getMessageType().name():null);
			msgContent.setSender(myGCMMessage.getSender());
			msgContent.setReceiver(myGCMMessage.getReceiver());
			msgContent.setMsgText(myGCMMessage.getContent());
			msgContent.setMsgDateTime(myGCMMessage.getSendDateTime());
			return msgContent;
		}
		return null;
	}
	}

	private void notifyListener() {
		if (myListener != null){
			myListener.onSend(myResponseCode);
		}
	}
	    	
}

