/**
 * 
 */
package com.bitBusy.goingg.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.IntentSender.SendIntentException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;

import com.bitBusy.goingg.WIDAuthentication.IDTokenVerificationObserver;
import com.bitBusy.goingg.WIDAuthentication.IDTokenVerifier;
import com.bitBusy.goingg.WIDAuthentication.WIDAccessToken;
import com.bitBusy.goingg.activity.ActivityMapViewHome;
import com.bitBusy.goingg.utility.Utility;
import com.bitBusy.goingg.R;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;


/**
 * @author SumaHarsha
 *
 */
public class WIDAuthenticationDialog implements com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks, 
com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener, IDTokenVerificationObserver
{
	public static final String GCLIENTID = "58548578962-r5vuj4k4djachbcifi8f7pkd2pvkfklc.apps.googleusercontent.com";
	public static final int GPLUS_SIGNIN = 1234567890;
	private static final String GGLTKENRETRIEVEERROR = "Token error";
	private static final String IDPROOF = "One-time authentication";
	private static final String GACTIVITYTYPE = "AddAction";
	private static final String GGLCONNERROR = "Error contacting Google";
	private static final String GGLCONNSUSPENDED = "Error:Connection suspended";
	private static final CharSequence ONETIMEVERIFICATION = "Authentication explanation";
	private static final String AUTHENTICATING = "Authenticating..";
	private WIDAuthtentication myWIDAuthenticator;
	private Context myContext;
	private Dialog myDialog;
	private ProgressDialog myProgressDialog;
		
	public WIDAuthenticationDialog()
	{
		super();
	}
	
	public WIDAuthenticationDialog setContext(Context the_context)
	{
		myContext = the_context;
		return this;
	}

	
	public void openDialog()
	{
		if (myContext != null)
		{
			myWIDAuthenticator = new WIDAuthtentication();
			myDialog = new Dialog(myContext);
			myDialog.setTitle(IDPROOF);
		    LayoutInflater inflater = (LayoutInflater) myContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		    myDialog.setContentView(inflater.inflate(R.layout.wid_login_dialog, null));
			setWIDDialogButtonListeners();
			myDialog.setCanceledOnTouchOutside(false);
			myDialog.setCancelable(false);
			myDialog.show();
		}
	}
	
	private void setWIDDialogButtonListeners()
	{
		setGoogleBtnListener();
		setInfoBtnListener();
	}

	private void setInfoBtnListener() {
		if (myDialog != null)
		{
			final ImageButton infoBtn = (ImageButton) myDialog.findViewById(R.id.wid_login_info);
			infoBtn.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					throwOpenWIDInfoDialog();
				}	
			});
		}
	
	}

	private void throwOpenWIDInfoDialog() {
		Dialog dialog = new Dialog(myContext);
		dialog.setTitle(ONETIMEVERIFICATION);
		LayoutInflater inflater = (LayoutInflater) myContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		dialog.setContentView(inflater.inflate(R.layout.wid_explanation_dialog, null));
		setOKbtbListenerWIDExplDialog(dialog);
		dialog.show();
	}
	
	private void setOKbtbListenerWIDExplDialog(final Dialog the_dialog) {
		if (the_dialog != null){
			Button ok = (Button) the_dialog.findViewById(R.id.wid_expl_dialog_ok);
			ok.setOnClickListener(new OnClickListener(){
				
				@Override
				public void onClick(View v) {
					the_dialog.cancel();
				}});
		}
	}

	private void setGoogleBtnListener() 
	{
		if (myDialog != null)
		{
			final SignInButton fbBtn = (SignInButton) myDialog.findViewById(R.id.wid_login_imgBtn_Gplus);
			fbBtn.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					myProgressDialog = new ProgressDialog(myContext);
					myProgressDialog.setMessage(AUTHENTICATING);
					myProgressDialog.setCancelable(false);
				myWIDAuthenticator.googleLogin();
			}
			});
		}				
	}

	private class WIDAuthtentication 
	{
		private GoogleApiClient myGoogleClient;

		private WIDAuthtentication()
		{
			fireUpManagers();
		}

		private void fireUpManagers() 
		{
			fireUpGoogleManager();
		}
		
		private void fireUpGoogleManager() 
		{
			myGoogleClient = new GoogleApiClient.Builder(myContext)
		        .addConnectionCallbacks(WIDAuthenticationDialog.this)
		        .addOnConnectionFailedListener(WIDAuthenticationDialog.this)
		        .addApi(Plus.API)
		        .addScope(Plus.SCOPE_PLUS_LOGIN)
		        .build();		
		}

		private void googleLogin()
		{
			if (myGoogleClient != null)
			{
				myGoogleClient.connect();
			}
		}
	}
	
	public void getGoogleAccessToken(String the_email) 
	{
		new GoogleAccessTokenRetriever().execute(the_email);
	}
		@Override
		public void onConnected(Bundle arg0) 
		{
		if (myWIDAuthenticator != null)
		{
			if (myProgressDialog != null && myProgressDialog.isShowing()){
				myProgressDialog.cancel();
			}

			if (myDialog.isShowing()){
				myDialog.cancel();
			}
			getGoogleAccessToken(Plus.AccountApi.getAccountName(myWIDAuthenticator.myGoogleClient));
		}
	}

	@Override
	public void onConnectionSuspended(int arg0) 
	{
		Utility.throwErrorMessage(myContext, GGLCONNERROR, GGLCONNSUSPENDED);
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) 
	{
		if (result.hasResolution()) 
		{
		    try
		    {
		      ((Activity) myContext).startIntentSenderForResult(result.getResolution().getIntentSender(),
		          GPLUS_SIGNIN, null, 0, 0, 0);
		    } catch (SendIntentException e) 
		    {
		     	myWIDAuthenticator.myGoogleClient.connect();
		    }		
		}
	}

	public void connectToGplus()
	{
		GoogleApiClient gClient = myWIDAuthenticator.myGoogleClient;
		if (gClient != null && !gClient.isConnecting())
		{
			gClient.connect();
		}
	}
	
	private class GoogleAccessTokenRetriever extends AsyncTask<String, Void, Void>
	{
		private String myToken;

		/* (non-Javadoc)
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected Void doInBackground(String... params) 
		{
			if (params != null && params.length > 0 && params[0] != null){
			String emailID = params[0];
			Bundle appActivities = new Bundle();
			appActivities.putString(GoogleAuthUtil.KEY_REQUEST_VISIBLE_ACTIVITIES,
					GACTIVITYTYPE);		 
			try {
				myToken = GoogleAuthUtil.getToken(myContext.getApplicationContext(), emailID,
						"audience:server:client_id:" + GCLIENTID);
				}
			catch (UserRecoverableAuthException e) 
			{ 
				 ((Activity) myContext).startActivityForResult(e.getIntent(), GPLUS_SIGNIN);
			}
			 catch (Exception e)
			  {
					Utility.throwErrorMessage(myContext, GGLTKENRETRIEVEERROR, e.getLocalizedMessage());
			  }		
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void params){
			IDTokenVerifier verify = new IDTokenVerifier(myContext, myToken);
			verify.registerObserver(WIDAuthenticationDialog.this);
			verify.verify();
		}
		
	}
	
	@Override
	public void onIDTokenVerified(boolean wasVerified, WIDAccessToken the_token) {
		if (!wasVerified && !myDialog.isShowing()){
			myDialog.show();
		}
		else{
			sendHome(wasVerified, the_token);
			disconnectGoogleClient();
		}
	}

	private void disconnectGoogleClient() {
		if (myWIDAuthenticator != null && myWIDAuthenticator.myGoogleClient != null && myWIDAuthenticator.myGoogleClient.isConnected()){
			myWIDAuthenticator.myGoogleClient.disconnect();
		}
	}

	private void sendHome(boolean wasVerified, WIDAccessToken the_token) {
		  ((ActivityMapViewHome) myContext).onWIDAuthenticated(wasVerified, the_token);		
	}
}
