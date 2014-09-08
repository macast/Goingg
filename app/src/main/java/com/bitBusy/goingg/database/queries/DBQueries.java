/**
 * 
 */
package com.bitBusy.goingg.database.queries;

import com.bitBusy.goingg.database.setup.DBManager;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

/**
 * @author SumaHarsha
 *
 */
public class DBQueries {
	/** The database into which data will be written or read from*/
	protected static SQLiteDatabase myDatabase;
	
	/** The database manager object to access the database*/
	protected DBManager myDBManager;

	/**The activity context*/
	protected Context myActivityContext;
	
	
	

	/** Constructor that initializes the database manager
	 * @param the_context 
	 */
	public DBQueries(Context the_context)
	{
		if (the_context != null)
		{
			myActivityContext = the_context;
		}
		myDBManager = DBManager.getInstance(the_context);
	}
	
	/** Method to get access to the database
	 * @throws android.database.SQLException when access not obtained
	 */
	private void open() throws SQLException
	{
		if (myDBManager != null)
		{
			myDBManager.setIsOpen(true);
			myDatabase = myDBManager.getMyWritableDatabase(); 
		}
	}
	
	/** Method to release access of database from the database manager*/
	private void close() 
	{
		myDBManager.setIsOpen(false);
		myDBManager.close();

	}

	/** Method to check if the db is opened. if not, opens and returns true
	 * @return boolean indicating if opened*/
	protected boolean checkAndOpen()
	{
		if (myDBManager!= null && !myDBManager.isOpen())
		{
			open();
			return true;
		}
		return false;
	}
	
	/** Method that closes db if it was opened by the caller
	 * @param flag indicating if caller opened
	 */
	protected void checkAndClose(boolean the_iOpened)
	{
		if(the_iOpened)
		{
			close();
		}
	}
}
