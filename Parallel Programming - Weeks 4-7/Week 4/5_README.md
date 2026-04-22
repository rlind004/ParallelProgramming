# Producer/Consumer on a Thread Pool

Script 4 showed the producer/consumer running on plain threads that we started ourselves with `new Thread(task).start()`. This script changes one thing: instead of spinning up the threads by hand, we hand the two `Runnable`s to a pool and let the pool decide which of its workers runs them.

## Why use a pool?

In a real program you rarely have exactly one producer and one consumer running forever. You have dozens, hundreds, maybe thousands of tasks showing up over time. Creating a fresh `Thread` for each one and then throwing it away costs real memory and real time.

Think of it as a taxi dispatcher. Without a pool, every rider hires a brand-new driver who disappears after a single trip. With a pool, the company keeps N drivers on duty and whoever is free takes the next call. Same riders, same destinations, a lot less overhead.

## What `ExecutorService` gives you

- `Executors.newFixedThreadPool(2)` builds a pool with two workers.
- `executor.execute(task)` hands a task to the pool. A free worker picks it up.
- `executor.shutdown()` means "I'm done submitting new tasks". Tasks that are already running keep going until they finish.

In this file we submit two tasks and the pool happens to have two workers, so each task ends up on its own thread. In a busier program the same pool would cycle the same workers through many more tasks over time, which is where the savings come in.

## What's unchanged from script 4

Everything to do with the buffer. It's still an `ArrayBlockingQueue<Integer>` with capacity 3, still using `put()` and `take()`, still blocking automatically when full or empty. The pool is only changing **how** the two tasks are run; it isn't touching the coordination between them.

## Running it

Both tasks sit inside infinite `while (true)` loops, so the program doesn't stop on its own. Let it run for a while, watch the producer and consumer trade places, then press Ctrl+C when you've seen enough.
