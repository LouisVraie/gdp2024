package demo.controller;

import demo.service.ChatService;
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
@RequestMapping("/service/chat")
public class ChatController {
    private final Logger log = LoggerFactory.getLogger(HelloController.class);

    @Autowired
    private ChatService chatService;

    @GetMapping()
    public ResponseEntity<String> chat() {
        try {
            return new ResponseEntity<>(chatService.chat(), HttpStatus.OK);
        } catch (Exception e){
            log.error(e.getMessage());
            return new ResponseEntity<>("", HttpStatus.BAD_REQUEST);
        }
    }
}
