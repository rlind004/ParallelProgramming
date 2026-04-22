# Countdown — Synchronized

Again, two threads, the same shared Countdown object, but this time the print-and-decrement is inside a synchronized(this) block. And if only one thread can be inside at a time, the countdown is clean — no skips and no duplicates.

Each thread sleeps for a short random amount of time after releasing the lock. This allows the other thread a chance to acquire the lock next so it is fairer in terms of turns.
