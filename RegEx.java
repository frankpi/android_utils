package rea;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegEx {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		StringBuffer str = new StringBuffer("");
		File file = new File("/home/frankpi/shared/aa");
		String line = null;
		BufferedReader in = null;
		try {
			FileInputStream is = new FileInputStream(file);
			InputStreamReader isr = new InputStreamReader(is);
			in = new BufferedReader(isr);
			while ((line = in.readLine()) != null){
				str.append(line+"\n");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String str1 = "ITEMNAME[5066]=罗马蜡烛";
		String regEx1 = "ITEMNAME\\[(.*)\\]=(.+)\\n";
		Pattern p1 = Pattern.compile(regEx1);
		Matcher m1 = p1.matcher(str);
		while(m1.find()){
			System.out.println("open"+","+m1.group(1)+","+m1.group(1)+","+"\""+m1.group(2)+"\""+"));"+"\n");
		}

	}
}
