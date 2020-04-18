package com.ryanthetide.oqrpc.source;

import java.io.File;
import java.io.FileWriter;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.json.JSONObject;

public class Config {
	
	private static File configFile;
	
	public static void init() {
		try {
			configFile =  new File(jarPath().replace(new File(jarPath()).getName(), "config.json"));
			if (!configFile.exists())configFile.createNewFile();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String jarPath() {
		try {
			String path = Config.class.getProtectionDomain().getCodeSource().getLocation().getPath();
			String r =  URLDecoder.decode(path, "UTF-8");

			if (r.startsWith("/")) return r.replaceFirst("/", "");
			return r;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	public static String getUpdater() {
		return jarPath().replace(new File(jarPath()).getName(), "Util.jar");
	}
	
	private static JSONObject read() {
		try {
			String text = new String(Files.readAllBytes(Paths.get(configFile.getAbsolutePath())));
			if (text.equals("")) {
				return write(new JSONObject());
			} else return new JSONObject(text);
		} catch (Exception e) {
			return new JSONObject();
		}
	}

	public static String getAddress() {
		JSONObject main = read();
		if (main.has("address")) return main.getString("address");
		else return "";
	}
	
	public static void setAddress(String s) {
		JSONObject main = read();
		main.put("address", s);
		write(main);
	}

	public static String getApkVersion() {
		JSONObject main = read();
		if (main.has("apkVersion")) return main.getString("apkVersion");
		else return "";
	}

	public static void setApk(String version) {
		write(read().put("apkVersion", version));
	}
	
	private static JSONObject write(JSONObject obj) {
		try {
			if (!configFile.exists()) configFile.createNewFile();
			FileWriter fw = new FileWriter(configFile);
			fw.write(obj.toString(4));
			fw.close();
			return new JSONObject();
		} catch (Exception ignored) {
			return new JSONObject();
		}
	}
	
	public static void setBootSetting(boolean toggled) {
		JSONObject main = read();
		main.put("startBoot", toggled);
		write(main);
	}
	
	public static boolean readBoot() {
		JSONObject main = read();
		if (main.has("startBoot")) return main.getBoolean("startBoot");
		setBootSetting(true);
		return true;
	}
}
