package edu.sinica.citi.mac.android.featExt;


import java.util.Calendar;

import mac.citi.sinica.android.featExt.R;



import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;


public class SpecExtActivity extends Activity {
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		runService();
	}
	
	private void runService()
	{
        Calendar cal= Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE,59);
        cal.set(Calendar.SECOND, 00);
        
        Intent intent=  new Intent(this, SpecExtService.class);
        PendingIntent pIntent = PendingIntent.getService(this,0, intent, 0);
        
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), 2*24*60*60*1000, pIntent);
        //every two days
	}


}