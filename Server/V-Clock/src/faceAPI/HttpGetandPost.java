package faceAPI;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HttpGetandPost {
	public String sendGet(String url, String param) {
		String result = "";
		BufferedReader in = null;
		String urlStringName = url + "?" + param;
		try {
			URL realUrl = new URL(urlStringName);
			//打开url连接
			HttpURLConnection conn = (HttpURLConnection) realUrl
					.openConnection();
			//连接
			conn.connect();

			// 从BufferedReader读取url的响应流
			in = new BufferedReader(
					new InputStreamReader(conn.getInputStream(),"utf-8"));
			String line;
			while ((line = in.readLine()) != null) {
				line = new String(line.getBytes(), "utf-8");
				result += line;
			}
		} catch (Exception e) {
			System.out.println("get请求发送失败！" + e);
			e.printStackTrace();
		}
		finally {
			try {
				if (in != null)
					in.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return result;
	}

	public String sendPost(String url, String param) throws Exception {
		String result = "";
		try {
			URL realUrl = new URL(url);
			//打开url连接
			HttpURLConnection conn = (HttpURLConnection) realUrl
					.openConnection();
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setRequestMethod("POST");
			conn.setUseCaches(false);
			conn.setInstanceFollowRedirects(true);
			conn.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			conn.connect();
			// post请求
			DataOutputStream out = new DataOutputStream(conn.getOutputStream());
			out.writeBytes(param);
			out.flush();
			out.close();
			// 从BufferedReader读取url的响应流
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					conn.getInputStream(),"utf-8"));
			String line;
			while ((line = reader.readLine()) != null) {
				line = new String(line.getBytes(), "utf-8");
				result += line;
			}
			reader.close();
			conn.disconnect();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

}
