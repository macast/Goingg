/**
 * 
 */
package com.bitBusy.goingg.database.tables;

import com.bitBusy.goingg.database.setup.TableDefinition;

/**
 * @author SumaHarsha
 *
 */
public enum TableUserAccount implements TableDefinition{

	USERIDCOL("userID", " TEXT"),
	FNAMECOL("fName", " TEXT"),
	LNAMECOL("lName", " TEXT"),
	CITYCOL("city", " TEXT"),
	GCMIDSCOL("gcmIDs", " BLOB"),
	LOCALFRIENDSCOL("localFriends", " BLOB"),
	PRIMARYKEYSPEC("PRIMARY KEY", "(userID)");
	
	private String myName;
	private String myDataType;
	
	private TableUserAccount(String the_name, String the_datatype)
	{
		myName = the_name;
		myDataType = the_datatype;
	}
	/* (non-Javadoc)
	 * @see com.bitBusy.going.database.TableDefinition#getName()
	 */
	@Override
	public String getName() {
		return myName;
	}

	/* (non-Javadoc)
	 * @see com.bitBusy.going.database.TableDefinition#getType()
	 */
	@Override
	public String getType() {
		return myDataType;
	}
}
