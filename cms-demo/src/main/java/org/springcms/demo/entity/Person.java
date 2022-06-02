package org.springcms.demo.entity;

import lombok.Data;
import org.springcms.core.mongo.base.BaseMongoDocument;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "people")
public class Person extends BaseMongoDocument {
    private final String name;
    private final int sex;
    private final int age;
}
