/**
 * 
 */
package com.bitBusy.goingg.events;

import java.util.Calendar;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import com.bitBusy.goingg.utility.AWSItemNameGenerator;
import com.google.android.gms.maps.model.LatLng;

/**
 * @author SumaHarsha
 *
 */
public class Event implements EventInterface{

	protected String myID;
	
	/** Name*/
	protected String myName;
	/** Category*/
	private EventCategory myCategory;
	
	/** Date*/
	protected Calendar myStartDateTime;
	
	/** end date*/
	protected Calendar myEndDateTime;
	
	/** Location*/
	protected LatLng myCoordinates;
	
	/** Address*/
	protected Address myAddress;
	
	/** Link*/
	private Link myInfoLink;
	
	/** description*/
	private String myDescription;
	
	protected Parcel myParcel;
	
	private Bitmap myImage;
	
	private String myPrice;
	
	protected boolean isGooglePlace;
	
	private String myCreator;
	
	private String myRating;
	
	private int myNumGoing;
	
	private int myNumMaybe;
	
	private int myNumNope;

	public Event(EventCategory the_category, String the_name, String the_description,Address the_address,
			Calendar the_startDateTime, Calendar the_endDateTime, 
			LatLng the_coordinates, Link the_infoLink, Bitmap the_img, String the_price, boolean the_isGooglePlace, String the_creator, String the_rating,
			int the_numGoing, int the_numNope, int the_numMaybe)
	{
		myName = the_name;
		myCategory = the_category;
		myStartDateTime = the_startDateTime;
		myEndDateTime = the_endDateTime;
		myCoordinates = the_coordinates;
		myAddress = the_address;
		myInfoLink = the_infoLink;
		myDescription = the_description;
		myImage = the_img;
		myPrice = the_price;
		isGooglePlace = the_isGooglePlace;
		myCreator = the_creator;
		myRating = the_rating;
		myNumGoing = the_numGoing;
		myNumNope = the_numNope;
		myNumMaybe = the_numMaybe;
		initializeID();
		//	myID = the_id;
	}
	
	
	protected void initializeID()
	{
		myID = AWSItemNameGenerator.generate(this);
	}
	public Event(Parcel the_parcel)
	{
		if (the_parcel != null)
		{
			myParcel = the_parcel;
			readName();
			readDescription();
			readCategory();
			readStartTime();
			readEndTime();
			readLatLng();
			readAddress();
			readInfoLink();
			readImage();
			readPrice();
			readGooglePlaceFlag();
			readCreator();
			readRating();
			readNumGoing();
			readNumNope();
			readNumMaybe();
			readID();
		}
	}
	
	/**
	 * 
	 */
	private void readID() {
		myID = myParcel.readString();
	}

	/**
	 * 
	 */
	private void readNumGoing() 
	{
		myNumGoing = myParcel.readInt();
	}

	private void readNumNope()
	{
		myNumNope = myParcel.readInt();
	}
	private void readNumMaybe()
	{
		myNumMaybe = myParcel.readInt();
	}
	
	/**
	 * 
	 */
	private void readRating() {
		myRating = myParcel.readString();
	}

	/**
	 * 
	 */
	private void readCreator() {
		myCreator = myParcel.readString();
	}

	private void readName()
	{
		myName = myParcel.readString();
	}
	
	private void readDescription()
	{
		myDescription = myParcel.readString();
	}
	private void readCategory()
	{
		myCategory = (EventCategory) myParcel.readSerializable();
	}
	private void readStartTime()
	{
		myStartDateTime = (Calendar) myParcel.readSerializable();
	}
	private void readEndTime()
	{
		myEndDateTime = (Calendar) myParcel.readSerializable();
	}
	private void readLatLng()
	{
		myCoordinates = (LatLng) myParcel.readParcelable(LatLng.class.getClassLoader());
	}
	private void readAddress()
	{
		myAddress = (Address) myParcel.readParcelable(Address.class.getClassLoader());
	}
	private void readInfoLink()
	{
		myInfoLink = (Link) myParcel.readParcelable(Link.class.getClassLoader());
	}
	private void readImage()
	{
		myImage = (Bitmap) myParcel.readParcelable(Bitmap.class.getClassLoader());
	}
	
	private void readPrice()
	{
		myPrice = myParcel.readString();
	}
	
	
	private void readGooglePlaceFlag()
	{
		isGooglePlace = myParcel.readByte()!=0;
	}
	/**
	 * Set name of event
	 * @param the_name
	 */
	public void setName(String the_name)
	{
		myName = the_name;
	}
	
	/** get name of event
	 * @return
	 */
	public String getName()
	{
		return myName;
	}
	
	/** 
	 * set category
	 * @param the_category
	 */
	public void setCategory(EventCategory the_category)
	{
		myCategory = the_category;
	}
	
	/** get category
	 * @return
	 */
	public EventCategory getCategory()
	{
		return myCategory;
	}
	
	/** returns start date
	 * @return the_date
	 */
	public Calendar getStartDateTime()
	{
		return myStartDateTime;
	}
	/**
	 * get date of event
	 * @return date
	 */
	public Calendar getEndDateTime()
	{
		return myEndDateTime;
	}
	
	/**
	 * set coordinates 
	 * @param the_coordinates
	 */
	public void setCoordinates(LatLng the_coordinates)
	{
		myCoordinates = the_coordinates;
	}
	
	/**
	 * get coordinates
	 * @return the coordinates
	 */
	public LatLng getCoordinates()
	{
		return myCoordinates;
	}
	
	/**
	 * set address
	 * @param the_address
	 */
	public void setAddress(Address the_address)
	{
		myAddress = the_address;
	}
	
	/**
	 * get address
	 * @return the address
	 */
	public Address getAddress()
	{
		return myAddress;
	}
	
	/**
	 * set info link
	 * @param the_link
	 */
	public void setInfoLink(Link the_link)
	{
		myInfoLink = the_link;
	}
	
	/**
	 * get info link
	 * @return the link
	 */
	public Link getInfoLink()
	{
		return myInfoLink;
	}
	
	/**
	 * set description
	 * @param the_description
	 */
	public void setInfoLink(String the_description)
	{
		myDescription = the_description;
	}
	
	public void setNumGoing(int the_num)
	{
		myNumGoing = the_num;
	}
	
	public void setNumNope(int the_num)
	{
		myNumNope = the_num;
	}
	
	public void setNumMaybe(int the_num)
	{
		myNumMaybe = the_num;
	}
	public void setImage(Bitmap the_image)
	{
		myImage = the_image;
	}
	/**
	 * get description
	 * @return description
	 */
	public String getDescription()
	{
		return myDescription;
	}
	public String getCreator()
	{
		return myCreator;
	}

	public Bitmap getImage()
	{
		return myImage;
	}
	
	public String getPrice()
	{
		return myPrice;
	}
	
	public boolean isGooglePlace()
	{
		return isGooglePlace;
	}

	public String getRating()
	{
		return myRating;
	}
	
	public String getID()
	{
		return myID;
	}
	
	public int getNumGoing()
	{
		return myNumGoing;
	}
	public int getNumNope()
	{
		return myNumNope;
	}
	public int getNumMaybe()
	{
		return myNumMaybe;
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		myParcel = dest;
		writeName();
		writeDescription();
		writeCategory();
		writeStartTime();
		writeEndTime();
		writeLatLng();
		writeAddress();
		writeInfoLink();
		writeImage();
		writePrice();
		writeIsGooglePlace();
		writeCreator();
		writeRating();
		writeNumGoing();
		writeNumNope();
		writeNumMaybe();
		writeID();
	}
	

	/**
	 * 
	 */
	private void writeID() {
		myParcel.writeString(myID);
	}

	private void writeNumGoing()
	{
		myParcel.writeInt(myNumGoing);
	}
	private void writeNumNope()
	{
		myParcel.writeInt(myNumNope);
	}
	private void writeNumMaybe()
	{
		myParcel.writeInt(myNumMaybe);
	}

	/**
	 * 
	 */
	private void writeRating() {
		myParcel.writeString(myRating);
	}

	/**
	 * 
	 */
	private void writeCreator() {
		myParcel.writeString(myCreator);		
	}

	/**
	 * 
	 */
	private void writeIsGooglePlace() {
		myParcel.writeByte((byte) (isGooglePlace?1:0));
	}

	/**
	 * 
	 */
	private void writeName() {
		myParcel.writeString(myName);
	}
	private void writeDescription()
	{
		myParcel.writeString(myDescription);
	}
	private void writeCategory()
	{
		myParcel.writeSerializable(myCategory);
	}
	private void writeStartTime()
	{
		myParcel.writeSerializable(myStartDateTime);
	}
	private void writeEndTime()
	{
		myParcel.writeSerializable(myEndDateTime);
	}
	private void writeLatLng()
	{
		myParcel.writeParcelable(myCoordinates, 0);
	}
	private void writeAddress()
	{
		myParcel.writeParcelable(myAddress, 0);
	}
	private void writeInfoLink()
	{
		myParcel.writeParcelable(myInfoLink, 0);
	}
	private void writeImage()
	{
		myParcel.writeParcelable(myImage, 0);
	}
	private void writePrice()
	{
		myParcel.writeString(myPrice);
	}
	
	public static final Parcelable.Creator<Event> CREATOR = new Event.Creator<Event>() {
        public Event createFromParcel(Parcel in) {
            return new Event(in); 
        }

        public Event[] newArray(int size) {
            return new Event[size];
        }
    };
    
    
    
    @Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}


    
}
