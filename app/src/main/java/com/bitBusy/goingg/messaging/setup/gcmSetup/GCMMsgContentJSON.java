/**
 * 
 */
package com.bitBusy.goingg.messaging.setup.gcmSetup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import com.bitBusy.goingg.events.PrivateEvents.City;
import com.bitBusy.goingg.events.PrivateEvents.PrivateEventUser;
import com.bitBusy.goingg.events.PrivateEvents.PrivateEventUserName;
import com.bitBusy.goingg.messaging.setup.MessageContentInterface;

/**
 * @author SumaHarsha
 *
 */
public class GCMMsgContentJSON implements MessageContentInterface
{
	public static final String MSGTYPE = "msgType";
	public static final String MSGTEXT = "msgText";
	private static final String SUSERNAME = "suserName";
	public static final String NAME = "name";
	private static final String SFNAME = "sfirstName";
	private static final String SLNAME = "slastName";
	private static final String SCITY = "scity";
	private static final String SGCMIDS = "sgcmids";
	private static final String RUSERNAME = "ruserName";
	private static final String RFNAME = "rfirstName";
	private static final String RLNAME = "rlastName";
	private static final String RCITY = "rcity";
	private static final String RGCMIDS = "rgcmids";
	public static final String GCMID = "gcmid";
	public static final String MSGDATETIME = "msgDateTime";
	private HashMap<String, Object> myMap;
		
	private void checkMapInitialization(){
		if (myMap == null){
			myMap = new HashMap<String, Object>();
		}
	}
	public void setMsgType(String the_msgType){
		if (the_msgType != null){
			checkMapInitialization();
			myMap.put(MSGTYPE, the_msgType);
		}
	}
	
	public String getMsgType(){
		if (myMap != null){
			return (String) myMap.get(MSGTYPE);
		}
		return null;
	}
	
	public void setMsgDateTime(String the_msgDateTime){
		if (the_msgDateTime != null){
			checkMapInitialization();
			myMap.put(MSGDATETIME, the_msgDateTime);
		}
	}
	
	public String getMsgDateTime(){
		if (myMap != null){
			return (String) myMap.get(MSGDATETIME);
		}
		return null;
	}
	
	public void setSender(PrivateEventUser the_user){
		if (the_user != null){
			checkMapInitialization();
			myMap.put(SUSERNAME, the_user.getUserName());
			myMap.put(SFNAME, the_user.getName() != null? the_user.getName().getFirstName():null);
			myMap.put(SLNAME, (the_user.getName() != null? the_user.getName().getLastName():null));
			myMap.put(SCITY, (the_user.getCity() != null? the_user.getCity().getName():null));
			myMap.put(SGCMIDS, makeGCMIDArray(the_user.getGCMIDs()));
		}
		
	}
	
	public PrivateEventUser getSender(){
		if (myMap != null){
			return new PrivateEventUser((String)myMap.get(SUSERNAME),
					new PrivateEventUserName((String)myMap.get(SFNAME),(String) myMap.get(SLNAME)), 
					new City((String)myMap.get(SCITY)), makeGCMIDHashSet(myMap.get(SGCMIDS)), null);
		}
		return null;
	}
	
	public void setReceiver(PrivateEventUser the_user){
		if (the_user != null){
			checkMapInitialization();
			myMap.put(RUSERNAME, the_user.getUserName());
			myMap.put(RFNAME, the_user.getName() != null? the_user.getName().getFirstName():null);
			myMap.put(RLNAME, (the_user.getName() != null? the_user.getName().getLastName():null));
			myMap.put(RCITY, (the_user.getCity() != null? the_user.getCity().getName():null));
			myMap.put(RGCMIDS, makeGCMIDArray(the_user.getGCMIDs()));
		}
		
	}
	
	private ArrayList<String> makeGCMIDArray(HashSet<GCMID> the_ids) {
		if (the_ids != null){
			ArrayList<String> list = new ArrayList<String>();
			for (GCMID id: the_ids){
				if (id != null){
					list.add(id.getGCMID());
				}
			}
			return list;
		}
		return null;
	}
	
	public PrivateEventUser getReceiver(){
		if (myMap != null){
			return new PrivateEventUser((String)myMap.get(RUSERNAME),
					new PrivateEventUserName((String)myMap.get(RFNAME),(String) myMap.get(RLNAME)), 
					new City((String)myMap.get(RCITY)), makeGCMIDHashSet(myMap.get(RGCMIDS)), null);
		}
		return null;
	}
	
	private HashSet<GCMID> makeGCMIDHashSet(Object the_obj){
		if (the_obj != null && the_obj instanceof ArrayList<?>){
			HashSet<GCMID> ids = new HashSet<GCMID>();
			for (Object obj: (ArrayList<?>) the_obj){
				if (obj instanceof String){
					ids.add(new GCMID((String) obj));
				}
			}
			return ids;
		}
		return null;
	}
	
	public void setMsgText(Object the_msgText){
		checkMapInitialization();
		myMap.put(MSGTEXT, the_msgText);
	}
	
	public Object getMsgText(){
		if (myMap != null){
			return myMap.get(MSGTEXT);
		}
		return null;
	}
}
