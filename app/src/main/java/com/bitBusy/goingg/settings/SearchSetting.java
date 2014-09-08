/**
 * 
 */
package com.bitBusy.goingg.settings;

import com.bitBusy.goingg.dialog.SearchSettingsDialog;

/**
 * @author SumaHarsha
 *
 */
public class SearchSetting {
	
	private QueryFilter myQueryFilter;
	
	private SearchSettingsDialog.DISPLAYOPTIONS myDisplayState;
	
	public SearchSetting(QueryFilter the_filter, SearchSettingsDialog.DISPLAYOPTIONS the_state)
	{
		myQueryFilter = the_filter;
		myDisplayState = the_state;
	}
	
	public QueryFilter getQueryFilter()
	{
		return myQueryFilter;
	}
	
	public SearchSettingsDialog.DISPLAYOPTIONS getDisplayState()
	{
		return myDisplayState;
	}
	
	@Override
	public boolean equals(Object the_object)
	{
		if (the_object != null && the_object instanceof SearchSetting)
		{
			SearchSetting object = (SearchSetting) the_object;
			return (myQueryFilter!=null?myQueryFilter.equals(object):object==null) && 
					myDisplayState == object.getDisplayState();
		}
		return false;
	}
	
	@Override
	public int hashCode()
	{
		return myQueryFilter.hashCode() + myDisplayState.hashCode();
	}

}
