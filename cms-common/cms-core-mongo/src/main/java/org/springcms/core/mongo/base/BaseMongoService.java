package org.springcms.core.mongo.base;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.List;

public class BaseMongoService<T extends BaseMongoDocument> {
    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Lazy
    @Autowired
    MongoOperations mongoOperations;

    public T insert(T entity) {
        return mongoOperations.insert(entity);
    }

    public Boolean insertAll(List<T> entitys) {
        mongoOperations.insertAll(entitys);
        return true;
    }

    public T save(T entity) {
        return mongoOperations.save(entity);
    }

    public Long deleteByIds(List ids, T entity) {
        Query query = new Query(Criteria.where("_id").in(ids));
        DeleteResult result = mongoOperations.remove(query, entity.getClass());
        return result.getDeletedCount();
    }

    public Long updateById(T entity) {
        try {
            Query query = new Query(Criteria.where("_id").is(entity.getId()));

            entity.setId(null);
            Update update = Update.fromDocument(Document.parse(JSON.toJSONString(entity)));

            UpdateResult result = mongoOperations.updateFirst(query, update, entity.getClass());
            return result.getMatchedCount();
        } catch (Exception e) {
            logger.error(e.getMessage());
            return 0L;
        }
    }

    public T getById(Object id, T entity) {
        return (T)mongoOperations.findById(id, entity.getClass());
    }

    public JSONObject query(T entity, Integer current, Integer size) {
        JSONObject jsonObject = null;
        try {
            Query query = new Query().skip((current - 1) * size).limit(size);

            Long total = mongoOperations.count(query, entity.getClass());
            List<T> entityList = (List<T>) mongoOperations.find(query, entity.getClass());

            jsonObject = new JSONObject();
            jsonObject.put("total", total);
            jsonObject.put("current", current);
            jsonObject.put("size", entityList.size());
            jsonObject.put("pages", (int)Math.ceil(total * 1.0 / size));
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return jsonObject;
    }
}
