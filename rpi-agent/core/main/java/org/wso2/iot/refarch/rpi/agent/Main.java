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

import com.pi4j.wiringpi.DHTSensor;
import com.pi4j.wiringpi.DHTSensorType;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Main class in the Raspberry Pi IoT agent
 */
public class Main {
    private static final int DEFAULT_DATA_PIN_NUMBER = 27;
    private DHTSensor dhtSensor;

    public Main(int dataPinNumber) {
        dhtSensor = new DHTSensor(DHTSensorType.DHT11, dataPinNumber);
    }

    public static void main(String[] args) {
        int dataPinNumber = DEFAULT_DATA_PIN_NUMBER;
        if (args[0] != null) {
            dataPinNumber = Integer.parseInt(args[0]);
        }
        new Main(dataPinNumber).start();
    }

    private void start() {
        ScheduledExecutorService dhtReaderScheduler = Executors.newScheduledThreadPool(1);
        dhtReaderScheduler.scheduleWithFixedDelay(new DHTSensorReaderTask(dhtSensor), 20, 20, TimeUnit.SECONDS);
    }

    public class DHTSensorReaderTask implements Runnable {
        private DHTSensor dhtSensor;

        public DHTSensorReaderTask(DHTSensor dhtSensor) {
            this.dhtSensor = dhtSensor;
        }

        @Override
        public void run() {
            dhtSensor.read();
            float temperature = dhtSensor.getTemperature(false);
            int humidity = dhtSensor.getHumidity();

            System.out.println("temperature = " + temperature);
            System.out.println("humidity = " + humidity);

            //TODO: publish to CEP/BAM
        }
    }
}
