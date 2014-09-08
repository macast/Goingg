/**
 * 
 */
package com.bitBusy.goingg.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.bitBusy.goingg.activity.ActivityContacts;
import com.bitBusy.goingg.activity.ActivityCreatePrivateEvent;
import com.bitBusy.goingg.activity.ActivityViewMessages;
import com.bitBusy.goingg.cache.DataStructureCache;
import com.bitBusy.goingg.database.setup.DBInteractor;
import com.bitBusy.goingg.utility.CurrentLoginState;

/**
 * @author SumaHarsha
 *
 */
public class MyAccountTabAdapter extends FragmentPagerAdapter {
 
	private Context myContext;
	
    public MyAccountTabAdapter(FragmentManager fm, Context the_context) {
        super(fm);
        myContext = the_context;
    }
 
    @Override
    public Fragment getItem(int index) {
 
        switch (index) {
        case 0:
            return new ActivityViewMessages();
        case 1:
            return new ActivityContacts();
        case 2:
            return new ActivityCreatePrivateEvent();
        }
        return null;
    }
    
    @Override
    public int getCount() {
        // get item count - equal to number of tabs
        return 3;
    }
 
}
