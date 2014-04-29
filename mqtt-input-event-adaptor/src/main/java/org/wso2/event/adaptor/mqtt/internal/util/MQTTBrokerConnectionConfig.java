package org.wso2.event.adaptor.mqtt.internal.util;

/**
 * Created by deep on 4/22/14.
 */
public class MQTTBrokerConnectionConfig {
    private String brokerProtocole = null;
    private String brokerHost = null;
    private String prokerPort = null;
    private String brokerUsername = null;
    private String brokerPassword = null;
    private boolean cleanSession = false;
    private String brokerUrl;

    public String getProkerPort() {
        return prokerPort;
    }

    public void setProkerPort(String prokerPort) {
        this.prokerPort = prokerPort;
    }

    public String getBrokerProtocole() {
        return brokerProtocole;
    }

    public void setBrokerProtocole(String brokerProtocole) {
        this.brokerProtocole = brokerProtocole;
    }

    public String getBrokerHost() {
        return brokerHost;
    }

    public void setBrokerHost(String brokerHost) {
        this.brokerHost = brokerHost;
    }

    public String getBrokerPassword() {
        return brokerPassword;
    }

    public void setBrokerPassword(String brokerPassword) {
        this.brokerPassword = brokerPassword;
    }

    public String getBrokerUsername() {
        return brokerUsername;
    }

    public void setBrokerUsername(String brokerUsername) {
        this.brokerUsername = brokerUsername;
    }

    public void setCleanSession(boolean cleanSession) {
        this.cleanSession = cleanSession;
    }


    public boolean isCleanSession() {
        return cleanSession;
    }

    public String getBrokerUrl() {
            return new String(this.getBrokerProtocole() + "://" + this.getBrokerHost() + ":" + this.getProkerPort());
    }
}
