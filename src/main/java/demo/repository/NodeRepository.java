package demo.repository;

import demo.model.Node;
import org.springframework.data.repository.CrudRepository;

import java.util.stream.Stream;

public interface NodeRepository extends CrudRepository<Node, String> {
    Stream<Node> streamAllBy();

    Node findNodeByHostname(String hostname);
}
