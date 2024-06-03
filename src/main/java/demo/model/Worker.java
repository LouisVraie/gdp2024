package demo.model;

import jakarta.persistence.*;

import java.util.Date;

@Entity
public class Worker {
    @Id
    private String hostname;

    private Date lastCheck;

    public Worker() {
    }
    public Worker(String hostname) {
        this.hostname = hostname;
    }

    public String getHostname() {
        return hostname;
    }
    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public Date getLastCheck() {
        return lastCheck;
    }
    public void setLastCheck(Date lastCheck) {
        this.lastCheck = lastCheck;
    }
}
