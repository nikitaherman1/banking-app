package com.backend.bankingapplication.core.configuration.sql;

public class SnowFlakeIdGenerator {

    private final long nodeId;

    private long sequence = 0L;
    private long lastTimestamp = -1L;

    private static final long NODE_BITS = 10L;
    private static final long SEQUENCE_BITS = 12L;
    private static final long MAX_NODE_ID = (1 << NODE_BITS) - 1;
    private static final long MAX_SEQUENCE = ~(-1L << SEQUENCE_BITS);

    public SnowFlakeIdGenerator(long nodeId) {
        if (nodeId < 0 || nodeId > MAX_NODE_ID) {
            throw new IllegalArgumentException("Invalid node id value, valid range is from 0 to 1023");
        }
        this.nodeId = nodeId << SEQUENCE_BITS;
    }

    public synchronized long nextId() {
        long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis < lastTimestamp) {
            throw new RuntimeException("Incorrect time because time went backwards");
        }

        if (currentTimeMillis == lastTimestamp) {
            sequence = (sequence + 1) & MAX_SEQUENCE;
            if (sequence == 0) {
                currentTimeMillis = waitForNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0;
        }
        lastTimestamp = currentTimeMillis;
        long timestamp = (currentTimeMillis << (NODE_BITS + SEQUENCE_BITS));

        return timestamp | nodeId | sequence;
    }

    private long waitForNextMillis(long lastTimestamp) {
        long currentTimeMillis = System.currentTimeMillis();
        while (currentTimeMillis <= lastTimestamp) {
            currentTimeMillis = System.currentTimeMillis();
        }
        return currentTimeMillis;
    }
}
