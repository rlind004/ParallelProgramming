/*
 * CyclicBarrier with a bit more to do.
 *
 * Script 2 showed the barrier on its own: a few threads, each one
 * calls await(), nobody moves until everyone has arrived. This file
 * adds the next layer on top of that simple idea.
 *
 * Picture a group assignment with five students. Each one works on
 * their own section. Nobody is allowed to hand in the project until
 * every single student is done. The moment the last one finishes,
 * one more thing happens automatically: somebody collects all five
 * sections and staples them into a single report. Only then do the
 * students go home.
 *
 * That "automatic thing after everyone arrives" is what Java calls
 * a barrier action. You pass it as a Runnable when you build the
 * CyclicBarrier and it runs on whichever thread happened to arrive
 * last. No extra thread, no separate signalling, no boilerplate.
 *
 * The "cyclic" part of the name: once all five threads cross the
 * barrier it resets itself and you can reuse it for the next round.
 * This example only goes through one round, but the door is open.
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CyclicBarrierExample {

    private CyclicBarrier cyclicBarrier;

    /*
     * Shared list of partial results. Each worker writes its own
     * entry so the writes never collide, but we still wrap the list
     * with synchronizedList so any later reader doesn't see a
     * half-baked internal state.
     */
    private List<List<Integer>> partialResults = Collections.synchronizedList(new ArrayList<>());

    private Random random = new Random();
    private int NUM_PARTIAL_RESULTS;
    private int NUM_WORKERS;

    /*
     * One worker thread. Picks a few random numbers, drops them into
     * the shared list, and then parks itself at the barrier.
     *
     * The important line is cyclicBarrier.await(). That's where the
     * worker waits until all of its siblings have caught up.
     */
    class NumberCruncherThread implements Runnable {

        @Override
        public void run() {
            String thisThreadName = Thread.currentThread().getName();
            List<Integer> partialResult = new ArrayList<>();

            // Do the work: a handful of random numbers.
            for (int i = 0; i < NUM_PARTIAL_RESULTS; i++) {
                Integer num = random.nextInt(10);
                System.out.println(thisThreadName + ": Crunching some numbers! Current result - " + num);
                partialResult.add(num);
            }

            // Drop our partial into the shared list.
            partialResults.add(partialResult);

            try {
                // Wait for the others. If I turn out to be the LAST
                // one to arrive, the AggregatorThread runs first and
                // then await() returns for everybody at roughly the
                // same time.
                System.out.println(thisThreadName + " waiting for others to reach barrier (rendez-vois).");
                cyclicBarrier.await();
            } catch (InterruptedException e) {
                // Woken up while waiting at the barrier. In this
                // example there's nothing useful to do so we just let
                // the thread end.
            } catch (BrokenBarrierException e) {
                // Some other thread's problem broke the barrier for
                // everyone. Real code would decide whether to retry
                // or bail out; here we just let it slide.
            }

            System.out.println(thisThreadName + " finished waiting the barrier.");
        }
    }

    /*
     * The "automatic step" that runs once all workers have arrived.
     * Nothing starts this Runnable by hand. We hand it to the
     * CyclicBarrier constructor and the barrier calls run() on
     * whichever worker happened to arrive last, BEFORE letting any
     * of them past await().
     *
     * By the time this runs, every worker has already written its
     * own entry into partialResults, so it's safe to walk the whole
     * list and total the numbers up.
     */
    class AggregatorThread implements Runnable {

        @Override
        public void run() {

            String thisThreadName = Thread.currentThread().getName();

            System.out.println(
                    thisThreadName + ": Computing sum of " + NUM_WORKERS
                            + " workers, having " + NUM_PARTIAL_RESULTS + " results each.");
            int sum = 0;

            // Walk through every worker's partial result and add up
            // the numbers one by one.
            for (List<Integer> threadResult : partialResults) {
                System.out.print("Adding ");
                for (Integer partialResult : threadResult) {
                    System.out.print(partialResult + " ");
                    sum += partialResult;
                }
                System.out.println();
            }
            System.out.println(thisThreadName + ": current result = " + sum);
        }
    }

    /*
     * Build the barrier and kick off the workers.
     *
     * CyclicBarrier takes two arguments: how many threads must
     * arrive, and an optional Runnable to run once they do. The
     * Runnable here is AggregatorThread.
     */
    public void runSimulation(int numWorkers, int numberOfPartialResults) {
        NUM_WORKERS = numWorkers;
        NUM_PARTIAL_RESULTS = numberOfPartialResults;

        cyclicBarrier = new CyclicBarrier(NUM_WORKERS, new AggregatorThread());

        System.out.println("Spawning " + NUM_WORKERS + " worker threads to compute "
                + NUM_PARTIAL_RESULTS + " partial results each");

        for (int i = 0; i < NUM_WORKERS; i++) {
            Thread worker = new Thread(new NumberCruncherThread());
            worker.setName("Thread " + i);
            worker.start();
        }
    }

    public static void main(String[] args) {
        CyclicBarrierExample demo = new CyclicBarrierExample();
        // Five workers, four numbers each, twenty numbers to sum at the end.
        demo.runSimulation(5, 4);
    }
}
