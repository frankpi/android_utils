package com.gameassist.plugin.noiab.iab;


import android.content.Context;
import android.database.CharArrayBuffer;
import android.net.Uri;
import android.util.Base64;

import com.gameassist.gson.stream.JsonWriter;
import com.gameassist.jsonmarshaller.JSONMarshaller;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;

public class Utils {
    public static void copyStringToBuffer(String str, CharArrayBuffer buffer) {
        if (str != null) {
            buffer.data = str.toCharArray();
            buffer.sizeCopied = buffer.data.length;
        }
    }

    public static String toJSON(Object object) {
        StringWriter w = new StringWriter();
        try {
            JSONMarshaller.marshall(new JsonWriter(w), object);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return w.toString();
    }

    public static String toJSON(Object object, String... stripped) {
        String result = toJSON(object);
        try {
            JSONObject json = new JSONObject(result);
            for (String s : stripped) {
                json.remove(s);
            }
            result = json.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String readString(File file) {
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
                sb.append('\n');
            }
            reader.close();
        } catch (Exception e) {
        }

        return sb.toString();
    }

    public static byte[] readFully(File file) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            FileInputStream fis = new FileInputStream(file);
            byte[] buffer = new byte[4906];
            int bytesRead;

            while ((bytesRead = fis.read(buffer)) > 0) {
                baos.write(buffer, 0, bytesRead);
            }
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return baos.toByteArray();
    }

    public static void extractFile(String data, File file) {
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(Base64.decode(data, 0));
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void copyStream(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[4096];
        int bytesRead;

        while ((bytesRead = in.read(buffer)) > 0) {
            out.write(buffer, 0, bytesRead);
        }

        out.flush();
    }

    public static Uri safeParseUri(String url) {
        try {
            return Uri.parse(url);
        } catch (Exception e) {
            return null;
        }
    }

    private static Integer versionCode;

    public static Integer getVersionCode(Context context, String packageName) {
        if (versionCode == null) {
            try {
                versionCode = context.getPackageManager().getPackageInfo(packageName, 0).versionCode;
            } catch (Exception e) {
                e.printStackTrace();
                versionCode = 0;
            }
        }

        return versionCode;
    }
}
