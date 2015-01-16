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
  
  // MQ-2：Combustible Gas, Smoke
  float mq2vol;
  int mq2SensorValue = analogRead(A1);
  mq2vol=((float)mq2SensorValue/1024*Vref)*100;
  blueToothSerial.print("mq2="); 
  blueToothSerial.println(mq2vol);
  // Print also to serial
  Serial.print("mq2="); 
  Serial.println(mq2vol);
  
  // MQ-3：Alcohol and Benzine
  float mq3vol;
  int mq3SensorValue = analogRead(A2);
  mq3vol=((float)mq3SensorValue/1024*Vref)*100;
  blueToothSerial.print("mq3="); 
  blueToothSerial.println(mq3vol);
  // Print also to serial
  Serial.print("mq3="); 
  Serial.println(mq3vol);

  // MQ-5：LPG, Natural Gas, Town Gas
  float mq5vol;
  int mq5SensorValue = analogRead(A3);
  mq5vol=((float)mq5SensorValue/1024*Vref)*100;
  blueToothSerial.print("mq5=");
  blueToothSerial.println(mq5vol);
  // Print also to serial
  Serial.print("mq5=");
  Serial.println(mq5vol);  
  
  // MQ-9：LPG, CO, CH4
  float mq9vol;
  int mq9SensorValue = analogRead(A4);
  mq9vol=((float)mq9SensorValue/1024*Vref)*100;
  blueToothSerial.print("mq9=");
  blueToothSerial.println(mq9vol);
  // Print also to serial
  Serial.print("mq9=");
  Serial.println(mq9vol);
  delay(2000);
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
  // blueToothSerial.print("AT+DEFAULT");             // Restore all setup value to factory setup
  // delay(2000); 
  blueToothSerial.print("AT+NAMESmartPM");    // set the bluetooth name as "SeeedBTSlave" ,the length of bluetooth name must less than 12 characters.
  delay(400);
  blueToothSerial.print("AT+PIN1234");             // set the pair code to connect 
  delay(400);
  blueToothSerial.print("AT+AUTH1");             //
  delay(400);
  blueToothSerial.flush();

}
