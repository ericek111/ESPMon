package me.lixko.espmonc;

public class GFXLib {
	public static final int NUM_BIN = 2;
	public static final int NUM_OCT = 8;
	public static final int NUM_DEC = 10;
	public static final int NUM_HEX = 16;
	
	public static byte[] setRotation(int rot) {
		byte[] barr = new byte[64];
		barr[0] = 0x11;
		barr[1] = (byte)(rot & 0xff);
		return barr;
	}
		
	public static byte[] fillScreen(int color) {
		byte[] barr = new byte[64];
		barr[0] = 0x22;
		barr[1] = (byte)((color >> 8) & 0xff);
		barr[2] = (byte)(color & 0xff);
		return barr;
	}

	public static byte[] drawPixel(int posx, int posy, int color) {
		byte[] barr = new byte[64];
		barr[0] = 0x23;
		barr[1] = (byte)((posx >> 8) & 0xff);
		barr[2] = (byte)(posx & 0xff);
		
		barr[3] = (byte)((posy >> 8) & 0xff);
		barr[4] = (byte)(posy & 0xff);
		
		barr[5] = (byte)((color >> 8) & 0xff);
		barr[6] = (byte)(color & 0xff);
		return barr;
	}
	
	public static byte[] drawFastVLine(int posx, int posy, int h, int color) {
		byte[] barr = new byte[64];
		barr[0] = 0x24;
		barr[1] = (byte)((posx >> 8) & 0xff);
		barr[2] = (byte)(posx & 0xff);
		
		barr[3] = (byte)((posy >> 8) & 0xff);
		barr[4] = (byte)(posy & 0xff);
		
		barr[5] = (byte)((h >> 8) & 0xff);
		barr[6] = (byte)(h & 0xff);
		
		barr[7] = (byte)((color >> 8) & 0xff);
		barr[8] = (byte)(color & 0xff);
		return barr;
	}
	
	public static byte[] drawFastHLine(int posx, int posy, int w, int color) {
		byte[] barr = drawFastHLine(posx, posy, w, color);
		barr[0] = 0x25;
		return barr;
	}
	
	public static byte[] fillRect(int posx, int posy, int w, int h, int color) {
		byte[] barr = new byte[64];
		barr[0] = 0x26;
		barr[1] = (byte)((posx >> 8) & 0xff);
		barr[2] = (byte)(posx & 0xff);
		
		barr[3] = (byte)((posy >> 8) & 0xff);
		barr[4] = (byte)(posy & 0xff);
		
		barr[5] = (byte)((w >> 8) & 0xff);
		barr[6] = (byte)(w & 0xff);
		
		barr[7] = (byte)((h >> 8) & 0xff);
		barr[8] = (byte)(h & 0xff);
		
		barr[9] = (byte)((color >> 8) & 0xff);
		barr[10] = (byte)(color & 0xff);
		return barr;
	}
	
	public static byte[] drawCircle(int posx, int posy, int r, int color) {
		byte[] barr = new byte[64];
		barr[0] = 0x31;
		barr[1] = (byte)((posx >> 8) & 0xff);
		barr[2] = (byte)(posx & 0xff);
		
		barr[3] = (byte)((posy >> 8) & 0xff);
		barr[4] = (byte)(posy & 0xff);
		
		barr[5] = (byte)((r >> 8) & 0xff);
		barr[6] = (byte)(r & 0xff);
		
		barr[7] = (byte)((color >> 8) & 0xff);
		barr[8] = (byte)(color & 0xff);
		return barr;
	}
	
	public static byte[] drawCircleHelper(int posx, int posy, int r, int cornername, int color) {
		byte[] barr = new byte[64];
		barr[0] = 0x32;
		barr[1] = (byte)((posx >> 8) & 0xff);
		barr[2] = (byte)(posx & 0xff);
		
		barr[3] = (byte)((posy >> 8) & 0xff);
		barr[4] = (byte)(posy & 0xff);
		
		barr[5] = (byte)((r >> 8) & 0xff);
		barr[6] = (byte)(r & 0xff);
		
		barr[7] = (byte)((cornername >> 8) & 0xff);
		barr[8] = (byte)(cornername & 0xff);
		
		barr[9] = (byte)((color >> 8) & 0xff);
		barr[10] = (byte)(color & 0xff);
		return barr;
	}
	
	public static byte[] fillCircle(int posx, int posy, int r, int color) {
		byte[] barr = drawCircle(posx, posy, r, color);
		barr[0] = 0x33;
		return barr;
	}
	
	public static byte[] fillCircleHelper(int posx, int posy, int r, int cornername, int delta, int color) {
		byte[] barr = new byte[64];
		barr[0] = 0x34;
		barr[1] = (byte)((posx >> 8) & 0xff);
		barr[2] = (byte)(posx & 0xff);
		
		barr[3] = (byte)((posy >> 8) & 0xff);
		barr[4] = (byte)(posy & 0xff);
		
		barr[5] = (byte)((r >> 8) & 0xff);
		barr[6] = (byte)(r & 0xff);
		
		barr[7] = (byte)((cornername >> 8) & 0xff);
		barr[8] = (byte)(cornername & 0xff);

		barr[9] = (byte)((delta >> 8) & 0xff);
		barr[10] = (byte)(delta & 0xff);
		
		barr[11] = (byte)((color >> 8) & 0xff);
		barr[12] = (byte)(color & 0xff);
		return barr;
	}
	
	public static byte[] drawRoundRect(int posx, int posy, int w, int h, int r, int color) {
		byte[] barr = new byte[64];
		barr[0] = 0x35;
		barr[1] = (byte)((posx >> 8) & 0xff);
		barr[2] = (byte)(posx & 0xff);
		
		barr[3] = (byte)((posy >> 8) & 0xff);
		barr[4] = (byte)(posy & 0xff);
		
		barr[5] = (byte)((w >> 8) & 0xff);
		barr[6] = (byte)(w & 0xff);
		
		barr[7] = (byte)((h >> 8) & 0xff);
		barr[8] = (byte)(h & 0xff);

		barr[9] = (byte)((r >> 8) & 0xff);
		barr[10] = (byte)(r & 0xff);
		
		barr[11] = (byte)((color >> 8) & 0xff);
		barr[12] = (byte)(color & 0xff);
		return barr;
	}
	
	public static byte[] fillRoundRect(int posx, int posy, int w, int h, int r, int color) {
		byte[] barr = drawRoundRect(posx, posy, w, h, r, color);
		barr[0] = 0x36;
		return barr;
	}
	
	public static byte[] setCursor(int posx, int posy) {
		byte[] barr = new byte[64];
		barr[0] = 0x51;
		barr[1] = (byte)((posx >> 8) & 0xff);
		barr[2] = (byte)(posx & 0xff);
		
		barr[3] = (byte)((posy >> 8) & 0xff);
		barr[4] = (byte)(posy & 0xff);
		return barr;
	}
	
	public static byte[] setTextColor(int color) {
		byte[] barr = new byte[64];
		barr[0] = 0x52;
		barr[1] = (byte)((color >> 8) & 0xff);
		barr[2] = (byte)(color & 0xff);
		return barr;
	}
	
	public static byte[] setTextColor(int color, int bg) {
		byte[] barr = new byte[64];
		barr[0] = 0x53;
		barr[1] = (byte)((color >> 8) & 0xff);
		barr[2] = (byte)(color & 0xff);
		
		barr[3] = (byte)((bg >> 8) & 0xff);
		barr[4] = (byte)(bg & 0xff);
		return barr;
	}
	
	public static byte[] setTextSize(int size) {
		byte[] barr = new byte[64];
		barr[0] = 0x54;
		barr[1] = (byte)(size & 0xff);
		return barr;
	}
	
	public static byte[] setTextWrap(boolean wrap) {
		byte[] barr = new byte[64];
		barr[0] = 0x55;
		barr[1] = (byte) (wrap ? 0x01 : 0x00);
		return barr;
	}
	
	public static byte[] println(int off, int len) {
		byte[] barr = new byte[64];
		barr[0] = 0x56;
		barr[1] = (byte)((off >> 8) & 0xff);
		barr[2] = (byte)(off & 0xff);
		
		barr[3] = (byte)((len >> 8) & 0xff);
		barr[4] = (byte)(len & 0xff);
		return barr;
	}
	
	public static byte[] shortPushToBuffer(int off, byte[] data) {
		byte[] barr = new byte[64];
		barr[0] = 0x61;
		barr[1] = (byte)((off >> 8) & 0xff);
		barr[2] = (byte)(off & 0xff);
		
		barr[3] = (byte)((data.length >> 8) & 0xff);
		barr[4] = (byte)(data.length & 0xff);
		
		for (int i = 5; (i < barr.length && i-5 < data.length); i++) {
			barr[i] = data[i-5];
		}
		return barr;
	}
	
	public static byte[] beginPushingToBuffer(int off, int len) {
		byte[] barr = new byte[64];
		barr[0] = 0x62;
		barr[1] = (byte)((off >> 8) & 0xff);
		barr[2] = (byte)(off & 0xff);
		
		barr[3] = (byte)((len >> 8) & 0xff);
		barr[4] = (byte)(len & 0xff);
		return barr;
	}
	
	public static byte[] getFromBuffer(int off, int len) {
		byte[] barr = new byte[64];
		barr[0] = 0x63;
		barr[1] = (byte)((off >> 8) & 0xff);
		barr[2] = (byte)(off & 0xff);
		
		barr[3] = (byte)((len >> 8) & 0xff);
		barr[4] = (byte)(len & 0xff);
		return barr;
	}
	
	public static byte[] emptyBuffer() {
		byte[] barr = new byte[64];
		barr[0] = 0x69;
		return barr;
	}
	
}
