package com.bitBusy.goingg.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bitBusy.goingg.WIDAuthentication.WIDAccessToken;
import com.bitBusy.goingg.dialog.ConfirmLogoutDialog;
import com.bitBusy.goingg.dialog.LoginDialog;
import com.bitBusy.goingg.dialog.WIDAuthenticationDialog;
import com.bitBusy.goingg.utility.ActionBarChoice;
import com.bitBusy.goingg.utility.CurrentLoginState;
import com.bitBusy.goingg.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.plus.Plus;

public class ActivityAppSettings extends Activity implements com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks, 
com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener{

	private static final String CONFIRMREVOKEACCESSMSG = "Are you sure? This will prevent you from:" +
			"\n* Finding public events submitted by Going users.\n* Creating and interacting with events.\n* Managing your Going account" ;
	private static final String CONFIRMREVOKEACCESSTITLE = "Confirm sign out & revoke access";
	private static final String ACCESSREVOKED = "Successfully signed out and revoked access";
	private static final CharSequence SIGNINGOUT = null;
	private GoogleApiClient myGoogleApiClient;
	private ProgressDialog myProgressDialog;
	private String myCurrentGoingUser;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_settings);
		setButtonsVisibility();
		setAccountsInfo();
	}
	
	private void setAccountsInfo() {
		setGoingID();
		setGoogleID();
	}
	
	private void setGoogleID() {
		if (CurrentLoginState.getUserLoggedInGoogle(this)){
			showGoogleID();
		}		
	}

	private void showGoogleID() {
		TextView googleID = (TextView) findViewById(R.id.settings_verificationidvalue);
		setValueToTV(googleID, getGoogleID());	
	}

	private void setGoingID() {
		myCurrentGoingUser = CurrentLoginState.getCurrentUser(this);
		if (myCurrentGoingUser != null && myCurrentGoingUser.length() > 0){
		TextView goingID = (TextView) findViewById(R.id.settings_goingidvalue);
		setValueToTV(goingID, myCurrentGoingUser);
		}
	}
	
	private void setValueToTV(TextView the_textView, String the_str) {
		if (the_textView != null && the_str != null){
			the_textView.setText(the_str);
		}
	}

	private String getGoogleID() {
		WIDAccessToken token = CurrentLoginState.getWIDAccessToken(this);
		if (token != null){
			return token.getEmailID();
		}
		return null;
	}

	private void setButtonsVisibility(){
		setGoingButtons();
		setVerificationIDButtons();
		}
	
	private void setVerificationIDButtons() {
		boolean loggedInGoogle = isLoggedIntoGoogle();
		setSignOutGoogleBtnVisibility(loggedInGoogle?View.VISIBLE:View.GONE);
		setSignInGoogleBtnVisibility(loggedInGoogle?View.GONE:View.VISIBLE);
	}

	private void setSignInGoogleBtnVisibility(int the_visibility) {
		Button signin = (Button) findViewById(R.id.settings_verificationid_signin);
		if (signin != null){
			signin.setVisibility(the_visibility);
		}			
	}

	private void setSignOutGoogleBtnVisibility(int the_visibility) {
		Button signout = (Button) findViewById(R.id.settings_verificationid_signout);
		if (signout != null){
			signout.setVisibility(the_visibility);
		}				
	}

	private boolean isLoggedIntoGoogle() {
		return CurrentLoginState.getUserLoggedInGoogle(this);
	}

	private void setGoingButtons(){
		boolean loggedInGoing = (myCurrentGoingUser != null && myCurrentGoingUser.length() > 0);
		setSignOutGoingBtnVisibility(loggedInGoing?View.VISIBLE:View.GONE);
		setSignInGoingBtnVisibility(loggedInGoing?View.GONE:View.VISIBLE);
	}

	
	private void setSignInGoingBtnVisibility(int the_visibility) {
		Button signin = (Button) findViewById(R.id.settings_goingid_signin);
		if (signin != null){
			signin.setVisibility(the_visibility);
		}		
	}

	private void setSignOutGoingBtnVisibility(int the_visibility) {
		Button signout = (Button) findViewById(R.id.settings_goingid_signout);
		if (signout != null){
			signout.setVisibility(the_visibility);
		}
	}
	
	public void signInGoing(View the_view){
		new LoginDialog(this).openDialog();
	}
	
	public void signOutGoing(View the_view){
		new ConfirmLogoutDialog(this).openDialog();
	}
	
	public void signInGoogle(View the_view){
		new WIDAuthenticationDialog().setContext(this).openDialog();
	}
	
	public void revokeAccessGoogle(View the_view){
		showRevokeAccessConfirmationDialog();
	}
	
	private void showRevokeAccessConfirmationDialog(){
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setTitle(CONFIRMREVOKEACCESSTITLE);
 		alertDialogBuilder.setMessage(CONFIRMREVOKEACCESSMSG).setCancelable(false).setPositiveButton
 			("Yes",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
						showGglSgnOutProgressDialog();
						connectGoogleApiClient();
					}
				  })
				.setNegativeButton("No",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
						dialog.cancel();
					}
				});
 		AlertDialog alertDialog = alertDialogBuilder.create();
 		alertDialog.show();
	}
	
	private void showGglSgnOutProgressDialog() {
		myProgressDialog = new ProgressDialog(this);
		myProgressDialog.setMessage(SIGNINGOUT);
		myProgressDialog.show();
	}

	private void connectGoogleApiClient() {
		buildGoogleApiClient();
		if (myGoogleApiClient != null){
			myGoogleApiClient.connect();
		}
	
	}

	private void revokeAccessForGoogleClient() {
		if (myGoogleApiClient != null){
			Plus.AccountApi.clearDefaultAccount(myGoogleApiClient);
			Plus.AccountApi.revokeAccessAndDisconnect(myGoogleApiClient)
			    .setResultCallback(new ResultCallback<Status>() {
			@Override
			public void onResult(Status the_status) {
				clearLocalLoginData();
			}
			});		
		}
	}
	
	private void clearLocalLoginData() {
		closeGgnSgnOutPrgrsDialog();
		Toast.makeText(this, ACCESSREVOKED, Toast.LENGTH_LONG).show();
		CurrentLoginState.setUserLoggedInGoogle(this, false, null);
		goHome();
	}
	
	private void closeGgnSgnOutPrgrsDialog() {
		if (myProgressDialog != null && myProgressDialog.isShowing()){
			myProgressDialog.cancel();
		}
	}

	private void goHome() {
		Intent intent = new Intent(this, ActivityMapViewHome.class);
		startActivity(intent);
	}

	private void buildGoogleApiClient() {
		myGoogleApiClient = new GoogleApiClient.Builder(this)
       .addConnectionCallbacks(this)
       .addOnConnectionFailedListener(this)
        .addApi(Plus.API)
        .addScope(Plus.SCOPE_PLUS_LOGIN)
        .build();	
	}

	@Override
	public boolean onCreateOptionsMenu(Menu the_menu) {
	  MenuInflater inflater = getMenuInflater();
	  inflater.inflate(R.menu.action_bar_menu, the_menu);
	  ActionBarChoice.setupActionBar(the_menu, this);
	  return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem the_item) {
	  com.bitBusy.goingg.utility.ActionBarChoice.choiceMade(this, the_item);
	  return true;
	}

	/* (non-Javadoc)
	 * @see com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener#onConnectionFailed(com.google.android.gms.common.ConnectionResult)
	 */
	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		clearLocalLoginData();
	}

	/* (non-Javadoc)
	 * @see com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks#onConnected(android.os.Bundle)
	 */
	@Override
	public void onConnected(Bundle arg0) {
		revokeAccessForGoogleClient();		
	}

	/* (non-Javadoc)
	 * @see com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks#onConnectionSuspended(int)
	 */
	@Override
	public void onConnectionSuspended(int arg0) {
		clearLocalLoginData();
		}
}
