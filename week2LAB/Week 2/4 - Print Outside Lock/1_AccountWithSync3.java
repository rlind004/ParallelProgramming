/*
 * This example is an extension to the AccountWithSynchronization2 example.
 * Here synchronization related to the changes in the balance is still achieved
 * using a synchronized block in the body of the of the deposit method.
 * On the other hand after each change in the account balance, the "current"
 * balance is printed in an non-synchronized way (outside the synchronized block).
 * You may try putting the printing instruction inside the synchronized block and
 * notice the difference.
 */
public class AccountWithSync3 {

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

        // The final balance is still always 100 (the update itself is synchronized).
        // But the intermediate print outputs during execution may look weird.
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

        public void deposit(int amount) throws InterruptedException {
            // The balance update is protected by the synchronized block.
            // No race condition here -- the final result is always correct.
            synchronized (this) {
                int newBalance = balance + amount;
                balance = newBalance;
            }
            // BUT this print statement is OUTSIDE the synchronized block.
            // That means: thread A might update balance from 50 to 51, release the lock,
            // then thread B immediately updates balance from 51 to 52 and releases the lock,
            // and THEN thread A prints "The balance now is: 52" (not 51!).
            // The printed values can appear out of order, show duplicates, or skip numbers.
            // The actual data is fine -- it's just the print that's misleading.
            // Try moving this line inside the synchronized block and compare.
            System.out.println("The balance now is: "+balance);
        }
    }
}
