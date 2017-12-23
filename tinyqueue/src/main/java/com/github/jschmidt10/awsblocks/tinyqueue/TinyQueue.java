package com.github.jschmidt10.awsblocks.tinyqueue;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;
import com.amazonaws.services.dynamodbv2.model.QueryRequest;
import com.google.common.base.Preconditions;

import java.util.Iterator;

import static com.github.jschmidt10.awsblocks.tinyqueue.DynamoField.QUEUE;
import static com.github.jschmidt10.awsblocks.tinyqueue.DynamoField.TIMESTAMP;

/**
 * A time-based queue that is backed by a DynamoDB table. As implied by the name, it is not suitable
 * for large volumes or high velocity data. The DynamoDB 'partition key' is the queue name, meaning that
 * all data will be managed by a single partition.
 */
public class TinyQueue implements AutoCloseable {

    private static final String DELETE_PROJECTION = QUEUE.getExpressionAlias() + ", " + TIMESTAMP.getExpressionAlias();

    private final String table;
    private final String queueName;
    private final AmazonDynamoDB dynamo;

    public TinyQueue(String table, String region, String queueName) {
        Preconditions.checkArgument(table != null, "Must define a table.");
        Preconditions.checkArgument(region != null, "Must define a region.");
        Preconditions.checkArgument(queueName != null, "Must define a queueName.");

        this.table = table;
        this.queueName = queueName;
        this.dynamo = AmazonDynamoDBClientBuilder.standard().withRegion(region).build();

        TinyQueueTable t = new TinyQueueTable(dynamo, table);
        t.verifyTable();
    }

    /**
     * Appends a new message to the queue with the current timestamp.
     *
     * @param message
     */
    public void append(byte[] message) {
        insert(message, System.currentTimeMillis());
    }

    /**
     * Inserts a message
     *
     * @param message
     * @param timestamp
     */
    public void insert(byte[] message, long timestamp) {
        dynamo.putItem(new PutItemRequest()
                .withTableName(table)
                .withItem(new TinyQueueItem(queueName, timestamp, message)));
    }

    /**
     * Fetches all messages that have been posted on or after the given timestamp.
     *
     * @param from
     * @return items
     */
    public Iterator<TinyQueueItem> fetch(long from) {
        return fetch(from, System.currentTimeMillis());
    }

    /**
     * Fetches all messages that were posted between the given timestamps.
     *
     * @param from
     * @param to
     * @return items
     */
    public Iterator<TinyQueueItem> fetch(long from, long to) {
        return dynamo
                .query(new TimeRangeQuery(table, queueName, from, to))
                .getItems()
                .stream()
                .map(TinyQueueItem::new)
                .iterator();
    }

    /**
     * Deletes all entries older than the cutoff.
     *
     * @param cutoff the end of the range to delete (exclusive)
     */
    public void delete(long cutoff) {
        delete(0L, cutoff);
    }

    /**
     * Deletes all entries in the given range.
     *
     * @param from the beginning of the range to delete (inclusive)
     * @param to   the end of the range to delete (exclusive)
     */
    public void delete(long from, long to) {
        QueryRequest query = new TimeRangeQuery(table, queueName, from, to);
        query.setProjectionExpression(DELETE_PROJECTION);

        dynamo
                .query(query)
                .getItems()
                .forEach(item -> dynamo.deleteItem(table, item));
    }

    @Override
    public void close() {
        dynamo.shutdown();
    }
}