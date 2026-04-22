/*
   In this second version of the example, is illustrated the usage of setPriority method.
   We assign different priorities to threads to see if the scheduler gives
   more CPU time to higher-priority threads.
*/

public class ThreadsExample2 {

    public static void main(String[] args) throws InterruptedException {
        // Same tasks as version 1
        PrintChar printA = new PrintChar('a', 100);
        PrintChar printB = new PrintChar('b', 100);
        PrintNum print100 = new PrintNum(100);

        Thread thread1 = new Thread(printA);
        Thread thread2 = new Thread(printB);
        Thread thread3 = new Thread(print100);

        // setPriority() gives a hint to the thread scheduler about relative importance.
        // MIN_PRIORITY = 1 (lowest), NORM_PRIORITY = 5 (default), MAX_PRIORITY = 10 (highest).
        // The scheduler MAY favor higher-priority threads, but this is NOT guaranteed.
        // Different operating systems handle priorities differently.
        thread1.setPriority(Thread.MIN_PRIORITY);   // thread1 gets lowest priority
        thread2.setPriority(Thread.MAX_PRIORITY);    // thread2 gets highest priority
        thread3.setPriority(Thread.NORM_PRIORITY);   // thread3 gets normal/default priority

        // Start all three threads. Despite the priorities, the output is still
        // largely unpredictable. You might notice thread2 ('b') appearing a bit
        // more often at the start, but don't rely on it.
        thread1.start();
        thread2.start();
        thread3.start();
    }
}
