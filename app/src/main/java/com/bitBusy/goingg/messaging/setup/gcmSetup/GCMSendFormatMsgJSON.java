/**
 * 
 */
package com.bitBusy.goingg.messaging.setup.gcmSetup;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author SumaHarsha
 *
 */
public class GCMSendFormatMsgJSON{

	public static final String MSGBODY = "message";
	private static final String MSGTITLE = "title";
    private List<String> registration_ids;
    private Map<String,Object> data;
  
    public void addData(String the_title, Object the_msg){
    	if (the_title != null && the_msg != null){
	    	if (data == null){
	    		data = new HashMap<String, Object>();
	    	}
	    	data.put(MSGTITLE, the_title);
	    	data.put(MSGBODY, the_msg);
    	}
    }
    
    public Map<String, Object> getData(){
    	return data;
    }
    
    public void setData(Map<String, Object> the_map){
    	data = the_map;
    }
    
    public List<String> getRegistration_ids(){
    	return registration_ids;
    }
    
    public void setRegistration_ids(List<String> list){
    	registration_ids = list;
    }
}
