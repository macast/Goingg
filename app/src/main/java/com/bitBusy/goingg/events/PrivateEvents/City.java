/**
 * 
 */
package com.bitBusy.goingg.events.PrivateEvents;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author SumaHarsha
 *
 */
public class City implements Parcelable
{

	protected Parcel myParcel;
	private String myName;
	
	public City(String the_name)
	{
		myName = the_name;
	}
	
	public City(Parcel the_parcel)
	{
		if (the_parcel != null)
		{
			myParcel = the_parcel;
			readCityName();
		}
	}
	
	private void readCityName(){
		if (myParcel != null){
			myName = myParcel.readString();
		}
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		if (dest != null)
		{
			myParcel = dest;
			writeCityName();
		}
	}

	private void writeCityName(){
		myParcel.writeString(myName);
	}
	
	public String getName()
	{
		return myName;
	}
	
	public static final Creator<City> CREATOR = new Creator<City>() {
        public City createFromParcel(Parcel in) {
            return new City(in); 
        }

        public City[] newArray(int size) {
            return new City[size];
        }
    };
  

	/* (non-Javadoc)
	 * @see android.os.Parcelable#describeContents()
	 */
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	}

