package org.wso2.iot.refarch.rpi.agent;
/*
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  WSO2
 * PROJECT       :  WSO2 IoT Reference Architecture - Raspberry Pi Agent
 * FILENAME      :  Agent.java
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


import org.json.simple.JSONObject;
import org.wso2.iot.refarch.rpi.agent.connector.HttpService;

/*
    The Agent class that will run continuously
 */
public class Agent {
    public static void main(String[] args) {
        HttpService httpService = new HttpService();
        JSONObject sampleObject = new JSONObject();
        sampleObject.put("Person", "Going");
        try {
            //TODO:- Add a config file for this
            httpService.sendPayload(sampleObject, "http://192.168.1.3:3000/iot");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
