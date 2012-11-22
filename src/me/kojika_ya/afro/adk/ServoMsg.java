package me.kojika_ya.afro.adk;

public interface ServoMsg {
	public static final short ROLL_ID = 0;
	public static final short SLOPE_ID = 1;

	public byte[] toMessage();
}
