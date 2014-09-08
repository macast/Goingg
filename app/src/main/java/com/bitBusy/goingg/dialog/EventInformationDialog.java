/**
 * 
 */
package com.bitBusy.goingg.dialog;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.Layout;
import android.text.TextUtils.TruncateAt;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bitBusy.goingg.activity.ActivityMapDirections;
import com.bitBusy.goingg.cache.LocalDBCache;
import com.bitBusy.goingg.database.setup.DBInteractor.EVENTMARKING;
import com.bitBusy.goingg.events.Address;
import com.bitBusy.goingg.events.DataSources;
import com.bitBusy.goingg.events.PublicEvent;
import com.bitBusy.goingg.events.User;
import com.bitBusy.goingg.mapDisplay.EventsDisplaySetter;
import com.bitBusy.goingg.utility.AWSItemNameGenerator;
import com.bitBusy.goingg.utility.UUIDGenerator;
import com.bitBusy.goingg.utility.Utility;
import com.bitBusy.goingg.weather.Forecast;
import com.bitBusy.goingg.webServices.EventsData.AWSDelete;
import com.bitBusy.goingg.webServices.EventsData.AWSReportSpam;
import com.bitBusy.goingg.webServices.EventsData.DataExtractor;
import com.bitBusy.goingg.webServices.EventsData.WeatherForecast;
import com.bitBusy.goingg.webServices.EventsData.WeatherForecastRequestor;
import com.bitBusy.goingg.webServices.UserData.AckEvent;
import com.bitBusy.goingg.webServices.UserData.UserStatsQuerier;
import com.bitBusy.goingg.webServices.awsSetup.AWSOperationListenerInterface;
import com.bitBusy.goingg.webServices.awsSetup.ListenerAck;
import com.bitBusy.goingg.R;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

/**
 * @author SumaHarsha
 *
 */
public class EventInformationDialog implements WeatherForecastRequestor, AWSOperationListenerInterface{
	
	public static final String ALARMDETAILS = "com.bitBusy.wevent.ALARMDETAILS";

	private static final String NOFORECAST = "Sorry, we don't seem to know the weather will be like for this/these date(s)!";
	
	public static final String APPENDMSG = "\n\nFound it on 'Going'! " +
			"\n https://play.google.com/store/apps/details?id=com.bitBusy.goingg";
	
	public static final String MESSAGESTRING = "Check this out! ";

	private static final String EVENTBRITE = "http://www.eventbrite.com/";

	private static final String SEATTLEGOV = "http://www.seattle.gov/";

	private static final String GOINGER = "Going user";

	private static final CharSequence NOUSERINFO = "Sorry, no information found for this user!";

	private static final String NO = "No";

	private static final String YES = "Yes";

	private static final CharSequence CONFIRMACKUNDOTITLE = "Undo event marking";

	private static final CharSequence CONFIRMACKUNDOMSG = "Are you sure you'd like to undo your marking for this event?";

	/** context*/
	private Context myContext;
	
	/** dialog*/
	private Dialog myDialog;
	
	/** event*/
	private PublicEvent myEvent;

	private EventsDisplaySetter mySetter;
	/** from latlng for map*/
	//private LatLng myFromLatLng;
	/**
	 * parameterized constructor
	 * @param the_context
	 * @param the_event
	 */
	public EventInformationDialog(Context the_context, PublicEvent the_event, EventsDisplaySetter the_setter)// LatLng the_fromLatLng)
	{
		myContext = the_context;
		myEvent = the_event;
		mySetter = the_setter;
	}
	
	
	/** throws open a dialog*/
	public void openDetailsDialog() {
		if (myEvent != null)
		{
			myDialog = new Dialog(myContext);
			myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		    LayoutInflater inflater = (LayoutInflater) myContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		    
		    // Inflate and set the layout for the dialog
		    // Pass null as the parent view because its going in the dialog layout
			myDialog.setContentView(inflater.inflate(R.layout.event_details_dialog, null));
			setEventDetails(myEvent);
			setTitleButtonListeners();
			setCloseButtonListener();
			setMapButtonListener();
			setWeatherButtonListener();
			myDialog.show();
		}
	}
	
	private void setTitleButtonListeners()
	{
		setReminderButtonListener();
		setShareButtonListener();
		setTrashSpamButtons();
	}
	
	/**
	 * 
	 */
	private void setTrashSpamButtons() 
	{
		if (myEvent != null)
		{
			if(myEvent.getCreator()!=null && (!myEvent.getCreator().equals(DataSources.GOOGLEPLACEPRIMARY) && 
					!myEvent.getCreator().equals(DataSources.SEATTLELINKS) && !myEvent.getCreator().equals(DataSources.EVENTBRITEPRIMARYLINK)))
					{
						if (myEvent.getCreator().equals(UUIDGenerator.id(myContext)))
						{
							setTrashButton();
						}
						else
						{
							setSpamButton();
						}
					}	
		}
	}


	/**
	 * 
	 */
	private void setSpamButton() {
		ImageButton spam = (ImageButton) myDialog.findViewById(R.id.eventDets_spamButton);
		ImageButton trash = (ImageButton) myDialog.findViewById(R.id.eventDets_trashButton);
		trash.setVisibility(View.GONE);
		spam.setVisibility(View.VISIBLE);
		spam.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				throwOpenSpamQDialog();
			}

			private void throwOpenSpamQDialog() {
					AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
							myContext);
						alertDialogBuilder.setTitle("Report spam");
						alertDialogBuilder
							.setMessage("Are you sure you want to report this event as spam?")
							.setCancelable(false)
							.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,int id) {
									dialog.dismiss();
									AWSReportSpam report = new AWSReportSpam(myContext, new Object[]{myEvent});
									report.execute();
								}
							  })
							.setNegativeButton("No",new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,int id) {
									dialog.cancel();
								}
							});
							AlertDialog alertDialog = alertDialogBuilder.create();
							alertDialog.show();
				}
		});		
	}


	/**
	 * 
	 */
	private void setTrashButton() {
		ImageButton spam = (ImageButton) myDialog.findViewById(R.id.eventDets_spamButton);
		ImageButton trash = (ImageButton) myDialog.findViewById(R.id.eventDets_trashButton);
		spam.setVisibility(View.GONE);
		trash.setVisibility(View.VISIBLE);
		trash.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				throwOpenDeleteQDialog();
			}

			private void throwOpenDeleteQDialog() {
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
						myContext);
					alertDialogBuilder.setTitle("Delete event");
					alertDialogBuilder
						.setMessage("Are you sure you want to delete this event?")
						.setCancelable(false)
						.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,int id) {
								dialog.dismiss();
								AWSDelete delete = new AWSDelete(myContext, new Object[]{myEvent});
								delete.registerListener(EventInformationDialog.this);
								delete.execute();
							}
						  })
						.setNegativeButton("No",new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,int id) {
								dialog.cancel();
							}
						});
						AlertDialog alertDialog = alertDialogBuilder.create();
						alertDialog.show();
			}
			
		});		
	}


	private void setReminderButtonListener()
	{
		ImageButton reminder = (ImageButton) myDialog.findViewById(R.id.eventDets_reminderButton);
		reminder.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new SetReminderDialog(myContext, myEvent).openDialog();
			}
			
		});
	}
	private void setShareButtonListener()
	{
		ImageButton share = (ImageButton) myDialog.findViewById(R.id.eventDets_shareButton);
		share.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
					  Intent shareIntent = new Intent();
				      shareIntent.setAction(Intent.ACTION_SEND);
				      shareIntent.setType("text/plain");
						shareIntent.putExtra(Intent.EXTRA_TEXT, MESSAGESTRING.concat(myEvent.getName()).concat(" between ").concat(Utility.getDateTime(myEvent.getStartDateTime())).concat
									(" and ").concat(Utility.getDateTime(myEvent.getEndDateTime())).concat(" at ").concat(myEvent.getAddress().toString()).
									concat("\n\n more: " + (myEvent.getInfoLink()!=null?myEvent.getInfoLink().getURL():"-")).concat(APPENDMSG));
						 ((Activity)myContext).startActivity(Intent.createChooser(shareIntent, "Share"));
				myDialog.dismiss();
			}
			
		});

	}

	/**
	 * listener to close the dialog on click
	 * @param the_detailsDialog
	 */
	private void setCloseButtonListener() {
		Button close = (Button) myDialog.findViewById(R.id.eventDets_close);
		close.setOnClickListener(new OnClickListener() {						 
			@Override
			public void onClick(View arg0) {
				myDialog.dismiss();
			}
 
		});
	}
	
		/**listener to get directions*/
	private void setMapButtonListener() {
		ImageButton info = (ImageButton) myDialog.findViewById(R.id.eventDets_map);
		info.setOnClickListener(new OnClickListener() {						 
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(myContext,ActivityMapDirections.class);
				Bundle args = new Bundle();
				args.putParcelable(ActivityMapDirections.TOLATLNG, myEvent!=null?myEvent.getCoordinates():null);
				intent.putExtra(ActivityMapDirections.LATLNGBUNDLE, args);
				intent.putExtra(ActivityMapDirections.EVENTNAME, myEvent!=null?myEvent.getName():"");
				intent.putExtra(ActivityMapDirections.EVENTCOLOR, myEvent!=null? myEvent.getCategory().getColor():BitmapDescriptorFactory.HUE_RED);
				intent.putExtra(ActivityMapDirections.TOADDRESS, myEvent!=null?myEvent.getAddress():null);
				myContext.startActivity(intent);
			}
 
		});
	}
	
	/**listener to get directions*/
	private void setWeatherButtonListener() {
		ImageButton weather = (ImageButton) myDialog.findViewById(R.id.eventDets_weather);
		weather.setOnClickListener(new OnClickListener() {						 
			@Override
			public void onClick(View arg0) {
				 WeatherForecast weather = new WeatherForecast(myContext);
				 weather.registerRequestor(EventInformationDialog.this);
				 weather.execute(myEvent);

				}
 
		});
	}
	
	/** 
	 * sets details on dialog
	 * @param the_event
	 * @param the_detailsDialog
	 */
	private void setEventDetails(final PublicEvent the_event) {
		if (myDialog != null && the_event != null)
		{
			setAckLayout();
			setNumbersLayout();
			final TextView name = (TextView) myDialog.findViewById(R.id.eventDets_namedetailsText);
			name.setText(the_event.getName()!=null && !DataExtractor.NULL.equals(the_event.getName())?the_event.getName():null);
			//setViewObserver(name);	
			final TextView category = (TextView) myDialog.findViewById(R.id.eventDets_categorydetails);
			category.setText(the_event.getCategory().getName()!= null?the_event.getCategory().getName().toString():null);
			//setViewObserver(category);		
			final TextView desc = (TextView) myDialog.findViewById(R.id.eventDets_descdetails);
			desc.setText(the_event.getDescription()!=null&&!DataExtractor.NULL.equals(the_event.getDescription())?
					Html.fromHtml(the_event.getDescription()):null);
			setViewObserver(desc);			
			TextView startDate = (TextView) myDialog.findViewById(R.id.eventDets_startsdetails);
			String start = Utility.getDateTime(the_event.getStartDateTime());
			startDate.setText(start!=null?start:null);
			TextView endDate = (TextView) myDialog.findViewById(R.id.eventDets_endsdetails);
			String end = Utility.getDateTime(the_event.getEndDateTime());
			endDate.setText(end!=null?end:null);
			TextView price = (TextView) myDialog.findViewById(R.id.eventDets_pricedetails);
			price.setText(myEvent.getPrice()==null|| DataSources.NULL.equals(myEvent.getPrice())?"":myEvent.getPrice());
			TextView info = (TextView) myDialog.findViewById(R.id.eventDets_infodetails);
			String link = the_event.getInfoLink().getURL().toString();
			info.setText(link!=null?link:null);
			Linkify.addLinks(info, Linkify.ALL);
			Address eventAdd = myEvent.getAddress();
			if (eventAdd != null)
			{
				StringBuffer addressBldr = new StringBuffer();
				String streetAdd = (eventAdd.getStreetAdd()!=null && !eventAdd.getStreetAdd().equals(DataExtractor.NULL))?
						eventAdd.getStreetAdd().concat(DataSources.COMMA):"";
				String city = (eventAdd.getCity()!=null && !eventAdd.getCity().equals(DataExtractor.NULL))?
						eventAdd.getCity().concat(DataSources.COMMA):"";
				String state = (eventAdd.getState()!=null && !eventAdd.getState().equals(DataExtractor.NULL))?
						eventAdd.getState().concat(" "):"";
				String country = (eventAdd.getCountry()!=null && !eventAdd.getCountry().equals(DataExtractor.NULL))?
						eventAdd.getCountry():"";
				String postal = (eventAdd.getPostalCode()!=null && !eventAdd.getPostalCode().equals(DataExtractor.NULL))?
						eventAdd.getPostalCode().concat(DataSources.COMMA):"";
				addressBldr.append(streetAdd).append(" ").append(city).append(" ").append(state).append(postal).append(" ").append(country);
				TextView address = (TextView) myDialog.findViewById(R.id.eventDets_addressdetails);
				address.setText(addressBldr.toString());
				Linkify.addLinks(address, Linkify.MAP_ADDRESSES);
				TextView source = (TextView) myDialog.findViewById(R.id.eventDets_sourcedetails);
				String sourceVal = getEventSource();
				if (sourceVal != null)
				{
					if(sourceVal.equals(GOINGER))
					{
						source.setText(GOINGER);
						source.setOnClickListener(new OnClickListener()
						{

							@Override
							public void onClick(View arg0) {
								UserStatsQuerier querier = new UserStatsQuerier(myContext, new Object[]{the_event.getCreator()});
								querier.registerListener(EventInformationDialog.this);
								querier.execute();
							}
							
						});
					}
					else
					{
						source.setText(sourceVal);
						Linkify.addLinks(source, Linkify.WEB_URLS);
					}
				}
			}
		}
	}
	
	/**
	 * 
	 */
	private void setAckLayout() {
		if (myDialog != null && Utility.isGoingerEvent(myEvent))
		{
			EVENTMARKING ack = LocalDBCache.getInstance().getEventMarking(AWSItemNameGenerator.generate(myEvent));
			if (ack == null)
			{
				LinearLayout ackedLayout = (LinearLayout) myDialog.findViewById(R.id.eventDets_markedLayout);
				if (ackedLayout != null)
				{
					ackedLayout.setVisibility(View.GONE);
					LinearLayout layout = (LinearLayout) myDialog.findViewById(R.id.eventDets_acklayout);
					if (layout != null)
					{
						layout.setVisibility(View.VISIBLE);
						Button goingButton = (Button) myDialog.findViewById(R.id.eventDets_yesbutton);
						setAckButtonListener(goingButton);
						Button noButton = (Button) myDialog.findViewById(R.id.eventDets_nobutton);
						setAckButtonListener(noButton);
						Button maybeButton = (Button) myDialog.findViewById(R.id.eventDets_maybebutton);
						setAckButtonListener(maybeButton);	
					}
				}
			}
			else
			{
				modifyUIOnIncAck(ack);
			}
		}
	}


	/**
	 * 
	 */
	private void setNumbersLayout() 
	{
		if (myDialog != null && myEvent != null && Utility.isGoingerEvent(myEvent))
		{
			LinearLayout layout = (LinearLayout) myDialog.findViewById(R.id.eventDets_numLayout);
			if (layout != null)
			{
				layout.setVisibility(View.VISIBLE);
				TextView going = (TextView) myDialog.findViewById(R.id.eventDets_goingvalue);
				setValueToTV(going, String.valueOf(myEvent.getNumGoing()));
				TextView nope = (TextView) myDialog.findViewById(R.id.eventDets_nopevalue);
				setValueToTV(nope, String.valueOf(myEvent.getNumNope()));
				TextView maybe = (TextView) myDialog.findViewById(R.id.eventDets_maybevalue);
				setValueToTV(maybe, String.valueOf(myEvent.getNumMaybe()));				
			}
		}
	}


	/**
	 * @param going
	 * @param valueOf
	 */
	private void setValueToTV(TextView the_tv, String the_value) 
	{
		if (the_tv != null && the_value != null)
		{
			the_tv.setText(the_value);
		}
	}


	/**
	 * @param maybeButton
	 */
	private void setAckButtonListener(final Button the_button) 
	{
		if (the_button != null)
		{
			the_button.setOnClickListener(new OnClickListener()
			{

				@Override
				public void onClick(View arg0) {
					AckEvent ack = new AckEvent(myContext, new Object[]{myEvent, getAckValue(), true});
					ack.registerListener(EventInformationDialog.this);
					ack.execute();
				}

				private EVENTMARKING getAckValue() {
					if (the_button != null && the_button.getText() != null && the_button.getText().toString() != null)
					{
						String buttonText = the_button.getText().toString();
						if (buttonText != null)
						{
							if (buttonText.equals(EVENTMARKING.Going.name()))
							{
								return EVENTMARKING.Going;
							}
							else if (buttonText.equals(EVENTMARKING.Nope.name()))
							{
								return EVENTMARKING.Nope;
							}
							else
							{
								return EVENTMARKING.Maybe;
							}
						}
					}
					return null;
				}
				
			});
		}
	}


	private String getEventSource()
	{
		if (myEvent != null && myEvent.getCreator() != null)
		{
			if (myEvent.getCreator().equals(DataSources.EVENTBRITEPRIMARYLINK))
			{
				return EVENTBRITE;
			}
			else if (myEvent.getCreator().equals(DataSources.SEATTLELINKS))
			{
				return SEATTLEGOV;
			}
			else
			{
				return GOINGER;
			}
		}
		return null;
	}


	
	/**
	 * @param venue
	 * @param eventdetsLessaddressvenue
	 */
	private void setViewObserver(final TextView tne_textview) {
		ViewTreeObserver vto = tne_textview.getViewTreeObserver();
		vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			 @Override
		        public void onGlobalLayout() {
				if (checkEllipsized(tne_textview))
					{
					tne_textview.setEllipsize(TruncateAt.END);
						ImageButton more = setMoreButton(tne_textview);
						if (more != null)
							{
							setButtonListeners(tne_textview, more.getId());
							}
					}
			 }
			 
		  }
    );			
	}


	/**
	 * @param name
	 * @return
	 */
	private ImageButton setMoreButton(TextView the_textView) {
		if (the_textView != null)
		{
			ImageButton moreButton = null;
			int id = the_textView.getId();
			if (id == R.id.eventDets_categorydetails)
			{
				moreButton = (ImageButton) myDialog.findViewById(R.id.eventDets_moreCategoryDetails);
				moreButton.setVisibility(View.VISIBLE);
			}
			else if (id == R.id.eventDets_descdetails)
			{
				moreButton = (ImageButton) myDialog.findViewById(R.id.eventDets_moreDesc);
				moreButton.setVisibility(View.VISIBLE);
			}
			return moreButton;
		}
		return null;
	}

	/**
	 * @param more
	 * @param eventdetsLessname
	 */
	private void setButtonListeners(final TextView the_textview, int the_moreButtonId)
	{
		final ImageButton more = (ImageButton) myDialog.findViewById(the_moreButtonId);

		OnClickListener moreListener = null;
		
		if (the_moreButtonId == R.id.eventDets_moreDesc)
		{
			moreListener =new OnClickListener() {						 
				@Override
				public void onClick(View arg0) {
					new EventDescriptionDialog(myContext, myEvent).openDialog();
				}
			};
		}
		else 
		{		
			ImageButton less = (ImageButton) myDialog.findViewById(getLessButton(the_moreButtonId));
			moreListener = setListeners(more,less,the_textview);
		}
		
		more.setOnClickListener(moreListener);
		
		}
	
	private int getLessButton(int the_more)
	{
		/*if(the_more == R.id.eventDets_moreName)
		{
			 return R.id.eventDets_lessName;
		}*/
		if(the_more == R.id.eventDets_moreCategoryDetails)
		{
			 return R.id.eventDets_lessCategoryDetails;
		}
	  /*  else if (the_more == R.id.eventDets_moreAddressBldng)
	    {
			return R.id.eventDets_lessAddressBldng;
		}
		else if(the_more == R.id.eventDets_moreAddressVenue)
		{
			return R.id.eventDets_lessAddressVenue;
		}
		else if(the_more == R.id.eventDets_moreAddressStreet)
		{
			return R.id.eventDets_lessAddressStreet;
		}
		else if (the_more == R.id.eventDets_moreContactName)
		{
			return R.id.eventDets_lessContactName;
		}
		else if (the_more == R.id.eventDets_moreContactPosition)
		{
			return R.id.eventDets_lessContactPosition;
		}*/
		return 0;
	}
	private void setEllipsize(TextView the_textview) {
		if (the_textview != null)
		{
			the_textview.setSelected(true);
			the_textview.setEllipsize(TruncateAt.MARQUEE);
			the_textview.setHorizontallyScrolling(true);
			the_textview.setMarqueeRepeatLimit(-1);
		}
	}


	private OnClickListener setListeners(final ImageButton more, final ImageButton less, final TextView the_textview)
	{
		return new OnClickListener() {						 
			@Override
			public void onClick(View arg0) {
				more.setVisibility(View.GONE);
				setEllipsize(the_textview);
				less.setVisibility(View.VISIBLE);
				less.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View v) {
						less.setVisibility(View.GONE);
						more.setVisibility(View.VISIBLE);
						the_textview.setEllipsize(null);
					}
					
				});
			}
						};

	}
	/**
	 * @param name
	 * @return
	 */
	private boolean checkEllipsized(TextView the_textview) {
		Layout layout = the_textview.getLayout();
		if ( layout != null)
        {
            int lines = layout.getLineCount();
            if ( lines > 0)
            {
                if ( layout.getEllipsisCount(lines-1) > 0)
                {
                    return true;
                }
            }
        }
        return false;
       }

	private void acceptForecast(Object[] the_params) {
		if (the_params != null && the_params.length == 2){
			try{
			
			}catch(Exception e){
			}		
		}
	}

	private void isDeleted(Object[] the_params) {
		if (the_params != null && the_params.length > 0)
		{
			try{
				boolean deleted = (Boolean) the_params[0];
				if (deleted){
					if (mySetter != null && myDialog != null)
					{
						mySetter.removeEvent(myEvent);
						if (myDialog.isShowing())
						{
							myDialog.dismiss();
						}
					}
					else if (mySetter == null)
					{
						Utility.throwErrorMessage(myContext, "Cannot delete", "Event cannot be deleted from this activity!");
					}
				}
			}catch (Exception e){
			}
		}
	}

	private void acceptResults(Object[] the_params) {
		if (the_params != null && the_params.length > 0){
			try{
				User user = (User) the_params[0];
				if (user != null)
				{
					new UserStatsDialog(myContext, user).openDialog();
				}
				else
				{
					Toast.makeText(myContext.getApplicationContext(), NOUSERINFO, Toast.LENGTH_LONG).show();
				}		
			}catch(Exception e){
			}
		}
	}

	private void savedAck(Object[] the_params) {
		if (the_params != null && the_params.length == 3){
			try{
			EVENTMARKING the_ack = (EVENTMARKING) the_params[0];
			boolean saved = (Boolean) the_params[1];
			boolean wasIncremental = (Boolean) the_params[2];
			if (saved && myDialog != null)
			{
				updateEventField(the_ack, wasIncremental);
				if (wasIncremental)
				{
					modifyUIOnIncAck(the_ack);
				}
				else
				{
					modifyUIOnDecAck(the_ack);
				}
			}			
		}catch(Exception e){
		}
	  }
   }


	/**
	 * @param the_ack
	 * @param wasIncremental
	 */
	private void updateEventField(EVENTMARKING the_ack, boolean wasIncremental) 
	{
		if (EVENTMARKING.Going == the_ack)
		{
			int updatedCount = wasIncremental?(myEvent.getNumGoing() + 1):(myEvent.getNumGoing() - 1);
			myEvent.setNumGoing(updatedCount);
		}
		else if (EVENTMARKING.Nope == the_ack)
		{
			int updatedCount = wasIncremental?(myEvent.getNumNope() + 1):(myEvent.getNumNope() - 1);
			myEvent.setNumNope(updatedCount);
		}
		else if (EVENTMARKING.Maybe == the_ack)
		{
			int updatedCount = wasIncremental?(myEvent.getNumMaybe() + 1):(myEvent.getNumMaybe() - 1);
			myEvent.setNumMaybe(updatedCount);			
		}			
	}


	/**
	 * @param the_ack
	 */
	private void modifyUIOnDecAck(EVENTMARKING the_ack) 
	{
		if (EVENTMARKING.Going == the_ack)
		{
			setValueToTV((TextView)myDialog.findViewById(R.id.eventDets_goingvalue), String.valueOf(myEvent.getNumGoing()));
		}
		else if (EVENTMARKING.Nope == the_ack)
		{
			setValueToTV((TextView)myDialog.findViewById(R.id.eventDets_nopevalue), String.valueOf(myEvent.getNumNope()));
		}
		else if (EVENTMARKING.Maybe == the_ack)
		{
			setValueToTV((TextView)myDialog.findViewById(R.id.eventDets_maybevalue), String.valueOf(myEvent.getNumMaybe()));
		}			
		setAckLayout();
	}


	/**
	 * @param the_ack
	 */
	private void modifyUIOnIncAck(EVENTMARKING the_ack)
	{
		makeAckButtonsInvisible(the_ack);
		ImageView markedImg = (ImageView)myDialog.findViewById(R.id.eventDets_markedImg);
		TextView markedTxt = (TextView) myDialog.findViewById(R.id.eventDets_markedTextValue);
		
		if (EVENTMARKING.Going == the_ack)
		{
			setValueToTV((TextView)myDialog.findViewById(R.id.eventDets_goingvalue), String.valueOf(myEvent.getNumGoing()));
			setValueToIV(markedImg, R.drawable.yes);
			setValueToTV(markedTxt, " \'Going\'");
		}
		else if (EVENTMARKING.Nope == the_ack)
		{
			setValueToTV((TextView)myDialog.findViewById(R.id.eventDets_nopevalue), String.valueOf(myEvent.getNumNope()));
			setValueToTV(markedTxt, " \'Nope\'");
			setValueToIV(markedImg, R.drawable.no);
		}
		else if (EVENTMARKING.Maybe == the_ack)
		{
			setValueToTV((TextView)myDialog.findViewById(R.id.eventDets_maybevalue), String.valueOf(myEvent.getNumMaybe()));
			setValueToTV(markedTxt, " \'Maybe\'");
			setValueToIV(markedImg, R.drawable.maybe);
		}		
	}


	/**
	 * @param markedImg
	 * @param maybe
	 */
	private void setValueToIV(ImageView the_imgView, int the_img) 
	{
		if (the_imgView != null)
		{
			the_imgView.setImageResource(the_img);
		}
	}


	/**
	 * @param the_ack 
	 * 
	 */
	private void makeAckButtonsInvisible(final EVENTMARKING the_ack)
	{
		if (myDialog != null)
		{
			LinearLayout layout = (LinearLayout) myDialog.findViewById(R.id.eventDets_acklayout);
			if (layout != null)
			{
				layout.setVisibility(View.GONE);
				LinearLayout ackedLayout = (LinearLayout) myDialog.findViewById(R.id.eventDets_markedLayout);
				if (ackedLayout != null)
				{
					ackedLayout.setVisibility(View.VISIBLE);
					ackedLayout.setOnClickListener(new OnClickListener()
					{

						@Override
						public void onClick(View arg0) {
							confirmUndoAckDialog(the_ack);
						}
						
					});
				}
			}
		}
	}


	/**
	 * @param the_ack 
	 * 
	 */
	protected void confirmUndoAckDialog(final EVENTMARKING the_ack)
	{
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				myContext); 
			alertDialogBuilder.setTitle(CONFIRMACKUNDOTITLE);
			alertDialogBuilder
				.setMessage(CONFIRMACKUNDOMSG)
				.setCancelable(false)
				.setPositiveButton(YES,new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id)
					{
						AckEvent ack = new AckEvent(myContext, new Object[]{myEvent,the_ack, false});
						ack.registerListener(EventInformationDialog.this);
						ack.execute();
						dialog.dismiss();
					}
				  })
				.setNegativeButton(NO,new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
						dialog.cancel();
					}
				});
 				AlertDialog alertDialog = alertDialogBuilder.create();
 				alertDialog.show();
			}


	/* (non-Javadoc)
	 * @see com.bitBusy.going.webServices.awsSetup.AWSOperationListenerInterface#onOperationComplete(java.lang.Object[])
	 */
	@Override
	public void onOperationComplete(ListenerAck the_ack) {
		if (the_ack != null && the_ack.getClassName() != null && the_ack.getParams() != null){
		 String the_className = the_ack.getClassName();
		if (the_className.equals(AWSDelete.class.getName())){
			isDeleted(the_ack.getParams());
		}
		else if (the_className.equals(UserStatsQuerier.class.getName())){
			acceptResults(the_ack.getParams());
		}
		else if (the_className.equals(AckEvent.class.getName())){
			savedAck(the_ack.getParams());
		}
	}
 }


	/* (non-Javadoc)
	 * @see com.bitBusy.going.webServices.EventsData.WeatherForecastRequestor#acceptForecast(java.util.ArrayList)
	 */
	@Override
	public void acceptForecast(ArrayList<Forecast> the_forecast) {
		if (the_forecast != null && the_forecast.size() > 0)
		{
			new WeatherForecastDialog(myContext, the_forecast).openDialog();
		}
		else
		{
			Toast.makeText(myContext.getApplicationContext(), NOFORECAST, Toast.LENGTH_LONG).show();
		}		
	}

}