// Same as version 5 -- no changes to PrintNum in this version.
public class PrintNum implements Runnable {

    private int lastNum;

    public PrintNum(int n) {
        lastNum = n;
    }

    @Override
    public void run() {
        System.out.print(Thread.currentThread().getName()+" started. ");
        for (int i = 1; i <= lastNum; i++) {
            System.out.print(" " + i);
        }
    }
}
