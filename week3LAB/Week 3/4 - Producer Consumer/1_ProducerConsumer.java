/*
 * In this example two threads (ProducerTask and ConsumerTask) cooperate adding
 * and removing data from a shared buffer. When the buffer is full the ProducerTask
 * has to wait and when the buffer is empty the ConsumerTask has to wait.
 */

import java.util.concurrent.*;
import java.util.concurrent.locks.*;

public class ProducerConsumer {
    private static Buffer buffer = new Buffer();

    public static void main(String[] args) {
        // Two threads: one produces data, one consumes it.
        // They share a bounded buffer (max 4 items).
        ExecutorService executor = Executors.newFixedThreadPool(2);
        executor.execute(new ProducerTask());
        executor.execute(new ConsumerTask());
        executor.shutdown();
    }

    // The producer keeps generating integers (1, 2, 3, ...) and writing them
    // to the buffer. If the buffer is full, it has to wait until the consumer
    // removes something.
    private static class ProducerTask implements Runnable {
        public void run() {
            try {
                int i = 1;
                while (true) {
                    System.out.println("Producer writes " + i);
                    buffer.write(i++); // Write the value, then increment for next time
                    // Random sleep (0-2 seconds) to simulate variable production speed
                    Thread.sleep((int)(Math.random() * 2000));
                }
            }
            catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }

    // The consumer keeps reading values from the buffer.
    // If the buffer is empty, it has to wait until the producer adds something.
    private static class ConsumerTask implements Runnable {
        public void run() {
            try {
                while (true) {
                    System.out.println("\t\t\tConsumer reads " + buffer.read());
                    // Random sleep to simulate variable consumption speed
                    Thread.sleep((int)(Math.random() * 2000));
                }
            }
            catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }

    private static class Buffer {
        private static final int CAPACITY = 4; // The buffer can hold at most 4 items

        // LinkedList acts as our queue: items are added at the end, removed from the front.
        private java.util.LinkedList<Integer> queue =
                new java.util.LinkedList<>();

        private static Lock lock = new ReentrantLock();

        // Two conditions for the two situations where a thread needs to wait:
        // notEmpty: the consumer waits on this when the buffer has nothing to read
        // notFull: the producer waits on this when the buffer is at capacity
        private static Condition notEmpty = lock.newCondition();
        private static Condition notFull = lock.newCondition();

        // Called by the producer to add a value to the buffer.
        public void write(int value) {
            lock.lock();
            try {
                // If the buffer is full, the producer can't add more.
                // It waits on the "notFull" condition until the consumer removes something.
                // Using WHILE (not IF) because after being woken up, another thread
                // might have filled the buffer again before we got the lock back.
                while (queue.size() == CAPACITY) {
                    System.out.println("Wait for notFull condition");
                    notFull.await(); // Release lock and sleep until signaled
                }

                // There's room now, so add the value to the end of the queue
                queue.offer(value);

                // Signal the consumer: "hey, the buffer is no longer empty,
                // there's at least one item you can read now."
                notEmpty.signal();
            }
            catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            finally {
                lock.unlock();
            }
        }

        // Called by the consumer to read and remove a value from the buffer.
        public int read() {
            int value = 0;
            lock.lock();
            try {
                // If the buffer is empty, there's nothing to read.
                // Wait on "notEmpty" until the producer adds something.
                while (queue.isEmpty()) {
                    System.out.println("\t\t\tWait for notEmpty condition");
                    notEmpty.await(); // Release lock and sleep until signaled
                }

                // There's at least one item, so remove it from the front of the queue
                value = queue.remove();

                // Signal the producer: "hey, I just took something out,
                // so the buffer is no longer full. You can write again."
                notFull.signal();
            }
            catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            finally {
                lock.unlock();
                return value;
            }
        }
    }
}
