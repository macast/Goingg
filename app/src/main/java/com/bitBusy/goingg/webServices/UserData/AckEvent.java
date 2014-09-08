/**
 * 
 */
package com.bitBusy.goingg.webServices.UserData;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.amazonaws.services.dynamodbv2.model.ConditionalCheckFailedException;
import com.amazonaws.services.simpledb.model.AttributeDoesNotExistException;
import com.amazonaws.services.simpledb.model.PutAttributesRequest;
import com.amazonaws.services.simpledb.model.ReplaceableAttribute;
import com.amazonaws.services.simpledb.model.UpdateCondition;
import com.bitBusy.goingg.database.setup.DBInteractor;
import com.bitBusy.goingg.database.setup.DBInteractor.EVENTMARKING;
import com.bitBusy.goingg.events.PublicEvent;
import com.bitBusy.goingg.utility.AWSItemNameGenerator;
import com.bitBusy.goingg.utility.Utility;
import com.bitBusy.goingg.webServices.awsSetup.AWSBaseOperation;
import com.bitBusy.goingg.webServices.awsSetup.ListenerAck;

/**
 * @author SumaHarsha
 *
 */
public class AckEvent extends AWSBaseOperation{
	private static final String SAVINGACK = "Saving acknowledgement..";
	private static String FAILMSG = "Sorry, acknowledgement not saved!";
	private String myDomain;
	private PublicEvent myEvent;
	private DBInteractor.EVENTMARKING myAck;
	private int myAWSUpdateTries;
	private boolean mySaveSuccess = true;
	private boolean isIncrement;
	private DBInteractor myDBInteractor;
	private String myEventID;
	
	public AckEvent(Context the_context, Object[] the_params)
	{
		super(the_context, the_params);
		myDBInteractor = new DBInteractor(myContext);
		setProgressDialogMsg(SAVINGACK);
	}
	

	

	/* (non-Javadoc)
	 * @see android.os.AsyncTask#doInBackground(Params[])
	 */
	@Override
	protected Void doInBackground(Void... arg0) {
		if (myParams != null && myParams.length == 3)
		{
			try{
				myEvent = (PublicEvent) myParams[0];
				myAck = (EVENTMARKING) myParams[1];
				isIncrement = (Boolean) myParams[2];
				myEventID = AWSItemNameGenerator.generate(myEvent);
				saveIntoAWS();
				if (mySaveSuccess)
				{
					if (isIncrement)
					{
						saveIntoLocalDB();
					}
					else
					{
						deleteFromDB();
					}
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return null;
	}
	/**
	 * 
	 */
	private void deleteFromDB() 
	{
		if (myDBInteractor != null)
		{
			mySaveSuccess = myDBInteractor.deleteEventMarking(myEventID);
		}
		else
		{
			mySaveSuccess = false;
		}
		if (!mySaveSuccess)
		{
			FAILMSG = "Local save of acknowledgement failed.";
		}
	}

	/**
	 * 
	 */
	private void saveIntoLocalDB()
	{
		if (myDBInteractor != null)
		{
			mySaveSuccess = myDBInteractor.markEvent(myEventID, myAck);
		}
		else
		{
			mySaveSuccess = false;
		}
		if (!mySaveSuccess)
		{
			FAILMSG = "Local save of acknowledgement failed.";
		}
	}

	/**
	 * @param event
	 * @param ack
	 */
	private void saveIntoAWS() 
	{
		if (myEvent != null)
		{
			initializeDomain();
			if (mySDBClient != null && myDomain != null)
			{
				updateAckCount(getCurrentCount());
			}
		}
	}
	
	/**
	 * @param currentCount
	 */
	private void updateAckCount(int currentCount) {
		if (myDomain != null && mySDBClient != null)
		{
			int newCount = isIncrement?(currentCount+1):(currentCount-1);
			try
			{
				mySDBClient.putAttributes(new PutAttributesRequest(myDomain,
						AWSItemNameGenerator.generate(myEvent), getAckAttribute((newCount)), 
						new UpdateCondition(myAck.name(), String.valueOf(currentCount), true)));
			}
			catch(ConditionalCheckFailedException e)
			{
				myAWSUpdateTries ++;
				if (myAWSUpdateTries < 10)
				{
					updateAckCount(getCurrentCount());
				}
				FAILMSG = e.getMessage();
				mySaveSuccess = false;
			}
			catch (AttributeDoesNotExistException e)
			{
				try
				{
					mySDBClient.putAttributes(new PutAttributesRequest(myDomain,
							AWSItemNameGenerator.generate(myEvent), getAckAttribute(newCount)));
				}
				catch (Exception e1)
				{
					FAILMSG = e1.getMessage();
					mySaveSuccess = false;
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
				FAILMSG = e.getMessage();
				mySaveSuccess = false;
			}
		}
	}		
	/**
	 * @param i
	 * @return
	 */
	private List<ReplaceableAttribute> getAckAttribute(int the_value)
	{
		ArrayList<ReplaceableAttribute> list = new ArrayList<ReplaceableAttribute>();
		list.add(new ReplaceableAttribute(myAck.name(), String.valueOf(the_value), true));
		return list;
	}

	/**
	 * 
	 */
	private void initializeDomain() 
	{
		if (myEvent != null && myEvent.getCategory() != null && myEvent.getCategory().getName() != null && 
				myEvent.getCategory().getName().name() != null)
		{
			myDomain = myEvent.getCategory().getName().name();
		}
	}

	/**
	 * @return
	 */
	private int getCurrentCount()
	{
		if (myEvent != null && myAck != null)
		{
			if (myAck == EVENTMARKING.Going)
			{
				return myEvent.getNumGoing();
			}
			else if (myAck == EVENTMARKING.Nope)
			{
				return myEvent.getNumNope();
			}
			else
			{
				return myEvent.getNumMaybe();
			}
		}
		return 0;
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
		if (!mySaveSuccess)
		{
			Utility.throwErrorMessage(myContext, "Acknowledgement save error", FAILMSG);
		}
		notifyListener(new ListenerAck(AckEvent.class.getName(),new Object[]{myAck, mySaveSuccess, isIncrement}));
	}
}
