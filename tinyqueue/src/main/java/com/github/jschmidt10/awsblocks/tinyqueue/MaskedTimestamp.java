package com.github.jschmidt10.awsblocks.tinyqueue;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;

/**
 * An AttributeValue that will mask the timestamp for insert into DynamoDB.
 */
public class MaskedTimestamp extends AttributeValue {

    public MaskedTimestamp(long timestamp) {
        setN(String.valueOf(Long.MAX_VALUE ^ timestamp));
    }
}
