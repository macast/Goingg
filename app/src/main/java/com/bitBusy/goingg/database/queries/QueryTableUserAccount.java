/**
 * 
 */
package com.bitBusy.goingg.database.queries;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.HashSet;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;

import com.bitBusy.goingg.database.tables.TableUserAccount;
import com.bitBusy.goingg.events.PrivateEvents.City;
import com.bitBusy.goingg.events.PrivateEvents.PrivateEventUser;
import com.bitBusy.goingg.events.PrivateEvents.PrivateEventUserName;
import com.bitBusy.goingg.messaging.setup.gcmSetup.GCMID;
import com.bitBusy.goingg.utility.CurrentLoginState;
import com.bitBusy.goingg.utility.Utility;

/**
 * @author SumaHarsha
 *
 */
public class QueryTableUserAccount extends DBQueries{
	
	private static final String TABLENAME = TableUserAccount.class.getSimpleName();
	private static final String USERIDCOL = TableUserAccount.USERIDCOL.getName();	
	private static final String GCMIDSCOL = TableUserAccount.GCMIDSCOL.getName();
	private static final String FNAMECOL = TableUserAccount.FNAMECOL.getName();
	private static final String LNAMECOL = TableUserAccount.LNAMECOL.getName();
	private static final String CITYCOL = TableUserAccount.CITYCOL.getName();
	private static final String LOCALFRIENDSCOL = TableUserAccount.LOCALFRIENDSCOL.getName();
	private static final String[] ALLCOLS = new String[]{USERIDCOL,FNAMECOL, LNAMECOL, CITYCOL, GCMIDSCOL, LOCALFRIENDSCOL};
	private Cursor myCursor;
	private Context myContext;
	
	public QueryTableUserAccount(Context the_context)
	{
		super(the_context);
		myContext = the_context;
	}
	
	public boolean saveUser(PrivateEventUser the_user){
		boolean returnValue = false;
		if (the_user != null && the_user.getUserName() != null)
		{
			boolean iOpened = checkAndOpen();
			ContentValues contentInsert = new ContentValues();
			contentInsert.put(FNAMECOL, (the_user.getName() != null?the_user.getName().getFirstName():null));
			contentInsert.put(LNAMECOL, (the_user.getName() != null?the_user.getName().getLastName():null));
			contentInsert.put(USERIDCOL, the_user.getUserName());
			contentInsert.put(CITYCOL, (the_user.getCity() != null? the_user.getCity().getName():null));
			contentInsert.put(GCMIDSCOL, setToBytes(the_user.getGCMIDs()));
			contentInsert.put(LOCALFRIENDSCOL, setToBytes(the_user.getFriendsIDs()));

			try
			{
			myDatabase.insertOrThrow(TABLENAME, null, contentInsert);
			returnValue = true;
			}
			catch(SQLiteConstraintException e)
			{
				try
				{
					HashSet<GCMID> current = getCurrentGCMIDs(the_user.getUserName());
					HashSet<GCMID> newSet = the_user.getGCMIDs();
					if (current != null && newSet != null && !current.equals(newSet)){
					HashSet<GCMID> updatedGCMIDs = updateGCMIDSet(current, newSet);
					contentInsert.put(GCMIDSCOL, setToBytes(updatedGCMIDs));
					}
					HashSet<String> currentFrnds = getCurrentFriends(the_user.getUserName());
					HashSet<String> newFrnds = the_user.getFriendsIDs();
					if (currentFrnds != null && newSet != null && !currentFrnds.equals(newFrnds)){
						HashSet<String> updatedFrnds = updateFriendsSet(currentFrnds, newFrnds);
						contentInsert.put(LOCALFRIENDSCOL, setToBytes(updatedFrnds));
					}
					myDatabase.update(TABLENAME, contentInsert, USERIDCOL + " = ?", new String[]{the_user.getUserName()});
					returnValue = true;
				}
				catch(Exception ex)
				{
					Utility.throwErrorMessage(myContext, "GCM ID save error", ex.getMessage());
				}
			}
			catch(Exception sqlex)
			{
				Utility.throwErrorMessage(myContext, "GCM ID save error", sqlex.getMessage());
			}		
			finally
			{
				checkAndClose(iOpened);
			}
		}
		return returnValue;
	}
	
	private HashSet<String> updateFriendsSet(HashSet<String> the_oldFriends,
			HashSet<String> the_newFriends) {
		if (the_oldFriends != null){
			if (the_newFriends != null){
				if (!the_oldFriends.equals(the_newFriends)){
					the_oldFriends.addAll(the_newFriends);
				}
			}
			return the_oldFriends;
		}
		return the_newFriends;
	}

	private HashSet<String> getCurrentFriends(String the_userName) {
		PrivateEventUser user = getUser(the_userName);
		if (user != null){
			return user.getFriendsIDs();
		}
		return null;
	}

	private HashSet<GCMID> updateGCMIDSet(HashSet<GCMID>  the_currentGCMIDs,
			HashSet<GCMID> the_newGCMIDs) {
		if (the_currentGCMIDs != null){
			if (the_newGCMIDs != null){
				if (!the_currentGCMIDs.equals(the_newGCMIDs)){
					the_currentGCMIDs.addAll(the_newGCMIDs);
				}
			}
			return the_currentGCMIDs;
		}
		return the_newGCMIDs;
	}

	private HashSet<GCMID> getCurrentGCMIDs(String the_userid) {
		PrivateEventUser user = getUser(the_userid);
		if (user != null){
			return user.getGCMIDs();
		}
		return null;
	}

	private byte[] setToBytes(HashSet<?> the_ids) {
		if (the_ids != null){
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutput out = null;
			try {
			  out = new ObjectOutputStream(bos);   
			  out.writeObject(the_ids);
			  return bos.toByteArray();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
			  try {
			    if (out != null) {
			      out.close();
			    }
			  } catch (IOException ex) {
			    // ignore close exception
			  }
			  try {
			    bos.close();
			  } catch (IOException ex) {
			    // ignore close exception
			  }
			}			
		}
		return null;
	}

	public PrivateEventUser getUser(String the_userid){
		boolean iOpened = checkAndOpen();
		String fname=null, lname = null, city = null;
		byte[] gcmBlob = null;
		byte[] frndIDBlob = null;
		myCursor = myDatabase.query
		(TABLENAME, ALLCOLS, USERIDCOL+"=?",new String[]{the_userid}, null, null, null);
		if (myCursor != null && myCursor.moveToFirst() && myCursor.getCount() > 0)
		{
			while (!myCursor.isAfterLast())
			{
				fname = myCursor.getString(myCursor.getColumnIndex(FNAMECOL));
				lname = myCursor.getString(myCursor.getColumnIndex(LNAMECOL));
				city = myCursor.getString(myCursor.getColumnIndex(CITYCOL));
				gcmBlob = myCursor.getBlob(myCursor.getColumnIndex(GCMIDSCOL));
				frndIDBlob = myCursor.getBlob(myCursor.getColumnIndex(LOCALFRIENDSCOL));
				myCursor.moveToNext();
			}
			myCursor.close();
			return new PrivateEventUser(the_userid, new PrivateEventUserName(fname, lname), new City(city), (HashSet<GCMID>) byteArrToSet(true, gcmBlob), 
					(HashSet<String>) byteArrToSet(false, frndIDBlob));
		}
		checkAndClose(iOpened);
		return null;
	}
	
	private HashSet<?> byteArrToSet(boolean isGCMBlob, byte[] the_blob) {
		if (the_blob != null){
			ByteArrayInputStream bis = new ByteArrayInputStream(the_blob);
			ObjectInput in = null;
			try {
			  in = new ObjectInputStream(bis);
			  return (isGCMBlob?(HashSet<GCMID>) in.readObject():(HashSet<String>) in.readObject()); 
			} catch (StreamCorruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
			  try {
			    bis.close();
			  } catch (IOException ex) {
			    // ignore close exception
			  }
			  try {
			    if (in != null) {
			      in.close();
			    }
			  } catch (IOException ex) {
			    // ignore close exception
			  }
			}
		}
		return null;
	}

	public HashSet<GCMID> getAllGCMIDs(String the_user) {
		if (the_user != null){
			return getCurrentGCMIDs(the_user);
		}
		return null;
	}
	
	public HashSet<String> getAllRelevantFriendsIDs(String the_user){
		String currentUser = CurrentLoginState.getCurrentUser(myContext);
		if (the_user != null && currentUser != null && the_user.equals(currentUser)){
			return getCurrentFriends(the_user);
		}
		return null;
	}

	public HashSet<PrivateEventUser> getAllFriends(String the_user) {
		String currentUser = CurrentLoginState.getCurrentUser(myContext);
		if (the_user != null && currentUser != null && the_user.equals(currentUser)){
			HashSet<String> ids = getCurrentFriends(the_user);
			if (ids != null){
				HashSet<PrivateEventUser> users = new HashSet<PrivateEventUser>();
				for (String id: ids){
					users.add(getUser(id));
				}
				return users;
			}
		}
		return null;	
	}

	/**
	 * creates a link between two users (friendship)
	 * @param the_firstuser
	 * @param the_seconduser
	 * @return
	 */
	public boolean addFriends(String the_firstuser, String the_seconduser) {
		return false;
	}
}
