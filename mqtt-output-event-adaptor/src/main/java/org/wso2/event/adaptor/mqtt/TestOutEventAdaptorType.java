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
import org.wso2.event.adaptor.mqtt.internal.util.TestOutEventAdaptorConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;


public final class TestOutEventAdaptorType extends AbstractOutputEventAdaptor {

    private static final Log log = LogFactory.getLog(TestOutEventAdaptorType.class);
    private ResourceBundle resourceBundle;
    private MQTTBrokerConnectionConfig mqttBrokerConnectionConfig;
    private MQTTClient mqttClient;

    @Override
    protected String getName() {
        return TestOutEventAdaptorConstants.EVENT_ADAPTOR_TYPE_TESTOUT;
    }

    @Override
    protected List<String> getSupportedOutputMessageTypes() {
        return null;
    }

    @Override
    protected void init() {
        this.resourceBundle = ResourceBundle.getBundle("org.wso2.event.adaptor.mqtt.i18n.Resources", Locale.getDefault());
        mqttBrokerConnectionConfig = new MQTTBrokerConnectionConfig("10.100.0.209","1883");
        String clientId = "R-Pi-Publisher";
        String topicName = "iot/demo";
        mqttClient  = new MQTTClient(mqttBrokerConnectionConfig,clientId,topicName);
    }

    @Override
    protected List<Property> getOutputAdaptorProperties() {
        List<Property> propertyList = new ArrayList<Property>();

        Property property1 = new Property(TestOutEventAdaptorConstants.EVENT_ADAPTOR_CONF_FIELD1);
        property1.setDisplayName(
                resourceBundle.getString(TestOutEventAdaptorConstants.EVENT_ADAPTOR_CONF_FIELD1));
        property1.setRequired(true);
        property1.setHint(resourceBundle.getString(TestOutEventAdaptorConstants.EVENT_ADAPTOR_CONF_FIELD1_HINT));
        propertyList.add(property1);

        return propertyList;
    }

    @Override
    protected List<Property> getOutputMessageProperties() {
        return null;
    }

    @Override
    public void publish(
            OutputEventAdaptorMessageConfiguration outputEventAdaptorMessageConfiguration,
            Object o, OutputEventAdaptorConfiguration outputEventAdaptorConfiguration,
            int tenantId) {
        try {
            mqttClient.publish(1, "Test".getBytes());
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
}
