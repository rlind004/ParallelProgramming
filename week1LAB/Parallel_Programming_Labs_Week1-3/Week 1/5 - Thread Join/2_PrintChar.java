// Same as version 4's PrintChar -- prints the thread name and the character.
public class PrintChar implements Runnable {

    private char charToPrint;
    private int times;

    public PrintChar(char c, int t) {
        charToPrint = c;
        times = t;
    }

    @Override
    public void run() {
        System.out.print(Thread.currentThread().getName()+" started. ");
        for (int i = 0; i < times; i++) {
            System.out.print(" "+charToPrint);
        }
    }
}
