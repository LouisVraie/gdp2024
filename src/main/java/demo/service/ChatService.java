package demo.service;

import org.springframework.stereotype.Service;

@Service
public class ChatService {
    public String chat() {
        return "Bonjour je suis " + System.getenv().get("HOSTNAME") + ", bienvenue chez moi ! Veux-tu discuter ?";
    }
}
