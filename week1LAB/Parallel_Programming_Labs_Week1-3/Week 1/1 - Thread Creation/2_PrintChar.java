// This class represents a task (something a thread can execute).
// It implements Runnable, which means it must provide a run() method.
// The run() method contains the code that the thread will execute.
public class PrintChar implements Runnable {

    private char charToPrint; // The character this task will print
    private int times; // How many times to print it
    private Thread prev; // A reference to another thread (used in later versions)

    // Constructor: sets up what character to print and how many times
    public PrintChar(char c, int t) {
        charToPrint = c;
        times = t;
        prev = null; // No dependency on another thread by default
    }

    // This method is used in version 6 to set up a dependency on another thread.
    // Not used in this version, but included in the class for later.
    public void waitFor(Thread t){
        prev = t;
    }

    @Override
    // This is the method that gets called when the thread starts.
    // It simply loops 'times' number of times, printing the character each iteration.
    // When multiple threads run this at the same time, their prints get mixed together.
    public void run() {
        for (int i = 0; i < times; i++) {
            System.out.print(" "+charToPrint);
        }
    }
}
