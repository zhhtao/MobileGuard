package com.zhhtao.mobileguard.utility;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.zhhtao.mobileguard.R;
import com.zhhtao.mobileguard.interfaces.MyHttpCallBackListener;

public class HttpUtility {
	public static void sendHttpRequest(final String urlstr, final MyHttpCallBackListener listener) {
		// 开启线程来发起网络请求
		new Thread(new Runnable() {
			@Override
			public void run() {
				HttpURLConnection connection = null;
				try {
					
					URL url = new URL(urlstr);
					connection = (HttpURLConnection) url.openConnection();
					connection.setRequestMethod("GET");
					connection.setConnectTimeout(8000);
					connection.setReadTimeout(8000);
					connection.setDoInput(true);
					connection.setDoOutput(true);
					
					int code = connection.getResponseCode();
					if (code == 200) {
						InputStream in = connection.getInputStream();
						// 下面对获取到的输入流进行读取
						BufferedReader reader = new BufferedReader(
								new InputStreamReader(in));
						StringBuilder response = new StringBuilder();
						String line;
						while ((line = reader.readLine()) != null) {
							response.append(line).append("\n");
						}
						
						if (listener != null)
							listener.onFinish(response.toString());
					}
				} catch (Exception e) {
					e.printStackTrace();
					if (listener != null)
						listener.onError(e);
				} finally {
					if (connection != null) {
						connection.disconnect();
					}
				}
			}
		}).start();
	}
}

