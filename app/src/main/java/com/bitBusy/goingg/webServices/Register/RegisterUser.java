/**
 * 
 */
package com.bitBusy.goingg.webServices.Register;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import android.content.Context;

import com.amazonaws.services.dynamodbv2.model.ConditionalCheckFailedException;
import com.amazonaws.services.simpledb.model.DeleteAttributesRequest;
import com.amazonaws.services.simpledb.model.PutAttributesRequest;
import com.amazonaws.services.simpledb.model.ReplaceableAttribute;
import com.amazonaws.services.simpledb.model.UpdateCondition;
import com.bitBusy.goingg.database.setup.DBInteractor;
import com.bitBusy.goingg.events.DataSources;
import com.bitBusy.goingg.events.PrivateEvents.City;
import com.bitBusy.goingg.events.PrivateEvents.PrivateEventUser;
import com.bitBusy.goingg.events.PrivateEvents.PrivateEventUserName;
import com.bitBusy.goingg.messaging.setup.gcmSetup.GCMID;
import com.bitBusy.goingg.utility.CurrentLoginState;
import com.bitBusy.goingg.utility.Encryption;
import com.bitBusy.goingg.utility.UUIDGenerator;
import com.bitBusy.goingg.utility.Utility;
import com.bitBusy.goingg.webServices.awsSetup.AWSBaseOperation;
import com.bitBusy.goingg.webServices.awsSetup.ListenerAck;

/**
 * @author SumaHarsha
 *
 */
public class RegisterUser extends AWSBaseOperation{

	public static enum RETMSGS {Success, UserLimitOnDeviceHit, UserNameAlreadyExists, GeneralFail};
	
	private static final String REGISTERING = "Registering you..";
		
	private String myUserName;
	
	private String myPassword;
	
	private String myFname;
	
	private String myLname;
	
	private String myCity;
	
	private String myUUID;
	
	private GCMID myGCMID;
		
	private RETMSGS myReturnCode;
			
	private String mySecurityKeyword;
	
	public RegisterUser(Context the_context, Object[] the_params)
	{
		super(the_context, the_params);
		setProgressDialogMsg(REGISTERING);
	}
	
	/* (non-Javadoc)
	 * @see android.os.AsyncTask#doInBackground(Params[])
	 */
	@Override
	protected Void doInBackground(Void... arg0) {
		if(myParams != null && myParams.length == 6)
		{
			try{
				myUserName = (String) myParams[0];
				myPassword = (String) myParams[1];
				mySecurityKeyword = (String) myParams[2];
				myFname = (String) myParams[3];
				myLname = (String) myParams[4];
				myCity = (String) myParams[5];
				myUUID = UUIDGenerator.id(myContext);
				myGCMID = new GCMID(CurrentLoginState.getGCMID(myUserName, myContext));
				if (myUserName != null && myPassword != null && myUUID != null && mySDBClient != null && mySecurityKeyword != null && 
						myFname != null && myLname != null && myCity != null && myGCMID != null)
				{
					CurrentLoginState.setUserLoggedInGoing(myContext, true, myUserName);
					if (insertIntoUserTable())
					{
						if (insertIntoUserStatsTable())
						{
							myReturnCode = RETMSGS.Success;
							saveInDB();
							return null;
						}
						deleteFromUserTable();
						myReturnCode = RETMSGS.UserLimitOnDeviceHit;
						CurrentLoginState.setUserLoggedInGoing(myContext, false, "");
						return null;			
					}
					myReturnCode = RETMSGS.UserNameAlreadyExists;
					CurrentLoginState.setUserLoggedInGoing(myContext, false, "");
					return null;
				}
			}catch(Exception e){
				e.printStackTrace();
			}
			myReturnCode = RETMSGS.GeneralFail;
			CurrentLoginState.setUserLoggedInGoing(myContext, false, "");
		}
			return null;
	}

	private void saveInDB() {
		HashSet<GCMID> gcmid = new HashSet<GCMID>();
		gcmid.add(myGCMID);
		new DBInteractor(myContext).saveUser(new PrivateEventUser(myUserName, 
				new PrivateEventUserName(myFname,myLname), new City(myCity), gcmid, null));
	}

	/**
	 * 
	 */
	private void deleteFromUserTable()
	{
		String tableSuffix = Utility.getAWSUserStatsTableSuffix(myUserName);
		if (tableSuffix != null && mySDBClient != null && myUserName != null)
		{
			try
			{
				mySDBClient.deleteAttributes(new DeleteAttributesRequest(DataSources.AUSER.concat(tableSuffix), myUserName));	
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
			
	}

	private boolean insertIntoUserStatsTable()
	{
		String tableSuffix = Utility.getAWSUserStatsTableSuffix(myUUID);
		if (tableSuffix != null && mySDBClient != null && myUUID != null && myUserName != null)
		{
			try
			{
				UpdateCondition updateCond = new UpdateCondition(DataSources.AUNAME, null, false);
				PutAttributesRequest request = new PutAttributesRequest(DataSources.AUSERSTATS.concat(tableSuffix), 
						myUUID, getUserStatsTableAttributes(),updateCond);			
				mySDBClient.putAttributes(request);
				return true;
			}
			catch(ConditionalCheckFailedException e)
			{
				myReturnCode = RETMSGS.UserLimitOnDeviceHit;
			}
			catch (Exception e)
			{
				myReturnCode = RETMSGS.GeneralFail;
			}		

		}
		return false;
	}

	/**
	 * @return
	 */
	private List<ReplaceableAttribute> getUserStatsTableAttributes() {
		ArrayList<ReplaceableAttribute> list = new ArrayList<ReplaceableAttribute>();
		list.add(new ReplaceableAttribute(DataSources.AUNAME, myUserName, false));
		return list;
	}

	/**
	 * @return
	 */
	private boolean insertIntoUserTable() 
	{
		String tableSuffix = Utility.getAWSUserStatsTableSuffix(myUserName);
		if (tableSuffix != null && mySDBClient != null && myUserName != null && myPassword != null)
		{
			try
			{
				UpdateCondition updateCond = new UpdateCondition(DataSources.AULCK, null, false);
				PutAttributesRequest request = new PutAttributesRequest(DataSources.AUSER.concat(tableSuffix),
						myUserName, getUserTableAttributes(),
												updateCond);
				mySDBClient.putAttributes(request);
				return true;
			}
			catch(ConditionalCheckFailedException e)
			{
				myReturnCode = RETMSGS.UserNameAlreadyExists;
			}
			catch (Exception e)
			{
				e.printStackTrace();
				myReturnCode = RETMSGS.GeneralFail;
			}		
		}
		return false;
	}

	private List<ReplaceableAttribute> getUserTableAttributes() 
	{
		ArrayList<ReplaceableAttribute> list = new ArrayList<ReplaceableAttribute>();
		list.add(new ReplaceableAttribute(DataSources.AULCK, String.valueOf(Encryption.encryptOneWay(myPassword).hashCode()), false));
		list.add(new ReplaceableAttribute(DataSources.ASECKEYWORD, String.valueOf(Encryption.encryptOneWay(mySecurityKeyword.
				toLowerCase(Locale.ENGLISH)).hashCode()), false));
		list.add(new ReplaceableAttribute(DataSources.AFNAME, Encryption.encryptTwoWay(myContext, myFname, myUserName), false));
		list.add(new ReplaceableAttribute(DataSources.ALNAME, Encryption.encryptTwoWay(myContext,myLname, myUserName), false));
		list.add(new ReplaceableAttribute(DataSources.ACITY, Encryption.encryptTwoWay(myContext,myCity, myUserName),false));
		list.add(new ReplaceableAttribute(DataSources.AGID, myGCMID.getGCMID(), false));
		String uuid = UUIDGenerator.id(myContext);
		if (uuid != null){
			list.add(new ReplaceableAttribute(DataSources.AUID, String.valueOf(Encryption.encryptOneWay(uuid.
					toLowerCase(Locale.ENGLISH)).hashCode()), false));		
		}			
		return list;
	}
	
	@Override
    protected void onPostExecute(Void params)
    {
		closeProgressDialog();
		notifyListener(new ListenerAck(RegisterUser.class.getName(), new Object[]{myReturnCode}));
    }
}
