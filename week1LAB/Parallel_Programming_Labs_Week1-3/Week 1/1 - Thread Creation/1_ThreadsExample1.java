/*
   An example which firstly illustrates the creation and the start of the threads.
   Secondly this example illustrates the non-determinism of a multi-threaded program
   (so this program generally provides different outputs for different executions).
*/

public class ThreadsExample1 {

    public static void main(String[] args) throws InterruptedException {

        // Here we create 3 "task" objects. Each task defines what a thread will do.
        // printA will print 'a' 100 times, printB will print 'b' 100 times,
        // and print100 will print numbers from 1 to 100.
        PrintChar printA = new PrintChar('a', 100);
        PrintChar printB = new PrintChar('b', 100);
        PrintNum print100 = new PrintNum(100);

        // A task alone doesn't run on its own. We need to wrap it inside a Thread.
        // The Thread constructor takes a Runnable (our task) as an argument.
        Thread thread1 = new Thread(printA);
        Thread thread2 = new Thread(printB);
        Thread thread3 = new Thread(print100);

        // start() tells the JVM to create a new thread of execution and call
        // the run() method of the task on that new thread.
        // After calling start(), the main thread continues immediately --
        // it does NOT wait for thread1 to finish before starting thread2.
        // All three threads will be running at the same time (concurrently).
        thread1.start();
        thread2.start();
        thread3.start();

        // At this point, 4 threads exist: the main thread (which is about to end)
        // and the 3 threads we just started. Their output will be interleaved
        // in an unpredictable order because the OS schedules them as it sees fit.
    }
}
