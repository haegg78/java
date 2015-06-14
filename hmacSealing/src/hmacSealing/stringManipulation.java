package hmacSealing;

public class stringManipulation {
	public static void main(String[] args) throws Exception
	{
		String strToBeTrimmed = "abcd¨&";
		strToBeTrimmed.replaceAll("^\\x20-\\x7e]","\\xc3");
		System.out.println("strToBeTrimmed: " + strToBeTrimmed);
	}
}
