// Same as version 1 but also prints the thread name at the start.
public class PrintNum implements Runnable {

    private int lastNum;

    public PrintNum(int n) {
        lastNum = n;
    }

    @Override
    public void run() {
        // Prints which thread is executing. If called via run() this will say "main".
        // If called via start() it will say something like "Thread-2".
        System.out.print(Thread.currentThread().getName()+" started. ");
        for (int i = 1; i <= lastNum; i++) {
            System.out.print(" " + i);
        }
    }
}
