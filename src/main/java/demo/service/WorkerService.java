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

import java.util.IllegalFormatConversionException;

@Service
public class WorkerService {
    private final Logger log = LoggerFactory.getLogger(WorkerService.class);
    private final int workerFixedRate = 60000;
    private Integer port;
    private String service;
    private Worker self;

    @Value("${server.port}")
    private int serverPort;

    /*@EventListener(ApplicationReadyEvent.class)
    @Scheduled(fixedRate = workerFixedRate)
    public void afterStartup(){
        try {
            this.port = Integer.valueOf(System.getenv().get("PORT"));
            this.service = System.getenv().get("SERVICE");
            if (this.port != null) {
                this.self = new Worker(port, service);
                RestClient restClient = RestClient.create();
                restClient.post()
                        .uri(String.format("http://registry:%d/registry/addWorker", this.serverPort))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(this.self).retrieve();
            }
        } catch (IllegalFormatConversionException e) {
            log.error(e.getMessage());
        }
    }*/
}
