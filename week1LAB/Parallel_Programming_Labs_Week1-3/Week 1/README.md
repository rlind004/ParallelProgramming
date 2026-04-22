# Week 1 — Introduction to Threads

This week covers the basics of creating and controlling threads in Java. We use the same example throughout (printing characters and numbers), and each version adds one new concept on top of the previous one.

1. **Thread Creation** — creating threads with Runnable and start(), observing non-deterministic output
2. **Thread Priority** — giving hints to the scheduler with setPriority()
3. **Thread Sleep** — pausing a thread with Thread.sleep()
4. **Start vs Run** — why calling run() directly doesn't create a new thread
5. **Thread Join** — making the main thread wait for others to finish
6. **Join Between Threads** — one worker thread waiting for another mid-execution
