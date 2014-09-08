/**
 * 
 */
package com.bitBusy.goingg.dialog;

import com.bitBusy.goingg.settings.SearchSetting;

/**
 * @author SumaHarsha
 *
 */
public interface SearchSettingListener {
	
	
	/**
	 * @param the_setting
	 * @param the_state
	 */
	void onSearch(SearchSetting the_setting);

}
