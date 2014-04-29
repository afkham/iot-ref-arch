package org.wso2.event.adaptor.mqtt;

import org.wso2.event.adaptor.mqtt.internal.util.MQTTBrokerConnectionConfig;
import org.wso2.event.adaptor.mqtt.internal.util.MQTTListener;

import java.util.UUID;

/**
 * Created by deep on 4/23/14.
 */
public class MQTTEventAdaptorMain {

    public static void main(String [] args){

        String subscriptionId = UUID.randomUUID().toString();
        MQTTBrokerConnectionConfig mqttBrokerConnectionConfig = new MQTTBrokerConnectionConfig();
        mqttBrokerConnectionConfig.setBrokerHost("localhost");
        mqttBrokerConnectionConfig.setProkerPort("1883");
        mqttBrokerConnectionConfig.setBrokerProtocole("tcp");
        String topicName = "wso2iot";
        String clientId = "WSO2IOT";
        MQTTListener mqttListener = new MQTTListener(mqttBrokerConnectionConfig, clientId,topicName, null);
        mqttListener.run();

    }
}
