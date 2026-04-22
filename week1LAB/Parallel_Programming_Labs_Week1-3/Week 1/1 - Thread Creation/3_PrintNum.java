// This task prints numbers from 1 up to lastNum.
// Like PrintChar, it implements Runnable so it can be executed by a Thread.
public class PrintNum implements Runnable {

    private int lastNum; // The last number to print (e.g., 100)

    // Constructor: store the upper bound
    public PrintNum(int n) {
        lastNum = n;
    }

    @Override
    // When the thread runs this task, it prints 1, 2, 3, ..., lastNum.
    // Each number is preceded by a space for readability.
    public void run() {
        for (int i = 1; i <= lastNum; i++) {
            System.out.print(" " + i);
        }
    }
}
