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
	// 发送get请求
	public String sendGet(String url, String param) {
		String result = "";
		BufferedReader in = null;
		String urlStringName = url + "?" + param;
		try {
			URL realUrl = new URL(urlStringName);
			// 打开和url之间的连接
			HttpURLConnection conn = (HttpURLConnection) realUrl
					.openConnection();

//			conn.setRequestMethod("GET");
//			conn.setDoOutput(true);
//			conn.setDoInput(true);
//			conn.setUseCaches(false);
			// 建立实际的连接
			conn.connect();
			// get请求
//			DataOutputStream out = new DataOutputStream(conn.getOutputStream());
//			out.writeBytes(param);
//			out.flush();
//			out.close();
			// 定义BufferedReader输入流来读取url的响应
			in = new BufferedReader(
					new InputStreamReader(conn.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				line = new String(line.getBytes(), "utf-8");
				result += line;
			}
		} catch (Exception e) {
			System.out.println("发送GET请求出现异常！" + e);
			e.printStackTrace();
		}
		// 使用finally关闭数据流
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

	// 发送post请求
	public String sendPost(String url, String param) throws Exception {
		String result = "";
		try {
			URL realUrl = new URL(url);
			// 打开和url之间的连接
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
			// 读取响应
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					conn.getInputStream()));
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
