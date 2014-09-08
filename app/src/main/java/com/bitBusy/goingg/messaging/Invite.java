/**
 * 
 */
package com.bitBusy.goingg.messaging;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Invite implements Serializable {

	private static final long serialVersionUID = -5855646450334524778L;
	private List<String> myRegistrationIDs;
    private Map<String,String> data;

    public void addRegId(String regId){
        if(myRegistrationIDs == null)
        	myRegistrationIDs = new LinkedList<String>();
        myRegistrationIDs.add(regId);
    }

    public void createData(String title, String message){
        if(data == null)
            data = new HashMap<String,String>();

        data.put("title", title);
        data.put("message", message);
    }
    
    public List<String> getGuestRegistrationIDS()
    {
    	return myRegistrationIDs;
    }
    
    public Map<String, String> getInviteData()
    {
    	return data;
    }
    public void setGuestRegistrationIDs(List<String> the_registrationIDs)
    {
    	myRegistrationIDs = the_registrationIDs;
    }
    public void setInviteData(Map<String, String> the_data)
    {
    	data = the_data;
    }
}