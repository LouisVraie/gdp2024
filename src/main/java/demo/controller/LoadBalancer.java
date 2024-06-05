package demo.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import demo.model.Node;
import demo.model.Worker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestClient;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.*;

@Controller
public class LoadBalancer {
    private final Logger log = LoggerFactory.getLogger(LoadBalancer.class);

    @Value("${server.port}")
    private int serverPort;
    private List<Node> nodes;
    private Map<String, Set<Integer>> workers = new HashMap<>();
    private Map<String, Integer> indexes = new HashMap<>();

    @PostMapping("/updateWorkers")
    public ResponseEntity<Map<String, Set<Integer>>> updateWorkers(@RequestBody List<Worker> workers) {
        log.info("Updated workers : {}", workers);

        for (Worker worker : workers) {
            String service = worker.getService();

            // Set workers with types
            if (this.workers.containsKey(service)) {
                this.workers.get(service).add(worker.getPort());
            } else {
                this.workers.put(service, new HashSet<>(Arrays.asList(worker.getPort())));
                this.indexes.put(service, 0);
            }
        }

        return new ResponseEntity<>(this.workers, HttpStatus.OK);
    }

    @PostMapping("/updateNodes")
    public ResponseEntity<Map<String, Set<Integer>>> updateNodes(@RequestBody List<Node> nodes) {
        log.info("Updated nodes : {}", workers);

        for (Worker worker : workers) {
            String service = worker.getService();

            // Set workers with types
            if (this.workers.containsKey(service)) {
                this.workers.get(service).add(worker.getPort());
            } else {
                this.workers.put(service, new HashSet<>(Arrays.asList(worker.getHostname())));
                this.indexes.put(service, 0);
            }
        }

        return new ResponseEntity<>(this.workers, HttpStatus.OK);
    }

    private ResponseEntity<String> sendRequest(String path, String service) {
        List<Integer> workersValid;

        if (!this.workers.containsKey(service) || (workersValid = new ArrayList<>(this.workers.get(service))) == null || workersValid.isEmpty()) {
            return new ResponseEntity<>("No workers found !", HttpStatus.NOT_FOUND);
        }

        this.indexes.put(service, (this.indexes.get(service) + 1) % workersValid.size());

        while (!workersValid.isEmpty()) {
            Integer workerPort = workersValid.get(this.indexes.get(service));
            String uri = String.format("http://%s:%d/service%s", nodeName, workerPort, path);

            try {
                HttpRequest httpRequest = HttpRequest.newBuilder().uri(URI.create(uri)).timeout(Duration.ofSeconds(2))
                        .GET().build();
                HttpClient httpClient = HttpClient.newHttpClient();
                log.info("Sending a request at {}", uri);
                HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

                return new ResponseEntity<>(response.body(), HttpStatus.OK);
            } catch (Exception e) {
                log.error("Dead worker: {}", workerName);

                workersValid.remove(workerName);
                this.workers.get(service).remove(workerName);

                if (workersValid.isEmpty()) {
                    this.indexes.put(service, 0);
                } else {
                    Random r = new Random();

                    this.indexes.put(service, r.nextInt(workersValid.size()));
                }
            }
        }

        return new ResponseEntity<>("No worker available", HttpStatus.NOT_FOUND);
    }

    @GetMapping("/hello/{name}")
    public ResponseEntity<String> hello(@PathVariable("name") String name) {
        return this.sendRequest(String.format("/hello/%s", name), "hello");
    }

    @GetMapping("/chat")
    public ResponseEntity<String> chat() {
        return this.sendRequest("/chat", "chat");
    }
}
