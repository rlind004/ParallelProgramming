// In this version, PrintChar can optionally wait for another thread mid-execution.
// The waitFor() method sets up the dependency, and the run() method checks it.
public class PrintChar implements Runnable {

    private char charToPrint;
    private int times;
    private Thread prev; // Reference to a thread we might need to wait for

    public PrintChar(char c, int t) {
        charToPrint = c;
        times = t;
        prev = null; // By default, we don't wait for anyone
    }

    // Calling this before start() tells this task to pause at iteration 70
    // and wait for thread 't' to finish before continuing.
    public void waitFor(Thread t){
        prev = t;
    }

    @Override
    public void run() {
        System.out.print(Thread.currentThread().getName()+" started. ");
        for (int i = 0; i < times; i++) {
            System.out.print(" "+charToPrint);

            // At iteration 70, if we have a thread to wait for, we call join() on it.
            // join() blocks THIS thread until the 'prev' thread finishes.
            // So if printB is told to waitFor(thread1), then at iteration 70:
            //   - thread2 (running printB) pauses here
            //   - it waits until thread1 (running printA) completes its run()
            //   - once thread1 is done, thread2 resumes and prints the remaining 30 chars
            if(i==70){
                try {
                    if(prev != null) {
                        prev.join(); // Block until 'prev' thread finishes
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
