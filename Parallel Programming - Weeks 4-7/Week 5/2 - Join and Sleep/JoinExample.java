/*
 * Two threads running at the same time. Each one prints three
 * lines with a one-second pause between them, and the main
 * thread waits for both with join.
 *
 * Run it and watch the two threads interleave their output. The
 * exact order depends on the scheduler. Compare with
 * join_example.c in the same folder.
 */
public class JoinExample {

    public static void main(String[] args) throws InterruptedException {

        // First worker. Lambda over a Runnable.
        Thread a = new Thread(() -> {
            for (int i = 1; i <= 3; i++) {
                System.out.println("Thread A step " + i);
                try { Thread.sleep(1000); } catch (InterruptedException e) {}
            }
        });

        // Second worker. Same shape, different name.
        Thread b = new Thread(() -> {
            for (int i = 1; i <= 3; i++) {
                System.out.println("Thread B step " + i);
                try { Thread.sleep(1000); } catch (InterruptedException e) {}
            }
        });

        a.start();
        b.start();

        // Wait for both before we finish.
        a.join();
        b.join();

        System.out.println("Both threads are done.");
    }
}
