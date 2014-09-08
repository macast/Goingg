/**
 * 
 */
package com.bitBusy.goingg.adapters;

import java.util.ArrayList;
import java.util.Locale;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.bitBusy.goingg.alarmSystem.EventReminder;
import com.bitBusy.goingg.alarmSystem.LocalAlarmManager;
import com.bitBusy.goingg.dialog.EventInformationDialog;
import com.bitBusy.goingg.dialog.EventQuerySearchDialog;
import com.bitBusy.goingg.dialog.messaging.FrndReqMsgDetailDialog;
import com.bitBusy.goingg.events.DataSources;
import com.bitBusy.goingg.events.EventCategory;
import com.bitBusy.goingg.events.PublicEvent;
import com.bitBusy.goingg.events.PrivateEvents.PrivateEventUser;
import com.bitBusy.goingg.mapDisplay.EventsDisplaySetter;
import com.bitBusy.goingg.messaging.setup.gcmSetup.GCMMessage;
import com.bitBusy.goingg.messaging.setup.gcmSetup.GCMMessage.GCMMessageType;
import com.bitBusy.goingg.utility.Encryption;
import com.bitBusy.goingg.utility.Utility;
import com.bitBusy.goingg.weather.Forecast;
import com.bitBusy.goingg.R;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

/**
 * @author SumaHarsha
 *
 */
public class ListAdapter extends BaseAdapter{
			
	public static final String ROUNDTOTWODECIMAL = "%.2f";
	
	/** list to be made into view*/
	private ArrayList<?> myList;
	
	/** context*/
	private Context myContext;
	
	/** flag to indicate what list this will be*/
	private boolean isDirection = false;
	
	/** is event*/
	private boolean isEventWithDistance = false;
	
	/** is reminder*/
	private boolean isReminder = false;
	
	private boolean isForecast = false;
	
	private boolean isLegend = false;
	
	private boolean isContact = false;
	
	private boolean isMessage = false;
	
	private int myLastPosition = -1;
	
	private EventsDisplaySetter mySetter;
	/** parameterized constructor*/
	public ListAdapter(Context the_context, ArrayList<?> the_list, EventsDisplaySetter the_setter)
	{
		myList = the_list;
		myContext = the_context;
		mySetter =  the_setter;
	}
	
	/** returns count
	 * @return int
	 */
	public int getCount() {
		return (myList==null?0:myList.size());
	}

	public Object getItem(int position) {
	return (myList != null && myList.size() > position)?myList.get(position):null;
	}

	
	public long getItemId(int position) {
			return position;
	}

	public View getView(final int the_position, View the_convertView, ViewGroup the_parent) {
		 ViewHolder viewholder = null;
		   if(myList != null) {
			   if (the_convertView == null)
			   {
				   viewholder = new ViewHolder();
				   the_convertView = new View(myContext);
				   LayoutInflater inf = (LayoutInflater) myContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				  Object sampleItem = getItem(0);
				  if (sampleItem instanceof String)
				  {
					  the_convertView = inf.inflate(R.layout.listview_directions_item, null);
					  isDirection = true;
					  viewholder.myFirstTV = (TextView) the_convertView.findViewById(R.id.listview_directionsitem_text);
				  }
				  else if (sampleItem instanceof Forecast)
				  {
					  the_convertView = inf.inflate(R.layout.weather_forecast_element, null);
					  isForecast = true;
					  viewholder.myFirstTV = (TextView) the_convertView.findViewById(R.id.weather_forecast_element_datevalue);
				  	  viewholder.mySecondTV = (TextView) the_convertView.findViewById(R.id.weather_forecast_element_desctext);
				  	  viewholder.myThirdTV = (TextView) the_convertView.findViewById(R.id.weather_forecast_element_highvalue);
				  	  viewholder.myFourthTV = (TextView) the_convertView.findViewById(R.id.weather_forecast_element_lowvalue);
				  	  viewholder.myImageView = (ImageView) the_convertView.findViewById(R.id.weather_forecast_element_descimg);
				  	  viewholder.myToggleButton = (ToggleButton) the_convertView.findViewById(R.id.weather_forecast_element_tempunitbutton);
				  }
				  else if (sampleItem instanceof EventWithDistance)	
				  {
					  myLastPosition = -1;
					  the_convertView = inf.inflate(R.layout.event_element, null);
					  isEventWithDistance = true;
					  viewholder.myFirstTV = (TextView) the_convertView.findViewById(R.id.event_element_eventname);
				  	  viewholder.mySecondTV = (TextView) the_convertView.findViewById(R.id.event_element_startsvalue);
				  	  viewholder.myThirdTV = (TextView) the_convertView.findViewById(R.id.event_element_endsvalue);
				  	  viewholder.myFourthTV = (TextView) the_convertView.findViewById(R.id.event_element_categoryvalue);
				  	  viewholder.myFifthTV = (TextView) the_convertView.findViewById(R.id.event_element_distancevalue);
				  	  viewholder.mySixthTV = (TextView) the_convertView.findViewById(R.id.event_element_startsprompt);
				  	  viewholder.mySeventhTV = (TextView) the_convertView.findViewById(R.id.event_element_endsprompt);
				  	  viewholder.myImageView = (ImageView) the_convertView.findViewById(R.id.event_element_img);
				  	  viewholder.myRatingBar = (RatingBar) the_convertView.findViewById(R.id.event_element_ratingBar);
				  }
				  else if (sampleItem instanceof EventReminder)
				  {
					  the_convertView = inf.inflate(R.layout.reminder_element, null);
					  isReminder = true;
					  viewholder.myFirstTV = (TextView) the_convertView.findViewById(R.id.reminder_element_datetime);
					  viewholder.myFirstIB = (ImageButton) the_convertView.findViewById(R.id.reminder_element_delete);
					  viewholder.mySecondTV = (TextView) the_convertView.findViewById(R.id.reminder_element_eventname);
					  viewholder.myThirdTV = (TextView) the_convertView.findViewById(R.id.reminder_element_startsvalue);
				  }
				  else if (sampleItem instanceof EventCategory)
				  {
					  the_convertView = inf.inflate(R.layout.image_with_text, null);
					  isLegend = true;
					  viewholder.myFirstTV = (TextView) the_convertView.findViewById(R.id.image_with_text_text);
					  viewholder.myImageView = (ImageView) the_convertView.findViewById(R.id.image_with_text_image);
				  }
				  else if (sampleItem instanceof PrivateEventUser)
				  {
					  the_convertView = inf.inflate(R.layout.listview_contact_item, null);
					  isContact = true;
					  viewholder.myFirstTV = (TextView) the_convertView.findViewById(R.id.listview_contact_item_name);
					  viewholder.mySecondTV = (TextView) the_convertView.findViewById(R.id.listview_contact_item_city);
				  }
				  else if (sampleItem instanceof GCMMessage){
					  the_convertView = inf.inflate(R.layout.message_element, null);
					  isMessage = true;
					  viewholder.myFirstTV = (TextView) the_convertView.findViewById(R.id.message_element_msgtype);
					  viewholder.mySecondTV = (TextView) the_convertView.findViewById(R.id.message_element_from);
					  viewholder.myThirdTV = (TextView) the_convertView.findViewById(R.id.message_element_date);
				  }
				  the_convertView.setTag(viewholder);
			   }
			   else
			   {
				   viewholder = (ViewHolder) the_convertView.getTag();
			   }
		  	Object item = getItem(the_position);
		  	if (isDirection)
		  	{	
		  		if (item != null)
		  		{
		  			viewholder.myFirstTV.setText(String.valueOf(the_position + 1).concat(". ").concat(item.toString()));
		  		}
		  	}

		  	else if (isContact)
		  	{	
		  		final PrivateEventUser friend = (PrivateEventUser) item;
		  		if (friend != null && friend.getName() != null && friend.getName().getFirstName() != null &&
		  				friend.getName().getLastName() != null && friend.getCity() != null && friend.getCity().getName() != null)
		  		{
		  			viewholder.myFirstTV.setText(Encryption.decryptTwoWay(myContext, friend.getName().getFirstName(), 
		  					friend.getUserName()).concat(" ").concat(
		  							Encryption.decryptTwoWay(myContext,friend.getName().getLastName(), friend.getUserName())));
		  			viewholder.mySecondTV.setText(friend.getCity().getName());
		  		}
		  	}
		  	else if (isMessage){
		  		final GCMMessage msg = (GCMMessage) item;
		  		Log.i(this.getClass().getSimpleName(), "displaying.." + msg);
		  		if (msg != null && msg.getSender() != null && msg.getSender().getName() != null &&
		  				msg.getMessageType() != null && msg.getRxDateTime() != null){
		  			String senderName = (msg.getSender().getName().getFirstName()!= null?msg.getSender().getName().getFirstName():"")+ " " + 
		  					((msg.getSender().getName().getLastName()!=null?
		  						msg.getSender().getName().getLastName():""));
		  			viewholder.myFirstTV.setText(Utility.mapMsgTypeToStr(msg.getMessageType()));
		  			viewholder.mySecondTV.setText(senderName);
		  			viewholder.myThirdTV.setText(msg.getRxDateTime());
			  		the_convertView.setOnClickListener(new OnClickListener()
			  		{
						@Override
						public void onClick(View arg0) {
							if (msg.getMessageType() == GCMMessageType.FriendRequest){
								new FrndReqMsgDetailDialog(myContext, ListAdapter.this, myList, the_position).openDialog();
							}
						}
			  		});
		  		}
		  	}
		  	else if (isForecast)
		  	{
		  		final Forecast forecast = (Forecast) item;
		  		if (forecast != null)
		  		{
		  			final TextView maxT = viewholder.myThirdTV;
		  			final TextView minT = viewholder.myFourthTV;
			  		viewholder.myFirstTV.setText(forecast.getDate());
			  		viewholder.mySecondTV.setText(forecast.getWeatherDesc());
			  		maxT.setText(forecast.getMaxTemp());
			  		minT.setText(forecast.getMinTemp());
			  		viewholder.myImageView.setImageBitmap(forecast.getImg());
			  		viewholder.myToggleButton.setOnCheckedChangeListener(new OnCheckedChangeListener()
			  		{

						@Override
						public void onCheckedChanged(CompoundButton arg0,
								boolean isChecked) {
							try
							{
								String max = forecast.getMaxTemp();
								String min = forecast.getMinTemp();
								if (!isChecked)
								{
									double maxVal = Double.valueOf(max);
									double minVal = Double.valueOf(min);
									maxVal = convertFtoC(maxVal);
									minVal = convertFtoC(minVal);
									max = String.format(Locale.ENGLISH, ROUNDTOTWODECIMAL, maxVal);
									min = String.format(Locale.ENGLISH, ROUNDTOTWODECIMAL, minVal);
								}
								maxT.setText(max);
							  	minT.setText(min);
							}
							catch(Exception e)
							{
								Toast.makeText(myContext.getApplicationContext(), "Error converting temperature!", Toast.LENGTH_LONG).show();
							}
						}

						private double convertFtoC(double the_val) {
							the_val = (the_val - 32) * 5/9;
							return the_val;
						}
			  			
			  		});
		  		}
		  	}
		  	else if (isEventWithDistance)
		  	{
		  		final EventWithDistance eventwithdist = (EventWithDistance) item;
		  		if (eventwithdist != null)
		  		{
		  			final PublicEvent event = eventwithdist.getEvent();
			  		if (event != null)
			  		{
				  		viewholder.myFirstTV.setText(event.getName());
				  		viewholder.myFourthTV.setText(event.getCategory().getName().toString());
				  		viewholder.myFifthTV.setText(eventwithdist.getDistance());
				  		if (event.getImage() != null)
				  		{
				  			viewholder.myImageView.setImageBitmap(event.getImage());
				  		}
				  		else
				  		{
				  			viewholder.myImageView.setImageResource(getImageResource(event.getCategory()));
				  		}
				  		if (!event.isGooglePlace())
				  		{
				  			viewholder.mySecondTV.setVisibility(View.VISIBLE);
				  			viewholder.mySecondTV.setText(Utility.getDateTime(event.getStartDateTime()));
					  		viewholder.myThirdTV.setText(Utility.getDateTime(event.getEndDateTime()));
					  		viewholder.myThirdTV.setVisibility(View.VISIBLE);
					  		viewholder.mySixthTV.setText("Starts: ");
					  		viewholder.mySeventhTV.setVisibility(View.VISIBLE);	
				  			viewholder.myRatingBar.setVisibility(View.GONE);  	
				  		}
				  		else
				  		{
				  			viewholder.mySixthTV.setText("Google rating:");
				  			viewholder.myRatingBar.setVisibility(View.VISIBLE);
				  			viewholder.myRatingBar.setOnTouchListener(new OnTouchListener() {
				  				public boolean onTouch(View v, MotionEvent event) {
				  	            return true;
				  	        }
				  			});
				  			viewholder.myRatingBar.setFocusable(false);
				  			viewholder.myRatingBar.setRating(getEventRating(event));
				  			viewholder.mySecondTV.setVisibility(View.INVISIBLE);
					  		viewholder.myThirdTV.setVisibility(View.GONE);
					  		viewholder.mySeventhTV.setVisibility(View.GONE);					  		
				  		}
				  		the_convertView.setOnClickListener(new OnClickListener()
				  		{
							@Override
							public void onClick(View arg0) {
								if(event.isGooglePlace())
								{
									new EventQuerySearchDialog(myContext, event).openDetailsDialog();
								}
								else
								{
									new EventInformationDialog(myContext, event, mySetter).openDetailsDialog();
								}
							}
				  			
				  		});
			  		Animation animation = AnimationUtils.loadAnimation(myContext, (the_position > myLastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
			  	    the_convertView.startAnimation(animation);
			  	    myLastPosition = the_position;
		  		}
		  		}
		   }
		  	else if(isLegend)
		  	{
		  		final EventCategory category = (EventCategory) item;
		  		setLegendRow(viewholder, category);
		  	}
		  	else if(isReminder)
		  	{
		  		final EventReminder reminder = (EventReminder) item;
		  		if (reminder != null)
		  		{
		  			final PublicEvent event = reminder.getEvent();
		  			viewholder.myFirstTV.setText(Utility.getDateTime(reminder.getDateTime()));
		  			viewholder.myFirstIB.setOnClickListener(new OnClickListener()
		  			{
						@Override
						public void onClick(View arg0) {
							showConfirmDeleteDialog();
						}

						private void showConfirmDeleteDialog() {
							AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(myContext);					 					 
								// set dialog message
								alertDialogBuilder
									.setMessage("Delete reminder?")
									.setCancelable(false)
									.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface dialog,int id) {
											if(LocalAlarmManager.deleteAlarm(reminder))
											{
												myList.remove(the_position);
												notifyDataSetChanged();
											}
										}
									  })
									.setNegativeButton("No",new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface dialog,int id) {
											dialog.dismiss();
										}
									});
					 
									// create alert dialog
									AlertDialog alertDialog = alertDialogBuilder.create();
									alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
									// show it
									alertDialog.show();							
						}
		  				
		  			});					
		  			if (event != null)
		  			{
		  				viewholder.mySecondTV.setText(event.getName());
		  				viewholder.myThirdTV.setText(Utility.getDateTime(event.getStartDateTime()));
		  				the_convertView.setOnClickListener(new OnClickListener()
		  				{

							@Override
							public void onClick(View arg0) {
								if (event.isGooglePlace())
								{
									new EventQuerySearchDialog(myContext,event).openDetailsDialog();
								}
								else
								{
									new EventInformationDialog(myContext, event, mySetter).openDetailsDialog();
								}								
							}
		  					
		  				});
		  			}
		  		}
		  	}	  	
		   }
			return the_convertView;
	}

	private float getEventRating(PublicEvent the_event) {
		if (the_event != null)
		{
			String rating = the_event.getRating();
			if (rating != null)
			{
				try
				{
					float floatRat = Float.parseFloat(rating);
					return floatRat;
				}
				catch(Exception e)
				{
					
				}
			}
		}
		return 0;
	}

	/**
	 * @param viewholder
	 * @param category
	 */
	private void setLegendRow(ViewHolder the_viewholder, EventCategory the_category) {
 		if (the_category != null)
  		{
  			the_viewholder.myFirstTV.setText(the_category.getName().toString());
  			if (BitmapDescriptorFactory.HUE_AZURE == the_category.getColor())
  			{
  				the_viewholder.myImageView.setBackgroundColor(myContext.getResources().getColor(R.color.hueazure));
  			}
  			else if (BitmapDescriptorFactory.HUE_BLUE == the_category.getColor())
  			{
  				the_viewholder.myImageView.setBackgroundColor(myContext.getResources().getColor(R.color.hueblue));		  			
  			}
  			else if(BitmapDescriptorFactory.HUE_CYAN == the_category.getColor())
  			{
  				the_viewholder.myImageView.setBackgroundColor(myContext.getResources().getColor(R.color.huecyan));		  			
  			}
  			else if (BitmapDescriptorFactory.HUE_GREEN == the_category.getColor())
  			{
  				the_viewholder.myImageView.setBackgroundColor(myContext.getResources().getColor(R.color.huegreen));
  			}
  			else if (BitmapDescriptorFactory.HUE_MAGENTA == the_category.getColor())
  			{
  				the_viewholder.myImageView.setBackgroundColor(myContext.getResources().getColor(R.color.huemagenta));
  			}
  			else if (BitmapDescriptorFactory.HUE_ORANGE == the_category.getColor())
  			{
  				the_viewholder.myImageView.setBackgroundColor(myContext.getResources().getColor(R.color.hueorange));
  			}
  			else if (BitmapDescriptorFactory.HUE_RED == the_category.getColor())
  			{
  				the_viewholder.myImageView.setBackgroundColor(myContext.getResources().getColor(R.color.huered));
  			}
  			else if (BitmapDescriptorFactory.HUE_ROSE == the_category.getColor())
  			{
  				the_viewholder.myImageView.setBackgroundColor(myContext.getResources().getColor(R.color.huerose));
  			}
  			else if (BitmapDescriptorFactory.HUE_YELLOW == the_category.getColor())
  			{
  				the_viewholder.myImageView.setBackgroundColor(myContext.getResources().getColor(R.color.hueyellow));
  			}
  			else if (BitmapDescriptorFactory.HUE_VIOLET == the_category.getColor())
  			{
  				the_viewholder.myImageView.setBackgroundColor(myContext.getResources().getColor(R.color.hueviolet));
  			}
  		}	
	}

	/**
	 * @param category
	 * @return
	 */
	private int getImageResource(EventCategory the_category) {
		if (the_category != null)
		{
			DataSources.Categories name = the_category.getName();
			if (name != null)
			{
				if (name.equals(DataSources.Categories.Academic))
				{
					return R.drawable.academic;
				}
				else if(name.equals(DataSources.Categories.Business))
				{
					return R.drawable.business;
				}
				else if(name.equals(DataSources.Categories.Community))
				{
					return R.drawable.community;
				}
				else if (name.equals(DataSources.Categories.Entertainment))
				{
					return R.drawable.entertainment;
				}
				else if (name.equals(DataSources.Categories.Family))
				{
					return R.drawable.family;
				}
				else if (name.equals(DataSources.Categories.Movies))
				{
					return R.drawable.cinema;
				}
				else if (name.equals(DataSources.Categories.Other))
				{
					return R.drawable.other;
				}
				else if (name.equals(DataSources.Categories.Pets))
				{
					return R.drawable.pets;
				}
				else if(name.equals(DataSources.Categories.Recreation))
				{
					return R.drawable.recreation;
				}
				else if(name.equals(DataSources.Categories.Sports))
				{
					return R.drawable.sports;
				}
			}
		}
		return 0;
	}


	class ViewHolder
	{
		TextView myFirstTV;
		TextView mySecondTV;
		TextView myThirdTV;
		TextView myFourthTV;
		TextView myFifthTV;
		TextView mySixthTV;
		TextView mySeventhTV;
		ImageView myImageView;
		ImageButton myFirstIB;
		ImageButton mySecondIB;
		ToggleButton myToggleButton;
		RatingBar myRatingBar;
	}

}
		  
