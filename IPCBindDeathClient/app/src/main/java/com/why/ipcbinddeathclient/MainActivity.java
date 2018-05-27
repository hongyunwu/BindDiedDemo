package com.why.ipcbinddeathclient;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Process;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.why.ipcbinddeath.ICallBack;
import com.why.ipcbinddeath.IInterface;

import java.util.UUID;


public class MainActivity extends AppCompatActivity {
	private static final String TAG = "MainActivity";
	private ServiceConnection serviceConnection;
	private IInterface iInterface;
	private String uuid = UUID.randomUUID().toString().replace("-", "");
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		findViewById(R.id.register).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				register();
			}
		});
		findViewById(R.id.unregister).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				unregister();
			}
		});
	}

	private void unregister() {
		if (serviceConnection != null) {
			try {
				if (iInterface != null) {
					iInterface.unRegisterCallBack(uuid);
				} else {
					Toast.makeText(this, "还未Bind成功...", Toast.LENGTH_SHORT).show();
				}

			} catch (RemoteException e) {
				e.printStackTrace();
			}
			unbindService(serviceConnection);
			serviceConnection = null;
		} else {
			Toast.makeText(this, "请先点击注册按钮...", Toast.LENGTH_SHORT).show();
		}
		Log.i(TAG, "unregister");
	}

	private void register() {
		Intent intent = new Intent();
		intent.setClassName("com.why.ipcbinddeath", "com.why.ipcbinddeath.server.IPCService");
		serviceConnection = new IPCServiceConnection();
		bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
		Log.i(TAG, "register");
	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case 1:
					Process.killProcess(Process.myPid());
					break;
				default:
					break;
			}

		}
	};

	ICallBack mCallBack = new ICallBack.Stub() {
		@Override
		public void replyMessage(String message) throws RemoteException {

			Log.i(TAG, "replyMessage:" + message);

			mHandler.sendEmptyMessageDelayed(1,5000);
		}
	};

	private class IPCServiceConnection implements ServiceConnection {
		//绑定成功
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			iInterface = IInterface.Stub.asInterface(service);
			try {
				iInterface.registerCallBack(uuid, mCallBack);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		//绑定失败
		@Override
		public void onServiceDisconnected(ComponentName name) {
			iInterface = null;
		}
	}

}
