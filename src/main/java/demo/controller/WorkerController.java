package demo.controller;

import demo.service.WorkerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class WorkerController {
    @Autowired
    private WorkerService workerService;
}
