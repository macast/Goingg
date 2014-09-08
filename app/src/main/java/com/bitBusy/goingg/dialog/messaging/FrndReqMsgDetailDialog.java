/**
 * 
 */
package com.bitBusy.goingg.dialog.messaging;

import java.util.ArrayList;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bitBusy.goingg.adapters.ListAdapter;
import com.bitBusy.goingg.messaging.refresh.EditDBRequest;
import com.bitBusy.goingg.messaging.refresh.EditDBRequest.EDITPARAM;
import com.bitBusy.goingg.messaging.refresh.EditFriends;
import com.bitBusy.goingg.messaging.refresh.EditMsgs;
import com.bitBusy.goingg.messaging.setup.gcmSetup.GCMMessengerListener;
import com.bitBusy.goingg.utility.Encryption;
import com.bitBusy.goingg.utility.Utility;
import com.bitBusy.goingg.webServices.UserData.AddFriend;
import com.bitBusy.goingg.webServices.awsSetup.AWSOperationListenerInterface;
import com.bitBusy.goingg.webServices.awsSetup.ListenerAck;
import com.bitBusy.goingg.R;

/**
 * @author SumaHarsha
 *
 */
public class FrndReqMsgDetailDialog extends MsgDetailDialog implements AWSOperationListenerInterface, GCMMessengerListener{
	
	private static final String DEVICESSYNCMSG = "Sync issue. Going, on all your/ your friend's devices may not be updated.";
	private boolean isRefreshFrndsReq = false;

	public FrndReqMsgDetailDialog(Context the_context,ListAdapter the_adapter, ArrayList<?> the_list, int the_pos) {
		super(the_context, the_adapter, the_list, the_pos);
	}
	
	public void openDialog(){
		buildDialog();
		showDialog();
	}

	/**
	 * throws open dialog
	 */
	private void showDialog() {
		if (myDialog != null){
			myDialog.show();
		}
	}

	/**
	 * builds the dialog
	 */
	private void buildDialog() {
		if (myContext != null && myMsg != null){
			myDialog = new Dialog(myContext);
			myDialog.setContentView(R.layout.friend_request_message);
			myDialog.setTitle("Friend request");
			setTextViews();
			setButtons();
		}		
	}

	/**
	 * set button listeners
	 */
	private void setButtons() {
		if (myDialog != null){
			Button deny = (Button) myDialog.findViewById(R.id.friend_req_msg_deny);
			deny.setOnClickListener(new OnClickListener() {						 
				@Override
				public void onClick(View arg0) {
					deleteMessage();
					myDialog.dismiss();
				}
			});
			Button accept = (Button) myDialog.findViewById(R.id.friend_req_msg_accept);
			accept.setOnClickListener(new OnClickListener() {						 
				@Override
				public void onClick(View arg0) {
					addContact();
				}
			});
		}
	}

	/**
	 * adding contact to aws
	 */
	private void addContact() {
	if (myMsg.getSender() != null && myMsg.getReceiver() != null){
			AddFriend add = new AddFriend(myContext, new Object[]{myMsg.getSender().getUserName()});
			add.registerListener(this);
			add.execute();
		}
	}
	
	/**
	 * set values to text fields of dialog
	 */
	private void setTextViews() {
		if (myDialog != null && myMsg != null && myMsg.getSender() != null){
			TextView from = (TextView) myDialog.findViewById(R.id.friend_req_msg_from);
			from.setText(myMsg.getSender().getName() != null? ((myMsg.getSender().getName().getFirstName() != null?
					myMsg.getSender().getName().getFirstName() + " "  :"") + (myMsg.getSender().getName().getLastName() != null?
							myMsg.getSender().getName().getLastName():"")):"");			
			TextView city = (TextView) myDialog.findViewById(R.id.friend_req_loction);
			city.setText(myMsg.getSender().getCity()!=null?myMsg.getSender().getCity().getName():"");
			TextView message = (TextView) myDialog.findViewById(R.id.friend_req_msg_detail);
			message.setText((myMsg.getContent() != null && myMsg.getContent().toString() != null &&
					myMsg.getContent().toString().length() > 0)?"\"" + myMsg.getContent().toString() + "\"":"");			
		}
	}
	
	/* (non-Javadoc)
	 * @see com.bitBusy.going.webServices.awsSetup.AWSOperationListenerInterface#onOperationComplete(com.bitBusy.going.webServices.awsSetup.ListenerAck)
	 */
	@Override
	public void onOperationComplete(ListenerAck the_ack) {
		if (the_ack != null && the_ack.getClassName() != null && the_ack.getClassName().equals(AddFriend.class.getName()) && 
					the_ack.getParams() != null && the_ack.getParams().length > 0){
				boolean wasAdded = (Boolean) the_ack.getParams()[0];
				if (wasAdded){
					addFriendOnLocalDBs();
				}
			}
	}

	/**
	 * adds friends on local dbs of both users, employing GCM messaging.
	 * a gcm message is sent with this instruction too all devices of both users.
	 */
	private void addFriendOnLocalDBs() {
		isRefreshFrndsReq  = true;
		EditFriends edit = new EditFriends(myContext, myMsg.getReceiver(), myMsg.getSender(), EditDBRequest.EDITPARAM.Add);
		edit.registerListener(this);
		edit.sendMessage();			
	}

	/* (non-Javadoc)
	 * @see com.bitBusy.going.messaging.setup.gcmSetup.GCMMessengerListener#onSend(int)
	 */
	@Override
	public void onSend(int the_code) {
		if (the_code != 200){
			Toast.makeText(myContext, DEVICESSYNCMSG, Toast.LENGTH_LONG).show();
		}
		if (isRefreshFrndsReq)
		{
			isRefreshFrndsReq = false;
			deleteMessage();
		}
	}

	/**
	 * deletes this message from all devices of sender and receiver
	 */
	private void deleteMessage() {
		EditMsgs del = new EditMsgs(myContext, myMsg.getReceiver(), myMsg.getSender(),null );
		del.setContent(myMsg.getMsgID(), EDITPARAM.Delete);
		del.registerListener(this);
		del.sendMessage();
	}
	
	

}
