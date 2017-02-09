/*
  TFT2.2 ILI9341 from top left:
  MISO  D6 (HMISO/GPIO12)
  LED   +3.3V
  SCK   D5 (HSCLK/GPIO14)
  MOSI  D7 (HMOSI/GPIO13)
  DC    D4 (GPIO2)
  RST   RST or 3V3
  CS    D1 (GPIO5)
  GND   GND
  VCC   +3.3V
*/

#include "SPI.h"
#include <ESP8266WiFi.h>
#include <WiFiClient.h>
#include <ESP8266WebServer.h>
#include <WiFiUdp.h>

//#include "Adafruit_GFX.h"
//#include "Adafruit_ILI9341.h"

#include "TFT_ILI9341_ESP.h"
extern "C" char __data_start[];    // start of SRAM data
extern "C" char _end[];     // end of SRAM data (used to check amount of SRAM this program's variables use)
extern "C" char __data_load_end[];  // end of FLASH (used to check amount of Flash this program's code and data uses)


#define SSID "OrangeWEI"
#define PASS "Y4arAPXw"
#define PORT 7099
#define MAX_SRV_CLIENTS 4

#define TFT_DC 2
#define TFT_CS 5
#define RGBTO565(_r, _g, _b) ((((_r) & B11111000)<<8) | (((_g) & B11111100)<<3) | ((_b) >>3))
bool debug = true;

#define ILI9341_CS_PIN 5
#define ILI9341_DC_PIN 2

//Adafruit_ILI9341 display = Adafruit_ILI9341(TFT_CS, TFT_DC);
TFT_ILI9341_ESP display = TFT_ILI9341_ESP();

WiFiServer server(PORT);
WiFiClient serverClients[MAX_SRV_CLIENTS];
char combuf[64];
char pacbuf[2048];
char *tbbuf = combuf;

WiFiUDP udp;
char replyPacekt[] = "Hi there! Got the message :-)";  // a reply string to send back


char dbuf[32768];
int bufleft = 0;
int bufoff = 0;
unsigned long lastbufwrite = 0;


void setup() {
  Serial.begin(115200);

  int debpin = digitalRead(16);
  if(debpin == 1 && debug) {
    Serial.println("Disabling debug output...");
    debug = false;
  } else if(debpin == 0 && !debug) {
    Serial.println("Enabling debug output...");
    debug = true;
  }
  
  if(debug) Serial.println("ESPMonD:");
  pinMode(16, INPUT_PULLUP);
  
  display.begin();
  display.setTextColor(ILI9341_WHITE, ILI9341_BLACK);
  display.setTextSize(1);
  display.setRotation(1);
  display.fillScreen(0);

  display.setTextSize(3);
  printCenteredString(140, 60, "Connecting...");
  display.setTextSize(2);
  display.setCursor(10, 195);
  display.print(SSID);
  display.setCursor(10, 214);
  display.print(PASS);
  int cc = 0;
  int crad = 15;
  int cx1 = 120;
  int cx2 = 160;
  int cx3 = 200;
  int cy = 120;
  display.drawCircle(cx1, cy, crad, ILI9341_WHITE);
  display.drawCircle(cx2, cy, crad, ILI9341_WHITE);
  display.drawCircle(cx3, cy, crad, ILI9341_WHITE);
  WiFi.begin(SSID, PASS);
  while (WiFi.status() != WL_CONNECTED) {
    cc++;
    switch(cc) {
      case 2:
        display.fillCircle(cx1, cy, crad, ILI9341_WHITE);
        break;
      case 3:
        display.fillCircle(cx2, cy, crad, ILI9341_WHITE);
        break;
      case 4:
        display.fillCircle(cx3, cy, crad, ILI9341_WHITE);
        break;
      case 5:
        display.fillRect(cx1-crad, cy-crad, crad*2, crad*2, 0);
        display.drawCircle(cx1, cy, crad, ILI9341_WHITE);
        break;
      case 6:
        display.fillRect(cx2-crad, cy-crad, crad*2, crad*2, 0);
        display.drawCircle(cx2, cy, crad, ILI9341_WHITE);
        break;
      case 7:
        display.fillRect(cx3-crad, cy-crad, crad*2, crad*2, 0);
        display.drawCircle(cx3, cy, crad, ILI9341_WHITE);
        cc = 1;
        break;
      default:
        break;
    }    
    delay(500);
    Serial.print(".");
  }
    
  if(debug) Serial.println("\nWiFi connected!");
  
  display.fillScreen(0);
  display.setTextSize(3);
  printCenteredString(140, 100, "Connected!");
  display.setTextSize(2);
  display.setCursor(0,0);
  printCenteredString(140, 5, "ESPMon-D by ericek111");
  printCenteredString(140, 24, "<3 for PatreS <3");
  printCenteredString(140, 140, SSID);
  printCenteredString(140, 160, WiFi.localIP().toString() + ":" + PORT);

  udp.begin(PORT);
  server.begin();
  server.setNoDelay(true);

  if(debug) Serial.println("Server listening!");

}

uint16_t combine(unsigned char msb, unsigned char lsb) {
  return (msb << 8u) | lsb;
}
uint16_t tb(uint8_t i) {
  return combine(tbbuf[i], tbbuf[i + 1]);
}
void sendDataFromBuffer(int serverclienti, int cbufoff, int cbufleft) {
  if(debug) Serial.print("Sending data from buffer (");
  if(debug) Serial.print(cbufoff);
  if(debug) Serial.print(" / ");
  if(debug) Serial.print(cbufleft);
  if(debug) Serial.print(": ");
  byte d;
  while(cbufleft > 0) {
    d = dbuf[cbufoff];
    serverClients[serverclienti].write(d);
    Serial.print((int)d, HEX);
    Serial.print(" ");
    cbufoff++;
    cbufleft--;
  }
  if(debug) Serial.println();
}
uint8_t proccessCommand(char *buf, int i) { // prot: 0 - TCP, 1 - UDP
  tbbuf = buf;
  //if (debug) Serial.println("\nProccessing!");
  uint8_t result = 0x01;
  int cdoff, cstrlen, c;
  switch (buf[i]) {
    case 0x01:
      break;
    case 0xf9:
      Serial.println("0xf9!");
        display.setTextColor(ILI9341_WHITE, ILI9341_BLACK);
  display.setTextSize(1);
  display.setRotation(1);
      display.println("0xf9!");
      break;
    case 0x11:
      display.setRotation(buf[i+1]);
      break;
    case 0x22:
      display.fillScreen(tb(i+1));
      break;
    case 0x23:
      display.drawPixel(tb(i+1), tb(i+3), tb(i+5));
      break;
    case 0x24:
      display.drawFastVLine(tb(i+1), tb(i+3), tb(i+5), tb(i+7));
      break;
    case 0x25:
      display.drawFastHLine(tb(i+1), tb(i+3), tb(i+5), tb(i+7));
      break;
    case 0x26:
      display.fillRect(tb(i+1), tb(i+3), tb(i+5), tb(i+7), tb(i+9));
      break;
    case 0x31:
      display.drawCircle(tb(i+1), tb(i+3), tb(i+5), tb(i+7));
      break;
    case 0x32:
      display.drawCircleHelper(tb(i+1), tb(i+3), tb(i+5), buf[i+7], tb(i+8));
      break;
    case 0x33:
      display.fillCircle(tb(i+1), tb(i+3), tb(i+5), tb(i+7));
      break;
    case 0x34:
      display.drawRoundRect(tb(i+1), tb(i+3), tb(i+5), tb(i+7), tb(i+9), tb(i+11));
      break;
    case 0x35:
      display.fillRoundRect(tb(i+1), tb(i+3), tb(i+5), tb(i+7), tb(i+9), tb(i+11));
      break;
    case 0x51:
      display.setCursor(tb(i+1), tb(i+3));
      break;
    case 0x52:
      display.setTextColor(tb(i+1));
      break;
    case 0x53:
      display.setTextColor(tb(i+1), tb(i+3));
      break;
    case 0x54:
      display.setTextSize(buf[i+1]);
      break;
    case 0x55:
      display.setTextWrap(buf[i+1]);
      break;
    case 0x56:
      cdoff = tb(i+1);
      cstrlen = tb(i+3);
      char str[512];
      memset(str, 0, sizeof(str));
      memcpy(&str, &dbuf[cdoff], cstrlen);
      /*for(int i = 0; i < cstrlen; i++) {
        str[i] = dbuf[cdoff+i];
      }*/
      if(debug) Serial.print("\nPrinting (");
      if(debug) Serial.print(cdoff);
      if(debug) Serial.print(" / ");
      if(debug) Serial.print(cstrlen);
      if(debug) Serial.print("): ");
      if(debug) Serial.println(str);
      display.println(str);
      break;
    case 0x61:
      memcpy(&dbuf[tb(i+1)], &buf[i+5], tb(i+3));
      if(debug) Serial.println("Shortpushing data to buffer: ");
      c = 0;
      while (c < 64 && debug)  {
          Serial.print((int)dbuf[c], HEX);
          Serial.print(" - ");
          c++;
        }
      break;
    case 0x62:
      bufleft = tb(i+1);
      bufoff = tb(i+3);
      break;
    case 0x63:
      // implemented separately in loop
      break;
    case 0x68:
      memset(&dbuf[tb(i+1)], 0, tb(i+3));
      break;
    case 0x69:
      memset(dbuf, 0, sizeof(dbuf));
      break;
    default:
      result = 0x02;
      break;
  }

  //serverClients[serverclienti].write(result);
  memset(combuf, 0, sizeof(combuf));
  return result;
}
void loop() {
  int debpin = digitalRead(16);
  if(debpin == 1 && debug) {
    Serial.println("Disabling debug output...");
    debug = false;
  } else if(debpin == 0 && !debug) {
    Serial.println("Enabling debug output...");
    debug = true;
  }
  uint8_t i;
  if (server.hasClient()) {
    for (i = 0; i < MAX_SRV_CLIENTS; i++) {
      //find free/disconnected spot
      if (!serverClients[i] || !serverClients[i].connected()) {
        if (serverClients[i]) serverClients[i].stop();
        serverClients[i] = server.available();
        if(debug) {
          Serial.print("New client: "); Serial.println(i);
        }
        continue;
      }
    }
    //no free/disconnected spot so reject
    WiFiClient serverClient = server.available();
    serverClient.stop();
  }
  for (i = 0; i < MAX_SRV_CLIENTS; i++) {
    if (serverClients[i] && serverClients[i].connected()) {
      if (serverClients[i].available()) {
        char by;
        if(debug) Serial.println("Receiving: ");
        int c = 0;
        while (serverClients[i].available()) {
          //combuf[i] = serverClients[i].read();
          by = serverClients[i].read();
          //if(by == 0x61 && c == 0)
          if (lastbufwrite + 2000 < millis() && bufleft ) {
            bufleft = 0;
            memset(dbuf, 0, sizeof(dbuf));
          }
          if (bufleft > 0) {
            dbuf[bufoff] = by;
            bufleft--;
            bufoff++;
            lastbufwrite = millis();
          }
          else combuf[c] = by; 
          if(c > 62) break;
          c++;
        }
        c = 0;
        while (c < 64 && debug)  {
          Serial.print((int)combuf[c], HEX);
          Serial.print(" - ");
          c++;
        }
        if (bufleft == 0) {
          if(combuf[0] == 0x63) {
            bufleft = tb(1);
            bufoff = tb(3);
            sendDataFromBuffer(i, bufleft, bufoff);
            bufleft = 0;
          } else proccessCommand(combuf, 0);
        }
        if (debug) Serial.println("**************************");
      }
    }
  };
  /*if(Serial.available()){
    size_t len = Serial.available();
    uint8_t sbuf[len];
    Serial.readBytes(sbuf, len);
    for(i = 0; i < MAX_SRV_CLIENTS; i++){
      if (serverClients[i] && serverClients[i].connected()){
        serverClients[i].write(sbuf, len);
        delay(1);
      }
    }
    }*/
    int packetSize = udp.parsePacket();
if (packetSize) {
  if (debug) Serial.printf("\nReceived %d bytes from %s, port %d\n", packetSize, udp.remoteIP().toString().c_str(), udp.remotePort());
  int len = udp.read(combuf, sizeof(combuf));

  int c = 0;
  while (c < 64 && debug) {
    Serial.print((int)combuf[c], HEX);
    Serial.print(" - ");
    c++;
  }
  if(len > 2) {
    uint8_t ret = proccessCommand(combuf, 0);
    udp.beginPacket(udp.remoteIP(), udp.remotePort());
    udp.write(ret);
    udp.endPacket();
  }
}

}
void printCenteredString(int x, int y, const char* str) {
  display.setCursor(x-(strlen(str)/2)*display.textsize*5, y);
  display.print(str);
}
void printCenteredString(int x, int y, String str) {
  display.setCursor(x-(str.length()/2)*display.textsize*5, y);
  display.print(str);
}

