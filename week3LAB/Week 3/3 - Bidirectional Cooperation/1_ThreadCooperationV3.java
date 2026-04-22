/*
 * In this example, two threads (DepositTask and WithdrawTask) modify
 * together the balance of an account not only in a synchronized way,
 * but also in cooperation. So the WithdrawTask will not withdraw if
 * amount is larger than the current balance and DepositTask will wait
 * if the amount would make the balance greater than 100.
 */
package Version1;

import java.util.concurrent.*;
import java.util.concurrent.locks.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ThreadCooperationV3 {

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

    // In this version, deposits can be larger (1-50) and there's a sleep on both sides.
    public static class DepositTask implements Runnable {

        @Override
        public void run() {
            try {
                while (true) {
                    account.deposit((int) (Math.random() * 50) + 1);
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
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(ThreadCooperationV3.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    private static class Account {

        private static Lock lock = new ReentrantLock();

        // TWO conditions on the same lock. This is the big addition in V3.
        // newDeposit: the withdraw thread waits on this (it needs a deposit to happen)
        // newWithdraw: the deposit thread waits on this (it needs a withdrawal to free up space)
        private static Condition newDeposit = lock.newCondition();
        private static Condition newWithdraw = lock.newCondition();

        private int balance = 0;
        private int withdrawRequest;  // How much the withdraw thread needs
        private int depositRequest;   // How much the deposit thread wants to add

        public int getBalance() {
            return balance;
        }

        public void withdraw(int amount) {
            lock.lock();
            try {
                if (balance < amount) {
                    withdrawRequest = amount;
                    System.out.println(WithdrawColor + "\t\t\tWait for a deposit (cannot withdraw " + amount + " )");
                    // Wait until the deposit thread signals that there's enough money
                    newDeposit.await();
                }

                balance -= amount;

                // After withdrawing, check if there's now room for the deposit thread.
                // If the deposit thread was waiting because balance + depositAmount > 100,
                // now that we've withdrawn some money, there might be enough room.
                if(balance + depositRequest <= 100){
                    newWithdraw.signalAll(); // Wake up the deposit thread
                }

                System.out.println(WithdrawColor + "\t\t\tWithdraw " + amount + "\t\t" + getBalance());
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                lock.unlock();
            }
        }

        public void deposit(int amount) throws InterruptedException {
            lock.lock();
            try {
                // NEW CONSTRAINT: the balance cannot exceed 100.
                // If this deposit would push it over 100, we have to wait
                // until the withdraw thread takes some money out.
                if(balance + amount > 100){
                    depositRequest = amount; // Tell the withdraw thread what we need
                    System.out.println(DepositColor + "Cannot deposit " + amount + ". Waiting for withdraw...");
                    // Wait on the newWithdraw condition -- the withdraw thread
                    // will signal this after it takes money out and makes room.
                    newWithdraw.await();
                }

                balance += amount;
                System.out.println(DepositColor + "Deposit " + amount + "\t\t\t\t\t" + getBalance());

                // After depositing, signal the withdraw thread if there's enough
                // money for what it asked for.
                if (balance >= withdrawRequest) {
                    newDeposit.signalAll();
                }
            } finally {
                lock.unlock();
            }
        }
    }
}
