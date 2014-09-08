/**
 * 
 */
package com.bitBusy.goingg.weather;

import android.graphics.Bitmap;

/**
 * @author SumaHarsha
 *
 */
public class Forecast {

	/** max temp*/
	private String myMaxTemp;
	
	/** min temp*/
	private String myMinTemp;
	
	/** weather desc*/
	private String myWeatherDesc;
	
	/** weather image*/
	private Bitmap myWeatherImg;
	
	/** date*/
	private String myDate;
	
	/** parameterized constructor
	 * @param string
	 * @param string2
	 * @param the_weatherDesc
	 * @param the_weatherImgURL
	 */
	public Forecast(String the_date, String the_maxTemp, String the_minTemp, String the_weatherDesc, Bitmap the_weatherImg)
	{
		myDate = the_date;
		myMaxTemp = the_maxTemp;
		myMinTemp = the_minTemp;
		myWeatherDesc = the_weatherDesc;
		myWeatherImg = the_weatherImg;
	}
	
	public String getDate()
	{
		return myDate;
	}
	public String getMaxTemp()
	{
		return myMaxTemp;
	}
	
	public String getMinTemp()
	{
		return myMinTemp;
	}
	
	public String getWeatherDesc()
	{
		return myWeatherDesc;
	}
	public Bitmap getImg()
	{
		return myWeatherImg;
	}
}
