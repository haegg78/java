package hmacSealing;

import java.io.FileInputStream;
import java.io.IOException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

public class hmacSeal {
	public static void main(String[] args) throws Exception
	{
		FileInputStream fis = null;
		int read = 0;
		byte[] buffer = new byte[4096];
	    try 
	    {
	    	String fileName = "/Users/haegg/log.txt";
	    	fis = new FileInputStream(fileName);
	        String secret = "secret";
	        Mac sha256_HMAC = Mac.getInstance("HmacSHA1");
	        SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(), "HmacSHA1");
	        sha256_HMAC.init(secret_key);
	        //String message = "Message";
	        while ((read = fis.read(buffer)) != -1)
	        {
	        	System.out.println("Number of bytes read: " + read);
	        	sha256_HMAC.update(buffer, 0, read);
	        }
	        String hash = Base64.encodeBase64String(sha256_HMAC.doFinal());
	        //String hash = Base64.encodeBase64String(sha256_HMAC.doFinal(message.getBytes()));
	        System.out.println(hash);
	    }
	    catch (Exception e)
	    {
	        System.out.println("Error");
	    }
	    finally
	    {
	    	if (fis != null)
	    	{
	    		fis.close();
	    	}
	    }
	    		
	}
}
