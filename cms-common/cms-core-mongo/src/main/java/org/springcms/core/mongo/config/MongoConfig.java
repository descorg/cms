package org.springcms.core.mongo.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;

@Configuration
public class MongoConfig extends AbstractMongoClientConfiguration {

    @Autowired
    private Environment environment;

    @Override
    public MongoClient mongoClient() {
        return MongoClients.create(environment.getProperty("spring.data.mongodb.uri", "mongodb://localhost:27017/cms"));
    }

    @Override
    protected String getDatabaseName() {
        return environment.getProperty("spring.data.mongodb.database", "cms");
    }
}
