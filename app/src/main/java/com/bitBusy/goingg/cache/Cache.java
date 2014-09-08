/**
 * 
 */
package com.bitBusy.goingg.cache;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import android.content.Context;
import android.os.AsyncTask;

import com.bitBusy.goingg.events.DataSources;
import com.bitBusy.goingg.webServices.EventsData.DataExtractor;
import com.bitBusy.goingg.webServices.EventsData.Querier;
import com.bitBusy.goingg.webServices.EventsData.WeatherForecast;

/**
 * @author SumaHarsha
 *
 */
public class Cache {
	
	
    private static final long MAX_SIZE =  20971520L; // 20 MB
	
	private static final String SEATTLE = "sea";
	
	private static final String CATEGORY = "category";

	private static final String TYPES = "types";

	private static final String RESOURCE = "/resource/";
	
	private static final String TEMPDATA = "/tempdata";

	private static final String MAPSPRIMARY = "http://maps.googleapis.com/maps/api/directions/xml?";
	private static Cache myInstance;
	
	private static File myDir;
		
	private Cache()
	{	
	}
	
	public static Cache getInstance()
	{
		if (myInstance == null)
		{
			myInstance = new Cache();
		}
		return myInstance;
	}
	
	public void cache(Context the_context, String the_link, Object the_json)
	{
		if (the_context != null && the_link != null && the_json != null)
		{
			if (myDir == null)
			{
				initiaizeDir(the_context);
			}
			if (myDir != null)
			{
				File file = new File(myDir, getFileName(the_link));
				writeToFile(the_context,the_link, file, the_json);
			}
		}
	}

	public String getCachedData(Context the_context, String the_link)
	{
		String data = null;
		if (the_link != null && the_context != null)
		{
			if (myDir == null)
			{
				initiaizeDir(the_context);
			}
			File file = new File(myDir, getFileName(the_link));
			if (file!=null && file.exists())
			{
				if (the_link.contains(DataSources.GOOGLEPLACEPAGETOKEN)) 
				{
					if ( checkForLink(file, the_link))
					{
						data = readFromFileGooglePage(file, the_link);
					}
				}
				else
				{
					data = readFromFile(file);
				}
			}		
		}
		return data;
	}
	/**
	 * @param file
	 * @return
	 */
	private String readFromFileGooglePage(File the_file, String the_link) {
		StringBuffer data = new StringBuffer();
		if (the_file != null)
		{
			BufferedReader br = null;
			try 
			{
				String currentLine;	 
				boolean readingLink = false;
				int index = 0;
				br = new BufferedReader(new FileReader(the_file));	 
				while ((currentLine = br.readLine()) != null) {
					if (readingLink)
					{
						readingLink = currentLine.equals(the_link.substring(index, currentLine.length()));
						index += currentLine.length();
					}
					if (!readingLink)
					{
						data.append(currentLine);
					}
				}	 
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					if (br != null)br.close();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		}
		return data.toString();
	}

	/**
	 * @param file
	 * @return
	 */
	private String readFromFile(File the_file) {
		StringBuffer data = new StringBuffer();
		if (the_file != null)
		{
			BufferedReader br = null;
			try 
			{
				String currentLine;	 
				br = new BufferedReader(new FileReader(the_file));	 
				while ((currentLine = br.readLine()) != null) {
					data.append(currentLine);
				}	 
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					if (br != null)br.close();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		}
		return data.toString();
	}

	/**
	 * @param the_context
	 */
	private void initiaizeDir(Context the_context) {
		myDir = new File(the_context.getFilesDir(), TEMPDATA);
		myDir.mkdir();
		}

	/**
	 * @param the_context 
	 * @param file
	 * @param the_json
	 */
	private void writeToFile(Context the_context, String the_uri, File the_file, Object the_json) {
		checkDirSpace();
		if (the_file != null && the_json != null && the_context != null)
		{
			FileOutputStream outputStream;
			try {
				outputStream = the_context.openFileOutput(the_file.getName(), Context.MODE_PRIVATE);
				if (outputStream != null)
				{
					if (the_uri.contains(MAPSPRIMARY) && the_json instanceof Document)
					{
						writeDirectionsDocToFile(the_file, the_json);
					}
					else
					{
						if (the_file.getName() != null && the_uri.contains(DataSources.GOOGLEPLACEPAGETOKEN))
						{
							outputStream.write(the_uri.getBytes());
						}
						outputStream.write(the_json.toString()!=null?the_json.toString().getBytes():null);
						outputStream.close();
					}
				}
				} catch (Exception e) {
				  e.printStackTrace();
				} 
		}
		
	}
	
	/**
	 * 
	 */
	private void checkDirSpace() {
		if (myDir != null)
		{
			long size = myDir.getFreeSpace();
			if (size >= (0.7 * MAX_SIZE))
			{
				new DirCleaner().execute();
			}
		}
	}

	private void writeDirectionsDocToFile(File the_file, Object the_doc) {
		if (the_file != null && the_doc != null && the_doc instanceof Document)
		{
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			try {
				Transformer transformer = transformerFactory.newTransformer();
				DOMSource source = new DOMSource((Document) the_doc);
				StreamResult result = new StreamResult(the_file);
				transformer.transform(source, result);
			} catch (TransformerConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TransformerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}

	/**
	 * @param name
	 * @return
	 */
	private String getFileName(String the_name) {
		if (the_name != null)
		{
			if (the_name.contains(DataSources.EVENTBRITEPRIMARYLINK))
			{
				return getEventbriteFileName(the_name);
			}
			if (the_name.contains(DataSources.GOOGLEPLACEPRIMARY))
			{
				return getGooglePlaceFileName(the_name);
			}
			if (the_name.contains(DataSources.GOOGLEPLACEPAGETOKEN))
			{
				return getGooglePlacePageTknFileName(the_name);
			}
			if (the_name.contains(DataSources.SEATTLELINKS))
			{
				return getSeattleLinksFileName(the_name);
			}
			if (the_name.contains(MAPSPRIMARY))
			{
				return the_name.substring(MAPSPRIMARY.length());
			}
			if (the_name.contains(WeatherForecast.SERVERLINK))
			{
				return the_name.substring(WeatherForecast.SERVERLINK.length());
			}
			
			
		}
		return the_name;
	}
	
	/**
	 * @param the_name
	 * @return
	 */
	private String getGooglePlacePageTknFileName(String the_name) {
		if (the_name != null)
		{
			return String.valueOf(the_name.hashCode());
		}
		return null;	
	}

	/**
	 * @param the_name
	 * @return
	 */
	private String getGooglePlaceFileName(String the_name) {
		if (the_name != null)
		{
			String indexarray = getGooglePlaceCategoriesChosen(the_name);
			String[] components = the_name.split
					(DataSources.AND.concat(DataExtractor.LOCATION).concat(DataSources.EQUALS));
			if (indexarray != null && components != null && components.length==2)
			{
				return "G".concat(indexarray).concat(components[1]);
			}
		}
		return null;
	}

	/**
	 * @param the_name
	 * @return
	 */
	private String getGooglePlaceCategoriesChosen(String the_name) {
		try
		{
			if (the_name != null)
			{
				String categorieswithextra = the_name.substring(the_name.indexOf(TYPES.concat(DataSources.EQUALS)) + 1);
				if (categorieswithextra!=null)
				{
					String categories = categorieswithextra.substring(0, categorieswithextra.indexOf(DataSources.AND));
					return getGooglePlaceCategoryIndices(categories);
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
			return null;
	}

	/**
	 * @param categories
	 * @return
	 */
	private String getGooglePlaceCategoryIndices(String the_categories) {
		if (the_categories  != null)
		{
			StringBuilder retStr = new StringBuilder();
			List<String> categories = Arrays.asList(DataSources.GOOGLEPLACECATEGORIES);
			StringTokenizer tokens = new StringTokenizer(the_categories, Querier.PIPE);
			while (tokens.hasMoreElements()) 
			{
				String next = tokens.nextToken();
				if (next != null)
				{
			 		retStr.append(categories.indexOf(next));
				}
			}
			return retStr.toString();
		}
		return null;
	}

	private String getEventbriteFileName(String the_name) 
	{
		if (the_name != null)
		{
			String indexarray = getEventbriteCategoriesChosen(the_name);
			String[] components = the_name.split
					(DataSources.AND.concat(DataSources.LATITUDE).concat(DataSources.EQUALS));
			if (indexarray != null && components != null && components.length==2)
			{
				return "E".concat(indexarray).concat(components[1]);
			}
		}
		return null;
	}

	/**
	 * @param the_name
	 * @return
	 */
		private String getEventbriteCategoriesChosen(String the_name) {
		if (the_name != null)
		{
			String categorieswithextra = the_name.substring(the_name.indexOf(CATEGORY.concat(DataSources.EQUALS)) + 1);
			if (categorieswithextra!=null)
			{
				String categories = categorieswithextra.substring(0, the_name.indexOf(DataSources.AND));
				return getEventbriteCategoryIndices(categories);
			}
		}
		return null;
	}
		
		private String getEventbriteCategoryIndices(String the_categories) {
			if (the_categories  != null)
			{
				StringBuilder retStr = new StringBuilder();
				List<String> categories = Arrays.asList(DataSources.EVENTBRITECATEGORIES);
				StringTokenizer tokens = new StringTokenizer(the_categories, ",");
				while (tokens.hasMoreElements()) 
				{
					String next = tokens.nextToken();
					if (next != null)
					{
				 		retStr.append(categories.indexOf(next));
					}
				}
				return retStr.toString();
			}
			return null;
		}

	public boolean isCached(Context the_context, String the_link)
	{
		if (the_link != null && the_context != null)
		{
			if (myDir == null)
			{
				initiaizeDir(the_context);
			}
			String file = getFileName(the_link);
			if (file != null)
			{
				File check = new File(myDir, file);
				if (check!=null && check.exists())
				{
					if (the_link.contains(DataSources.GOOGLEPLACEPAGETOKEN))
					{
						return checkForLink(check, the_link);
					}
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * @param check
	 * @param the_link
	 * @return
	 */
	private boolean checkForLink(File the_file, String the_link) {
		boolean retVal = false;
		if (the_file != null && the_link != null)
		{
			String currentLine;	 
			BufferedReader br = null;
			int index = 0;
				try 
				{
					retVal = true;
					br = new BufferedReader(new FileReader(the_file));	 
					while (retVal && ((currentLine = br.readLine()) != null)) {
						retVal = currentLine.equals(the_link.substring(index, currentLine.length()));
						index += currentLine.length();
					}
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					try {
						if (br != null)br.close();
					} catch (IOException ex) {
						ex.printStackTrace();
					}
				}
		}
		return retVal;
	}

	private String getSeattleLinksFileName(String the_name) {
		if (the_name != null)
		{
			String[] components = the_name.split(RESOURCE);
			if (components != null && components.length == 2)
			{
				return SEATTLE.concat(components[1]);
			}
		}
		return null;
	}

	/**
	 * @param myContext
	 * @param url
	 * @return
	 */
	public Document getDirsCachedData(Context the_context, String the_link) {
		if (the_link != null && the_context != null)
		{
			if (myDir == null)
			{
				initiaizeDir(the_context);
			}
			File file = new File(myDir, getFileName(the_link));
			if (file!=null && file.exists())
			{
	            DocumentBuilder builder;
				try {
					builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		            return builder.parse(file);

				} catch (ParserConfigurationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SAXException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}		
		return null;
	}

	private class DirCleaner extends AsyncTask<Void, Void, Void>
	{

		/* (non-Javadoc)
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected Void doInBackground(Void... params) {
			if (myDir != null)
			{
				   int i = 0;
				   File[] files = myDir.listFiles();
				   if (files != null)
				   {
						while (i < files.length && files[i] != null && myDir.getFreeSpace() >= (0.4 * MAX_SIZE))
						{	
				            files[i].delete();
				            i++;				            
						}
				   }
				 }
			return null;
		}
		
	}

}