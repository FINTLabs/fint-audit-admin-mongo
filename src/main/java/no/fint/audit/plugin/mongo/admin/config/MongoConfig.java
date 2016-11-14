package no.fint.audit.plugin.mongo.admin.config;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.stereotype.Component;

@RefreshScope
@Component
@Configuration
@EnableMongoRepositories(basePackages = "no.fint.audit.plugin.mongo.admin.repository")
public class MongoConfig extends AbstractMongoConfiguration {

    @Value("${fint.audit.mongo.databasename}")
    private String databaseName;

    @Value("${fint.audit.mongo.hostname}")
    private String hostname;

    @Value("${fint.audit.mongo.port}")
    private int port;


    @Override
    protected String getDatabaseName() {
        return databaseName;
    }

    @Override
    public Mongo mongo() throws Exception {
        return new MongoClient(hostname, port);
    }

    @Override
    protected String getMappingBasePackage() {
        return "no.fint.audit.plugin.mongo.admin";
    }
}
