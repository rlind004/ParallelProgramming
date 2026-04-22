# CyclicBarrier — Worker Aggregation

A group assignment with five students. Each one writes their own section independently. Nobody is allowed to hand anything in until every single student has finished. Once the last one is done, someone collects all five sections and staples them into a single report. Only then do the students go home.

A **CyclicBarrier** is that waiting point, plus the "stapling" step that runs automatically when everyone has arrived. You build one with two things:

1. A count (5) — how many threads have to arrive before anyone moves.
2. An optional "barrier action" (a `Runnable`) — what to run once they do.

Every thread calls `cyclicBarrier.await()` when it's finished with its part. The call blocks right there. When the fifth thread arrives, the barrier action runs first, and then all five threads continue past `await()` together.

## How it's different from `join()`

- `join()` has the **main thread** waiting for a worker to finish.
- `CyclicBarrier` has the **workers** waiting for each other. Main is nowhere to be seen.

## What the example does

- Five worker threads each generate four random numbers (0–9).
- Each worker drops its four numbers into a shared synchronized list.
- Each worker calls `await()` and parks.
- When all five have parked, the `AggregatorThread` runs: it walks the shared list, adds up all twenty numbers, and prints the total.
- After the aggregator, each worker moves past `await()` and prints a "finished" message.

## Why "cyclic"?

Because the barrier can be reused. Once all five threads have crossed, it resets itself and you can use it again for the next round of work. This program only goes through one round, but the door is open for more. Scripts in other courses often use the same barrier to drive a multi-step simulation, one barrier crossing per step.
