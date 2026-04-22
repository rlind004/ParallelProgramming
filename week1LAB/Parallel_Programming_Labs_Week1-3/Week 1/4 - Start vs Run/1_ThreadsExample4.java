/* In this (4th) version is demonstrated the difference between calling the start
*  and run methods. Calling the run method does not create a new thread, only
*  calling the start method does create a new thread. */
public class ThreadsExample4 {

    public static void main(String[] args) throws InterruptedException {
        PrintChar printA = new PrintChar('a', 100);
        PrintChar printB = new PrintChar('b', 100);
        PrintNum print100 = new PrintNum(100);

        Thread thread1 = new Thread(printA);
        Thread thread2 = new Thread(printB);
        Thread thread3 = new Thread(print100);

        // IMPORTANT: here we call run() instead of start().
        // run() does NOT create a new thread. It just calls the run() method
        // like any normal method call, on the CURRENT thread (which is "main").
        // So thread1.run() executes, finishes completely, then thread2.run() starts, etc.
        // The output is always the same: all a's, then all b's, then all numbers.
        // There is zero parallelism happening here.
        //
        // If you change these to start(), new threads get created and the output
        // becomes non-deterministic again (like version 1).
        thread1.run();
        thread2.run();
        thread3.run();
    }
}
