/*
 * The classic race condition. Start 100 threads, each one adds 1
 * to a shared balance. Expected final balance is 100. Actual
 * result is usually less, because "read the balance, compute a
 * new value, write it back" is three separate steps and another
 * thread can slip in between any two of them.
 *
 * Run this a few times and watch the final number bounce around.
 * That variability IS the bug. See account_without_sync.c for
 * the pthreads version of the exact same broken program.
 */
public class AccountWithoutSync {

    // Shared state. Every thread reads and writes this.
    static int balance = 0;

    // Intentionally broken. There is no lock around the
    // read-then-write. Two threads can both read the same value,
    // both compute newBalance + 1, and both write back, which
    // loses one of the two increments.
    static void deposit() {
        int newBalance = balance + 1;
        balance = newBalance;
    }

    public static void main(String[] args) throws InterruptedException {
        Thread[] threads = new Thread[100];

        for (int i = 0; i < 100; i++) {
            threads[i] = new Thread(AccountWithoutSync::deposit);
        }

        for (int i = 0; i < 100; i++) {
            threads[i].start();
        }

        for (int i = 0; i < 100; i++) {
            threads[i].join();
        }

        // Expected: 100. Actual: usually a bit less.
        System.out.println("Final balance: " + balance);
    }
}
