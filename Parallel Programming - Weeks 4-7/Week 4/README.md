# Week 4 — Higher-Level Concurrency Tools

Weeks 1 to 3 gave you the raw materials: create a thread, synchronize access with `synchronized`, let threads cooperate with `wait()` and `notify()`. They all work, but they're low-level and easy to trip over. A missing `notify()` can freeze a program. A `wait()` without a loop around it can give you the wrong answer. And managing lots of threads by hand gets old fast.

Week 4 is about the toolbox that sits on top of that foundation: the classes in **java.util.concurrent**. They handle the tricky bits for you, so you can focus on what the program is actually trying to do.

## Exercises

| # | File | Concept | What it replaces |
|---|------|---------|-----------------|
| 1 | SemaphoreATMExample | Semaphore — up to N threads at a time (4 ATMs, 6 customers) | `synchronized` is one-at-a-time, Semaphore is N-at-a-time |
| 2 | CyclicBarrierMinimal | CyclicBarrier on its own, three threads meeting up | Shows the rendezvous idea alone, before any aggregation |
| 3 | CyclicBarrierWorkerAggregation | CyclicBarrier with a barrier action that sums partial results | Manual `join()` plus hand-rolled aggregation |
| 4 | ProducerConsumerBlockingQueueNoExecutor | Same producer/consumer, but with ArrayBlockingQueue instead of wait/notify | The Week 3 wait/notify version |
| 5 | ProducerConsumerBlockingQueue | Same program once more, now running on an ExecutorService pool | Raw `new Thread().start()` from script 4 |

## The course so far

- **Week 1**: threads, join, sleep. The absolute basics.
- **Week 2**: what goes wrong when threads share data, and how `synchronized` fixes it.
- **Week 3**: `wait()` and `notify()` for letting threads cooperate.
- **Week 4**: the `java.util.concurrent` tools that make the same things easier, safer, and shorter.

The teaching pattern is "do it by hand first, then do it properly". You really need the hand-made versions from weeks 1–3 to appreciate what the week-4 tools are saving you from.
