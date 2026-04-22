/* In this (5th) version is demonstrated the usage of join method.
* Here the main method uses the join method to wait the threads 1,2,3
* to complete their execution and then print MAIN-DONE on the screen */
public class ThreadsExample5 {

    public static void main(String[] args) throws InterruptedException {
        PrintChar printA = new PrintChar('a', 100);
        PrintChar printB = new PrintChar('b', 100);
        PrintNum print100 = new PrintNum(100);

        Thread thread1 = new Thread(printA);
        Thread thread2 = new Thread(printB);
        Thread thread3 = new Thread(print100);

        // start() launches the threads. They begin executing concurrently.
        // start() returns immediately -- it does NOT wait for the thread to finish.
        thread1.start();
        thread2.start();
        thread3.start();

        // join() makes the CALLING thread (here: main) wait until the target thread
        // finishes its run() method completely.
        // So thread1.join() means: "main, stop here and wait until thread1 is done."
        // After thread1 finishes, main moves to thread2.join(), waits for thread2, etc.
        // Note: the three worker threads still run concurrently with each other.
        // join() only blocks the thread that CALLS it (main), not the others.
        thread1.join();
        thread2.join();
        thread3.join();

        // By the time we get here, all three threads have finished their work.
        // Without the join() calls above, "MAIN-DONE" could appear in the middle
        // of the output because main would not wait for the other threads.
        System.out.print("MAIN-DONE");
    }
}
