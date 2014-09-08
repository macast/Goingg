package com.bitBusy.goingg.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;

import com.bitBusy.goingg.adapters.ListAdapter;
import com.bitBusy.goingg.database.setup.DBInteractor;
import com.bitBusy.goingg.utility.ActionBarChoice;
import com.bitBusy.goingg.R;

public class ActivityViewReminders extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_reminders);
		setListView();
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
	  
	
	private void setListView()
	{
		ListView listview = (ListView) findViewById(R.id.activity_view_reminders_list);
		listview.setAdapter(new ListAdapter(this, new DBInteractor(this).getAllReminders(), null));
	}

}
