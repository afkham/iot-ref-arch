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
 * FILENAME      :  DHTSensor.java
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

/**
 * TODO: class level comment
 */
public class DHTSensor {

    private DHTSensorType type;
    private int dataPinNumber;
    private int temperature;
    private int humidity;
    private boolean readCalled;

    public DHTSensor(DHTSensorType type, int dataPinNumber) {
        this.type = type;
        this.dataPinNumber = dataPinNumber;
    }

    static {
        // Load the platform library
//        NativeLibraryLoader.load("pi4j", "libpi4j.so");
        System.loadLibrary("dht");
    }

    private native void readSensor(int sensorType, int dataPinNumber);

    public void read() {
        readSensor(type.getType(), dataPinNumber);
        readCalled = true;
    }

    public float getTemperature(boolean isFahrenheit) {
        if (!readCalled) throw new IllegalStateException("read not called");
        if (isFahrenheit) {
            return  convertCtoF(temperature);
        } else {
            return temperature;
        }
    }

    private float convertCtoF(float c) {
        return c * 9 / 5 + 32;
    }

    public int getHumidity() {
        if (!readCalled) throw new IllegalStateException("read not called");
        return humidity;
    }
}
