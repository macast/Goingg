/**
 * 
 */
package com.bitBusy.goingg.events;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author SumaHarsha
 *
 */
public class Email implements Parcelable{

	/** my id*/
	private String myID;
	
	private Parcel myParcel;
	/**
	 * parameterized constructor
	 * @param the_id
	 */
	public Email(String the_id)
	{
		myID = the_id;
	}
	public Email(Parcel the_input)
	{
		if (the_input != null)
		{
			myParcel = the_input;
			readID();
		}
	}
	private void readID()
	{
		myID = myParcel.readString();
	}
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
	
	private void writeID()
	{
		myParcel.writeString(myID);
	}
	
	public static final Creator<Email> CREATOR = new Creator<Email>() {
        public Email createFromParcel(Parcel in) {
            return new Email(in); 
        }

        public Email[] newArray(int size) {
            return new Email[size];
        }
    };
    
	

	/**
	 * returns email id
	 * @return
	 */
	public String getID()
	{
		return myID;
	}
	/**
	 * indicates if email is valid
	 * @return boolean
	 */
	public boolean isValid()
	{
		return android.util.Patterns.EMAIL_ADDRESS.matcher(myID).matches();
	}
	
	public boolean equals(Email the_email)
	{
		if (the_email != null)
		{
			String id = the_email.getID();
			return id!=null?id.equals(myID):myID==null;
			
		}
		return false;
	}
}
