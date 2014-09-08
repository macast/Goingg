/**
 * 
 */
package com.bitBusy.goingg.database.setup;

import com.bitBusy.goingg.database.tables.TableMarkedEvents;
import com.bitBusy.goingg.database.tables.TableMessages;
import com.bitBusy.goingg.database.tables.TableSavedReminders;
import com.bitBusy.goingg.database.tables.TableUserAccount;

/**
 * @author SumaHarsha
 *
 */
public enum TablesEnum {

	SAVEDREMINDERS(TableSavedReminders.class),
	MARKEDEVENTS(TableMarkedEvents.class),
	USER(TableUserAccount.class),
	MESSAGES(TableMessages.class);
	
	private Class<?> myClass;
	private TablesEnum(Class<?> the_class)
	{
		myClass = the_class;
	}
	
	public Class<?> getClassRef()
	{
		return myClass;
	}
	
}


