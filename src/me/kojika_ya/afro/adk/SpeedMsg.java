package me.kojika_ya.afro.adk;

public class SpeedMsg implements ServoMsg{
	private Short preamble;
	private Short id;
	private Short sc;
	private Short speed;
	
	public SpeedMsg(short id, short speed) {
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
		this.id = (short) (id + 0xC0);
		this.sc = 0x02;
		if(0 <= speed && speed < 128){
			this.speed = speed;
		}else{
			this.speed = 0;
		}
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
		msg[2] = sc.byteValue();
		msg[3] = speed.byteValue();
		return msg;
	}

	@Override
	public String toString() {
		return "SpeedMsg [preamble=" + preamble + ", id=" + id + ", sc=" + sc
				+ ", speed=" + speed + "]";
	}
	
}
