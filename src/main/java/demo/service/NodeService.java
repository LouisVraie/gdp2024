package demo.service;

import demo.model.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class NodeService {
    private final Logger log = LoggerFactory.getLogger(NodeService.class);
    private String hostname;

    @Value("${server.port}")
    private String serverPort;

    @EventListener(ApplicationReadyEvent.class)
    public void afterStartup(){
        this.hostname = System.getenv().get("HOSTNAME");
        log.info("NodeService hostname : {}", this.hostname);
        if (this.hostname != null && this.serverPort != null && this.hostname.matches("^node.*")){
            log.info("Inside");
            Node node = new Node(hostname);
            RestClient restClient = RestClient.create();
            restClient.post()
                    .uri(String.format("http://registry:%s/registry/addNode", this.serverPort))
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(node).retrieve();
        }
    }
}
