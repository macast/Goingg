/**
 * 
 */
package com.bitBusy.goingg.webServices.SignIn;

import java.util.HashSet;
import java.util.List;

import android.content.Context;

import com.amazonaws.services.simpledb.model.Attribute;
import com.amazonaws.services.simpledb.model.Item;
import com.amazonaws.services.simpledb.model.SelectRequest;
import com.amazonaws.services.simpledb.model.SelectResult;
import com.bitBusy.goingg.database.setup.DBInteractor;
import com.bitBusy.goingg.events.DataSources;
import com.bitBusy.goingg.events.PrivateEvents.City;
import com.bitBusy.goingg.events.PrivateEvents.PrivateEventUser;
import com.bitBusy.goingg.events.PrivateEvents.PrivateEventUserName;
import com.bitBusy.goingg.messaging.setup.gcmSetup.GCMID;
import com.bitBusy.goingg.utility.Encryption;
import com.bitBusy.goingg.utility.Utility;
import com.bitBusy.goingg.webServices.awsSetup.AWSBaseOperation;
import com.bitBusy.goingg.webServices.awsSetup.ListenerAck;

/**
 * @author SumaHarsha
 *
 */
public class AWSSignIn extends AWSBaseOperation{

	private static final String SELECTREQUEST = "Select * from ".concat(DataSources.AUSER);
	private static final String SIGNINGIN = "Signing in..";
	private String myUsername;
	private String myPwd;
	private SelectResult mySelectResponse;
	private boolean isSignedIn;
	
	public AWSSignIn(Context the_context, Object[] the_params)
	{
		super(the_context, the_params);
		setProgressDialogMsg(SIGNINGIN);
	}
	
	/* (non-Javadoc)
	 * @see android.os.AsyncTask#doInBackground(Params[])
	 */
	@Override
	protected Void doInBackground(Void... arg0) {
		if (myParams != null)
		{
			try{
				myUsername = (String) myParams[0];
				myPwd = (String) myParams[1];
				if (myUsername != null && myPwd != null)
				{
					isSignedIn = signIn();
				}
			}catch(Exception e){
			}
		}
		return null;
	}

	
	private boolean signIn()
	{
		return confirmCredentials();
	}

	/**
	 * @return
	 */
	private boolean confirmCredentials()
	{
		String userRegPwd = queryUserPwd();
		String encryptedPwdEntry = String.valueOf(Encryption.encryptOneWay(myPwd).hashCode());
		if (userRegPwd != null && encryptedPwdEntry != null &&
				userRegPwd.equals(encryptedPwdEntry))
		{
			return true;
		}
		return false;
	}
	/**
	 * @return
	 */
	private String queryUserPwd() 
	{
		mySelectResponse = getSelectResponse();
		return readQueryResultPwd();
	}
	/**
	 * @param queryResult
	 * @return
	 */
	private String readQueryResultPwd()
	{
		if (mySelectResponse != null && mySelectResponse.getItems() != null && mySelectResponse.getItems().size() > 0)
		{
			Item item = mySelectResponse.getItems().get(0);
			return getPWDfromAttributes(item.getAttributes());
		}	
		return null;
	}


	private String getPWDfromAttributes(List<Attribute> the_attributes) {
	if (the_attributes != null)
	{
		try
		{
			for (Attribute attr: the_attributes)
			{
				if (attr != null && attr.getName() != null)
				{
					if (attr.getName().equals(DataSources.AULCK))
					{
						return attr.getValue();
					}
				}
			}
		}
		catch(Exception e)
		{
			//Utility.throwErrorMessage(myContext, UNABLETOCONNECTTOAWSTITLE, e.getMessage());
		}
	}
	return null;
}
	/**
	 * @return
	 */
	private SelectResult getSelectResponse() {
		String selectSttment = getSelectStatement();
		if (selectSttment != null && mySDBClient != null)
		{
			SelectRequest selectRequest = new SelectRequest(selectSttment, true);
			selectRequest.setConsistentRead(true);	
			return mySDBClient.select(selectRequest);		
		}
		return null;
	}
	
	private String getSelectStatement() {
		String domain = Utility.getAWSUserStatsTableSuffix(myUsername);
		if (domain != null)
		{
		return 
				SELECTREQUEST.concat(domain).
				concat(" where itemName() = '").concat(myUsername).concat("'");
		}
		return null;
	}
	
	@Override
    protected void onPostExecute(Void param)
    {
		closeProgressDialog();
		if (isSignedIn){
			saveMe();
		}
		notifyListener(new ListenerAck(AWSSignIn.class.getName(), new Object[]{isSignedIn}));
    }
	
	private void saveMe()
	{
		
		if (mySelectResponse != null && mySelectResponse.getItems() != null && mySelectResponse.getItems().size() > 0)
		{
			Item item = mySelectResponse.getItems().get(0);
			new DBInteractor(myContext).saveUser(getPrivateEventUserObject(item));
			//Utility.initiateFriendsList(myContext);
		}	
	}

	private PrivateEventUser getPrivateEventUserObject(Item the_item) {
		if (the_item != null && the_item.getAttributes() != null && the_item.getName() != null){
			List<Attribute> attributes = the_item.getAttributes();
			String userid = the_item.getName();
			String fname = null, lname = null, city = null; 
			HashSet<GCMID> gcmid = new HashSet<GCMID>();
			HashSet<String> friendids = new HashSet<String>();
			for (Attribute attr: attributes){
				if (attr != null && attr.getName() != null){
					String attrName = attr.getName();
					if (attrName.equals(DataSources.AFNAME)){
						fname = attr.getValue();
					} else if (attrName.equals(DataSources.ALNAME)){
						lname = attr.getValue();
					} else if (attrName.equals(DataSources.ACITY)){
						city = attr.getValue();
					} else if (attrName.equals(DataSources.AGID)){
						gcmid.add(new GCMID(attr.getValue()));
					} else if (attrName.equals(DataSources.AFNAME)){
						friendids.add(attr.getValue());
					}
				}
			}
			return new PrivateEventUser(userid, new PrivateEventUserName(fname, lname), new City(city), gcmid, friendids);
		}
		return null;
	}
}
