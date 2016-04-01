package com.zhhtao.mobileguard.activities;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.zhhtao.mobileguard.R;
import com.zhhtao.mobileguard.R.string;
import com.zhhtao.mobileguard.interfaces.MyHttpCallBackListener;
import com.zhhtao.mobileguard.utility.HttpUtility;
import com.zhhtao.mobileguard.utility.UIUtils;

import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class SplashActivity extends Activity {

	protected static final int LOAD_MAIN_UI = 0;
	protected static final int SHOW_UPDATE_DIALOG = 1;
	private TextView tv_splash_version;
	PackageManager packageManager;
	Activity context;
	int curVersion;
	String desc,downloadUrlString;
	
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case LOAD_MAIN_UI:
				loadMainUI();
				break;
			case SHOW_UPDATE_DIALOG:
				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				builder.setTitle("更新提醒");
				builder.setMessage("发现新版本："+desc);
				
				builder.setPositiveButton("开始更新", new OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						
					}
				});
				
				builder.setNegativeButton("下次再说", new OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						
					}
				});
				builder.show();
				break;

			default:
				break;
			}
		};
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		context = this;
		
		tv_splash_version = (TextView) findViewById(R.id.tv_splash_version);
		packageManager = getPackageManager();
		try {
			PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
			String versionName = packageInfo.versionName;
			curVersion = packageInfo.versionCode;
			tv_splash_version.setText("版本号："+versionName);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();//can't reach
		}
		UIUtils.myToast(context, "start");
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		checkVersion();
	}
	//连接服务器，检查是否存在新的版本
	private void checkVersion() {
		// TODO Auto-generated method stub
		long startTime = System.currentTimeMillis();
		Message message = Message.obtain();
		final Message finalMessage = message;
		String url = getResources().getString(R.string.downloadUrl);
		HttpUtility.sendHttpRequest(url, new MyHttpCallBackListener() {
			@Override
			public void onFinish(String response) {
				// TODO Auto-generated method stub
				if (TextUtils.isEmpty(response)) {
					//获取失败
					finalMessage.what = LOAD_MAIN_UI;
				} else {
//					UIUtils.myToast(context, response);
					JSONObject jObject;
					try {
						jObject = new JSONObject(response);
						downloadUrlString = jObject.getString("downloadurl");
						int serverVersion = jObject.getInt("version");
						desc = jObject.getString("desc");
						//需要升级
						if (serverVersion > curVersion) {
							finalMessage.what = SHOW_UPDATE_DIALOG;
						} else {
							//进入主界面
							finalMessage.what = LOAD_MAIN_UI;
						}
					} catch (JSONException e) {
						e.printStackTrace();
						finalMessage.what = LOAD_MAIN_UI;
					}
				}
			}
			
			@Override
			public void onError(Exception e) {
				// TODO Auto-generated method stub
				UIUtils.myToast(context, "网络连接失败");
				finalMessage.what = LOAD_MAIN_UI;
			}
		});
		
		long endTime = System.currentTimeMillis();
		long runTime = endTime - startTime;
		if (runTime < 2000) {
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					handler.sendMessage(finalMessage);
				}
			}, 2000-runTime);
		} else {
			message = finalMessage;
			handler.sendMessage(message);
		}
		
		
		
	}
	
	private void loadMainUI() {
		Intent intent = new Intent(context, HomeActivity.class);
		startActivity(intent);
		finish();
	}

}
