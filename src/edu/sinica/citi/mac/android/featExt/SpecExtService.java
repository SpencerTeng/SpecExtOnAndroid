package edu.sinica.citi.mac.android.featExt;

import edu.sinica.citi.mac.SpecExtraction.SpecExtractor;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.view.Gravity;

import android.widget.Toast;

public class SpecExtService extends Service implements Runnable{

	


	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);
        CharSequence msg = "開始檢查更新...";
        Toast toast = Toast.makeText(this, msg, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER,0,0);
        
        toast.show();
        
        
        //create a thread to avoid ANR (application not respond exception)
		Thread curThread = new Thread(this);
		curThread.start();
		

		
	}



	@Override
	public void run() {
		// TODO Auto-generated method stub

		boolean isFinished = false;
		//String root = "/mnt/sdcard/";
		//String dirOut = ".MACdata/";
		
		String root = "/mnt/sdcard/";
		String dirOut = ".MACdataCompressed/";		
		
		String sProcess = root + "已更新之歌曲.txt";
		
		

		System.out.println("updating");
	
		
	

		
       SpecExtractor se = new SpecExtractor(root, dirOut, sProcess);
	
	
        long tElapse = se.run2();
		String text = "共耗時: "+ tElapse/1000 + "秒";	

      
		System.out.println("done!");
		isFinished = true;
		



			
		if (isFinished)
		{
		
			Looper.prepare();
			
			CharSequence msg = "已完成歌曲更新, " + text;
			Toast toast = Toast.makeText(this, msg, Toast.LENGTH_LONG);
	        toast.setGravity(Gravity.CENTER,0,0);
	        toast.show();
	        
	        Looper.loop();

		}
	}

}
