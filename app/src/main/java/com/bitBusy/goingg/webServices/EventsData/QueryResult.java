/**
 * 
 */
package com.bitBusy.goingg.webServices.EventsData;

/**
 * @author SumaHarsha
 *
 */
public class QueryResult {

		
	private String myURI;
	
	private Object myData;
	
	public QueryResult(String the_uri, Object the_data)
	{
		myData = the_data;
		myURI = the_uri;
	}
	
	public Object getData()
	{
		return myData;
	}
	
	public String getURI()
	{
		return myURI;
	}
	
}
