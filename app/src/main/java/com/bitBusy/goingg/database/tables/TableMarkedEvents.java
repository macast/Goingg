/**
 * 
 */
package com.bitBusy.goingg.database.tables;

import com.bitBusy.goingg.database.setup.TableDefinition;

/**
 * @author SumaHarsha
 *
 */
public enum TableMarkedEvents implements TableDefinition{

	EVENTIDCOL("eventID", " TEXT"),
	MARKEDCOL("markedCol", " TEXT"),
	PRIMARYKEYSPEC("PRIMARY KEY", "(eventID)");

	private String myName;
	private String myDataType;
	
	private TableMarkedEvents(String the_name, String the_datatype)
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
