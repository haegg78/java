package com.eklind.dropboxSyncer;

import com.dropbox.core.*;
import java.io.*;
import java.util.Locale;

import com.dropbox.core.*;

public class Authorize {
	public static DbxClient validate(String dropboxUser) throws IOException, DbxException {
        final String APP_KEY = "q66p3ueua0sny1g";
        final String APP_SECRET = "sofxbwuenbl563n";
        DbxClient client = null;
        String line = "";
        String filePath = "/tmp/dbAuthorityFile";
        String accessToken = "";
        String [] accessTokens = null;
        Boolean entryFound = false;
        BufferedReader bf = null;
        DbxRequestConfig config = new DbxRequestConfig("JavaTutorial/1.0",
	            Locale.getDefault().toString());
        // Trying to find user using bf
        try
        {
        	try 
        	{
        		bf = new BufferedReader(new FileReader(filePath));
        	}
        	catch (IOException ioe)
        	{
        		ioe.printStackTrace();
        		File f = new File(filePath);
        		f.createNewFile();
        		bf = new BufferedReader(new FileReader(filePath));
        	}
            while ((line = bf.readLine()) != null && (!entryFound))
            {
            	if (line.contains(dropboxUser))
            	{
            		// Get the accessToken
            		System.out.println("line: " + line);
            		accessTokens = line.split(";");
            		System.out.println("accessTokens size: " + accessTokens.length);
            		accessToken = accessTokens[1];
            		entryFound = true;
            	}
            }
            // Close bf
            bf.close();
            // Check if token found
            if (!entryFound)
            {
    	        DbxAppInfo appInfo = new DbxAppInfo(APP_KEY, APP_SECRET);
    	        DbxWebAuthNoRedirect webAuth = new DbxWebAuthNoRedirect(config, appInfo);
    	        // Have the user sign in and authorize your app.
    	        String authorizeUrl = webAuth.start();
    	        System.out.println("1. Go to: " + authorizeUrl);
    	        System.out.println("2. Click \"Allow\" (you might have to log in first)");
    	        System.out.println("3. Copy the authorization code.");
    	        String code = new BufferedReader(new InputStreamReader(System.in)).readLine().trim();

    	        // This will fail if the user enters an invalid authorization code.
    	        DbxAuthFinish authFinish = webAuth.finish(code);
    	        accessToken = authFinish.accessToken;
            }
            // Connect to dropBox
	        //System.out.println("accessToken: " + accessToken);
	        client = new DbxClient(config, accessToken);
        }
        catch (Exception e)
        {
        	e.printStackTrace();
        }
        finally
        {
        	
        }
		return client;
	}

}
