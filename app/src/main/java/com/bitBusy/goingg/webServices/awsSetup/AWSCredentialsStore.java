/**
 * 
 */
package com.bitBusy.goingg.webServices.awsSetup;

import android.content.Context;
import android.os.AsyncTask;

import com.amazonaws.auth.AnonymousAWSCredentials;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.services.securitytoken.AWSSecurityTokenServiceClient;
import com.amazonaws.services.securitytoken.model.AssumeRoleWithWebIdentityRequest;
import com.amazonaws.services.securitytoken.model.AssumeRoleWithWebIdentityResult;
import com.amazonaws.services.securitytoken.model.Credentials;
import com.amazonaws.services.simpledb.AmazonSimpleDBClient;
import com.bitBusy.goingg.events.DataSources;
import com.bitBusy.goingg.R;

/**
 * @author SumaHarsha
 *
 */
public class AWSCredentialsStore
{
	private static final String GOOGLESESSIONNAME = "GGL";
	private static AWSCredentialsStore myInstance;
	private static Credentials myCredentials;
	private static AmazonSimpleDBClient mySDBClient;
	
	private AWSCredentialsStore()
	{
		
	}
	
	public static AWSCredentialsStore getInstance()
	{
		if (myInstance == null)
		{
			myInstance = new AWSCredentialsStore();
		}
		return myInstance;
	}

	public void initializeCredentials(Context the_context, String the_accesstoken)
	{
		new IntitalizeCredentials(the_context, the_accesstoken).execute();
	}
	
	/*private Credentials getCredentials()
	{
		return myCredentials;
	}*/
	
	public AmazonSimpleDBClient getSDBClient()
	{
		return mySDBClient;
	}
	
	
	private class IntitalizeCredentials extends AsyncTask<Void, Void, Void>
	{
		private Context myContext;
		private String myAccessToken;
		
		private IntitalizeCredentials(Context the_context, String the_accesstoken)
		{
			myContext = the_context;
			myAccessToken = the_accesstoken;
		}

		
		/* (non-Javadoc)
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected Void doInBackground(Void... arg0) {
			if (myContext != null && myAccessToken != null)
			{
				try
				{
					AssumeRoleWithWebIdentityRequest request = new  AssumeRoleWithWebIdentityRequest();
					setRequestParameters(request);
					AWSSecurityTokenServiceClient client =	new AWSSecurityTokenServiceClient(new AnonymousAWSCredentials());
				    AssumeRoleWithWebIdentityResult assumeRoleWithWebIdentityResponse =
				    		client.assumeRoleWithWebIdentity(request);
				    myCredentials = assumeRoleWithWebIdentityResponse.getCredentials();	
				    initializeSDBClient();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}			return null;
		}


	
		/**
		 * @param request
		 * @param myAuthenticator2
		 */
		private void setRequestParameters(AssumeRoleWithWebIdentityRequest the_request)
		{
			if (the_request != null)
			{
				the_request.setWebIdentityToken(myAccessToken);
				the_request.setRoleArn(myContext.getResources().getString(R.string.ggle_role_arn));
				the_request.setRoleSessionName(GOOGLESESSIONNAME);
			}
		}


		private void initializeSDBClient()
		{
			if (myCredentials != null && myCredentials.getAccessKeyId() != null && myCredentials.getSecretAccessKey() != null && myCredentials.getSessionToken() != null)
			{
				BasicSessionCredentials credentials = 
						new BasicSessionCredentials(myCredentials.getAccessKeyId(),myCredentials.getSecretAccessKey(), myCredentials.getSessionToken());
				mySDBClient = new AmazonSimpleDBClient( credentials);	
				mySDBClient.setEndpoint(DataSources.AENDPNT);			
			}
		}
	}
	
	
	
}
