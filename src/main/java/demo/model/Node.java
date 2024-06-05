package demo.model;

import java.util.List;

public class Node {
    String hostname;
    List<Worker> workers;

    public Node(String hostname) {
        this.hostname = hostname;
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
