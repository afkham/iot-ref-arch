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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.wso2.carbon.event.output.adaptor.core.AbstractOutputEventAdaptor;
import org.wso2.carbon.event.output.adaptor.core.Property;
import org.wso2.carbon.event.output.adaptor.core.config.OutputEventAdaptorConfiguration;
import org.wso2.carbon.event.output.adaptor.core.message.config.OutputEventAdaptorMessageConfiguration;
import org.wso2.event.adaptor.mqtt.internal.util.MQTTBrokerConnectionConfig;
import org.wso2.event.adaptor.mqtt.internal.util.MQTTClient;
import org.wso2.event.adaptor.mqtt.internal.util.MQTTOutputEventAdaptorConstants;
import org.wso2.carbon.event.output.adaptor.core.MessageType;

import java.util.*;


public final class MQTTOutputEventAdaptorType extends AbstractOutputEventAdaptor {

    private static final Log log = LogFactory.getLog(MQTTOutputEventAdaptorType.class);

    private ResourceBundle resourceBundle;
    private static MQTTOutputEventAdaptorType mqttOutputEventAdaptorType = new MQTTOutputEventAdaptorType();
    private MQTTBrokerConnectionConfig mqttBrokerConnectionConfig;
    private MQTTClient mqttClient;

    @Override
    protected String getName() {
        return MQTTOutputEventAdaptorConstants.EVENT_ADAPTOR_TYPE_TESTOUT;
    }

    @Override
    protected List<String> getSupportedOutputMessageTypes() {
        List<String> supportOutputMessageTypes = new ArrayList<String>();
        supportOutputMessageTypes.add(MessageType.TEXT);
        return supportOutputMessageTypes;
    }

    @Override
    protected void init() {
        this.resourceBundle = ResourceBundle.getBundle("org.wso2.event.adaptor.mqtt.i18n.Resources", Locale.getDefault());
    }

    @Override
    protected List<Property> getOutputAdaptorProperties() {
        List<Property> propertyList = new ArrayList<Property>();

        Property host = new Property(MQTTOutputEventAdaptorConstants.EVENT_ADAPTOR_CONF_HOST);
        host.setDisplayName(
                resourceBundle.getString(MQTTOutputEventAdaptorConstants.EVENT_ADAPTOR_CONF_HOST));
        host.setRequired(true);

        Property port = new Property(MQTTOutputEventAdaptorConstants.EVENT_ADAPTOR_CONF_PORT);
        port.setDisplayName(
                resourceBundle.getString(MQTTOutputEventAdaptorConstants.EVENT_ADAPTOR_CONF_PORT));
        port.setRequired(true);

        propertyList.add(host);
        propertyList.add(port);

        return propertyList;
    }

    @Override
    protected List<Property> getOutputMessageProperties() {
        List<Property> propertyList = new ArrayList<Property>();

        Property topic = new Property(MQTTOutputEventAdaptorConstants.EVENT_ADAPTOR_CONF_TOPIC);
        topic.setDisplayName(
                resourceBundle.getString(MQTTOutputEventAdaptorConstants.EVENT_ADAPTOR_CONF_TOPIC));
        topic.setRequired(true);
        topic.setHint(resourceBundle.getString(MQTTOutputEventAdaptorConstants.EVENT_ADAPTOR_CONF_TOPIC_HINT));

        Property clientId = new Property(MQTTOutputEventAdaptorConstants.EVENT_ADAPTOR_CONF_CLIENTID);
        clientId.setDisplayName(
                resourceBundle.getString(MQTTOutputEventAdaptorConstants.EVENT_ADAPTOR_CONF_CLIENTID));
        clientId.setRequired(true);
        clientId.setHint(resourceBundle.getString(MQTTOutputEventAdaptorConstants.EVENT_ADAPTOR_CONF_CLIENTID_HINT));

        propertyList.add(topic);
        propertyList.add(clientId);

        return propertyList;
    }

    @Override
    public void publish(
            OutputEventAdaptorMessageConfiguration outputEventAdaptorMessageConfiguration,
            Object o, OutputEventAdaptorConfiguration outputEventAdaptorConfiguration,
            int tenantId) {
        try {
            Map<String,String> adaptorProps = outputEventAdaptorConfiguration.getOutputProperties();
            Map<String, String> messageProps = outputEventAdaptorMessageConfiguration.getOutputMessageProperties();
            String topic = messageProps.get(MQTTOutputEventAdaptorConstants.EVENT_ADAPTOR_CONF_TOPIC);
            String client_id = messageProps.get(MQTTOutputEventAdaptorConstants.EVENT_ADAPTOR_CONF_CLIENTID);
            String host = adaptorProps.get(MQTTOutputEventAdaptorConstants.EVENT_ADAPTOR_CONF_HOST);
            String port = adaptorProps.get(MQTTOutputEventAdaptorConstants.EVENT_ADAPTOR_CONF_PORT);

            mqttBrokerConnectionConfig = new MQTTBrokerConnectionConfig(host,port);
            mqttClient  = new MQTTClient(mqttBrokerConnectionConfig,client_id,topic);
            mqttClient.publish(1, o.toString().getBytes());
            mqttClient.mqttClient.disconnect();
            mqttClient.mqttClient.close();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void testConnection(
            OutputEventAdaptorConfiguration outputEventAdaptorConfiguration, int tenantId) {

    }

    @Override
    public void removeConnectionInfo(OutputEventAdaptorMessageConfiguration outputEventAdaptorMessageConfiguration, OutputEventAdaptorConfiguration outputEventAdaptorConfiguration, int i) {

    }

    public static MQTTOutputEventAdaptorType getInstance() {
        return mqttOutputEventAdaptorType;
    }
}
