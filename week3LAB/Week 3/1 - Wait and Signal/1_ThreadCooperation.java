/*
 * In this example, two threads (DepositTask and WithdrawTask) modify
 * together the balance of an account not only in a synchronized way,
 * but also in cooperation. So the WithdrawTask will not withdraw if
 * amount is larger than the current balance. In that case it will wait
 * on the condition that a new deposit must be done.
 */

import java.util.concurrent.*;
import java.util.concurrent.locks.*;

public class ThreadCooperation {

    private static Account account = new Account();

    // Colors to distinguish deposit (blue) and withdraw (red) output
    public final static String DepositColor = ThreadColors.ANSI_BLUE;
    public final static String WithdrawColor = ThreadColors.ANSI_RED;

    public static void main(String[] args) {
        System.out.println("Thread 1\t\tThread 2\t\tBalance");

        // ExecutorService manages a pool of threads for us.
        // newFixedThreadPool(2) creates a pool with 2 threads.
        // We submit two tasks: one deposits money, one withdraws money.
        // Both run indefinitely (while(true) loops inside).
        ExecutorService executor = Executors.newFixedThreadPool(2);
        executor.execute(new DepositTask());
        executor.execute(new WithdrawTask());
        executor.shutdown(); // No new tasks accepted, but existing ones keep running
    }

    // This task keeps depositing random amounts (1-10) into the account.
    // After each deposit it sleeps for 1 second to slow things down
    // so we can see the output clearly.
    public static class DepositTask implements Runnable {

        @Override
        public void run() {
            try {
                while (true) {
                    account.deposit((int) (Math.random() * 10) + 1);
                    Thread.sleep(1000); // Wait a bit before the next deposit
                }
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }

    // This task keeps trying to withdraw random amounts (1-20).
    // If there's not enough money, it will WAIT (not fail) until a deposit happens.
    public static class WithdrawTask implements Runnable {

        @Override
        public void run() {
            while (true) {
                account.withdraw((int) (Math.random() * 20) + 1);
            }
        }
    }

    private static class Account {
        // ReentrantLock for mutual exclusion (same concept as Week 2's Sync4/5)
        private static Lock lock = new ReentrantLock();

        // A Condition is created FROM the lock. It lets threads wait for
        // a specific thing to happen. Here, "newDeposit" means:
        // "I'm waiting because I need a new deposit to occur."
        // A thread that calls newDeposit.await() goes to sleep and releases the lock.
        // A thread that calls newDeposit.signalAll() wakes up all waiting threads.
        private static Condition newDeposit = lock.newCondition();

        private int balance = 0;

        public int getBalance() {
            return balance;
        }

        public void withdraw(int amount) {
            lock.lock(); // Must hold the lock before checking/modifying balance
            try {
                // WHILE loop, not IF. This is important.
                // After being woken up by signalAll(), we re-check the condition.
                // Maybe the deposit wasn't enough, or another thread withdrew first.
                // We keep waiting until there's truly enough money.
                while (balance < amount) {
                    System.out.println(WithdrawColor + "\t\t\tWait for a deposit (cannot withdraw " + amount + " )");

                    // await() does two things at once:
                    // 1. Releases the lock (so the deposit thread can acquire it)
                    // 2. Puts this thread to sleep until someone calls signalAll()
                    // When woken up, it re-acquires the lock automatically before continuing.
                    newDeposit.await();
                }

                // If we get here, balance >= amount, so the withdrawal is safe
                balance -= amount;
                System.out.println(WithdrawColor + "\t\t\tWithdraw " + amount + "\t\t" + getBalance());
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                lock.unlock(); // Always release the lock, even if an exception occurs
            }
        }

        public void deposit(int amount) {
            lock.lock();
            try {
                balance += amount;
                System.out.println(DepositColor + "Deposit " + amount + "\t\t\t\t\t" + getBalance());

                // After depositing, we signal ALL threads waiting on newDeposit.
                // This wakes up the withdraw thread (if it's waiting) so it can
                // re-check whether there's now enough money.
                // signalAll() wakes everyone; signal() would wake just one.
                newDeposit.signalAll();
            } finally {
                lock.unlock();
            }
        }
    }
}
