package org.wso2.iot.refarch.rpi.agent;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  WSO2
 * PROJECT       :  WSO2 IoT Reference Architecture - Raspberry Pi Agent
 * FILENAME      :  Receiver.java
 * 
 * This file is part of the Pi4J project. More information about
 * this project can be found here:  http://www.wso2.com/
 * **********************************************************************
 * %%
 * Copyright (C) 2014 WSO2
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.RaspiPin;

public class Receiver{
    private MQTTClient mqttClient;
    private MQTTBrokerConnectionConfig mqttBrokerConnectionConfig;
    final GpioController gpio;
    final GpioPinDigitalOutput pin;
    {
        gpio = GpioFactory.getInstance();
        pin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_17, "Relay");
    }
    //17
    public Receiver() {
        mqttBrokerConnectionConfig = new MQTTBrokerConnectionConfig("192.168.1.9","1883");
        String clientId = "R-Pi-Receiver";
        String topicName = "iot/demo";
        mqttClient = new MQTTClient(mqttBrokerConnectionConfig,clientId,topicName, this);
        Agent.startService();
    }

    public static void main(String[] args) {
        new Receiver();
    }
    public void run(String message){
        if(message.equals("on")){
           pin.high();
        }else if(message.equals("off")){
            pin.low();
        }
    }
}
