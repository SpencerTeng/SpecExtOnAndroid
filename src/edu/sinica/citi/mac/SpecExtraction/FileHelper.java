package edu.sinica.citi.mac.SpecExtraction;

import java.io.File;

import java.util.ArrayList;

import java.util.List;

//import android.util.Log;


//import android.util.Log;


public class FileHelper

{

	public static List<File> fetchFileList(String directoryName, final String fileExtension)

	{


		List<File> fileList = new ArrayList<File>();

		File fileDirectory = new File(directoryName);

		
		
		getDirectoryContent(fileList, fileDirectory, fileExtension);

		return fileList;

	}


	private static void getDirectoryContent(List<File> fileList, File fileDirectory, String fileExtension)

	{

		if (fileDirectory.exists())
		{
			/*//for testing
			System.out.println("file dir: "+fileDirectory);
			if(fileDirectory.isDirectory())
			{
				System.out.println(fileDirectory + " is a directory!");

			}
			*/
			try
			{
				for (File file : fileDirectory.listFiles())

				{

					//Log.i("FileHelper processing file:", file.getAbsolutePath());

					if (file.isDirectory())

						getDirectoryContent(fileList, file, fileExtension);

					else

					{

						String fileName = file.getName();

						if (fileName.endsWith(fileExtension.toLowerCase()) || fileName.endsWith(fileExtension.toUpperCase()))

							fileList.add(file);

					}

				}
			}
			catch(Exception e)
			{
				System.out.println("error at: " + fileDirectory);
			}
			
		}
	}

}