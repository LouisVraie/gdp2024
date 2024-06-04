package demo.service;

import org.springframework.stereotype.Service;

@Service
public class HelloService {
    public String hello(String name) {
        return "Bonjour " + name + ", je m'appelle " + System.getenv().get("HOSTNAME");
    }
}
