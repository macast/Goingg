/**
 * 
 */
package com.bitBusy.goingg.webServices.EventsData;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import android.content.Context;
import android.view.View;
import android.widget.ProgressBar;

import com.bitBusy.goingg.activity.ActivityMapViewHome;

/**
 * @author SumaHarsha
 *
 */
public class QueryPipelineParameters {
	
	private static final int NUMBERTHREADS = 10;

	private static QueryPipelineParameters myInstance;

	private ExecutorService myExecutor;

	private AtomicInteger myURISubmitted;
	
	private AtomicInteger myURIProcessed;

	private ProgressBar myProgressBar;

	private Context myContext;

	private QueryPipelineParameters()
	{
		 myURISubmitted = new AtomicInteger(0);
		 myURIProcessed =  new AtomicInteger(0);
	}
	
	public static QueryPipelineParameters getInstance()
	{
		if (myInstance == null)
		{
			myInstance = new QueryPipelineParameters();
		}
		return myInstance;
	}
	
	public ExecutorService getExecutorService()
	{
		if (myExecutor == null ||myExecutor.isShutdown())
		{
			myExecutor = Executors.newFixedThreadPool(NUMBERTHREADS);
		}
		return myExecutor;
	}
	
	public void incrementSubmittedURICount()
	{
		myURISubmitted.incrementAndGet();
		setProgressBarVisibility(View.VISIBLE);
	}
	
	public void incrementProcessedURICount()
	{
		myURIProcessed.incrementAndGet();
		toShutDownExecutor();
	}

	/**
	 * 
	 */
	private void toShutDownExecutor() {
		if (myURIProcessed.get() == myURISubmitted.get())
	   	{
			myURIProcessed.set(0);
			myURISubmitted.set(0);
	   		shutDownExecutor();
	   	}
	}
	
	 private void shutDownExecutor()
	   {
		   	   myExecutor.shutdownNow();
		   	   setProgressBarVisibility(View.GONE);
	   }
	 
	 public void setProgressBar(ProgressBar the_progressBar)
	 {
		 myProgressBar = the_progressBar;
		 setProgressBarVisibility(View.VISIBLE);
	 }
	 
	 private void setProgressBarVisibility(final int the_visibility)
	 {
		 if (myProgressBar != null && myContext != null)
		{
					((ActivityMapViewHome) myContext).runOnUiThread( new Runnable()
					{
						public void run()
						{
							myProgressBar.setVisibility(the_visibility);
						}
					});
		}
	 }
	 
	 public void setContext(Context the_context)
	 {
		 myContext = the_context;
	 }
	 
}
