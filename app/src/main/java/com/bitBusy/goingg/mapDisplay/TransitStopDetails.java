/**
 * 
 */
package com.bitBusy.goingg.mapDisplay;

import com.bitBusy.goingg.events.Link;
import com.google.android.gms.maps.model.LatLng;

/**
 * @author SumaHarsha
 *
 */
public class TransitStopDetails {

	/** location*/
	private LatLng myLatLng;
	
	/** stop name*/
	private String myStopName;
	
	/** vehicle dets*/
	private String myVehicleDets;
	
	/** arrival or dep time*/
	private String myArrDepTime;
	
	/** transit agency name*/
	private String myTransitAgencyName;
	
	/** transit agency url*/
	private Link myTransitURL;
	
	/** no of stops*/
	private String myNoStops;
	
	/** line url*/
	private Link myLineURL;
	
	/** ph no*/
	private String myPhoneNo;
	/**
	 * parameterized constructor
	 * @param the_latLng
	 * @param the_stopName
	 * @param the_vehicleDets
	 * @param the_arrDepTime
	 */
	public TransitStopDetails(LatLng the_latLng, String the_stopName, String the_vehicleDets, Link the_lineURL, String the_arrDepTime, String numStops,
			String the_transitName, Link the_transitURL, String the_phoneNo)
	{
		myLatLng = the_latLng;
		myStopName = the_stopName;
		myVehicleDets = the_vehicleDets; 
		myArrDepTime = the_arrDepTime;
		myNoStops = numStops;
		myTransitAgencyName = the_transitName;
		myTransitURL = the_transitURL;
		myLineURL = the_lineURL;
		myPhoneNo = the_phoneNo;
	}
	
	/**
	 * gets location
	 * @return
	 */
	public LatLng getLatLng()
	{
		return myLatLng;
	}
	
	/**
	 * gets stop name
	 * @return
	 */
	public String getStopName()
	{
		return myStopName;
	}
	
	/**
	 * gets vehicle dets
	 * @return
	 */
	public String getVehicleDets()
	{
		return myVehicleDets;
	}
	
	/**
	 * gets arrival/dep time
	 * @return
	 */
	public String getArrDepTime()
	{
		return myArrDepTime;
	}
	
	/**
	 * gets no of stops
	 * @return
	 */
	public String getNoOfStops()
	{
		return myNoStops;
	}
	
	public String getTransitAgencyName()
	{
		return myTransitAgencyName;
	}
	
	public Link getTransitURL()
	{
		return myTransitURL;
	}
	
	/** gets line url*/
	public Link getLineURL()
	{
		return myLineURL;
	}
	
	public String getPhoneNo()
	{
		return myPhoneNo;
	}
}
