package com.frankpi.video99.util.a.en;

import java.io.IOException;
import java.util.zip.DataFormatException;

import android.util.Log;

public class Util {
	// private static final double ENCRYPTION_KEY = 498569365.5688;
	// private static final double ENCRYPTION_KEY = (double)AdBase64.EK +
	// AES.EK;
	private static final double ENCRYPTION_KEY = 123456.0000;
	
	public static byte[] zipAndEncrypt(byte[] input) throws IOException {
		AES aes = new AES();
		aes.setKey(aes.genKey(Long.toHexString(Double.doubleToLongBits(ENCRYPTION_KEY))));
		Log.i("gameassist2key", Long.toHexString(Double.doubleToLongBits(ENCRYPTION_KEY)));
		return aes.RootEncrypt(AdBase64.encode(Zip.zipBytes(input)));
	}

	public static byte[] decryptAndUnzip(byte[] input) throws DataFormatException, IOException {
		AES aes = new AES();
		aes.setKey(aes.genKey(Long.toHexString(Double.doubleToLongBits(ENCRYPTION_KEY))));
		return Zip.unzipBytes(AdBase64.decode(new String(aes.RootDecrypt(input))));
	}
}