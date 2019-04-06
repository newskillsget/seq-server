package com.gedo.server.business.service;

import com.gedo.server.domain.AppRoomId;
import com.gedo.server.domain.RoomSeq;
import com.gedo.server.domain.SeqReq;
import com.gedo.server.repository.mongo.MongoClientImpl;
import com.gedo.server.repository.mongo.MongoDBClient;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Gedo on 2019/4/4.
 */
@Service
public class SeqService {

    private static final Logger log = LoggerFactory.getLogger(SeqService.class);
    //<appId,<LastestId,maxId>>
    public static Map<String, AtomicLong[]> APP_ROOMID = new ConcurrentHashMap<>();
    //<appId,<roomId<LastestSeq,maxSeq>>>
    public static Map<String, ConcurrentHashMap<String, AtomicLong[]>> APP_ROOMID_SEQ = new ConcurrentHashMap<>();
    private ReentrantLock lock = new ReentrantLock();

    @Autowired
    private MongoDBClient mongoDBClient;

    @PostConstruct
    public void initIds() {
        mongoDBClient.initAllIds();
    }

    public Long getNextNum(SeqReq msg) {
        Long nextNum;
        if (null == msg.getRoomId()) {
            nextNum = getRoomIdByAppId(msg);
        } else {
            nextNum = getSeqByRoomId(msg);
        }
        return nextNum;
    }

    private Long getRoomIdByAppId(SeqReq seqReq) {
        AtomicLong[] roomIdArray = APP_ROOMID.get(seqReq.getAppId());
        if (null == roomIdArray || roomIdArray.length <= 1) {
            Document nextRoomId = null;
            synchronized (seqReq.getAppId().intern()) {
                try {
                    nextRoomId = mongoDBClient.getNextRoomId(seqReq.getAppId(), 0L, true);
                } catch (Exception e) {
                    log.error("", e);
                }
            }
            if (null != nextRoomId) {
                roomIdArray = new AtomicLong[]{new AtomicLong(1L), new AtomicLong(1000L)};
                APP_ROOMID.put(seqReq.getAppId(), roomIdArray);
                //给这个app roomId为1的房间初始化序号
                initRoomSeq(seqReq.getAppId(), 1);
            } else {
                return reTry(seqReq);
            }
        }
        long resultRoomId = roomIdArray[0].incrementAndGet();
        long primitiveRoomId = roomIdArray[1].longValue();
        if (resultRoomId >= primitiveRoomId) {
            Document nextRoomId = mongoDBClient.getNextRoomId(seqReq.getAppId(), primitiveRoomId, false);
            if (null != nextRoomId) {
                roomIdArray[1] = new AtomicLong(nextRoomId.getLong(AppRoomId.ROOM_ID));
            } else {
                return reTry(seqReq);
            }
        }
        return resultRoomId;
    }

    private Long getSeqByRoomId(SeqReq seqReq) {
        ConcurrentHashMap<String, AtomicLong[]> roomSeqMap = APP_ROOMID_SEQ.get(seqReq.getAppId());
        if (null == roomSeqMap) {
            //这个app的roomId序列尚未初始化完毕
            return reTry(seqReq);
        }
        AtomicLong[] seqArray = roomSeqMap.get(String.valueOf(seqReq.getRoomId()));
        if (null == seqArray) {
            //初始化该roomId序号
            //synchronized ((seqReq.getAppId() + seqReq.getRoomId()).intern()) {
            lock.lock();
            try {
                if (null == roomSeqMap.get(String.valueOf(seqReq.getRoomId()))) {
                    initRoomSeq(seqReq.getAppId(), seqReq.getRoomId());
                }
            } catch (Exception e) {
                log.error("", e);
            } finally {
                lock.unlock();
            }
            seqArray = roomSeqMap.get(String.valueOf(seqReq.getRoomId()));
        }
        long resultSeq = seqArray[0].incrementAndGet();
        long primitiveSeq = seqArray[1].longValue();
        if (resultSeq >= primitiveSeq) {
            Document nextSeq = mongoDBClient.getNextSeq(seqReq.getAppId(), seqReq.getRoomId(), primitiveSeq, false);
            if (null != nextSeq) {
                seqArray[1] = new AtomicLong(nextSeq.getLong(RoomSeq.SEQ));
            } else {
                return reTry(seqReq);
            }
        }
        return resultSeq;
    }

    private void initRoomSeq(String appId, long roomId) {
        mongoDBClient.getNextSeq(appId, roomId, 0, true);
        ConcurrentHashMap<String, AtomicLong[]> firstRoomSeq = new ConcurrentHashMap<>();
        if (null != APP_ROOMID_SEQ.get(appId)) {
            firstRoomSeq = APP_ROOMID_SEQ.get(appId);
        }
        AtomicLong[] seqArray = new AtomicLong[]{new AtomicLong(1L), new AtomicLong(10000L)};
        firstRoomSeq.put(String.valueOf(roomId), seqArray);
        APP_ROOMID_SEQ.put(appId, firstRoomSeq);
    }

    private Long reTry(SeqReq seqReq) {
        Long ret = 0L;
        try {
            Thread.sleep(100L);
            if (seqReq.getRetry() <= 3) {
                seqReq.setRetry(seqReq.getRetry() + 1);
                ret = getNextNum(seqReq);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return ret;
    }
}



