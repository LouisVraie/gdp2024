package demo.controller;

import demo.model.Worker;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.stream.Stream;

@RestController
@RequestMapping("/workers")
public class RegisteryController {
    private static final Logger log = LoggerFactory.getLogger(RegisteryController.class);
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    @Autowired
    private WorkerRepository workersRepo;

    @Scheduled(fixedRate = 2 * 6 * 1000)
    @Transactional
    public void reportRunningWorkers() {
        log.info("Running workers {}", workersRepo.streamAllBy().toList());

        workersRepo.streamAllBy().forEach(worker -> {
            // Compare date and check it's within 2min range
            if (new Date(System.currentTimeMillis() - 2 * 6 * 1000).after(worker.getLastCheck())) {
                log.info("Worker {} is not responding", worker.getHostname());
                workersRepo.delete(worker);
                log.info("Worker {} is removed", worker.getHostname());
            }
        });
    }

    @Transactional
    @GetMapping()
    public ResponseEntity<Object> getUsers() {
        Stream<Worker> s = workersRepo.streamAllBy();
        return new ResponseEntity<>(s.toList(), HttpStatus.OK);
    }

    @PostMapping()
    public ResponseEntity<Worker> put(@RequestBody Worker user) {
        user.setLastCheck(new Date(System.currentTimeMillis()));
        workersRepo.save(user);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }
}
