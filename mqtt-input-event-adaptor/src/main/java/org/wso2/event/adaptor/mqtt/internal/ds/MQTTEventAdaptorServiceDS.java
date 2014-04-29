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

package org.wso2.event.adaptor.mqtt.internal.ds;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.service.component.ComponentContext;
import org.wso2.carbon.event.input.adaptor.core.InputEventAdaptorFactory;
import org.wso2.carbon.utils.ConfigurationContextService;
import org.wso2.event.adaptor.mqtt.MQTTEventAdaptorFactory;


/**
 * @scr.component name="input.MQTTEventAdaptorService.component" immediate="true"
 * @scr.reference name="configurationcontext.service"
 * interface="org.wso2.carbon.utils.ConfigurationContextService" cardinality="1..1"
 * policy="dynamic" bind="setConfigurationContextService" unbind="unsetConfigurationContextService"
 */
public class MQTTEventAdaptorServiceDS {

    private static final Log log = LogFactory.getLog(MQTTEventAdaptorServiceDS.class);

    /**
     * initialize the agent service here service here.
     *
     * @param context
     */
    protected void activate(ComponentContext context) {

        try {
            InputEventAdaptorFactory mqttEventAdaptorFactory = new MQTTEventAdaptorFactory();
            context.getBundleContext().registerService(InputEventAdaptorFactory.class.getName(), mqttEventAdaptorFactory, null);
            log.info("Successfully deployed the MQTT input event adaptor service");
        } catch (RuntimeException e) {
            log.error("Can not create the MQTT input event adaptor service ", e);
            e.printStackTrace();
        }
    }

    protected void setConfigurationContextService(
            ConfigurationContextService configurationContextService) {
    }

    protected void unsetConfigurationContextService(
            ConfigurationContextService configurationContextService) {
    }
}
