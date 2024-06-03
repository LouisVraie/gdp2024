package demo.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import demo.model.Worker;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Random;

@Controller
public class LoadBalancer {
    private List<Worker> workers;

    private Random random;
    private int index = 0;

    private final RestClient restClient = RestClient.create();

    private void updateWorkers() throws JsonProcessingException {
        this.random = new Random();

        // Do while r is empty and select a new random worker
        // do {
        //     String r = restClient.get().uri("http://registery:8081/workers")
        //             .retrieve().body(String.class);
        //     ObjectMapper mapper = new ObjectMapper();
        //     this.workers = mapper.readValue(r, new TypeReference<List<Worker>>() {
        //     });

        //     if(workers.isEmpty()) {
        //         throw new RuntimeException("No workers available");
        //     }

        //     this.index = random.nextInt(this.workers.size());
        // } while (this.workers.isEmpty());


        String r = restClient.get().uri("http://registery:8081/workers")
                .retrieve().body(String.class);
        ObjectMapper mapper = new ObjectMapper();
        this.workers = mapper.readValue(r, new TypeReference<List<Worker>>() {
        });

        if(workers.isEmpty()) {
            throw new RuntimeException("No workers available");
        }

        this.index = (this.index + 1) % this.workers.size();
    }

    @GetMapping("/hi")
    public ResponseEntity<String> hello() throws JsonProcessingException {
        this.updateWorkers();
        String uri = "http://" + this.workers.get(this.index).getHostname() + ":8081/hello";
        String rw = restClient.get().uri(uri).retrieve().body(String.class);

        return new ResponseEntity<>(rw, HttpStatus.OK);
    }
}
