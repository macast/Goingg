/**
 * 
 */
package com.bitBusy.goingg.mapDisplay;

import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.text.Html;

import com.bitBusy.goingg.events.Link;
import com.google.android.gms.maps.model.LatLng;

/**
 * @author SumaHarsha
 *
 */
public class MapDirectionsUtil {
	
	private static final String HTMLTAG = "html_instructions";
	
	private static final String TRANSITDETSTAG = "transit_details";
		
	private static final String DEPARTS = " -Departs from ";
	
	private static final String ARRIVES = " -Arrives at ";
	
	private static final String NUMSTOPS = " -Number of stops: ";
	
	private static final String AT = " at ";
	
	private static final String NAMETAG = "name";
	
	private static final String TEXTTAG = "text";
	
	private static final String ARRIVALSTOPTAG = "arrival_stop";
	
	private static final String DEPARTURESTOPTAG = "departure_stop";
	
	private static final String ARRIVALTIMETAG = "arrival_time";
	
	private static final String DEPARTURETIMETAG = "departure_time";
	
	private static final String NUMSTOPSTAG = "num_stops";
	
	private static final String TRANSITAGENCYTAG = "agency";
	
	private static final String TRANSITAGENCYLINETAG = "line";
	
	private static final String TRANSITVEHICLETAG = "vehicle";
	
	private static final String TRANSITAGENCYSHORTNAMETAG = "short_name";
	
	private static final String URLTAG = "url";
	
	private static final String LOCATIONTAG = "location";
	
	private static final String LATTAG = "lat";
	
	private static final String LNGTAG = "lng";
	
	private static final String PHONETAG = "phone";
	
	private static final String WARNINGTAG = "warning";
	
	private static final String COPYRIGHTSTAG = "copyrights";
	
	
	public static String getDurationText (Document doc) {
        NodeList nl1 = doc.getElementsByTagName("duration");
        if (nl1.getLength() > 0)
        {
        	Node node1 = nl1.item(nl1.getLength() - 1);
	        NodeList nl2 = node1.getChildNodes();
	        Node node2 = nl2.item(getNodeIndex(nl2, "text"));
	        return node2.getTextContent();
	    }
        return "could not retrieve";
	}

    public static int getDurationValue (Document doc) {
        NodeList nl1 = doc.getElementsByTagName("duration");
        Node node1 = nl1.item(nl1.getLength() - 1);
        NodeList nl2 = node1.getChildNodes();
        Node node2 = nl2.item(getNodeIndex(nl2, "value"));
        return Integer.parseInt(node2.getTextContent());
    }

    public static String getDistanceText (Document doc) {
        NodeList nl1 = doc.getElementsByTagName("distance");
        Node node1 = nl1.item(nl1.getLength() - 1);
        NodeList nl2 = node1.getChildNodes();
        Node node2 = nl2.item(getNodeIndex(nl2, "text"));
        return node2.getTextContent();
    }

    public static int getDistanceValue (Document doc) {
        NodeList nl1 = doc.getElementsByTagName("distance");
       if (nl1.getLength() > 0)
       {
        Node node1 = nl1.item(nl1.getLength() - 1);
        NodeList nl2 = node1.getChildNodes();
        Node node2 = nl2.item(getNodeIndex(nl2, "value"));
        return Integer.parseInt(node2.getTextContent());
       }
       return 0;
    }

    public static String getStartAddress (Document doc) {
        NodeList nl1 = doc.getElementsByTagName("start_address");
        Node node1 = nl1.item(0);
        if (node1 != null)
        {
        	return node1.getTextContent();
        }
        return "no results!";
    }

    public static String getEndAddress (Document doc) {
        NodeList nl1 = doc.getElementsByTagName("end_address");
        Node node1 = nl1.item(0);
        return node1.getTextContent();
    }

    public static String getCopyRights (Document doc) {
        NodeList nl1 = doc.getElementsByTagName("copyrights");
        Node node1 = nl1.item(0);
        return node1.getTextContent();
    }

    public static ArrayList<LatLng> getDirection (Document doc) {
        ArrayList<LatLng> listGeopoints = new ArrayList<LatLng>();

    	if (doc != null)
    	{
	        NodeList nl1, nl2, nl3;
	        nl1 = doc.getElementsByTagName("step");
	        if (nl1.getLength() > 0) {
	            for (int i = 0; i < nl1.getLength(); i++) {
	                Node node1 = nl1.item(i);
	                nl2 = node1.getChildNodes();
	
	                Node locationNode = nl2.item(getNodeIndex(nl2, "start_location"));
	                nl3 = locationNode.getChildNodes();
	                Node latNode = nl3.item(getNodeIndex(nl3, "lat"));
	                double lat = Double.parseDouble(latNode.getTextContent());
	                Node lngNode = nl3.item(getNodeIndex(nl3, "lng"));
	                double lng = Double.parseDouble(lngNode.getTextContent());
	                listGeopoints.add(new LatLng(lat, lng));
	
	                locationNode = nl2.item(getNodeIndex(nl2, "polyline"));
	                nl3 = locationNode.getChildNodes();
	                latNode = nl3.item(getNodeIndex(nl3, "points"));
	                ArrayList<LatLng> arr = decodePoly(latNode.getTextContent());
	                for(int j = 0 ; j < arr.size() ; j++) {
	                    listGeopoints.add(new LatLng(arr.get(j).latitude, arr.get(j).longitude));
	                }
	
	                locationNode = nl2.item(getNodeIndex(nl2, "end_location"));
	                nl3 = locationNode.getChildNodes();
	                latNode = nl3.item(getNodeIndex(nl3, "lat"));
	                lat = Double.parseDouble(latNode.getTextContent());
	                lngNode = nl3.item(getNodeIndex(nl3, "lng")); 
	                lng = Double.parseDouble(lngNode.getTextContent());
	                listGeopoints.add(new LatLng(lat, lng));
	            }
	        }
    	}
        return listGeopoints;
    }

    private static int getNodeIndex(NodeList nl, String nodename) {
        for(int i = 0 ; i < nl.getLength() ; i++) {
            if(nl.item(i).getNodeName().equals(nodename))
                return i;
        }
        return -1;
    }

    private static ArrayList<LatLng> decodePoly(String encoded) {
        ArrayList<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;
        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;
            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng position = new LatLng((double) lat / 1E5, (double) lng / 1E5);
            poly.add(position);
        }
        return poly;
    }
    
    
    /**
     * gets departure stops
     * @param the_doc
     * @return
     */
    public static ArrayList<TransitStopDetails> getDepartureStops(Document the_doc)
    {
    	ArrayList<TransitStopDetails> stops = new ArrayList<TransitStopDetails>();
    	NodeList stepNodeList, transitDetailsList, departureStopList, departureStopNameList,
    	vehicleList, vehicleNameList, departureTimeList,departureStopTimeTextList, lineList,numStopsList,lineNameList, 
    	departureLatLngList, latList, lngList, agencyList, agencyNameList, agencyURLList, urlList, phoneList;
    	
    	stepNodeList = the_doc.getElementsByTagName("step");
    	LatLng latLngLoc = null;
    	String departureStop = null;
    	String arrivalStopName = null;
		String vehicleDets = null;
		String departureTime = null;
		String arrivalTime = null;
		String numStops = null;
		Link url = null, agencyURL = null;
		String tempURL = null, agencyName = null, phoneNum = null;

    	Node prevStepNode = null;
    	  for (int i = 0; i < stepNodeList.getLength(); i++) 
          {
              Node node = stepNodeList.item(i);
              if (node.getNodeType() == Node.ELEMENT_NODE && !node.getParentNode().equals(prevStepNode))
              {
              	prevStepNode = node;
              	Element element = (Element) node;
              	transitDetailsList = element.getElementsByTagName(TRANSITDETSTAG);
            	if (transitDetailsList != null && transitDetailsList.getLength() >0 && transitDetailsList.item(0) != null)
            	{
            		Node transitNode = transitDetailsList.item(0);
       				if (transitNode.getNodeType() == Node.ELEMENT_NODE)
       				{
       					Element transitElement = (Element) transitNode;
       					lineList = transitElement.getElementsByTagName(TRANSITAGENCYLINETAG);
        				vehicleList = transitElement.getElementsByTagName(TRANSITVEHICLETAG);
        				departureStopList = transitElement.getElementsByTagName(DEPARTURESTOPTAG);
            			departureTimeList = transitElement.getElementsByTagName(DEPARTURETIMETAG);
            			numStopsList = transitElement.getElementsByTagName(NUMSTOPSTAG);
            			agencyList = transitElement.getElementsByTagName(TRANSITAGENCYTAG);
            			urlList = transitElement.getElementsByTagName(URLTAG);
        				if (vehicleList != null && vehicleList.getLength() >0 && vehicleList.item(0) != null)
        				{
        					Node vehicleNode = vehicleList.item(0);
        					if (vehicleNode.getNodeType() == Node.ELEMENT_NODE)
    						{
    							Element vehicleElement = (Element) vehicleNode;
    							vehicleNameList = vehicleElement.getElementsByTagName(NAMETAG);
    							if (vehicleNameList != null && vehicleNameList.getLength() > 0 && vehicleNameList.item(0) != null)
   								{
    								vehicleDets = vehicleNameList.item(0).getTextContent();       					
   								}
    						}
        				}
        					if (lineList != null && lineList.getLength() > 0 && lineList.item(0) != null)
        				{
            					Node lineNode = lineList.item(0);
            					if (lineNode.getNodeType() == Node.ELEMENT_NODE)
            					{
            						Element lineElement = (Element) lineNode;
       								lineNameList = lineElement.getElementsByTagName(TRANSITAGENCYSHORTNAMETAG);

       								if (lineNameList != null && lineNameList.getLength() >0 && lineNameList.item(0) != null)
       								{
       									vehicleDets = 
       											vehicleDets.concat(" " + lineNameList.item(0).getTextContent());
       								}
       							
       								
       							}
            			}
        				double lat = 0,lng = 0;
        				if (departureStopList != null && departureStopList.getLength() > 0 && departureStopList.item(0) != null)
        				{
        					Node departureNode = departureStopList.item(0);
        					if (departureNode.getNodeType() == Node.ELEMENT_NODE)
        					{
        						Element departureElement = (Element) departureNode;
        						departureStopNameList = departureElement.getElementsByTagName(NAMETAG);
        						if (departureStopNameList != null && departureStopNameList.getLength() >= 0 && departureStopNameList.item(0) != null)
        						{
        							departureStop = departureStopNameList.item(0).getTextContent();
        						departureLatLngList = departureElement.getElementsByTagName(LOCATIONTAG);
        						if (departureLatLngList != null && departureLatLngList.getLength() > 0 && departureLatLngList.item(0) != null)
                				{
        							Node departureLatLngNode = departureLatLngList.item(0);
        							if (departureLatLngNode.getNodeType() == Node.ELEMENT_NODE)
                					{
                						Element depLatLngEle = (Element) departureLatLngNode;
                						latList = depLatLngEle.getElementsByTagName(LATTAG);
                						lngList = depLatLngEle.getElementsByTagName(LNGTAG);
                						if (latList != null && latList.getLength() > 0 && latList.item(0) != null)
                        				{
                							lat = Double.parseDouble(latList.item(0).getTextContent());
                        				}
                						if (lngList != null && lngList.getLength() > 0 && lngList.item(0) != null)
                        				{
                							lng = Double.parseDouble(lngList.item(0).getTextContent());
                        				}
                						latLngLoc = new LatLng(lat,lng);
                					}
                				}
        					}              					
        					}
        				}
        				if (departureTimeList != null && departureTimeList.getLength() > 0 && departureTimeList.item(0) != null)
        				{
        					Node departureNode = departureTimeList.item(0);
        					if (departureNode.getNodeType() == Node.ELEMENT_NODE)
        					{
        						Element departureElement = (Element) departureNode;
        						departureStopTimeTextList = departureElement.getElementsByTagName(TEXTTAG);
        						if (departureStopTimeTextList != null && departureStopTimeTextList.getLength() >= 0 && departureStopTimeTextList.item(0) != null)
        							departureTime = departureStopTimeTextList.item(0).getTextContent();
        					}
        				}
        				if (numStopsList != null && numStopsList.getLength() > 0 && numStopsList.item(0) != null)
        				{
        							numStops =   numStopsList .item(0).getTextContent();	
        				}
        				if (agencyList != null && agencyList.getLength() > 0 && agencyList.item(0) != null)
        				{
        						Node agencyNode = agencyList.item(0);
        						if (agencyNode.getNodeType() == Node.ELEMENT_NODE)
        						{
        							Element agencyElement = (Element) agencyNode;
        							agencyNameList = agencyElement.getElementsByTagName(NAMETAG);
        							if (agencyNameList != null && agencyNameList.getLength() > 0 && agencyNameList.item(0) != null)
        							{
        								agencyName = agencyNameList.item(0).getTextContent();
        							}
        							agencyURLList = agencyElement.getElementsByTagName(URLTAG);
        							if (agencyURLList != null && agencyURLList.getLength() > 0 && agencyURLList.item(0) != null)
        							{
        								tempURL = agencyURLList.item(0).getTextContent();
        								agencyURL = new Link(tempURL);
        							}
        							phoneList = agencyElement.getElementsByTagName(PHONETAG);
        							if (phoneList != null && phoneList.getLength() > 0 && phoneList.item(0) != null)
        							{
        								phoneNum = phoneList.item(0).getTextContent();

        							}
        							
        						}
        				}
        				if (urlList != null && urlList.getLength() > 0 && urlList.item(0) != null)
        				{
        							tempURL  =   urlList.item(0).getTextContent();
        							url = new Link(tempURL);
        				}
       				}
       					TransitStopDetails details = new TransitStopDetails(latLngLoc, departureStop!=null?departureStop:arrivalStopName,
       						vehicleDets, url, departureTime != null?departureTime:arrivalTime, numStops, agencyName, agencyURL, phoneNum);
       					stops.add(details);
            	}
              }
          }
            	return stops;
    }
    
    
    /** gets arrival stops
     * @param the_doc
     * @return
     */
    public static ArrayList<TransitStopDetails> getArrivalStops(Document the_doc)
    {
    	ArrayList<TransitStopDetails> stops = new ArrayList<TransitStopDetails>();
    	NodeList stepNodeList, transitDetailsList, arrivalStopList, arrivalStopNameList, vehicleList, vehicleNameList, arrivalTimeList, 
    	arrivalStopTimeTextList,lineList,numStopsList,lineNameList, arrivalLatLngList, latList, lngList, agencyList, agencyNameList, agencyURLList, urlList, phoneList;
    	stepNodeList = the_doc.getElementsByTagName("step");
    	LatLng latLngLoc = null;
    	String departureStop = null;
    	String arrivalStopName = null;
		String vehicleDets = null;
		String departureTime = null;
		String arrivalTime = null;
		String numStops = null;
		Link url = null, agencyURL = null;
		String tempURL = null, agencyName = null, phoneNum = null;

    	Node prevStepNode = null;
    	  for (int i = 0; i < stepNodeList.getLength(); i++) 
          {
              Node node = stepNodeList.item(i);
              if (node.getNodeType() == Node.ELEMENT_NODE && !node.getParentNode().equals(prevStepNode))
              {
              	prevStepNode = node;
              	Element element = (Element) node;
              	transitDetailsList = element.getElementsByTagName(TRANSITDETSTAG);
            	if (transitDetailsList != null && transitDetailsList.getLength() >0 && transitDetailsList.item(0) != null)
            	{
            		Node transitNode = transitDetailsList.item(0);
       				if (transitNode.getNodeType() == Node.ELEMENT_NODE)
       				{
       					Element transitElement = (Element) transitNode;
       					lineList = transitElement.getElementsByTagName(TRANSITAGENCYLINETAG);
        				vehicleList = transitElement.getElementsByTagName(TRANSITVEHICLETAG);
        				arrivalStopList = transitElement.getElementsByTagName(ARRIVALSTOPTAG);
            			arrivalTimeList = transitElement.getElementsByTagName(ARRIVALTIMETAG);
            			numStopsList = transitElement.getElementsByTagName(NUMSTOPSTAG);
            			agencyList = transitElement.getElementsByTagName(TRANSITAGENCYTAG);
            			urlList = transitElement.getElementsByTagName(URLTAG);
        		
        				if (vehicleList != null && vehicleList.getLength() >0 && vehicleList.item(0) != null)
        				{
        					Node vehicleNode = vehicleList.item(0);
        					if (vehicleNode.getNodeType() == Node.ELEMENT_NODE)
    						{
    							Element vehicleElement = (Element) vehicleNode;
    							vehicleNameList = vehicleElement.getElementsByTagName(NAMETAG);
    							if (vehicleNameList != null && vehicleNameList.getLength() > 0 && vehicleNameList.item(0) != null)
   								{
    								vehicleDets = vehicleNameList.item(0).getTextContent();       					
   								}
    						}
        				}
        				if (lineList != null && lineList.getLength() > 0 && lineList.item(0) != null)
        				{
            					Node lineNode = lineList.item(0);
            					if (lineNode.getNodeType() == Node.ELEMENT_NODE)
            					{
            						Element lineElement = (Element) lineNode;
       								lineNameList = lineElement.getElementsByTagName(TRANSITAGENCYSHORTNAMETAG);
       								if (lineNameList != null && lineNameList.getLength() >0 && lineNameList.item(0) != null)
       								{
       									vehicleDets = 
       											vehicleDets.concat(" " + lineNameList.item(0).getTextContent());
       								}
       							}
            			}
        				double lat = 0,lng = 0;
        				if (arrivalStopList != null && arrivalStopList.getLength() >= 0 && arrivalStopList.item(0) != null)
        				{
        					Node arrivalNode = arrivalStopList.item(0);
        					if (arrivalNode.getNodeType() == Node.ELEMENT_NODE)
        					{
        						Element arrivalElement = (Element) arrivalNode;
        						arrivalLatLngList = arrivalElement.getElementsByTagName(LOCATIONTAG);
        						arrivalStopNameList = arrivalElement.getElementsByTagName(NAMETAG);
        						if (arrivalStopNameList != null && arrivalStopNameList.getLength() >= 0 && arrivalStopNameList.item(0) != null)
        							{
        							arrivalStopName = arrivalStopNameList.item(0).getTextContent();
        							}
        						if (arrivalLatLngList != null && arrivalLatLngList.getLength() > 0 && arrivalLatLngList.item(0) != null)
                				{
        							Node arrivalLatLngNode = arrivalLatLngList.item(0);
        							if (arrivalLatLngNode.getNodeType() == Node.ELEMENT_NODE)
                					{
                						Element arrLatLngEle = (Element) arrivalLatLngNode;
                						latList = arrLatLngEle.getElementsByTagName(LATTAG);
                						lngList = arrLatLngEle.getElementsByTagName(LNGTAG);
                						if (latList != null && latList.getLength() > 0 && latList.item(0) != null)
                        				{
                							lat = Double.parseDouble(latList.item(0).getTextContent());
                        				}
                						if (lngList != null && lngList.getLength() > 0 && lngList.item(0) != null)
                        				{
                							lng = Double.parseDouble(lngList.item(0).getTextContent());
                        				}
                						latLngLoc = new LatLng(lat,lng);
                					}
                				}
        					}
        				}
        				if (arrivalTimeList != null && arrivalTimeList.getLength() > 0 && arrivalTimeList.item(0) != null)
        				{
        					Node arrivalNode = arrivalTimeList.item(0);
        					if (arrivalNode.getNodeType() == Node.ELEMENT_NODE)
        					{
        						Element arrivalElement = (Element) arrivalNode;
        						arrivalStopTimeTextList = arrivalElement.getElementsByTagName(TEXTTAG);
        						if (arrivalStopTimeTextList != null && arrivalStopTimeTextList.getLength() >= 0 && arrivalStopTimeTextList.item(0) != null)
        							arrivalTime = arrivalStopTimeTextList .item(0).getTextContent();
        							
        					}
        				}
        				if (numStopsList != null && numStopsList.getLength() > 0 && numStopsList.item(0) != null)
        				{
        							numStops =   numStopsList .item(0).getTextContent();	
        				}
        				if (agencyList != null && agencyList.getLength() > 0 && agencyList.item(0) != null)
        				{
        						Node agencyNode = agencyList.item(0);
        						if (agencyNode.getNodeType() == Node.ELEMENT_NODE)
        						{
        							Element agencyElement = (Element) agencyNode;
        							agencyNameList = agencyElement.getElementsByTagName(NAMETAG);
        							if (agencyNameList != null && agencyNameList.getLength() > 0 && agencyNameList.item(0) != null)
        							{
        								agencyName = agencyNameList.item(0).getTextContent();
        							}
        							agencyURLList = agencyElement.getElementsByTagName(URLTAG);
        							if (agencyURLList != null && agencyURLList.getLength() > 0 && agencyURLList.item(0) != null)
        							{
        								tempURL = agencyURLList.item(0).getTextContent();
        								agencyURL = new Link(tempURL);
        							}
        							phoneList = agencyElement.getElementsByTagName(PHONETAG);
        							if (phoneList != null && phoneList.getLength() > 0 && phoneList.item(0) != null)
        							{
        								phoneNum = phoneList.item(0).getTextContent();

        							}
        							
        						}
        				}
        				if (urlList != null && urlList.getLength() > 0 && urlList.item(0) != null)
        				{
        							tempURL  =   urlList.item(0).getTextContent();
        							url = new Link(tempURL);
        				}
       				}
       					TransitStopDetails details = new TransitStopDetails(latLngLoc, departureStop!=null?departureStop:arrivalStopName,
       						vehicleDets, url, departureTime != null?departureTime:arrivalTime, numStops, agencyName, agencyURL, phoneNum);
       					stops.add(details);
       
       		
            	}
              }
          }
          return stops; 			       				
    }
    
   
    
    /**
     * method to get a list of directions
     * @param the_doc
     * @return
     */
    public static ArrayList<String> getInstructionsList(Document the_doc)
    {
    	ArrayList<String> directions = new ArrayList<String>(); 	
    	Node prevStepNode = null;
    	NodeList stepNodeList, htmlList, transitDetailsList, lineList, lineNameList,
    	vehicleList, vehicleNameList, arrivalStopList, arrivalStopNameList, departureStopList, departureStopNameList,
    	arrivalTimeList, departureTimeList,departureStopTimeTextList, arrivalStopTimeTextList, numStopsList;
    	stepNodeList = the_doc.getElementsByTagName("step");
            for (int i = 0; i < stepNodeList.getLength(); i++) 
            {
            	String direction = "";

                Node node = stepNodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE && !node.getParentNode().equals(prevStepNode))
                {
                	prevStepNode = node;
                	Element element = (Element) node;
                	htmlList = element.getElementsByTagName(HTMLTAG);
                	transitDetailsList = element.getElementsByTagName(TRANSITDETSTAG);
                	if (htmlList != null && htmlList.getLength() >=0 && htmlList.item(0) != null)
                	{
                		direction = htmlList.item(0).getTextContent();
                		direction = direction != null?Html.fromHtml(direction).toString():null;
                		direction = direction != null?direction.replace("\n\n", ". "):null;
                		
                		if (transitDetailsList != null && transitDetailsList.getLength() >0 && transitDetailsList.item(0) != null)
                    	{
                    		Node transitNode = transitDetailsList.item(0);
               				if (transitNode.getNodeType() == Node.ELEMENT_NODE)
               				{
               					Element transitElement = (Element) transitNode;
               					lineList = transitElement.getElementsByTagName(TRANSITAGENCYLINETAG);
                				vehicleList = transitElement.getElementsByTagName(TRANSITVEHICLETAG);
                				arrivalStopList = transitElement.getElementsByTagName(ARRIVALSTOPTAG);
                				departureStopList = transitElement.getElementsByTagName(DEPARTURESTOPTAG);
	                			departureTimeList = transitElement.getElementsByTagName(DEPARTURETIMETAG);
	                			arrivalTimeList = transitElement.getElementsByTagName(ARRIVALTIMETAG);
	                			numStopsList = transitElement.getElementsByTagName(NUMSTOPSTAG);
                				
                				if (vehicleList != null && vehicleList.getLength() >0 && vehicleList.item(0) != null)
                				{
                					Node vehicleNode = vehicleList.item(0);
                					if (vehicleNode.getNodeType() == Node.ELEMENT_NODE)
            						{
            							Element vehicleElement = (Element) vehicleNode;
            							vehicleNameList = vehicleElement.getElementsByTagName(NAMETAG);
            							if (vehicleNameList != null && vehicleNameList.getLength() > 0 && vehicleNameList.item(0) != null)
           								{
           										direction = 
           											direction.concat("\n\n"+ vehicleNameList.item(0).getTextContent());       					
           								}
            						}
                				}
                				if (lineList != null && lineList.getLength() > 0 && lineList.item(0) != null)
                				{
                    					Node lineNode = lineList.item(0);
                    					if (lineNode.getNodeType() == Node.ELEMENT_NODE)
                    					{
                    						Element lineElement = (Element) lineNode;
               								lineNameList = lineElement.getElementsByTagName(TRANSITAGENCYSHORTNAMETAG);
               								if (lineNameList != null && lineNameList.getLength() >0 && lineNameList.item(0) != null)
               								{
               									direction = 
               											direction.concat(" " + lineNameList.item(0).getTextContent());
               								}
               							}
                    			}
                				String departureDetails = "";
                				if (departureStopList != null && departureStopList.getLength() > 0 && departureStopList.item(0) != null)
                				{
                					Node departureNode = departureStopList.item(0);
                					if (departureNode.getNodeType() == Node.ELEMENT_NODE)
                					{
                						Element departureElement = (Element) departureNode;
                						departureStopNameList = departureElement.getElementsByTagName(NAMETAG);
                						if (departureStopNameList != null && departureStopNameList.getLength() >= 0 && departureStopNameList.item(0) != null)
                							departureDetails = departureDetails.concat("\n" + DEPARTS  +  departureStopNameList.item(0).getTextContent());
                					}
                				}
                				if (departureTimeList != null && departureTimeList.getLength() > 0 && departureTimeList.item(0) != null)
                				{
                					Node departureNode = departureTimeList.item(0);
                					if (departureNode.getNodeType() == Node.ELEMENT_NODE)
                					{
                						Element departureElement = (Element) departureNode;
                						departureStopTimeTextList = departureElement.getElementsByTagName(TEXTTAG);
                						if (departureStopTimeTextList != null && departureStopTimeTextList.getLength() >= 0 && departureStopTimeTextList.item(0) != null)
                							departureDetails = departureDetails.concat(AT +  departureStopTimeTextList.item(0).getTextContent());
                					}
                				}
                				direction = direction.concat(departureDetails);
                				
                				String arrivalDetails = "";
                				if (arrivalStopList != null && arrivalStopList.getLength() >= 0 && arrivalStopList.item(0) != null)
                				{
                					Node arrivalNode = arrivalStopList.item(0);
                					if (arrivalNode.getNodeType() == Node.ELEMENT_NODE)
                					{
                						Element arrivalElement = (Element) arrivalNode;
                						arrivalStopNameList = arrivalElement.getElementsByTagName(NAMETAG);
                						if (arrivalStopNameList != null && arrivalStopNameList.getLength() >= 0 && arrivalStopNameList.item(0) != null)
                							arrivalDetails = arrivalDetails.concat("\n" + ARRIVES + arrivalStopNameList.item(0).getTextContent());
                					}
                				}
                				if (arrivalTimeList != null && arrivalTimeList.getLength() > 0 && arrivalTimeList.item(0) != null)
                				{
                					Node arrivalNode = arrivalTimeList.item(0);
                					if (arrivalNode.getNodeType() == Node.ELEMENT_NODE)
                					{
                						Element arrivalElement = (Element) arrivalNode;
                						arrivalStopTimeTextList = arrivalElement.getElementsByTagName(TEXTTAG);
                						if (arrivalStopTimeTextList != null && arrivalStopTimeTextList.getLength() >= 0 && arrivalStopTimeTextList.item(0) != null)
                							arrivalDetails = arrivalDetails.concat(AT +  arrivalStopTimeTextList .item(0).getTextContent());
                					}
                				}
                				direction = direction.concat(arrivalDetails);
                				
                				if (numStopsList != null && numStopsList.getLength() > 0 && numStopsList.item(0) != null)
                				{
                							direction = direction.concat("\n" + NUMSTOPS +  numStopsList .item(0).getTextContent());	
                				}

                    		}        
               			}
                    }
                }
                
                if (direction != null && direction.length() > 0)
                {
                	directions.add(direction);
                }
            }
            String warning = getWarning(the_doc);
            String copyright = getCopyrights(the_doc);
            if (warning != null && warning != "")
            {
            	int index = directions.size() - 1;
            	String lastDirection = directions.get(index);
            	if (lastDirection != null)
            	{
            		lastDirection = lastDirection.concat("\n\n").concat(warning);
            		directions.remove(index);
            		directions.add(index, lastDirection);
            	}
            	else
            	{
            		directions.add(warning);
            	}
            	
            }
            if (copyright != null && copyright != "")
            {
            	int index = directions.size() - 1;
            	String lastDirection = directions.get(index);
            	if (lastDirection != null)
            	{
            		lastDirection = lastDirection.concat("\n\n").concat(copyright);
            		directions.remove(index);
            		directions.add(index, lastDirection);
            	}
            	else
            	{
            		directions.add(copyright);
            	}
            }
        return directions;
        }

	/**
	 * @param the_doc
	 * @return
	 */
	private static String getWarning(Document the_doc) {
		String warning = null;
		if (the_doc != null)
		{
			NodeList warningList = the_doc.getElementsByTagName(WARNINGTAG);
			if (warningList != null)
			{
				warning = "";
				int len = warningList.getLength();
				for (int i = 0; i < len; i++)
				{
					warning = warning.concat("\n").concat(warningList.item(i)!=null?warningList.item(i).getTextContent():"");
				}
			}
		}
		return warning;
	}
	
	/**
	 * @param the_doc
	 * @return
	 */
	private static String getCopyrights(Document the_doc) {
		String copyrights = null;
		if (the_doc != null)
		{
			NodeList copyrightsList = the_doc.getElementsByTagName(COPYRIGHTSTAG);
			if (copyrightsList != null)
			{
				copyrights = "";
				int len = copyrightsList.getLength();
				for (int i = 0; i < len; i++)
				{
					copyrights = copyrights.concat("\n").concat(copyrightsList.item(i)!=null?copyrightsList.item(i).getTextContent():"");
				}
			}
		}
		return copyrights;
	}
	}
