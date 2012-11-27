package me.kojika_ya.afro.adk;

import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Fragment;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.util.Log;

public class ADKConnector extends Fragment{
	private static final String TAG = "ADKConnector"; 

	private static final String ACTION_USB_PERMISSION = "com.example.adksample.action.USB_PERMISSION";

	private UsbManager mUsbMng;
	
	private PendingIntent mPermissionIntent ;
	private boolean mPermissionRequestPending;
	private UsbAccessory mAccessory;
//	private ADKCommunicator mCommunicator;
	private FileOutputStream mOutputStream;

	/*input output stream */
	private ParcelFileDescriptor mFileDescriptor;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mUsbMng = (UsbManager) getActivity().getSystemService(Context.USB_SERVICE);		

		/* receiverの登録 */
		IntentFilter filter = new IntentFilter();
		filter.addAction(ACTION_USB_PERMISSION);
		filter.addAction(UsbManager.ACTION_USB_ACCESSORY_DETACHED);
		getActivity().registerReceiver(mUsbReceiver, filter);
	}
	
	@Override
	public void onResume() {
		UsbAccessory[] accessories = mUsbMng.getAccessoryList();
		UsbAccessory accessory = (accessories == null ? null : accessories[0]);
		if(accessory != null){
			//アプリ起動時点でUSBが接続されていれば、アクセサリーをオープンする。
			if(mUsbMng.hasPermission(accessory)){
				openAccessory(accessory);
			}else{
				synchronized (mUsbReceiver) {
					if(!mPermissionRequestPending){
						mUsbMng.requestPermission(accessory, mPermissionIntent);
						mPermissionRequestPending = true;
					}
				}
			}
		}else{
			Log.d(TAG, "Accessory is null");
		}
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
		closeAccessory();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		getActivity().unregisterReceiver(mUsbReceiver);
	}

	/**
	 * ADKに情報を送る
	 * @param command
	 * @param value
	 */
	public void sendCommand(ServoMsg msg){
		System.out.println(msg);
		final byte[] message = msg.toMessage();
		if(mOutputStream != null){
			try{
				mOutputStream.write(message);
			}catch(IOException e){
				Log.e(TAG, "write failed", e);
			}
		}

	}
	Handler handler = new Handler();
	boolean nowSending = false;
	
	/**
	 * USBに接続しているアクセサリーと接続する。
	 * @param accessory
	 */
	private void openAccessory(UsbAccessory accessory){
		mFileDescriptor = mUsbMng.openAccessory(accessory);
		
		if(mFileDescriptor != null){
			mAccessory = accessory;
			FileDescriptor fd = mFileDescriptor.getFileDescriptor();
			mOutputStream = new FileOutputStream(fd);
//			mCommunicator = new ADKCommunicator(fd);
//			new Thread(null, mCommunicator, "DemoKit").start();
			Log.d(TAG, "accessory opend");
			
		}else{
			Log.d(TAG, "accessory open fail");
		}
	}

	/**
	 * 接続しているUSBアクセサリーとの接続を終了する。
	 */
	private void closeAccessory(){
		try{
			if(mFileDescriptor != null){
				mFileDescriptor.close();
			}
		}catch(IOException e){
		}finally{
			mFileDescriptor = null;
			mAccessory = null;
//			mCommunicator = null;
		}
	}

	/**
	 * アプリ起動時にUSBを接続すると呼び出される処理
	 * 接続時：USBアクセサリーのストリームを開く。
	 * 切断時：USBアクセサリーのストリームを閉じる。
	 */
	private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver(){
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if(ACTION_USB_PERMISSION.equals(action)){
				//USB接続時はアクセサリーのストリームを開く。
				synchronized (this) {
					UsbAccessory accessory = intent.getParcelableExtra(UsbManager.EXTRA_ACCESSORY);
					if(intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)){
						openAccessory(accessory);
					}else{
						Log.d("adksample", "permission denied for accessory "+ accessory);
					}
					mPermissionRequestPending = false;
				}
			}else if(UsbManager.ACTION_USB_ACCESSORY_DETACHED.equals(action)){
				//USB切断時はストリームを閉じる。
				UsbAccessory accessory = intent.getParcelableExtra(UsbManager.EXTRA_ACCESSORY);
				if(accessory!= null && accessory.equals(mAccessory)){
					closeAccessory();
				}
			}
		}
	};

}
