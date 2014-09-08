/**
 * 
 */
package com.bitBusy.goingg.webServices.EventsData;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.content.Context;

import com.amazonaws.services.simpledb.AmazonSimpleDBClient;
import com.amazonaws.services.simpledb.model.SelectRequest;
import com.amazonaws.services.simpledb.model.SelectResult;
import com.bitBusy.goingg.cache.Cache;
import com.bitBusy.goingg.events.DataSources;
import com.bitBusy.goingg.mapDisplay.EventsDisplaySetter;
import com.bitBusy.goingg.settings.QueryFilter;
import com.bitBusy.goingg.webServices.awsSetup.AWSCredentialsStore;

/**
 * @author SumaHarsha
 *
 */
public class QuerierWorker implements Runnable
{
//Callable<String>{

	private static int TIMEOUTMILLIS = 2000;
	
	private static final int TIMEOUTSOCKET = 5000;
	
	private String myURI;
		
	private EventsDisplaySetter myEventsSetter;
	
	private QueryPipelineParameters myQueryParameters;
	
	private Context myContext;

	private QueryFilter myQueryFilter;
	
	public QuerierWorker(Context the_context, EventsDisplaySetter the_eventssetter, QueryFilter the_queryfilter, String processingURI)
	{
		myEventsSetter = the_eventssetter;
		myURI = processingURI;
		 myQueryParameters = QueryPipelineParameters.getInstance();
		 myQueryFilter = the_queryfilter;
		 myContext = the_context;
	}
	/* (non-Javadoc)
	 * @see java.util.concurrent.Callable#call()
	 */
	@Override
	public void run()
	{
	//String call() throws Exception {
		if (myURI != null)
		{
			if (Cache.getInstance().isCached(myContext, myURI))
			{
				sendToDataExtractor(new QueryResult(myURI, Cache.getInstance().getCachedData(myContext, myURI)));
			}
			else
			{
				if (myURI.startsWith(DataSources.AWSSELECTSTATEMENT))
				{
					sendToDataExtractor(fireAWSQuery());
				}
				else
				{
					HttpGet httpGet = null;
					HttpClient client = new DefaultHttpClient();
					try
					{
					httpGet = new HttpGet(myURI);
					HttpParams httpParameters = new BasicHttpParams();	
					HttpConnectionParams.setConnectionTimeout(httpParameters, TIMEOUTMILLIS);
					HttpConnectionParams.setSoTimeout(httpParameters, TIMEOUTSOCKET);
					}
					catch(Exception e)
					{
						e.printStackTrace();
						sendToDataExtractor(null);
					}
					readData(client, httpGet);
				}
			}
		}
		else
		{
			sendToDataExtractor(null);
		}
	}
	 
	/**
	 * @return
	 */
	private QueryResult fireAWSQuery() {
		AmazonSimpleDBClient sdbClient = AWSCredentialsStore.getInstance().getSDBClient();
		if (myURI != null && myURI.length() > 0 && sdbClient != null)
		{
			try
			{
				String[] components = myURI.split(AWSPut.TOKEN);
				if (components != null && components.length >=1)
				{
					SelectRequest selectRequest = new SelectRequest(components[0], true);
					selectRequest.setConsistentRead(true);
					if (components.length > 1 && components[1] != null && components[1].length() > 0)
					{
						selectRequest.setNextToken(components[1]);
					}
					SelectResult response =  sdbClient.select(selectRequest);
					return new QueryResult(myURI, response);
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return null;
	}
	private void readData(HttpClient the_client, HttpGet the_httpGet) {
		  StringBuilder dataBuilder = new StringBuilder();
		  HttpResponse response = null;
		try {
			  response = the_client.execute(the_httpGet);
				}
		catch (ClientProtocolException e) {
			e.printStackTrace();
			} catch (IOException e) { 
				e.printStackTrace();
			}
		if (response != null)
		{
			  StatusLine statusLine = response.getStatusLine();
			  int statusCode = statusLine.getStatusCode();
			  if (statusCode == 200) {
			    HttpEntity entity = response.getEntity();
			    InputStream content = null;
				try {
					content = entity.getContent();
			    BufferedReader reader = new BufferedReader(new InputStreamReader(content));
			    String line;
			    while ((line = reader.readLine()) != null) {
			    	dataBuilder.append(line);
			    }
				}
				 catch (IllegalStateException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				  try {
					entity.consumeContent();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}
		Cache.getInstance().cache(myContext, myURI, dataBuilder.toString());
		sendToDataExtractor(new QueryResult(myURI, dataBuilder.toString()));
	}

	private void sendToDataExtractor(QueryResult the_result)
	{
			DataExtractor dataExtractor = new DataExtractor(the_result , myQueryFilter, myEventsSetter);
			myQueryParameters.getExecutorService().submit(dataExtractor);

	}

}
