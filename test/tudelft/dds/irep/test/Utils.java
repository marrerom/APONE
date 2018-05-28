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
	

//	
//	static public String readStrFile(String path) throws IOException{
//		FileReader fr = new FileReader(path);
//		BufferedReader br = new BufferedReader(fr);
//		StringBuffer sb = new StringBuffer();
//		while (br.ready()){
//		   sb.append(br.readLine() + System.lineSeparator());
//		}
//		br.close();
//		return sb.toString();
//	}
//	
//	 static public byte[] readSmallBinaryFile(String aFileName) throws IOException {
//		    Path path = Paths.get(aFileName);
//		    return Files.readAllBytes(path);
//		  }
//	 
//	 static public void writeSmallBinaryFile(byte[] aBytes, String aFileName) throws IOException {
//		    Path path = Paths.get(aFileName);
//		    Files.write(path, aBytes); //creates, overwrites
//		  }
//	 


}
