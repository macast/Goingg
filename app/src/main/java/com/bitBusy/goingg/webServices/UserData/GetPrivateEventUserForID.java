/**
 * 
 */
package com.bitBusy.goingg.webServices.UserData;

import java.util.HashSet;
import java.util.List;

import android.content.Context;

import com.amazonaws.services.simpledb.model.Attribute;
import com.amazonaws.services.simpledb.model.Item;
import com.amazonaws.services.simpledb.model.SelectRequest;
import com.amazonaws.services.simpledb.model.SelectResult;
import com.bitBusy.goingg.events.DataSources;
import com.bitBusy.goingg.events.PrivateEvents.City;
import com.bitBusy.goingg.events.PrivateEvents.PrivateEventUser;
import com.bitBusy.goingg.events.PrivateEvents.PrivateEventUserName;
import com.bitBusy.goingg.messaging.setup.gcmSetup.GCMID;
import com.bitBusy.goingg.utility.Utility;
import com.bitBusy.goingg.webServices.awsSetup.AWSBaseOperation;
import com.bitBusy.goingg.webServices.awsSetup.ListenerAck;

/**
 * @author SumaHarsha
 *
 */
public class GetPrivateEventUserForID extends AWSBaseOperation{
	
	private static final String SEARCHING = "Retrieving user's details..";
	private static final String SELECTREQUEST = "Select " + DataSources.AGID + ", " + DataSources.AFNAME + ", " + 
	DataSources.ALNAME + ", " + DataSources.ACITY + " from ".concat(DataSources.AUSER);
	private String myFriendID;
	private PrivateEventUser myPrivateEventUserResult;
	
	public GetPrivateEventUserForID(Context the_context, Object[] the_params)
	{
		super(the_context, the_params);
		setProgressDialogMsg(SEARCHING);
	}
	
	@Override
	protected Void doInBackground(Void... arg0) 
	{
		if (myParams != null && myParams.length > 0 && myParams[0] != null){
			try{
				myFriendID = (String) myParams[0];
				getUserDets();
			}catch(Exception e){
		  }
		}
		return null;
	}

	private void getUserDets()
	{
		if (mySDBClient != null)
		{
			SelectResult result = submitQuery();
			readResults(result);			
		}		
	}

	private void readResults(SelectResult the_result)
	{
		if (the_result != null && the_result.getItems() != null && the_result.getItems().size() > 0)
		{
			List<Item> items = the_result.getItems();
			if (items != null && items.size() == 1){
				readResultAttributes(items.get(0));
			}
		}
	}

	private void readResultAttributes(Item the_item) {
		if (the_item != null && the_item.getAttributes() != null && the_item.getAttributes().size() > 0){
			List<Attribute> the_attributes = the_item.getAttributes();
			String fname = null, lname = null, city = null;
			HashSet<GCMID> ids = new HashSet<GCMID>();
			for (Attribute attr: the_attributes){
				if (attr != null && attr.getName() != null){
					String name = attr.getName();
						if (name.equals(DataSources.AGID)){
							 ids.add(new GCMID(attr.getValue()));
						}
						else if (name.equals(DataSources.AFNAME)){
							fname = attr.getValue();
						}
						else if (name.equals(DataSources.ALNAME)){
							lname = attr.getValue();
						}
						else if (name.equals(DataSources.ACITY)){
							city = attr.getValue();
						}
					}
				}
			myPrivateEventUserResult = new PrivateEventUser(the_item.getName(), new PrivateEventUserName(fname, lname), new City(city), ids, null);
		}
	}

	private SelectResult submitQuery()
	{
		String tableSuffix = Utility.getAWSUserStatsTableSuffix(myFriendID);
		if (mySDBClient != null && tableSuffix != null)
		{
			SelectRequest selectRequest = new SelectRequest(SELECTREQUEST.concat(tableSuffix).concat(getWhereClause()), true);
			selectRequest.setConsistentRead(true);	
			return mySDBClient.select(selectRequest);		
		}
		return null;
	}

	private String getWhereClause()
	{
		if (myFriendID != null)
		{
			StringBuilder builder = new StringBuilder(" where ");
			builder.append(DataSources.AITEMNAME).append(" = '").append(myFriendID).append("'");
			return builder.toString();
		}
		return null;
	}
	
	@Override
    protected void onPostExecute(Void param)
    {
		closeProgressDialog();
		notifyListener(new ListenerAck(GetPrivateEventUserForID.class.getName(), new Object[]{myPrivateEventUserResult}));
    }
}
