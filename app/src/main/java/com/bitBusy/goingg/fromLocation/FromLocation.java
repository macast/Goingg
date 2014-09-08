/**
 * 
 */
package com.bitBusy.goingg.fromLocation;

import com.bitBusy.goingg.events.Address;
import com.google.android.gms.maps.model.LatLng;

/**
 * @author SumaHarsha
 *
 */
public class FromLocation{

	/** from*/
	private LatLng myFromLoc;
	
	/** address*/
	private Address myFromAddress;
	
	private static FromLocation myInstance;
	/**
	 * set the location
	 * @param the_fromLoc
	 */
	
	private FromLocation()
	{
		
	}
	
	public static FromLocation getInstance()
	{
		if (myInstance == null)
		{
			myInstance = new FromLocation();
		}
		return myInstance;
	}
	
	public void setFromLoc(LatLng the_fromLoc)
	{
		myFromLoc = the_fromLoc;
	}
	
	/**
	 * sets add
	 * @param the_fromAddress
	 */
	public void setFromAdd(Address the_fromAddress)
	{
		myFromAddress = the_fromAddress;
	}
	/**
	 * returns from loc
	 * @return
	 */
	public LatLng getFromLoc()
	{
		return myFromLoc;
	}
	
	/**
	 * gets from add
	 * @return
	 */
	public Address getFromAdd()
	{
		return myFromAddress;
	}
}
