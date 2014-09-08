/**
 * 
 */
package com.bitBusy.goingg.mapDisplay;

import com.google.android.gms.maps.model.MarkerOptions;

/**
 * @author SumaHarsha
 *
 */
public class MarkerToAdd {
	

	/**marker options*/
	private MarkerOptions myMarkerOptions;
	
	/** details*/
	private TransitStopDetails myTransitDetails;
	
	/** arr dep flag*/
	private boolean myFlagIsDepart;
	/**constructor
	 * @param the_markerOptions
	 * @param the_stopDetails
	 */
	public MarkerToAdd(MarkerOptions the_markerOptions, TransitStopDetails the_stopDetails, boolean the_dep)
	{
		myMarkerOptions = the_markerOptions;
		myTransitDetails = the_stopDetails;
		myFlagIsDepart = the_dep;
	}
	
	/**
	 * gets marker options
	 * @return
	 */	
	public MarkerOptions getMarker()
	{
		return myMarkerOptions;
	}
	
	/**
	 * get transit details
	 * @return
	 */
	public TransitStopDetails getTransitDetails()
	{
		return myTransitDetails;
	}
	
	/**
	 * determines if depart
	 * @return
	 */
	
	public boolean isDepart()
	{
		return myFlagIsDepart;
	}
}
