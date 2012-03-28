package edu.sinica.citi.mac.SpecExtraction;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javazoom.jl.converter.Converter;
import android.widget.Toast;



public class SpecExtractor {

	private List<String> listProcessed;
	static final int TARGET_SAMPLE_RATE = 22050;
	String root;
	String dirOut;
	String sProcess;

	public SpecExtractor()
	{
		/*//default values
		String root = "/sdcard/";
		String dirOut = "MACdata/";
		String sProcess = root + dirOut + "processedSongs.txt";
		*/
		this( "/sdcard/", "MACdata/","/sdcard/MACdata/processedSongs.txt");
		
		
	}

	public SpecExtractor(String root ,String dirOutRev , String sProcess)
	{
		this.root = root;
		this.dirOut = root + dirOutRev;
		this.sProcess = sProcess;
		 
		System.out.println("outputDir: "+ dirOut);
		File fOutputDir = new File(dirOut);
		if (!fOutputDir.exists())
		{
			System.out.println("outputDir isn't exist");
			fOutputDir.mkdirs();
		}

		File fProcess = new File(sProcess);
		if(!fProcess.exists())
		{
			System.out.println("creating process file... " + sProcess);
			try {
				FileWriter f_tmp = new FileWriter(fProcess);
				f_tmp.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("process file: "+ sProcess);
		try 
		{
			FileReader fr = new FileReader(sProcess);		
			BufferedReader br = new BufferedReader(fr);

			listProcessed = new ArrayList<String>();
			String item ;
			while((item = br.readLine()) != null)
			{

				listProcessed.add(item);
			}
			br.close();
		} 
		
		catch (FileNotFoundException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}



	public void storeDbMag(String fAudio, String sOutput, int FRAMESIZE)
	{		
		File audioFile = new File(fAudio);
		storeDbMag(audioFile, sOutput, FRAMESIZE);
	}

	

	
	public void storeDbMag(File fAudio, String sOutput, int FRAMESIZE)
	{		

		try
		{				
			
			FileWriter fw = new FileWriter(sOutput);
			PrintWriter out = new PrintWriter(fw);


			Converter jc = new Converter();
			
			String fName = fAudio.getAbsolutePath();

			jc.convert(fName, fName+".wav");
			
			File WF = new File(fName+".wav");
			// Open the wav file specified as the first argument
			WavFile wavFile = WavFile.openWavFile(WF);		
			// Display information about the wav file
		//	wavFile.display();
			// Create a buffer of 1024 frames
			int SampleRate = (int) wavFile.getSampleRate();
			int numChannels = wavFile.getNumChannels();
			int downSRfactor = SampleRate / TARGET_SAMPLE_RATE;
			

			double[] buffer = new double[FRAMESIZE];
			int framesRead;
			//here, we extract the first 30 seconds
			long ByteLimit = wavFile.getSampleRate() * numChannels * 30 ;
			long curByte = 0;
			do
			{
				// buffer contains mono, single channel signal
				if(wavFile.getNumChannels() > 1)
				{
					double[] buffer_tmp = new double[FRAMESIZE * numChannels];
					framesRead = wavFile.readFramesDownSampled(buffer_tmp, 0, FRAMESIZE * numChannels, downSRfactor);
					
					//convert to mono by summarizing them up				
					for(int i = 0,j=0;i<buffer.length && j< buffer_tmp.length - numChannels;i++,j+=numChannels)
					{
						buffer[i] = buffer_tmp[j] + buffer_tmp[j+numChannels-1];
					}
				}	
				
				else
				{
					// Read frames into buffer
					framesRead = wavFile.readFramesDownSampled(buffer, 0, FRAMESIZE * numChannels, downSRfactor);
				}
				
				curByte += framesRead;

				
				//fft is a complex, we need the magnitude, that is, abs(fft)
				FFT fft = new FFT(buffer,null,false,true);
				double[] mag_fft = fft.getMagnitudeSpectrum();

				//we need convert them to db scale
				for(double m:mag_fft)
				{
					double dbSpec =20*Math.log10(m + 2.2204e-16);
					float dbf = (float)dbSpec;

					out.append(Float.toString(dbf)+',');
					
				}
				out.append("\n");
				
			}
			while (framesRead != 0 && curByte<=ByteLimit);



			wavFile.close();


			out.flush();
			out.close();
			fw.close();
			

			WF.delete();
		}
		catch (Exception e)
		{
			System.err.println(e);
		}
	}
	
	
	
	private String setOutFilePath(String dirOut, String fileInput)
	{

		System.out.println("spec will be stored under: "+ dirOut);
		File f = new File(dirOut);
		if(!f.exists())
			f.mkdirs();
		
		String[] token = fileInput.split("\\.");

		// split the path to replace the extension to .spec 
		StringBuffer songName = new StringBuffer();

		for(int i = 0;i<token.length-1;i++)
		{

			//System.out.println("token"+i+": "+token[i]);
			songName.append(token[i] + '.');

		}

		songName.append("spec");
		// get its dir
		// reuse token array		
		token = fileInput.split("/");
		StringBuffer subDir = new StringBuffer();
		for (int i = 1;i<token.length-1;i++)
		{
			subDir.append(token[i] + "/");

		}


		//System.out.println("subDir: " + subDir);
		File tmp = new File(dirOut+ "/" + subDir );

		if (!tmp.exists()) 
		{
			System.out.println(tmp+" not exist!");
			tmp.mkdirs();
		}

		String output = dirOut + songName;
		System.out.println("output file: "+output);
		return output;
	}
	

	private void deleteDirectory(File path)
	{

	    if( path.exists() ) {
	        File[] files = path.listFiles();
	        for(int i=0; i<files.length; i++) {
	           if(files[i].isDirectory()) {
	             deleteDirectory(files[i]);
	           }
	           else {
	             files[i].delete();
	           }
	        }
	      }
	      
	    


	}


	private void test(String sOutput)
	{
		File f = new File(sOutput);
		try {
			f.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public long run2() 
	{

		//System.out.println("here!");

		System.out.println("list size: "+listProcessed.size());
		int FRAMESIZE = 1024;
		//spectrum will be stored under here, and be deleted after zipped
		String tmpFiles = root+"tmp/";
		boolean isAnyNew = false;
		long tStart = System.currentTimeMillis();
	
		//System.out.println("root: " + root);
		//root: "/sdcard/"
	
		List<File> filelist = FileHelper.fetchFileList(root, "mp3");
		String[] fileName = new String[filelist.size()];
		int idx = 0;

		File fProcess = new File(sProcess);
		if(!fProcess.exists())
		{			
			System.out.println("sProcess isn't exist ");
			try {
				fProcess.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		FileWriter fwProc;
		try {
			
			fwProc = new FileWriter(fProcess);
			PrintWriter outProc = new PrintWriter(fwProc);
			

			for(Iterator<String> itr2 = listProcessed.iterator();itr2.hasNext();)
			{
				String s = itr2.next();
				//System.out.println("Items in processed list: " + s);				
				outProc.append(s+"\n");
			}

			for (Iterator<File> itr = filelist.iterator();itr.hasNext();)
			{
				File f = itr.next();
				
				String songPath = f.getAbsolutePath();
				//System.out.println("file in lists: " + f.getPath());	

				
				String sOutput = setOutFilePath(tmpFiles,songPath);
		//		System.out.println("listProcessed: "+listProcessed.indexOf(songPath) );
				
				
				
				if(listProcessed.indexOf(songPath) < 0)
				//if(true)				
				{	
					isAnyNew = true;
					outProc.append(songPath+ "\n");
					System.out.println("add " + songPath);
					fileName[idx++] = sOutput;
					
					long tEach = System.currentTimeMillis();
					System.out.println("extracting spectrum...");
					//you could either input File or Sting of audio file
					storeDbMag(f, sOutput, FRAMESIZE);	
					//test(sOutput);
					
					long tEachEnd = System.currentTimeMillis();
					System.out.println("Time consumed: " + (tEachEnd - tEach) + "msec.");
				}
				
				
			}

			outProc.flush();				
			outProc.close();
			fwProc.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (isAnyNew)		
		{
			String outZipName = dirOut+"MACdata_to" + (listProcessed.size()+idx) + ".zip";
			System.out.println("outZipName: " + outZipName);

			AppZip az = new AppZip(tmpFiles, outZipName);
			az.zipIt();
		}
		//Compress cp = new Compress(fileName, outZipName );
		//cp.zip();

		System.out.println("I'm gonna delete" + tmpFiles);
		deleteDirectory(new File(tmpFiles));
		///ttt(tmpFiles);
		long tEnd = System.currentTimeMillis();
		System.out.println("Number of songs: " + filelist.size());
		System.out.println("Total time consumed: " + (tEnd - tStart) + "msec.");

		return (tEnd - tStart) ;
	}

}

