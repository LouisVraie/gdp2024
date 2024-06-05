package demo.service;

import demo.model.Node;
import demo.model.Worker;

import java.util.ArrayList;
import java.util.List;

import demo.repository.NodeRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class ServerService {
    private final Logger log = LoggerFactory.getLogger(ServerService.class);
    private final RestClient restClient = RestClient.create();

    @Autowired
    private NodeRepository nodeRepository;
    private String hostname;
    private String serverPort;

    public boolean launchService(String service, int nbw, int firstPort) {
        this.hostname = System.getenv().get("HOSTNAME");
        this.serverPort = System.getenv().get("PORT");
        if (this.hostname != null && this.serverPort != null && this.hostname.matches("/node.*/")){
            // create the given number of workers
            List<Worker> workers = new ArrayList<>();
            for (int i = 0; i < nbw; i++) {
                workers.add(new Worker(firstPort+i, service));
            }

            this.addWorkersToNode(workers);

            return true;
        }
        return false;
    }
    @Transactional
    public void addWorkersToNode(List<Worker> workers) {
        List<Node> nodes = nodeRepository.streamAllBy().toList();

        int nodeCount = nodes.size();

        int index = 0;

        for (Worker worker : workers) {
            // Round robin workers in node
            Node node = nodes.get(index % nodeCount);
            node.addWorker(worker);
            nodeRepository.save(node);
            log.info("Added node : {}", node);
            index++;
        }

        this.updateNodes();
    }

    @Transactional
    public void updateNodes() {
        List<Node> nodes = nodeRepository.streamAllBy().toList();

        this.restClient.post().uri(String.format("http://loadbalancer:%d/updateNodes", this.serverPort))
                .contentType(MediaType.APPLICATION_JSON).body(nodes).retrieve();
    }
}
