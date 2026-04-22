/*
 * A semaphore is what you reach for when "one thread at a time" is
 * too strict.
 *
 * Picture a small bank branch with four ATMs. Six customers walk in
 * at the same moment. Four of them step up to the machines straight
 * away. The other two stand in line until one of the first four
 * finishes and walks off. That's basically a semaphore, only with
 * threads instead of customers and a counter instead of a queue.
 *
 *   acquire() takes one permit. If there are no permits left, you
 *             wait here until someone gives one back.
 *   release() hands a permit back. Whoever is waiting can go.
 *
 * Compared to synchronized, which is "one thread in the room at a
 * time", a Semaphore(4) is "up to four threads in the room at a
 * time". It's the right tool when you own N copies of some resource
 * and you want all N used in parallel, but no more.
 *
 * Below: six threads named A through F. Four of them get in right
 * away. E and F park on acquire() and don't start working until one
 * of the first four calls release(). Watch availablePermits count
 * down from 4 to 0 and climb back up again.
 */

package javathreads;

import java.util.concurrent.Semaphore;  // the one new class this week

public class SemaphoreTest {

    // Four ATMs, so four permits.
    static Semaphore semaphore = new Semaphore(4);

    /*
     * Each thread is one customer. They announce themselves, try to
     * grab a permit (blocking if none are free), do five "operations"
     * with a one-second pause between each, then release the permit
     * so the next person can take their turn.
     */
    static class MyATMThread extends Thread {

        String name = "";

        MyATMThread(String name) {
            this.name = name;
        }

        public void run() {

            try {

                // Print how many ATMs are still free BEFORE we try to
                // take one. If this line says 0, the next line is
                // where we're going to wait in the queue.
                System.out.println(name + " : acquiring lock...");
                System.out.println(name + " : available Semaphore permits now: "
                        + semaphore.availablePermits());

                // Take a permit. If the counter is already at zero
                // this blocks until someone else calls release().
                semaphore.acquire();
                System.out.println(name + " : got the permit!");

                try {

                    // Five fake ATM operations, one second each. While
                    // we're inside this loop we hold one of the four
                    // permits, so at most three other customers can be
                    // operating their own ATMs at the same time.
                    for (int i = 1; i <= 5; i++) {

                        System.out.println(name + " : is performing operation " + i
                                + ", available Semaphore permits : "
                                + semaphore.availablePermits());

                        Thread.sleep(1000);

                    }

                } finally {

                    // release() sits in a finally block on purpose. If
                    // something blew up inside the loop we'd still
                    // want the permit to go back. Forgetting a release
                    // is like a customer walking out of the ATM room
                    // without ever stepping away from the machine: no
                    // one else will ever get to use it again.
                    System.out.println(name + " : releasing lock...");
                    semaphore.release();
                    System.out.println(name + " : available Semaphore permits now: "
                            + semaphore.availablePermits());

                }

            } catch (InterruptedException e) {

                e.printStackTrace();

            }

        }

    }

    public static void main(String[] args) {

        System.out.println("Total available Semaphore permits : "
                + semaphore.availablePermits());

        // Six customers all walk in at once.
        MyATMThread t1 = new MyATMThread("A");
        t1.start();

        MyATMThread t2 = new MyATMThread("B");
        t2.start();

        MyATMThread t3 = new MyATMThread("C");
        t3.start();

        MyATMThread t4 = new MyATMThread("D");
        t4.start();

        MyATMThread t5 = new MyATMThread("E");
        t5.start();

        MyATMThread t6 = new MyATMThread("F");
        t6.start();

    }
}
