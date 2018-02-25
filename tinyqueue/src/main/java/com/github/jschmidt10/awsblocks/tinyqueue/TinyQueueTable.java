package com.github.jschmidt10.awsblocks.tinyqueue;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.*;
import com.google.common.base.Preconditions;

import java.util.Arrays;
import java.util.List;

import static com.github.jschmidt10.awsblocks.tinyqueue.DynamoField.QUEUE;
import static com.github.jschmidt10.awsblocks.tinyqueue.DynamoField.TIMESTAMP;

/**
 * The DynamoDB table required for TinyQueue to work.
 */
public class TinyQueueTable {

    private AmazonDynamoDB dynamo;
    private String table;

    public TinyQueueTable(AmazonDynamoDB dynamo, String table) {
        this.dynamo = dynamo;
        this.table = table;
    }

    /**
     * Creates the TinyQueue formatted table if it does not already exist.
     *
     * @param readUnits
     * @param writeUnits
     * @return True if the table was created, false, if it already existed.
     */
    public boolean createIfNotExists(long readUnits, long writeUnits) {
        return tableExists(table) || createTable(table, readUnits, writeUnits);
    }

    /**
     * Verifies the format of the given table matches what is needed by TinyQueue.
     *
     * @throws IllegalStateException If the table does not match the expected format.
     */
    public void verify() {
        DescribeTableResult dtr = dynamo.describeTable(table);
        List<KeySchemaElement> schema = dtr.getTable().getKeySchema();

        Preconditions.checkState(schema.contains(QUEUE.getKeySchemaElement()), "The hash key must be named " + QUEUE.getName());
        Preconditions.checkState(schema.contains(TIMESTAMP.getKeySchemaElement()), "The sort key must be named " + TIMESTAMP.getName());
    }

    private boolean createTable(String table, long readUnits, long writeUnits) {
        CreateTableRequest ctr = new CreateTableRequest();
        ctr.setTableName(table);
        ctr.setKeySchema(Arrays.asList(QUEUE.getKeySchemaElement(), TIMESTAMP.getKeySchemaElement()));
        ctr.setProvisionedThroughput(new ProvisionedThroughput(readUnits, writeUnits));
        ctr.setAttributeDefinitions(Arrays.asList(
                QUEUE.getAttributeDefinition(),
                TIMESTAMP.getAttributeDefinition()));

        dynamo.createTable(ctr);

        return true;
    }

    private boolean tableExists(String table) {
        try {
            dynamo.describeTable(table);
            return true;
        } catch (ResourceNotFoundException e) {
            return false;
        }
    }
}
