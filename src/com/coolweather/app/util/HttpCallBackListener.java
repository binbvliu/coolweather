package com.coolweather.app.util;

public interface HttpCallBackListener{
	void onFinsh(String response);
	void onError(Exception e);
}