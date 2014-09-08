/**
 * 
 */
package com.bitBusy.goingg.dialog;

import com.bitBusy.goingg.settings.DirectionSetting;

/**
 * @author SumaHarsha
 *
 */
public interface DirectionDialogListener {

	/**
	 * method called when ok called on dialog
	 * @param the_fromLocation
	 * @param the_mode
	 */
	public void onSet(DirectionSetting the_setting);
}
