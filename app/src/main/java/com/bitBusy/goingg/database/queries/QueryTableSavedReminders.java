/**
 * 
 */
package com.bitBusy.goingg.database.queries;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.bitBusy.goingg.alarmSystem.EventReminder;
import com.bitBusy.goingg.database.tables.TableSavedReminders;
import com.bitBusy.goingg.events.Address;
import com.bitBusy.goingg.events.EventCategory;
import com.bitBusy.goingg.events.Link;
import com.bitBusy.goingg.events.PublicEvent;
import com.bitBusy.goingg.utility.Utility;
import com.google.android.gms.maps.model.LatLng;

/**
 * @author SumaHarsha
 *
 */
public class QueryTableSavedReminders extends DBQueries{

	/**
	 * @param the_context
	 */
	public QueryTableSavedReminders(Context the_context) {
		super(the_context);
		// TODO Auto-generated constructor stub
	}
	
	/** Name of table related to queries in this class*/
	private static final String tableName = TableSavedReminders.class.getSimpleName();
	
	/** String for the category column name*/
	private static final String idCol = TableSavedReminders.IDCOL.getName();
			
	/** String for the category column name*/
	private static final String nameCol = TableSavedReminders.NAMECOL.getName();
	
	/** String for the category column name*/
	private static final String categoryCol = TableSavedReminders.CATEGORYCOL.getName();
	
	/** String for the category column name*/
	private static final String startCol = TableSavedReminders.START.getName();
	
	/** String for the category column name*/
	private static final String endCol = TableSavedReminders.END.getName();
	
	/** String for the category column name*/
	private static final String latCol = TableSavedReminders.LAT.getName();

	private static final String lngCol = TableSavedReminders.LNG.getName();
		
	private static final String streetCol = TableSavedReminders.STREET.getName();
		
	private static final String cityCol = TableSavedReminders.CITY.getName();
	
	private static final String stateCol = TableSavedReminders.STATE.getName();
	
	private static final String countryCol = TableSavedReminders.COUNTRY.getName();
	
	private static final String postalCodeCol = TableSavedReminders.POSTALCODE.getName();
	
	private static final String infoCol = TableSavedReminders.INFO.getName();
	
	private static final String imgCol = TableSavedReminders.IMAGE.getName();
	
	private static final String descCol = TableSavedReminders.DESC.getName();
		
	private static final String priceCol = TableSavedReminders.PRICE.getName();

	private static final String googlePlaceCol = TableSavedReminders.ISGOOGLEPLACE.getName();

	private static final String createdByCol = TableSavedReminders.CREATOR.getName();
	
	private static final String ratingCol = TableSavedReminders.RATING.getName();
	
	private static final String numGoingCol = TableSavedReminders.NUMGOING.getName();
	
	private static final String numNopeCol = TableSavedReminders.NUMNOPE.getName();
	
	private static final String numMaybeCol = TableSavedReminders.NUMMAYBE.getName();
	
	private static final String dateTimeCol = TableSavedReminders.DATETIME.getName();


		/** String array of all column names in this table*/
	private static final String[] allColumns = new String[]{idCol, nameCol, categoryCol, startCol, endCol, latCol, lngCol, streetCol, 
		 cityCol, stateCol, countryCol, postalCodeCol, infoCol, imgCol, descCol, priceCol, googlePlaceCol, dateTimeCol, createdByCol, ratingCol,
		 numGoingCol, numNopeCol, numMaybeCol};
		
	/** Reference to the Cursor object used to store and process query results*/
	private Cursor myCursor;




	/**
	 * @param i
	 * @param j
	 * @param k
	 * @param l
	 * @param m
	 * @return 
	 */
	public boolean saveAlarm(EventReminder the_reminder) {
		boolean iOpened = checkAndOpen();
		boolean returnValue = true;
		PublicEvent event = the_reminder.getEvent();
	ContentValues contentInsert = new ContentValues();
			contentInsert.put(idCol, the_reminder.getID());
			if (event != null)
			{
				contentInsert.put(nameCol, event.getName());
				contentInsert.put(categoryCol, event.getCategory().getName().toString());
				contentInsert.put(startCol, Utility.getDateTime(event.getStartDateTime()));
				contentInsert.put(endCol, Utility.getDateTime(event.getEndDateTime()));
				LatLng coords = event.getCoordinates();
				if (coords != null)
				{
					contentInsert.put(latCol, coords.latitude);
					contentInsert.put(lngCol, coords.longitude);
				}
				Address addr = event.getAddress();
				if (addr != null)
				{
					contentInsert.put(streetCol, addr.getStreetAdd());
					contentInsert.put(cityCol, addr.getCity());
					contentInsert.put(stateCol, addr.getState());
					contentInsert.put(countryCol, addr.getCountry());
					contentInsert.put(postalCodeCol, addr.getPostalCode());
				}
				contentInsert.put(infoCol, event.getInfoLink()!=null?event.getInfoLink().getURL():null);
				contentInsert.put(imgCol, getImgByteArray(event));
				contentInsert.put(descCol, event.getDescription());
				contentInsert.put(priceCol, event.getPrice());
				contentInsert.put(googlePlaceCol, (event.isGooglePlace()?1:0));
				contentInsert.put(createdByCol, event.getCreator());
				contentInsert.put(ratingCol, event.getRating());
				contentInsert.put(numGoingCol, event.getNumGoing());
				contentInsert.put(numNopeCol, event.getNumNope());
				contentInsert.put(numMaybeCol, event.getNumMaybe());
			}
			contentInsert.put(dateTimeCol, Utility.getDateTime(the_reminder.getDateTime()));

			try
			{
			myDatabase.insertOrThrow(tableName, null, contentInsert);
			}
			catch(Exception sqlex)
			{
				returnValue = false;
			}		
			finally
			{
				checkAndClose(iOpened);
			}
			return returnValue;
	
	}

	/**
	 * @param event
	 * @return
	 */
	private byte[] getImgByteArray(PublicEvent the_event) {
		Bitmap img = the_event.getImage();
		if (img != null)
		{
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			img.compress(Bitmap.CompressFormat.PNG, 100, bos);
			return bos.toByteArray();
		}
		return null;	
	}

	/**
	 * @param the_reminder
	 */
	public boolean deleteAlarm(EventReminder the_reminder) {
		boolean retValue = false;
		if (the_reminder != null)
		{
			boolean iOpened = checkAndOpen();
					if (myDatabase.delete(tableName, idCol + " = " + the_reminder.getID(), null)== 1)
					{
						retValue = true;
					}					
			checkAndClose(iOpened);
		}
		return retValue;
	}

	/**
	 * @return
	 */
	public ArrayList<EventReminder> getAllReminders() {
		boolean iOpened = checkAndOpen();
		ArrayList<EventReminder> reminders = new ArrayList<EventReminder>();
		myCursor = myDatabase.query
		(tableName, allColumns, null, null, null, null, null);
		if (myCursor != null && myCursor.moveToFirst())
		{
			while (!myCursor.isAfterLast())
			{
				reminders.add(new EventReminder(myCursor.getInt(myCursor.getColumnIndex(idCol)), 
						new PublicEvent(
								EventCategory.getCategory(myCursor.getString(myCursor.getColumnIndex(categoryCol))),
								 myCursor.getString(myCursor.getColumnIndex(nameCol)), 
								 myCursor.getString(myCursor.getColumnIndex(descCol)), 
								 new Address(
								 myCursor.getString(myCursor.getColumnIndex(streetCol)),
								 myCursor.getString(myCursor.getColumnIndex(cityCol)),
								 myCursor.getString(myCursor.getColumnIndex(stateCol)),
								 myCursor.getString(myCursor.getColumnIndex(countryCol)),
								 myCursor.getString(myCursor.getColumnIndex(postalCodeCol))),
								 Utility.getCalendarForReminder(myCursor.getString(myCursor.getColumnIndex(startCol))), 
								 Utility.getCalendarForReminder(myCursor.getString(myCursor.getColumnIndex(endCol))),
								 new LatLng(myCursor.getFloat(myCursor.getColumnIndex(latCol)), 
										    myCursor.getFloat(myCursor.getColumnIndex(lngCol))), 
										    new Link(myCursor.getString(myCursor.getColumnIndex(infoCol))),
										    getBitmap(myCursor.getBlob(myCursor.getColumnIndex(imgCol))), 
										    myCursor.getString(myCursor.getColumnIndex(priceCol)), 
											 myCursor.getInt(myCursor.getColumnIndex(googlePlaceCol))==0?false:true, 
											myCursor.getString(myCursor.getColumnIndex(createdByCol)), myCursor.getString(myCursor.getColumnIndex(ratingCol)),
											myCursor.getInt(myCursor.getColumnIndex(numGoingCol)), myCursor.getInt(myCursor.getColumnIndex(numNopeCol)),
											myCursor.getInt(myCursor.getColumnIndex(numMaybeCol))),
										    		Utility.getCalendarForReminder(myCursor.getString(myCursor.getColumnIndex(dateTimeCol)))));
								
				myCursor.moveToNext();
			}
			myCursor.close();
		}
		checkAndClose(iOpened);
		return reminders;
	}

	/**
	 * @param blob
	 * @return
	 */
	private Bitmap getBitmap(byte[] the_blob) {
		if(the_blob != null)
		{
	        return BitmapFactory.decodeByteArray(the_blob, 0, the_blob.length);
		}
		return null;
	}

}
