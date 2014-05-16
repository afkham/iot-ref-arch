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
package org.wso2.iot.refarch.rpi.agent.connector;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.simple.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

/*
    Service class that handles Payload sending to Server
 */
public class HttpService extends ConnectionService {
    String address;
    public HttpService(String address){
        this.address = address;
    }
    public void sendPayload(JSONObject data) throws IOException, ExecutionException, InterruptedException {

        JSONObject dataObj = new JSONObject();
        dataObj.put("data", data);

        HttpClient client = HttpClientBuilder.create().build();
        System.out.println("Created HTTP Client");
        HttpPost post = new HttpPost(address);
        post.setHeader("content-type", "application/json");
        BasicHttpEntity he = new BasicHttpEntity();
        he.setContent(new ByteArrayInputStream(dataObj.toString().getBytes()));
        post.setEntity(he);
        client.execute(post);
        System.out.println("Payload sent");
    }
}
