/**
 * 
 */
package com.bitBusy.goingg.dialog;

import java.util.Random;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.bitBusy.goingg.activity.ActivityViewMyAccount;
import com.bitBusy.goingg.messaging.setup.gcmSetup.GCMCheckListener;
import com.bitBusy.goingg.messaging.setup.gcmSetup.GCMRegistrationChecker;
import com.bitBusy.goingg.utility.CurrentLoginState;
import com.bitBusy.goingg.utility.Utility;
import com.bitBusy.goingg.webServices.Register.RegisterUser;
import com.bitBusy.goingg.webServices.SignIn.AWSSignIn;
import com.bitBusy.goingg.webServices.awsSetup.AWSOperationListenerInterface;
import com.bitBusy.goingg.webServices.awsSetup.ListenerAck;
import com.bitBusy.goingg.R;

/**
 * @author SumaHarsha
 *
 */
public class LoginDialog implements AWSOperationListenerInterface, GCMCheckListener{
//SignInObserver, RegisterUserObserver{

	private static final String EMPTYUSERNAMEHINT = "user name must be at least 8 characters long";
	private static final String EMPTYPWDHINT = "password must have a value";
	private static final String INVALIDLOGINTITLE = "Login error";
	private static final String INVALIDLOGINMSG = "Username/password entered incorrectly.";
	private static final String REGPWDHINT = "must have a value & match below field";
	private static final String REGCNFPWDHINT = "must have a value & match above field";
	private static final String FNAMEINVALID = "first name must have a value";
	private static final String LNAMEINVALID = "last name must have a value";
	private static final String CITYINVALID = "city must have a value";
	private static final String REGSECKYWORDHINT = "security keyword must have a value";
	private static final String SEEKBARMISMATCHHINT = "not scrolled to required value!";
	private static final String DEVICELIMITTITLE = "Registration error";
	private static final String DEVICELIMITMSG = "You have already registered with this device! (only one registration/device allowed)";
	private static final String USERNAMEALREADYEXISTS = "user name not available. try another!";
	private static final String LOGGEDIN = "Logged in";

	private boolean isRegistering = false;

	/** context*/
	private Context myContext;
	
	private int mySeekBarPrgrs=0;
	
	private TextView mySeekBarTV;
	
	private int myToSeekValue;
	
	private TextView myTitleTextView;
	
	/** dialog*/
	private Dialog myDialog;
	
	private boolean myRegistrationETsVisible = false;
	private String myUserName;
	
	
	public LoginDialog(Context the_context)
	{
		myContext = the_context;
	}
	
	
	/** throws open a dialog*/
	public void openDialog() {
		
			myDialog = new Dialog(myContext);
			myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		    LayoutInflater inflater = (LayoutInflater) myContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		    
		    // Inflate and set the layout for the dialog
		    // Pass null as the parent view because its going in the dialog layout
			myDialog.setContentView(inflater.inflate(R.layout.login_dialog, null));
			myTitleTextView = (TextView) myDialog.findViewById(R.id.login_titleName);
			setButtonListeners();
			setScrollBar();
			myDialog.show();
		
	}
	
	/**
	 * 
	 */
	private void setScrollBar()
	{
		if (myDialog != null)
		{
			int low = 1, high = 11;
			myToSeekValue = new Random().nextInt(high - low) + low;
			mySeekBarTV = (TextView) myDialog.findViewById(R.id.login_seekedprompt);
			TextView seekTo = (TextView) myDialog.findViewById(R.id.login_seekprompt);
			seekTo.setText(seekTo.getText().toString() + " " + String.valueOf(myToSeekValue));
			SeekBar seekbar = (SeekBar) myDialog.findViewById(R.id.login_seekbar);
			seekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
				@Override
				public void onProgressChanged(SeekBar seekBar,int progresValue, boolean fromUser) 
				{
					mySeekBarPrgrs = progresValue;
				}

	      @Override
	      public void onStopTrackingTouch(SeekBar seekBar) {
	    	  mySeekBarTV.setText(mySeekBarPrgrs + "/" + seekBar.getMax());
	      }

		@Override
		public void onStartTrackingTouch(SeekBar arg0) {
			
		}
	  });
		}
	}


	/**
	 * 
	 */
	private void setButtonListeners()
	{
		setCancelButtonListener();
		setRegisterButtonListener();
		setLoginButtonListener();
	}


	/**
	 * 
	 */
	private void setLoginButtonListener()
	{
		if (myDialog != null)
		{
			Button login = (Button) myDialog.findViewById(R.id.login_dialog_signin);
			login.setOnClickListener(new OnClickListener() {						 
				@Override
				public void onClick(View arg0) {
					myTitleTextView.setText(R.string.login);
					if (myRegistrationETsVisible)
					{
						setSecondaryETsVis(View.GONE);
					}
					else
					{
						EditText nameText = (EditText) myDialog.findViewById(R.id.login_username);
					  	   EditText passText = (EditText) myDialog.findViewById(R.id.login_password);
					  	   String name = nameText.getText().toString();
					  	   String pwd =  passText.getText().toString();
					  	   boolean checkName = true, checkPwd = true;
					  	   if (name == null || name.length() == 0)
					  	   {
					  		   nameText.setHint(EMPTYUSERNAMEHINT);
					  		   nameText.setHintTextColor(Color.RED);
					  		   checkName = false;									            		   
					  	   }
					  	   if (pwd == null || pwd.length() == 0)
					  	   {
					  		   passText.setHint(EMPTYPWDHINT);
					  		   passText.setHintTextColor(Color.RED);
					  		   checkPwd = false;
					  	   }
					  	   if (checkName && checkPwd)
					  	   {
					  		   signIn(name, pwd);
					  	   }			
					}
				}
			});
		}
     			
	}


	/**
	 * 
	 */
	private void setRegisterButtonListener() 
	{
		if (myDialog != null)
		{
			
			Button register = (Button) myDialog.findViewById(R.id.login_dialog_register);
			register.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View arg0) {
					myTitleTextView.setText(R.string.register);
					if (!myRegistrationETsVisible)
					{
						setSecondaryETsVis(View.VISIBLE);
					}
					else
					{
						EditText nameTxt = (EditText) myDialog.findViewById(R.id.login_username);
						EditText pwdTxt = (EditText) myDialog.findViewById(R.id.login_password);
						EditText cnfrmPwdTxt = (EditText) myDialog.findViewById(R.id.login_confirmpwd);
						EditText secretWordTxt = (EditText) myDialog.findViewById(R.id.login_email);
						EditText firstNameTxt = (EditText) myDialog.findViewById(R.id.login_fname);
						EditText lastNameTxt = (EditText) myDialog.findViewById(R.id.login_lname);
						EditText cityTxt = (EditText) myDialog.findViewById(R.id.login_city);
						if (verifyRegistrationInput(nameTxt, pwdTxt, cnfrmPwdTxt, secretWordTxt, firstNameTxt, lastNameTxt, cityTxt))
						{
							isRegistering = true;									
							checkForGCM();
						}
					}
				} 
			});
		}
	}

	private void checkForGCM() {
		GCMRegistrationChecker checker = new GCMRegistrationChecker(myContext, myUserName);
		checker.registerListener(LoginDialog.this);
		checker.checkMyGCMRegistration();		
	}


	/**
	 * @param secretWordTxt 
	 * @param cnfrmPwdTxt 
	 * @param pwdTxt 
	 * @param the_nameTxt 
	 * @param cityTxt 
	 * @param lastNameTxt 
	 * @param the_fNameTxt 
	 * @return
	 */
	protected boolean verifyRegistrationInput(EditText the_nameTxt,
			EditText the_pwdTxt, EditText the_cnfrmPwdTxt, EditText the_secretWordTxt,
			EditText the_fNameTxt, EditText the_lNameTxt, EditText the_cityTxt)
	{
		boolean retValue = true;
		if (the_nameTxt != null && the_pwdTxt != null && the_cnfrmPwdTxt != null && the_secretWordTxt != null && 
				the_fNameTxt != null && the_lNameTxt != null && the_cityTxt != null)
		{
			String name = the_nameTxt.getText().toString();
			if (name == null || name.length() <= 8)
			{
				the_nameTxt.setText("");
				the_nameTxt.setHint(EMPTYUSERNAMEHINT);
				the_nameTxt.setHintTextColor(Color.RED);
				retValue = false;
			}
			myUserName = name;
			String pwd = the_pwdTxt.getText().toString();
			String cnfrmPwd = the_cnfrmPwdTxt.getText().toString();
			if (pwd == null || pwd.length() == 0 || cnfrmPwd == null || 
					cnfrmPwd.length() == 0 || !pwd.equals(cnfrmPwd))
			{
				the_pwdTxt.setText("");
				the_pwdTxt.setHint(REGPWDHINT);
				the_pwdTxt.setHintTextColor(Color.RED);
				the_cnfrmPwdTxt.setText("");
				the_cnfrmPwdTxt.setHint(REGCNFPWDHINT);
				the_cnfrmPwdTxt.setHintTextColor(Color.RED);
				retValue = retValue & false;
			}
			String securityKeyword = the_secretWordTxt.getText().toString();
			if (securityKeyword == null || securityKeyword.length() == 0)
			{
				the_secretWordTxt.setHint(REGSECKYWORDHINT);
				the_secretWordTxt.setHintTextColor(Color.RED);
				retValue = retValue & false;
			}
			retValue = retValue & verifyETValidity(the_fNameTxt, FNAMEINVALID) & 
					verifyETValidity(the_lNameTxt, LNAMEINVALID) & 
					verifyETValidity(the_cityTxt, CITYINVALID); 
			if (myToSeekValue != mySeekBarPrgrs)
			{
				mySeekBarTV.setText("");
				mySeekBarTV.setHint(SEEKBARMISMATCHHINT);
				mySeekBarTV.setHintTextColor(Color.RED);
				retValue = retValue & false;
			}
		}
		return retValue;
	}


	private boolean verifyETValidity(EditText the_ET, String the_hint)
	{
		if (the_hint != null && the_ET != null)
		{
			String value = the_ET.getText().toString();
			if (value == null || value.length() == 0)
			{
				the_ET.setHint(the_hint);
				the_ET.setHintTextColor(Color.RED);
				return false;
			}
			return true;
		}	
		return false;
	}


	/**
	 * @param visible
	 */
	protected void setSecondaryETsVis(int visible) {
		if (myDialog != null)
		{
			EditText confirmPwd = (EditText) myDialog.findViewById(R.id.login_confirmpwd);
			confirmPwd.setVisibility(visible);
			EditText email = (EditText) myDialog.findViewById(R.id.login_email);
			email.setVisibility(visible);
			EditText fname = (EditText) myDialog.findViewById(R.id.login_fname);
			fname.setVisibility(visible);
			EditText lname = (EditText) myDialog.findViewById(R.id.login_lname);
			lname.setVisibility(visible);
			EditText city = (EditText) myDialog.findViewById(R.id.login_city);
			city.setVisibility(visible);		
			SeekBar seekbar = (SeekBar) myDialog.findViewById(R.id.login_seekbar);
			seekbar.setVisibility(visible);
			TextView seekPrmt = (TextView) myDialog.findViewById(R.id.login_seekprompt);
			seekPrmt.setVisibility(visible);
			mySeekBarTV.setVisibility(visible);
			myRegistrationETsVisible = (View.VISIBLE == visible)?true:false;
		}
	}


	/**
	 * 
	 */
	private void setCancelButtonListener()
	{
		if (myDialog != null)
		{
			Button close = (Button) myDialog.findViewById(R.id.login_dialog_cancel);
			close.setOnClickListener(new OnClickListener() {						 
				@Override
				public void onClick(View arg0) {
					myDialog.dismiss();
				}
	 
			});
		}		
	}
	
	private void signIn(String the_name, String the_pwd)
	{
		AWSSignIn awsSignIn = new AWSSignIn(myContext, new Object[]{the_name, the_pwd});
		awsSignIn.registerListener(this);
		myUserName = the_name;
		awsSignIn.execute();
	}
	
	
	private void signInSuccess(Object[] the_params)
	{
		if (the_params != null && the_params.length > 0){
			try{
			boolean success = (Boolean) the_params[0];
			if (success)
			{
				checkForGCM();
			}
			else
			{
				Utility.throwErrorMessage(myContext, INVALIDLOGINTITLE, INVALIDLOGINMSG);
			}
			}catch(Exception e){
			}
		}
	}
	 
	private void startMyAccountActivity()
	{
		Activity currActivity = (Activity)myContext;
		Intent intent = new Intent(currActivity, ActivityViewMyAccount.class);
		currActivity.startActivity(intent);		
	}

	private void registrationStatus(Object[] the_params) 
	{
		if (the_params != null && the_params.length > 0){
			try{
				RegisterUser.RETMSGS the_msg = (RegisterUser.RETMSGS) the_params[0];
				if (the_msg == com.bitBusy.goingg.webServices.Register.RegisterUser.RETMSGS.Success)
				{
					Toast.makeText(myContext, "successfully registered", Toast.LENGTH_LONG).show();
					startMyAccountActivity();
				}
				else if (the_msg == RegisterUser.RETMSGS.UserLimitOnDeviceHit)
				{
					Utility.throwErrorMessage(myContext, DEVICELIMITTITLE, DEVICELIMITMSG);
				}
				else if (the_msg == RegisterUser.RETMSGS.UserNameAlreadyExists)
				{
					EditText nameTxt = (EditText) myDialog.findViewById(R.id.login_username);
					nameTxt.setText("");
					nameTxt.setHint(USERNAMEALREADYEXISTS);
					nameTxt.setHintTextColor(Color.RED);
				}
				else
				{
					Utility.throwErrorMessage(myContext, "Registration error", "No, no, no, nooo..!");
				}
			}catch(Exception e){
			}
		}		
	}


	/* (non-Javadoc)
	 * @see com.bitBusy.going.webServices.awsSetup.AWSOperationListenerInterface#onOperationComplete(com.bitBusy.going.webServices.awsSetup.ListenerAck)
	 */
	@Override
	public void onOperationComplete(ListenerAck the_ack) {
		if (the_ack != null && the_ack.getClassName() != null){
			String className = the_ack.getClassName();
			if (className.equals(AWSSignIn.class.getName())){
				signInSuccess(the_ack.getParams());
			}
			else if (className.equals(RegisterUser.class.getName())){
				registrationStatus(the_ack.getParams());
			}
		}
	}


	/* (non-Javadoc)
	 * @see com.bitBusy.going.messaging.setup.gcmSetup.GCMCheckListener#onChecked()
	 */
	@Override
	public void onChecked() {
		if (isRegistering){
			continueRegistration();
		}
		else{
			Toast.makeText(myContext.getApplicationContext(), LOGGEDIN, Toast.LENGTH_LONG).show();
			CurrentLoginState.setUserLoggedInGoing(myContext, true, myUserName);
		//	Utility.initiateFriendsList(myContext);
		//	Utility.initiateGCMIDsList(myContext);
			startMyAccountActivity();
		}
	}


	/**
	 * 
	 */
	private void continueRegistration() {
		EditText nameTxt = (EditText) myDialog.findViewById(R.id.login_username);
		EditText pwdTxt = (EditText) myDialog.findViewById(R.id.login_password);
		EditText secretWordTxt = (EditText) myDialog.findViewById(R.id.login_email);
		EditText firstNameTxt = (EditText) myDialog.findViewById(R.id.login_fname);
		EditText lastNameTxt = (EditText) myDialog.findViewById(R.id.login_lname);
		EditText cityTxt = (EditText) myDialog.findViewById(R.id.login_city);
		RegisterUser register = new RegisterUser(myContext, new Object[]
				{nameTxt.getText().toString(), pwdTxt.getText().toString(), secretWordTxt.getText().toString()
				, firstNameTxt.getText().toString(), lastNameTxt.getText().toString(), cityTxt.getText().toString()});
		register.registerListener(LoginDialog.this);
		register.execute();		
			
	}
}
