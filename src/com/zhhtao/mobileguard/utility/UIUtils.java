package com.zhhtao.mobileguard.utility;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

public class UIUtils {
	public static void myToast(final Activity context , final String msg) {
		
		if ("main".equals(Thread.currentThread().getName())) {
			Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
		} else {
			context.runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
				}
			});
		}
		
	}
}
