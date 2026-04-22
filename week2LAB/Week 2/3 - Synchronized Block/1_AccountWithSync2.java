/*
 * This example is an extension to the previous week example AccountWithoutSynch,
 * but ensuring synchronization, so the final balance will always be 100.
 * Here synchronization is achieved using a synchronized block in the body of the
 * of the deposit method.
 */

public class AccountWithSync2 {

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
        private int balance = 0;

        public int getBalance() {
            return balance;
        }

        // Instead of making the whole method synchronized (like in Sync1),
        // here we use a synchronized BLOCK inside the method.
        // synchronized(this) means: acquire the lock on this Account object
        // before executing the code inside the curly braces.
        //
        // The advantage of a synchronized block is that you can choose exactly
        // which part of the method needs protection. Only the code inside the block
        // is locked -- anything before or after it runs without holding the lock.
        // In Sync1, the entire method body was protected; here, we have more control.
        public void deposit(int amount) throws InterruptedException {
            synchronized (this) {
                int newBalance = balance + amount;
                balance = newBalance;
            }
            // This print statement is OUTSIDE the synchronized block.
            // It's commented out here, but Sync3 shows what happens when you
            // print outside -- the printed values can appear out of order.
            //System.out.println("The balance now is: "+balance);
        }
    }
}
