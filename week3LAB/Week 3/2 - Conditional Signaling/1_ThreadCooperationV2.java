/*
 * In this example, two threads (DepositTask and WithdrawTask) modify
 * together the balance of an account not only in a synchronized way,
 * but also in cooperation. So the WithdrawTask will not withdraw if
 * amount is larger than the current balance. Now withdraw
 * thread is signalled only when amount is collected.
 */
package Version1;

import java.util.concurrent.*;
import java.util.concurrent.locks.*;

public class ThreadCooperationV2 {

    private static Account account = new Account();
    public final static String DepositColor = ThreadColors.ANSI_BLUE;
    public final static String WithdrawColor = ThreadColors.ANSI_RED;

    public static void main(String[] args) {
        System.out.println("Thread 1\t\tThread 2\t\tBalance");
        ExecutorService executor = Executors.newFixedThreadPool(2);
        executor.execute(new DepositTask());
        executor.execute(new WithdrawTask());
        executor.shutdown();
    }

    public static class DepositTask implements Runnable {

        @Override
        public void run() {
            try {
                while (true) {
                    account.deposit((int) (Math.random() * 10) + 1);
                    Thread.sleep(1000);
                }
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static class WithdrawTask implements Runnable {

        @Override
        public void run() {
            while (true) {
                account.withdraw((int) (Math.random() * 20) + 1);
            }
        }
    }

    private static class Account {

        private static Lock lock = new ReentrantLock();
        private static Condition newDeposit = lock.newCondition();

        private int balance = 0;

        // This field tracks how much the withdraw thread is waiting for.
        // The deposit method checks this to decide whether to signal or not.
        private int withdrawRequest;

        public int getBalance() {
            return balance;
        }

        public void withdraw(int amount) {
            lock.lock();
            try {
                // Compared to V1, this uses IF instead of WHILE.
                // It's simpler but slightly less safe -- in V1, the while loop
                // re-checks after waking up. Here we assume the signal is only
                // sent when there's enough money (which is what deposit() does below).
                if (balance < amount) {
                    // Store how much we need, so deposit() knows when to wake us up
                    withdrawRequest = amount;
                    System.out.println(WithdrawColor + "\t\t\tWait for a deposit (cannot withdraw " + amount + " )");
                    newDeposit.await();
                }

                balance -= amount;
                System.out.println(WithdrawColor + "\t\t\tWithdraw " + amount + "\t\t" + getBalance());
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                lock.unlock();
            }
        }

        public void deposit(int amount) {
            lock.lock();
            try {
                balance += amount;
                System.out.println(DepositColor + "Deposit " + amount + "\t\t\t\t\t" + getBalance());

                // KEY IMPROVEMENT over V1:
                // In V1, we always called signalAll() after every deposit, even
                // if the balance was still too low for the withdrawal.
                // Here, we only signal when the balance is actually enough to
                // cover what the withdraw thread asked for.
                // This avoids unnecessary wake-ups where the withdraw thread
                // wakes up, checks the balance, finds it's still not enough,
                // and goes back to sleep.
                if (balance >= withdrawRequest) {
                    newDeposit.signalAll();
                }
            } finally {
                lock.unlock();
            }
        }
    }
}
