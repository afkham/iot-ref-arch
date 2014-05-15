/*
 *  Copyright (c) 2005-2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 */
package org.wso2.iot.refarch.rpi.agent;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  WSO2
 * PROJECT       :  WSO2 IoT Reference Architecture - Raspberry Pi Agent
 * FILENAME      :  Main.java
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

import org.eclipse.paho.client.mqttv3.MqttException;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Main class in the Raspberry Pi IoT agent
 */
public class Main {
    private static final int DEFAULT_DATA_PIN_NUMBER = 27;
    private DHTSensor dhtSensor;
    private MQTTClient mqttClient;
    private MQTTBrokerConnectionConfig mqttBrokerConnectionConfig;
    Agent agent;
    public Main(int dataPinNumber) {
        dhtSensor = new DHTSensor(DHTSensorType.DHT11, dataPinNumber);
        mqttBrokerConnectionConfig = new MQTTBrokerConnectionConfig("10.100.0.209","1883");
        String clientId = "R-Pi-Publisher";
        String topicName = "wso2iot";
        mqttClient = new MQTTClient(mqttBrokerConnectionConfig,clientId,topicName);
        agent = new Agent();
    }

    public static void main(String[] args) {
        int dataPinNumber = DEFAULT_DATA_PIN_NUMBER;
        if (args.length > 0 && args[0] != null) {
            dataPinNumber = Integer.parseInt(args[0]);
        }
        new Main(dataPinNumber).start();
    }

    private void start() {
        ScheduledExecutorService dhtReaderScheduler = Executors.newScheduledThreadPool(1);
        dhtReaderScheduler.scheduleWithFixedDelay(new MonitoringTask(dhtSensor), 0, 20, TimeUnit.SECONDS);
    }
    public class MonitoringTask implements Runnable {
        private DHTSensor dhtSensor;

        public MonitoringTask(DHTSensor dhtSensor) {
            this.dhtSensor = dhtSensor;
        }

        @Override
        public void run() {
            dhtSensor.read();
            float temperature = dhtSensor.getTemperature(false);
            int humidity = dhtSensor.getHumidity();
            JSONObject payload = generatePayload(humidity, temperature);
            try {
                mqttClient.publish(1, payload.toJSONString().getBytes());
                JSONObject infoObject = agent.createInfoObject();
                //Inserting the sensors payload to info object
                infoObject.put("sensors", payload);
                agent.httpService.sendPayload(infoObject);
            } catch (MqttException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public JSONObject generatePayload(int humdity, float temperature){
        JSONObject payload = new JSONObject();
        payload.put("humidity", humdity);
        payload.put("temperature", temperature);
        return payload;
    }
}
