package demo.controller;


import demo.service.HelloService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/service/hello")
public class HelloController {
    private final Logger log = LoggerFactory.getLogger(HelloController.class);

    @Autowired
    HelloService helloService;

    @GetMapping("/{name}")
    public ResponseEntity<String> hello(@PathVariable("name") String name) {
        try {
            return new ResponseEntity<>(helloService.hello(name), HttpStatus.OK);
        } catch (Exception e){
            log.error(e.getMessage());
            return new ResponseEntity<>("", HttpStatus.BAD_REQUEST);
        }
    }
}
