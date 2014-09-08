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
public class Link implements Parcelable{

	/** splitter*/
	private static final String DELETEONE = "{\"url\":";
	
	/** second delete*/
	private static final String DELETETWO = "\\";
	
	/** empty*/
	private static final String EMPTY = "";
	
	/** begins and ends*/
	private static final String STARTQUOTE = "\"";
	
	/**ends */
	private static final String ENDQUOTE = "\"}";
	
	/** string url*/
	private String myURL;
	
	private Parcel myParcel;
	
	/** parameterized constructor
	 * formats the string to url 
	 * @param the_url
	 */
	public Link(String the_url)
	{
		if (the_url != null)
		{
			String formattedOne = the_url.replace(DELETEONE, EMPTY);
			String formattedTwo = formattedOne!=null?formattedOne.replace(DELETETWO, EMPTY):the_url;
			String formattedThree = formattedTwo;
			if (formattedTwo != null && formattedTwo.startsWith(STARTQUOTE))
			{
				formattedThree = formattedTwo.substring(1);
			}
			if (formattedThree != null && formattedThree.endsWith(ENDQUOTE))
			{
				formattedThree = formattedThree.length()>3?formattedThree.substring(0, formattedThree.length()-2):formattedThree;
			}
			myURL = formattedThree;
		}
	}
	
	public Link(Parcel the_input)
	{
		if (the_input != null)
		{
			myParcel = the_input;
			readURL();
		}
	}
	private void readURL()
	{
		myURL = myParcel.readString();
	}
	/**returns url
	 * @return url
	 */
	public String getURL()
	{
		return myURL;
	}
	
	public boolean equals(Link the_link)
	{
		if (the_link != null)
		{
			return the_link.getURL()!=null?the_link.getURL().equals(myURL):myURL==null;
		}
		return false;
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
			writeURL();
		}
	}
	
	private void writeURL()
	{
		myParcel.writeString(myURL);
	}
	
	public static final Creator<Link> CREATOR = new Creator<Link>() {
        public Link createFromParcel(Parcel in) {
            return new Link(in); 
        }

        public Link[] newArray(int size) {
            return new Link[size];
        }
    };
    
	}
