/* The last discussed version of this example. Here we are again illustrating
the usage of the join method. In this case we are informing task printB to join
thread1 by calling a method that we added (waitFor).
*/
public class ThreadsExample6 {

    public static void main(String[] args) throws InterruptedException {
        PrintChar printA = new PrintChar('a', 100);
        PrintChar printB = new PrintChar('b', 100);
        PrintNum print100 = new PrintNum(100);

        Thread thread1 = new Thread(printA);
        Thread thread2 = new Thread(printB);
        Thread thread3 = new Thread(print100);

        // This tells task printB: "you should wait for thread1 at some point."
        // It stores a reference to thread1 inside printB.
        // The actual waiting happens inside PrintChar's run() method at iteration 70.
        printB.waitFor(thread1);

        // All three threads start running concurrently
        thread1.start();
        thread2.start();
        thread3.start();

        // Main thread waits for all three to finish, just like version 5
        thread1.join();
        thread2.join();
        thread3.join();

        System.out.print("MAIN-DONE");
    }
}
