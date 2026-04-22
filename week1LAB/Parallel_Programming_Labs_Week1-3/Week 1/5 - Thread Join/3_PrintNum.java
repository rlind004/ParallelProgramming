// Same as version 4's PrintNum -- prints thread name and numbers.
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
