/**
 * 
 */
package com.bitBusy.goingg.settings;

import java.util.Calendar;

import com.bitBusy.goingg.events.Address;

/**
 * @author SumaHarsha
 *
 */
public class DirectionSetting {
	
	/** from address*/
	private Address myFromAddress;
	
	/** mode of transport*/
	private String myMode;
	
	/** highway avoided*/
	private boolean myHighwayAvoided;
	
	/** toll avoided*/
	private boolean myTollAvoided;
	
	/** is depart time*/
	private boolean myDepartAtSet;
	
	/** set date time*/
	private Calendar myDateTime;
	
	
	/**
	 * parameterized constructor
	 * @param the_fromAddress
	 * @param the_mode
	 * @param the_highwayFlag
	 * @param the_tollFlag
	 * @param the_departRadio
	 * @param the_dateTime
	 */
	public DirectionSetting(Address the_fromAddress, String the_mode, boolean the_highwayFlag, boolean the_tollFlag, boolean the_departRadio,
							Calendar the_dateTime)
	{
		myFromAddress = the_fromAddress;
		myMode = the_mode;
		myHighwayAvoided = the_highwayFlag;
		myTollAvoided = the_tollFlag;
		myDepartAtSet = the_departRadio;
		myDateTime = the_dateTime;
	}
	
	
	/**
	 * gets from address
	 * @return
	 */	
	public Address getFromAddress()
	{
		return myFromAddress;
	}
	
	/**
	 * gets mode
	 * @return
	 */
	public String getMode()
	{
		return myMode;
	}
	
	/**
	 * is highway avoided
	 * @return
	 */
	public boolean isHighwayAvoided()
	{
		return myHighwayAvoided;
	}
	/**
	 * is toll avoided
	 * @return
	 */
	public boolean isTollAvoided()
	{
		return myTollAvoided;
	}
	/**
	 * is depart date/time set
	 * @return
	 */
	public boolean isDepartAtSet()
	{
		return myDepartAtSet;
	}
	/**
	 * get date time set
	 * @return
	 */
	public Calendar getDateTime()
	{
		return myDateTime;
	}

	public boolean equals(DirectionSetting the_setting)
	{
		if (the_setting != null)
		{
			return (myFromAddress!=null?myFromAddress.equals(the_setting.getFromAddress()):the_setting.getFromAddress()== null &&
					myMode != null? myMode.equals(the_setting.getMode()):the_setting.getMode()==null && 
					myHighwayAvoided == the_setting.isHighwayAvoided() && myTollAvoided == the_setting.isTollAvoided() &&
					myDepartAtSet == the_setting.isDepartAtSet() && myDateTime != null?myDateTime.equals(the_setting.getDateTime()):the_setting.getDateTime()==null);
		}
		return false;
	}
}
