/**
 * 
 */
package com.bitBusy.goingg.dialog.messaging;

import java.util.ArrayList;

import android.app.Dialog;
import android.content.Context;

import com.bitBusy.goingg.adapters.ListAdapter;
import com.bitBusy.goingg.messaging.setup.gcmSetup.GCMMessage;

/**
 * @author SumaHarsha
 *
 */
public class MsgDetailDialog implements MsgDetailDialogInterface{

	protected Dialog myDialog;
	protected Context myContext;
	protected GCMMessage myMsg;
	protected ListAdapter myListAdapter;
	protected ArrayList<?> myList;
	protected int myItemPosition;
	
	public MsgDetailDialog(Context the_context, ListAdapter the_adapter, ArrayList<?> the_list, int the_pos){
		myContext = the_context;
		myListAdapter = the_adapter;
		myItemPosition = the_pos;
		myList = the_list;
		initializeMsg();
	}
	
	/**
	 * assigns value to msg field - by getting from list
	 */
	private void initializeMsg() {
		if (myList != null && myItemPosition >= 0 && myItemPosition < myList.size()){
			Object item = myList.get(myItemPosition);
			if (item != null && item instanceof GCMMessage){
				myMsg = (GCMMessage) item;
			}
		}
	}
	/* (non-Javadoc)
	 * @see com.bitBusy.going.dialog.messaging.MsgDetailDialogInterface#openDialog()
	 */
	@Override
	public void openDialog() {
		// TODO Auto-generated method stub
		
	}
	
	
	

}
