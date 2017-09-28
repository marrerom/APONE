package tudelft.dds.irep.test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Utils {
	
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

}
