/*
* In this first version of the program, two threads manipulate together (simultaneously)
* a Countdown object in an unsynchronized way. As race condition occurs the output is
* inconsistent (and unpredictable). You may try executing this several times and witness
* how the output may vary.
* */
public class Main {

    public static void main(String[] args) {
        // Create ONE Countdown object shared by both threads.
        // Both threads will call doCountdown() on this same object.
        Countdown countdown = new Countdown(30);

        // Create two threads. Both receive the SAME countdown object.
        CountdownThread t1 = new CountdownThread(countdown);
        t1.setName("Thread 1");
        CountdownThread t2 = new CountdownThread(countdown);
        t2.setName("Thread 2");

        // Start both threads. They run concurrently and both modify 'i' in countdown.
        // Because there's no synchronization, the value of 'i' can be read by one thread,
        // changed by the other, and the first thread won't know about it.
        t1.start();
        t2.start();
    }
}

class Countdown {
    private int i; // Shared counter -- both threads read and write this

    public Countdown(int value) {
        i = value;
    }

    // Both threads call this method on the same Countdown object.
    // The problem: while thread 1 is inside the while loop (between reading i
    // and decrementing i), thread 2 can also enter and change i.
    // This leads to skipped numbers, repeated numbers, or other inconsistencies.
    public void doCountdown() {
        String color;

        // Assign a color based on thread name so we can visually tell them apart.
        switch (Thread.currentThread().getName()) {
            case "Thread 1":
                color = ThreadColors.ANSI_BLUE;
                break;
            case "Thread 2":
                color = ThreadColors.ANSI_PURPLE;
                break;
            default:
                color = ThreadColors.ANSI_GREEN;
        }

        // This loop is the CRITICAL SECTION -- it reads and modifies 'i'.
        // Without synchronization, both threads can be inside this loop at the same time.
        // For example: both read i=10, both print "i=10", both decrement to 9.
        // Now 10 was printed twice and we effectively skipped a decrement.
        while (i > 0) {
            System.out.println(color + Thread.currentThread().getName() + ": i =" + i);
            i--;
        }
    }
}

// A simple thread class that holds a reference to a Countdown object
// and calls doCountdown() when started.
class CountdownThread extends Thread {
    private Countdown threadCountdown;

    public CountdownThread(Countdown countdown) {
        threadCountdown = countdown;
    }

    public void run() {
        threadCountdown.doCountdown();
    }
}
