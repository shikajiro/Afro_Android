package me.kojika_ya.afro;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import me.kojika_ya.afro.adk.ADKConnector;
import me.kojika_ya.afro.adk.EmptyMsg;
import me.kojika_ya.afro.adk.MoveMsg;
import me.kojika_ya.afro.adk.ServoMsg;
import me.kojika_ya.afro.adk.SpeedMsg;
import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Face;
import android.hardware.Camera.FaceDetectionListener;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * @author shikajiro
 *
 */
public class MainActivity extends Activity {

	protected static final String TAG = "MainActivity";
	private SurfaceView mCameraSurfaceView;
	private SurfaceHolder mCameraHolder;
	private Camera camera;
	
	private SurfaceView mOverlaySurfaceView;
	private SurfaceHolder mOverlayHolder;
	
	private ADKConnector mAdkConnector;
	
	short upside;
	short side;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		//cameraを描画するView
		mCameraSurfaceView = (SurfaceView) findViewById(R.id.camera_surface_view);
		mCameraHolder = mCameraSurfaceView.getHolder();
		mCameraHolder.addCallback(mCameraCallback);
		
		//overlayを描画するView
		mOverlaySurfaceView = (SurfaceView) findViewById(R.id.overlay_surface_view);
		mOverlayHolder = mOverlaySurfaceView.getHolder();
		mOverlayHolder.setFormat(PixelFormat.TRANSPARENT);
		
		mAdkConnector = new ADKConnector();
		getFragmentManager().beginTransaction().add(android.R.id.content, mAdkConnector).commit();
		
		//ADKに継続的にデータを送るタイマー
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				ServoMsg servo;
				if(dancing){
					if(DANCE.length <= danceIndex) danceIndex = 0;
					moveDanceServo(DANCE[danceIndex]);
					++danceIndex;
					
				}else{
					if(side == 0){
						servo = new EmptyMsg();
						mAdkConnector.sendCommand(servo);
					}else{
						servo = SpeedMsg.newInstancePosition(ServoMsg.ROLL_ID, (short) 0x40);
						mAdkConnector.sendCommand(servo);
						servo = SpeedMsg.newInstancePosition(ServoMsg.SLOPE_ID, (short) 0x40);
						mAdkConnector.sendCommand(servo);
						servo = MoveMsg.newInstancePosition(ServoMsg.ROLL_ID, side);
						mAdkConnector.sendCommand(servo);
						servo = MoveMsg.newInstancePosition(MoveMsg.SLOPE_ID, upside);
						mAdkConnector.sendCommand(servo);
					}
				}
				
			}
		};
		new Timer().schedule(task, 0, 100);
	}
	boolean isDanceTimer;
	boolean dancing;
	int danceIndex;
	int[] DANCE = {1, 3, 0, 0, 4, 0, 0, 3, 0, 0, 4, 0, 0, 
            0, 0, 0, 0, 0, 0, 0, 
            2, 3, 0, 0, 4, 0, 0, 3, 0, 0, 4, 0, 0, 0, 
            0, 0, 0, 0, 0, 0};

	private void moveDanceServo(int moveid){
		ServoMsg servo = null;
		switch (moveid) {
		case 0:
			servo = MoveMsg.newInstanceZeroPosition();
			mAdkConnector.sendCommand(servo);
			break;
		case 1:
			//1b72
			servo = SpeedMsg.newInstancePosition(ServoMsg.ROLL_ID, (short) 0x06);
			mAdkConnector.sendCommand(servo);
			servo = MoveMsg.newInstanceHexPosition(ServoMsg.ROLL_ID, (byte)0x30, (byte)0x72);
			mAdkConnector.sendCommand(servo);
			break;
		case 2:
			servo = SpeedMsg.newInstancePosition(ServoMsg.ROLL_ID, (short) 0x06);
			mAdkConnector.sendCommand(servo);
			servo = MoveMsg.newInstanceHexPosition(ServoMsg.ROLL_ID, (byte)0x10, (byte)0x72);
			mAdkConnector.sendCommand(servo);
			break;
		case 3:
			servo = SpeedMsg.newInstancePosition(ServoMsg.SLOPE_ID, (short) 0x30);
			mAdkConnector.sendCommand(servo);
			servo = MoveMsg.newInstanceHexPosition(ServoMsg.SLOPE_ID, (byte)0x40, (byte)0x4c);
			mAdkConnector.sendCommand(servo);
			break;
		case 4:
			servo = SpeedMsg.newInstancePosition(ServoMsg.SLOPE_ID,  (short) 0x30);
			mAdkConnector.sendCommand(servo);
			servo = MoveMsg.newInstanceHexPosition(ServoMsg.SLOPE_ID, (byte)0x30, (byte)0x4c);
			mAdkConnector.sendCommand(servo);
			break;
		case 5:
			servo = SpeedMsg.newInstancePosition(ServoMsg.ROLL_ID, (short) 0x06);
			mAdkConnector.sendCommand(servo);
			servo = MoveMsg.newInstanceHexPosition(ServoMsg.ROLL_ID, (byte)0x3a, (byte)0x4c);
			mAdkConnector.sendCommand(servo);
			break;
		case 6:
			servo = SpeedMsg.newInstancePosition(ServoMsg.SLOPE_ID,  (short) 0x30);
			mAdkConnector.sendCommand(servo);
			servo = MoveMsg.newInstanceHexPosition(ServoMsg.SLOPE_ID, (byte)0x3a, (byte)0x4c);
			mAdkConnector.sendCommand(servo);
			break;
		default:
			break;
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	/**
	 * カメラの映像をプレビュー画面に描画する処理
	 */
	private SurfaceHolder.Callback mCameraCallback = new SurfaceHolder.Callback(){

		public void surfaceCreated(SurfaceHolder holder) {
			camera = Camera.open(CameraInfo.CAMERA_FACING_FRONT);

			try {
				camera.setPreviewDisplay(holder);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
			camera.startPreview();
			Camera.Parameters params = camera.getParameters();
			List<Size> previewSizes = params.getSupportedPreviewSizes();
			for (Size size : previewSizes) {
				Log.d("camera param", size.width + ":" + size.height);
			}
			params.setPreviewSize(1280, 720);
			camera.setParameters(params);
			camera.setFaceDetectionListener(detectListener);
			camera.startFaceDetection();
			camera.startPreview();
		}

		public void surfaceDestroyed(SurfaceHolder holder) {
			camera.stopFaceDetection();
			camera.release();
			camera = null;
		}

	};
	Timer stopTimer;
	Timer danceTimer;
	/**
	 * プレビューに表示された映像に顔が認識された時の処理。
	 */
	private FaceDetectionListener detectListener = new FaceDetectionListener(){
		private static final int SIZE_MAX = 2000 * 2000;
		@Override
		public void onFaceDetection(Face[] faces, Camera camera) {
			if(faces.length == 0){
				Log.i(TAG, "not found faces.");
				side = upside = 0;
				Log.i(TAG, "isDanceTimer:"+isDanceTimer);
				if(!isDanceTimer){
					isDanceTimer = true;
					TimerTask danceTask = new TimerTask() {
						@Override
						public void run() {
							dancing = true;
							Log.i(TAG, "dancing flug enable.");
							TimerTask stopTask = new TimerTask() {
								@Override
								public void run() {
									Log.i(TAG, "dancing stoped.");
									dancing = false;
									isDanceTimer = false;
								}
							};
							stopTimer = new Timer();
							stopTimer.schedule(stopTask, 10 * 1000);
							Log.i(TAG, "start dance stop timer.");
						}
					};
					danceTimer = new Timer();
					danceTimer.schedule(danceTask, 10 * 1000);
					Log.i(TAG, "danceTimer start.");
				}
			}else{
				Log.i(TAG, "found faces.");
				isDanceTimer = false;
				if(danceTimer!=null)danceTimer.cancel();
				if(stopTimer!=null)stopTimer.cancel();
				Log.i(TAG, "danceTimer cancel.");
				if(dancing)dancing = false;
				Log.i(TAG, "dancing flug disable.");
			}
			
			for (Face face : faces) {
				// Log.i("face",
				// "id:"+face.id+" score:"+face.score+" leftEye:"+face.leftEye+" rightEye:"+face.rightEye+" mouth:"+face.mouth);
				Rect rect = face.rect;

				// xy 1204:-1024の比率
				 Log.d("face",
				 "face rect"+rect.left+":"+rect.right+":"+rect.top+":"+rect.bottom);

				int width = rect.right - rect.left;
				int height = rect.bottom - rect.top;

				int horizontalCenter = rect.centerX();
				int verticalCenter = rect.centerY();

				// 大きいほど近い　小さいほど遠い
				// TODO 計算で0~1の値に「距離」に変換したい
				int size = width * height;
				Log.d("face", "face point:[" + horizontalCenter + ":"
						+ verticalCenter + "]" + " face [" + width + ":" + height
						+ "]");

				int depth = SIZE_MAX / size; // 近い1-100遠い
				double hypo = Math.sqrt(Math.pow(horizontalCenter, 2)
						+ Math.pow(verticalCenter, 2));
				Log.d("face", "face depth:" + depth + " hypo:" + hypo);
				
				upside = (short) (horizontalCenter+700);
				side = (short) (verticalCenter+700);

				Canvas canvas = mOverlayHolder.lockCanvas();
				canvas.drawColor(0, Mode.CLEAR);
				
				Matrix matrix = new Matrix();
				matrix.setScale(-1, 1);
			    matrix.postScale(mOverlaySurfaceView.getWidth() / 2000f, mOverlaySurfaceView.getHeight() / 2000f);
			    matrix.postTranslate(mOverlaySurfaceView.getWidth() / 2f, mOverlaySurfaceView.getHeight() / 2f);
			    int saveCount = canvas.save();
			    canvas.concat(matrix);

				Paint paint = new Paint();
				paint.setColor(Color.WHITE);
				paint.setStyle(Paint.Style.STROKE);
				paint.setStrokeWidth(3);

				canvas.drawRect(face.rect, paint);
				canvas.restoreToCount(saveCount);
				mOverlayHolder.unlockCanvasAndPost(canvas);

			}
		}
	};
	Handler handler = new Handler();

}
