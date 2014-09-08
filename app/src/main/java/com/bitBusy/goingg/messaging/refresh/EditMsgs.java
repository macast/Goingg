/**
 * 
 */
package com.bitBusy.goingg.messaging.refresh;

import java.util.Calendar;

import android.content.Context;

import com.bitBusy.goingg.events.PrivateEvents.PrivateEventUser;
import com.bitBusy.goingg.messaging.messageTypes.EditMsgsMessage;
import com.bitBusy.goingg.utility.Utility;

/**
 * @author SumaHarsha
 *
 */
public class EditMsgs extends EditDBRequest{

	/**
	 * @param the_context
	 * @param the_sender
	 * @param the_rx
	 * @param the_content
	 */
	public EditMsgs(Context the_context, PrivateEventUser the_sender,
			PrivateEventUser the_rx, Object the_content) {
		super(the_context, the_sender, the_rx, the_content);
		buildMsg();
	}
	
	/**
	 * builds msg to be sent
	 */
	private void buildMsg(){
		myMsg = new EditMsgsMessage(mySender, myReceiver, myContent, Utility.getDate(Calendar.getInstance()), 
				null);
	}
	
	public void setContent(String the_msgID, EDITPARAM the_param){
		EditMsgParam editDetail = new EditMsgParam();
		editDetail.setMsgAction(the_param);
		editDetail.setMsgID(the_msgID);
		myContent = editDetail;
	}
	public class EditMsgParam{
		private String myMsgID;
		private EDITPARAM myAction;
		
		public void setMsgID(String the_id){
			myMsgID = the_id;
		}
		
		public void setMsgAction(EDITPARAM the_param){
			myAction = the_param;
		}
		
		public String getMsgID(){
			return myMsgID;
		}
		
		public EDITPARAM getMsgAction(){
			return myAction;
		}
		
	}
}
