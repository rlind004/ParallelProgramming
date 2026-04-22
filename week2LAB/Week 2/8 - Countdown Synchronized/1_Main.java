/*
 * In this second version of the program, two threads manipulate together (simultaneously)
 * a Countdown object in a synchronized way. No race conditions occur and the output is always
 * consistent (counting down step by step), but which thread is making the respective changes
 * is not predicable. You may try executing this several times and witness how the output may vary.
 * */
public class Main {

    public static void main(String[] args) {
        // Same as V1: one shared Countdown, two threads
        Countdown countdown = new Countdown(30);

        CountdownThread t1 = new CountdownThread(countdown);
        t1.setName("Thread 1");
        CountdownThread t2 = new CountdownThread(countdown);
        t2.setName("Thread 2");

        t1.start();
        t2.start();
    }
}

class Countdown {
    private int i;

    public Countdown(int value) {
        i = value;
    }

    public void doCountdown() {
        String color;

        // Color assignment is outside the synchronized block because it doesn't
        // access shared data -- each thread gets its own local 'color' variable.
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
            // synchronized(this) locks on the Countdown object.
            // Only one thread can be inside this block at a time.
            // So the print + decrement happen together atomically --
            // no other thread can read or modify 'i' in between.
            synchronized (this) {
                System.out.println(color + Thread.currentThread().getName() + ": i =" + i);
                i--;
            }
            // After releasing the lock, the thread sleeps for a random short time.
            // This gives the other thread a chance to grab the lock next.
            // Without this sleep, one thread might grab the lock again immediately
            // and do several decrements before the other thread gets a turn.
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
