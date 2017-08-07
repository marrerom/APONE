package tudelft.dds.irep.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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
			return datetarget.format(timestamp);
	 }
	 public static String getTimestamp() {
		 Date now = new Date();
		 return getTimestamp(now);
	 }
	 public static Date getDate(String timestamp) throws ParseException {
		 return (new SimpleDateFormat(JsonDateSerializer.timestampFormat)).parse(timestamp);
	 }
	 
	 public static byte[] decodeBinary(String valuestr) {
		 return java.util.Base64.getDecoder().decode(valuestr);
	 }
	 
	 public static String encodeBinary(byte[] valuebin) {
		 return java.util.Base64.getEncoder().encodeToString(valuebin);
	 }
	 

}
