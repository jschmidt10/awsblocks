package com.github.jschmidt10.awsblocks.tinyqueue;

import com.amazonaws.regions.Regions;

public class Trial {
    public static void main(String[] args) {
        try (TinyQueue queue = new TinyQueue("tinyqueue_test", Regions.US_EAST_1.getName(), "queue1")) {
            queue.delete(1514041797177L);

            queue.append("msg31".getBytes());
            queue.append("msg32".getBytes());
            queue.append("msg33".getBytes());

            queue
                    .fetch(0L)
                    .forEachRemaining(entry -> {
                        System.out.println(entry.getQueueName() + ", " + entry.getTimestamp() + ", " + new String(entry.getMessage()));
                    });
        }
    }
}
