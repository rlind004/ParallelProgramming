/*
 * The smallest useful Java threaded program. One background
 * thread says hello, main prints its own line, and then main
 * waits for the background thread to finish before exiting.
 *
 * Compare with hello_thread.c in the same folder. Run them both
 * and you should see the same output, up to ordering.
 */
public class HelloThread {

    public static void main(String[] args) throws InterruptedException {

        // A thread built from a lambda. The lambda is the "run"
        // method of an anonymous Runnable.
        Thread t = new Thread(() -> {
            System.out.println("Hello from the child thread!");
        });

        // Java needs you to explicitly start the thread. Calling
        // run() directly would just execute the lambda on the
        // main thread, which is a classic beginner mistake.
        t.start();

        // Runs in main, at the same time as the child thread. The
        // order in which the two lines print is up to the OS.
        System.out.println("Hello from the main thread!");

        // Wait for the child to finish before we exit.
        t.join();

        System.out.println("Both threads are done. Goodbye!");
    }
}
