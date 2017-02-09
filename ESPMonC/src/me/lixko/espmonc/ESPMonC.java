package me.lixko.espmonc;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.Mem;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

public class ESPMonC {
	
	public static int COMMDELAY = 10;
	public static boolean debug = false;
	static InetSocketAddress IPAddress;
	
	ProcessBuilder smips = new ProcessBuilder("nvidia-smi","--format=csv,nounits", "--query-gpu=clocks.gr,clocks.max.gr,clocks.mem,clocks.max.mem,temperature.gpu,power.draw,memory.used,memory.total,utilization.gpu");
	Process smiproc;
	HashMap<String, String> smivalues = new HashMap<String, String>();
	static Sigar sigar = new Sigar();
	
	Random rand;
	public ESPMonC() {
		rand = new Random();
	}
	
	public int randInt(int min, int max) {
	    return rand.nextInt((max - min) + 1) + min;
	}
	
	public static short RGBto565(int rgb) {
	    return (short) ( (((rgb >> 19) & 0x1f) << 11) | (((rgb >> 10) & 0x3f) <<  5) | ((rgb >>  3) & 0x1f) );
		//return (short) (rgb << 3);
	}
	public static int RGBto565c(int c) {
		return ((( ((c >> 16) & 0xFF) >> 3) & 0b00011111) << 11) | ((( ((c >> 8) & 0xFF) >> 2) & 0b00111111) << 5) | (( (c & 0xFF) >> 3) & 0b00011111);
	}
	public static int RGBto565c(int r, int g, int b) {
		return ((( r >> 3) & 0b00011111) << 11) | ((( g >> 2) & 0b00111111) << 5) | (( b >> 3) & 0b00011111);
	}
	
	final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
	public static String bytesToHex(byte[] bytes) {
	    char[] hexChars = new char[bytes.length * 2];
	    for ( int j = 0; j < bytes.length; j++ ) {
	        int v = bytes[j] & 0xFF;
	        hexChars[j * 2] = hexArray[v >>> 4];
	        hexChars[j * 2 + 1] = hexArray[v & 0x0F];
	    }
	    return new String(hexChars);
	}
	
	public static void ex(OutputStream os, byte[] barr) throws IOException {
		try {
			os.write(barr);
			Thread.sleep(COMMDELAY);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public static void ex(DatagramSocket ucs, byte[] barr) throws IOException {
		try {
			DatagramPacket usendPacket = new DatagramPacket(barr, barr.length, IPAddress);
			ucs.send(usendPacket);
			Thread.sleep(COMMDELAY);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void updateSMI() {
		String[] header = new String[]{};
		String[] values = new String[]{};
		try {
			smiproc = smips.start();
			BufferedReader in = new BufferedReader(new InputStreamReader(smiproc.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				if(line.startsWith("clocks.current")) header = line.split(", ");
				if(line.length() > 4) values = line.split(", ");
			}
			smiproc.waitFor();
			in.close();
		} catch (IOException | InterruptedException e2) {
			e2.printStackTrace();
		} 
		if(header.length > 0 && values.length > 0 && values.length == header.length) {
			for(int s = 0; s < header.length; s++) {
				smivalues.put(header[s].split(" ")[0], values[s]);
			}
		}
	}
	
	public void main() {		
		smips.redirectErrorStream(true);
		
		String dataToBeSent = "";
		String incomingData;
		int result = 0;
		BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
		Socket clientSocket = null;
		boolean repeat = true;
		int repeati = 0;
		
		byte[] udata;
		DatagramPacket usendPacket;
		DatagramSocket uclientSocket;
		
		IPAddress = new InetSocketAddress("192.168.100.21", 7099);

		while(repeat)
		try {
			uclientSocket = new DatagramSocket();
			clientSocket = new Socket();
			clientSocket.connect(IPAddress, 10000);
			clientSocket.setSoTimeout(3000);

			OutputStream outToServer = clientSocket.getOutputStream();
			BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			/*while(dataToBeSent != "q") {
				dataToBeSent = inFromUser.readLine();
				outToServer.writeBytes(dataToBeSent + '\n');
			}*/
			//int[] color = new int[]{ 0x000000, 0x7f0000, 0xff0000, 0xff7f00, 0xffff00, 0xffff7f, 0xffffff, 0xff007f, 0xff00ff, 0x007fff, 0x00ffff};
			repeati = 0;
			byte[] by = new byte[64];
			long startTime = 0;
			long endTime = 0;
			/*by[0] = (byte) 0xf9;
			outToServer.write(by);*/
			
			uclientSocket.connect(IPAddress);
			
			Thread.sleep(400);
			
			//ex(uclientSocket, GFXLib.setTextColor(RGBto565c(0xffffff), RGBto565c(0x000000)));
			GFXLib.setTextColor(RGBto565c(0x0), RGBto565c(0xffffff));
			ex(uclientSocket, GFXLib.setCursor(0, 0));
			ex(uclientSocket, GFXLib.setTextSize(1));
			ex(uclientSocket, GFXLib.setRotation(0));
			ex(uclientSocket, GFXLib.fillScreen(0xffffff));
		    
			/*ex(outToServer, GFXLib.setTextColor(RGBto565c(0xffffff), RGBto565c(0x000000)));			
			ex(outToServer, GFXLib.setCursor(0, 0));
			ex(outToServer, GFXLib.setTextSize(1));
			ex(outToServer, GFXLib.setRotation(0));
			ex(outToServer, GFXLib.fillScreen(0xffffff));*/
			
			/*by = GFXLib.fillScreen(0);
			outToServer.write(by);
			Thread.sleep(COMMDELAY);*/
			Thread.sleep(600);
			
			for(int i = 0; i < 100000; i++) {
				if(!clientSocket.isConnected()) break;
				startTime = System.nanoTime();
				//by = GFXLib.fillRect(randInt(0, 320), randInt(0, 240), 10, 10, RGBto565c(randInt(0, 255), randInt(0, 255), randInt(0, 255)));
				String str = i + "";
				
				/*ex(outToServer, GFXLib.setCursor((int)(i / 40)*24, (i % 40)*8));
				ex(outToServer, GFXLib.shortPushToBuffer(0, str.getBytes()));			
				ex(outToServer, GFXLib.println(0, str.getBytes().length));*/
				
				//ex(uclientSocket, GFXLib.setCursor(((int)(i / 40)*24) % 240, (i % 40)*8));
				updateSMI();
				int s = 0;
				/*for (Map.Entry<String, String> entry : smivalues.entrySet()) {
					str = entry.getKey() + "\n" + entry.getValue() + "\n";
					ex(uclientSocket, GFXLib.setCursor(((int)(s / 40)*24) % 240, ((s * 3) % 40)*8));
					ex(uclientSocket, GFXLib.shortPushToBuffer(0, str.getBytes()));			
					ex(uclientSocket, GFXLib.println(0, str.getBytes().length));
					s++;
				}*/
				

				

				
				//ex(uclientSocket, GFXLib.setTextSize(2));

				
				final int recheight = 200;
				int recfilled = 0;
				
				recfilled = recheight * (Integer.parseInt(smivalues.get("memory.used"))) / Integer.parseInt(smivalues.get("memory.total"));
				ex(uclientSocket, GFXLib.fillRect(20 + recfilled, 250, recheight - recfilled, 40, 0));
				ex(uclientSocket, GFXLib.fillRect(20, 250, recfilled, 40, RGBto565c(0xff0000)));
				
				
				ex(uclientSocket, GFXLib.setCursor(20, 242));
				str = "Used GPU memory: " + smivalues.get("memory.used") + " / " + smivalues.get("memory.total") + " MB";
				ex(uclientSocket, GFXLib.shortPushToBuffer(0, str.getBytes()));			
				ex(uclientSocket, GFXLib.println(0, str.getBytes().length));

				
				recfilled = recheight * (Integer.parseInt(smivalues.get("utilization.gpu"))) / 100;
				ex(uclientSocket, GFXLib.fillRect(20 + recfilled, 180, recheight - recfilled, 40, 0));
				ex(uclientSocket, GFXLib.fillRect(20, 180, recfilled, 40, RGBto565c(0x00ff00)));
								
				ex(uclientSocket, GFXLib.setCursor(20, 172));
				str = "GPU Utilization";
				ex(uclientSocket, GFXLib.shortPushToBuffer(0, str.getBytes()));			
				ex(uclientSocket, GFXLib.println(0, str.getBytes().length));
				
		        Mem mem = null;
		        try {
		            mem = sigar.getMem();
		        } catch (SigarException se) {
		            se.printStackTrace();
		        }
		        if(mem != null){
		        	str = "Used RAM: " + mem.getUsed()/1024/1024 + " / " + mem.getTotal()/1024/1024 + " / " + mem.getActualUsed()/1024/1024 + " MB";
		        	recfilled = (int) (recheight * mem.getActualUsed() / mem.getTotal());
		        	int recfilled2 = (int) (recheight * mem.getUsed() / mem.getTotal());
		        	
					ex(uclientSocket, GFXLib.fillRect(20 + recfilled2, 120, recheight - recfilled2, 40, 0));
					ex(uclientSocket, GFXLib.fillRect(20, 120, recfilled2, 40, RGBto565c(0x845F3B)));
					ex(uclientSocket, GFXLib.fillRect(20, 120, recfilled, 40, RGBto565c(0x007FFE)));
					
		        } else {
		        	recfilled = 0;
		        	str = "RAM unavailable!" ;
		        }
		        
								
				ex(uclientSocket, GFXLib.setCursor(20, 112));
				ex(uclientSocket, GFXLib.shortPushToBuffer(0, str.getBytes()));			
				ex(uclientSocket, GFXLib.println(0, str.getBytes().length));
				
				CpuPerc[] cpus = null;
				try {
					cpus = sigar.getCpuPercList();
				} catch (SigarException e) {
					e.printStackTrace();
				}
				
				if(cpus != null) {
					int barheight = 20;
					int y = 20;
					setDelay(2);
					ex(uclientSocket, GFXLib.fillRect(20, y, recheight, barheight * cpus.length, 0));
					for(int x = 0; x < cpus.length; x++)  {
						int recfilledo = 0;
						
						recfilled = (int) (cpus[x].getUser() / 1 * recheight);
						ex(uclientSocket, GFXLib.fillRect(20 + recfilledo, y, recfilled, barheight, RGBto565c(0x007FFE)));
						recfilledo += recfilled;

						recfilled = (int) (cpus[x].getSys() / 1 * recheight);
						ex(uclientSocket, GFXLib.fillRect(20 + recfilledo, y, recfilled, barheight, RGBto565c(0x00EEFE)));
						recfilledo += recfilled;
						
						recfilled = (int) (cpus[x].getNice() / 1 * recheight);
						ex(uclientSocket, GFXLib.fillRect(20 + recfilledo, y, recfilled, barheight, RGBto565c(0x00FE5A)));
						recfilledo += recfilled;
						
						recfilled = (int) (cpus[x].getWait() / 1 * recheight);
						ex(uclientSocket, GFXLib.fillRect(20 + recfilledo, y, recfilled, barheight, RGBto565c(0xFEBE00)));
						recfilledo += recfilled;
						y += barheight;
					}
					setDelay(10);
				}
				

				/*ex(uclientSocket, GFXLib.setCursor(((int)(i / 40)*24) % 240, (i % 40)*8));
				ex(uclientSocket, GFXLib.shortPushToBuffer(0, str.getBytes()));			
				ex(uclientSocket, GFXLib.println(0, str.getBytes().length));*/
				
				//char[] cbuf = new char[64];
				//inFromServer.read(cbuf, 0, 64);
				//result = inFromServer.read();
				endTime = System.nanoTime();
				if(debug) System.out.println(i + ": FROM SERVER: " + result + " at " + ((endTime - startTime) / 1000000) + " ms.");
				//System.out.println(bytesToHex(result.getBytes()));
				
				Thread.sleep(COMMDELAY);
				
				/*repeat = false;
				break;*/
				if(i == 399) {
					i = 0;
					ex(uclientSocket, GFXLib.fillScreen(0xffffff));
					Thread.sleep(500);
				}
			}
			
			// 100 000 @ 40
			
			//outToServer.write(fillRect(10, 10, 50, 50, RGBto565c(0xff0000)));
			
			clientSocket.close();
			
		} catch (IOException | InterruptedException e) {
			if(e instanceof SocketTimeoutException && clientSocket != null) {
				repeat = true;
				System.out.println("Timed out! Connecting again...");
				try {
					clientSocket.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			} else if(e instanceof ConnectException) {
				repeat = true;
				System.out.println("ConnectException: " + e.getMessage());
			} else {
				repeat = false;
				e.printStackTrace();
			}
			repeati++;
			if(repeati == 5 && repeat) repeat = false;
		}
		System.out.println("Exiting app...");
	}
	
	public void setDelay(int delay) {
		this.COMMDELAY = delay;
	}
}
