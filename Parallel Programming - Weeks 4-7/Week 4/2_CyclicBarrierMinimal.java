/*
 * A tiny CyclicBarrier example, meant to come before the full one.
 *
 * The full script next door throws a lot at you at once: inner classes,
 * a shared list of partial results, an aggregator running as the
 * barrier action. Before any of that, it's worth seeing the barrier
 * in isolation.
 *
 * Three threads do a little bit of work on their own. Each one calls
 * await() when it's done. None of them gets past that line until all
 * three have arrived. That's it. No shared list, no aggregation.
 *
 * Compare with join() from Week 1. join() makes the MAIN thread wait
 * for a worker to end. A CyclicBarrier makes the workers wait for each
 * OTHER while main has long moved on.
 */

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class CyclicBarrierMinimal {

    /* The "3" means three parties have to arrive before any of them is
       allowed through. We're not passing a barrier action this time
       (the second constructor argument) because we don't need one yet. */
    static CyclicBarrier barrier = new CyclicBarrier(3);

    static class Worker implements Runnable {
        @Override
        public void run() {
            String me = Thread.currentThread().getName();

            try {
                /* Pretend to do some real work for a random amount of
                   time, so the threads don't all reach the barrier at
                   the same instant. Watching the timing is half the
                   point of this example. */
                System.out.println(me + " is working...");
                Thread.sleep((long) (Math.random() * 2000));

                System.out.println(me + " reached the barrier, waiting for the others");
                barrier.await();

                /* Only reachable once every worker has called await().
                   Run the program a few times and you'll see that these
                   three "passed" lines always come out in a cluster,
                   never staggered like the "is working" lines. */
                System.out.println(me + " passed the barrier, continuing");
            } catch (InterruptedException e) {
                /* Woken up during sleep or during the wait at the
                   barrier. Nothing meaningful to do here, so we just
                   let the thread end. */
            } catch (BrokenBarrierException e) {
                /* Thrown when some other thread at this barrier was
                   interrupted or timed out, which "breaks" the barrier
                   for everyone currently parked on it. Real code would
                   typically retry the whole group or abort. */
            }
        }
    }

    public static void main(String[] args) {
        new Thread(new Worker(), "Worker-1").start();
        new Thread(new Worker(), "Worker-2").start();
        new Thread(new Worker(), "Worker-3").start();
    }
}
