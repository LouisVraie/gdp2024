package demo.model;

import jakarta.persistence.*;

import java.util.Date;

@Entity
public class Worker {
    @Id
    private String hostname;

    private String service;
    private Date lastCheck;

    public Worker() {
    }
    public Worker(String hostname, String service) {
        this.hostname = hostname;
        this.service = service;
        this.lastCheck = new Date(System.currentTimeMillis());
    }

    public String getHostname() {
        return hostname;
    }
    public void setHostname(String hostname) {
        this.hostname = hostname;
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
