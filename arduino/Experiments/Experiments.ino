#include <SoftwareSerial.h>   //Software Serial Port

#include <TH02_dev.h>
#include "Arduino.h"
#include "Wire.h" 
 
#define RxD         7
#define TxD         6

#define PINLED      13

#define LEDON()     digitalWrite(PINLED, HIGH)
#define LEDOFF()    digitalWrite(PINLED, LOW)

#define DEBUG_ENABLED  1

SoftwareSerial blueToothSerial(RxD,TxD);

void setup()
{
    Serial.begin(9600);
    pinMode(RxD, INPUT);
    pinMode(TxD, OUTPUT);
    pinMode(PINLED, OUTPUT);
    LEDOFF();
    TH02.begin();
    
    setupBlueToothConnection();
}

void loop()
{
  LEDOFF();
  float humidity = TH02.ReadHumidity();
  blueToothSerial.println(humidity);
  Serial.println(humidity);
  delay(1000);
  
}

/***************************************************************************
 * Function Name: setupBlueToothConnection
 * Description:  initilizing bluetooth connction
 * Parameters: 
 * Return: 
***************************************************************************/
void setupBlueToothConnection()
{	
  blueToothSerial.begin(9600);
  blueToothSerial.print("AT");
  delay(400); 
  blueToothSerial.print("AT+DEFAULT");             // Restore all setup value to factory setup
  delay(2000); 
  blueToothSerial.print("AT+NAMESmartPM");    // set the bluetooth name as "SeeedBTSlave" ,the length of bluetooth name must less than 12 characters.
  delay(400);
  blueToothSerial.print("AT+PIN1111");             // set the pair code to connect 
  delay(400);
  blueToothSerial.print("AT+AUTH1");             //
  delay(400);
  blueToothSerial.flush();

}
