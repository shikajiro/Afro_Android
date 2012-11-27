package me.kojika_ya.afro.adk;

public class SpeedMsg implements ServoMsg{
	private Short preamble;
	private Short id;
	private Short sc;
	private Short speed;
	
	public SpeedMsg(short id) {
		super();
		this.preamble = 0xAA;
		this.id = (short) (id + 0xC0);
		this.sc = 0x02;
	}
	

	public static SpeedMsg newInstancePosition(short id, short speed) {
		SpeedMsg msg = new SpeedMsg(id);
		if(0 <= speed && speed < 128){
			msg.setSpeed(speed);
		}else{
			msg.setSpeed((short) 0);
		}
		return msg;
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
				+ ", speed=" + Integer.toHexString(speed) + "]";
	}


	public Short getSpeed() {
		return speed;
	}


	public void setSpeed(Short speed) {
		this.speed = speed;
	}
	
}
