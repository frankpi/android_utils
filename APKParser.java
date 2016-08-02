package com.gameassist.pluginuploader;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import pxb.android.axml.Axml;
import pxb.android.axml.Axml.Node;
import pxb.android.axml.Axml.Node.Attr;
import pxb.android.axml.AxmlReader;

import com.gameassist.pluginuploader.orm.PluginComponent;

public class APKParser {
	
	public static PluginComponent parseAPK(File source) throws IOException {
		ZipFile zf = new ZipFile(source);
		ZipEntry ze = zf.getEntry("AndroidManifest.xml");
		InputStream in = zf.getInputStream(ze);
		byte[] data = readFully(in);
		in.close();
		
		AxmlReader reader = new AxmlReader(data);
		Axml axml = new Axml();
		reader.accept(axml);
		
		String packageName = null;
		int versionCode = 0;
		String entry = null;
		String targetName = null;
		String targetVersion = null;
		
		Node manifest = axml.firsts.get(0);
		for (Attr a : manifest.attrs) {
			if ("package".equals(a.name)) {
				packageName = (String) a.value;
			} else if ("versionCode".equals(a.name)) {
				versionCode = (Integer) a.value;
			}
		}

		for (Node n : manifest.children) {
			if ("application".equals(n.name)) {
				for (Node m : n.children) {
					if ("meta-data".equals(m.name)) {
						String name = null;
						String value = null;
						for (Attr a : m.attrs) {
							if ("name".equals(a.name)) {
								name = (String) a.value;
							} else if ("value".equals(a.name)) {
								value = (String) a.value;
							}
						}
						if ("Entry".equals(name)) {
							entry = value;
						} else if ("Target0".equals(name)) {
							targetName = value;
						} else if ("Version0".equals(name)) {
							targetVersion = value;
						}
					}
				}
			}
		}
		
		if (packageName != null && entry != null) {
			PluginComponent result = new PluginComponent();
			result.setPluginName(packageName);
			result.setEntryPoint(entry);
			result.setTimestamp(versionCode);
			result.setTargetPackage(targetName);
			return result;
		} else {
			return null;
		}
	}
	
	public static byte[] readFully(InputStream in) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			byte[] buffer = new byte[4906];
			int bytesRead;

			while ((bytesRead = in.read(buffer)) > 0) {
				baos.write(buffer, 0, bytesRead);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return baos.toByteArray();
	}
	
	public static byte[] readFully(File file) throws IOException {
		FileInputStream in = new FileInputStream(file);
		try {
			return readFully(in);
		} finally {
			in.close();
		}
	}
}
