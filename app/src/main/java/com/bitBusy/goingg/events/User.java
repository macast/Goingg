/**
 * 
 */
package com.bitBusy.goingg.events;

/**
 * @author SumaHarsha
 *
 */
public class User {

	private int myEventsSubmitted;
	
	private int mySpamGen;
	
	private int mySpamRep;
	
	private String myID;
	
	public User(String the_id, int the_eventsSub, int the_spamGen, int the_spamRep)
	{
		myID = the_id;
		myEventsSubmitted = the_eventsSub;
		mySpamGen = the_spamGen;
		mySpamRep = the_spamRep;
	}
	
	public String getID()
	{
		return myID;
	}
	
	public int getEventsSubmitted()
	{
		return myEventsSubmitted;
	}
	
	public int getSpamGenerated()
	{
		return mySpamGen;
	}
	
	public int getSpamReported()
	{
		return mySpamRep;
	}
}
