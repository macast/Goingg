package com.bitBusy.goingg.activity;

import java.util.ArrayList;
import java.util.HashSet;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.bitBusy.goingg.adapters.ListAdapter;
import com.bitBusy.goingg.cache.DataStructureCache;
import com.bitBusy.goingg.messaging.setup.gcmSetup.GCMMessage;
import com.bitBusy.goingg.R;

public class ActivityViewMessages extends Fragment  {

	private View myParentView;
	private Context myContext;
	private ListAdapter myListAdapter;
	

	 @Override
	  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		 myParentView = inflater.inflate(R.layout.activity_view_messages, container, false);
		 myContext = getActivity();
		 setList();
		 return myParentView;	 
	 }
	
	 private void setList() 
		{
			if (myParentView != null && myContext != null)
			{
				HashSet<GCMMessage> msgs = DataStructureCache.getInstance().getMessages();
				if (msgs != null){
					myListAdapter = new ListAdapter(myContext,new ArrayList<GCMMessage>(msgs), null);
					ListView listview = (ListView) myParentView.findViewById(R.id.activity_view_messages);
					listview.setAdapter(myListAdapter);
				}
			}
		}
}
