/**
 * 
 */
package com.bitBusy.goingg.messaging.setup.gcmSetup;

import java.io.Serializable;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author SumaHarsha
 *
 */
public class GCMID implements Parcelable, Serializable{
	
	private static final long serialVersionUID = 1L;
	private String myID;
	private Parcel myParcel;
	
	public GCMID(String the_id){
		myID = the_id;
	}
	
	public GCMID(Parcel the_parcel)
	{
		if (the_parcel != null)
		{
			myParcel = the_parcel;
			readID();
		}
	}
	
	private void readID(){
		myID = myParcel.readString();
	}
	
	/* (non-Javadoc)
	 * @see android.os.Parcelable#describeContents()
	 */
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	/* (non-Javadoc)
	 * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)
	 */
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		if (dest != null)
		{
			myParcel = dest;
			writeID();
		}
	}
	
	public void writeID(){
		myParcel.writeString(myID);
	}
	
	public static final Creator<GCMID> CREATOR = new Creator<GCMID>() {
        public GCMID createFromParcel(Parcel in) {
            return new GCMID(in); 
        }

        public GCMID[] newArray(int size) {
            return new GCMID[size];
        }
    };
	public String getGCMID(){
		return myID;
	}
	
	@Override
	public boolean equals(Object the_obj){
		if (the_obj != null && the_obj instanceof GCMID){
			GCMID obj = (GCMID) the_obj;
			if (obj != null){
				return (obj.getGCMID() != null? obj.getGCMID().equals(myID):myID==null);
			}
		}
		return false;
	}
	
	@Override
	public int hashCode(){
		int retHashcode = 0;
		if (myID != null)
		{
			int len = myID.length();
			char[] name = myID.toCharArray();
			for (int i = 0; i < len; i++)
			{
				retHashcode += Math.pow(name[i]*31, len-i+1);
			}
		}
		return retHashcode;
	}
}

