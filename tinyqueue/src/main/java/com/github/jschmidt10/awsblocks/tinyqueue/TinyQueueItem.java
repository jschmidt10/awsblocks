package com.github.jschmidt10.awsblocks.tinyqueue;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;

import java.nio.ByteBuffer;
import java.util.Map;
import java.util.TreeMap;

import static com.github.jschmidt10.awsblocks.tinyqueue.DynamoField.*;

/**
 * A DynamoDB item formatted for TinyQueue usage.
 */
public class TinyQueueItem extends TreeMap<String, AttributeValue> {

    private final String queueName;
    private final long timestamp;
    private final byte[] message;

    public TinyQueueItem(String queueName, long timestamp, byte[] message) {
        this.queueName = queueName;
        this.timestamp = timestamp;
        this.message = message;

        put(QUEUE.getName(), new AttributeValue(queueName));
        put(TIMESTAMP.getName(), new MaskedTimestamp(timestamp));
        put(MESSAGE.getName(), new AttributeValue().withB(ByteBuffer.wrap(message)));
    }

    public TinyQueueItem(Map<String, AttributeValue> item) {
        putAll(item);

        this.queueName = item.get(QUEUE.getName()).getS();
        this.timestamp = Long.MAX_VALUE ^ Long.parseLong(item.get(TIMESTAMP.getName()).getN());
        this.message = item.get(MESSAGE.getName()).getB().array();
    }

    public String getQueueName() {
        return queueName;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public byte[] getMessage() {
        return message;
    }
}
