package demo.service;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
import demo.model.Node;
import demo.model.Worker;

import java.util.ArrayList;
import java.util.List;

import demo.repository.NodeRepository;
import demo.repository.WorkerRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class ServerService {
    private final Logger log = LoggerFactory.getLogger(ServerService.class);
    private final RestClient restClient = RestClient.create();

    @Autowired
    private NodeRepository nodeRepository;

    @Autowired
    private WorkerRepository workerRepository;

    @Value("${server.port}")
    private int serverPort;

    @Transactional
    public boolean launchService(String service, int nbw, int firstPort) {
        ResponseEntity<List<Node>> response = restClient.get()
                .uri(String.format("http://registry:%d/registry/nodes", this.serverPort))
                .retrieve().toEntity(new ParameterizedTypeReference<List<Node>>() {});

        List<Node> nodes = response.getBody();

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

    @Transactional
    public void updateNodes() {
        List<Node> nodes = nodeRepository.streamAllBy().toList();

        this.restClient.post().uri(String.format("http://loadbalancer:%d/updateNodes", this.serverPort))
                .contentType(MediaType.APPLICATION_JSON).body(nodes).retrieve();
    }

    private DockerClient createDockerClient(String dockerHost) {
        DockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
                .withDockerHost("tcp://" + dockerHost + ":2375")
                .withDockerTlsVerify(false)
                .build();

        DockerHttpClient httpClient = new ApacheDockerHttpClient.Builder()
                .dockerHost(config.getDockerHost())
                .sslConfig(config.getSSLConfig())
                .build();

        return DockerClientImpl.getInstance(config, httpClient);
    }

}
