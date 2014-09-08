/**
 * 
 */
package com.bitBusy.goingg.messaging.setup.gcmSetup;

import com.bitBusy.goingg.events.PrivateEvents.PrivateEventUser;

/**
 * @author SumaHarsha
 *
 */
public class GCMMessage implements GCMMessageInterface
{

	public static enum GCMMessageType 
	{FriendRequest, InviteRequest, InviteResponse, TrackingRequest, TrackingResponse, GroupChat, EditFriends, EditMessages};
	protected String myMsgID;
	protected PrivateEventUser mySender;
	protected PrivateEventUser myReceiver;
	protected String mySendDateTime;
	protected String myRxDateTime;
	protected Object myContent;
	private GCMMessageType myMessageType;

	
	public GCMMessage
	(PrivateEventUser the_sender, PrivateEventUser the_rx, Object the_content, String the_sendDateTime, String the_rxDateTime)
	{
		mySender = the_sender;
		myReceiver = the_rx;
		myContent = the_content;
		mySendDateTime = the_sendDateTime;
		myRxDateTime = the_rxDateTime;
		initializeMsgID();
	}
	
	private void initializeMsgID(){
		if (mySender != null && mySender.getUserName() != null && mySendDateTime != null){
			myMsgID = mySender.getUserName().concat(mySendDateTime);
		}
	}
	
	private void contextualizeMsgID(){
		if (myMessageType != null){
			if (myMessageType == GCMMessageType.FriendRequest){
				setFriendRequestID();
			}
			/*else if (myMessageType == GCMMessageType.FriendResponse){
				setFriendResponseID();
			}*/
			else if (myMessageType == GCMMessageType.EditFriends){
				setRefreshFriendsID();
			}
			else if (myMessageType == GCMMessageType.EditMessages){
				setDeleteMsgID();
			}
		}
	}
	
	/**
	 * cretes id when type of DeleteMessage
	 */
	private void setDeleteMsgID() {
		if (myReceiver != null && myReceiver.getUserName() != null && mySender != null && mySender.getUserName() != null &&
				mySendDateTime != null&& myContent != null && myContent.toString() != null){
			myMsgID = myReceiver.getUserName().concat(mySender.getUserName()).concat(GCMMessageType.EditMessages.name()).concat(mySendDateTime).concat(
					myContent.toString());
		}			
	}

	/**
	 * sets id for message when type of RefreshFriends
	 */
	private void setRefreshFriendsID() {
		if (myReceiver != null && myReceiver.getUserName() != null && mySender != null && mySender.getUserName() != null &&
				mySendDateTime != null){
			myMsgID = myReceiver.getUserName().concat(mySender.getUserName()).concat(GCMMessageType.EditFriends.name()).concat(mySendDateTime);
		}		
	}
	
	
	private void setFriendRequestID(){
		if (mySender != null && mySender.getUserName() != null && myReceiver != null && myReceiver.getUserName() != null){
			myMsgID = mySender.getUserName().concat(myReceiver.getUserName()).concat(GCMMessageType.FriendRequest.name());
		}
	}
	public void setType(GCMMessageType the_msgType){
		myMessageType = the_msgType;
		contextualizeMsgID();
	}
	
	public String getMsgID(){
		return myMsgID;
	}
	
	public PrivateEventUser getSender()
	{
		return mySender;
	}

	public Object getContent()
	{
		return myContent;
	}
	
	public PrivateEventUser getReceiver(){
		return myReceiver;
	}
	
	public String getSendDateTime(){
		return mySendDateTime;
	}
	
	public String getRxDateTime(){
		return myRxDateTime;
	}
	@Override	
	public GCMMessageType getMessageType() {
		return myMessageType;
	}
	
	@Override
	public String toString(){
		StringBuilder builder = new StringBuilder();
		builder.append("My receiver: " + myReceiver.toString());
		builder.append("My sender: " + mySender.toString());
		builder.append("My type: " + myMessageType.name());
		builder.append("My content: " + myContent.toString());
		builder.append("My send date time: " + mySendDateTime);
		builder.append("My rx date time: " + myRxDateTime);
		return builder.toString();
	}
}
