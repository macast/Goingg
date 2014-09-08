/**
 * 
 */
package com.bitBusy.goingg.fromLocation;

import com.google.android.gms.maps.model.LatLng;

/**
 * @author SumaHarsha
 *
 */
public interface LocationClientListener {
	
	/**
	 * Method called when the location has been determined
	 * @param the_location
	 */
	public void onLocationIdentified(LatLng the_location);

}
