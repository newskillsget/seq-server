package com.gedo.server.repository.mongo;

import com.mongodb.client.MongoCollection;
import org.bson.Document;

/**
 * Created by Gedo on 2019/4/4.
 */
public interface MongoDBClient {

    MongoCollection<Document> getMongoCollection(String tableName);

    Document getNextRoomId(String appKey, long primitiveRoomId, boolean insert);

    Document getNextSeq(String appKey, long roomId, long primitiveSeq, boolean insert);

    void initAllIds();


}



