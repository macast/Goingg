/**
 * 
 */
package com.bitBusy.goingg.webServices.UserData;

import java.util.ArrayList;

import android.content.Context;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.simpledb.model.PutAttributesRequest;
import com.amazonaws.services.simpledb.model.ReplaceableAttribute;
import com.bitBusy.goingg.events.DataSources;
import com.bitBusy.goingg.utility.CurrentLoginState;
import com.bitBusy.goingg.utility.Utility;
import com.bitBusy.goingg.webServices.awsSetup.AWSBaseOperation;

/**
 * @author SumaHarsha
 *
 */
public class SaveMyGCMID extends AWSBaseOperation{
	
		private static final String ADDINGCONTACT = "Adding contact..";
		private static final String IDNOTSTORED = "Storage error";
		private String myGCMID;
		private String myUserName;
		private String myError;
		private boolean isStored = false;
		
		public SaveMyGCMID(Context the_context, Object[] the_params)
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
			if (myParams != null && myParams.length >= 2)
			{
				try{
					myUserName = (String) myParams[0];
					myGCMID = (String)myParams[1];
					saveID();
				}catch(Exception e){
				}
			}
			return null;
		}

		private void saveID() 
		{
			try
			{
				if (mySDBClient != null && myGCMID != null && myUserName != null ){
					String suffix = Utility.getAWSUserStatsTableSuffix(myUserName);
					ArrayList<ReplaceableAttribute> attributes = getReplaceableAttribute();
					if (attributes != null && suffix != null)
					{
						mySDBClient.setEndpoint(DataSources.AENDPNT);
						mySDBClient.putAttributes(new PutAttributesRequest(DataSources.AUSER.concat(suffix),
								myUserName, attributes));
						isStored = true;
					}
				}
			}
			catch(AmazonServiceException e)
			{
				myError = e.getMessage();
			}
		}

		private ArrayList<ReplaceableAttribute> getReplaceableAttribute()
		{
			ArrayList<ReplaceableAttribute> list = new ArrayList<ReplaceableAttribute>();
			list.add(new ReplaceableAttribute(DataSources.AGID, myGCMID, false));
			return list;
		}
		
		@Override
	    protected void onPostExecute(Void params)
	    {
			closeProgressDialog();
			if (!isStored){
				Utility.throwErrorMessage(myContext, IDNOTSTORED, myError);
			}
	    }
}

