/**
 * 
 */
package com.bitBusy.goingg.dialog;

import com.bitBusy.goingg.WIDAuthentication.WIDAccessToken;

/**
 * @author SumaHarsha
 *
 */
public interface WIDAuthenticationListener {

	/**
	 * @param wasAuthenticated
	 * @param the_token
	 * @param the_authenticator
	 */
	void onWIDAuthenticated(boolean wasAuthenticated, WIDAccessToken the_token);

}
