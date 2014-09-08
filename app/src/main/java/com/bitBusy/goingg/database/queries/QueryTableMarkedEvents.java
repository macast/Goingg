/**
 * 
 */
package com.bitBusy.goingg.database.queries;

import java.util.HashMap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;

import com.bitBusy.goingg.database.setup.DBInteractor;
import com.bitBusy.goingg.database.tables.TableMarkedEvents;
import com.bitBusy.goingg.utility.Utility;

/**
 * @author SumaHarsha
 *
 */
public class QueryTableMarkedEvents extends DBQueries{
	
	private static final String TABLENAME = TableMarkedEvents.class.getSimpleName();
	
	private static final String EVENTIDCOL = TableMarkedEvents.EVENTIDCOL.getName();
	
	private static final String EVENTMARKING = TableMarkedEvents.MARKEDCOL.getName();
	
	private static final String[] ALLCOLS = new String[]{EVENTIDCOL, EVENTMARKING};
	
	private Context myContext;
	
	private Cursor myCursor;

	public QueryTableMarkedEvents(Context the_context)
	{
		super(the_context);
		myContext = the_context;
	}
	
	public boolean markEvent(String the_eventID, DBInteractor.EVENTMARKING the_marking)
	{
		boolean returnValue = false;
		if (the_eventID != null)
		{
			boolean iOpened = checkAndOpen();
			ContentValues contentInsert = new ContentValues();
			contentInsert.put(EVENTIDCOL, the_eventID);
			contentInsert.put(EVENTMARKING, the_marking.name());
			try
			{
			myDatabase.insertOrThrow(TABLENAME, null, contentInsert);
			returnValue = true;
			}
			catch(SQLiteConstraintException e)
			{
				try
				{
					ContentValues contentInsertMod = new ContentValues();
					contentInsertMod.put(EVENTMARKING, the_marking.name());
					myDatabase.update(TABLENAME, contentInsertMod, EVENTIDCOL + " = ?", new String[]{the_eventID});
					returnValue = true;
				}
				catch(Exception ex)
				{
					returnValue = false;
					Utility.throwErrorMessage(myContext, "Acknowledgement save error", ex.getMessage());
				}
			}
			catch(Exception sqlex)
			{
				Utility.throwErrorMessage(myContext, "Acknowledgement save error", sqlex.getMessage());
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
	
	public boolean deleteEventMarking(String the_eventID)
	{
		boolean retValue = false;
		if (the_eventID != null)
		{
			boolean iOpened = checkAndOpen();
			try
			{
						if (myDatabase.delete(TABLENAME, EVENTIDCOL + " = ?", new String[]{the_eventID})== 1)
						{
							retValue = true;
						}	
			}
			catch(Exception e)
			{
				Utility.throwErrorMessage(myContext, "Acknowledgement save error", e.getMessage());
				retValue = false;
			}
			finally
			{
				checkAndClose(iOpened);
			}
		}
		return retValue;

	}

	/**
	 * @return
	 */
	public HashMap<String, com.bitBusy.goingg.database.setup.DBInteractor.EVENTMARKING> getAllEventMarkings()
	{
		boolean iOpened = checkAndOpen();
		HashMap<String, com.bitBusy.goingg.database.setup.DBInteractor.EVENTMARKING> map =
				new HashMap<String, com.bitBusy.goingg.database.setup.DBInteractor.EVENTMARKING>();
		myCursor = myDatabase.query
		(TABLENAME, ALLCOLS, null, null, null, null, null);
		if (myCursor != null && myCursor.moveToFirst())
		{
			while (!myCursor.isAfterLast())
			{
				map.put(myCursor.getString(myCursor.getColumnIndex(EVENTIDCOL)), 
						DBInteractor.EVENTMARKING.valueOf(myCursor.getString(myCursor.getColumnIndex(EVENTMARKING))));											
				myCursor.moveToNext();
			}
			myCursor.close();
		}
		checkAndClose(iOpened);
		return map;
	}
}
