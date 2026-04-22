# Semaphore — ATM Example

Picture a small bank branch with four ATMs. Six customers walk in at the same time. Four of them step up to the machines right away. The other two stand in line and wait until one of the first four finishes and walks off. That's the whole idea a semaphore captures.

A **Semaphore** is a counter of permits. Two operations:

- `acquire()` — take a permit. If there are none left, you block and wait.
- `release()` — hand a permit back. Somebody who was waiting can now go.

The difference from `synchronized`:

- `synchronized` lets exactly one thread into the room at a time.
- `Semaphore(4)` lets up to four threads into the room at a time.

Use a semaphore when you have N copies of some resource and you want them all being used in parallel, but no more than N.

## What the example does

- Six threads named A through F start at (roughly) the same time.
- The semaphore starts with four permits.
- The first four threads grab a permit and begin "doing ATM stuff" (five fake operations with one-second sleeps).
- Threads E and F sit on `acquire()` and don't start working until one of the first four calls `release()`.

Watch the output. `availablePermits` counts down from 4 to 0 as threads come in and climbs back up as they leave. The last two threads print "got the permit!" only after one of the first four has finished.

## One detail worth calling out

`release()` lives inside a `finally` block. That's on purpose. If the worker threw an exception halfway through and we had put `release()` in the normal flow, the permit would be lost forever. An ATM that never becomes free again. `finally` makes sure the release always happens, even on a crash.
