/**
 * 
 */
package com.bitBusy.goingg.webServices.awsSetup;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.amazonaws.services.simpledb.AmazonSimpleDBClient;

/**
 * @author SumaHarsha
 *
 */
public class AWSBaseOperation extends AsyncTask<Void, Void, Void> implements AWSOperationInterface{

	protected AmazonSimpleDBClient mySDBClient;
	protected ProgressDialog myProgressDialog;
	protected Context myContext;
	protected Object[] myParams;
	protected AWSOperationListenerInterface myListener;
	
	public AWSBaseOperation(Context the_context, Object[] the_params){
		myContext = the_context;
		myProgressDialog = new ProgressDialog(the_context);
		myParams = the_params;
		connectToAWS();
		
	}

	/* (non-Javadoc)
	 * @see com.bitBusy.going.webServices.awsSetup.AWSConnectionInterface#connectToAWS()
	 */
	@Override
	public void connectToAWS() {
		mySDBClient = AWSCredentialsStore.getInstance().getSDBClient();				
		
	}
	
	@Override
	protected void onPreExecute()
	{
		if (myProgressDialog != null){
		    myProgressDialog.setCanceledOnTouchOutside(false);
			myProgressDialog.show();
		}
	}

	/* (non-Javadoc)
	 * @see android.os.AsyncTask#doInBackground(Params[])
	 */
	@Override
	protected Void doInBackground(Void... params) {
		// TODO Auto-generated method stub
		return null;
	}
	
	protected void setProgressDialogMsg(String the_msg){
		if (myProgressDialog != null & the_msg != null){
			myProgressDialog.setMessage(the_msg);
		}
	}
	
	public void registerListener(AWSOperationListenerInterface the_listener)
	{
		myListener = the_listener;
	}

	protected void closeProgressDialog() {
		if (myProgressDialog != null &&  myProgressDialog.isShowing())
		{
			myProgressDialog.dismiss();
		}			
	}
	
	protected void notifyListener(ListenerAck the_ack)
	{
		if (myListener != null)
		{
			myListener.onOperationComplete(the_ack);
		}
	}
}
