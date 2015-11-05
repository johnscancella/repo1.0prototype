package gov.loc.rdc.app;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;

import com.github.fakemongo.Fongo;
import com.mongodb.Mongo;

@Configuration
public class UnitTestApplicationConfig extends AbstractMongoConfiguration {
    @Override
    protected String getDatabaseName() {
        return "InMemoryMongo";
    }
 
    @Override
    public Mongo mongo() throws Exception {
        return new Fongo(getDatabaseName()).getMongo();
    }
}
