/**
 * 
 */
package com.bitBusy.goingg.webServices.EventsData;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import android.content.Context;
import android.widget.Toast;

import com.amazonaws.services.dynamodbv2.model.ConditionalCheckFailedException;
import com.amazonaws.services.simpledb.model.Attribute;
import com.amazonaws.services.simpledb.model.AttributeDoesNotExistException;
import com.amazonaws.services.simpledb.model.DeleteAttributesRequest;
import com.amazonaws.services.simpledb.model.GetAttributesRequest;
import com.amazonaws.services.simpledb.model.GetAttributesResult;
import com.amazonaws.services.simpledb.model.PutAttributesRequest;
import com.amazonaws.services.simpledb.model.ReplaceableAttribute;
import com.amazonaws.services.simpledb.model.UpdateCondition;
import com.bitBusy.goingg.events.DataSources;
import com.bitBusy.goingg.events.EventCategory;
import com.bitBusy.goingg.events.PublicEvent;
import com.bitBusy.goingg.events.User;
import com.bitBusy.goingg.utility.AWSItemNameGenerator;
import com.bitBusy.goingg.utility.UUIDGenerator;
import com.bitBusy.goingg.utility.Utility;
import com.bitBusy.goingg.webServices.UserData.UserStatsQuerierWorker;
import com.bitBusy.goingg.webServices.awsSetup.AWSBaseOperation;

/**
 * @author SumaHarsha
 *
 */
public class AWSReportSpam extends AWSBaseOperation{
	
	private static final String REPORTINGEVENT = "Reporting this event as spam..";
	
	private static final String SUCCESSMSG = "Spam reported!";
	
	private String FAILMSG = "Spam not reported! You may have already reported this event as spam in the past.";
	
	private String USERSTATSUPDATEFAILMSG = "Spam reported but unable to update user stats!";
	
	private boolean mySaveSuccessful;
		
	private PublicEvent myEvent;
	
	private String myDomain;
	
	private String myAWSID;

	private int mySpamRepUpdateTries = 0;

	private int mySpamGenUpdateTries = 0;
	
	private String myUserID;
	
	public AWSReportSpam(Context the_context, Object[] the_params)
	{
		super(the_context, the_params);
		setProgressDialogMsg(REPORTINGEVENT);
	}
	
	/* (non-Javadoc)
	 * @see android.os.AsyncTask#doInBackground(Params[])
	 */
	@Override
	protected Void doInBackground(Void... params) {
		if (myParams != null && myParams.length > 0)
		{
			try{
				myEvent = (PublicEvent)myParams[0];
				if (myEvent != null && report())
				{
					mySaveSuccessful = true;
				}
				else
				{
					mySaveSuccessful = false;
				}
			}catch(Exception e){
			}
		}
		else
		{
			mySaveSuccessful = false;
		}
		return null;
	}

	/**
	 * @param event
	 * @return
	 */
	private boolean report() {
		if (myEvent != null)
		{
			myUserID = UUIDGenerator.id(myContext);
			String datetime = Utility.getAWSDBDateTime(Calendar.getInstance());
			if (myUserID != null && datetime != null)
			{
				GetAttributesResult queryresults = getSpamReporters();
				return reportEvent(queryresults);
			}

		}
		return false;
	}
	

	/**
	 * @param queryresults
	 */
	private boolean reportEvent(GetAttributesResult the_result) {
		if (the_result != null)
		{
			boolean success = false;
			String mySpamReportID = getReportID(the_result);
			if (mySpamReportID != null && mySpamReportID.equals(DataSources.ASPAM6))
			{
				success = deleteEvent();
			}
			else
			{
				success = reportEventAsMe(mySpamReportID);
			}
			if (success)
			{
				return updateUserStats();
			}
		}
		return false;
	}

	/**
	 * @param mySpamReportID
	 */
	private boolean updateUserStats() {
		boolean reporterStatsUpdated = updateReporterStats();
		boolean spammerStatsUpdated = updateSpammerStats();
		if (!reporterStatsUpdated || !spammerStatsUpdated)
		{
			FAILMSG = USERSTATSUPDATEFAILMSG;
			return false;
		}
		return true;
	}

	/**
	 * @return
	 */
	private boolean updateSpammerStats() {
		if (myEvent != null && myEvent.getCreator() != null)
		{
			User user = new UserStatsQuerierWorker(mySDBClient, myEvent.getCreator()).getUserStats();
			return updateGeneratedCount(user);			
		}
		else
		{
			FAILMSG = USERSTATSUPDATEFAILMSG;
		}
		return false;
	}


	/**
	 * @param user
	 * @return
	 */
	private boolean updateGeneratedCount(User the_user) {
		if (the_user != null && the_user.getID() != null)
		{ 
			String id = the_user.getID();
			int currentCount = the_user.getSpamGenerated();
			try
			{
				mySDBClient.putAttributes(new PutAttributesRequest(DataSources.AUSERSTATS.concat(Utility.getAWSUserStatsTableSuffix(id)),
						id, getSpamGenAttr((currentCount+1)), 
						new UpdateCondition(DataSources.ASPAMGENERATED, String.valueOf(currentCount), true)));
				return true;
			}
			catch(ConditionalCheckFailedException e)
			{
				mySpamGenUpdateTries ++;
				if (mySpamGenUpdateTries < 10)
				{
					updateSpammerStats();
				}
				FAILMSG = e.getMessage();
			}
			catch (AttributeDoesNotExistException e)
			{
				try
				{
					mySDBClient.putAttributes(new PutAttributesRequest(DataSources.AUSERSTATS.concat(Utility.getAWSUserStatsTableSuffix(id)),
						id, getSpamGenAttr((currentCount+1))));
					return true;
				}
				catch (Exception e1)
				{
					FAILMSG = e1.getMessage();
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
				FAILMSG = e.getMessage();
			}
		}
		return false;
	}
	
	private List<ReplaceableAttribute> getSpamGenAttr(int the_count) {
		ArrayList<ReplaceableAttribute> list = new ArrayList<ReplaceableAttribute>();
		list.add(new ReplaceableAttribute(DataSources.ASPAMGENERATED, String.valueOf(the_count), true));
		return list;
		}

	/**
	 * @return
	 */
	private boolean updateReporterStats() {
		if (myUserID != null)
		{
			User user = new UserStatsQuerierWorker(mySDBClient, myUserID).getUserStats();
			return updateReportedCount(user);			
		}
		else
		{
			FAILMSG = USERSTATSUPDATEFAILMSG;
		}
		return false;
	}

	/**
	 * @param user
	 * @return
	 */
	private boolean updateReportedCount(User the_user) {
		if (the_user != null && the_user.getID() != null)
		{ 
			String id = the_user.getID();
			int currentCount = the_user.getSpamReported();
			try
			{
				mySDBClient.putAttributes(new PutAttributesRequest(DataSources.AUSERSTATS.concat(Utility.getAWSUserStatsTableSuffix(id)),
						id, getSpamRepAttr((currentCount+1)), 
						new UpdateCondition(DataSources.ASPAMREPORTED, String.valueOf(currentCount), true)));
				return true;
			}
			catch(ConditionalCheckFailedException e)
			{
				mySpamRepUpdateTries ++;
				if (mySpamRepUpdateTries < 10)
				{
					updateReporterStats();
				}
				FAILMSG = e.getMessage();
			}
			catch (AttributeDoesNotExistException e)
			{
				try
				{
					mySDBClient.putAttributes(new PutAttributesRequest(DataSources.AUSERSTATS.concat(Utility.getAWSUserStatsTableSuffix(id)),
						id, getSpamRepAttr((currentCount+1))));
					return true;
				}
				catch (Exception e1)
				{
					FAILMSG = e1.getMessage();
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
				FAILMSG = e.getMessage();
			}
		}
		return false;
	}
	
	private List<ReplaceableAttribute> getSpamRepAttr(int the_count) {
		ArrayList<ReplaceableAttribute> list = new ArrayList<ReplaceableAttribute>();
		list.add(new ReplaceableAttribute(DataSources.ASPAMREPORTED, String.valueOf(the_count), true));
		return list;
		}


	
	private boolean deleteEvent() {
		if (mySDBClient != null && myEvent != null && myDomain != null && myAWSID != null)
		{
			try
			{
				mySDBClient.deleteAttributes(new DeleteAttributesRequest(myDomain, myAWSID));	
				return true;
			}
			catch (Exception e)
			{
				e.printStackTrace();
				FAILMSG = e.getMessage();
			}
		}
		return false;
	}

	/**
	 * @param mySpamReportID
	 */
	private boolean reportEventAsMe(String the_attrName)
	{
		try
		{
			if (the_attrName != null && myDomain != null && myAWSID != null)
			{
				PutAttributesRequest putRequest = new PutAttributesRequest(myDomain, myAWSID, getAttributesToAdd(the_attrName));
				mySDBClient.putAttributes(putRequest);
				return true;
			}
		}
		catch(Exception e)
		{
			FAILMSG = e.getMessage();
		}
		return false;
	}

	/**
	 * @param the_attrName
	 * @return
	 */
	private List<ReplaceableAttribute> getAttributesToAdd(String the_attrName) {
		if (the_attrName != null)
		{
			ArrayList<ReplaceableAttribute> attrList = new ArrayList<ReplaceableAttribute>();
			ReplaceableAttribute spamRepID = new ReplaceableAttribute(the_attrName, myUserID, true);
			attrList.add(spamRepID);
			ReplaceableAttribute spamRepTime = new ReplaceableAttribute(the_attrName.concat(DataSources.ATIME), 
					Utility.getAWSDBDateTime(Calendar.getInstance()), true);
			attrList.add(spamRepTime);
			return attrList;
		}
		return null;
	}

	/**
	 * @param the_result
	 * @return
	 */
	private String getReportID(GetAttributesResult the_result) {
		String retVal = null;
		if (the_result != null)
		{
			List<Attribute> list = the_result.getAttributes();
			retVal = whichSpamColNotPopulated(list);
		}
		return retVal;
	}

	/**
	 * @param list
	 * @return
	 */
	private String whichSpamColNotPopulated(List<Attribute> the_list) {
		if (the_list != null)
		{
			String[] valuesArr = new String[]{DataSources.ASPAM1, DataSources.ASPAM2, DataSources.ASPAM3, DataSources.ASPAM4, DataSources.ASPAM5, DataSources.ASPAM6};
			if (!alreadyReported(the_list))
			{
				return getSpamReporterCol(the_list, valuesArr);
			}
		}
		else
		{
			return DataSources.ASPAM1;
		}
		return null;
	}

	/**
	 * @param the_list 
	 * @param valuesArr
	 * @return
	 */
	private String getSpamReporterCol(List<Attribute> the_list, String[] the_valuesArr) {
		if (the_list != null && the_valuesArr != null)
		{
			ArrayList<String> attNames = getAttributeNamesFromList(the_list);
			if (attNames != null)
			{
				for (int i = 0; i < the_valuesArr.length; i++)
				{
					if (!attNames.contains(the_valuesArr[i]))
					{
						return the_valuesArr[i];
					}
				}
			}
			
		}
		return null;
	}

	/**
	 * @param the_list
	 * @return
	 */
	private ArrayList<String> getAttributeNamesFromList(List<Attribute> the_list) {
		if (the_list != null)
		{
			ArrayList<String> attNames = new ArrayList<String>(the_list.size());
			for (Attribute attr: the_list)
			{
				if (attr != null)
				{
					attNames.add(attr.getName());
				}
			}
			return attNames;
		}
		return null;
	}

	/**
	 * @param the_list 
	 * @param valuesArr 
	 * @return
	 */
	private boolean alreadyReported(List<Attribute> the_list) {
		if (the_list != null)
		{
				for (Attribute attr: the_list)
					{
						if (attr!=null && (attr.getName() != null && 
							 (attr.getName().equals(DataSources.ASPAM1) || attr.getName().equals(DataSources.ASPAM2) || 
									 attr.getName().equals(DataSources.ASPAM3) || attr.getName().equals(DataSources.ASPAM4) ||
									 attr.getName().equals(DataSources.ASPAM5))) && 
									 (attr.getValue() != null && attr.getValue().equals(myUserID)))
						{
							return true;
						}
					}
		}
		return false;
	}

	/**
	 * @param the_event
	 * @return
	 */
	private GetAttributesResult getSpamReporters() {
		myDomain = getDomain(myEvent.getCategory());
		myAWSID = AWSItemNameGenerator.generate(myEvent);
		if (mySDBClient != null && myDomain != null && myAWSID != null)
		{
			GetAttributesRequest request = new GetAttributesRequest(myDomain, myAWSID);
			request.setAttributeNames(getAttributesToQuery());
			return mySDBClient.getAttributes(request);
		}
		return null;
	}

	/**
	 * @return
	 */
	private static final Collection<String> getAttributesToQuery() {
		ArrayList<String> list = new ArrayList<String>();
		list.add(DataSources.ASPAM1);
		list.add(DataSources.ASPAM2);
		list.add(DataSources.ASPAM3);
		list.add(DataSources.ASPAM4);
		list.add(DataSources.ASPAM5);
		list.add(DataSources.ASPAM6);
		return list;
	}
	
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
    }

	/**
	 * 
	 */
	private void showSuccessToast() {
		if (mySaveSuccessful)
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

