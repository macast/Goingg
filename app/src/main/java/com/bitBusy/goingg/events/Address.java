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
public class Address implements Parcelable{

	/** data size*/
	private static final int DATASIZE = 5;
			
	/** street address*/
	private String myStreetAddress;
	
	/** venue*/
	private String myCity;
	
	private String myState;
	
	private String myCountry;
	
	private String myPostalCode;
	
	/**
	 * parameterized constructor
	 * @param the_bldngNmRmNo
	 * @param the_streetAdd
	 * @param the_venue
	 */
	public Address(String the_streetAdd, String the_city, String the_state, String the_country, String the_postalcode)
	{
		myStreetAddress = the_streetAdd;
		myCity = the_city;
		myState = the_state;
		myCountry = the_country;
		myPostalCode = the_postalcode;
	}
	
	public Address(Parcel the_input)
	{
		String[] data = new String[DATASIZE];
		the_input.readStringArray(data);
		myStreetAddress = data[0];
		myCity = data[1];
		myState = data[2];
		myCountry = data[3];
		myPostalCode = data[4];
	}
	
	
	public String getState()
	{
		return myState;
	}
	/**
	 * gets street address
	 * @return street address
	 */
	public String getStreetAdd()
	{
		return myStreetAddress;
	}
	
	
	public String getCity()
	{
		return myCity;
	}
	public String getCountry()
	{
		return myCountry;
	}
	public String getPostalCode()
	{
		return myPostalCode;
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
		dest.writeStringArray(new String[]{myStreetAddress, myCity, myState, myCountry, myPostalCode });
		}
	  public static final Creator<Address> CREATOR = new Creator<Address>() {
          public Address createFromParcel(Parcel in) {
              return new Address(in); 
          }

          public Address[] newArray(int size) {
              return new Address[size];
          }
      };
      
      
      /**
       * compares two objects
       * @param the_address
       * @return
       */
      @Override
      public boolean equals(Object the_addressobj)
      {
    	  if (the_addressobj != null && the_addressobj instanceof Address)
    	  {
    		  Address the_address = (Address) the_addressobj;
    		  String streetAdd = the_address.getStreetAdd();
    		  String city = the_address.getCity();
    		  String state = the_address.getState();
    		  String country = the_address.getCountry();
    		  String postalCode = the_address.getPostalCode();
        	 
    		  return ((streetAdd!=null?streetAdd.equals(myStreetAddress):myStreetAddress==null) &&
    				  (city!=null?city.equals(myCity):myCity==null) &&
    				  (state!=null?state.equals(myState):myState==null) &&
    				  (country != null?country.equals(myCountry):myCountry==null) &&
    				  (postalCode!=null?postalCode.equals(myPostalCode):myPostalCode==null));
    	  }
    	  return false;
      }
      
      
      @Override
      public String toString()
      {
    	  StringBuilder returnAdd = new StringBuilder();
    	  returnAdd.append(myStreetAddress != null&&myStreetAddress !=""?myStreetAddress:"")
    	  .append(myCity!=null && myCity!=""?", "+myCity:"").append(myState != null&& myState!=""?", "+myState:"").append( 
    	   myCountry != null&&myCountry!=""?" " +myCountry+" ":"").append(myPostalCode != null?myPostalCode:"");
 	  return returnAdd.toString();
    	
      }
      
      @Override
      public int hashCode()
      {
    	  int retHashcode = 0;
    	  
  		if (myStreetAddress != null)
  		{
  			char[] street = myStreetAddress.toCharArray();
  			int len = myStreetAddress.length();
  			for (int i = 0; i < len; i++)
  			{
  				retHashcode += Math.pow(street[i]*31, len-i+1);
  			}
  		}	
      
      if (myCity != null)
      {
		  char[] city = myCity.toCharArray();
    	  int len = myCity.length();
			for (int i = 0; i < len; i++)
			{
				retHashcode += Math.pow(city[i]*31, len-i+1);
			}
		}
      if (myState != null)
      {
		  char[] state = myState.toCharArray();
    	  int len = myState.length();
			for (int i = 0; i < len; i++)
			{
				retHashcode += Math.pow(state[i]*31, len-i+1);
			}
		}
      if (myCountry != null)
      {
		  char[] country = myCountry.toCharArray();
    	  int len = myCountry.length();
			for (int i = 0; i < len; i++)
			{
				retHashcode += Math.pow(country[i]*31, len-i+1);
			}
		}
      if (myPostalCode != null)
      {
		  char[] postalcode = myPostalCode.toCharArray();
    	  int len = myPostalCode.length();
			for (int i = 0; i < len; i++)
			{
				retHashcode += Math.pow(postalcode[i]*31, len-i+1);
			}
		}
		return retHashcode;

      }
      
}
