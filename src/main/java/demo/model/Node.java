package demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Node {
    @Id
    String hostname;

    @OneToMany(mappedBy = "node")
    List<Worker> workers;

    public Node () {

    }

    public Node(String hostname) {
        this.hostname = hostname;
        this.workers = new ArrayList<>();
    }

    public Node(String hostname, List<Worker> workers) {
        this.hostname = hostname;
        this.workers = workers;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public List<Worker> getWorkers() {
        return workers;
    }

    public void setWorkers(List<Worker> workers) {
        this.workers = workers;
    }

    public void addWorker(Worker worker) {
        this.workers.add(worker);
    }

    public void removeWorker(Worker worker) {
        this.workers.remove(worker);
    }
}
