/**
 * 
 */
package com.bitBusy.goingg.dialog;

import java.util.HashSet;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bitBusy.goingg.activity.ActivityContacts;
import com.bitBusy.goingg.events.DataSources;
import com.bitBusy.goingg.events.PrivateEvents.City;
import com.bitBusy.goingg.events.PrivateEvents.PrivateEventUser;
import com.bitBusy.goingg.events.PrivateEvents.PrivateEventUserName;
import com.bitBusy.goingg.messaging.FriendRequestResponse.FriendRequest;
import com.bitBusy.goingg.webServices.UserData.AddFriend;
import com.bitBusy.goingg.webServices.UserData.SearchForUser;
import com.bitBusy.goingg.webServices.awsSetup.AWSOperationListenerInterface;
import com.bitBusy.goingg.webServices.awsSetup.ListenerAck;
import com.bitBusy.goingg.R;

/**
 * @author SumaHarsha
 *
 */
public class AddContactDialog implements AWSOperationListenerInterface{

	public static final String[] SUFFIXARR = new String[]{"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", 
		"S", "T", "U", "V", "W", "X", "Y", "Z", DataSources.ASPLCHAR};
	private static final String IDMUSTHAVVEVAL = "contact's Going id must have a value";
	private static final String INVALIDCITY = "your friend must reside somewhere!";
	private static final String INVALIDNAME = "either first or last name must have a value";
	private static final CharSequence SEARCHRESULTS = "Search results";
	private static final String CONFIRMSENDREQUEST = "Would you like to send a friend request to ";
	private static final String SENDREQUEST = "Send request";
	private static final CharSequence NORESULTSFOUND = null;
	private int myCurrentSuffixIndex = 0;
	private Context myContext;
	private Dialog myDialog;
	private ActivityContacts myFragment;
	private String myFriendsUserName;
	private String myMsg;
	private boolean mySearchETsVisible = false;
	
	public AddContactDialog(Context the_context, ActivityContacts the_fragment)
	{
		myContext = the_context;
		myFragment = the_fragment;
	}
	
	public void openDialog()
	{
		if (myContext != null)
		{
			myDialog = new Dialog(myContext);
			myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		    LayoutInflater inflater = (LayoutInflater) myContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			myDialog.setContentView(inflater.inflate(R.layout.add_friend_dialog, null));
			setButtonListeners();
			myDialog.show();
		}
	}
	
	private void setButtonListeners()
	{
		setAddButtonListener();
		setSearchButtonListener();
		setCancelButtonListener();
	}
	
	private void setSearchButtonListener() 
	{
		if (myDialog != null)
		{
			Button search = (Button) myDialog.findViewById(R.id.add_friend_search);
			search.setOnClickListener(new OnClickListener()
			{

				@Override
				public void onClick(View v) {
					if (!mySearchETsVisible)
					{
						updateSearchETsVisible(View.VISIBLE);
					}
					else
					{
						PrivateEventUser contact = getContact();
						if (contact != null)
						{
							SearchForUser search = new SearchForUser(myContext, new Object[]{contact, myCurrentSuffixIndex});
							search.registerListener(AddContactDialog.this);
							search.execute();
						}
					}
				}
				
			});
		}
	}

	private PrivateEventUser getContact() 
	{
		if (myDialog != null)
		{
			EditText fname = (EditText) myDialog.findViewById(R.id.add_friend_fname);
			EditText lname = (EditText) myDialog.findViewById(R.id.add_friend_lname);
			EditText city = (EditText) myDialog.findViewById(R.id.add_friend_city);
			if (verifyETValidity(city, INVALIDCITY))
			{
				if (!verifyETValidity(fname, INVALIDNAME))
				{
					if (verifyETValidity(lname, INVALIDNAME))
					{
						clearHints(fname, lname);
					}
					else
					{
						return null;
					}
				}
				return new PrivateEventUser(null, 
						new PrivateEventUserName(fname.getText().toString(), lname.getText().toString()), new City(city.getText().toString()), 
						null, null);
			}
		}
		return null;
	}

	/**
	 * @param fname
	 * @param lname
	 */
	private void clearHints(EditText the_fname, EditText the_lname) 
	{
		the_fname.setHint("");
		the_lname.setHint("");
	}

	private boolean verifyETValidity(EditText the_ET, String the_hint)
	{
		if (the_hint != null && the_ET != null)
		{
			String value = the_ET.getText().toString();
			Log.i(this.getClass().getSimpleName(), "et val:" + value);
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
	private void setAddButtonListener()
	{
		if (myDialog != null)
		{
			Button add = (Button) myDialog.findViewById(R.id.add_friend_add);
			add.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					if (mySearchETsVisible)
					{
						updateSearchETsVisible(View.GONE);
					}
					else
					{
						 initializeFriendsNameAndMsg();
						if (myFriendsUserName != null && myFriendsUserName.length() > 0)
						{
							/*AddFriend add = new AddFriend(myContext, new Object[]{myFriendsUserName});
							add.registerListener(AddContactDialog.this);
							add.execute();*/
							new FriendRequest(myContext, myFriendsUserName, myMsg).send();
							myDialog.cancel();
						}	
					}
				}

				private void initializeFriendsNameAndMsg() 
				{
					if (myDialog != null)
					{
						EditText nameVal = (EditText) myDialog.findViewById(R.id.add_friend_edittext);
						EditText msgVal = (EditText) myDialog.findViewById(R.id.add_friend_msg);
						myFriendsUserName = nameVal.getText().toString();
						myMsg = msgVal.getText().toString();
						if (myFriendsUserName == null || myFriendsUserName.length() == 0)
						{
							nameVal.setHint(IDMUSTHAVVEVAL);
							nameVal.setHintTextColor(Color.RED);
						}
					}
				}
				
			});
		}
	}
	
	/**
	 * @param gone
	 */
	protected void updateSearchETsVisible(int the_visibility)
	{
		if (myDialog != null)
		{
			EditText fname = (EditText) myDialog.findViewById(R.id.add_friend_fname);
			fname.setVisibility(the_visibility);
			EditText lname = (EditText) myDialog.findViewById(R.id.add_friend_lname);
			lname.setVisibility(the_visibility);
			EditText city = (EditText) myDialog.findViewById(R.id.add_friend_city);
			city.setVisibility(the_visibility);
			mySearchETsVisible = (the_visibility == View.VISIBLE)?true:false;

			EditText id = (EditText) myDialog.findViewById(R.id.add_friend_edittext);
			id.setVisibility(mySearchETsVisible?View.GONE:View.VISIBLE);
			EditText msg = (EditText) myDialog.findViewById(R.id.add_friend_msg);
			msg.setVisibility(mySearchETsVisible?View.GONE:View.VISIBLE);
		
			TextView title = (TextView) myDialog.findViewById(R.id.add_friend_titletext);
			title.setText(mySearchETsVisible?myContext.getResources().getString(R.string.search):myContext.getResources().getString(R.string.newcontact));
		}
	}

	private void setCancelButtonListener()
	{
		if (myDialog != null)
		{
			Button cancel = (Button) myDialog.findViewById(R.id.add_friend_cancel);
			cancel.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					myDialog.cancel();
				}
				
			});
		}
	}

	private void wasFriendAdded(Object[] the_params) 
	{
		if (the_params != null && the_params.length > 0)
		{
			try{
				boolean wasAdded = (Boolean) the_params[0];
				if (wasAdded){
					refreshObserverList();
				}
			}catch(Exception e){
			}
		//	GoingContacts.getInstance().addToContacts(new Contact(myFriendsUserName, Contact.DETAILS.Going, null, null, null, null));
		}
	}

	private void refreshObserverList() 
	{
		if (myFragment != null)
		{
			myFragment.refreshList();
		}
	}

	private void onSearch(Object[] the_params)
	{
		if (the_params != null && the_params.length == 2){
			String[] usernames = null;
		try{
			HashSet<PrivateEventUser> contacts = (HashSet<PrivateEventUser>) the_params[0];
			usernames = getUsernames(contacts);
		}catch(Exception e){
		}
		throwOpenFoundUsersDialog(usernames);	
		}
	}

	/**
	 * @param the_usernames 
	 * 
	 */
	private void throwOpenFoundUsersDialog(final String[] the_usernames) 
	{
		if (the_usernames != null)
		{	
			 AlertDialog.Builder builder = new AlertDialog.Builder(myContext);
			    builder.setTitle(SEARCHRESULTS)
			           .setItems(the_usernames, new DialogInterface.OnClickListener() {
			               public void onClick(DialogInterface dialog, int which) {
			            	   confirmSendRequestDialog(the_usernames[which]);
			            	   myDialog.cancel();
			               }
			    });
			    builder.create();
			    builder.show();
		}
		else{
			 showNoSearchResultsDialog();
		}
	}
	
	private void showNoSearchResultsDialog(){
		 AlertDialog.Builder builder = new AlertDialog.Builder(myContext);
		    builder.setTitle(SEARCHRESULTS)
		    .setMessage(NORESULTSFOUND)
		    .setPositiveButton("OK",  new DialogInterface.OnClickListener() {
			               public void onClick(DialogInterface dialog, int which) {
			            	   myDialog.cancel();
			               }
			    });
	}

	/**
	 * @param string
	 */
	protected void confirmSendRequestDialog(String the_username)
	{
		if (the_username != null)
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(myContext);
		    builder.setTitle(SENDREQUEST)
		    .setMessage(CONFIRMSENDREQUEST.concat(the_username).concat("?"))
	         .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
	             public void onClick(DialogInterface dialog, int id) {
	                 // FIRE ZE MISSILES!
	            	 dialog.cancel();
	             }
	         })
	         .setNegativeButton("No", new DialogInterface.OnClickListener() {
	             public void onClick(DialogInterface dialog, int id) {
	                 // User cancelled the dialog
	            	 dialog.cancel();             }
	         });
		    builder.create();
		    builder.show();
		}
	}

	/**
	 * @param the_contacts
	 * @return
	 */
	private String[] getUsernames(HashSet<PrivateEventUser> the_contacts) 
	{
		if (the_contacts != null)
		{
			String[] usernames = new String[the_contacts.size()];
			int i = 0;
			for (PrivateEventUser contact: the_contacts)
			{
				if (contact != null && contact.getUserName() != null)
				{
					usernames[i] = contact.getUserName();
					i++;
				}
			}
			return usernames;
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see com.bitBusy.going.webServices.awsSetup.AWSOperationListenerInterface#onOperationComplete(java.lang.Object[])
	 */
	@Override
	public void onOperationComplete(ListenerAck the_ack) {
		if (the_ack != null && the_ack.getClassName() != null && the_ack.getParams() != null){	
			String the_className = the_ack.getClassName();
			if (the_className.equals(SearchForUser.class.getName())){
				onSearch(the_ack.getParams());
			}
			else if (the_className.equals(AddFriend.class.getName())){
				wasFriendAdded(the_ack.getParams());
			}
		}
	}
	
	
}
