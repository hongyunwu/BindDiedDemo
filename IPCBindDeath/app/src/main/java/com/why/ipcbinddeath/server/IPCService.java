package com.why.ipcbinddeath.server;

import android.app.Service;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;

import com.why.ipcbinddeath.ICallBack;
import com.why.ipcbinddeath.IInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IPCService extends Service {
	private static final String TAG = "IPCService";
	public static final int DO_SOMETHING = 1;
	private Map<String, CallBack> mUUIDs = new HashMap<>();

	private class CallBack implements IBinder.DeathRecipient{
		ICallBack callBack;
		String UUID;
		public CallBack(String UUID,ICallBack callBack){
			this.UUID = UUID;
			this.callBack = callBack;
		}

		@Override
		public void binderDied() {
			Log.i(TAG,"bindDied:"+UUID);
			mUUIDs.remove(UUID);
		}
		public ICallBack getCallBack(){
			return callBack;
		}
		public String  getUUID(){
			return UUID;
		}
	}
	private Handler mHandler = new Handler() {


		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case DO_SOMETHING:
					doSomething();
					removeMessages(DO_SOMETHING);
					sendEmptyMessageDelayed(DO_SOMETHING, 1000);
					break;
			}
		}
	};

	@Override
	public void onCreate() {
		super.onCreate();
		mHandler.sendEmptyMessage(DO_SOMETHING);
	}

	@Override
	public IBinder onBind(Intent intent) {

		return new IPCBinder();
	}

	public class IPCBinder extends IInterface.Stub {

		@Override
		public void registerCallBack(String UUID, ICallBack callback) throws RemoteException {
			CallBack value = null;
			if (!mUUIDs.containsKey(UUID)) {

				value= new CallBack(UUID, callback);
				mUUIDs.put(UUID, value);
			} else {
				CallBack iCallBack = mUUIDs.remove(UUID);
				if (iCallBack != null&&iCallBack.getCallBack()!=null) {
					iCallBack.getCallBack().replyMessage("当前iCallBack已被替代");
				}
				value = new CallBack(UUID, callback);
				mUUIDs.put(UUID, value);

			}
			callback.asBinder().linkToDeath(value,0);
			callback.replyMessage("UUID:" + UUID + "注册成功");
		}

		@Override
		public void unRegisterCallBack(String UUID) throws RemoteException {
			if (mUUIDs.containsKey(UUID)) {
				CallBack iCallBack = mUUIDs.remove(UUID);
				iCallBack.getCallBack().replyMessage("当前UUID已经反注册成功");
				iCallBack.getCallBack().asBinder().unlinkToDeath(iCallBack,0);
			}

		}
	}

	public void doSomething() {
		for (Map.Entry<String, CallBack> entry : mUUIDs.entrySet()) {

			CallBack iCallBack = entry.getValue();

			Log.i(TAG, "doSomething...UUID:" + entry.getKey());

		}
	}

	@Override
	public boolean onUnbind(Intent intent) {
		return super.onUnbind(intent);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mHandler.removeMessages(DO_SOMETHING);
	}
}
