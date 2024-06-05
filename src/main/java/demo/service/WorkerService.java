package demo.service;

import demo.controller.RegistryController;
import demo.model.Worker;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class WorkerService {
    private final Logger log = LoggerFactory.getLogger(WorkerService.class);
    private final  int workerFixedRate = 60000;
    private String hostname; // TODO : Delete
    private String service;
    private Worker self;

    @Value("${server.port}")
    private int serverPort;

    @EventListener(ApplicationReadyEvent.class)
    @Scheduled(fixedRate = workerFixedRate)
    public void afterStartup(){
        this.hostname = System.getenv().get("HOSTNAME");
        this.service = System.getenv().get("SERVICE");
        if (this.hostname != null && !this.hostname.equals("registry") && !this.hostname.equals("loadbalancer")){
            this.self = new Worker(hostname, service);
            RestClient restClient = RestClient.create();
            restClient.post()
                    .uri(String.format("http://registry:%d/registry/addWorker", this.serverPort))
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(this.self).retrieve();
        }
    }
}
