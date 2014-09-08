/**
 * 
 */
package com.bitBusy.goingg.events.PrivateEvents;

import java.util.ArrayList;
import java.util.HashSet;

import android.os.Parcel;
import android.os.Parcelable;

import com.bitBusy.goingg.messaging.setup.gcmSetup.GCMID;

/**
 * @author SumaHarsha
 *
 */
public class PrivateEventUser implements Parcelable{

	protected Parcel myParcel;
	private String myUserName;
	private PrivateEventUserName myName;
	private City myCity;
	private HashSet<GCMID> myGCMIDs;
	private HashSet<String> myRelevantFriendsIDs;
	
	public PrivateEventUser(String the_username, PrivateEventUserName the_name, City the_city, HashSet<GCMID> the_ids, HashSet<String> the_friendIDs)
	{
		myUserName = the_username;
		myName = the_name;
		myCity = the_city;
		myGCMIDs = the_ids;
		myRelevantFriendsIDs = the_friendIDs;
	}
	
	public PrivateEventUser(Parcel the_parcel)
	{
		if (the_parcel != null)
		{
			myParcel = the_parcel;
			readUserName();
			readName();
			readCity();
			readGCMIDs();
			readFriendIDs();
		}
	}
	
	private void readFriendIDs(){
		ArrayList<String> array = new ArrayList<String>();
		myParcel.readStringList(array);
		if (myRelevantFriendsIDs == null){
			myRelevantFriendsIDs = new HashSet<String>();
		}
		myRelevantFriendsIDs.addAll(array);
	}
	
	private void readGCMIDs(){
		ArrayList<GCMID> array = new ArrayList<GCMID>();
		 myParcel.readTypedList(array, GCMID.CREATOR);
		 if (myGCMIDs == null){
			 myGCMIDs = new HashSet<GCMID>();
		 }
		 myGCMIDs.addAll(array);
	}
	
	private void readUserName() {
		myUserName = myParcel.readString();
	}
	
	private void readName(){
		myName = myParcel.readParcelable(PrivateEventUserName.class.getClassLoader());
	}

	private void readCity(){
		myCity = myParcel.readParcelable(City.class.getClassLoader());
	}

	public String getUserName()
	{
		return myUserName;
	}
	
	public PrivateEventUserName getName(){
		return myName;
	}
	
	public City getCity(){
		return myCity;
	}
	
	public HashSet<GCMID> getGCMIDs(){
		return myGCMIDs;
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
			writeUserName();
			writeName(flags);
			writeCity(flags);
			writeGCMIDs(flags);
			writeFriendsIDs(flags);
		}
	}
	
	private void writeFriendsIDs(int flags){
		ArrayList<String> list = new ArrayList<String>();
		list.addAll(myRelevantFriendsIDs);
		myParcel.writeStringList(list);
	}
	
	private void writeGCMIDs(int flags){
		ArrayList<GCMID> list = new ArrayList<GCMID>();
		list.addAll(myGCMIDs);
		myParcel.writeTypedList(list);
	}

	private void writeCity(int flags){
		myParcel.writeParcelable(myCity, flags);
	}
	
	private void writeName(int flags){
		myParcel.writeParcelable(myName, flags);
	}
	
	private void writeUserName() {
		myParcel.writeString(myUserName);
	}
	
	@Override
	public boolean equals(Object the_obj)
	{
		PrivateEventUser user = (PrivateEventUser) the_obj;
		if (user != null)
		{
			String id = user.getUserName();
			return id!=null?id.equals(myUserName):myUserName==null;
		}
		return false;
	}
	
	@Override
	public int hashCode(){
		int useridLen = myUserName!=null?myUserName.length():0;
		int retHashcode = 0;
		if (myUserName != null){
			char[] name = myUserName.toCharArray();
			for (int i = 0; i < useridLen; i++){
				retHashcode += Math.pow(name[i]*31, useridLen-i+1);
			}
		}
		return retHashcode;
	}
	
	public HashSet<String> getFriendsIDs(){
		return myRelevantFriendsIDs;
	}
	
	public void addFriendID(String the_friend){
		if (the_friend != null){
			if (myRelevantFriendsIDs == null){
				myRelevantFriendsIDs = new HashSet<String>();
			}
			myRelevantFriendsIDs.add(the_friend);
		}
	}
		
	
	
	public static final Creator<PrivateEventUser> CREATOR = new Creator<PrivateEventUser>() {
        public PrivateEventUser createFromParcel(Parcel in) {
            return new PrivateEventUser(in); 
        }

        public PrivateEventUser[] newArray(int size) {
            return new PrivateEventUser[size];
        }
    };
    
    @Override
    public String toString(){
    	return myUserName;
    }
  }
