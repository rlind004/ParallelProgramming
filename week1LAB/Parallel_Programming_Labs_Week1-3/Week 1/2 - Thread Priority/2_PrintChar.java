// Same PrintChar class as version 1.
// See version 1 for detailed comments on how Runnable works.
public class PrintChar implements Runnable {

    private char charToPrint;
    private int times;
    private Thread prev;

    public PrintChar(char c, int t) {
        charToPrint = c;
        times = t;
        prev = null;
    }

    public void waitFor(Thread t){
        prev = t;
    }

    @Override
    public void run() {
        for (int i = 0; i < times; i++) {
            System.out.print(" "+charToPrint);
        }
    }
}
