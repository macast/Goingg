/**
 *
 */
package com.bitBusy.goingg.fromLocation;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.model.LatLng;

/**
 * @author SumaHarsha
 *
 */
public class LocationIdentifier implements ConnectionCallbacks, OnConnectionFailedListener {



	// https://developers.google.com/maps/documentation/android/location
	        private LocationClient locationClient;

	        /** contexT*/
	        private Context myContext;

	        /** value to return*/
	        private LatLng myLastLoc;


	        /** listeners*/
	        private ArrayList<LocationClientListener> myListeners;

	        /**
	         * parameterized constructor
	         * @param the_context
	         */

	        public LocationIdentifier(Context the_context)
	        {
	        	myContext = the_context;
	        }

	        /**
	         * returns last known loc
	         * @return
	         */
	        public LatLng getLastKnownLoc()
	        {
	        	getLocation();
	        	return myLastLoc;
	        }

	        /** initializes location client and sends connect command*/
	        public void getLocation()
	        {
	        	locationClient = new LocationClient(myContext, this, this);
	        	connect();

	        }

	        /** connects*/
	        private void connect()
	        {
                locationClient.connect();
            }

	        /** disconnects*/
	        private void disconnect()
	        {
	        	locationClient.disconnect();
	        }

	        @Override
	        public void onConnected(Bundle arg0) {
	        	Runnable newThread = getLastLocRunnable();
	        	new Thread(newThread).start();
	        }

	        private Runnable getLastLocRunnable(){
	            Runnable thread = new Runnable() {
	                public void run() {
	                	Location loc = locationClient.getLastLocation();
	                	long startTime = System.currentTimeMillis();
	                	while (loc == null && System.currentTimeMillis() - startTime < 7000)
	                	{
	                	}
	                	if (loc != null){
	                	myLastLoc = new LatLng(loc.getLatitude(), loc.getLongitude());
	                	}
	                	((Activity) myContext).runOnUiThread(new Runnable(){
	                        public void run() {
	                        	notifyListeners();
	    	                	disconnect();
	                        }
	                	});
	                }
	            };
	            return thread;
	        }
	        /**
	         * method to notify listeners
	         */
	        private void notifyListeners()
	        {
	        	if (myListeners != null)
	        	{
	        		for (LocationClientListener listener: myListeners)
	        		{
	        			listener.onLocationIdentified(myLastLoc);
	        		}
	        	}
	        }

	        /**
	         * method to register a listener
	         * @param the_listener
	         */
	        public void registerListener(LocationClientListener the_listener)
	        {
	        	if (myListeners == null)
	        	{
	        		myListeners = new ArrayList<LocationClientListener>();
	        	}
	        	myListeners.add(the_listener);
	        }
			/* (non-Javadoc)
			 * @see com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener#onConnectionFailed(com.google.android.gms.common.ConnectionResult)
			 */
			@Override
			public void onConnectionFailed(ConnectionResult arg0) {
				// TODO Auto-generated method stub

			}
			/* (non-Javadoc)
			 * @see com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks#onDisconnected()
			 */
			@Override
			public void onDisconnected() {
				// TODO Auto-generated method stub

			}
	               /* Geocoder geocoder = new Geocoder(myContext);
	                try {
	                        List<Address> result = geocoder.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);

	                        TextView textView = (TextView)findViewById(R.id.address);
	                        textView.setText(addressToText(result.get(0)));
	                } catch (IOException e) {
	                        e.printStackTrace();
	                }
	*/

/*
	        private CharSequence addressToText(Address address) {
	                final StringBuilder addressText = new StringBuilder();
	                for (int i = 0, max = address.getMaxAddressLineIndex(); i < max; ++i) {
	                        addressText.append(address.getAddressLine(i));
	                        if ((i+1) < max) {
	                                addressText.append(", ");
	                        }
	                }
	                addressText.append(", ");
	                addressText.append(address.getCountryName());
	                return addressText;
	        }

	        @Override
	        public void onConnectionFailed(ConnectionResult arg0) {
	                Toast.makeText(this, "Connection Failed", Toast.LENGTH_LONG).show();
	        }
	        @Override
	        public void onDisconnected() {
	                Toast.makeText(this, "Disconnected", Toast.LENGTH_LONG).show();
	        }

*/
}






