// Same PrintChar as version 1, but without the waitFor/prev fields
// (those aren't needed until version 6).
public class PrintChar implements Runnable {

    private char charToPrint;
    private int times;

    public PrintChar(char c, int t) {
        charToPrint = c;
        times = t;
    }

    @Override
    public void run() {
        for (int i = 0; i < times; i++) {
            System.out.print(" "+charToPrint);
        }
    }
}
