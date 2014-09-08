/**
 * 
 */
package com.bitBusy.goingg.webServices.UserData;

import java.util.List;

import com.amazonaws.services.simpledb.AmazonSimpleDBClient;
import com.amazonaws.services.simpledb.model.Attribute;
import com.amazonaws.services.simpledb.model.Item;
import com.amazonaws.services.simpledb.model.SelectRequest;
import com.amazonaws.services.simpledb.model.SelectResult;
import com.bitBusy.goingg.events.DataSources;
import com.bitBusy.goingg.events.User;
import com.bitBusy.goingg.utility.Utility;

/**
 * @author SumaHarsha
 *
 */
public class UserStatsQuerierWorker {
	
	private static final String SELECTREQUEST = "Select * from ".concat(DataSources.AUSERSTATS);

	private AmazonSimpleDBClient mySDBClient;
	
	private String myID;
	
	public UserStatsQuerierWorker(AmazonSimpleDBClient the_client, String the_id)
	{
		mySDBClient = the_client;
		myID = the_id;
	}
	
	public User getUserStats()
	{
		if (mySDBClient != null)
		{
			SelectResult response = requestData();
			return getUser(response);
		}
		return null;
	}
	/**
	 * @param response
	 * @return
	 */
	private User getUser(SelectResult the_response) {
		if (the_response != null && the_response.getItems() != null && the_response.getItems().size() > 0)
		{
			Item item = the_response.getItems().get(0);
			return getUserFromItemAttributes(item.getAttributes());
		}
		return new User(myID, 0,0,0);
	}

	/**
	 * @param attributes
	 * @return
	 */
	private User getUserFromItemAttributes(List<Attribute> the_attributes) {
		if (the_attributes != null)
		{
			int events = 0, spamGen = 0, spamRep = 0;
			try
			{
				for (Attribute attr: the_attributes)
				{
					if (attr != null && attr.getName() != null)
					{
						if (attr.getName().equals(DataSources.AEVENTSSUBMITTED))
						{
							events = Integer.parseInt(attr.getValue());
						}
						else if (attr.getName().equals(DataSources.ASPAMGENERATED))
						{
							spamGen = Integer.parseInt(attr.getValue());
						}
						else if (attr.getName().equals(DataSources.ASPAMREPORTED))
						{
							spamRep = Integer.parseInt(attr.getValue());
						}
					}
				}
			}
			catch(Exception e)
			{
				
			}
			return new User(myID,events, spamGen, spamRep);
		}
		return null;
	}

	
	private SelectResult requestData() {
		SelectRequest selectRequest = new SelectRequest(getSelectStatement(), true);
		selectRequest.setConsistentRead(true);	
		return mySDBClient.select(selectRequest);		
	}

	/**
	 * @return
	 */
	private String getSelectStatement() {
		return 
				SELECTREQUEST.concat(Utility.getAWSUserStatsTableSuffix(myID)).concat(" where itemName() = '").concat(myID).concat("'");
	}

	
}
