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
  float humidity = TH02.ReadHumidity(); 
  blueToothSerial.print("hum=");   
  blueToothSerial.println(humidity);
  
  float temper = TH02.ReadTemperature();
  blueToothSerial.print("temp=");   
  blueToothSerial.println(temper);
  
  Serial.print("hum=");
  Serial.println(humidity);
  Serial.print("temp=");
  Serial.println(temper);

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
  blueToothSerial.print("AT+PIN1234");             // set the pair code to connect 
  delay(400);
  blueToothSerial.print("AT+AUTH1");             //
  delay(400);
  blueToothSerial.flush();

}
