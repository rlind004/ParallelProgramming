/*
 * In this fourth version of the program, two threads manipulate different countdown objects
 * so no race conditions occur (as there is no common structure accessed by both threads).
 * Each thread will continue counting on its own (however the precise order in the output is
 * not predictable. You may try executing this several times and witness how the output may vary.
 * */
public class Main {

    public static void main(String[] args) {
        // KEY DIFFERENCE from V1/V2/V3: we create TWO separate Countdown objects.
        // Each thread gets its own. They don't share any data.
        Countdown countdown = new Countdown(30);
        Countdown countdown2 = new Countdown(30);

        // t1 works with 'countdown', t2 works with 'countdown2'.
        // Since they never touch the same object, there's no possibility of a race condition.
        // No synchronization is needed at all.
        CountdownThread t1 = new CountdownThread(countdown);
        t1.setName("Thread 1");
        CountdownThread t2 = new CountdownThread(countdown2);
        t2.setName("Thread 2");

        t1.start();
        t2.start();

        // The output will show two independent countdowns (both from 30 to 1),
        // interleaved in some unpredictable order. But each countdown is complete
        // and correct because there's no sharing.
    }
}

class Countdown {
    private int i;

    public Countdown(int value) {
        i = value;
    }

    public void doCountdown() {
        String color;

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

        while (i > 0) {
            // The synchronized block is still here from V2, but it's locking on 'this'.
            // Since each thread has its own Countdown object, they're locking on
            // DIFFERENT objects, so they never actually block each other.
            // The synchronization has no practical effect here -- it's harmless but unnecessary.
            synchronized (this) {
                System.out.println(color + Thread.currentThread().getName() + ": i =" + i);
                i--;
            }
           try {
                Thread.sleep((long)(50*Math.random()));
           } catch (InterruptedException e) {
                e.printStackTrace();
           }
        }
    }
}

class CountdownThread extends Thread {
    private Countdown threadCountdown;

    public CountdownThread(Countdown countdown) {
        threadCountdown = countdown;
    }

    public void run() {
        threadCountdown.doCountdown();
    }
}
