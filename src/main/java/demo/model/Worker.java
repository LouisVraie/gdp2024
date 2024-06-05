package demo.model;

import jakarta.persistence.*;

import java.util.Date;

@Entity
public class Worker {
    @Id
    private int port;
    private String service;
    private Date lastCheck;

    public Worker() {
    }

    public Worker(int port, String service) {
        this.port = port;
        this.service = service;
        this.lastCheck = new Date(System.currentTimeMillis());
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getService() { return service; }
    public void setService(String type) { this.service = type; }

    public Date getLastCheck() {
        return lastCheck;
    }
    public void setLastCheck(Date lastCheck) {
        this.lastCheck = lastCheck;
    }
}
