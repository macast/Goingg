/**
 * 
 */
package com.bitBusy.goingg.webServices.SignIn;

import android.content.Context;

import com.amazonaws.services.securitytoken.model.AssumeRoleRequest;
import com.bitBusy.goingg.webServices.awsSetup.AWSBaseOperation;

/**
 * @author SumaHarsha
 *
 */
public class AWSSTSSignIn extends AWSBaseOperation
{

	private static final String GETTINGPERM = "Requesting server to notice your awesomeness..";
	private static final int ONEHOURINSECS = 3600;
	
	public AWSSTSSignIn(Context the_context, Object[] the_params)
	{
		super(the_context, the_params);
		setProgressDialogMsg(GETTINGPERM);		
	}

	/* (non-Javadoc)
	 * @see android.os.AsyncTask#doInBackground(Params[])
	 */
	@Override
	protected Void doInBackground(Void... arg0) {
		AssumeRoleRequest request = new AssumeRoleRequest();
		request.setDurationSeconds(ONEHOURINSECS);
		return null;
	}
	
}	
