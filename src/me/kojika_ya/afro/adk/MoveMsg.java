package me.kojika_ya.afro.adk;

import android.util.Log;

/**
 * @author shikajiro USBアクセサリーと情報をやりとりするデータを詰めるDTO
 */
public class MoveMsg implements ServoMsg{
	
	private static final String TAG = "MoveMsg";
	private Short preamble;
	private Short id;
	private Short position;

	public MoveMsg(short id, short position) {
		super();
		switch (id) {
		case ROLL_ID:
		case SLOPE_ID:
			break;
		default:
			//指定が想定外の場合、強制定期に横回転にする。
			id = ROLL_ID;
			return;
		}
		this.preamble = 0xAA;
		this.id = (short) (id + 0x80);
		// 3500-11500はまだ使えないので絞る。
//		this.position = map(position, 0, 1500, 3500, 11500);
		this.position = map(position, 0, 1500, 5000, 10000);
	}

	/**
	 * 値を特定の範囲に変換する。
	 * @param x
	 * @param inMin
	 * @param inMax
	 * @param outMin
	 * @param outMax
	 * @return
	 */
	private short map(short x, int inMin, int inMax, int outMin, int outMax){
		if(x < inMin || inMax < x){
			return 0;
		}
		short result = (short) ((x - inMin) * (outMax - outMin) / (inMax - inMin) + outMin);
		if(result < outMin || outMax < result){
			return 0;
		}
		return result;
	}

	/**
	 * 移動位置をADK側のAPIに合わせたbyte配列に変換する。
	 * 
	 * @return
	 */
	@Override
	public byte[] toMessage() {
		byte[] msg = new byte[64];
		msg[0] = preamble.byteValue();
		msg[1] = id.byteValue();
		msg[3] = position.byteValue();
		msg[2] = (byte) (position >> 8);
		return msg;
	}

	@Override
	public String toString() {
		return "MoveMsg [preamble=0x" + Integer.toHexString(preamble) + ", id=0x" + Integer.toHexString(id) + ", position="
				+ position + "]";
	}

}
