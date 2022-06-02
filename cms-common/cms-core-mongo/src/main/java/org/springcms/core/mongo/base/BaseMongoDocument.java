package org.springcms.core.mongo.base;

import lombok.Data;
import org.bson.types.ObjectId;

import java.io.Serializable;

@Data
public class BaseMongoDocument implements Serializable {
    private ObjectId id;
}
