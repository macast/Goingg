/**
 * 
 */
package com.bitBusy.goingg.dialog;

import android.app.Dialog;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.bitBusy.goingg.events.PublicEvent;
import com.bitBusy.goingg.R;

/**
 * @author SumaHarsha
 *
 */
public class EventDescriptionDialog {

	private Context myContext;
	
	private PublicEvent myEvent;
	
	public EventDescriptionDialog(Context the_context, PublicEvent the_event)
	{
		myContext = the_context;
		myEvent = the_event;
	}
	
	
	public void openDialog()
	{
		if (myContext != null && myEvent != null)
		{
			Dialog detailsDialog = new Dialog(myContext);
			detailsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		    LayoutInflater inflater = (LayoutInflater) myContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		    
		    // Inflate and set the layout for the dialog
		    // Pass null as the parent view because its going in the dialog layout
			detailsDialog.setContentView(inflater.inflate(R.layout.event_description_dialog, null));
			setDescription(detailsDialog);
			setOKButton(detailsDialog);
			detailsDialog.show();
		}
	}
	
	private void setDescription(Dialog the_dialog)
	{
		if (the_dialog != null)
		{
			TextView titleTV = (TextView) the_dialog.findViewById(R.id.event_desc_titleText);
			if (titleTV != null)
			{
				titleTV.setText(myEvent.getName());
			}
			TextView descTV = (TextView) the_dialog.findViewById(R.id.event_desc_text);
			if (descTV != null)
			{
				descTV.setText(Html.fromHtml(myEvent.getDescription()));
			}
		}
	}
	
	private void setOKButton(final Dialog the_dialog)
	{
		if (the_dialog != null)
		{
			Button okButton = (Button) the_dialog.findViewById(R.id.event_desc_ok);
			okButton.setOnClickListener(new OnClickListener() {						 
				@Override
				public void onClick(View the_view) {
					the_dialog.dismiss();
				}
			});
		}
	}
	
}
