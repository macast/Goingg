/**
 * 
 */
package com.bitBusy.goingg.mapDisplay;

/**
 * @author SumaHarsha
 *
 */
public class MarkerAtLocation {

	private int myTotalMarkers;
	
	private int myClicks;
	
	public MarkerAtLocation(int the_totalMarkers, int the_clicks)
	{
		myTotalMarkers = the_totalMarkers;
		myClicks = the_clicks;
	}
	
	public int getTotalMarkers()
	{
		return myTotalMarkers;
	}
	
	public int getClicks()
	{
		return myClicks;
	}
	
	public void setTotalMarkers(int the_markers)
	{
		myTotalMarkers = the_markers;
	}
	
	public void setClicks(int the_clicks)
	{
		myClicks = the_clicks;
	}
}
