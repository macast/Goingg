/**
 * 
 */
package com.bitBusy.goingg.webServices.EventsData;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.widget.Toast;

import com.amazonaws.services.dynamodbv2.model.ConditionalCheckFailedException;
import com.amazonaws.services.simpledb.AmazonSimpleDBClient;
import com.amazonaws.services.simpledb.model.AttributeDoesNotExistException;
import com.amazonaws.services.simpledb.model.DeleteAttributesRequest;
import com.amazonaws.services.simpledb.model.PutAttributesRequest;
import com.amazonaws.services.simpledb.model.ReplaceableAttribute;
import com.amazonaws.services.simpledb.model.UpdateCondition;
import com.bitBusy.goingg.events.DataSources;
import com.bitBusy.goingg.events.EventCategory;
import com.bitBusy.goingg.events.PublicEvent;
import com.bitBusy.goingg.events.User;
import com.bitBusy.goingg.utility.AWSItemNameGenerator;
import com.bitBusy.goingg.utility.Utility;
import com.bitBusy.goingg.webServices.UserData.UserStatsQuerierWorker;
import com.bitBusy.goingg.webServices.awsSetup.AWSBaseOperation;
import com.bitBusy.goingg.webServices.awsSetup.ListenerAck;

/**
 * @author SumaHarsha
 *
 */
public class AWSDelete extends AWSBaseOperation
{
	private static final String DELETINGEVENT = "Deleting event..";
	private static final String SUCCESSMSG = "Event deleted!";
	private static final String USERSTATSUPDATEFAILMSG = "Event deleted but unable to update user stats!";
	private String FAILMSG = "Event not deleted!";
	private boolean myDeleteSuccessful;
	private int myStatsUpdateTries = 0;
	private PublicEvent myEvent;
	
	public AWSDelete(Context the_context,Object[] the_params)
	{
		super(the_context, the_params);
		setProgressDialogMsg(DELETINGEVENT);
	}

	/* (non-Javadoc)
	 * @see android.os.AsyncTask#doInBackground(Params[])
	 */
	@Override
	protected Void doInBackground(Void... params) {
		if (myParams != null && myParams.length > 0)
		{
			try{
				myEvent = (PublicEvent) myParams[0];
				if (myEvent != null && deleteEvent())
				{
					myDeleteSuccessful = true;
				}
				else
				{
					myDeleteSuccessful = false;
				}
			}catch(Exception e){
				
			}
		}
		else
		{
			myDeleteSuccessful = false;
		}
		return null;
	}

	/**
	 * @param event
	 * @return
	 */
	private boolean deleteEvent() {
		if (myEvent!= null)
		{
			try
			{
				String domain = getDomain(myEvent.getCategory());
				if (domain != null && mySDBClient != null)
				{
					mySDBClient.deleteAttributes(new DeleteAttributesRequest(domain, AWSItemNameGenerator.generate(myEvent)));
					return decrementUserEventCount(mySDBClient);
				}
			}
			catch(Exception e)
			{
				FAILMSG = e.getMessage();
			}
		}
		return false;
	}
	/**
	 * @param sdbClient
	 */
	private boolean decrementUserEventCount(AmazonSimpleDBClient the_sdbClient) {
		if (the_sdbClient != null && myEvent != null)
		{
			User user = new UserStatsQuerierWorker(the_sdbClient, myEvent.getCreator()).getUserStats();
			if (user != null)
			{
				if (user.getEventsSubmitted() > 0)
				{
					return decrementUserEventCountInDB(user, the_sdbClient);
				}
				return true;
			}
			FAILMSG = USERSTATSUPDATEFAILMSG;
		}
		return false;
	}

	/**
	 * @param user
	 * @param the_sdbClient
	 */
	private boolean decrementUserEventCountInDB(User the_user,
			AmazonSimpleDBClient the_sdbClient) {
		if (the_user != null && the_user.getID() != null && the_sdbClient !=  null)
		{ 
			String id = the_user.getID();
			int currentCount = the_user.getEventsSubmitted();
			try
			{
				the_sdbClient.putAttributes(new PutAttributesRequest(DataSources.AUSERSTATS.concat(Utility.getAWSUserStatsTableSuffix(id)),
						id, getEventsSubAttr((currentCount-1)), 
						new UpdateCondition(DataSources.AEVENTSSUBMITTED, String.valueOf(currentCount), true)));
				return true;
			}
			catch(ConditionalCheckFailedException e)
			{
				myStatsUpdateTries++;
				if (myStatsUpdateTries < 10)
				{
					decrementUserEventCount(the_sdbClient);
				}
				FAILMSG = e.getMessage();
				return false;
			}
			catch (AttributeDoesNotExistException e)
			{
					FAILMSG = e.getMessage();
					return false;
			}
			catch(Exception e)
			{
				e.printStackTrace();
				FAILMSG = e.getMessage();
				return false;
			}
		}
		return false;
	}
	private List<ReplaceableAttribute> getEventsSubAttr(int the_count) {
		ArrayList<ReplaceableAttribute> list = new ArrayList<ReplaceableAttribute>();
		list.add(new ReplaceableAttribute(DataSources.AEVENTSSUBMITTED, String.valueOf(the_count), true));
		return list;
		}

	/**
	 * @param category
	 * @return
	 */
	private String getDomain(EventCategory the_category) {
		if (the_category != null && the_category.getName() != null && the_category.getName().name() != null)
		{
			return the_category.getName().name();
		}
		return null;
	}

	@Override
    protected void onPostExecute(Void params)
    {
		showSuccessToast();
		closeProgressDialog();
		notifyListener(new ListenerAck(AWSDelete.class.getName(),new Object[]{myDeleteSuccessful}));
    }
	/**
	 * 
	 */
	private void showSuccessToast() {
		if (myDeleteSuccessful)
		{
			toast(SUCCESSMSG);
		}
		else
		{
			toast(FAILMSG);
		}
	}

	/**
	 * @param failmsg2
	 */
	private void toast(String the_msg) {
		Toast.makeText(myContext.getApplicationContext(), the_msg, Toast.LENGTH_LONG).show();
	}
}
