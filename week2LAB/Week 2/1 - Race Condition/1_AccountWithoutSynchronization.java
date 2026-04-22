/**
 * In this example is illustrated the unpleasant situation of "race condition".
 * A large number of threads are created and they modify a simple instance of
 * Account. As we are not taking care about synchronization, the final result
 * may vary from one execution to another.
 */
public class AccountWithoutSynchronization {

    // This is the shared Account object. All 100 threads will access this same object.
    private static Account account = new Account();

    public static void main(String[] args) {
        Thread threads[] = new Thread[100]; // Array to hold 100 thread references

        // Create 100 threads, each one will deposit 1 into the account
        for (int i = 0; i < 100; i++) {
            threads[i] = new Thread(new AddAPennyTask());
        }

        // Start all 100 threads. They all begin running concurrently.
        for (int i = 0; i < 100; i++) {
            threads[i].start();
        }

        // Wait for ALL 100 threads to finish before checking the balance.
        // Without these join() calls, we might print the balance before
        // all threads have deposited their penny.
        for(int i=0; i < 100; i++){
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // We expect 100 (since 100 threads each deposited 1), but because of
        // the race condition in deposit(), the actual result is often less than 100.
        // Run this several times and you'll see different values.
        System.out.println("What is balance? " + account.getBalance());
    }

    // Each thread runs this task, which just deposits 1 into the shared account.
    private static class AddAPennyTask implements Runnable{
        public void run(){
            account.deposit(1);
        }
    }

    private static class Account {
        private int balance = 0;

        public int getBalance() {
            return balance;
        }

        // THIS IS WHERE THE RACE CONDITION HAPPENS.
        // Two threads might both read balance as (say) 5 at the same time.
        // Both compute newBalance = 5 + 1 = 6.
        // Both write 6 back to balance.
        // Result: balance is 6 instead of 7. One deposit was lost.
        //
        // The problem is that "read balance, add amount, write back" is NOT atomic.
        // Between reading and writing, another thread can sneak in and do the same thing.
        public void deposit(int amount) {
            int newBalance = balance + amount; // Step 1: read balance and compute new value
            // If another thread runs between step 1 and step 2, the update gets lost.
            //Thread.sleep(1); // Uncomment this to make the problem much more obvious
            balance = newBalance; // Step 2: write the new value back
        }
    }
}
