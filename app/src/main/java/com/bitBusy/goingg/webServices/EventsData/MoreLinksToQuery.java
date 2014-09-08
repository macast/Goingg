/**
 * 
 */
package com.bitBusy.goingg.webServices.EventsData;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author SumaHarsha
 *
 */
public class MoreLinksToQuery {

	
	private LinkedBlockingQueue<String> myLinks;
	
	private static MoreLinksToQuery myInstance;
	
	private MoreLinksToQuery()
	{
		myLinks = new LinkedBlockingQueue<String>();
	}
	
	public static MoreLinksToQuery getInstance()
	{
		if (myInstance == null)
		{
			myInstance = new MoreLinksToQuery();
		}
		return myInstance;
	}
	
	public void addLink(String the_link)
	{
		if(the_link != null && the_link.length() > 0)
		{
			myLinks.add(the_link);
		}
	}
	
	public String removeFirstLink()
	{
		if (myLinks.size() > 0)
		{
			return myLinks.remove();
		}
		return null;
	}
	
	public int getLinksQueueSize()
	{
		return myLinks.size();
	}
	public void clearQueue()
	{
		myLinks.clear();
	}
}
