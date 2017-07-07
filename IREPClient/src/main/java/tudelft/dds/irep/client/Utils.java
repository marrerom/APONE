package tudelft.dds.irep.client;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

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

}
