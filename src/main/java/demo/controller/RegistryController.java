package demo.controller;

import demo.model.Node;
import demo.model.Worker;
import demo.service.RegistryService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/registry")
public class RegistryController {
    private final Logger log = LoggerFactory.getLogger(RegistryController.class);

    private List<Node> nodes = new ArrayList<>();

    @Autowired
    private RegistryService registryService;

    /*@PostMapping("/addWorker")
    @Transactional
    public ResponseEntity<Worker> addWorker(@RequestBody Worker worker) {
        try{
            this.registryService.addWorker(worker);
            return new ResponseEntity<>(worker, HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }*/

    @PostMapping("/addNode")
    @Transactional
    public ResponseEntity<Node> addNode(@RequestBody Node node) {
        try{
            this.registryService.addNode(node);
            this.nodes.add(node);
            return new ResponseEntity<>(node, HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/nodes")
    @Transactional
    public ResponseEntity<Object> getNodes() {
        try{
            return new ResponseEntity<>(this.registryService.getNodes(), HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }
}
