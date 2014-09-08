/**
 * 
 */
package com.bitBusy.goingg.database.queries;

import java.util.HashSet;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import com.bitBusy.goingg.database.setup.DBInteractor;
import com.bitBusy.goingg.database.tables.TableMessages;
import com.bitBusy.goingg.events.PrivateEvents.PrivateEventUser;
import com.bitBusy.goingg.messaging.setup.gcmSetup.GCMMessage;
import com.bitBusy.goingg.utility.Utility;

/**
 * @author SumaHarsha
 *
 */
public class QueryTableMessages extends DBQueries{
	
	private static final String TABLENAME = TableMessages.class.getSimpleName();	
	private static final String MSGIDCOL = TableMessages.MSGIDCOL.getName();
	private static final String MSGTYPECOL = TableMessages.MSGTYPE.getName();
	private static final String MSGTEXTCOL = TableMessages.MSGTEXT.getName();
	private static final String SENDERIDCOL = TableMessages.SENDERID.getName();
	private static final String RECEIVERIDCOL = TableMessages.RECEIVERID.getName();
	private static final String SENDDATETIMECOL = TableMessages.SENDDATETIME.getName();
	private static final String RXDATETIMECOL = TableMessages.RXDATETIME.getName();
	private static final String RXED = TableMessages.RXEDMESSAGE.getName();
	private static final String[] ALLCOLS = new String[]{MSGIDCOL, MSGTYPECOL, MSGTEXTCOL, SENDERIDCOL, RECEIVERIDCOL, 
		SENDDATETIMECOL, RXDATETIMECOL, RXED};	
	private Context myContext;	
	private Cursor myCursor;

	public QueryTableMessages(Context the_context)
	{
		super(the_context);
		myContext = the_context;
	}

	public boolean saveMessage(GCMMessage the_msg, boolean received)
	{
		boolean returnValue = false;
		if (the_msg != null)
		{
			DBInteractor dbInteractor = new DBInteractor(myContext);
			boolean iOpened = checkAndOpen();
			ContentValues contentInsert = new ContentValues();
			contentInsert.put(MSGIDCOL, the_msg.getMsgID());
			contentInsert.put(MSGTYPECOL, the_msg.getMessageType()!=null?the_msg.getMessageType().name():null);
			contentInsert.put(MSGTEXTCOL, (the_msg.getContent()!=null?the_msg.getContent().toString():null));
			contentInsert.put(SENDERIDCOL, the_msg.getSender()!=null? the_msg.getSender().getUserName():null);
			contentInsert.put(RECEIVERIDCOL, the_msg.getReceiver()!=null? the_msg.getReceiver().getUserName():null);
			contentInsert.put(SENDDATETIMECOL, the_msg.getSendDateTime());
			contentInsert.put(RXDATETIMECOL, the_msg.getRxDateTime());
			contentInsert.put(RXED, received?1:0);
			dbInteractor.saveUser(the_msg.getSender());
			try
			{
			myDatabase.insertOrThrow(TABLENAME, null, contentInsert);
			
			returnValue = true;
			}catch(Exception sqlex)
			{
				//Utility.throwErrorMessage(myContext, "Message save error", sqlex.getMessage());
				sqlex.printStackTrace();
				returnValue = false;
			}		
			finally
			{
				checkAndClose(iOpened);
			}
			return returnValue;
	

		}
		return false;
	}
	
	public boolean deleteMsg(String the_msgID)
	{
		boolean retValue = false;
		if (the_msgID != null)
		{
			boolean iOpened = checkAndOpen();
			try
			{
						if (myDatabase.delete(TABLENAME, MSGIDCOL + " = ?", new String[]{the_msgID})== 1)
						{
							retValue = true;
						}	
			}
			catch(Exception e)
			{
				//Utility.throwErrorMessage(myContext, "Message delete error", e.getMessage());
				e.printStackTrace();
				retValue = false;
			}
			finally
			{
				checkAndClose(iOpened);
			}
		}
		return retValue;

	}

	public HashSet<GCMMessage> getAllInboxMessages()
	{
		boolean iOpened = checkAndOpen();
		HashSet<GCMMessage> msgs = new HashSet<GCMMessage>();
		myCursor = myDatabase.query
		(TABLENAME, ALLCOLS, RXED+"=?",new String[]{"1"}, null, null, null);
		if (myCursor != null && myCursor.moveToFirst())
		{
			while (!myCursor.isAfterLast())
			{
				DBInteractor dbInteractor = new DBInteractor(myContext);
				PrivateEventUser sender = dbInteractor.getUser(myCursor.getString(myCursor.getColumnIndex(SENDERIDCOL)));
				PrivateEventUser receiver = dbInteractor.getUser(myCursor.getString(myCursor.getColumnIndex(RECEIVERIDCOL)));
				GCMMessage msg = new GCMMessage(sender, receiver, 
						myCursor.getString(myCursor.getColumnIndex(MSGTEXTCOL)), myCursor.getString(myCursor.getColumnIndex(SENDDATETIMECOL)), 
						myCursor.getString(myCursor.getColumnIndex(RXDATETIMECOL)));
				msg.setType(Utility.getGCMMsgTypeFrmVal(myCursor.getString(myCursor.getColumnIndex(MSGTYPECOL))));
				msgs.add(msg);
				myCursor.moveToNext();
			}
			myCursor.close();
		}
		checkAndClose(iOpened);
		return msgs;
	}
}
