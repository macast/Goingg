/**
 * 
 */
package com.bitBusy.goingg.webServices.UserData;

import android.content.Context;

import com.bitBusy.goingg.events.User;
import com.bitBusy.goingg.webServices.awsSetup.AWSBaseOperation;
import com.bitBusy.goingg.webServices.awsSetup.ListenerAck;

/**
 * @author SumaHarsha
 *
 *
 */

public class UserStatsQuerier extends AWSBaseOperation{

	private static final String RETRIEVINGSTATS = "Retrieving user details..";
		
	private String myID;
		
	private User myUser;
	
	public UserStatsQuerier(Context the_context, Object[] the_params)
	{
		super(the_context, the_params);
		setProgressDialogMsg(RETRIEVINGSTATS);
	}
	
	/*(non-Javadoc)
	 * @see android.os.AsyncTask#doInBackground(Params[])
	 */
	@Override
	protected Void doInBackground(Void... arg0) {
		if (myParams != null && myParams.length > 0)
		{
			try{
				myID = (String) myParams[0];
				if (myID != null)
				{
					myUser = new UserStatsQuerierWorker(mySDBClient, myID).getUserStats();
				}
			}catch(Exception e){
			}		
		}
		return null;
	}

	@Override
    protected void onPostExecute(Void param)
    {
		closeProgressDialog();
		notifyListener(new ListenerAck(UserStatsQuerier.class.getName(),new Object[]{myUser}));
    }
}
