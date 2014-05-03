package org.wso2.iot.refarch.rpi.agent;

public class Receiver{
    private MQTTClient mqttClient;
    private MQTTBrokerConnectionConfig mqttBrokerConnectionConfig;

    public Receiver() {
        mqttBrokerConnectionConfig = new MQTTBrokerConnectionConfig("10.100.0.209","1883");
        String clientId = "R-Pi-Receiver";
        String topicName = "iot/demo";
        mqttClient = new MQTTClient(mqttBrokerConnectionConfig,clientId,topicName);

    }

    public static void main(String[] args) {
        new Receiver();
    }
}
