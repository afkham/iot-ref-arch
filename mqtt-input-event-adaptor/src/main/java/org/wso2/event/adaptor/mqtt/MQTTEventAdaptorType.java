/*
 * Copyright 2004,2005 The Apache Software Foundation.
 *
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
 */

package org.wso2.event.adaptor.mqtt;

import org.apache.axis2.engine.AxisConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.event.input.adaptor.core.AbstractInputEventAdaptor;
import org.wso2.carbon.event.input.adaptor.core.InputEventAdaptorListener;
import org.wso2.carbon.event.input.adaptor.core.MessageType;
import org.wso2.carbon.event.input.adaptor.core.Property;
import org.wso2.carbon.event.input.adaptor.core.config.InputEventAdaptorConfiguration;
import org.wso2.carbon.event.input.adaptor.core.message.config.InputEventAdaptorMessageConfiguration;
import org.wso2.event.adaptor.mqtt.internal.util.MQTTBrokerConnectionConfig;
import org.wso2.event.adaptor.mqtt.internal.util.MQTTEventAdaptorConstants;
import org.wso2.event.adaptor.mqtt.internal.util.MQTTListener;

import java.util.*;


public final class MQTTEventAdaptorType extends AbstractInputEventAdaptor {

    private static final Log log = LogFactory.getLog(MQTTEventAdaptorType.class);
    private ResourceBundle resourceBundle;
    private static MQTTEventAdaptorType mqttEventAdaptorAdaptor = new MQTTEventAdaptorType();

    public static MQTTEventAdaptorType getInstance() {
        return mqttEventAdaptorAdaptor;
    }

    @Override
    protected String getName() {
        return MQTTEventAdaptorConstants.EVENT_ADAPTOR_TYPE_MQTT;
    }

    @Override
    protected List<String> getSupportedInputMessageTypes() {
        List<String> supportInputMessageTypes = new ArrayList<String>();
        supportInputMessageTypes.add(MessageType.TEXT);
        return supportInputMessageTypes;
    }

    @Override
    protected void init() {
       this.resourceBundle = ResourceBundle.getBundle("org.wso2.event.adaptor.mqtt.i18n.Resources", Locale.getDefault());
        log.info("MQTTEventAdaptorType init");

    }


    @Override
    protected List<Property> getInputAdaptorProperties() {

        List<Property> propertyList = new ArrayList<Property>();
        //Broker Hostname
        Property brokerHostname = new Property(MQTTEventAdaptorConstants.EVENT_ADAPTOR_CONF_BROKER_HOSTNAME);
        brokerHostname.setDisplayName(
                resourceBundle.getString(MQTTEventAdaptorConstants.EVENT_ADAPTOR_CONF_BROKER_HOSTNAME));
        brokerHostname.setRequired(true);
        brokerHostname.setHint(resourceBundle.getString(MQTTEventAdaptorConstants.EVENT_ADAPTOR_CONF_BROKER_HOSTNAME_HINT));
        propertyList.add(brokerHostname);
        //Broker Port
        Property brokerPort = new Property(MQTTEventAdaptorConstants.EVENT_ADAPTOR_CONF_BROKER_PORT);
        brokerPort.setDisplayName(
                resourceBundle.getString(MQTTEventAdaptorConstants.EVENT_ADAPTOR_CONF_BROKER_PORT));
        brokerPort.setRequired(true);
        brokerPort.setHint(
                resourceBundle.getString(MQTTEventAdaptorConstants.EVENT_ADAPTOR_CONF_BROKER_PORT_HINT));
        propertyList.add(brokerPort);

        return propertyList;
    }

    @Override
    protected List<Property> getInputMessageProperties() {

        List<Property> propertyList = new ArrayList<Property>();
        Property topicName = new Property(MQTTEventAdaptorConstants.EVENT_ADAPTOR_CONF_TOPIC_NAME);
        topicName.setDisplayName(
                resourceBundle.getString(MQTTEventAdaptorConstants.EVENT_ADAPTOR_CONF_TOPIC_NAME));
        topicName.setRequired(true);
        topicName.setHint(resourceBundle.getString(MQTTEventAdaptorConstants.EVENT_ADAPTOR_CONF_TOPIC_NAME_HINT));
        propertyList.add(topicName);

        return propertyList;
    }

    @Override
    public String subscribe(
            InputEventAdaptorMessageConfiguration inputEventAdaptorMessageConfiguration,
            InputEventAdaptorListener inputEventAdaptorListener,
            InputEventAdaptorConfiguration inputEventAdaptorConfiguration,
            AxisConfiguration axisConfiguration) {

        log.info("MQTT in subscribed " + inputEventAdaptorListener.getEventAdaptorName());
        String subscriptionId = UUID.randomUUID().toString();
        MQTTBrokerConnectionConfig mqttBrokerConnectionConfig = new MQTTBrokerConnectionConfig();
        mqttBrokerConnectionConfig.setBrokerHost(inputEventAdaptorConfiguration.getInputProperties().get(MQTTEventAdaptorConstants.EVENT_ADAPTOR_CONF_BROKER_HOSTNAME));
        mqttBrokerConnectionConfig.setProkerPort(inputEventAdaptorConfiguration.getInputProperties().get(MQTTEventAdaptorConstants.EVENT_ADAPTOR_CONF_BROKER_PORT));
        mqttBrokerConnectionConfig.setBrokerProtocole("tcp");
        String topicName = inputEventAdaptorMessageConfiguration.getInputMessageProperties().get(MQTTEventAdaptorConstants.EVENT_ADAPTOR_CONF_TOPIC_NAME);
        String clientId =  MQTTEventAdaptorConstants.EVENT_ADAPTOR_CLIENT_ID;
        MQTTListener mqttListener = new MQTTListener(mqttBrokerConnectionConfig, clientId,topicName,inputEventAdaptorListener);
        mqttListener.run();
        return subscriptionId;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void unsubscribe(
            InputEventAdaptorMessageConfiguration inputEventAdaptorMessageConfiguration,
            InputEventAdaptorConfiguration inputEventAdaptorConfiguration,
            AxisConfiguration axisConfiguration, String s) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

}
