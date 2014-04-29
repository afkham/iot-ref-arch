package org.wso2.iot.refarch.rpi.agent;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;

import java.security.Timestamp;
import java.util.concurrent.Semaphore;

/**
 * Created by deep on 4/29/14.
 */
public class MQTTClient implements MqttCallback {

    //private static final Log log = LogFactory.getLog(MQTTListener.class);

    private MqttClient mqttClient;
    private MqttConnectOptions connectionOptions;
    private boolean cleanSession;
    private String brokerUrl;
    private String password;
    private String userName;
    private String mqttClientId;
    private String topicName;
    private Semaphore semaphore = new Semaphore(0);

    public MQTTClient(MQTTBrokerConnectionConfig mqttBrokerConnectionConfig, String mqttClientId, String topic) {
        //Initializing the variables locally
        this.brokerUrl = mqttBrokerConnectionConfig.getBrokerUrl();
        this.mqttClientId = mqttClientId;
        // this.quietMode = quietMode;
        this.cleanSession = mqttBrokerConnectionConfig.isCleanSession();
        this.password = mqttBrokerConnectionConfig.getBrokerPassword();
        this.userName = mqttBrokerConnectionConfig.getBrokerUsername();
//        this.mqttAaction = action;
        this.topicName = topic;
//        this.qos = qos;
//        this.messagePayLoad = payload;

        //Sotring messages until the server fetches them
        String temp_directory = System.getProperty("java.io.tmpdir");
        MqttDefaultFilePersistence dataStore = new MqttDefaultFilePersistence(temp_directory);


        try {
            // Construct the connection options object that contains connection parameters
            // such as cleanSession and LWT
            connectionOptions = new MqttConnectOptions();
            connectionOptions.setCleanSession(cleanSession);
            if (password != null) {
                connectionOptions.setPassword(this.password.toCharArray());
            }
            if (userName != null) {
                connectionOptions.setUserName(this.userName);
            }

            // Construct an MQTT blocking mode client
            mqttClient = new MqttClient(this.brokerUrl, mqttClientId, dataStore);

            // Set this wrapper as the callback handler
            mqttClient.setCallback(this);

        } catch (MqttException e) {
            e.printStackTrace();
        }

    }

    public void publish(int qos, byte[] payload) throws MqttException {

        // Connect to the MQTT server
        mqttClient.connect(connectionOptions);

        // Create and configure a message
        MqttMessage message = new MqttMessage(payload);
        message.setQos(qos);

        // Send the message to the server, control is not returned until
        // it has been delivered to the server meeting the specified
        // quality of service.
        mqttClient.publish(topicName, message);
    }

    @Override
    public void connectionLost(Throwable throwable) {

    }

    @Override
    public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {

    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

    }
}
