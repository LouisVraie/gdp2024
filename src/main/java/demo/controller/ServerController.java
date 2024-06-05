package demo.controller;

import demo.service.RegistryService;
import demo.service.ServerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ServerController {
    private final Logger log = LoggerFactory.getLogger(ServerController.class);

    @Autowired
    private RegistryService registryService;

    private int numNextWorker = 8000;

    @GetMapping("/launch/{service}")
    public ResponseEntity<String> launchService(
            @PathVariable("service") String service,
            @RequestParam(value = "nbw") int nbw
    ) {
        try{
            boolean isNodeStarted = this.registryService.launchService(service, nbw, this.numNextWorker);
            this.numNextWorker += nbw;
            String message;
            if (isNodeStarted) {
                message = String.format("New Node started on service %s with %d workers", service, nbw);
            } else {
                message = String.format("WARN : Node not started !!! on service %s with %d workers", service, nbw);
            }
            return new ResponseEntity<>(message, HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }
}
