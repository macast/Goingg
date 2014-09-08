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
import com.bitBusy.goingg.dialog.AddContactDialog;
import com.bitBusy.goingg.events.DataSources;
import com.bitBusy.goingg.events.PrivateEvents.City;
import com.bitBusy.goingg.events.PrivateEvents.PrivateEventUser;
import com.bitBusy.goingg.events.PrivateEvents.PrivateEventUserName;
import com.bitBusy.goingg.messaging.setup.gcmSetup.GCMID;
import com.bitBusy.goingg.utility.CurrentLoginState;
import com.bitBusy.goingg.utility.Encryption;
import com.bitBusy.goingg.webServices.awsSetup.AWSBaseOperation;
import com.bitBusy.goingg.webServices.awsSetup.ListenerAck;

/**
 * @author SumaHarsha
 *
 */
public class SearchForUser extends AWSBaseOperation
{
	
	private static final String SEARCHING = "Searching for user..";
	private static final String SELECTREQUEST = "Select * from ".concat(DataSources.AUSER);
	private PrivateEventUser myToFindContact;
	private HashSet<PrivateEventUser> myFoundContacts;
	private int myDomainSuffixIndex;
	
	public SearchForUser(Context the_context, Object[] the_params)
	{
		super(the_context, the_params);
		setProgressDialogMsg(SEARCHING);
	}
	
	@Override
	protected Void doInBackground(Void... arg0) 
	{
		if (myParams != null && myParams.length == 2 && myParams[0] != null && 
				myParams[0] instanceof PrivateEventUser && myParams[1] != null && myParams[1] instanceof Integer)
		{
			try{
				myToFindContact = (PrivateEventUser) myParams[0];
				myDomainSuffixIndex = (Integer) myParams[1];
				searchForUser();
			}catch(Exception e){
			}
		}
		return null;
	}

	private void searchForUser()
	{
		if (mySDBClient != null)
		{
			SelectResult result = submitQuery();
			readResults(result);			
		}		
	}

	
	/**
	 * @param result
	 */
	private void readResults(SelectResult the_result)
	{
		if (the_result != null && the_result.getItems() != null && the_result.getItems().size() > 0)
		{
			myFoundContacts = new HashSet<PrivateEventUser>();
			List<Item> items = the_result.getItems();
			for (Item item: items)
			{			
				if (item != null)
					//&& item.getAttributes() != null && item.getAttributes().size() > 0 && item.getAttributes().get(0) != null &&
					//	item.getAttributes().get(0).getName() != null && item.getAttributes().get(0).getName().equals(DataSources.AITEMNAME))
				{
					myFoundContacts.add(getPrivateEventUser(item));
				}
			}
		}
	}
	
	private PrivateEventUser getPrivateEventUser(Item the_item) {
		if (the_item != null){
			String userid= the_item.getName();
			List<Attribute> attributes = the_item.getAttributes();
			String fname = null, lname = null, city = null;
			HashSet<GCMID> ids = new HashSet<GCMID>();
			HashSet<String> friends = new HashSet<String>();
			String currentUser = CurrentLoginState.getCurrentUser(myContext);
			if (attributes != null){
				for (Attribute attr: attributes){
					if (attr != null && attr.getName() != null){
						if (attr.getName().equals(DataSources.ACITY)){
							city = attr.getValue();
						}
						else if (attr.getName().equals(DataSources.AFNAME)){
							fname = attr.getValue();
						}
						else if (attr.getName().equals(DataSources.ALNAME)){
							lname = attr.getValue();
						}
						else if (attr.getName().equals(DataSources.AGID)){
							ids.add(new GCMID(attr.getValue()));
						}
						else if (attr.getName().equals(DataSources.AFRIENDNAME)){
							if ((currentUser != null && the_item.getName().equals(currentUser)) || (attr.getValue() != null && 
									attr.getValue().equals(currentUser))){
								friends.add(attr.getValue());
							}
						}
					}
				}
			}
			return new PrivateEventUser(userid, new PrivateEventUserName(fname, lname), new City(city), ids , friends);
		}
		return null;
	}

	private SelectResult submitQuery()
	{
		if (mySDBClient != null)
		{
			SelectRequest selectRequest = new SelectRequest(SELECTREQUEST.concat(AddContactDialog.SUFFIXARR[myDomainSuffixIndex]).
					concat(getWhereClause()), true);
			selectRequest.setConsistentRead(true);	
			return mySDBClient.select(selectRequest);		
		}
		return null;
	}

	/**
	 * @return
	 */
	private String getWhereClause()
	{
		if (myToFindContact != null)
		{
			StringBuilder builder = new StringBuilder(" where ");
			if (myToFindContact.getCity() != null && myToFindContact.getCity().getName()!= null && myToFindContact.getName() != null)
			{
				builder.append(DataSources.ACITY).append(" = '").append(Encryption.encryptTwoWay(myContext, 
						myToFindContact.getCity().getName(), myToFindContact.getUserName())).append("'");
				if (myToFindContact.getName().getFirstName() != null && myToFindContact.getName().getFirstName().length() > 0)
				{
					builder.append(" and ").append(DataSources.AFNAME).append(" = '").
					append(Encryption.encryptTwoWay(myContext, myToFindContact.getName().getFirstName(), myToFindContact.getUserName())).append("'");
				}
				if (myToFindContact.getName().getLastName() != null && myToFindContact.getName().getLastName().length() > 0)
				{
					builder.append(" and ").append(DataSources.ALNAME).append(" = '").
					append(Encryption.encryptTwoWay(myContext, myToFindContact.getName().getLastName(), myToFindContact.getUserName())).append("'");
				}			
			}
			return builder.toString();
		}
		return null;
	}

	@Override
    protected void onPostExecute(Void param)
    {
		closeProgressDialog();
		notifyListener(new ListenerAck(SearchForUser.class.getName(),new Object[]{ myFoundContacts, myDomainSuffixIndex}));
    }	
}
