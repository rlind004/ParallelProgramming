# Thread Join

The main thread has to wait for all three threads to finish and then print "MAIN-DONE." Which is what join() does — it blocks the calling thread until the target thread terminates.

thread1. join(); // wait for thread 1

thread2. join(); // wait for thread 2

thread3. join(); // wait for thread 3

System.out.print("MAIN-DONE");

If there are no join() calls, start() would return straight away, and the main thread would print “MAIN-DONE” somewhere in the middle of the output while other threads are still running.

Even so, the three threads still run concurrently with all the other threads. Only the calls to join() block the main thread.

Take out the joins, and instead of "MAIN-DONE" being an indelible bookend in the output, it is now sandwiched between other lines.
