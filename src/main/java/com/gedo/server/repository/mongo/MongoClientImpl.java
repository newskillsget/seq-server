package com.gedo.server.repository.mongo;


import com.gedo.server.business.service.SeqService;
import com.gedo.server.domain.AppRoomId;
import com.gedo.server.domain.RoomSeq;
import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.ReturnDocument;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

/**
 * Created by Gedo on 2019/4/4.
 */
@Component("MongoClientImpl")
public class MongoClientImpl implements MongoDBClient {

    private final Logger logger = LoggerFactory.getLogger(MongoClientImpl.class);

    private static volatile MongoClient mongoClient;

    private static final String DB = "mongodb";
    @Value("${spring.mongo.hosts}")
    private String hosts;

    @Value("${spring.mongo.user}")
    private String user;

    @Value("${spring.mongo.authdb}")
    private String authdb;

    @Value("${spring.mongo.password}")
    private String password;
    private void initClient() throws Exception {
        List<ServerAddress> seeds = initMonoServer();
        //MongoCredential.createScramSha1Credential()三个参数分别为 用户名 数据库名称 密码
        MongoCredential credential = MongoCredential.createScramSha1Credential(user, authdb, password.toCharArray());
        List<MongoCredential> credentials = new ArrayList<>();
        credentials.add(credential);
        mongoClient = new MongoClient(seeds, credentials);
        logger.info("=====MongoClient init successfully!");
    }

    private List<ServerAddress> initMonoServer() {
        if (StringUtils.isBlank(hosts)) {
            throw new RuntimeException("mongo hosts param is blank");
        } else {
            String[] hostArray = hosts.split(";");
            List<ServerAddress> list = new ArrayList<ServerAddress>(hostArray.length);
            Stream.of(hostArray).forEach(str -> {
                String[] ss = str.split(":");
                ServerAddress serverAddr = new ServerAddress(ss[0], Integer.valueOf(ss[1]));
                list.add(serverAddr);
            });
            return list;
        }
    }

    /**
     * 获取mongodb连接
     */
    public MongoClient getMongoClient() {
        if (mongoClient == null) {
            synchronized (this) {
                if (mongoClient == null) {
                    try {
                        initClient();
                    } catch (Exception e) {
                        logger.error("", e);
                    }
                }
            }
        }
        return mongoClient;
    }

    /**
     * 访问数据库集合
     *
     * @param tableName 集合名称
     */
    @Override
    public MongoCollection<Document> getMongoCollection(String tableName) {
        return this.getMongoClient().getDatabase(DB).getCollection(tableName);
    }

    /**
     * 生成下一段RoomId
     */
    @Override
    public Document getNextRoomId(String appId, long primitiveRoomId, boolean insert) {
        MongoCollection<Document> collection = getMongoCollection(AppRoomId.TABLE_NAME);
        FindOneAndUpdateOptions options = new FindOneAndUpdateOptions();
        if (insert) {
            options.upsert(true);
        } else {
            options.upsert(false);
        }
        options.returnDocument(ReturnDocument.AFTER);
        Bson filter = Filters.and(Filters.eq(AppRoomId.APP_ID, appId),
                Filters.eq(AppRoomId.ROOM_ID, primitiveRoomId));
        return collection.findOneAndUpdate(filter, new Document("$inc", new Document(AppRoomId.ROOM_ID, 1000L)), options);
    }

    /**
     * 生成下一段seq
     */
    @Override
    public Document getNextSeq(String appId, long roomId, long primitiveSeq, boolean insert) {
        MongoCollection<Document> collection = getMongoCollection(RoomSeq.TABLE_NAME);
        FindOneAndUpdateOptions options = new FindOneAndUpdateOptions();
        if (insert) {
            options.upsert(true);
        } else {
            options.upsert(false);
        }
        options.returnDocument(ReturnDocument.AFTER);
        Bson filter = Filters.and(Filters.eq(RoomSeq.APP_ID, appId),
                Filters.eq(RoomSeq.ROOM_ID, roomId), Filters.eq(RoomSeq.SEQ, primitiveSeq));
        return collection.findOneAndUpdate(filter, new Document("$inc", new Document(RoomSeq.SEQ, 10000L)), options);
    }


    public void initAllIds() {
        FindIterable<Document> iterable = getMongoCollection(AppRoomId.TABLE_NAME).find();
        iterable.forEach(new Block<Document>() {
            @Override
            public void apply(Document appRoomId) {
                try {
                    Document RoomId = getNextRoomId(appRoomId.getString(AppRoomId.APP_ID),
                            appRoomId.getLong(AppRoomId.ROOM_ID), false);
                    AtomicLong start = new AtomicLong(appRoomId.getLong(AppRoomId.ROOM_ID));
                    AtomicLong end = new AtomicLong(RoomId.getLong(AppRoomId.ROOM_ID));
                    SeqService.APP_ROOMID.put(appRoomId.getString(AppRoomId.APP_ID), new AtomicLong[]{start, end});
                } catch (Exception e) {
                    logger.error("", e);
                }
            }
        });
        iterable = getMongoCollection(RoomSeq.TABLE_NAME).find();
        iterable.forEach(new Block<Document>() {
            @Override
            public void apply(Document roomSeq) {
                try {
                    Document nextRoomseq = getNextSeq(((Document)roomSeq.get("_id")).getString("appId"),
                            ((Document)roomSeq.get("_id")).getLong("roomId"), roomSeq.getLong(RoomSeq.SEQ), false);
                    AtomicLong start = new AtomicLong(roomSeq.getLong(RoomSeq.SEQ));
                    AtomicLong end = new AtomicLong(nextRoomseq.getLong(RoomSeq.SEQ));
                    if (null == SeqService.APP_ROOMID_SEQ.get(RoomSeq.APP_ID)) {
                        ConcurrentHashMap<String, AtomicLong[]> roomMap = new ConcurrentHashMap<>();
                        roomMap.put(String.valueOf(nextRoomseq.getLong(RoomSeq.ROOM_ID)), new AtomicLong[]{start, end});
                    } else {
                        SeqService.APP_ROOMID_SEQ.get(RoomSeq.APP_ID).put(String.valueOf(nextRoomseq.getLong(RoomSeq.ROOM_ID))
                                , new AtomicLong[]{start, end});
                    }
                } catch (Exception e) {
                    logger.error("", e);
                }
            }
        });
    }


    public Document getNextNums(String appKey, long primitiveNum, long roomId) {
        long size;
        Bson filter;
        String updateField;
        MongoCollection<Document> collection;
        FindOneAndUpdateOptions options = new FindOneAndUpdateOptions();
        options.upsert(true).returnDocument(ReturnDocument.AFTER);
        if (roomId == 0) {
            size = 1000L;
            updateField = "roomId";
            collection = getMongoCollection(AppRoomId.TABLE_NAME);
            filter = Filters.and(Filters.eq("_id", appKey), Filters.eq(updateField, primitiveNum));
        } else {
            size = 10000L;
            updateField = "seq";
            collection = getMongoCollection(RoomSeq.TABLE_NAME);
            filter = Filters.and(Filters.eq("_id.appKey", appKey),
                    Filters.eq("_id.roomId", roomId), Filters.eq(updateField, primitiveNum));
        }
        return collection.findOneAndUpdate(filter, new Document("$inc", new Document(updateField, size)), options);
    }

}



