/*
 * Producer/consumer running on a thread pool.
 *
 * Script 4 showed the same program with raw threads, started by hand
 * with new Thread(...).start(). This file changes ONE thing: instead
 * of creating the threads ourselves, we hand the two Runnables to
 * an ExecutorService and let it run them on its own pool of workers.
 *
 * Why bother? In a real program you rarely have exactly one producer
 * and one consumer running forever. You have dozens, hundreds, maybe
 * thousands of tasks showing up over time. Creating a brand-new
 * Thread for each one and then throwing it away costs real memory
 * and real time. A pool keeps a small team of threads alive and
 * reuses them over and over.
 *
 * Think of it like a taxi dispatcher. The old way was "every rider
 * hires a driver from scratch, one trip, and then we let them go".
 * The pool way is "the company keeps N drivers on duty and whoever
 * is free takes the next call". Same riders, same destinations, way
 * less overhead.
 *
 * Everything else in this file is exactly what you already saw in
 * script 4: an ArrayBlockingQueue with capacity 3, put() blocking
 * when full, take() blocking when empty. The pool is only changing
 * HOW the two tasks get run, not WHAT they do.
 */

import java.util.concurrent.*;  // ArrayBlockingQueue, ExecutorService, Executors

public class ProducerConsumerUsingBlockingQueue {

    // Same conveyor-belt buffer as in script 4. Capacity 3.
    private static ArrayBlockingQueue<Integer> buffer = new ArrayBlockingQueue<>(3);

    public static void main(String[] args) {
        /*
         * A pool with two threads. In this tiny example we submit
         * exactly two tasks, so each task happens to land on its
         * own thread. In a busier program the same pool would cycle
         * its two workers through many more tasks over time.
         */
        ExecutorService executor = Executors.newFixedThreadPool(2);

        // Submit the two Runnables. They start running immediately.
        executor.execute(new ProducerTask());
        executor.execute(new ConsumerTask());

        /*
         * shutdown() means "don't accept any new tasks", NOT "stop
         * the tasks that are already running". Since both of our
         * tasks loop forever, the pool never actually goes idle and
         * the program runs until you press Ctrl+C.
         */
        executor.shutdown();
    }

    /*
     * Producer. Pumps out consecutive integers and drops them onto
     * the buffer. The only interesting line is buffer.put(i), which
     * blocks on its own whenever the buffer is full. No while-loop
     * around it, no notify, no synchronized block. The queue is
     * handling all of that behind the scenes.
     */
    private static class ProducerTask implements Runnable {
        public void run() {
            try {
                int i = 1;
                while (true) {
                    System.out.println("Producer writes " + i);
                    buffer.put(i++);
                    Thread.sleep((int)(Math.random() * 1000));
                }
            }
            catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }

    /*
     * Consumer. Same story from the other side: take() blocks until
     * there's something in the buffer, so we never accidentally read
     * from an empty one.
     *
     * The triple tab in front of the print is just to push the
     * consumer's output to the right in the terminal, so you can
     * tell the two threads apart at a glance.
     */
    private static class ConsumerTask implements Runnable {
        public void run() {
            try {
                while (true) {
                    System.out.println("\t\t\tConsumer reads " + buffer.take());
                    Thread.sleep((int)(Math.random() * 1000));
                }
            }
            catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }
}
