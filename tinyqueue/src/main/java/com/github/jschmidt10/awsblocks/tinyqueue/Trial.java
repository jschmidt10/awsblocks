package com.github.jschmidt10.awsblocks.tinyqueue;

import com.amazonaws.regions.Regions;

public class Trial {
    public static void main(String[] args) {
        try (TinyQueue queue = new TinyQueue("tinyqueue_test", Regions.US_EAST_1.getName(), "queue1")) {

            queue.insert("msg100".getBytes(), 0L);
            queue.insert("msg101".getBytes(), 1L);
            queue.insert("msg102".getBytes(), 2L);
            queue.insert("msg103".getBytes(), 3L);
            queue.insert("msg104".getBytes(), 4L);
            queue.insert("msg105".getBytes(), 5L);
            queue.insert("msg106".getBytes(), 6L);
            queue.insert("msg107".getBytes(), 7L);
            queue.insert("msg108".getBytes(), 8L);
            queue.insert("msg109".getBytes(), 9L);

            queue.delete(2L, 6L);

            queue.fetch(0L)
                    .forEachRemaining(entry -> {
                        System.out.println(entry.getQueueName() + ", " + entry.getTimestamp() + ", " + new String(entry.getMessage()));
                    });

            queue.delete(0L, System.currentTimeMillis());
        }
    }
}
