package com.eklind;


import java.io.File;

import org.apache.catalina.Context;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.deploy.ApplicationParameter;
import org.apache.catalina.startup.Tomcat;

public class ServletContainer {
	private static Tomcat tomcat;
	public static void main(String[] args) throws Exception {
		StartTomcat(10080);
		Thread.sleep(120000);
		StopTomcat();
	}
	public static void StartTomcat(int port) throws Exception {
		tomcat = new Tomcat();
		tomcat.setPort(port);
		Context ctx1 = tomcat.addContext("/as2", new File(".").getAbsolutePath());
		Context ctx2 = tomcat.addContext("/as4", new File(".").getAbsolutePath());
		Tomcat.addServlet(ctx1, "HelloWorld", HelloWorldServLet.class.getName());
		Tomcat.addServlet(ctx2, "HelloEarth", HelloEarthServLet.class.getName());
		ctx1.addServletMapping("/*", "HelloWorld");
		ctx2.addServletMapping("/*", "HelloEarth");
		/*
		 * Start Tomcat
		 */
		tomcat.start();
	}
	public static void StopTomcat() throws Exception {
		tomcat.stop();
	}
}
