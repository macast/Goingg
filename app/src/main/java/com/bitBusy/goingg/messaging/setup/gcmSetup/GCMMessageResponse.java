/**
 * 
 */
package com.bitBusy.goingg.messaging.setup.gcmSetup;

import java.util.ArrayList;

import android.content.Context;

import com.bitBusy.goingg.adapters.ListAdapter;
import com.bitBusy.goingg.database.setup.DBInteractor;
import com.bitBusy.goingg.events.PrivateEvents.PrivateEventUser;

/**
 * @author SumaHarsha
 *
 */
public class GCMMessageResponse extends GCMMessage{
	
	private static final String myNessagesList = null;
	private ListAdapter myListAdapter;
	private ArrayList<?> myMessagesList;
	private int myReplyToItemPos;
	protected Context myContext;
	private GCMMessage myReplyToMsg;
	protected DBInteractor myDBInteractor;
	protected Object myContent;
	
	/**
	 * @param the_sender
	 * @param the_rx
	 * @param the_content
	 * @param the_sendDateTime
	 * @param the_rxDateTime
	 */
	public GCMMessageResponse(Context the_context, PrivateEventUser the_sender,
			PrivateEventUser the_rx, Object the_content,
			String the_sendDateTime, String the_rxDateTime, ListAdapter the_adapter, ArrayList<?> the_msgList, int the_pos) {
		super(the_sender, the_rx, the_content, the_sendDateTime, the_rxDateTime);
		myListAdapter = the_adapter;
		myMessagesList = the_msgList;
		myReplyToItemPos = the_pos;
		myContext = the_context;
		myDBInteractor = new DBInteractor(the_context);
		myContent = the_content;
		initializeMyReplyToMsg();
	}
	
	/**
	 * the message to which this is a reply
	 */
	private void initializeMyReplyToMsg() {
		if (myMessagesList != null && myReplyToItemPos >= 0 && myReplyToItemPos < myMessagesList.size()){
			Object obj = myMessagesList.get(myReplyToItemPos);
			if (obj != null && obj instanceof GCMMessage){
				myReplyToMsg = (GCMMessage) obj;
			}
		}
	}

	public void deleteItem(){
		if (myNessagesList != null && myListAdapter != null && myReplyToItemPos >= 0 && myReplyToItemPos < myMessagesList.size()){
			if (new DBInteractor(myContext).deleteMessage(myReplyToMsg)){
				myMessagesList.remove(myReplyToItemPos);
				myListAdapter.notifyDataSetChanged();
			}
		}
	}
	
}
