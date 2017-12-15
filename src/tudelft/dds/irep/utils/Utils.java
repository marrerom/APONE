package tudelft.dds.irep.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.SimpleTimeZone;
import java.util.UUID;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.NewCookie;

import org.apache.commons.lang3.tuple.Pair;

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
