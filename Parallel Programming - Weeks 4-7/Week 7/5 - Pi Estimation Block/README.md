# Pi Estimation — Block Distribution

This is our first real parallel computation. Instead of printing or bank accounts, we're computing pi using the Leibniz series:

```
pi = 4 * [1 - 1/3 + 1/5 - 1/7 + 1/9 - ...]
```

Each thread computes a contiguous **block** of terms:

```
4 threads, 1000 terms:
  Thread 0: terms   0–249
  Thread 1: terms 250–499
  Thread 2: terms 500–749
  Thread 3: terms 750–999
```

The important design choice: each thread computes into a **local** variable (`my_sum`), then locks the mutex only once to add it to the global `sum`. This is much better than locking inside the loop (which would lock/unlock thousands of times).

```c
// Good: lock once at the end
for (i = ...) { my_sum += ...; }    // no lock
pthread_mutex_lock(&mutex);
sum += my_sum;                       // one lock
pthread_mutex_unlock(&mutex);

// Bad: lock every iteration (slow!)
for (i = ...) {
    pthread_mutex_lock(&mutex);
    sum += ...;                      // thousands of locks
    pthread_mutex_unlock(&mutex);
}
```

**Limitation:** `n` must be evenly divisible by the number of threads, or some terms are skipped. Fixed in the next version.

The program also runs a single-threaded version and compares timing, so you can see the actual speedup.

**Compile and run:**
```bash
gcc -g -Wall -o pi pi_estimation_block.c -lpthread -lm
./pi 4 1000000
```
