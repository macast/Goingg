/**
 * 
 */
package com.bitBusy.goingg.events;


import com.bitBusy.goingg.events.DataSources.Categories;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

/**
 * @author SumaHarsha
 *
 */

public enum EventCategory {
	
	CATEGORYACADEMIC(Categories.Academic, BitmapDescriptorFactory.HUE_CYAN),
	CATEGORYBUSINESS(Categories.Business, BitmapDescriptorFactory.HUE_AZURE),
	CATEGORYCOMMUNITY(Categories.Community, BitmapDescriptorFactory.HUE_BLUE),
	CATEGORYENTERTAINMENT(Categories.Entertainment, BitmapDescriptorFactory.HUE_RED),
	CATEGORYFAMILY(Categories.Family, BitmapDescriptorFactory.HUE_ROSE),
	CATEGORYMOVIES(Categories.Movies, BitmapDescriptorFactory.HUE_GREEN),
	CATEGORYPETS(Categories.Pets, BitmapDescriptorFactory.HUE_VIOLET),
	CATEGORYRECREATION(Categories.Recreation, BitmapDescriptorFactory.HUE_MAGENTA),
	CATEGORYSPORTS(Categories.Sports, BitmapDescriptorFactory.HUE_ORANGE), 
	CATEGORYOTHER(Categories.Other, BitmapDescriptorFactory.HUE_YELLOW);
	
	
	

	/** name of event*/
	private Categories myCategory;
	
	/** color of event*/
	private float myColor;
	
	/** uri*/
//	private HashMap<String, String[]> myURIs;
	
	
	/**
	 * parameterized constructor
	 * @param the_name of event
	 * @param the_color of event
	 * @param the_uri
	 */
	private EventCategory(Categories the_name, float the_color)
	//, HashMap<String, String[]> the_uri)
	{
		myCategory = the_name;
		myColor = the_color;		
//		myURIs = the_uri;
	}
	
	/**
	 * gets name of event
	 * @return the name
	 */
	public Categories getName()
	{
		return myCategory;
	}
	
	/**
	 * gets color of event
	 * @return the color
	 */
	public float getColor()
	{
		return myColor;
	}
	
	
	public static EventCategory getCategory(String the_value)
	{
		for(EventCategory category: EventCategory.values())
		{
			if (category.getName()!= null && category.getName().name().equals(the_value))
			{
				return category;
			}
		}
		return CATEGORYOTHER;
	}
}

