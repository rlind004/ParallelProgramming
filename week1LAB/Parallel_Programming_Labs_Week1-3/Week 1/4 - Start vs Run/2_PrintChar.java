// Same task as before, but now we also print which thread is running this code.
public class PrintChar implements Runnable {

    private char charToPrint;
    private int times;

    public PrintChar(char c, int t) {
        charToPrint = c;
        times = t;
    }

    @Override
    public void run() {
        // Thread.currentThread() returns a reference to the thread that is
        // currently executing this code. getName() returns that thread's name.
        // When we use run() instead of start(), this will print "main"
        // because the task is running on the main thread, not a new one.
        // When we use start(), it will print something like "Thread-0".
        System.out.print(Thread.currentThread().getName()+" started. ");
        for (int i = 0; i < times; i++) {
            System.out.print(" "+charToPrint);
        }
    }
}
