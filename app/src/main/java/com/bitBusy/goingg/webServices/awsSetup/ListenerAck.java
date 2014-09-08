/**
 * 
 */
package com.bitBusy.goingg.webServices.awsSetup;

/**
 * @author SumaHarsha
 *
 */
public class ListenerAck {

	private String myClassName;
	private Object[] myParams;
	
	public ListenerAck(String the_className, Object[] the_params){
		myClassName = the_className;
		myParams = the_params;
	}
	
	public String getClassName(){
		return myClassName;
	}
	
	public Object[] getParams(){
		return myParams;
		}
}
