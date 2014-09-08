/**
 * 
 */
package com.bitBusy.goingg.database.tables;

import com.bitBusy.goingg.database.setup.TableDefinition;

/**
 * @author SumaHarsha
 *
 */
public enum TableMessages implements TableDefinition{

	MSGIDCOL("msgID", " TEXT"),
	MSGTYPE("msgType", " TEXT"),
	MSGTEXT("msgText", " TEXT"),
	SENDERID("senderID", " TEXT"),
	RECEIVERID("receiverID", " TEXT"),
	SENDDATETIME("sendDateTime", " TEXT"),
	RXDATETIME("rxDateTime", " Text"),
	RXEDMESSAGE("rxed", " INTEGER"),
	PRIMARYKEYSPEC("PRIMARY KEY", "(msgID)");
	
	private String myName;
	private String myDataType;
	
	private TableMessages(String the_name, String the_datatype)
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
