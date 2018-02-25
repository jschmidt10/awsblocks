package com.github.jschmidt10.awsblocks.tinyqueue;

import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.google.common.base.Preconditions;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

/**
 * The fields needed for TinyQueue to work in DynamoDB.
 */
public enum DynamoField {
    QUEUE("QueueName", "S", KeyType.HASH), TIMESTAMP("Timestamp", "N", KeyType.RANGE, "#TS"), MESSAGE("Message", "B");

    /**
     * Gets a mapping of expression aliases to their attribute names. For use with DynamoDB queries.
     *
     * @return expression alias mappings
     */
    public static Map<String, String> getExpressionAliases() {
        Map<String, String> names = new TreeMap<>();

        names.put(QUEUE.getExpressionAlias(), QUEUE.getName());
        names.put(TIMESTAMP.getExpressionAlias(), TIMESTAMP.getName());

        return Collections.unmodifiableMap(names);
    }

    private final String name;
    private final String type;
    private final KeyType keyType;
    private final String expressionAlias;

    private DynamoField(String name, String type) {
        this(name, type, null, "#" + name);
    }

    private DynamoField(String name, String type, KeyType keyType) {
        this(name, type, keyType, "#" + name);
    }

    private DynamoField(String name, String type, KeyType keyType, String expressionAlias) {
        this.name = name;
        this.type = type;
        this.keyType = keyType;
        this.expressionAlias = expressionAlias;
    }

    public String getName() {
        return name;
    }

    public String getExpressionAlias() {
        return expressionAlias;
    }

    /**
     * Gets the KeySchemaElement for this field.
     *
     * @return key schema element
     */
    public KeySchemaElement getKeySchemaElement() {
        Preconditions.checkState(keyType != null, "Can only create KeySchemaElement for key elements.");
        return new KeySchemaElement(name, keyType);
    }

    /**
     * Gets the AttributeDefinition for this field.
     *
     * @return attribute definition
     */
    public AttributeDefinition getAttributeDefinition() {
        return new AttributeDefinition(name, type);
    }
}