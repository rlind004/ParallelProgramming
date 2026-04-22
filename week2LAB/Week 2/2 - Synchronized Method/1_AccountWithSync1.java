/*
 * This example is an extension to the previous week example AccountWithoutSync,
 * but ensuring synchronization, so the final balance will always be 100.
 * Here synchronization is achieved using the synchronized keyword in the
 * header of the deposit method.
 */

public class AccountWithSync1 {

    private static Account account = new Account();

    public static void main(String[] args) {
        Thread threads[] = new Thread[100];

        // Same setup: 100 threads, each deposits 1
        for (int i = 0; i < 100; i++) {
            threads[i] = new Thread(new AddAPennyTask());
        }

        for (int i = 0; i < 100; i++) {
            threads[i].start();
        }

        // Wait for all threads to finish
        for(int i=0; i < 100; i++){
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Now the balance is ALWAYS 100, because deposit() is synchronized.
        System.out.println("What is the final balance? " + account.getBalance());
    }

    private static class AddAPennyTask implements Runnable{
        public void run(){
            try {
                account.deposit(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static class Account {
        private int balance = 0;

        public int getBalance() {
            return balance;
        }

        // The 'synchronized' keyword on the method means: before a thread can
        // enter this method, it must acquire the lock on 'this' object (the Account).
        // If another thread already holds that lock (i.e., is inside this method),
        // the current thread has to wait until the lock is released.
        //
        // This guarantees that only ONE thread executes deposit() at a time,
        // so the read-modify-write sequence cannot be interrupted by another thread.
        // The race condition from the previous example is eliminated.
        public synchronized void deposit(int amount) throws InterruptedException {
            int newBalance = balance + amount;
            // Even if we add a sleep here, the result is still correct because
            // no other thread can enter this method while we hold the lock.
            // But it would slow things down a lot since threads wait one by one.
            //Thread.sleep(200);
            balance = newBalance;
        }
    }
}
