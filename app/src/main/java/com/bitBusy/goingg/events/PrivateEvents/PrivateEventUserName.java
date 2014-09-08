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
public class PrivateEventUserName implements Parcelable
{
	
	protected Parcel myParcel;
	private String myFirstName;
	private String myLastName;
	
	public PrivateEventUserName(String the_firstname, String the_lastname)
	{
		myFirstName = the_firstname;
		myLastName = the_lastname;
	}
	public PrivateEventUserName(Parcel the_parcel)
	{
		if (the_parcel != null)
		{
			myParcel = the_parcel;
			readFirstName();
			readLastName();
		}
	}
	
	private void readFirstName(){
		if (myParcel != null){
			myFirstName = myParcel.readString();
		}
	}
	
	private void readLastName(){
		if (myParcel != null){
			myLastName = myParcel.readString();
		}
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		if (dest != null)
		{
			myParcel = dest;
			writeFirstName();
			writeLastName();
		}
	}

	private void writeFirstName(){
		myParcel.writeString(myFirstName);
	}
	
	private void writeLastName(){
		myParcel.writeString(myLastName);
	}
	
	public String getFirstName()
	{
		return myFirstName;
	}
	
	public String getLastName()
	{
		return myLastName;
	}
	
	@Override
	public String toString()
	{
		return (myFirstName!=null?myFirstName:"") + " " + (myLastName!=null?myLastName:"");
	}
	
	public static final Creator<PrivateEventUserName> CREATOR = new Creator<PrivateEventUserName>() {
        public PrivateEventUserName createFromParcel(Parcel in) {
            return new PrivateEventUserName(in); 
        }

        public PrivateEventUserName[] newArray(int size) {
            return new PrivateEventUserName[size];
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