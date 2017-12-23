package com.github.jschmidt10.awsblocks.tinyqueue;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.QueryRequest;

import java.util.Map;
import java.util.TreeMap;

import static com.github.jschmidt10.awsblocks.tinyqueue.DynamoField.QUEUE;
import static com.github.jschmidt10.awsblocks.tinyqueue.DynamoField.TIMESTAMP;

/**
 * A timestamp based DynamoDB query.
 */
public class TimeRangeQuery extends QueryRequest {

    public TimeRangeQuery(String table, String queueName, long from, long to) {
        Map<String, AttributeValue> attributeValues = new TreeMap<>();
        attributeValues.put(":queue", new AttributeValue(queueName));
        attributeValues.put(":start", new MaskedTimestamp(to));
        attributeValues.put(":end", new MaskedTimestamp(from));

        setTableName(table);
        setKeyConditionExpression(QUEUE.getExpressionAlias() + " = :queue AND " + TIMESTAMP.getExpressionAlias() + " BETWEEN :start AND :end");
        setExpressionAttributeNames(DynamoField.getExpressionAliases());
        setExpressionAttributeValues(attributeValues);
    }
}
