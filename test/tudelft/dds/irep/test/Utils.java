package tudelft.dds.irep.test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.WebApplicationException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.util.EntityUtils;
import org.junit.Assert;

import tudelft.dds.irep.utils.BadRequestException;

public class Utils {
	
	public static Map<String, String> decodeQueryParams(String queryString) throws UnsupportedEncodingException {
		Map<String, String> queryparams = new HashMap<String, String>();
		queryString = URLDecoder.decode(queryString, StandardCharsets.UTF_8.toString());
	    byte[] decoded = tudelft.dds.irep.utils.Utils.decodeBinary(queryString);
	    queryString = new String(decoded);
	    String[] qparams = queryString.split("&");
	    for (String param: qparams) {
	    	String name = param.split("=")[0];
	    	String value = param.split("=")[1];
	    	queryparams.put(name, URLDecoder.decode(value, StandardCharsets.UTF_8.toString()));
	    }
	    return queryparams;
	}
	
	public static String encodeQueryParams(Map<String, String> queryParams) throws UnsupportedEncodingException {
	    StringBuffer qparams = new StringBuffer();
		for (String param: queryParams.keySet()) {
			qparams.append(param+"="+URLEncoder.encode(queryParams.get(param), StandardCharsets.UTF_8.toString()));
	    	qparams.append("&");
	    }
		if (qparams.length()>0)
			qparams.delete(qparams.length()-1, qparams.length());
		String qparamsString = tudelft.dds.irep.utils.Utils.encodeBinary(qparams.toString().getBytes());
		qparamsString = URLEncoder.encode(qparamsString);
		return qparamsString;
	}
	
	static public String readStrFile(String path) throws IOException{
		FileReader fr = new FileReader(path);
		BufferedReader br = new BufferedReader(fr);
		StringBuffer sb = new StringBuffer();
		while (br.ready()){
		   sb.append(br.readLine() + System.lineSeparator());
		}
		br.close();
		return sb.toString();
	}
	
	 static public byte[] readSmallBinaryFile(String aFileName) throws IOException {
		    Path path = Paths.get(aFileName);
		    return Files.readAllBytes(path);
		  }
	 
	 static public void writeSmallBinaryFile(byte[] aBytes, String aFileName) throws IOException {
		    Path path = Paths.get(aFileName);
		    Files.write(path, aBytes); //creates, overwrites
		  }
	 
	 static public String checkWebResponse(Collection<WebApplicationException> validExceptions , HttpResponse response, String message) throws ParseException, IOException {
		 HttpEntity entity = response.getEntity();
		 String contents = null;
		 if (entity != null)
			 contents = EntityUtils.toString(response.getEntity());
		 for (WebApplicationException e: validExceptions) {
			 if (response.getStatusLine().getStatusCode() == e.getResponse().getStatusInfo().getStatusCode()) {
				 throw e;
			}
		 }
		Assert.assertTrue(message, response.getStatusLine().getStatusCode() == 200 || response.getStatusLine().getStatusCode() == 204);
		return contents;
	 }

}
