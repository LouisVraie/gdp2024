package demo.model;

import jakarta.persistence.*;

import java.util.Date;

@Entity
public class Worker {
    @Id
    private String hostname;

    private String type;
    private Date lastCheck;

    public Worker() {
    }
    public Worker(String hostname, String type) {
        this.hostname = hostname;
        this.type = type;
        this.lastCheck = new Date(System.currentTimeMillis());
    }

    public String getHostname() {
        return hostname;
    }
    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getType() { return type; }

    public void setType(String type) { this.type = type; }

    public Date getLastCheck() {
        return lastCheck;
    }
    public void setLastCheck(Date lastCheck) {
        this.lastCheck = lastCheck;
    }
}
