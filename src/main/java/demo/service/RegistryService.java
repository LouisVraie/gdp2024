package demo.service;

import demo.model.Node;
import demo.model.Worker;
import demo.repository.NodeRepository;
import demo.repository.WorkerRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class RegistryService {
    private final Logger log = LoggerFactory.getLogger(RegistryService.class);
    private final int registeryFixedRate = 120000;
    private final RestClient restClient = RestClient.create();

    @Autowired
    private NodeRepository nodeRepository;
    @Autowired
    private WorkerRepository workerRepository;


    @Value("${server.port}")
    private int serverPort;

    /*
    @Scheduled(fixedRate = registeryFixedRate)
    @Transactional
    public void reportRunningWorkers() {
        if (System.getenv().get("HOSTNAME").equals("registry")) {
            List<Worker> workers = workerRepository.streamAllBy().toList();
            log.info("Running workers {}", workers);

            List<Worker> updatedWorkers = new ArrayList<>();

            workers.forEach(worker -> {
                // Compare date and check it's within 2min range
                if (new Date(System.currentTimeMillis() - registeryFixedRate).after(worker.getLastCheck())) {
                    log.info("Worker {} is not responding", worker.getPort());
                    workerRepository.delete(worker);
                    log.info("Worker {} is removed", worker.getPort());
                } else {
                    updatedWorkers.add(worker);
                }
            });

            this.updateWorkers(workers);
        }
    }

    @Transactional
    public void addWorker(Worker worker) {
        Worker oldWorker = this.workerRepository.findWorkerByPort(worker.getPort());

        if (oldWorker != null) {
            oldWorker.setLastCheck(worker.getLastCheck());
            this.workerRepository.save(oldWorker);
            log.info("Updated worker : {}", worker);
        } else {
            this.workerRepository.save(worker);
            log.info("Added worker : {}", worker);

            List<Worker> workers = workerRepository.streamAllBy().toList();

            this.updateWorkers(workers);
        }
    }

    private void updateWorkers(List<Worker> workers) {
        this.restClient.post().uri(String.format("http://loadbalancer:%d/updateWorkers", this.serverPort))
                .contentType(MediaType.APPLICATION_JSON).body(workers).retrieve();
    }*/

    public List<Worker> getWorkers() {
        return this.workerRepository.streamAllBy().toList();
    }

    @Transactional
    public void addNode(Node node) {
        this.nodeRepository.save(node);
        log.info("Added node : {}", node);

        this.updateNodes();
    }
    @Transactional
    public void updateNodes() {
        List<Node> nodes = nodeRepository.streamAllBy().toList();

        this.restClient.post().uri(String.format("http://loadbalancer:%d/updateNodes", this.serverPort))
                .contentType(MediaType.APPLICATION_JSON).body(nodes).retrieve();
    }

    public List<Node> getNodes() {
        return this.nodeRepository.streamAllBy().toList();
    }

    @Transactional
    public boolean launchService(String service, int nbw, int firstPort) {
        List<Node> nodes = this.nodeRepository.streamAllBy().toList();

        log.info("Nodes list : {}", nodes);

        if(!nodes.isEmpty()) {
            // create the given number of workers
            List<Worker> workers = new ArrayList<>();
            for (int i = 0; i < nbw; i++) {
                log.info("Add new worker");
                workers.add(new Worker(firstPort+i, service));
            }

            this.addWorkersToNode(nodes, workers);

            return true;
        }
        return false;
    }
    @Transactional
    public void addWorkersToNode(List<Node> nodes, List<Worker> workers) {
        log.info("addWorkersToNode Workers: {}", workers);
        int nodeCount = nodes.size();

        int index = 0;

        for (Worker worker : workers) {
            // Round robin workers in node
            Node node = nodes.get(index % nodeCount);
            log.info("Node");
            worker.setNode(node);
            workerRepository.save(worker);
            log.info("setNode");
            node.addWorker(worker);
            log.info("addWorker");
            nodeRepository.save(node);
            log.info("Updated node with workers : {}", node);
            index++;
        }

        this.updateNodes();
    }
}
