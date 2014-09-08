/**
 * 
 */
package com.bitBusy.goingg.cache;

import java.util.HashMap;
import java.util.Set;

import com.bitBusy.goingg.database.setup.DBInteractor.EVENTMARKING;

/**
 * @author SumaHarsha
 *
 */
public class LocalDBCache 
{
	private static LocalDBCache myInstance;
	private static HashMap<String, EVENTMARKING> myMap;
	
	private LocalDBCache()
	{
		
	}
	
	public static LocalDBCache getInstance()
	{
		if (myInstance == null)
		{
			myInstance = new LocalDBCache();
			myMap = new HashMap<String, EVENTMARKING>();
		}
		return myInstance;
	}
	
	public void addToDBCache(HashMap<String, EVENTMARKING> the_map)
	{
		if (the_map != null && the_map.keySet() != null)
		{
			Set<String> keys = the_map.keySet();
			for (String key: keys)
			{
				myMap.put(key, the_map.get(key));
			}
		}
	}
	
	public EVENTMARKING getEventMarking(String the_key)
	{
		if (the_key != null)
		{
			return myMap.get(the_key);
		}
		return null;
	}
	public void deleteFromCache(String the_key)
	{
		if (the_key != null)
		{
			myMap.remove(the_key);
		}
	}
}
