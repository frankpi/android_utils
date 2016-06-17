package rea;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;


public class ZipUtils {
	
	public static byte[] zipBytes(byte[] input) {
		try {
			if (input == null || input.length == 0) {
				return null;
			}
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			DeflaterOutputStream dos = new DeflaterOutputStream(baos);
			dos.write(input);
			baos.close();
			dos.close();
			return baos.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	public static byte[] unzipBytes(byte[] input) {
		try {
			if (input == null || input.length == 0) {
				return null;
			}
			InflaterInputStream iis = new InflaterInputStream(new ByteArrayInputStream(input));
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			byte[] buf = new byte[1024];
			int length;
			while ((length = iis.read(buf)) > 0) {
				bos.write(buf, 0, length);
			}
			iis.close();
			bos.close();
			return bos.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

}
