/*
 * Producer/consumer with ArrayBlockingQueue, before we add the thread
 * pool on top.
 *
 * Week 3's producer/consumer was built out of synchronized + wait() +
 * notify(). It worked, but it was fiddly: you had to remember the
 * while-loop around wait, you had to notify in the right place, you
 * could deadlock if you got any of it wrong.
 *
 * ArrayBlockingQueue replaces all of that. put() blocks automatically
 * when the queue is full, take() blocks automatically when the queue
 * is empty, and the locking happens behind the scenes. That's the one
 * idea this script is about.
 *
 * The threads are started the same way as in Week 2 and Week 3, with
 * plain new Thread(...).start(), so the queue is the only new thing
 * you have to absorb. The next script swaps those raw threads for an
 * ExecutorService, but that's a separate story.
 */

import java.util.concurrent.ArrayBlockingQueue;

public class ProducerConsumerBlockingQueueNoExecutor {

    /* Capacity 3: at most three integers can sit in the buffer at
       once. Try changing it to 1 or to 10 and watch how the rhythm of
       the output changes. A smaller buffer forces the two threads to
       take turns; a bigger one lets the producer race ahead. */
    private static ArrayBlockingQueue<Integer> buffer = new ArrayBlockingQueue<>(3);

    public static void main(String[] args) {
        /* Same launching style as the earlier weeks. No pool, no
           executor, nothing clever. Just two threads. */
        new Thread(new ProducerTask()).start();
        new Thread(new ConsumerTask()).start();
    }

    /*
     * Producer. Generates consecutive integers forever and drops them
     * into the buffer. The only interesting line is buffer.put(i),
     * which blocks all on its own whenever the buffer is full. No
     * while-loop, no notify, no synchronized block.
     */
    private static class ProducerTask implements Runnable {
        public void run() {
            try {
                int i = 1;
                while (true) {
                    System.out.println("Producer writes " + i);
                    buffer.put(i++);
                    Thread.sleep((int) (Math.random() * 1000));
                }
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }

    /*
     * Consumer. Same story on the other side. take() blocks until
     * something is actually in the buffer, so we never accidentally
     * read from an empty one.
     *
     * The triple tab in the print is just to push the consumer output
     * to the right in the terminal so you can tell the two threads
     * apart at a glance.
     */
    private static class ConsumerTask implements Runnable {
        public void run() {
            try {
                while (true) {
                    System.out.println("\t\t\tConsumer reads " + buffer.take());
                    Thread.sleep((int) (Math.random() * 1000));
                }
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }
}
