// Access from ARM Running Linux

#define BCM2708_PERI_BASE        0x20000000
#define GPIO_BASE                (BCM2708_PERI_BASE + 0x200000) /* GPIO controller */

#include <jni.h>
#include "org_wso2_iot_refarch_rpi_agent_DHTSensor.h"

#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <dirent.h>
#include <fcntl.h>
#include <assert.h>
#include <unistd.h>
#include <sys/mman.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <sys/time.h>
#include <bcm2835.h>
#include <unistd.h>

#define MAXTIMINGS 100

//#define DEBUG

#define DHT11 11
#define DHT22 22
#define AM2302 22

int * readDHTRaw(int type, int pin);
void readDHT(JNIEnv *env, jobject thisObj, int type, int pin);
void setField(JNIEnv *env, jobject thisObj, const char *name , int fieldValue);

JNIEXPORT void JNICALL Java_org_wso2_iot_refarch_rpi_agent_DHTSensor_readSensor
  (JNIEnv *env, jobject thisObj, jint sensorType, jint dataPinNumber)
{
     if (!bcm2835_init())
         return;
     int type = 0;
     if ((long)sensorType == 11) {
        type = DHT11;
        #ifdef DEBUG
        printf("DHT sensor type: DHT11\n");
        #endif
     } else if ((long)sensorType == 22){
        type = DHT22;
        #ifdef DEBUG
        printf("DHT sensor type: DHT22\n");
        #endif
     } else if ((long)sensorType == 2302) {
        type = AM2302;
        #ifdef DEBUG
        printf("DHT sensor type: AM2302\n");
        #endif
     } else {
          printf("Select 11, 22, 2302 as type!\n");
          return;
     }

      long dhtpin = (long) dataPinNumber;

      if (dhtpin <= 0) {
            printf("Please select a valid GPIO pin #\n");
            return;
      }

      #ifdef DEBUG
      printf("Using pin #%ul\n", dhtpin);
      #endif
      readDHT(env, thisObj, type, dhtpin);
}

int bits[250], data[100];

int * readDHTRaw(int type, int pin) {
  int bitidx = 0;
  int counter = 0;
  int laststate = HIGH;
  int j=0;

  // Set GPIO pin to output
  bcm2835_gpio_fsel(pin, BCM2835_GPIO_FSEL_OUTP);

  bcm2835_gpio_write(pin, HIGH);
  usleep(500000);  // 500 ms
  bcm2835_gpio_write(pin, LOW);
  usleep(20000);

  bcm2835_gpio_fsel(pin, BCM2835_GPIO_FSEL_INPT);

  data[0] = data[1] = data[2] = data[3] = data[4] = 0;

  // wait for pin to drop?
  while (bcm2835_gpio_lev(pin) == 1) {
    usleep(1);
  }

  // read data!
  int i = 0;
  for (i=0; i< MAXTIMINGS; i++) {
    counter = 0;
    while ( bcm2835_gpio_lev(pin) == laststate) {
        counter++;
        //nanosleep(1);         // overclocking might change this?
        if (counter == 1000)
          break;
    }
    laststate = bcm2835_gpio_lev(pin);
    if (counter == 1000) break;
    bits[bitidx++] = counter;
    if ((i>3) && (i%2 == 0)) {
      // shove each bit into the storage bytes
      data[j/8] <<= 1;
      if (counter > 200)
        data[j/8] |= 1;
      j++;
    }
  }


#ifdef DEBUG
  for (int i=3; i<bitidx; i+=2) {
    printf("bit %d: %d\n", i-3, bits[i]);
    printf("bit %d: %d (%d)\n", i-2, bits[i+1], bits[i+1] > 200);
  }
#endif

  #ifdef DEBUG
  printf("Data (%d): 0x%x 0x%x 0x%x 0x%x 0x%x\n", j, data[0], data[1], data[2], data[3], data[4]);
  #endif

  if ((j >= 39) &&
      (data[4] == ((data[0] + data[1] + data[2] + data[3]) & 0xFF)) ) {
     int f = -1;
     int h = -1;

     if (type == DHT11) {
        f = data[2];
        h = data[0];
        printf("Temp = %d *C, Hum = %d \n", data[2], data[0]);
     } else if (type == DHT22) {
        h = data[0] * 256 + data[1];
        h /= 10;
        f = (data[2] & 0x7F)* 256 + data[3];
        f /= 10.0;
        if (data[2] & 0x80)  f *= -1;
        printf("Temp =  %d *C, Hum = %d \n", f, h);
      }
      static int result[2];
      result[0] = f;
      result[1] = h;
      return result;
 } else {
     usleep(500000);  // 500 ms
     readDHTRaw(type, pin);
 }
}

void readDHT(JNIEnv *env, jobject thisObj, int type, int pin) {
     int *p = readDHTRaw(type, pin);
     setField(env, thisObj, "temperature", *(p));
     setField(env, thisObj, "humidity", *(p+1));
}


void setField(JNIEnv *env, jobject thisObj, const char *name , int fieldValue){
      jclass thisClass = (*env)->GetObjectClass(env, thisObj);
      jfieldID fid = (*env)->GetFieldID(env, thisClass, name, "I");
      if (NULL == fid) return;

      // Change the variable
      (*env)->SetIntField(env, thisObj, fid, fieldValue);
}