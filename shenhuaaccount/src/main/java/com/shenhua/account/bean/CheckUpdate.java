package com.shenhua.account.bean;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Jsoup;

import android.app.ProgressDialog;
import android.os.Environment;

public class CheckUpdate {

	private static Map<String, String> map = new HashMap<String, String>();

	public static Map<String, String> getNewVersion() throws IOException,
			JSONException {
		Connection.Response response = Jsoup
				.connect(MyStringUtils.CURL + MyStringUtils.API_TAKEN)
				.method(Method.GET).ignoreContentType(true).timeout(5000)
				.execute();
		JSONObject dataJson = new JSONObject(response.body());
		JSONObject dataJson2 = dataJson.getJSONObject("binary");
		map.put("name", dataJson.getString("name"));
		map.put("version", dataJson.getString("version"));
		map.put("changelog", dataJson.getString("changelog"));
		map.put("versionShort", dataJson.getString("versionShort"));
		map.put("direct_install_url", dataJson.getString("direct_install_url"));
		map.put("fsize", bytes2kb(Long.parseLong(dataJson2.getString("fsize"))));
		return map;
	}

	public static String bytes2kb(long bytes) {
		BigDecimal filesize = new BigDecimal(bytes);
		BigDecimal megabyte = new BigDecimal(1024 * 1024);
		float returnValue = filesize.divide(megabyte, 2, BigDecimal.ROUND_UP)
				.floatValue();
		if (returnValue > 1)
			return (returnValue + "MB");
		BigDecimal kilobyte = new BigDecimal(1024);
		returnValue = filesize.divide(kilobyte, 2, BigDecimal.ROUND_UP)
				.floatValue();
		return (returnValue + "KB");
	}

	public static File getFileFromServer(String path, ProgressDialog pd)
			throws Exception {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			URL url = new URL(path);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(5000);
			pd.setMax(conn.getContentLength());
			InputStream is = conn.getInputStream();
			File file = new File(Environment.getExternalStorageDirectory(),
					"update.apk");
			FileOutputStream fos = new FileOutputStream(file);
			BufferedInputStream bis = new BufferedInputStream(is);
			byte[] buffer = new byte[1024];
			int len;
			int total = 0;
			while ((len = bis.read(buffer)) != -1) {
				fos.write(buffer, 0, len);
				total += len;
				pd.setProgress(total);
			}
			fos.close();
			bis.close();
			is.close();
			return file;
		} else {
			return null;
		}

	}
}
