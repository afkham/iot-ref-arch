package org.wso2.iot.refarch.rpi.agent;

/**
 * Created by deep on 4/29/14.
 */
public class MQTTBrokerConnectionConfig {
    private String brokerProtocole = "tcp";
    private String brokerHost = null;
    private String brokerPort = null;
    private String brokerUsername = null;
    private String brokerPassword = null;
    private boolean cleanSession = false;
    private String brokerUrl;

    MQTTBrokerConnectionConfig(String host,String port){
        this.brokerHost = host;
        this.brokerPort = port;
    }

    public String getProkerPort() {
        return brokerPort;
    }

    public void setProkerPort(String prokerPort) {
        this.brokerPort = prokerPort;
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
