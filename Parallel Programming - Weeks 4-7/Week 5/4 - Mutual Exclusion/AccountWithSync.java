/*
 * Same program as AccountWithoutSync.java, but deposit() is now
 * synchronized. Only one thread can be inside the method at a
 * time, so the read-then-write pair no longer has a race. The
 * final balance is always exactly 100.
 *
 * See account_with_sync.c for the C version using pthread_mutex.
 */
public class AccountWithSync {

    static int balance = 0;

    // "synchronized" on a static method locks on the class object
    // itself, so every thread agrees on the same lock without us
    // having to create one explicitly.
    static synchronized void deposit() {
        int newBalance = balance + 1;
        balance = newBalance;
    }

    public static void main(String[] args) throws InterruptedException {
        Thread[] threads = new Thread[100];

        for (int i = 0; i < 100; i++) {
            threads[i] = new Thread(AccountWithSync::deposit);
        }

        for (int i = 0; i < 100; i++) {
            threads[i].start();
        }

        for (int i = 0; i < 100; i++) {
            threads[i].join();
        }

        // Always 100 now, no matter how many times you run it.
        System.out.println("Final balance: " + balance);
    }
}
