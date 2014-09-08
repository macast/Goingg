package com.bitBusy.goingg.activity;

import java.util.ArrayList;
import java.util.HashSet;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.bitBusy.goingg.adapters.ListAdapter;
import com.bitBusy.goingg.cache.DataStructureCache;
import com.bitBusy.goingg.dialog.AddContactDialog;
import com.bitBusy.goingg.events.PrivateEvents.PrivateEventUser;
import com.bitBusy.goingg.R;

public class ActivityContacts extends Fragment {

	private View myParentView;
	private Context myContext;
	private ListAdapter myListAdapter;
	

	 @Override
	  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		 myParentView = inflater.inflate(R.layout.activity_contacts, container, false);
		 myContext = getActivity();
		 setList();
		 setAddButtonListener();
		 return myParentView;	 
	 }
		
	private void setAddButtonListener()
	{
		if (myParentView != null)
		{
			Button add = (Button) myParentView.findViewById(R.id.activity_contacts_addnew);
			add.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View arg0) 
				{
					new AddContactDialog(myContext, ActivityContacts.this).openDialog();
				} 
				
			});
		}
	}

	private void setList() 
	{
		if (myParentView != null && myContext != null)
		{
			HashSet<PrivateEventUser> frnds = DataStructureCache.getInstance().getFriends();
			if (frnds != null){
				myListAdapter = new ListAdapter(myContext, new ArrayList<PrivateEventUser>(), null);
				ListView listview = (ListView) myParentView.findViewById(R.id.activity_contacts_list);
				listview.setAdapter(myListAdapter);
			}
		}
	}

	
	public void refreshList()
	{
		if (myListAdapter != null)
		{
			myListAdapter.notifyDataSetChanged();
		}
	}
}
