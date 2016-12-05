package no.fint.audit.plugin.mongo.admin;

import no.rogfk.hateoas.extension.annotations.EnableHalHypermediaSupport;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableHalHypermediaSupport
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
