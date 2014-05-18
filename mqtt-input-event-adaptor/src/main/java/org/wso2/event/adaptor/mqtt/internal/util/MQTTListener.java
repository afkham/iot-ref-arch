package org.wso2.event.adaptor.mqtt.internal.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;
import org.wso2.carbon.event.input.adaptor.core.InputEventAdaptorListener;
import org.wso2.carbon.event.input.adaptor.core.exception.InputEventAdaptorEventProcessingException;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * Created by deep on 4/22/14.
 */
public class MQTTListener implements MqttCallback, Runnable {

    private static final Log log = LogFactory.getLog(MQTTListener.class);

    private MqttClient mqttClient;
    private MqttConnectOptions connectionOptions;
    private boolean cleanSession;
    private String brokerUrl;
    private String password;
    private String userName;
    private String mqttClientId;
    private String topic;
    private Semaphore semaphore = new Semaphore(0);

    private InputEventAdaptorListener eventAdaptorListener = null;


    public MQTTListener(MQTTBrokerConnectionConfig mqttBrokerConnectionConfig, String mqttClientId, String topic, InputEventAdaptorListener inputEventAdaptorListener) {
        log.info("creating MQTT Listener " + mqttClientId);
        //Initializing the variables locally
        this.brokerUrl = mqttBrokerConnectionConfig.getBrokerUrl();
        this.mqttClientId = mqttClientId;
        // this.quietMode = quietMode;
        this.cleanSession = mqttBrokerConnectionConfig.isCleanSession();
        this.password = mqttBrokerConnectionConfig.getBrokerPassword();
        this.userName = mqttBrokerConnectionConfig.getBrokerUsername();
//        this.mqttAaction = action;
        this.topic = topic;
//        this.qos = qos;
//        this.messagePayLoad = payload;
        eventAdaptorListener = inputEventAdaptorListener;

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
    private void start(MqttClient client, String topicName, int qos) {
        ScheduledExecutorService dhtReaderScheduler = Executors.newScheduledThreadPool(1);
        dhtReaderScheduler.scheduleWithFixedDelay(new Reconnector(topicName, qos), 0, 10, TimeUnit.SECONDS);
    }
    public class Reconnector implements Runnable {

        private final String topicName;
        private final int qos;

        public Reconnector(String topicName, int qos) {
            this.topicName = topicName;
            this.qos = qos;
        }

        @Override
        public void run() {

            try {
                if(!mqttClient.isConnected()){
                    mqttClient.connect(connectionOptions);
                    System.out.println("Mqtt client reconnected");
                    mqttClient.subscribe(topicName, qos);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void subscribe(String topicName, int qos) throws MqttException {

        // Connect to the MQTT server
        start(mqttClient, topicName, qos);

        // Subscribe to the requested topic
        // The QoS specified is the maximum level that messages will be sent to the client at.
        // For instance if QoS 1 is specified, any messages originally published at QoS 2 will
        // be downgraded to 1 when delivering to the client but messages published at 1 and 0
        // will be received at the same level they were published at.

        //Will need to wait for the message delivery
//        try {
//            semaphore.acquire();
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            //First will unsubscribe
//            mqttClient.unsubscribe(topicName);
//            // Disconnect the client from the server
//            mqttClient.disconnect();
//        }
    }

    @Override
    public void run() {
        try {
            subscribe(this.topic,1);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void connectionLost(Throwable throwable) {

    }

    @Override
    public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
        try {
            log.info("message arrived " + mqttMessage.toString());
            System.out.println("message arrived " + mqttMessage.toString());
            String msgText = mqttMessage.toString();
             eventAdaptorListener.onEventCall(msgText);
        } catch (InputEventAdaptorEventProcessingException e) {
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

    }
}
