/*
 * This example is an extension to the previous week example AccountWithoutSync,
 * but ensuring synchronization, so the final balance will always be 100.
 * Here synchronization is achieved using Lock objects.
 */

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class AccountWithSync4 {

    private static Account account = new Account();

    public static void main(String[] args) {
        Thread threads[] = new Thread[100];

        for (int i = 0; i < 100; i++) {
            threads[i] = new Thread(new AddAPennyTask());
        }

        for (int i = 0; i < 100; i++) {
            threads[i].start();
        }

        for(int i=0; i < 100; i++){
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

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
        // ReentrantLock is an alternative to the synchronized keyword.
        // It does the same thing (mutual exclusion) but gives you more control.
        // "Reentrant" means the same thread can acquire the lock multiple times
        // without deadlocking itself.
        Lock lock = new ReentrantLock();
        private int balance = 0;

        public int getBalance() {
            return balance;
        }

        // With ReentrantLock, you manually call lock() and unlock() to define
        // the critical section. This is more explicit than synchronized.
        //
        // IMPORTANT: you should always put unlock() in a finally block to make
        // sure the lock gets released even if an exception happens.
        // (In this simple example it's written without finally for brevity,
        //  but Sync5 and the Week 3 examples use the proper pattern.)
        public void deposit(int amount) throws InterruptedException{
            lock.lock();     // Acquire the lock -- other threads must wait
            int newBalance = balance + amount;
            balance = newBalance;
            lock.unlock();   // Release the lock -- next waiting thread can proceed
        }
    }
}
