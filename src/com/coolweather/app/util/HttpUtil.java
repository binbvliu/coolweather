package com.coolweather.app.util;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpUtil {
	
	public static void sendHttpRequest(final String address, final HttpCallBackListener listener){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				URL url = null;
				HttpURLConnection connection = null;
				 try {
					url = new URL(address);
					connection = (HttpURLConnection) url.openConnection();
					connection.setReadTimeout(8000);
					connection.setRequestMethod("GET");
					InputStream in = connection.getInputStream();
					String response = new String(StreamTool.read(in));
					if(listener!=null){
						listener.onFinsh(response);
					}
				} catch (Exception e) {
					if(listener!=null){
						listener.onError(e);
					}
					e.printStackTrace();
				}finally{
					if(connection!=null){
						connection.disconnect();
					}
				}
			}
		}).start();
	}
	
}
