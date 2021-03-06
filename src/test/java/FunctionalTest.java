package test.java;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.servlet.ServletContext;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.junit.Assert;
import org.junit.Test;


public class FunctionalTest {
	
	@Test
	public void startTest() throws ClientProtocolException, IOException {
		Properties p = new Properties();
		p.load(getClass().getResourceAsStream("config.properties"));
		String host = p.getProperty("HOST");
		String port = p.getProperty("PORT");
		String context = p.getProperty("CONTEXT");
		String timebeforetest = p.getProperty("TIMEBEFORETEST");
		
		HttpContext httpContext = new BasicHttpContext();
		HttpClient httpClient1 = new DefaultHttpClient();
		
		HttpGet httpGet = new HttpGet("http://"+host+":"+port+"/"+context+"/service/test/setmasteruser");
		HttpResponse res = httpClient1.execute(httpGet, httpContext);
		Assert.assertTrue("Test init",res.getStatusLine().getStatusCode() == 200 || res.getStatusLine().getStatusCode() == 204);
		
		
		httpGet = new HttpGet("http://"+host+":"+port+"/"+context+"/service/test/"+timebeforetest);
		HttpClient httpClient2 = new DefaultHttpClient();
		httpClient2.execute(httpGet, httpContext);
		Assert.assertTrue("Test result",res.getStatusLine().getStatusCode() == 200 || res.getStatusLine().getStatusCode() == 204);
	}
}
