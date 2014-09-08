/**
 * 
 */
package com.bitBusy.goingg.WIDAuthentication;

/**
 * @author SumaHarsha
 *
 */
public class WIDAccessToken
{
	private String myToken;
	private long myExpiration;
	private String myEmail;
	
	public WIDAccessToken(String the_token, long the_secondsSince1970, String the_email)
	{
		myToken = the_token;
		myExpiration = the_secondsSince1970;
		myEmail = the_email;
	}
	
	public String getToken()
	{
		return myToken;
	}
	
	public long getDate()
	{
		return myExpiration;
	}
	
	public String getEmailID()
	{
		return myEmail;
	}
}
