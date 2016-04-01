package com.zhhtao.mobileguard.interfaces;

public interface MyHttpCallBackListener {
	void onFinish(String response);
	void onError(Exception e);
}
