/*
 * This example is an extension to the example AccountWithSync4,
 * where the names of the threads doing the work are printed.
 * Here we can notice that it is not possible to predict the order
 * that the threads take the turns for execution, but the operations
 * always are done in a consistent way.
 */
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class AccountWithSync5 {

    private static Account account = new Account();

    public static void main(String[] args) {
        Thread threads[] = new Thread[100];

        for (int i = 0; i < 100; i++) {
            threads[i] = new Thread(new AddAPennyTask());
            // Give each thread a name like "1", "2", ... "100"
            // so we can see which thread does each deposit in the output.
            threads[i].setName(""+(i+1));
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
        Lock lock = new ReentrantLock();
        private int balance = 0;

        public int getBalance() {
            return balance;
        }

        // Same as Sync4 but we also print which thread did the deposit.
        // The print is INSIDE the locked region, so the printed values will be
        // consistent: if thread 47 updates balance from 30 to 31, it prints 31.
        // The ORDER of threads is still unpredictable (thread 47 might go before
        // thread 3), but each individual operation is correct.
        public void deposit(int amount) throws InterruptedException {
            lock.lock();
            int newBalance = balance + amount;
            balance = newBalance;
            System.out.println("Updated by thread no. "+Thread.currentThread().getName()+ " and the balance now is "+balance);
            lock.unlock();
        }
    }
}
