/*
 * Two threads sharing one bank account.
 *
 * The deposit thread adds a random 1..10 once per second. The
 * withdraw thread tries to take out a random 1..20 and, when the
 * balance is too small, waits on the lock object until a deposit
 * wakes it up.
 *
 * Both threads loop forever. Let the program run for a few
 * seconds, watch the two clerks trade places, then stop it with
 * Ctrl+C.
 *
 * The C version with pthread_cond_t is in thread_cooperation.c
 * next to this file. It has the same structure; only the API
 * names and the manual unlock are different.
 */
import java.util.Random;

public class ThreadCooperation {

    static int balance = 0;
    static final Object lock = new Object();
    static final Random rand = new Random();

    public static void main(String[] args) {
        // Two infinite workers. We don't bother joining because
        // neither of them ever finishes.
        new Thread(ThreadCooperation::depositLoop).start();
        new Thread(ThreadCooperation::withdrawLoop).start();
    }

    static void depositLoop() {
        while (true) {
            synchronized (lock) {
                int amount = rand.nextInt(10) + 1;
                balance += amount;
                System.out.println("Add " + amount + ". Balance now is " + balance);

                // Ring the bell. Wakes up anyone currently in wait()
                // on this same lock object.
                lock.notifyAll();
            }
            try { Thread.sleep(1000); } catch (InterruptedException e) {}
        }
    }

    static void withdrawLoop() {
        while (true) {
            synchronized (lock) {
                int amount = rand.nextInt(20) + 1;

                // while, not if. See the README for the reason.
                while (balance < amount) {
                    System.out.println("\t\t\tCannot withdraw " + amount + ", waiting...");
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        return;
                    }
                }

                balance -= amount;
                System.out.println("\t\t\tWithdrew " + amount + ". Balance now is " + balance);
            }
        }
    }
}
