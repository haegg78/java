package com.eklind.dropboxSyncer;

import com.dropbox.core.*;
import com.sun.org.apache.xml.internal.security.exceptions.Base64DecodingException;
import com.sun.org.apache.xml.internal.security.utils.Base64;

import java.io.*;
import java.sql.*;
import java.util.Arrays;
import java.util.Locale;

import org.sqlite.*;


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
	private static void checkDB(Connection conn, String dropboxUser) throws SQLException{
		Boolean tableExist = false;
		ResultSet rs = null;
		Statement stmt = conn.createStatement();
		String sql = "SELECT ACCESSTOKEN FROM DROPBOXACCESSTOKENS where user='" + dropboxUser + "';";
		try
		{
			conn.setAutoCommit(false);
			rs = stmt.executeQuery(sql);
			tableExist = true;
		}
		catch (SQLException sqle)
		{
			if (sqle.getMessage().contains("no such table: DROPBOXACCESSTOKENS"))
			{
				System.out.println("Inside table not found");
				sqle.printStackTrace();
				sql = "CREATE TABLE DROPBOXACCESSTOKENS "
						+ "(USER TEXT PRIMARY KEY NOT NULL,"
						+ "ACCESSTOKEN TEXT NOT NULL)";
				stmt.executeUpdate(sql);
				conn.commit();
			}
		}
		finally
		{
			if (rs != null)
			{
				rs.close();
			}
			if (stmt != null)
			{
				stmt.close();
			}
		}
	}
	private static String getAccessToken(Connection conn, String dropboxUser) throws Base64DecodingException, SQLException {
		String accessToken = "";
		ResultSet rs = null;
		Statement stmt = conn.createStatement();
		String sql = "SELECT ACCESSTOKEN FROM DROPBOXACCESSTOKENS WHERE USER='" + dropboxUser + "';";
		try
		{
			rs = stmt.executeQuery(sql);
			while (rs.next())
			{
				accessToken = rs.getString(1);
				accessToken = new String(Base64.decode(accessToken));
				System.out.println("Returning accessToken: " + accessToken);
			}
		}
		catch (SQLException sqle)
		{
			sqle.printStackTrace();
		}
		finally
		{
			if (rs != null)
			{
				rs.close();
			}
			if (stmt != null)
			{
				stmt.close();
			}
		}
		return accessToken;
	}
	private static void insertUpdateAccessToken(Connection conn, String dropboxUser, String accessToken, Boolean authenticate) throws SQLException {
		Statement stmt = conn.createStatement();
		String sql = "";
		try
		{
			conn.setAutoCommit(false);
			if (authenticate)
			{
				accessToken = Base64.encode(accessToken.getBytes());
				// Update the already existing entry
				sql = "UPDATE DROPBOXACCESSTOKENS set ACCESSTOKEN='" + accessToken + "' WHERE USER='" + dropboxUser + "';";
				stmt.executeUpdate(sql);
				conn.commit();
			}
			else
			{
				accessToken = Base64.encode(accessToken.getBytes());
				// Normal handling of just inserting the entry
				sql = "INSERT INTO DROPBOXACCESSTOKENS VALUES('" + dropboxUser + "','" + accessToken + "');";
				stmt.executeUpdate(sql);
				conn.commit();
			}
		}
		catch (SQLException sqle)
		{
			sqle.printStackTrace();
		}
		finally
		{
			if (stmt != null)
			{
				stmt.close();
			}
		}
	}
	public static DbxClient validate(String dropboxUser, Boolean authenticate) throws ClassNotFoundException, SQLException, IOException, DbxException {
        DbxClient client = null;
        String line = "";
        System.out.println(System.getProperty("user.home"));
        String dbPath = System.getProperty("user.home") + File.separator + ".portableSyncer/dbAuthorityFile.db";
        String accessToken = "";
        String [] accessTokens = null;
        Boolean entryFound = false;
        BufferedReader bf = null;
        Connection conn = null;
        DbxRequestConfig config = new DbxRequestConfig("JavaTutorial/1.0",
	            Locale.getDefault().toString());
        // Trying to find user using bf
        try
        {
        	Class.forName("org.sqlite.JDBC");
        	conn = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
        	checkDB(conn, dropboxUser);
        	if (authenticate)
        	{
        		// Force the authentication
            	accessToken = authenticate(config);
    	        // Store entry in the file, we also need to remove the old entry
            	insertUpdateAccessToken(conn, dropboxUser, accessToken, authenticate);
        	}
        	else
        	{
            	accessToken = getAccessToken(conn, dropboxUser);
            	if (!accessToken.equals(""))
            	{
            		entryFound = true;
            	}
                // Check if token found
                if (!entryFound)
                {
                	accessToken = authenticate(config);
        	        // Store entry in db
                	insertUpdateAccessToken(conn, dropboxUser, accessToken, authenticate);
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
        	if (conn != null)
        	{
        		conn.close();
        	}
        }
		return client;
	}

}
