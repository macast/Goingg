/**
 * 
 */
package com.bitBusy.goingg.database.tables;

import com.bitBusy.goingg.database.setup.TableDefinition;

/**
 * @author SumaHarsha
 *
 */
public enum TableSavedReminders implements TableDefinition{
	
	IDCOL("id", " INTEGER"),
	NAMECOL("name", " TEXT"),
	CATEGORYCOL("category", " TEXT"),
	START("start", " TEXT"),
	END("end", " TEXT"),
	LAT("lat", " REAL"),
	LNG("lng", " REAL"),
	STREET("street", " TEXT"),
	VENUE("venue", " TEXT"),
	CITY("city", " TEXT"),
	STATE("state", " TEXT"),
	COUNTRY("country", " TEXT"),
	POSTALCODE("postalcode", " TEXT"),
	INFO("info", " TEXT"),
	DESC("desc", " TEXT"),
	IMAGE("img", " BLOB"),
	PRICE("price", " STRING"),
	ISGOOGLEPLACE("googleplace", " INTEGER"),
	CREATOR("createdBy", " TEXT"),
	RATING("rating", " TEXT"),
	NUMGOING("numGoing", " INTEGER"),
	NUMMAYBE("numMaybe", " INTEGER"),
	NUMNOPE("numNope", " INTEGER"),
	DATETIME("datetime", " TEXT"),
	PRIMARYKEYSPEC("PRIMARY KEY", "(id)");

	private String myName;
	private String myDataType;
	
	private TableSavedReminders(String the_name, String the_datatype)
	{
		myName = the_name;
		myDataType = the_datatype;
	}
	/* (non-Javadoc)
	 * @see com.bitBusy.wevent.database.TableDefinition#getName()
	 */
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return myName;
	}

	/* (non-Javadoc)
	 * @see com.bitBusy.wevent.database.TableDefinition#getType()
	 */
	@Override
	public String getType() {
		// TODO Auto-generated method stub
		return myDataType;
	}

}
