awsblocks tinyqueue
===================

The tinyqueue library stores a time-based queue in DynamoDB. Be aware that all data for a single queue will be sent to a 
single partition in DynamoDB. This is not ideal for large volumes or high speeds of data flow. This library is really only 
meant for small amounts of data.

### An example

We're building an app where we store users' chirps. A chirp is a single onomatopoeia that explains how you feel at the moment. 
Here's a sample program that generates some chirps, prints them, and then ages off some old ones.

```java
public class Chirp {
    public static void main(String[] args) {
        String region = "us-east-1";
        AmazonDynamoDB dynamo = AmazonDynamoDBClientBuilder.standard().withRegion(region).build();
        String table = "tinyqueue_table";
    
        // First make our table to back TinyQueue
        int readUnitsPerSec = 4;
        int writeUnitsPerSec = 1; // write units are sorta expensive
        TinyQueueTable tqt = new TinyQueueTable(dynamo, table);
        tqt.createTableIfNotExists(readUnitsPerSec, writeUnitsPerSec);
        
        // Now make a new queue and start using it
        try (TinyQueue queue = new TinyQueue(table, region, "jeffs_chirps")) {
            queue.append("Boom!"); // appends to the end of the queue
            queue.insert("Pow!", 0L); // you can insert at a specified timestamp too
            queue.insert("Bam!", 1L);
            
            queue.fetch(10L); // fetch all the chirps starting at the timestamp 10 until now
            
            queue.delete(1L, 10L); // delete all the chirps between time (1L, 10L] (end is exclusive)
        }
    }
}
```
