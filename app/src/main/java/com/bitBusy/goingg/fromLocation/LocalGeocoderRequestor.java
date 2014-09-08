/**
 * 
 */
package com.bitBusy.goingg.fromLocation;

import com.google.android.gms.maps.model.LatLng;

/**
 * @author SumaHarsha
 *
 */
public interface LocalGeocoderRequestor {

	/** method called when lat lng ready*/
	public void acceptLatLng(LatLng the_latLng);
}
