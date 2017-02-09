package me.lixko.espmonc;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Random;

/* PROTOCOL:
 * 
 * Request:
 * 
 * 0x03 - proccessBuffer [2 offset, 2 length]
 * 0x09 - ping []
 * 
 * 0x11 - setRotation [1 rot]
 * 
 * 0x22 - fillScreen [2 color]
 * 0x23 - drawPixel [2 posx, 2 posy, 2 color]
 * 0x24 - drawFastVLine [2 posx, 2 posy, 2 height, 2 color]
 * 0x25 - drawFastHLine [2 posx, 2 posy, 2 width, 2 color]
 * 0x26 - fillRect [2 posx, 2 posy, 2 height, 2 width, 2 color]
 * 
 * 0x31 - drawCircle [2 x0, 2 y0, 2 radius, 2 color]
 * 0x32 - drawCircleHelper [2 x0, 2 y0, 2 radius, 1 cornername, 2 color]
 * 0x33 - fillCircle [2 x0, 2 y0, 2 radius, 2 color]
 * 0x34 - fillCircleHelper [2 x0, 2 y0, 2 radius, 1 cornername, 2 delta, 2 color]
 * 0x35 - drawRoundRect [2 posx, 2 posy, 2 width, 2 height, 2 radius, 2 color]
 * 0x36 - fillRoundRect [2 posx, 2 posy, 2 width, 2 height, 2 radius, 2 color]
 * 
 * 0x51 - setCursor [2 posx, 2 posy]
 * 0x52 - setTextColor [2 color]
 * 0x53 - setTextColor [2 color, 2 bg]
 * 0x54 - setTextSize [1 size]
 * 0x55 - setTextWrap [1 wrap]
 * 0x56 - println [2 offset, 2 length]
 * 
 * 0x61 - shortPushToBuffer [2 offset, 2 length]
 * 0x62 - beginPushingToBuffer [2 offset, 2 length]
 * 0x63 - >TCP getFromBuffer [2 offset, 2 length]
 * 0x68 - emptyBuffer [2 offset, 2 length]
 * 0x69 - emptyBuffer []
 * 
 * Response:
 * 0x02 - Not implemented!
 * 
 * 
 * 
 * 
 * http://nvidia.custhelp.com/app/answers/detail/a_id/3751/~/useful-nvidia-smi-queries
 * nvidia-smi --format=csv,nounits --query-gpu=clocks.gr,clocks.max.gr,clocks.mem,clocks.max.mem,temperature.gpu,power.draw,memory.used,memory.total,utilization.gpu -l 1
 * 
 * nvidia-smi dmon -s pucm
 */

public class Main {
	
	public static void main(String[] args) throws InterruptedException {
		ESPMonC emonc = new ESPMonC();
		try {
			while(true) {
				emonc.main();
				Thread.sleep(500);
				System.out.println("Repeating...");
			}
		} catch(Exception ex) {
			System.out.println("CRASHED! Repeating in 3 seconds...");
			ex.printStackTrace();
			Thread.sleep(3000);
		}
		
	}

}
