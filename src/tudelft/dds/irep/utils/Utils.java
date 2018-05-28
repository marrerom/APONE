package tudelft.dds.irep.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.SimpleTimeZone;
import java.util.UUID;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.NewCookie;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.junit.Assert;

import com.fasterxml.uuid.Generators;
import com.google.common.base.Preconditions;

import tudelft.dds.irep.data.schema.JsonDateSerializer;

public class Utils {
	

	
	 public static byte[] serialize(Object obj) throws IOException {
	     ByteArrayOutputStream out = new ByteArrayOutputStream();
	     ObjectOutputStream os = new ObjectOutputStream(out);
	     os.writeObject(obj);
	     return out.toByteArray();
	 }
	 public static Object deserialize(byte[] data) throws IOException, ClassNotFoundException {
	     ByteArrayInputStream in = new ByteArrayInputStream(data);
	     ObjectInputStream is = new ObjectInputStream(in);
	     return is.readObject();
	 }
	 public static String getTimestamp(Date timestamp) {
			DateFormat datetarget = new SimpleDateFormat(JsonDateSerializer.timestampFormat); //Standard format recognized by Jackson
			datetarget.setTimeZone(new SimpleTimeZone(SimpleTimeZone.UTC_TIME, "UTC"));
			return datetarget.format(timestamp);
	 }
	 public static String getTimestamp() {
		 Date now = new Date();
		 return getTimestamp(now);
	 }
	 
	 //TODO: why these type of timestamps don't work??? received from the web form, in ISO format:  2017-08-29T15:40:00.000Z 
	 public static Date getDate(String timestamp) throws ParseException {
		 try {
			return new Date(timestamp);
		 } catch (IllegalArgumentException e) {
//			DateFormat datetarget = new SimpleDateFormat(JsonDateSerializer.timestampFormat); //Standard format recognized by Jackson
//			datetarget.setTimeZone(new SimpleTimeZone(SimpleTimeZone.UTC_TIME, "UTC"));
//			return datetarget.parse(timestamp);
			return (new SimpleDateFormat(JsonDateSerializer.timestampFormat)).parse(timestamp);
		 }
	 }
	 
	 public static Date addDay(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.DATE, 1);
		return c.getTime();
	 }
	 
	 public static byte[] decodeBinary(String valuestr) {
		 return java.util.Base64.getDecoder().decode(valuestr);
	 }
	 
	 public static String encodeBinary(byte[] valuebin) {
		 return java.util.Base64.getEncoder().encodeToString(valuebin);
	 }
	 
	 public static String getRequestIdentifier(String idrun, HttpServletRequest request) {
		 String idunit = null;
		 if (request.getCookies() != null) {
			for (javax.servlet.http.Cookie c : request.getCookies()) {
				if (c.getName().equals(idrun)) {
					idunit = c.getValue();
					break;
				}
			}
		}
			
		if (idunit == null || idunit.isEmpty()) {
			UUID uuid = Generators.timeBasedGenerator().generate();
			idunit = uuid.toString().toString();
		}
		return idunit;
	 }
	 
	 public static String getVariantURL(String target, Map<String,?> params, String idunit, String variant) throws UnsupportedEncodingException {
		 String query = "";
		 for (String key: params.keySet()) {
			 String encodedValue = URLEncoder.encode(params.get(key).toString(), StandardCharsets.UTF_8.toString());
			 query = query + key+"="+encodedValue +"&";
		 }
		 query = query + "_idunit="+URLEncoder.encode(idunit, StandardCharsets.UTF_8.toString()) +"&";
		 query = query + "_variant="+URLEncoder.encode(variant, StandardCharsets.UTF_8.toString());
		 
		 if (target.contains("?"))
			 target = target + "&" + query;
		 else
			 target = target + "?" + query;
		 
		 String[] urlparts = target.split("\\?");
		 if (urlparts.length == 2) {
			 String encoded64Query = Utils.encodeBinary(urlparts[1].getBytes());
			 target = urlparts[0] + "?" + URLEncoder.encode(encoded64Query, StandardCharsets.UTF_8.toString());
		 }
		 
		 return target;
	 }
	 
	 public static NewCookie getCookie(URI target, String idrun, String idunit) {
		 NewCookie newcookie = new NewCookie(idrun,idunit,"/",target.getHost(),"",Integer.MAX_VALUE,false); //Only valid if same domain
		 return newcookie;
	 }
	 
	 static public String checkWebResponse(Collection<WebApplicationException> validExceptions , HttpResponse response, String message) throws IOException {
		 
		 System.out.println("check response, code "+response.getStatusLine().getStatusCode());
				 
		 HttpEntity entity = response.getEntity();
		 String contents = null;
		 if (entity != null)
			 contents = EntityUtils.toString(response.getEntity());
		 for (WebApplicationException e: validExceptions) {
			 if (response.getStatusLine().getStatusCode() == e.getResponse().getStatusInfo().getStatusCode()) {
				 System.out.println("checkResponse, throw exception "+e.getMessage());
				 throw e;
			}
		 }
		 
		 
	    System.out.println("checkResponse before assert");
		 
		Assert.assertTrue(message, response.getStatusLine().getStatusCode() == 200 || response.getStatusLine().getStatusCode() == 204);
		return contents;
	 }
	 
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
	 /*If the user has admin rol, the user is set to null, because is the same value if there is no authentication.
	  * 
	  */
//	 public static String getExperimenter(HttpServletRequest request) {
//		 if (request.isUserInRole("irepadmin"))
//			 return null;
//		 if (request.getUserPrincipal() != null)
//			 return request.getUserPrincipal().getName();
//		 return null;
//	 }

	 
}
