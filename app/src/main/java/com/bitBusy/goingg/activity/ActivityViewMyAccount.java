package com.bitBusy.goingg.activity;

import java.util.HashSet;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.bitBusy.goingg.adapters.MyAccountTabAdapter;
import com.bitBusy.goingg.cache.DataStructureCache;
import com.bitBusy.goingg.database.setup.DBInteractor;
import com.bitBusy.goingg.events.PrivateEvents.PrivateEventUser;
import com.bitBusy.goingg.messaging.setup.gcmSetup.GCMMessage;
import com.bitBusy.goingg.utility.ActionBarChoice;
import com.bitBusy.goingg.utility.CurrentLoginState;
import com.bitBusy.goingg.R;

public class ActivityViewMyAccount extends FragmentActivity implements ActionBar.TabListener 
{
	public enum TABCHOICES {ShowInvites, ShowRecent, CreatePrivateEvent};
	private static final int[] TABIMGIDS = new int[]{R.drawable.msg, R.drawable.users, R.drawable.personalize};
	private ViewPager myViewPager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_my_account);
		setTitle();
		setTabs();
		
	}

	private void setTabs()
	{
		myViewPager = (ViewPager) findViewById(R.id.pager);
        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);        

        MyAccountTabAdapter tabAdapter = new MyAccountTabAdapter(getSupportFragmentManager(), this); 
        myViewPager.setAdapter(tabAdapter);
        myViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
        	 
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
 
            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }
 
            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
    
      //  actionBar.setHomeButtonEnabled(false);
 
        // Adding Tabs
        for (int id : TABIMGIDS) {
            actionBar.addTab(actionBar.newTab().setIcon(id).setTabListener(this));
        }		
	}

	private void setTitle()
	{
		String user = getUserName();
		setTitle(user);
	}

	private String getUserName()
	{
		return CurrentLoginState.getCurrentUser(this);
	}
	
	
	/* (non-Javadoc)
	 * @see android.app.ActionBar.TabListener#onTabReselected(android.app.ActionBar.Tab, android.app.FragmentTransaction)
	 */
	@Override
	public void onTabReselected(Tab arg0, FragmentTransaction arg1) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see android.app.ActionBar.TabListener#onTabSelected(android.app.ActionBar.Tab, android.app.FragmentTransaction)
	 */
	@Override
	public void onTabSelected(Tab arg0, FragmentTransaction arg1) {
		if (myViewPager != null)
		{
			initializeMessageList();
			initializeFriendsList();
			myViewPager.setCurrentItem(arg0.getPosition());
		}
	}
	
	private void initializeMessageList(){
		if (!DataStructureCache.getInstance().isMsgsInitialized()){
			HashSet<GCMMessage> msgs = new HashSet<GCMMessage>();
			msgs = new DBInteractor(this).getAllInboxMessages(CurrentLoginState.getCurrentUser(this));
			if (msgs != null){
				DataStructureCache.getInstance().addMsgs(msgs);
			}
		}
	}
	
	private void initializeFriendsList(){
		if (!DataStructureCache.getInstance().isFriendsListInitialized()){
			HashSet<PrivateEventUser> frnds = new HashSet<PrivateEventUser>();
			frnds = new DBInteractor(this).getAllFriends(CurrentLoginState.getCurrentUser(this));
			if (frnds != null){
				DataStructureCache.getInstance().addFriends(frnds);
			}
		}
	}
	/* (non-Javadoc)
	 * @see android.app.ActionBar.TabListener#onTabUnselected(android.app.ActionBar.Tab, android.app.FragmentTransaction)
	 */
	@Override
	public void onTabUnselected(Tab arg0, FragmentTransaction arg1) {
		// TODO Auto-generated method stub	
	}
	@Override
	  public boolean onCreateOptionsMenu(Menu the_menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.action_bar_menu, the_menu);
	    ActionBarChoice.setupActionBar(the_menu, this);
	    return true;
	  }
	  
	
	  @Override
	  public boolean onOptionsItemSelected(MenuItem the_item) {
	    com.bitBusy.goingg.utility.ActionBarChoice.choiceMade(this, the_item);

	    return true;
	  }
	  
}