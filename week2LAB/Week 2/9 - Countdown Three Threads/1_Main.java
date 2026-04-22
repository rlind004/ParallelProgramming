/*
 * In this third version of the program, three threads manipulate together (simultaneously)
 * a Countdown object in a synchronized way. No race conditions occur and the output is always
 * consistent (counting down step by step), but which thread is making the respective changes
 * is not predicable. You may try executing this several times and witness how the output may vary.
 * */
public class Main {

    public static void main(String[] args) {
        Countdown countdown = new Countdown(30);

        // Same as V2 but now we have THREE threads sharing the same countdown.
        // The synchronized block still works -- it doesn't matter how many threads
        // are competing for the lock, only one can hold it at a time.
        CountdownThread t1 = new CountdownThread(countdown);
        t1.setName("Thread 1");
        CountdownThread t2 = new CountdownThread(countdown);
        t2.setName("Thread 2");
        CountdownThread t3 = new CountdownThread(countdown);
        t3.setName("Thread 3");

        t1.start();
        t2.start();
        t3.start();
    }
}

class Countdown {
    private int i;

    public Countdown(int value) {
        i = value;
    }

    public void doCountdown() {
        String color;

        // Thread 3 doesn't match "Thread 1" or "Thread 2", so it gets the
        // default green color. You could add a "Thread 3" case if you want
        // a specific color for it.
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
            // Same synchronized block as V2. Works for 3 threads just as well.
            // While one thread is inside, the other two wait.
            synchronized (this) {
                System.out.println(color + Thread.currentThread().getName() + ": i =" + i);
                i--;
            }

            try {
                Thread.sleep((long) (50 * Math.random()));
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
