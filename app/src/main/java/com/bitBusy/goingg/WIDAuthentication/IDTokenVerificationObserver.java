/**
 * 
 */
package com.bitBusy.goingg.WIDAuthentication;

/**
 * @author SumaHarsha
 *
 */
public interface IDTokenVerificationObserver {

	public void onIDTokenVerified(boolean wasVerified, WIDAccessToken the_token);
}
