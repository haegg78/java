package com.eklind.dropboxSyncer;

import com.dropbox.core.*;
import java.io.*;
import java.util.Locale;


public class Authorize {
	private static DbxAppInfo appInfo() throws DbxException {
        final String APP_KEY = "q66p3ueua0sny1g";
        final String APP_SECRET = "sofxbwuenbl563n";
        DbxAppInfo appInfo = new DbxAppInfo(APP_KEY, APP_SECRET);		
		return appInfo;
	}
	private static String authenticate(DbxRequestConfig config) throws IOException, DbxException {
        DbxAppInfo appInfo = appInfo();
        DbxWebAuthNoRedirect webAuth = new DbxWebAuthNoRedirect(config, appInfo);
        // Have the user sign in and authorize your app.
        String authorizeUrl = webAuth.start();
        System.out.println("1. Go to: " + authorizeUrl);
        System.out.println("2. Click \"Allow\" (you might have to log in first)");
        System.out.println("3. Copy the authorization code.");
        String code = new BufferedReader(new InputStreamReader(System.in)).readLine().trim();

        // This will fail if the user enters an invalid authorization code.
        DbxAuthFinish authFinish = webAuth.finish(code);
        String accessToken = authFinish.accessToken;
        return accessToken;
	}
	public static DbxClient validate(String dropboxUser, Boolean authenticate) throws IOException, DbxException {
        DbxClient client = null;
        String line = "";
        System.out.println(System.getProperty("user.home"));
        String filePath = System.getProperty("user.home") + File.separator + ".portableSyncer/dbAuthorityFile";
        //String filePath = "/tmp/dbAuthorityFile";
        String accessToken = "";
        String [] accessTokens = null;
        Boolean entryFound = false;
        BufferedReader bf = null;
        FileWriter fw = null;
        DbxRequestConfig config = new DbxRequestConfig("JavaTutorial/1.0",
	            Locale.getDefault().toString());
        // Trying to find user using bf
        try
        {
        	if (authenticate)
        	{
        		// Force the authentication
            	accessToken = authenticate(config);
    	        // Store entry in the file, we also need to remove the old entry
    	        fw = new FileWriter(filePath, true);
    	        fw.write(dropboxUser + ";" + accessToken + "\r\n");
    	        fw.flush();
        	}
        	else
        	{
            	try 
            	{
            		bf = new BufferedReader(new FileReader(filePath));
            	}
            	catch (IOException ioe)
            	{
            		ioe.printStackTrace();
            		File f = new File(filePath);
            		f.getParentFile().mkdirs();
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
                	accessToken = authenticate(config);
        	        // Store entry in the file
        	        fw = new FileWriter(filePath, true);
        	        fw.write(dropboxUser + ";" + accessToken + "\r\n");
        	        fw.flush();
                }
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
        	if (fw != null)
        	{
        		fw.close();
        	}
        }
		return client;
	}

}
