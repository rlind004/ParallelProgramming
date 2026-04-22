/*
   In this third version of the example, is illustrated the usage of sleep method.
   Notice that the sleep method is called within the run method of the thread that
   is going to sleep (which in this case is the thread related to PrintNum task).

   The main class itself is identical to version 1 -- the sleep happens inside
   PrintNum.run(), not here.
*/

public class ThreadsExample3 {

    public static void main(String[] args) throws InterruptedException {
        // Create the same three tasks as before
        PrintChar printA = new PrintChar('a', 100);
        PrintChar printB = new PrintChar('b', 100);
        PrintNum print100 = new PrintNum(100);

        Thread thread1 = new Thread(printA);
        Thread thread2 = new Thread(printB);
        Thread thread3 = new Thread(print100);

        // Start all three. thread3 (PrintNum) will pause at number 80 for 5 seconds.
        // During that 5-second pause, thread1 and thread2 keep printing normally.
        thread1.start();
        thread2.start();
        thread3.start();
    }
}
