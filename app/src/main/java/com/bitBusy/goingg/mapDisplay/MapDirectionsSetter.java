/**
 * 
 */
package com.bitBusy.goingg.mapDisplay;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bitBusy.goingg.dialog.TransitInfoDialog;
import com.bitBusy.goingg.fromLocation.FromLocation;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * @author SumaHarsha
 *
 */
public class MapDirectionsSetter{

	/** zoom level*/
	private static final int ZOOMLEVEL = 10;
	
	/** map*/
	private GoogleMap myMap;
	
	/** markers*/
	private ArrayList<MarkerToAdd> myMarkers;
	
	/** lat lng to index of array*/
	private HashMap<String, Integer> myMarkerDataIndices;
		
	/** context*/
	private Context myContext;

	private HashMap<LatLng, MarkerAtLocation> myLocationMarkers;

	private Marker myPreviousClickedMarker;
	/**
	 * parameterized constructor
	 * @param the_map
	 * @param the_markers
	 */
	public MapDirectionsSetter(Context the_context, GoogleMap the_map, ArrayList<MarkerToAdd> the_markers)
	{
		myMap = the_map;
		if (myMap != null)
		{
			myMarkers = the_markers;
			myContext = the_context;
			myLocationMarkers = new HashMap<LatLng, MarkerAtLocation>();
			setMarkerListener();
		    setInfoWindowListener();
		}
	}
	
	
	/** add markers*/	
	public void addMarkers()
	{
		LatLng current = FromLocation.getInstance().getFromLoc();
		double minLatitude = current.latitude;
		double maxLatitude = current.latitude;
		double minLongitude = current.longitude;
		double maxLongitude = current.longitude;
		double markerlatitude;
		double markerlongitude;
		if(myMarkers != null)
		{
			LatLngBounds.Builder builder = new LatLngBounds.Builder();
			myMarkerDataIndices = new HashMap<String, Integer>();

			//sets markers
			int len = myMarkers.size();
			for(int i= 0; i<len ;i++)
			{
				MarkerToAdd marker = myMarkers.get(i);
				if (marker != null && myMap != null)
				{
					MarkerOptions markerOpt = marker.getMarker();
					LatLng position = markerOpt!=null?markerOpt.getPosition():null;

					if (position != null)
					{
						markerlatitude = position.latitude;
						markerlongitude =  position.longitude;
						minLatitude = Math.min(markerlatitude, minLatitude);
						maxLatitude = Math.max(markerlatitude, maxLatitude);
						minLongitude = Math.min(markerlongitude, minLongitude);
						maxLongitude = Math.max(markerlongitude, maxLongitude);
					    builder.include(position);
					    myMarkerDataIndices.put(markerOpt.getTitle(), i);
					    addToLocationMap(position);
						myMap.addMarker(markerOpt);
					}
				}	
				
			}					
//		LatLngBounds bounds = builder.build();
		   CameraUpdate cu = CameraUpdateFactory.newLatLngZoom
				   (new LatLng((maxLatitude + minLatitude)/2, (maxLongitude + minLongitude)/2), ZOOMLEVEL);
				   //bounds.getCenter());
 		    myMap.animateCamera(cu);    

		}
	}
	
private void setMarkerListener()
{
	myMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
		
		@Override
		public boolean onMarkerClick(Marker the_marker) {
			if (the_marker != null&& the_marker != myPreviousClickedMarker)
			{
				myPreviousClickedMarker = the_marker;
				int index = myMarkerDataIndices!=null?myMarkerDataIndices.get(the_marker.getTitle()):0;
				MarkerToAdd marker = myMarkers!=null?myMarkers.get(index):null;
				TransitStopDetails stopdets = marker!=null?marker.getTransitDetails():null;
					if (stopdets != null)
				{
					MarkerAtLocation markersHere = myLocationMarkers.get(stopdets.getLatLng());
					if (markersHere != null)
					{
						int total = markersHere.getTotalMarkers();
						if (total > 1)
						{
							int clicks = markersHere.getClicks();
							toast(total, clicks);
							int updateClicks =(clicks+1)%(total+1);
							clicks = updateClicks==0?1:updateClicks;
							markersHere.setClicks(clicks);
						}
					}
				}
			}
			return false;
		}
	});

}

private void toast(int the_total, int the_clicks) 
{
	Toast toast = Toast.makeText(myContext.getApplicationContext(),  "( "+String.valueOf(the_clicks) + " of " + 
			String.valueOf(the_total)+" )" + " \n" +	String.valueOf(the_total) + " routes' informations here. Click again for next.", Toast.LENGTH_LONG);
	LinearLayout layout = (LinearLayout) toast.getView();
	if (layout.getChildCount() > 0) {
	  TextView tv = (TextView) layout.getChildAt(0);
	  tv.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
	}
	toast.show();
	
}
	/**
	 * method to set listener on info window
	 * @param marker
	 */
	private void setInfoWindowListener() {
				myMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener()
			{
				@Override
				public void onInfoWindowClick(Marker the_marker) {
					int index = myMarkerDataIndices!=null?myMarkerDataIndices.get(the_marker.getTitle()):0;
					MarkerToAdd marker = myMarkers!=null?myMarkers.get(index):null;
					new TransitInfoDialog(myContext,marker).openDetailsDialog();	
			}

				
			});
		
	}
	
	/**
	 * @param the_event
	 */
	private void addToLocationMap(LatLng the_coords) {
		if (the_coords!=null)
		{
			MarkerAtLocation markershere = myLocationMarkers.get(the_coords);
			if (markershere == null)
			{
				markershere = new MarkerAtLocation(1,1);
				myLocationMarkers.put(the_coords, markershere);
			}
			else
			{
				markershere.setTotalMarkers((markershere.getTotalMarkers()+1));
			}
		
		}
	}
}
