# Week 5 — From Java Threads to Pthreads

Weeks 1 to 4 taught threading in Java. Weeks 6 and 7 will teach the same ideas in C with pthreads. This week is the bridge between them.

The goal is not to learn anything new. It is to line up what you already know about Java threads next to what the C version of the same idea looks like, so that when Week 6 starts you already have a picture of where every piece fits.

Every example in this folder comes from programs you have already seen (or are close cousins of them). Each subfolder contains two files: the Java version and the C version. Compile and run both, watch them produce the same output, and read the comments on either side.

## Quick translation table

Before walking through the examples, here is the small table you will want to have in mind. On the left, the Java tool you already know. On the right, the C tool that does the same thing.

| What you want | Java | C (pthreads) |
|---|---|---|
| Make a thread | `new Thread(r)` | `pthread_create(&t, NULL, fn, arg)` |
| Start it running | `thread.start()` | (already running after `pthread_create`) |
| Wait for it to finish | `thread.join()` | `pthread_join(t, NULL)` |
| Pause for one second | `Thread.sleep(1000)` | `sleep(1)` or `usleep(1000000)` |
| One thread at a time | `synchronized (obj) { ... }` | `pthread_mutex_lock` / `pthread_mutex_unlock` |
| Wait for a condition | `obj.wait()` | `pthread_cond_wait(&cv, &mtx)` |
| Wake one waiter | `obj.notify()` | `pthread_cond_signal(&cv)` |
| Wake every waiter | `obj.notifyAll()` | `pthread_cond_broadcast(&cv)` |
| Pass data to a thread | constructor arguments | cast through `void*` |
| Thread finishes (no return value) | just return from `run()` | `return NULL;` |
| Compile and run | `javac Foo.java`, `java Foo` | `gcc foo.c -o foo -lpthread`, `./foo` |

## A few things that bite Java programmers

These are the small differences that catch almost everyone on their first pthreads program. Learn them now and save yourself an hour of debugging later.

### 1. `synchronized` releases itself, mutexes do not

In Java, `synchronized (obj) { ... }` releases the lock on its own when the block exits, even if an exception is thrown. In C you have to call `pthread_mutex_unlock` by hand. If you forget, every other thread piles up at the door forever and your program quietly freezes. No warning, no error, just silence.

### 2. You pass data through `void*`, not constructor arguments

Java lets you hand data to a thread through the Runnable's constructor:

```java
class Task implements Runnable {
    int id;
    Task(int id) { this.id = id; }
    public void run() { System.out.println(id); }
}
```

In C you cram that data into a `void*` and cast it back on the other side. For a single small integer you can cast it straight through:

```c
pthread_create(&t, NULL, fn, (void*)(long) id);

void* fn(void* arg) {
    long id = (long) arg;
    ...
}
```

For anything more than one value, pack it into a struct and pass `&the_struct`.

### 3. No garbage collector

Every `malloc` needs a matching `free`. Forgetting does not crash anything, it just leaks memory. In a short assignment program the leak is usually harmless; in a long-running server it adds up fast.

### 4. `while` around `wait`, not `if`

In both Java and C, a thread waiting on a condition variable can wake up even when the condition is not true yet (spurious wakeups, or the condition may have been changed again by another thread between the signal and the wake). Always re-check after waking up:

```java
while (balance < amount) {
    lock.wait();
}
```

```c
while (balance < amount) {
    pthread_cond_wait(&cv, &mtx);
}
```

Use `while`, not `if`. Always.

## The five examples

Each subfolder has a Java file and a C file that do the same thing. Run them both and compare the output. Then read the comments on each side.

### 1 — Hello Threads

One thread prints a greeting, the main thread waits for it, and that's the whole program. The smallest useful threaded program in each language.

- `1 - Hello Threads/HelloThread.java`
- `1 - Hello Threads/hello_thread.c`

```
cd "1 - Hello Threads"
javac HelloThread.java && java HelloThread
gcc hello_thread.c -o hello_thread -lpthread && ./hello_thread
```

### 2 — Join and Sleep

Two workers running at the same time. Each one prints three lines with a one-second pause between them, and main waits for both with `join`. Watch how their outputs interleave.

- `2 - Join and Sleep/JoinExample.java`
- `2 - Join and Sleep/join_example.c`

### 3 — Race Condition

A hundred threads all adding 1 to the same balance. Expected result: 100. Actual result: usually less. The bug is identical in both languages, because the two-step `read then write` is not atomic. Run either version a few times and watch the number change.

- `3 - Race Condition/AccountWithoutSync.java`
- `3 - Race Condition/account_without_sync.c`

### 4 — Mutual Exclusion

The same 100-thread program, but now guarded. Java uses `synchronized`, C uses `pthread_mutex`. Run either version and you always get exactly 100.

- `4 - Mutual Exclusion/AccountWithSync.java`
- `4 - Mutual Exclusion/account_with_sync.c`

### 5 — Thread Cooperation

A deposit thread and a withdraw thread sharing one account. When the balance is too small, the withdraw thread has to wait. Java does this with `wait()` / `notifyAll()` on the lock object. C does it with `pthread_cond_wait` / `pthread_cond_broadcast` on a condition variable. The structure is the same on both sides; only the names and the unlock discipline differ.

- `5 - Thread Cooperation/ThreadCooperation.java`
- `5 - Thread Cooperation/thread_cooperation.c`

Both versions loop forever. Let them run for a few seconds, watch the two clerks trade places, then stop them with Ctrl+C.

## What is next

Once you can read each pair of files and see how the two versions line up, you are ready for Week 6. The bank-balance pattern you will see in Weeks 6 and 7 is just the idea in this folder, carried further.

## Tip on exporting this as a PDF

If you want to print this document or hand it out as a PDF, you have a few options:

- Open it in VS Code, install the "Markdown PDF" extension, and use `Markdown PDF: Export (pdf)`.
- Use `pandoc README.md -o week5.pdf` from the command line.
- Any modern browser can save a rendered markdown file as PDF through its print dialog.

The source of truth is this markdown file, so if you spot something to fix, edit here and regenerate the PDF.
