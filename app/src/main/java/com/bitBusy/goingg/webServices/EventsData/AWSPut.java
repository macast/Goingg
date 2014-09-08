/**
 * 
 */
package com.bitBusy.goingg.webServices.EventsData;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.widget.Toast;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.dynamodbv2.model.ConditionalCheckFailedException;
import com.amazonaws.services.simpledb.model.AttributeDoesNotExistException;
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
import com.bitBusy.goingg.webServices.awsSetup.AWSCredentialsStore;

/**
 * @author SumaHarsha
 *
 */
public class AWSPut extends AWSBaseOperation{
	
	public static final String TOKEN = "TOKEN";
	
	private static final String SAVINGEVENT = "Saving your event..";
	
	private static final String SUCCESSMSG = "Event created!";
	
	private static final String USERSTATSUPDATEFAILMSG = "Event created but user stats could not be updated!";
	
	private String FAILMSG = "Event not created!";
		
	private boolean mySaveSuccessful = false;
		
	private int myStatsUpdateTries = 0;
	
	public AWSPut(Context the_context, Object[] the_params)
	{
		super(the_context, the_params);
		myProgressDialog = new ProgressDialog(the_context);
		setProgressDialogMsg(SAVINGEVENT);
	}
	
	/* (non-Javadoc)
	 * @see android.os.AsyncTask#doInBackground(Params[])
	 */
	@Override
	protected Void doInBackground(Void... params) {
		if (myParams != null && myParams.length > 0)
		{
			try{
				PublicEvent event = (PublicEvent) myParams[0];
				if (event != null)
					{
						saveEvent(event);
					}
			}catch(Exception e){
				
			}
		}
		return null;
	}

	

	/**
	 * @param event
	 */
	private void saveEvent(PublicEvent the_event) {
		if (the_event != null)
		{
			try
			{
				String domain = getDomain (the_event.getCategory());
				mySDBClient = AWSCredentialsStore.getInstance().getSDBClient();
				if (domain != null && mySDBClient != null)
				{
					ArrayList<ReplaceableAttribute> attributes = getAllReplaceableEventAttributes(the_event);
					if (attributes != null)
					{
						mySDBClient.setEndpoint(DataSources.AENDPNT);
						mySDBClient.putAttributes(new PutAttributesRequest(domain, AWSItemNameGenerator.generate(the_event), attributes));
						updateUserStats();
					}
				}
			}
			catch(AmazonServiceException e)
			{
				mySaveSuccessful = false;
				FAILMSG = e.getMessage();
			}
		}
	}

	/**
	 * @param sdbClient
	 * @return
	 */
	private void updateUserStats() {
		String userID = UUIDGenerator.id(myContext);
		if (mySDBClient != null && userID != null)
		{
			User user = getUserCurrentEventsSubCount(userID);
			updateEventsCount(user);
		}
		else
		{
			mySaveSuccessful = false;
			FAILMSG = USERSTATSUPDATEFAILMSG;
		}
	}

	/**
	 * @param the_sdbClient
	 * @param user
	 * @return
	 */
	private User getUserCurrentEventsSubCount(String the_user) {
		UserStatsQuerierWorker userStats = new UserStatsQuerierWorker(mySDBClient, the_user);
		return userStats.getUserStats();		
	}

	/**
	 * @param user
	 * @param currentEventsCount
	 * @return
	 */
	private void updateEventsCount(User the_user) {
		if (the_user != null && the_user.getID() != null)
		{ 
			String id = the_user.getID();
			int currentCount = the_user.getEventsSubmitted();
			try
			{
				mySDBClient.putAttributes(new PutAttributesRequest(DataSources.AUSERSTATS.concat(Utility.getAWSUserStatsTableSuffix(id)),
						id, getEventsSubAttr((currentCount+1)), 
						new UpdateCondition(DataSources.AEVENTSSUBMITTED, String.valueOf(currentCount), true)));
				mySaveSuccessful = true;
			}
			catch(ConditionalCheckFailedException e)
			{
				myStatsUpdateTries++;
				if (myStatsUpdateTries < 10)
				{
					updateUserStats();
				}
				mySaveSuccessful = false;
				FAILMSG = e.getMessage();
			}
			catch (AttributeDoesNotExistException e)
			{
				try
				{
					mySDBClient.putAttributes(new PutAttributesRequest(DataSources.AUSERSTATS.concat(Utility.getAWSUserStatsTableSuffix(id)),
						id, getEventsSubAttr((currentCount+1))));
					mySaveSuccessful = true;
				}
				catch (Exception e1)
				{
					mySaveSuccessful = false;
					FAILMSG = e1.getMessage();
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
				mySaveSuccessful = false;
				FAILMSG = e.getMessage();
			}
		}
	}
		


	private List<ReplaceableAttribute> getEventsSubAttr(int the_count) {
		ArrayList<ReplaceableAttribute> list = new ArrayList<ReplaceableAttribute>();
		list.add(new ReplaceableAttribute(DataSources.AEVENTSSUBMITTED, String.valueOf(the_count), true));
		return list;
		}

	private String getDomain(EventCategory the_category) {
		if (the_category != null && the_category.getName() != null && the_category.getName().name() != null)
		{
			return the_category.getName().name();
		}
		return null;
	}
	/**
	 * @return
	 */
	private String getUniqueID() {
		return UUIDGenerator.id(myContext);
	}

	/**
	 * @param the_event
	 * @return
	 */
	private ArrayList<ReplaceableAttribute> getAllReplaceableEventAttributes(
			PublicEvent the_event) {
		if (the_event !=null)
		{
				ArrayList<ReplaceableAttribute> list = new ArrayList<ReplaceableAttribute> ();

			list.addAll(getAddressList(the_event));
			ReplaceableAttribute eventDesc = 
					  new ReplaceableAttribute(DataSources.ADESC, the_event.getDescription()!=null?the_event.getDescription():"", true);
			list.add(eventDesc);
			String endtime = Utility.getAWSDBDateTime(the_event.getEndDateTime());
			ReplaceableAttribute eventEnd = 
					  new ReplaceableAttribute(DataSources.AEND,endtime != null?endtime:"" , true);
			list.add(eventEnd);
			ReplaceableAttribute eventImg = 
					  new ReplaceableAttribute(DataSources.AIMG, "", true);
			list.add(eventImg);
			ReplaceableAttribute eventLat = 
					  new ReplaceableAttribute(DataSources.ALAT, the_event.getCoordinates()!=null?String.valueOf(the_event.getCoordinates().latitude):null, true);
			list.add(eventLat);
			ReplaceableAttribute eventLink = 
					  new ReplaceableAttribute(DataSources.ALINK, the_event.getInfoLink()!=null?the_event.getInfoLink().getURL():"", true);
			list.add(eventLink);
			ReplaceableAttribute eventLng = 
					  new ReplaceableAttribute(DataSources.ALNG, the_event.getCoordinates()!=null?String.valueOf(the_event.getCoordinates().longitude):null, true);
			list.add(eventLng);
			ReplaceableAttribute eventNme = 
					  new ReplaceableAttribute(DataSources.ANAME, the_event.getName()!=null?the_event.getName():"", true);
			list.add(eventNme);
			ReplaceableAttribute eventPrice = 
					  new ReplaceableAttribute(DataSources.APRICE, the_event.getPrice()!=null?the_event.getPrice():"", true);
			list.add(eventPrice);
			String starttime = Utility.getAWSDBDateTime(the_event.getStartDateTime());
			ReplaceableAttribute eventStart = 
					  new ReplaceableAttribute(DataSources.ASTART, starttime!=null?starttime:"", true);
			list.add(eventStart);	
			ReplaceableAttribute createdBy = 
					  new ReplaceableAttribute(DataSources.ACREATEDBY, getUniqueID(), true);
			list.add(createdBy);		
			String datetime = Utility.getAWSDBDateTime(Calendar.getInstance());
			ReplaceableAttribute createdOn = 
					  new ReplaceableAttribute(DataSources.ACREATEDON,datetime!=null?datetime:"" , true);
			list.add(createdOn);				
			return list;
		}
		return null;
	}
	

	/**
	 * @param the_event
	 * @return
	 */
	private ArrayList<ReplaceableAttribute> getAddressList(PublicEvent the_event) {
		ArrayList<ReplaceableAttribute> list = new ArrayList<ReplaceableAttribute>();
		if (the_event != null)
		{
			String street = the_event.getAddress().getStreetAdd();
			String city = the_event.getAddress().getCity();
			String state = the_event.getAddress().getState();
			String country = the_event.getAddress().getCountry();
			String zip = the_event.getAddress().getPostalCode();
			ReplaceableAttribute streetAtt = 
					  new ReplaceableAttribute(DataSources.ASTREETADD, 
							 street!=null?street:"", true);
			list.add(streetAtt);
			ReplaceableAttribute cityAtt = 
					  new ReplaceableAttribute(DataSources.ACITYADD, 
							 city!=null?city:"", true);
			list.add(cityAtt);
			ReplaceableAttribute stateAtt = 
					  new ReplaceableAttribute(DataSources.ASTATEADD, 
							 state!=null?state:"", true);
			list.add(stateAtt);
			ReplaceableAttribute countryAtt = 
					  new ReplaceableAttribute(DataSources.ACOUNTRYADD, 
							 country!=null?country:"", true);
			list.add(countryAtt);
			ReplaceableAttribute zipAtt = 
					  new ReplaceableAttribute(DataSources.AZIPADD, 
							 zip!=null?zip:"", true);
			list.add(zipAtt);
		}
		return list;
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
