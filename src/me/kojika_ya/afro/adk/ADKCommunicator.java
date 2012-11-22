package me.kojika_ya.afro.adk;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import android.util.Log;

/**
 * @author shikajiro
 * ADKと通信する。コマンド送ったり、レスポンスを貰ったり。
 *
 */
public class ADKCommunicator implements Runnable {
	private static final String TAG = "ADKCommunicator"; 
	
	private FileInputStream mInputStream;
	private FileOutputStream mOutputStream;

	public ADKCommunicator(FileDescriptor fd) {
		mInputStream = new FileInputStream(fd);
		mOutputStream = new FileOutputStream(fd);
	}
	
	/**
	 * USBとの通信処理
	 */
	@Override
	public void run() {
		int ret = 0;
		byte[] buffer = new byte[8];
		
		while(ret==0){
			try{
				ret = mInputStream.read(buffer);
			}catch(IOException e){
				break;
			}
			if(ret > 0){
//				Message m = Message.obtain(mHandler, 1);
//				m.obj = new ResponseMsg(buffer);
//				mHandler.sendMessage(m);
			}else{
				Log.d(TAG, "unknown msg");
			}
		}
	}
	

	/**
	 * ADKに情報を送る
	 * @param command
	 * @param value
	 */
	public void sendCommand(MoveMsg msg){
		byte[] message = msg.toMessage();
		
		if(mOutputStream != null){
			try{
				mOutputStream.write(message);
			}catch(IOException e){
				Log.e(TAG, "write failed", e);
			}
		}
	}
	
	/**
	 * USBから受け取ったデータを画面等に反映させる処理
	 * 現時点の仕様ではレスポンスはない。
	 */
//	private static Handler mHandler = new Handler(){
//		public void handleMessage(Message msg) {
//			switch (msg.what) {
//			default:
//				break;
//			}
//		};
//	};
}
