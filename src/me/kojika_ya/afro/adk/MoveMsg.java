package me.kojika_ya.afro.adk;


/**
 * @author shikajiro USBアクセサリーと情報をやりとりするデータを詰めるDTO
 */
public class MoveMsg implements ServoMsg{
	
	@SuppressWarnings("unused")
	private static final String TAG = "MoveMsg";
	private Short preamble;
	private Short id;
	private Short position;

	public MoveMsg(short id){
		this.id = (short) (id + 0x80);
		this.preamble = 0xAA;
	}
	
	public static MoveMsg newInstancePosition(short id, short position) {
		MoveMsg msg = new MoveMsg(id);
		switch (id) {
		case ROLL_ID:
			msg.setPosition(map(position, 0, 1500, 5000, 10000));
			break;
		case SLOPE_ID:
			msg.setPosition(map(position, 0, 1500, 12288, 16384));
			break;
		default:
			break;
		}
		return msg;
	}
	
	public static MoveMsg newInstanceHexPosition(short id, byte position1, byte position2){
		MoveMsg msg = new MoveMsg(id);
		msg.setPosition((short) ((position1 << 8) + position2));
		return msg;
	}
	
	public static MoveMsg newInstanceZeroPosition(){
		MoveMsg msg = new MoveMsg((short) 0);
		msg.setId((short)0x00);
		msg.setPosition((short) 0);
		return msg;
	}
	
	public Short getPreamble() {
		return preamble;
	}

	public void setPreamble(Short preamble) {
		this.preamble = preamble;
	}

	public Short getId() {
		return id;
	}

	public void setId(Short id) {
		this.id = id;
	}

	public Short getPosition() {
		return position;
	}

	public void setPosition(Short position) {
		this.position = position;
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
	private static short map(short x, int inMin, int inMax, int outMin, int outMax){
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
		byte[] msg = new byte[4];
		msg[0] = preamble.byteValue();
		msg[1] = id.byteValue();
		msg[3] = position.byteValue();
		msg[2] = (byte) (position >> 8);
		return msg;
	}

	@Override
	public String toString() {
		return "MoveMsg [preamble=0x" + Integer.toHexString(preamble) + ", id=0x" + Integer.toHexString(id) + ", position="
				+ Integer.toHexString(position) + "]";
	}

}
