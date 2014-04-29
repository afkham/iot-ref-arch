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

package org.wso2.event.adaptor.mqtt.internal.util;


public final class MQTTEventAdaptorConstants {

    private MQTTEventAdaptorConstants() {
    }

    public static final String EVENT_ADAPTOR_TYPE_MQTT = "mqtt";
    public static final String EVENT_ADAPTOR_CONF_BROKER_HOSTNAME = "broker.hostname";
    public static final String EVENT_ADAPTOR_CONF_BROKER_HOSTNAME_HINT = "broker.hostname.hint";
    public static final String EVENT_ADAPTOR_CONF_BROKER_PORT = "broker.port";
    public static final String EVENT_ADAPTOR_CONF_BROKER_PORT_HINT = "broker.port.hint";

    public static final String EVENT_ADAPTOR_CONF_TOPIC_NAME = "mqtt.topic.name";
    public static final String EVENT_ADAPTOR_CONF_TOPIC_NAME_HINT = "mqtt.topic.name.hint";

    public static final String EVENT_ADAPTOR_CLIENT_ID = "WSO2CEP-Input-Adaptor";
}
