/**
 * 
 */
package com.bitBusy.goingg.webServices.UserData;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.simpledb.model.PutAttributesRequest;
import com.amazonaws.services.simpledb.model.ReplaceableAttribute;
import com.bitBusy.goingg.events.DataSources;
import com.bitBusy.goingg.utility.CurrentLoginState;
import com.bitBusy.goingg.utility.Utility;
import com.bitBusy.goingg.webServices.awsSetup.AWSBaseOperation;
import com.bitBusy.goingg.webServices.awsSetup.ListenerAck;

/**
 * @author SumaHarsha
 *
 */
public class AddFriend extends AWSBaseOperation
{
	
	private static final String ADDINGCONTACT = "Adding contact..";
	private static final String FRIENDNOTADDED = "Contact not added!";
	private String myUserName;
	private boolean wasFriendAdded;
	private String myFriendsName;
	private String myErrorMsg; 
	
	public AddFriend(Context the_context, Object[] the_params)
	{
		super(the_context, the_params);
		setProgressDialogMsg(ADDINGCONTACT);
	}
	
	/* (non-Javadoc)
	 * @see android.os.AsyncTask#doInBackground(Params[])
	 */
	@Override
	protected Void doInBackground(Void... arg0) 
	{
		initializeUsername();
		if (myParams != null && myParams.length > 0 && myParams[0] != null &&
				myUserName != null && myUserName.length()> 0)
		{
			try{
				myFriendsName = (String)myParams[0];
				saveFriend();
				Log.i(this.getClass().getSimpleName(), "my name: " + myUserName + " my friends name : " + myFriendsName);
			}catch(Exception e){
			}
		}
		return null;
	}

	private void saveFriend() 
	{
		addToMyList();
		addToFriendsList();
	}

	/**
	 * adds me to friend's list of friends
	 */
	private void addToFriendsList() {
		try
		{
			if (mySDBClient != null && myFriendsName != null)
			{
				String suffix = Utility.getAWSUserStatsTableSuffix(myFriendsName);
				ArrayList<ReplaceableAttribute> attributes = getReplaceableAttributeForFriend();
				if (attributes != null && suffix != null)
				{
					mySDBClient.setEndpoint(DataSources.AENDPNT);
					mySDBClient.putAttributes(new PutAttributesRequest(DataSources.AUSER.concat(suffix),
							myFriendsName, attributes));
				}
			}
		}
		catch(AmazonServiceException e)
		{
			wasFriendAdded = false;
			myErrorMsg = e.getLocalizedMessage();
		}				
	}

	/**
	 * @returns list of attributes to be added for friend
	 */
	private ArrayList<ReplaceableAttribute> getReplaceableAttributeForFriend() {
		ArrayList<ReplaceableAttribute> list = new ArrayList<ReplaceableAttribute>();
		list.add(new ReplaceableAttribute(DataSources.AFRIENDNAME, myUserName, false));
		return list;
	}

	/**
	 * adds friend to my list of friends
	 */
	private void addToMyList() {
		try
		{
			if (mySDBClient != null && myUserName != null)
			{
				String suffix = Utility.getAWSUserStatsTableSuffix(myUserName);
				ArrayList<ReplaceableAttribute> attributes = getReplaceableAttributeForMe();
				if (attributes != null && suffix != null)
				{
					mySDBClient.setEndpoint(DataSources.AENDPNT);
					mySDBClient.putAttributes(new PutAttributesRequest(DataSources.AUSER.concat(suffix),
							myUserName, attributes));
					wasFriendAdded = true;
				}
			}
		}
		catch(AmazonServiceException e)
		{
			wasFriendAdded = false;
			myErrorMsg = e.getLocalizedMessage();
		}		
	}

	private ArrayList<ReplaceableAttribute> getReplaceableAttributeForMe()
	{
		ArrayList<ReplaceableAttribute> list = new ArrayList<ReplaceableAttribute>();
		list.add(new ReplaceableAttribute(DataSources.AFRIENDNAME, myFriendsName, false));
		return list;
	}
	
	private void initializeUsername() 
	{
		if (myContext != null)
		{
			myUserName = CurrentLoginState.getCurrentUser(myContext);
		}
	}
	
	@Override
    protected void onPostExecute(Void params)
    {
		if (!wasFriendAdded)
		{
			Utility.throwErrorMessage(myContext, FRIENDNOTADDED, myErrorMsg);
		}
		notifyListener(new ListenerAck(AddFriend.class.getName(), new Object[]{wasFriendAdded}));
		closeProgressDialog();
    }	
}
