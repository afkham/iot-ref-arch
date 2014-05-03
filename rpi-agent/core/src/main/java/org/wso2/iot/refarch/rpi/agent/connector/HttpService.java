package org.wso2.iot.refarch.rpi.agent.connector;

import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.Response;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class HttpService extends ConnectionService {
    public void sendPayload(JSONObject data, String address) throws IOException, ExecutionException, InterruptedException {
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        System.out.println(data.toJSONString());
        Future<Response> f = asyncHttpClient.preparePost(address).setBody(data.toJSONString()).setHeader("content-type", "application/json").execute();
        Response r = f.get();
        System.out.println("Done");
    }
}
