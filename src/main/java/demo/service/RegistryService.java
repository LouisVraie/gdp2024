package demo.service;

import demo.model.Worker;
import demo.repository.WorkerRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class RegistryService {
    private final Logger log = LoggerFactory.getLogger(RegistryService.class);
    private final int registeryFixedRate = 120000;
    private final RestClient restClient = RestClient.create();

    @Autowired
    private WorkerRepository workersRepo;

    @Value("${server.port}")
    private int serverPort;

    @Scheduled(fixedRate = registeryFixedRate)
    @Transactional
    public void reportRunningWorkers() {
        if (System.getenv().get("HOSTNAME").equals("registry")) {
            List<Worker> workers = workersRepo.streamAllBy().toList();
            log.info("Running workers {}", workers);

            List<Worker> updatedWorkers = new ArrayList<>();

            workers.forEach(worker -> {
                // Compare date and check it's within 2min range
                if (new Date(System.currentTimeMillis() - registeryFixedRate).after(worker.getLastCheck())) {
                    log.info("Worker {} is not responding", worker.getHostname());
                    workersRepo.delete(worker);
                    log.info("Worker {} is removed", worker.getHostname());
                } else {
                    updatedWorkers.add(worker);
                }
            });

            this.updateWorkers(workers);
        }
    }

    @Transactional
    public void addWorker(Worker worker) {
        Worker oldWorker = this.workersRepo.findWorkerByHostname(worker.getHostname());

        if (oldWorker != null) {
            oldWorker.setLastCheck(worker.getLastCheck());
            this.workersRepo.save(oldWorker);
            log.info("Updated worker : {}", worker);
        } else {
            this.workersRepo.save(worker);
            log.info("Added worker : {}", worker);

            List<Worker> workers = workersRepo.streamAllBy().toList();

            this.updateWorkers(workers);
        }
    }

    private void updateWorkers(List<Worker> workers) {
        this.restClient.post().uri(String.format("http://loadbalancer:%d/updateWorkers", this.serverPort))
                .contentType(MediaType.APPLICATION_JSON).body(workers).retrieve();
    }

    public List<Worker> getWorkers() {
        return this.workersRepo.streamAllBy().toList();
    }
}
