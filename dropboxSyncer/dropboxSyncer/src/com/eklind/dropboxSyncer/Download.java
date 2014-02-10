package com.eklind.dropboxSyncer;
import com.dropbox.core.*;
import com.eklind.dropboxSyncer.Authorize;
import java.io.*;
import java.util.Locale;

public class Download {
	   public static void main(String[] args) throws IOException, DbxException {
		   DbxClient client = Authorize.validate("haegg78", false);
		   try
		   {
			   System.out.println("Linked account: " + client.getAccountInfo().displayName);
		   }
		   catch (DbxException dbxe)
		   {
			   if (dbxe.getMessage().contains("401"))
			   {
				   System.out.println("Inside download.java");
				   dbxe.printStackTrace();
				   client = Authorize.validate("haegg78", true);
				   System.out.println("Linked account: " + client.getAccountInfo().displayName);
			   }
		   }
		   //String accessT = Authorize.validate("haegg78");
		   //System.out.println("AccessToken: " + accessT);
		   
	        /*

	        File inputFile = new File("/tmp/working-draft.txt");
	        FileInputStream inputStream = new FileInputStream(inputFile);
	        try {
	            DbxEntry.File uploadedFile = client.uploadFile("/magnum-opus.txt",
	                DbxWriteMode.add(), inputFile.length(), inputStream);
	            System.out.println("Uploaded: " + uploadedFile.toString());
	        } finally {
	            inputStream.close();
	        }

	        DbxEntry.WithChildren listing = client.getMetadataWithChildren("/");
	        System.out.println("Files in the root path:");
	        for (DbxEntry child : listing.children) {
	            System.out.println("	" + child.name + ": " + child.toString());
	        }

	        FileOutputStream outputStream = new FileOutputStream("magnum-opus.txt");
	        try {
	            DbxEntry.File downloadedFile = client.getFile("/magnum-opus.txt", null,
	                outputStream);
	            System.out.println("Metadata: " + downloadedFile.toString());
	        } finally {
	            outputStream.close();
	        }
			*/
	    }
}
