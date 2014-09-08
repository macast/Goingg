/**
 * 
 */
package com.bitBusy.goingg.webServices.EventsData;

import java.util.ArrayList;

import com.bitBusy.goingg.weather.Forecast;

/**
 * @author SumaHarsha
 *
 */
public interface WeatherForecastRequestor {

	/** method caeed when forecast ready
	 * @param the_forecast
	 */
	public void acceptForecast(ArrayList<Forecast> the_forecast);
}
