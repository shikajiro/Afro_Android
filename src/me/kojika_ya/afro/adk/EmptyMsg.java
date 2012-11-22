package me.kojika_ya.afro.adk;

public class EmptyMsg implements ServoMsg{

	private static final String TAG = "MoveMsg";
	private Short preamble;
	private Short id;
	private Short position;
	public EmptyMsg() {
		super();
		this.preamble = 0x00;
	}
	@Override
	public byte[] toMessage() {
		byte[] msg = new byte[1];
		msg[0] = preamble.byteValue();
		return msg;
	}
	
	@Override
	public String toString() {
		return "EmptyMsg [preamble=0x" + Integer.toHexString(preamble);
	}

}
