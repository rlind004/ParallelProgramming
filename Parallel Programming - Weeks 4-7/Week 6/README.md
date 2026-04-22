# Week 6 — Pthreads in C

## Why are we redoing the same exercises in C?

Weeks 1 to 4 taught you threading in Java: creating threads, joining them, sleeping, race conditions, synchronization. Now we do the same things in C. It's worth your time, for four reasons.

### 1. You see what Java was hiding

Java gives you tidy abstractions. A `Thread` class. A `synchronized` keyword. A garbage collector. It all just works, which is great, but it also hides what's really happening underneath. C doesn't hide anything:

- No `Thread` class. You call `pthread_create` and manage the handles yourself.
- No constructor arguments. You push everything through a `void*` pointer and hope you cast it correctly on the other side.
- No `synchronized`. You lock and unlock by hand. If you forget the unlock, your program quietly freezes.
- No garbage collector. Every `malloc` needs a matching `free`.

### 2. The cost of mistakes is higher, which is a good teacher

In Java, forgetting `synchronized` gives you a wrong balance. In C, forgetting `pthread_mutex_unlock` can freeze the whole program. Forgetting `free` leaks memory. Casting a `void*` to the wrong type corrupts data silently. You pay attention very quickly.

### 3. Pthreads is the layer underneath

On Linux and macOS, Java's threads are built on top of pthreads. When you write `thread.start()`, deep down the JVM is calling `pthread_create`. Knowing pthreads means you understand what every language's threading library is actually doing behind the scenes.

### 4. Most serious parallel code isn't in Java

High-performance computing, operating systems, game engines, embedded software — almost all of it is C or C++. If you only know the Java model, you're stuck in one lane. Pthreads is the portable lane.

**Short version**: Java teaches you *what* threads do. C teaches you *how* they actually work.

## Exercises

| # | Folder | Concept | Java counterpart |
|---|--------|---------|------------------|
| 0 | Simple Hello | `pthread_create`, `pthread_join`, passing data through `void*` | The simplest Thread + start + join example |
| 1 | N Threads | Dynamic thread count with `malloc`, one rank per thread | N threads created in a loop |
| 2 | Basic Thread Creation | Command-line args, input validation, the `(void*) long` cast trick | Same N-thread idea in a slightly different style |
| 3 | Passing Structs to Threads | Passing a whole struct through `void*` | Week 1: Runnable with fields |
| 4 | PrintChar and PrintNum | Two different thread functions side by side, `sleep()` | Week 1: PrintChar + PrintNum with Thread.sleep |
| 5 | Race Condition | A shared counter with no lock, things go wrong | Week 2: AccountWithoutSynchronization |
| 6 | Mutex Synchronization | `pthread_mutex_lock` / `pthread_mutex_unlock` fix the race | Week 2: AccountWithSync |

## How to compile and run

All of these need to be linked against pthreads:

```bash
gcc -o program_name source_file.c -lpthread
./program_name
```
