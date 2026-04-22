// This version of PrintNum introduces Thread.sleep().
// When the counter reaches 80, the thread pauses for 5 seconds.
public class PrintNum implements Runnable {

    private int lastNum;

    public PrintNum(int n) {
        lastNum = n;
    }

    @Override
    public void run() {
        for (int i = 1; i <= lastNum; i++) {
            System.out.print(" " + i);

            // When we reach number 80, we pause this thread for 5 seconds.
            // Thread.sleep(5000) puts the CURRENT thread to sleep for 5000 milliseconds.
            // Only this thread sleeps -- the other two threads printing 'a' and 'b'
            // continue running without any interruption.
            if(i==80){
                try {
                    Thread.sleep(5000);  // sleep 5 seconds
                } catch (InterruptedException e) {
                    // InterruptedException is a checked exception that sleep() can throw.
                    // It happens if another thread calls interrupt() on this thread
                    // while it's sleeping. For now we just print the error and move on.
                    e.printStackTrace();
                }
            }
        }
    }
}
