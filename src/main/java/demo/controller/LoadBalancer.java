package demo.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
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
    private Map<String, Set<String>> workers = new HashMap<>();
    private Map<String, Integer> indexes = new HashMap<>();
    @PostMapping("/updateWorkers")
    public ResponseEntity<Map<String, Set<String>>> updateWorkers(@RequestBody List<Worker> workers) {
        log.info("Updated workers : {}", workers);

        for (Worker worker : workers) {
            String type = worker.getType();

            // Set workers with types
            if (this.workers.containsKey(type)) {
                this.workers.get(type).add(worker.getHostname());
            } else {
                this.workers.put(type, new HashSet<>(Arrays.asList(worker.getHostname())));
                this.indexes.put(type, 0);
            }
        }

        return new ResponseEntity<>(this.workers, HttpStatus.OK);
    }

    private ResponseEntity<String> sendRequest(String path, String type) {
        List<String> workersValid;

        if (!this.workers.containsKey(type) || (workersValid = new ArrayList<>(this.workers.get(type))) == null || workersValid.isEmpty()) {
            return new ResponseEntity<>("No workers found !", HttpStatus.NOT_FOUND);
        }

        this.indexes.put(type, (this.indexes.get(type) + 1) % workersValid.size());

        while (!workersValid.isEmpty()) {
            String workerName = workersValid.get(this.indexes.get(type));
            String uri = String.format("http://%s:%d/service%s", workerName, this.serverPort, path);

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
                this.workers.get(type).remove(workerName);

                if (workersValid.isEmpty()) {
                    this.indexes.put(type, 0);
                } else {
                    Random r = new Random();

                    this.indexes.put(type, r.nextInt(workersValid.size()));
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
