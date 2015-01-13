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
#define Vref 5.0 // Voltage reference?!

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
  // Get humidity data
  float humidity = TH02.ReadHumidity(); 
  blueToothSerial.print("hum=");   
  blueToothSerial.println(humidity);
  // Print also to serial
  Serial.print("hum=");
  Serial.println(humidity);
  
  // Get temperature data
  float temper = TH02.ReadTemperature();
  blueToothSerial.print("temp=");   
  blueToothSerial.println(temper);
  // Print also to serial
  Serial.print("temp=");
  Serial.println(temper);
  
  // HCHO sensor connected to A0
  int hchoSensorValue=analogRead(A0);
  float hchovol=(hchoSensorValue*4.95/1023)*100;
  blueToothSerial.print("hcho=");
  blueToothSerial.println(hchovol);
  // Print also to serial
  Serial.print("hcho=");
  Serial.println(hchovol);
  
  // MQ sensors: 
  // MQ-2：Combustible Gas, Smoke
  // MQ-3：Alcohol and Benzine
  // MQ-5：LPG, Natural Gas, Town Gas
  // MQ-9：LPG, CO, CH4
  // MQ sensor connected to A1
  float mqvol;
  int mqSensorValue = analogRead(A1);
  mqvol=((float)mqSensorValue/1024*Vref)*100;
  blueToothSerial.print("mq3="); // change or add other MQ sensors accordingly
  blueToothSerial.println(mqvol);
  // Print also to serial
  Serial.print("mq3="); // change or add other MQ sensors accordingly
  Serial.println(mqvol);
  
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
